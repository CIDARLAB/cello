package org.cellocad;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.multipart.support.MultipartFilter;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

//    @Bean
//    public CommonsMultipartResolver commonsMultipartResolver() {
//        final CommonsMultipartResolver commonsMultipartResolver = new CommonsMultipartResolver();
//        commonsMultipartResolver.setMaxUploadSize(-1);
//        return commonsMultipartResolver;
//    }
//
//    @Bean
//    public FilterRegistrationBean multipartFilterRegistrationBean() {
//        final MultipartFilter multipartFilter = new MultipartFilter();
//        final FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean(multipartFilter);
//        filterRegistrationBean.addInitParameter("multipartResolverBeanName", "commonsMultipartResolver");
//        return filterRegistrationBean;
//    }
}
