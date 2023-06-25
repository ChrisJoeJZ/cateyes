package cateye.chris.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.StatViewServlet;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class ConnConfig {
    @Bean
    @ConfigurationProperties("spring.datasource")
    public DataSource dataSource() {
        DruidDataSource druidDataSource = new DruidDataSource();
        try {
            druidDataSource.setFilters("stat");//加入Druid监控功能
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return druidDataSource;
    }
    /**
     * 配置Druid的监控页：    http://localhost:8010/druid/
     * @return
     */
    @Bean
    public ServletRegistrationBean statViewServlet() {
        StatViewServlet statViewServlet = new StatViewServlet();
        ServletRegistrationBean servletRegistrationBean =
                new ServletRegistrationBean<StatViewServlet>(statViewServlet, "/druid/*");
        return servletRegistrationBean;
    }
}
