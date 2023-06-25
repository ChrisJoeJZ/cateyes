package cateye.chris.utils;

import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.teaopenapi.models.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AliSMSClient {

    /**
     * 使用AK&SK初始化账号Client
     * @return Client
     * @throws Exception
     */
    @Bean
    public Client createClient() throws Exception {
        Config config = new Config()
                // 您的 AccessKey ID
                .setAccessKeyId("LTAI5tH69NEs9dhxmJUZmZSv")
                // 您的 AccessKey Secret
                .setAccessKeySecret("NvNXteVR2WWs9kz3wgBXV8f8cxFotA");
        // 访问的域名
        config.endpoint = "dysmsapi.aliyuncs.com";
        Client client = new Client(config);
        return client;
    }
}
