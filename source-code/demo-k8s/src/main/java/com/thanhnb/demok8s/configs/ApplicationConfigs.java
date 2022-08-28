package com.thanhnb.demok8s.configs;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class ApplicationConfigs {

    @Value("${spring.profiles.active}")
    private String env;

    @Value("${spring.datasource.url}")
    private String dataSourceUrl;

    @Value("${spring.datasource.username}")
    private String userName;

    @Value("${spring.datasource.password}")
    private String password;

    @Override
    public String toString() {
        return "ApplicationConfigs{" +
                "env='" + env + '\'' +
                ", dataSourceUrl='" + dataSourceUrl + '\'' +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
