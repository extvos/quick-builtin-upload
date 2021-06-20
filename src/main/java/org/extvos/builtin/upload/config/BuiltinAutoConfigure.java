package org.extvos.builtin.upload.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * @author shenmc
 */
@EntityScan("org.extvos.builtin.upload.entity")
@MapperScan("org.extvos.builtin.upload.mapper")
@ComponentScan(basePackages = "org.extvos.builtin.upload")
public class BuiltinAutoConfigure {
    @Bean
    public Docket createUploadDocket() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("文件上传服务")
                .apiInfo(new ApiInfoBuilder()
                        .title("文件上传服务")
                        .description("Builtin Upload services for generic use.")
                        .contact(new Contact("Mingcai SHEN","https://github.com/","archsh@gmail.com"))
                        .termsOfServiceUrl("https://github.com/quickstart/java-scaffolds/quick-builtin-upload.git")
                        .version(getClass().getPackage().getImplementationVersion())
                        .build())
                .select()
                .apis(RequestHandlerSelectors.basePackage("org.extvos.builtin.upload"))
                .build();
    }
}
