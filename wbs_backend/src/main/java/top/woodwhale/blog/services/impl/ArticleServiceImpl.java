package top.woodwhale.blog.services.impl;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.woodwhale.blog.dao.*;
import top.woodwhale.blog.pojo.*;
import top.woodwhale.blog.response.ResponseResult;
import top.woodwhale.blog.response.ResponseState;
import top.woodwhale.blog.services.IArticleService;
import top.woodwhale.blog.services.ISolrService;
import top.woodwhale.blog.services.IUserService;
import top.woodwhale.blog.utils.RedisUtils;
import top.woodwhale.blog.utils.SnowflakeIdUtils;
import top.woodwhale.blog.utils.TextUtils;

import javax.persistence.criteria.Predicate;
import java.util.*;

import static top.woodwhale.blog.utils.Constants.Article.*;
import static top.woodwhale.blog.utils.Constants.Label.LABEL_INIT_COUNT;
import static top.woodwhale.blog.utils.Constants.Page.DEFAULT_PAGE;
import static top.woodwhale.blog.utils.Constants.Page.MAX_SIZE;
import static top.woodwhale.blog.utils.Constants.TimeValue.REDIS_5_MINS;
import static top.woodwhale.blog.utils.Constants.User.ROLE_ADMIN;

@Slf4j
@Service
@Transactional
public class ArticleServiceImpl implements IArticleService {

    /**
     * 注入id生成器
     */
    @Autowired
    private SnowflakeIdUtils idUtils;

    /**
     * 注入redis工具
     */
    @Autowired
    private RedisUtils redisUtils;

    /**
     * 注入userService层
     */
    @Autowired
    private IUserService userService;

    /**
     * 注入solrService
     */
    @Autowired
    private ISolrService solrService;

    /**
     * 注入articleDao层
     */
    @Autowired
    private ArticleDao articleDao;

    /**
     * 注入articleNoContentDao层
     */
    @Autowired
    private ArticleNoContentDao articleNoContentDao;

    /**
     * 草稿dao层
     */
    @Autowired
    private ArticleSketchDao articleSketchDao;

    /**
     * 无内容草稿dao层
     */
    @Autowired
    private ArticleSketchNoContentDao articleSketchNoContentDao;

    /**
     * 文章分类dao层
     */
    @Autowired
    private CategoryDao categoryDao;

    /**
     * 评论dao层
     */
    @Autowired
    private CommentDao commentDao;

    /**
     * 标签dao层
     */
    @Autowired
    private LabelDao labelDao;

    /**
     * 发布新文章
     *
     * @param article 文章对象
     * @return ResponseResult
     */
    @Override
    public ResponseResult addArticle(Article article) {
        // 1. 判断用户状态
        User user = userService.checkUser();
        if (user == null) {
            return ResponseResult.GET(ResponseState.NOT_LOGIN);
        }
        // 2. 判断文章必要的数据是否都填写了
        ResponseResult dataIsFilledIn = articleDataIsFilledIn(article);
        if (dataIsFilledIn != null) {
            return dataIsFilledIn;
        }
        // 3. 补充文章数据
        article.setId(String.valueOf(idUtils.nextId()));
        article.setUserId(user.getId());
        article.setState(TextUtils.isEmpty(article.getState()) ? ARTICLE_PUBLISH_STATE :
                article.getState().equals(ARTICLE_TOP_STATE) ? ARTICLE_TOP_STATE :
                        ARTICLE_PUBLISH_STATE);  // 判断是否需要置顶
        article.setCreateTime(article.getCreateTime() == null ? new Date() : article.getCreateTime());
        article.setUpdateTime(new Date());
        article.setViewCount(ARTICLE_INIT_VIEW_COUNT);
        // 4. 保存文章数据到MySQL
        articleDao.save(article);
        addLabels2Mysql(article.getLabels());
        editCategory2Mysql(article.getCategoryId(), true);
        // 6. 将数据传入solr搜索中
        solrService.addArticle(article);
        return ResponseResult.SUCCESS(TextUtils.successAdd("文章"));
    }

    /**
     * 给分类添加一个数量
     *
     * @param categoryId 分类id
     */
    private void editCategory2Mysql(String categoryId, boolean flag) {
        Category categoryInMysql = categoryDao.findOneById(categoryId);
        Category copiedCategory = new Category();
        BeanUtils.copyProperties(categoryInMysql, copiedCategory);
        long nowCount = copiedCategory.getCount();
        copiedCategory.setCount(flag ? nowCount + 1 : nowCount - 1 < 0 ? 0 : nowCount - 1);
        categoryInMysql = copiedCategory;
        categoryDao.save(categoryInMysql);
    }

