package top.woodwhale.blog.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import top.woodwhale.blog.pojo.Image;

/**
 * 图片dao层
 */
public interface ImageDao extends JpaRepository<Image, String>, JpaSpecificationExecutor<Image> {

    /**
     * 通过图片id查找图片
     * @param imageId 图片id
     * @return Image
     */
    Image findOneById(String imageId);

    /**
     * 通過imageId刪除image
     * @param imageId imageId
     * @param userId userId
     * @return int
     */
    @Modifying
    @Query(nativeQuery = true, value = "delete from `tb_images` where `id` = ? and `user_id` = ?")
    int deleteOneByImageId(String imageId, String userId);

}
