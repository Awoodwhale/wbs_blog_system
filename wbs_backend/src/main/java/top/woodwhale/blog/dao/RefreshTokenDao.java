package top.woodwhale.blog.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import top.woodwhale.blog.pojo.RefreshToken;

/**
 * RefreshTokenDao层
 */
public interface RefreshTokenDao extends JpaRepository<RefreshToken, String>, JpaSpecificationExecutor<RefreshToken> {
    /**
     * 通过tokenKey寻找RefreshTokenDao
     * @param tokenKey tokenKey
     * @return RefreshToken
     */
    RefreshToken findOneByTokenKey(String tokenKey);

    /**
     * 通过userId寻找RefreshToken
     * @param userId userId
     * @return RefreshToken
     */
    int deleteAllByUserId(String userId);

    /**
     * 通过tokenKy删除RefreshToken
     * @param tokenKey tokenKye
     * @return int
     */
    int deleteAllByTokenKey(String tokenKey);

    /**
     * 通过userId更新token key
     * @param newTokenKey 新的tokenKey
     * @param id userid
     * @return int
     */
    @Modifying
    @Query(nativeQuery = true, value = "update `tb_refresh_token` set `token_key` = ? where `user_id` = ?")
    int updateNewTokenKeyByUserId(String newTokenKey, String id);
}
