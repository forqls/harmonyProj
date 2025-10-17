package kh.GiveHub.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//@Configuration
//public class WebMvcConfig implements WebMvcConfigurer{

	//로컬경로
//	@Value("${upload.path}")
//	private String uploadPath;
//
//	public static String getBasePath() {
//		String os = System.getProperty("os.name").toLowerCase();
//		if (os.contains("win")) {
//			return "c:/GiveHub";
//		}else {
//			return "/Users/" + System.getProperty("user.name") + "/GiveHub";
//		}
//	}
//	@Override
//	public void addResourceHandlers(ResourceHandlerRegistry registry) {
//		String basePath = getBasePath();
//
//		registry.addResourceHandler("/temp/**")
//				.addResourceLocations("file:///"+basePath+"/temp/");
//		registry.addResourceHandler("/upload/**")
//				.addResourceLocations("file:///"+basePath+"/upload/");
//		registry.addResourceHandler("/**")
//				.addResourceLocations("classpath:/static/");
//		registry.addResourceHandler("/upload/**")
//				.addResourceLocations("file:///" + uploadPath);
//
//	}
//}
