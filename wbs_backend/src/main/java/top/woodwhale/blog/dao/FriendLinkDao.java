package top.woodwhale.blog.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import top.woodwhale.blog.pojo.FriendLink;

/**
 * 友链dao层
 */
public interface FriendLinkDao extends JpaRepository<FriendLink, String>, JpaSpecificationExecutor<FriendLink> {

    /**
     * 通过友链id寻找友链
     * @param friendLinkId 友链id
     * @return FriendLink
     */
    FriendLink findOneById(String friendLinkId);

    /**
     * 通过友链url寻找友链
     * @param url url
     * @return FriendLink
     */
    FriendLink findOneByUrl(String url);

    /**
     * 通过友链id删除友链
     * @param friendLinkId 友链id
     * @return int
     */
    @Modifying
    @Query(nativeQuery = true, value = "update `tb_friends` set `state` = '0' where `id` = ?")
    int deleteOneById(String friendLinkId);
}
