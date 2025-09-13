package com.example.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

  @Value("${storage.local-dir:/data/uploads}")
  private String localDir;

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    String location = ensureFileUrl(localDir);
    registry
        .addResourceHandler("/media/**")
        .addResourceLocations(location)
        .setCachePeriod(31536000); // ~1y
  }

  private static String ensureFileUrl(String path) {
    String p = path;
    if (!p.endsWith("/")) p = p + "/";
    if (!p.startsWith("file:")) p = "file:" + p;
    return p;
  }
}
