package me.whiteship.demoinflearnrestapi.common;

import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;

import org.springframework.boot.test.autoconfigure.restdocs.RestDocsMockMvcConfigurationCustomizer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentationConfigurer;


@TestConfiguration
public class RestDocsConfiguration {

	@Bean
	public RestDocsMockMvcConfigurationCustomizer restDocsMockMvcBuilderCustomizer() {
		return new RestDocsMockMvcConfigurationCustomizer() {
			
			@Override
			public void customize(MockMvcRestDocumentationConfigurer configurer) {
				configurer.operationPreprocessors()
						.withResponseDefaults(prettyPrint())
						.withRequestDefaults(prettyPrint());
			}
		};
	}
}
