package top.woodwhale.blog.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;
import top.woodwhale.blog.dao.ImageDao;
import top.woodwhale.blog.pojo.Image;
import top.woodwhale.blog.pojo.User;
import top.woodwhale.blog.response.ResponseResult;
import top.woodwhale.blog.services.IImageService;
import top.woodwhale.blog.services.IUserService;
import top.woodwhale.blog.utils.ServletUtils;
import top.woodwhale.blog.utils.SnowflakeIdUtils;
import top.woodwhale.blog.utils.TextUtils;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;

import static top.woodwhale.blog.utils.Constants.Image.IMAGE_NORMAL_STATE;
import static top.woodwhale.blog.utils.Constants.Image.IMAGE_REMOVE_STATE;
import static top.woodwhale.blog.utils.Constants.Page.DEFAULT_PAGE;
import static top.woodwhale.blog.utils.Constants.Page.MAX_SIZE;

@Slf4j
@Service
@Transactional
public class ImageServiceImpl implements IImageService {

    /**
     * 注入的userService，用来判断用户权限
     */
    @Autowired
    private IUserService userService;

    /**
     * 注入的id生成器
     */
    @Autowired
    private SnowflakeIdUtils idUtils;

    /**
     * 注入的image Dao层
     */
    @Autowired
    private ImageDao imageDao;

    /**
     * 配置文件中设定的保存路径
     */
    @Value("${blog.image.save-path}")
    private String imagePath;

    /**
     * 配置文件中最大文件大小 byte
     */
    @Value("${blog.image.max-size}")
    private long imageMaxSize;

    /**
     * 格式化时间
     */
    public SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy_MM_dd");

