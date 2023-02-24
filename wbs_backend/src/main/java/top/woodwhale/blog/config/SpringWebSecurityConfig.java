package top.woodwhale.blog.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * SpringSecurity安全配置类
 */
@Configuration
@EnableWebSecurity  // 开启web安全
@EnableGlobalMethodSecurity(prePostEnabled = true)  // 鉴别权限
public class SpringWebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/**").permitAll() // 放行
                .and().csrf().disable();    // 静止跨域访问
    }
}