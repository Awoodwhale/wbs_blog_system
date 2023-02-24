package top.woodwhale.blog.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.woodwhale.blog.dao.FriendLinkDao;
import top.woodwhale.blog.pojo.FriendLink;
import top.woodwhale.blog.response.ResponseResult;
import top.woodwhale.blog.services.IFriendLinkService;
import top.woodwhale.blog.utils.SnowflakeIdUtils;
import top.woodwhale.blog.utils.TextUtils;

import java.util.Date;
import java.util.List;

import static top.woodwhale.blog.utils.Constants.FriendLink.*;

@Slf4j
@Service
@Transactional
public class FriendLinkServiceImpl implements IFriendLinkService {

    /**
     * 友情连接的dao层注入
     */
    @Autowired
    private FriendLinkDao friendLinkDao;

    /**
     * ID生成器注入
     */
    @Autowired
    private SnowflakeIdUtils idUtils;

    /**
     * 添加友链（通过友链url判断是否存在相同友链）
     * @param friendLink 友链Bean类
     * @return ResponseResult
     */
    @Override
    public ResponseResult addFriendLink(FriendLink friendLink) {
        // 1. 判断信息是否合法，
        if (TextUtils.isEmpty(friendLink.getName())) {
            return ResponseResult.FAILED(TextUtils.notNullable("友链name"));
        }
        if (TextUtils.isEmpty(friendLink.getUrl())) {
            return ResponseResult.FAILED(TextUtils.notNullable("友链URL"));
        }
        // 2. 去MySQL中查询是否之前添加删除过
        boolean flag = false;
        FriendLink friendLinkInMysql = friendLinkDao.findOneByUrl(friendLink.getUrl());
        if (friendLinkInMysql != null) {
            // 如果查询结果不为空，且这个结果不是删除状态，那么就是已经存在了这个友链
            if (friendLinkInMysql.getState().equals(FRIEND_LINK_NORMAL_STATE)) {
                return ResponseResult.FAILED(TextUtils.hasExisted("友链"));
            }
            // 否则就是之前删除过这个友链
            flag = true;
        }
        // 3. 补充数据
        friendLink.setId(flag ? friendLinkInMysql.getId() : String.valueOf(idUtils.nextId()));
        friendLink.setLogo(TextUtils.isEmpty(friendLink.getLogo()) ? DEFAULT_LOGO : friendLink.getLogo());
        friendLink.setState(FRIEND_LINK_NORMAL_STATE);
        friendLink.setCreateTime(new Date());
        friendLink.setUpdateTime(new Date());
        // 4. 保存到MySQL中
        friendLinkDao.save(friendLink);
        return ResponseResult.SUCCESS(TextUtils.successAdd("友链"));
    }

    /**
     * 通过友链的ID删除友链
     * 并非真的删除，而是通过修改这条记录的状态
     * @param friendLinkId 友链id
     * @return ResponseResult
     */
    @Override
    public ResponseResult deleteFriendLink(String friendLinkId) {
        // 1. 判断参数是否有效
        if (TextUtils.isEmpty(friendLinkId)) {
            return ResponseResult.FAILED(TextUtils.notNullable("友链ID"));
        }
        // 是否是批量删除
        if (friendLinkId.contains("_") && friendLinkId.split("_").length > 0) {
            boolean flag = true;
            for (String id : friendLinkId.split("_")) {
                // 删除友链（更改友链状态）
                int res = friendLinkDao.deleteOneById(id);
                if (res <= 0) {
                    flag = false;
                }
            }
            return flag ? ResponseResult.SUCCESS(TextUtils.successDelete("友链批量")) :
                    ResponseResult.FAILED(TextUtils.failDelete("友链批量"));
        } else {
            // 2.删除友链（更改友链状态）
            int res = friendLinkDao.deleteOneById(friendLinkId);
            return res > 0 ? ResponseResult.SUCCESS(TextUtils.successDelete("友链")) :
                    ResponseResult.FAILED(TextUtils.failDelete("友链"));
        }

    }

    /**
     * 通过友链ID进行更改友链信息
     * @param friendLinkId 友链id
     * @param friendLinkInfo 友链Bean类
     * @return ResponseResult
     */
    @Override
    public ResponseResult updateFriendLink(String friendLinkId, FriendLink friendLinkInfo) {
        // 1. 判断友链是否有效
        if (TextUtils.isEmpty(friendLinkId)) {
            return ResponseResult.FAILED(TextUtils.notNullable("友链URL"));
        }
        // 2. 从MySQL中取出友链对象
        FriendLink friendLinkInMysql = friendLinkDao.findOneById(friendLinkId);
        if (friendLinkInMysql == null || friendLinkInMysql.getState().equals(FRIEND_LINK_REMOVE_STATE)) {
            return ResponseResult.FAILED(TextUtils.notExist("友链"));
        }
        FriendLink copiedFriendLink = new FriendLink();
        BeanUtils.copyProperties(friendLinkInMysql,copiedFriendLink);
        // 3. 设置新信息
        if (!TextUtils.isEmpty(friendLinkInfo.getUrl())) {
            copiedFriendLink.setUrl(friendLinkInfo.getUrl());
        } else {
            return ResponseResult.FAILED(TextUtils.notNullable("友链URL"));
        }
        if (!TextUtils.isEmpty(friendLinkInfo.getName())) {
            copiedFriendLink.setName(friendLinkInfo.getName());
        } else {
            return ResponseResult.FAILED(TextUtils.notNullable("友链name"));
        }
        if (!TextUtils.isEmpty(friendLinkInfo.getLogo())) {
            copiedFriendLink.setLogo(friendLinkInfo.getLogo());
        }
        // 4. 保存bean数据
        copiedFriendLink.setOrder(friendLinkInfo.getOrder());
        copiedFriendLink.setUpdateTime(new Date());
        friendLinkInMysql = copiedFriendLink;
        // 5. 保存数据到MySQL中
        friendLinkDao.save(friendLinkInMysql);
        return ResponseResult.SUCCESS(TextUtils.successUpdate("友链信息"));
    }

    /**
     * 通过友链ID获取友链信息
     * @param friendLinkId 友链id
     * @return ResponseResult
     */
    @Override
    public ResponseResult getFriendLink(String friendLinkId) {
        // 1. 数据判空
        if (TextUtils.isEmpty(friendLinkId)) {
            return ResponseResult.FAILED(TextUtils.notNullable("友链ID"));
        }
        // 2. 判断是否存在
        FriendLink friendLinkInMysql = friendLinkDao.findOneById(friendLinkId);
        if (friendLinkInMysql == null || friendLinkInMysql.getState().equals(FRIEND_LINK_REMOVE_STATE)) {
            return ResponseResult.FAILED(TextUtils.notExist("友链"));
        }
        return ResponseResult.SUCCESS(TextUtils.successGet("友链信息")).setData(friendLinkInMysql);
    }

    /**
     * 获取友链列表
     * @return ResponseResult
     */
    @Override
    public ResponseResult listFriendLinks() {
        // 2. 根据创建时间和order进行降序排序 ，同时选择没有被删除的categories
        List<FriendLink> friendLinksList = friendLinkDao.findAll((Specification<FriendLink>)
                        (root, query, criteriaBuilder) ->
                                criteriaBuilder.equal(root.get("state").as(String.class), FRIEND_LINK_NORMAL_STATE)
                ,Sort.by(Sort.Direction.DESC, "createTime", "order"));
        return ResponseResult.SUCCESS(TextUtils.successGet("友链列表")).setData(friendLinksList);
    }
}
