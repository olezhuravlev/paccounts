package org.paccounts.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@PropertySource("classpath:backend.properties")
@EnableConfigurationProperties({AppProps.class})
public class ApplicationConfig implements WebMvcConfigurer {

    @Bean
    CollectionsFactory restContainerProvider() {
        return new CollectionsFactory();
    }
}
