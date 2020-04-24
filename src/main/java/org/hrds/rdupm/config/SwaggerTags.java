package org.hrds.rdupm.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.service.Tag;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Swagger Api 描述配置
 */
@Configuration
@EnableSwagger2
public class SwaggerTags {
    public static final String EXAMPLE = "Example";
    public static final String IMAGE = "IMAGE";
    public static final String GUIDE = "GUIDE";
    public static final String PROJECT = "PROJECT";
	public static final String IMAGE_TAG = "IMAGE_TAG";

	@Autowired
    public SwaggerTags(Docket docket) {
        docket.tags(
                new Tag(EXAMPLE, "EXAMPLE 案例"),
                new Tag(IMAGE, "harbor-镜像"),
                new Tag(GUIDE, "harbor-配置指引"),
                new Tag(PROJECT, "harbor-镜像仓库"),
                new Tag(IMAGE_TAG, "harbor-镜像TAG")
        );
    }
}
