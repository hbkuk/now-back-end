package com.now.common.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;

/**
 * 데이터베이스 관련 설정을 로드하는 설정 클래스입니다
 */
@Slf4j
@Configuration
public class DatabaseContextLoaderListener {

    /**
     * 데이터베이스 초기화를 위한 DataSourceInitializer를 생성하여 반환
     *
     * @param dataSource DataSource 객체
     * @return DataSourceInitializer 객체
     */
    @Bean
    @Profile("test")  // test 프로파일이 아닌 경우에만 dataSourceInitializer() 메서드가 실행
    public DataSourceInitializer dataSourceInitializer(DataSource dataSource) {
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(new ClassPathResource("initdb.sql"));

        DataSourceInitializer initializer = new DataSourceInitializer();
        initializer.setDataSource(dataSource);
        initializer.setDatabasePopulator(populator);

        return initializer;
    }

}