    /**
     * 将文章标签删除（准备更新文章之前调用)
     *
     * @param labels 标签
     */
    private void deleteLabelsInMysql(String labels) {
        List<String> labelTags = new ArrayList<>();
        if (!TextUtils.isEmpty(labels)) {
            if (labels.contains(",")) {
                labelTags.addAll(Arrays.asList(labels.split(",")));
            } else {
                labelTags.add(labels);
            }
            for (String label : labelTags) {
                int res = labelDao.deleteOneCount(label);
                // 无法删除证明该分类最后一个文章要被删除
                if (res == 0) {
                    int delRes = labelDao.deleteOneByName(label);
                    // 如果删除成功，log数据
                    if (delRes > 0) {
                        log.info("标签: " + label + " 删除成功!");
                    }
                }
            }
        }
    }

    /**
     * 将文章标签添加到article
     *
     * @param labels 标签
     */
    private void addLabels2Mysql(String labels) {
        // 5. 将标签加入到MySQL中
        List<String> labelTags = new ArrayList<>();
        if (!TextUtils.isEmpty(labels)) {
            if (labels.contains(",")) {
                labelTags.addAll(Arrays.asList(labels.split(",")));
            } else {
                labelTags.add(labels);
            }
            for (String label : labelTags) {
                // 先去执行更新，如果更新失败，就去新建入库一个label
                int res = labelDao.updateOneCount(label);
                if (res == 0) {
                    Label labelToMysql = new Label();
                    labelToMysql.setId(String.valueOf(idUtils.nextId()));
                    labelToMysql.setCount(LABEL_INIT_COUNT);
                    labelToMysql.setName(label);
                    labelToMysql.setCreateTime(new Date());
                    labelToMysql.setUpdateTime(new Date());
                    labelDao.save(labelToMysql);
                    log.info("标签: " + label + " 添加成功!");
                }
            }
        }
    }