    /**
     * 上传图片
     *
     * @param file 文件
     * @return ResponseResult
     */
    @Override
    public ResponseResult uploadImage(MultipartFile file) {
        // 查看用户权限
        User user = userService.checkUser();
        if (user == null) {
            return ResponseResult.FAILED(TextUtils.smtError("用户图片所有权"));
        }
        // 判断文件类型，仅仅支持图片文件上传——JPG、PNG、GIF
        if (!TextUtils.isImage(file)) {
            return ResponseResult.FAILED(TextUtils.smtError("文件类型或文件名"));
        }
        log.info("file size --> " + file.getSize());
        log.info("max size --> " + imageMaxSize);
        // 限制图片大小
        if (file.getSize() > imageMaxSize) {
            return ResponseResult.FAILED("图片最大仅支持" + (imageMaxSize / 1024 / 1024) + "Mb");
        }
        // 创建图片保存路径 path/time/id.png
        String contentType = file.getContentType();
        String originalFilename = file.getOriginalFilename();
        long currentTimeMillis = System.currentTimeMillis();
        String currentDay;
        currentDay = new SimpleDateFormat("yyyy_MM_dd").format(currentTimeMillis);
        String type = TextUtils.getImageType(Objects.requireNonNull(contentType));
        long id = idUtils.nextId();
        String targetPath = imagePath + File.separator + currentDay + File.separator + id + type;
        File targetFile = new File(targetPath);
        if (!(targetFile.getParentFile().exists() || targetFile.getParentFile().mkdirs() || targetFile.mkdirs())) {
            return ResponseResult.FAILED(TextUtils.smtError("图片文件夹创建"));
        }
        try {
            if (!targetFile.exists() || targetFile.createNewFile()) {
                // 设置文件键值对
                HashMap<String, String> resMap = new HashMap<>();
                String resPath = currentTimeMillis + "_" + id + type;
                resMap.put("id", resPath);
                resMap.put("name", originalFilename);
                // 如果图片md5值相同，那么就有相同的图片存在，拒绝存入MySQL
                String imageMd5 = DigestUtils.md5DigestAsHex(file.getInputStream());
                Image imageInMysql = null;
                Optional<Image> daoOne = imageDao.findOne((Specification<Image>) (root, query, criteriaBuilder) ->
                        criteriaBuilder.and(criteriaBuilder.equal(root.get("md5").as(String.class), imageMd5),
                                criteriaBuilder.equal(root.get("userId").as(String.class), user.getId())));
                // 如果查询到有md5相同的，就不去转化文件，否则转化文件到本地
                if (daoOne.isPresent()) {
                    imageInMysql = daoOne.get();
                } else {
                    file.transferTo(targetFile);
                }
                boolean isUpdate = false;
                boolean isCommonUser = true;
                if (imageInMysql != null) {
                    /*
                        imageInMysql不为空有两种情况：
                        1. 存在同样的图片，状态是存在的，需要判断是否是同一个用户的
                        2. 存在同样的图片，但是状态时删除的
                     */
                    // 如果是正常状态，就返回MySQL中存在的图片id和名称
                    if (imageInMysql.getState().equals(IMAGE_NORMAL_STATE) &&
                            imageInMysql.getUserId().equals(user.getId())) {
                        resMap.put("id", imageInMysql.getUrl());
                        resMap.put("name", imageInMysql.getName());
                        // 更新日期
                        imageInMysql.setUpdateTime(new Date());
                        imageDao.save(imageInMysql);
                        log.info("数据库中存在当前md5图片");
                        return ResponseResult.SUCCESS(TextUtils.successGet("图片")).setData(resMap);
                    }
                    // 否则就是将当前图片的state改为正常
                    if (imageInMysql.getState().equals(IMAGE_REMOVE_STATE) &&
                            imageInMysql.getUserId().equals(user.getId())) {
                        isUpdate = true;
                    }
                    // 如果不是同一个用户上传的，但是图片md5相同，那么标记一下
                    if (imageInMysql.getState().equals(IMAGE_NORMAL_STATE) &&
                            !imageInMysql.getUserId().equals(user.getId())) {
                        isCommonUser = false;
                    }
                }
                log.info("targetFile --> " + targetFile);
                log.info("是否是同一个用户上传图片 --> " + isCommonUser);
                log.info("是否是更新图片 --> " + isUpdate);
                // 将键值对存储到MySQL中，方便之后查询图片
                Image image = new Image();
                // 设置md5
                image.setMd5(imageMd5);
                // 如果不是同一个用户，但是上传了md5值相同的文件，就新建一个,如果是同一个用户，查看当前是否需要更新
                image.setId(!isCommonUser ? String.valueOf(idUtils.nextId()) :
                        isUpdate ? imageInMysql.getId() : String.valueOf(idUtils.nextId()));
                image.setName(originalFilename);
                image.setCreateTime(new Date());
                image.setUpdateTime(new Date());
                image.setPath(targetFile.getPath());
                image.setState(IMAGE_NORMAL_STATE);
                image.setUrl(resPath);
                image.setUserId(user.getId());
                imageDao.save(image);
                return ResponseResult.SUCCESS(TextUtils.successAdd("图片")).setData(resMap);
            }
        } catch (IOException e) {
            log.error("图片转化文件错误", e);
        }
        return ResponseResult.FAILED(TextUtils.smtError("图片文件上传"));
    }

    /**
     * 获取image
     *
     * @param imageUrl imageId
     */
    @Override
    public void getImage(String imageUrl) {
        // 判断imageUrl是否合法
        if (TextUtils.isEmpty(imageUrl) || !imageUrl.contains("_") || !imageUrl.contains(".")) {
            return;
        }
        // 转化url，读取file
        long time;
        String timeFormat;
        String imageName;
        String targetPath;
        try {
            time = Long.parseLong(imageUrl.split("_")[0]);
            timeFormat = new SimpleDateFormat("yyyy_MM_dd").format(time);
            imageName = imageUrl.split("_")[1];
            targetPath = imagePath + File.separator + timeFormat + File.separator + imageName;
        } catch (Exception e) {
            log.error("路径转换失败", e);
            return;
        }
        File file = new File(targetPath);
        if (!file.exists()) {
            return;
        }
        // 输出url
        ServletOutputStream ops = null;
        FileInputStream fos = null;
        HttpServletResponse response = ServletUtils.getResponse();
        try {
            response.setContentType("image/png");
            ops = response.getOutputStream();
            fos = new FileInputStream(file);
            byte[] buf = new byte[1024];
            var len = 0;
            while ((len = fos.read(buf)) != -1) {
                ops.write(buf, 0, len);
            }
            ops.flush();
        } catch (IOException e) {
            log.error("图片流转化失败", e);
        } finally {
            try {
                if (ops != null) {
                    ops.close();
                }
                if (fos != null) {
                    fos.close();
                }
            } catch (Exception e) {
                log.error("关流失败", e);
            }
        }
    }

