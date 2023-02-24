package top.woodwhale.blog;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.SystemDefaultHttpClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import springfox.documentation.oas.annotations.EnableOpenApi;
import top.woodwhale.blog.utils.RedisUtils;
import top.woodwhale.blog.utils.SnowflakeIdUtils;
import top.woodwhale.blog.utils.TextUtils;

import static top.woodwhale.blog.utils.Constants.Settings.REDIS_KEY_WEB_SITE_RUN_TIME;

@EnableOpenApi  // 开启接口测试
@EnableScheduling // 开启定时任务功能
@SpringBootApplication
public class BlogApplication {

    /**
     * redisUtils
     */
    @Autowired
    public RedisUtils redisUtils;

    /**
     * solr的url
     */
    @Value("${spring.data.solr.host}")
    String solrUrl;

    /**
     * Spring应用启动main方法
     * @param args args
     */
    public static void main(String[] args) {
        SpringApplication.run(BlogApplication.class,args);
    }

    /**
     * 雪花算法ID生成的Bean类
     * @return SnowflakeIdWorker
     */
    @Bean
    public SnowflakeIdUtils createIdUtils() {
        return new SnowflakeIdUtils(0,0);
    }
    /**
     * SpringSecurity带有的BCryptPasswordEncoder密码加密类
     * @return BCryptPasswordEncoder
     */
    @Bean
    public BCryptPasswordEncoder createPasswordEncoder() {
        return  new BCryptPasswordEncoder();
    }

    /**
     * Redis数据库 增删改查工具类
     * @return RedisUtils
     */
    @Bean
    public RedisUtils createRedisUtils() {
        return new RedisUtils();
    }

    /**
     * solr的启动客户端
     * @return HttpSolrClient
     */
    @Bean
    public HttpSolrClient solrClient(){
        HttpClient httpClient = new SystemDefaultHttpClient();
        return new HttpSolrClient.Builder(solrUrl).withHttpClient(httpClient).build();
    }

    /**
     * 定时任务，让运行日期叠加
     */
    @Scheduled(cron = "59 0 23 * * ?")
    public void updateRunningTime() {
        String runTime = (String) redisUtils.get(REDIS_KEY_WEB_SITE_RUN_TIME);
        if (TextUtils.isEmpty(runTime)) {
            redisUtils.set(REDIS_KEY_WEB_SITE_RUN_TIME,"1");
        }
        redisUtils.incr(REDIS_KEY_WEB_SITE_RUN_TIME,1);
    }
}
