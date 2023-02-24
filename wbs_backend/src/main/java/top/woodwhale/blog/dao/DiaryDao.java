package top.woodwhale.blog.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import top.woodwhale.blog.pojo.Diary;

public interface DiaryDao extends JpaRepository<Diary, String>, JpaSpecificationExecutor<Diary> {

    @Modifying
    int deleteOneById(String diaryId);

    Diary findOneById(String diaryId);
}
