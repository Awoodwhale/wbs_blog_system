package top.woodwhale.blog.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import top.woodwhale.blog.pojo.Setting;

/**
 * Setting Bean类的Dao层
 */
public interface SettingDao extends JpaRepository<Setting, String>, JpaSpecificationExecutor<Setting> {
    /**
     * 通过key寻找setting
     * @param key key
     * @return Setting
     */
    Setting findOneByKey(String key);
}
