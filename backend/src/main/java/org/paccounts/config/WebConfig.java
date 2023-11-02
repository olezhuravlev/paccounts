package org.paccounts.config;

import org.paccounts.component.SysInfoMethodArgumentResolver;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final AppProps appProps;
    private final SysInfoMethodArgumentResolver sysInfoMethodArgumentResolver;

    public WebConfig(AppProps appProps, SysInfoMethodArgumentResolver resolver) {
        this.appProps = appProps;
        this.sysInfoMethodArgumentResolver = resolver;
    }

    // ToDo: DISABLE ALLOWANCE FOR CROSS-ORIGIN REQUESTS FOR PRODUCTION BUILD!
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/*");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(sysInfoMethodArgumentResolver);
    }

    @Bean
    public LocaleResolver localeResolver() {
        CookieLocaleResolver resolver = new CookieLocaleResolver(appProps.localeResolverCookieName());
        resolver.setDefaultLocale(appProps.defaultLocale());
        return resolver;
    }

    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
        interceptor.setParamName(appProps.localeParameterName());
        return interceptor;
    }

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename(appProps.messagesBasename());
        messageSource.setDefaultEncoding(appProps.defaultEncoding());
        return messageSource;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
    }
}