    /**
     * 获取文章列表
     *
     * @param page       页数
     * @param size       每页容量
     * @param keyword    关键字（可选)
     * @param categoryId 文章分类id（可选）
     * @param state      文章状态（可选）
     * @return ResponseResult
     */
    @Override
    public ResponseResult listArticles(int page, int size, String keyword, String categoryId, String state) {
        page = Math.max(DEFAULT_PAGE, page);
        size = Math.min(MAX_SIZE, Math.max(size, 1));
        Page<ArticleNoContent> all = articleNoContentDao.findAll((Specification<ArticleNoContent>) (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (!TextUtils.isEmpty(state)) {
                predicates.add(state.equals(ARTICLE_NOT_REMOVE_STATE) ?
                        cb.notEqual(root.get("state").as(String.class), ARTICLE_REMOVE_STATE) :
                        cb.equal(root.get("state").as(String.class), state));
            }
            if (!TextUtils.isEmpty(categoryId)) {
                predicates.add(cb.equal(root.get("categoryId").as(String.class), categoryId));
            }
            if (!TextUtils.isEmpty(keyword)) {
                predicates.add(cb.like(root.get("title").as(String.class), "%" + keyword + "%"));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        }, PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "state", "createTime")));
        return ResponseResult.SUCCESS(TextUtils.successGet("文章列表")).setData(all);
    }

    /**
     * 获取文章内容
     *
     * @param articleId   文章id
     * @param isFromAdmin 是否是管理员获取
     * @return ResponseResult
     */
    @Override
    public ResponseResult getArticle(String articleId, boolean isFromAdmin) {
        // 1. 判断文章id合法性
        if (TextUtils.isEmpty(articleId)) {
            return ResponseResult.FAILED(TextUtils.notNullable("文章Id"));
        }
        // 2. 判断用户权限
        User user = userService.checkUser();

        // 如果是管理员获取，一定要获取最新的从MySQL中存储的
        if (isFromAdmin) {
            Article article = articleDao.findOneById(articleId);
            if (article == null) {
                return ResponseResult.FAILED(TextUtils.notExist("文章"));
            }
            return ResponseResult.SUCCESS("管理员获取文章成功").setData(article);
        }
        // 3. 先从redis中获取文章()
        Gson gson = new Gson();
        String articleJson = (String) redisUtils.get(REDIS_KEY_ARTICLE_CACHE + articleId);
        if (!TextUtils.isEmpty(articleJson)) {
            Article article = gson.fromJson(articleJson, Article.class);
            // 判断查看的文章状态
            if (user != null && !user.getRoles().equals(ROLE_ADMIN) && article.getState().equals(ARTICLE_REMOVE_STATE)) {
                // 普通用户不能查看被删除的文章和草稿文章
                return ResponseResult.FAILED(TextUtils.notExist("文章"));
            }
            // 增加阅读数量
            redisUtils.incr(REDIS_KEY_ARTICLE_VIEW_COUNT + articleId, 1);
            return ResponseResult.SUCCESS(TextUtils.successGet("文章")).setData(article);
        }
        // 4. mysql查询id是否存在
        Article article = articleDao.findOneById(articleId);
        if (article == null) {
            return ResponseResult.FAILED(TextUtils.notExist("文章"));
        }
        // 5. 判断查看的文章状态
        if (user != null && !user.getRoles().equals(ROLE_ADMIN) && article.getState().equals(ARTICLE_REMOVE_STATE)) {
            // 普通用户不能查看被删除的文章和草稿文章
            return ResponseResult.FAILED(TextUtils.notExist("文章"));
        }
        // 6. 存入redis中5分钟
        redisUtils.set(REDIS_KEY_ARTICLE_CACHE + articleId, gson.toJson(article), REDIS_5_MINS);
        // 7. 设置阅读量的key，先从redis中拿，如果redis中没有，就从MySQL中获取，并添加到redis中
        // 不是管理员编辑就不会添加阅读量
        String viewCount = (String) redisUtils.get(REDIS_KEY_ARTICLE_VIEW_COUNT + articleId);
        if (TextUtils.isEmpty(viewCount)) {
            redisUtils.set(REDIS_KEY_ARTICLE_VIEW_COUNT + articleId, (article.getViewCount() + 1) + "");
        } else {
            // 更新到MySQL中
            long newCount = redisUtils.incr(REDIS_KEY_ARTICLE_VIEW_COUNT + articleId, 1);
            article.setViewCount(newCount);
            articleDao.save(article);
            // 更新solr的阅读量
            solrService.updateArticle(articleId, article);
        }
        return ResponseResult.SUCCESS(TextUtils.successGet("文章")).setData(article);
    }

    /**
     * 更新文章状态的提取方法
     *
     * @param article       文章
     * @param copiedArticle 被复制的文章对象
     */
    private void updateCopiedArticle(Article article, Article copiedArticle) {
        // 判断修改是否合法
        String categoryId = article.getCategoryId();
        if (!TextUtils.isEmpty(categoryId) && categoryDao.findOneById(categoryId) != null) {
            copiedArticle.setCategoryId(categoryId);
        }
        if (!TextUtils.isEmpty(article.getTitle())) {
            copiedArticle.setTitle(article.getTitle());
        }
        if (!TextUtils.isEmpty(article.getContent())) {
            copiedArticle.setContent(article.getContent());
        }
        if (!TextUtils.isEmpty(article.getSummary())) {
            copiedArticle.setSummary(article.getSummary());
        }
        if (!TextUtils.isEmpty(article.getLabels())) {
            deleteLabelsInMysql(copiedArticle.getLabels());
            copiedArticle.setLabels(article.getLabels());
            addLabels2Mysql(copiedArticle.getLabels());
        }
        copiedArticle.setCover(article.getCover()); // 图片可设置为空
        if (article.getCreateTime() != null) {
            copiedArticle.setCreateTime(article.getCreateTime());
        }
        if (!TextUtils.isEmpty(article.getState())) {
            // 只要不是删除状态，都可以设置
            if (!article.getState().equals(ARTICLE_REMOVE_STATE)) {
                copiedArticle.setState(article.getState());
            }
        }
        copiedArticle.setUpdateTime(new Date());
    }

    /**
     * 更新文章
     * 仅仅支持已发布的文章，可以修改：标题、内容、分类、摘要
     *
     * @param articleId 文章id
     * @param article   文章bean类
     * @return ResponseResult
     */
    @Override
    public ResponseResult updateArticle(String articleId, Article article) {
        // 判断文章id合法性
        if (TextUtils.isEmpty(articleId)) {
            return ResponseResult.FAILED(TextUtils.notNullable("文章Id"));
        }
        // 1. 从MySQL中找出该文章
        Article articleInMysql = articleDao.findOneById(articleId);
        if (articleInMysql == null || articleInMysql.getState().equals(ARTICLE_REMOVE_STATE)) {
            return ResponseResult.FAILED(TextUtils.notExist("文章"));
        }
        // 2. 判断文章必要的数据是否都填写了
        Article copiedArticle = new Article();
        BeanUtils.copyProperties(articleInMysql, copiedArticle);
        updateCopiedArticle(article, copiedArticle);
        // 3. 判断文章内容是否合法
        ResponseResult dataIsFilledIn = articleDataIsFilledIn(copiedArticle);
        if (dataIsFilledIn != null) {
            return dataIsFilledIn;
        }
        if (!articleInMysql.getCategoryId().equals(article.getCategoryId())) {
            editCategory2Mysql(articleInMysql.getCategoryId(), false);
            editCategory2Mysql(article.getCategoryId(), true);
        }
        articleInMysql = copiedArticle;
        articleDao.save(articleInMysql);
        // 4. solr更新数据
        solrService.updateArticle(articleInMysql.getId(), articleInMysql);
        return ResponseResult.SUCCESS(TextUtils.successUpdate("文章"));
    }

    /**
     * 判断文章的数据是否都填写了
     *
     * @param article article
     * @return ResponseResult
     */
    @Nullable
    private ResponseResult articleDataIsFilledIn(Article article) {
        if (article == null) {
            return ResponseResult.FAILED(TextUtils.notNullable("文章数据"));
        }
        if (TextUtils.isEmpty(article.getCategoryId()) || categoryDao.findOneById(article.getCategoryId()) == null) {
            return ResponseResult.FAILED(TextUtils.smtError("文章分类"));
        }
        if (TextUtils.isEmpty(article.getTitle())) {
            return ResponseResult.FAILED(TextUtils.notNullable("文章标题"));
        }
        if (article.getTitle().length() > ARTICLE_TITLE_MAX_LENGTH) {
            return ResponseResult.FAILED("文章标题长度不可超过128个字符");
        }
        if (TextUtils.isEmpty(article.getContent())) {
            return ResponseResult.FAILED(TextUtils.notNullable("文章内容"));
        }
        if (TextUtils.isEmpty(article.getType())) {
            return ResponseResult.FAILED(TextUtils.notNullable("文章格式"));
        }
        if (TextUtils.isEmpty(article.getSummary())) {
            return ResponseResult.FAILED(TextUtils.notNullable("文章摘要"));
        }
        if (article.getSummary().length() > ARTICLE_SUMMARY_MAX_LENGTH) {
            return ResponseResult.FAILED("文章摘要长度不可超过256个字符");
        }
        if (TextUtils.isEmpty(article.getLabels())) {
            return ResponseResult.FAILED(TextUtils.notNullable("文章标签"));
        }
        return null;
    }

    /**
     * 从MySQL中删除文章
     *
     * @param articleId 文章id
     * @return ResponseResult
     */
    @Override
    public ResponseResult deleteArticle(String articleId) {
        // 1. 判断文章id合法性
        if (TextUtils.isEmpty(articleId)) {
            return ResponseResult.FAILED(TextUtils.notNullable("文章Id"));
        }
        // 2. 判断文章是否存在
        Article articleInMysql = articleDao.findOneById(articleId);
        if (articleInMysql == null) {
            return ResponseResult.FAILED(TextUtils.notExist("文章"));
        }
        if (!articleInMysql.getState().equals(ARTICLE_REMOVE_STATE)) {
            return ResponseResult.FAILED("仅回收站文章可删除");
        }
        // 4. 如果是已发布的文章，需要删除评论
        String title = articleInMysql.getTitle();
        int commentDeleteRes = commentDao.deleteAllByArticleId(articleId);
        if (commentDeleteRes > 0) {
            log.info("id: " + articleId + " 标题: " + title + " 的comment被删除了");
        }
        // 5. 删除标签
        deleteLabelsInMysql(articleInMysql.getLabels());
        // 6. 删除文章
        int articleDeleteRes = articleDao.deleteOneById(articleId);
        if (articleDeleteRes > 0) {
            // 删除solr，删除redis中的缓存
            solrService.deleteArticle(articleId);
            redisUtils.del(REDIS_KEY_ARTICLE_CACHE + articleId);
            log.info("id: " + articleId + " 标题: " + title + " 的article被删除了");
            return ResponseResult.SUCCESS(TextUtils.successDelete("文章"));
        }
        return ResponseResult.FAILED(TextUtils.failDelete("文章"));
    }

    /**
     * 修改文章状态
     * <p>
     * 作用如下：
     * <p>
     * 1. 发布 -> 删除（删除到回收站操作）    请求删除
     * 2. 删除 -> 发布（回收站还原操作）     请求发布
     * 3. 置顶 -> 普通发布（取消置顶操作）   请求置顶
     * 4. 普通发布 -> 置顶（置顶操作）      请求发布
     *
     * @param articleId 文章id
     * @param state     文章状态
     * @return ResponseResult
     */
    @Override
    public ResponseResult updateArticleState(String articleId, String state) {
        if (TextUtils.isEmpty(articleId)) {
            return ResponseResult.FAILED(TextUtils.notNullable("文章Id"));
        }
        // 1. 判断文章是否存在
        Article articleInMysql = articleDao.findOneById(articleId);
        if (articleInMysql == null) {
            return ResponseResult.FAILED(TextUtils.notExist("文章"));
        }
        // 2. 判断文章状态是否发生改变，置顶状态除外
        if (articleInMysql.getState().equals(state) && !state.equals(ARTICLE_TOP_STATE)) {
            return ResponseResult.FAILED("文章状态未改变");
        }
        // 3. 判断需要修改的状态
        if (state.equals(ARTICLE_PUBLISH_STATE) && articleInMysql.getState().equals(ARTICLE_REMOVE_STATE)) {
            // 删除转为发布
            editCategory2Mysql(articleInMysql.getCategoryId(), true);
            int res = articleDao.publishOneById(articleId);
            if (res > 0) {
                // 更新solr记录
                solrService.addArticle(articleDao.findOneById(articleId));
                return ResponseResult.SUCCESS(TextUtils.successUpdate("文章发布状态"));
            }
            return ResponseResult.FAILED(TextUtils.failUpdate("文章发布状态"));
        }
        if (state.equals(ARTICLE_REMOVE_STATE) &&
                (articleInMysql.getState().equals(ARTICLE_PUBLISH_STATE) ||
                        articleInMysql.getState().equals(ARTICLE_TOP_STATE))) {
            // 发布转为删除
            editCategory2Mysql(articleInMysql.getCategoryId(), false);
            int res = articleDao.deleteOneStateById(articleId);
            if (res > 0) {
                // 删除solr记录
                solrService.deleteArticle(articleId);
                return ResponseResult.SUCCESS(TextUtils.successUpdate("文章删除状态"));
            }
            return ResponseResult.FAILED(TextUtils.failUpdate("文章删除状态"));
        }
        if (state.equals(ARTICLE_TOP_STATE) && articleInMysql.getState().equals(ARTICLE_PUBLISH_STATE)) {
            // 发布转为置顶
            int res = articleDao.topOneById(articleId);
            if (res > 0) {
                return ResponseResult.SUCCESS(TextUtils.successUpdate("文章置顶状态"));
            }
            return ResponseResult.FAILED(TextUtils.failUpdate("文章置顶状态"));
        }
        if (state.equals(ARTICLE_TOP_STATE) && articleInMysql.getState().equals(ARTICLE_TOP_STATE)) {
            // 取消置顶
            int res = articleDao.publishOneById(articleId);
            if (res > 0) {
                return ResponseResult.SUCCESS(TextUtils.successUpdate("文章取消置顶状态"));
            }
            return ResponseResult.FAILED(TextUtils.failUpdate("文章取消置顶状态"));
        }
        return ResponseResult.FAILED(TextUtils.smtError("文章状态修改"));
    }

    /**
     * 新建草稿文章
     * 保存到MySQL中，state字段为 2
     *
     * @param articleSketch 草稿bean类
     * @return ResponseResult
     */
    @Override
    public ResponseResult addSketchArticle(ArticleSketch articleSketch) {
        // 1. 判断用户是否登录
        User user = userService.checkUser();
        if (user == null) {
            return ResponseResult.GET(ResponseState.NOT_LOGIN);
        }
        // 2. 判断必要内容是否填写
        if (TextUtils.isEmpty(articleSketch.getTitle())) {
            return ResponseResult.FAILED(TextUtils.notNullable("文章标题"));
        }
        if (TextUtils.isEmpty(articleSketch.getContent())) {
            return ResponseResult.FAILED(TextUtils.notNullable("文章内容"));
        }

        // 3. 判断文章分类的合法
        String categoryId = articleSketch.getCategoryId();
        if (!TextUtils.isEmpty(categoryId)) {
            Category categoryInMysql = categoryDao.findOneById(categoryId);
            if (categoryInMysql == null) {
                return ResponseResult.FAILED(TextUtils.notExist("文章分类"));
            }
        }
        // 4. 补充数据
        articleSketch.setId(String.valueOf(idUtils.nextId()));
        articleSketch.setUserId(user.getId());
        articleSketch.setCreateTime(new Date());
        articleSketch.setUpdateTime(new Date());
        // 5. 保存到数据库
        articleSketchDao.save(articleSketch);
        // 6. 添加label到MySQL
        if (!TextUtils.isEmpty(articleSketch.getLabels())) {
            addLabels2Mysql(articleSketch.getLabels());
        }
        return ResponseResult.SUCCESS(TextUtils.successAdd("文章草稿"));
    }

    /**
     * 更新草稿内容
     *
     * @param articleId     articleId
     * @param articleSketch article bean类
     * @return ResponseResult
     */
    @Override
    public ResponseResult updateSketchArticle(String articleId, ArticleSketch articleSketch) {
        // 判断id是否合法
        if (TextUtils.isEmpty(articleId)) {
            return ResponseResult.FAILED(TextUtils.notNullable("文章草稿Id"));
        }
        ArticleSketch articleInMysql = articleSketchDao.findOneById(articleId);
        // 如果为空，返回失败
        if (articleInMysql == null) {
            return ResponseResult.FAILED(TextUtils.notExist("文章草稿"));
        }
        // 复制对象
        ArticleSketch copiedArticle = new ArticleSketch();
        BeanUtils.copyProperties(articleInMysql, copiedArticle);
        // 更新数据
        if (TextUtils.isEmpty(articleSketch.getTitle())) {
            return ResponseResult.FAILED(TextUtils.notNullable("文章标题"));
        } else {
            copiedArticle.setTitle(articleSketch.getTitle());
        }
        if (TextUtils.isEmpty(articleSketch.getContent())) {
            return ResponseResult.FAILED(TextUtils.notNullable("文章内容"));
        } else {
            copiedArticle.setContent(articleSketch.getContent());
        }
        if (!TextUtils.isEmpty(articleSketch.getType())) {
            copiedArticle.setType(articleSketch.getType().equals(ARTICLE_RICH_TYPE) ? ARTICLE_RICH_TYPE : ARTICLE_MARKDOWN_TYPE);
        }
        if (!TextUtils.isEmpty(articleSketch.getCategoryId())) {
            String categoryId = articleSketch.getCategoryId();
            Category categoryInMysql = categoryDao.findOneById(categoryId);
            if (categoryInMysql == null) {
                return ResponseResult.FAILED(TextUtils.notExist("文章分类"));
            } else {
                copiedArticle.setCategoryId(categoryId);
            }
        }
        if (!TextUtils.isEmpty(articleSketch.getSummary())) {
            copiedArticle.setSummary(articleSketch.getSummary());
        }
        if (!TextUtils.isEmpty(articleSketch.getLabels())) {
            // 更新之前，先去把MySQL中的标签重置一下
            deleteLabelsInMysql(copiedArticle.getLabels());
            copiedArticle.setLabels(articleSketch.getLabels());
            // 添加新的标签
            addLabels2Mysql(copiedArticle.getLabels());
        }
        copiedArticle.setUpdateTime(new Date());
        // 保存
        articleInMysql = copiedArticle;
        articleSketchDao.save(articleInMysql);
        return ResponseResult.SUCCESS(TextUtils.successUpdate("文章草稿"));
    }

    /**
     * 发布草稿文章 （草稿 -> 发布）
     *
     * @param articleId 文章id
     * @return ResponseResult
     */
    @Override
    public ResponseResult publishSketchArticle(String articleId) {
        // 判断user是否登录
        User user = userService.checkUser();
        if (user == null) {
            return ResponseResult.GET(ResponseState.NOT_LOGIN);
        }
        ArticleSketch articleInMysql = articleSketchDao.findOneById(articleId);
        if (articleInMysql == null) {
            return ResponseResult.FAILED(TextUtils.notExist("文章草稿"));
        }
        // 准备需要发布的文章的数据
        Article publishArticle = new Article();
        // 保存数据
        if (!TextUtils.isEmpty(articleInMysql.getTitle())) {
            publishArticle.setTitle(articleInMysql.getTitle());
        }
        if (!TextUtils.isEmpty(articleInMysql.getContent())) {
            publishArticle.setContent(articleInMysql.getContent());
        }
        if (!TextUtils.isEmpty(articleInMysql.getSummary())) {
            publishArticle.setSummary(articleInMysql.getSummary());
        } else {
            publishArticle.setSummary(publishArticle.getContent().length() > 55 ?
                    publishArticle.getContent().substring(0, 55) : publishArticle.getContent());
        }
        if (!TextUtils.isEmpty(articleInMysql.getType())) {
            publishArticle.setType(articleInMysql.getType());
        } else {
            publishArticle.setType(ARTICLE_MARKDOWN_TYPE);
        }
        if (!TextUtils.isEmpty(articleInMysql.getLabels())) {
            publishArticle.setLabels(articleInMysql.getLabels());
            publishArticle.setLabelTags(articleInMysql.getLabels().contains(",") ?
                    Arrays.asList(articleInMysql.getLabels().split(",")) : List.of(articleInMysql.getLabels()));
        }
        if (!TextUtils.isEmpty(articleInMysql.getCategoryId())) {
            publishArticle.setCategoryId(articleInMysql.getCategoryId());
        }
        if (!TextUtils.isEmpty(articleInMysql.getCover())) {
            publishArticle.setCover(articleInMysql.getCover());
        }
        if (!TextUtils.isEmpty(articleInMysql.getUserId())) {
            publishArticle.setUserId(articleInMysql.getUserId());
        } else {
            publishArticle.setUserId(user.getId());
        }
        publishArticle.setState(ARTICLE_PUBLISH_STATE);
        publishArticle.setViewCount(ARTICLE_INIT_VIEW_COUNT);
        publishArticle.setId(idUtils.nextId() + "");
        publishArticle.setCreateTime(new Date());
        publishArticle.setCreateTime(new Date());
        // 检测数据是否填充完毕
        ResponseResult dataIsFilledIn = articleDataIsFilledIn(publishArticle);
        if (dataIsFilledIn != null) {
            return dataIsFilledIn;
        }
        articleDao.save(publishArticle);
        // solr更新
        solrService.addArticle(publishArticle);
        // 文章分类更新
        editCategory2Mysql(publishArticle.getCategoryId(), true);
        return ResponseResult.SUCCESS(TextUtils.successAdd("文章"));
    }

    /**
     * 获取置顶文章列表
     *
     * @return ResponseResult
     */
    @Override
    public ResponseResult listTopArticles() {
        List<ArticleNoContent> topList = articleNoContentDao.findAll((Specification<ArticleNoContent>)
                        (root, query, criteriaBuilder) ->
                                criteriaBuilder.equal(root.get("state").as(String.class), ARTICLE_TOP_STATE),
                Sort.by(Sort.Direction.DESC, "createTime"));
        return ResponseResult.SUCCESS(TextUtils.successGet("置顶文章")).setData(topList);

    }

    /**
     * 获取推荐文章列表
     * 通过标签来进行处理
     *
     * @param articleId 文章id
     * @param size      推荐文章数量
     * @return ResponseResult
     */
    @Override
    public ResponseResult listRecommendArticles(String articleId, int size) {
        if (size > 5) size = 5;
        // 判断文章是否存在
        ArticleNoContent articleNoContentInMysql = articleNoContentDao.findOneById(articleId);
        if (articleNoContentInMysql == null ||
                articleNoContentInMysql.getState().equals(ARTICLE_REMOVE_STATE)) {
            return ResponseResult.FAILED(TextUtils.notExist("文章"));
        }
        // 查询文章，不需要文章内容，只需要标签
        String labels = articleNoContentDao.listArticleLabelsById(articleId);
        List<String> labelTags = new ArrayList<>();
        if (labels != null) {
            if (labels.contains(",")) {
                labelTags.addAll(Arrays.asList(labels.split(",")));
            } else {
                labelTags.add(labels);
            }
        } else {
            return ResponseResult.FAILED(TextUtils.notExist("文章"));
        }
        // 从列表中随机获取一个标签，查询该标签中的文章
        String tag = labelTags.get(TextUtils.getRandomInt(labelTags.size()));
        log.info("被随机选择到的推荐标签 --> " + tag);
        // 如果查询出来的数量不够，那么就去找最新的文章进行推荐
        List<ArticleNoContent> resList = new ArrayList<>(articleNoContentDao
                .listArticleLabelsByLike("%" + tag + "%", articleId, size));
        log.info("已经找到推荐文章" + resList.size() + "篇");
        if (resList.size() < size) {
            int remainSize = size - resList.size();
            List<ArticleNoContent> articleNoContents = articleNoContentDao
                    .listArticleLatestBySize(articleId, remainSize);
            if (articleNoContents != null && articleNoContents.size() != 0) {
                resList.addAll(articleNoContents);
            }
        }
        return ResponseResult.SUCCESS(TextUtils.successGet("推荐文章列表")).setData(new HashSet<>(resList));
    }

    /**
     * 通过标签获取文章list
     *
     * @param page  页数
     * @param size  每页容量
     * @param label 标签
     * @return ResponseResult
     */
    @Override
    public ResponseResult listLabelArticles(int page, int size, String label) {
        String labelTrim = label.trim();
        if (TextUtils.isEmpty(labelTrim)) {
            return ResponseResult.FAILED(TextUtils.notNullable("标签"));
        }
        page = Math.max(DEFAULT_PAGE, page);
        size = Math.min(MAX_SIZE, Math.max(size, 1));
        Page<ArticleNoContent> res = articleNoContentDao.findAll((Specification<ArticleNoContent>)
                        (root, query, criteriaBuilder) ->
                                criteriaBuilder.and(
                                        criteriaBuilder.like(root.get("labels").as(String.class), "%" + labelTrim + "%"),
                                        criteriaBuilder.or(
                                                criteriaBuilder.equal(root.get("state").as(String.class), ARTICLE_PUBLISH_STATE),
                                                criteriaBuilder.equal(root.get("state").as(String.class), ARTICLE_TOP_STATE
                                                )
                                        )),
                PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "state", "createTime")));
        return ResponseResult.SUCCESS(TextUtils.successGet("标签文章列表")).setData(res);
    }

    /**
     * 获取排名前size名的标签
     *
     * @param size 获取前size个标签
     * @return ResponseResult
     */
    @Override
    public ResponseResult listLabels(int size) {
        size = Math.min(MAX_SIZE, Math.max(size, 1));
        Page<Label> all = labelDao.findAll(PageRequest.of(0, size,
                Sort.by(Sort.Direction.DESC, "count")));
        return ResponseResult.SUCCESS(TextUtils.successGet("标签列表")).setData(all);
    }

    /**
     * 删除草稿文章
     *
     * @param articleId 草稿id
     * @return ResponseResult
     */
    @Override
    public ResponseResult deleteSketchArticle(String articleId) {
        ArticleSketch sketchInMysql = articleSketchDao.findOneById(articleId);
        if (sketchInMysql == null) {
            return ResponseResult.FAILED(TextUtils.notExist("文章草稿"));
        }
        // 直接从数据库中删除
        int res = articleSketchDao.deleteOneById(articleId);
        if (res > 0) {
            return ResponseResult.SUCCESS(TextUtils.successDelete("文章草稿"));
        }
        return ResponseResult.FAILED(TextUtils.smtError("文章草稿删除"));
    }

    /**
     * 获取文章草稿列表
     *
     * @param page 页数
     * @param size 每页容量
     * @return ResponseResult
     */
    @Override
    public ResponseResult listSketchArticles(int page, int size) {
        User user = userService.checkUser();
        if (user == null) {
            return ResponseResult.GET(ResponseState.NOT_LOGIN);
        }
        page = Math.max(DEFAULT_PAGE, page);
        size = Math.min(MAX_SIZE, Math.max(size, 1));
        Page<ArticleSketchNoContent> all = articleSketchNoContentDao.findAll((Specification<ArticleSketchNoContent>)
                        (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("userId").as(String.class), user.getId()),
                PageRequest.of(page - 1, size,
                        Sort.by(Sort.Direction.DESC, "updateTime")));
        return ResponseResult.SUCCESS(TextUtils.successGet("文章草稿列表")).setData(all);
    }

    /**
     * 获取草稿文章
     *
     * @param articleId 草稿文章id
     * @return ResponseResult
     */
    @Override
    public ResponseResult getSketchArticle(String articleId) {
        ArticleSketch sketchInMysql = articleSketchDao.findOneById(articleId);
        if (sketchInMysql == null) {
            return ResponseResult.FAILED(TextUtils.notExist("文章草稿"));
        }
        return ResponseResult.SUCCESS(TextUtils.successGet("文章草稿")).setData(sketchInMysql);
    }

    /**
     * 获取文章标题
     *
     * @param articleId 文章id
     * @return ResponseResult
     */
    @Override
    public ResponseResult getArticleTitle(String articleId) {
        if (TextUtils.isEmpty(articleId)) {
            return ResponseResult.FAILED(TextUtils.notNullable("文章Id"));
        }
        HashMap<String, String> map = new HashMap<>();
        if (articleId.contains("_") && articleId.split("_").length > 0) {
            for (String id : articleId.split("_")) {
                ArticleNoContent articleInMysql = articleNoContentDao.findOneById(id);
                if (articleInMysql != null) {
                    map.put(id, articleInMysql.getTitle());
                }
            }
        } else {
            ArticleNoContent articleInMysql = articleNoContentDao.findOneById(articleId);
            if (articleInMysql == null || articleInMysql.getState().equals(ARTICLE_REMOVE_STATE)) {
                return ResponseResult.FAILED(TextUtils.notExist("文章"));
            }
            map.put(articleId, articleInMysql.getTitle());
        }
        if (!map.isEmpty()) {
            return ResponseResult.SUCCESS(TextUtils.successGet("文章标题")).setData(map);
        }
        return ResponseResult.FAILED(TextUtils.notExist("文章"));
    }

    /**
     * 门户获取文章
     *
     * @param page page
     * @param size size
     * @return ResponseResult
     */
    @Override
    public ResponseResult listArticlesFromPortal(int page, int size) {
        page = Math.max(DEFAULT_PAGE, page);
        size = Math.min(MAX_SIZE, Math.max(size, 1));
        Page<ArticleNoContent> article = articleNoContentDao.findAll((Specification<ArticleNoContent>) (root, query, cb) ->
                        cb.notEqual(root.get("state").as(String.class), ARTICLE_REMOVE_STATE),
                PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "state", "createTime")));
        return ResponseResult.SUCCESS(TextUtils.successGet("门户文章")).setData(article);
    }

    // TODO：文章列表添加——点击数、评论数，需要对相应的接口进行调整
}
