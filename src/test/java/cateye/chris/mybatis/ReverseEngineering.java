package cateye.chris.mybatis;

import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.exception.InvalidConfigurationException;
import org.mybatis.generator.exception.XMLParserException;
import org.mybatis.generator.internal.DefaultShellCallback;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ReverseEngineering {

    public static void main(String[] args)
            throws IOException, XMLParserException, InvalidConfigurationException, SQLException, InterruptedException {
        List<String> waring = new ArrayList<>();
        //  读取逆向工程的配置文件(对应resource包下的mybatis-reverse-engineering.xml配置类)
        ClassPathResource cpr = new ClassPathResource("mybatis-reverse-engineering.xml");
        ConfigurationParser cp = new ConfigurationParser(waring);
        Configuration config = cp.parseConfiguration(cpr.getInputStream());
        DefaultShellCallback callback = new DefaultShellCallback(true);
        MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config,callback,waring);
        myBatisGenerator.generate(null);
    }
}
