package com.archy.dezhou.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "app")
public class AppConfig {
    
    private List<ServerConfig> servers;
    
    public static class ServerConfig {
        private String host;
        private int port;
        private int threadCnt;
        
        // Getters and Setters
        public String getHost() {
            return host;
        }
        
        public void setHost(String host) {
            this.host = host;
        }
        
        public int getPort() {
            return port;
        }
        
        public void setPort(int port) {
            this.port = port;
        }
        
        public int getThreadCnt() {
            return threadCnt;
        }
        
        public void setThreadCnt(int threadCnt) {
            this.threadCnt = threadCnt;
        }
    }
    
    public List<ServerConfig> getServers() {
        return servers;
    }
    
    public void setServers(List<ServerConfig> servers) {
        this.servers = servers;
    }
}