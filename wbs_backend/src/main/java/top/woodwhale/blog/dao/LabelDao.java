package top.woodwhale.blog.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import top.woodwhale.blog.pojo.Label;

public interface LabelDao extends JpaRepository<Label, String>, JpaSpecificationExecutor<Label> {

    Label findOneById(String id);

    Label findOneByName(String label);

    @Modifying
    @Query(nativeQuery = true, value = "update `tb_labels` set `count` = `count` + 1 where `name` = ?")
    int updateOneCount(String label);

    int deleteOneById(String labelId);

    @Modifying
    @Query(nativeQuery = true,value = "update `tb_labels` set `count` = `count` - 1 where `name` = ? and `count` != 1")
    int deleteOneCount(String label);

    int deleteOneByName(String label);
}
