package plus.extvos.builtin.upload.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * @author Mingcai SHEN
 */
@EntityScan("plus.extvos.builtin.upload.entity")
@ComponentScan(basePackages = "plus.extvos.builtin.upload")
public class BuiltinAutoConfigure {
    @Bean
    @ConditionalOnProperty(prefix = "spring.swagger", name = "enabled", havingValue = "true")
    public Docket createUploadDocket() {
        return new Docket(DocumentationType.SWAGGER_2)
            .groupName("文件上传服务")
            .apiInfo(new ApiInfoBuilder()
                .title("文件上传服务")
                .description("Builtin Upload services for generic use.")
                .contact(new Contact("Mingcai SHEN", "https://github.com/", "archsh@gmail.com"))
                .termsOfServiceUrl("https://github.com/extvos/quick-builtin-upload.git")
                .version(getClass().getPackage().getImplementationVersion())
                .build())
            .select()
            .apis(RequestHandlerSelectors.basePackage("plus.extvos.builtin.upload"))
            .build();
    }
}
