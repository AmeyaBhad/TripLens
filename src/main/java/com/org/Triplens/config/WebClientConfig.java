
package com.org.Triplens.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;

import io.netty.resolver.DefaultAddressResolverGroup;
import jakarta.annotation.PostConstruct;
import reactor.netty.http.client.HttpClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClient() {
        // Create a custom HttpClient that forces standard DNS resolution
        HttpClient httpClient = HttpClient.create()
                .resolver(DefaultAddressResolverGroup.INSTANCE);

        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .baseUrl("https://aerodatabox.p.rapidapi.com")
                .defaultHeader("X-RapidAPI-Key", "eb6ff38583msh003c1a8ee71f384p1979e6jsn4d9b228b47a0") 
                .defaultHeader("X-RapidAPI-Host", "aerodatabox.p.rapidapi.com")
                .build();
    }
    
//    @PostConstruct
//    public void loaded() {
//        System.out.println("🔥 WebClientConfig LOADED 🔥");
//    }
}