package com.archy.dezhou.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis Configuration
 * Ensures all MyBatis mappers are properly scanned and registered
 */
@Configuration
@MapperScan({
    "com.archy.dezhou.dao",
    "com.archy.dezhou.dao.RoomDBMapper" 
})
public class MyBatisConfig {
    
    // Additional MyBatis configuration can be added here
    // For example, custom type handlers, interceptors, etc.
    
    /*
    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        factoryBean.setDataSource(dataSource);
        factoryBean.setMapperLocations(new PathMatchingResourcePatternResolver()
            .getResources("classpath:com/archy/dezhou/dao/*.xml"));
        return factoryBean.getObject();
    }
    */
}