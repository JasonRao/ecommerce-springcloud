package com.ir6.ecommerce.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * <h1>Swagger 配置类</h1>
 * 原生: /swagger-ui.html
 * 美化: /doc.html
 * */
@Configuration
@EnableSwagger2
public class SwaggerConfig {
    @Bean
    public Docket docket() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())// 展示在 Swagger 页面上的自定义工程描述信息
                .select() // 选择展示哪些接口
                .apis(RequestHandlerSelectors.basePackage("com.ir6.ecommerce"))
                .paths(PathSelectors.any())
                .build();
    }

    public ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("ecommerce-micro-service")
                .description("ecommerce-springcloud-service")
                .contact(new Contact("Jason007", "www.ir6.com", "jason.007@ir6.com"))
                .version("1.0")
                .build();
    }
}