    /**
     * 获取图片列表
     *
     * @param page 页数
     * @param size 每页容量
     * @return ResponseResult
     */
    @Override
    public ResponseResult listImages(int page, int size) {
        // 处理page和size
        page = Math.max(page, DEFAULT_PAGE);
        size = Math.min(MAX_SIZE, Math.max(size, 1));
        // 获取当前登录的user
        User user = userService.checkUser();
        if (user == null) {
            return ResponseResult.FAILED(TextUtils.smtError("用户图片所有权"));
        }
        final String userId = user.getId();
        // 创建分页条件，根据更新时间降序
        Page<Image> imageLists = imageDao.findAll((Specification<Image>) (root, query, criteriaBuilder) ->
                        criteriaBuilder.and(criteriaBuilder.equal(root.get("userId").as(String.class), userId),
                                criteriaBuilder.equal(root.get("state").as(String.class), IMAGE_NORMAL_STATE))
                , PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "updateTime")));

        return ResponseResult.SUCCESS(TextUtils.successGet("图片列表")).setData(imageLists);
    }

    /**
     * 通过imageId删除图片
     *
     * @param imageId 图片id
     * @return ResponseResult
     */
    @Override
    public ResponseResult deleteImage(String imageId) {
        // 需要判断是否是当前管理员用户的图片
        User user = userService.checkUser();
        if (user == null) {
            return ResponseResult.FAILED(TextUtils.smtError("用户图片所有权"));
        }
        if (imageId.contains("_") && imageId.split("_").length > 0) {
            boolean flag = true;
            // 循环删除多个图片数据
            for (String id : imageId.split("_")) {
                Image imageInMysql = imageDao.findOneById(id);
                if (imageInMysql != null && !TextUtils.isEmpty(imageInMysql.getPath())) {
                    try {
                        FileSystemUtils.deleteRecursively(new File(imageInMysql.getPath()));
                        log.info("删除本地图片 " + imageInMysql.getPath() + " 成功!");
                    } catch (Exception e) {
                        flag = false;
                        log.error("删除本地磁盘图片失败!", e);
                    }
                    int res = imageDao.deleteOneByImageId(id, user.getId());
                    if (res == 0) {
                        flag = false;
                        log.error("图片 " + id + " 数据库删除错误");
                    }
                } else {
                    flag = false;
                    log.error("图片 " + id + " 不存在!");
                }
            }
            if (flag) {
                return ResponseResult.SUCCESS(TextUtils.successDelete("图片批量"));
            }
            return ResponseResult.FAILED(TextUtils.smtError("部分图片删除"));
        }
        // 删除就从本地磁盘删除
        Image imageInMysql = imageDao.findOneById(imageId);
        if (imageInMysql == null) {
            return ResponseResult.FAILED(TextUtils.failDelete("磁盘图片"));
        }
        try {
            FileSystemUtils.deleteRecursively(new File(imageInMysql.getPath()));
            log.info("删除本地图片 " + imageInMysql.getPath() + " 成功!");
        } catch (Exception e) {
            log.error("删除本地磁盘图片失败!", e);
        }
        // 删除数据库中的记录
        int res = imageDao.deleteOneByImageId(imageId, user.getId());
        if (res > 0) {
            return ResponseResult.SUCCESS(TextUtils.successDelete("图片"));
        }
        return ResponseResult.FAILED(TextUtils.failDelete("数据库图片"));
    }
}
