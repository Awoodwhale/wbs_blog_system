package top.woodwhale.blog.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import top.woodwhale.blog.pojo.Looper;

/**
 * 轮播图dao层
 */
public interface LooperDao extends JpaRepository<Looper, String>, JpaSpecificationExecutor<Looper> {
    /**
     * 通过轮播图id寻找轮播图
     * @param looperId 轮播图id
     * @return Looper
     */
    Looper findOneById(String looperId);

    /**
     * 通过轮播图id删除轮播图
     * @param looperId 轮播图id
     * @return  int
     */
    int deleteOneById(String looperId);
}
