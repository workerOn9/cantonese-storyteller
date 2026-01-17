package com.storyteller;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * 粤语评书应用程序主类
 * Spring Boot应用程序的入口点
 * 
 * 功能：
 * - 启动Spring Boot应用程序
 * - 配置REST客户端
 * - 配置WebClient用于HTTP请求
 */
@SpringBootApplication
public class StorytellerApplication {

    /**
     * 主函数 - 应用程序入口点
     * 启动粤语评书Spring Boot应用程序
     * 
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication.run(StorytellerApplication.class, args);
    }

    /**
     * 配置RestTemplate Bean
     * 用于同步HTTP请求，调用外部TTS服务API
     * 
     * @return RestTemplate实例
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    /**
     * 配置WebClient.Builder Bean
     * 用于异步HTTP请求，支持响应式编程
     * 
     * @return WebClient.Builder实例
     */
    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }
}