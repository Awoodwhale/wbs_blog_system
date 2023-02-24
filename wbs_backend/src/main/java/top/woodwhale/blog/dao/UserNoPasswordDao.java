package top.woodwhale.blog.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import top.woodwhale.blog.pojo.UserNoPassword;

/**
 * User Bean类的Dao层
 */
public interface UserNoPasswordDao extends JpaRepository<UserNoPassword, String>, JpaSpecificationExecutor<UserNoPassword> {
    UserNoPassword findOneById(String id);
}
