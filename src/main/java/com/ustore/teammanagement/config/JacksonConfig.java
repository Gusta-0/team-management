package com.ustore.teammanagement.config;

import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.time.ZoneId;
import java.util.TimeZone;

@Configuration
public class JacksonConfig {

    private static final ZoneId ZONE_SP = ZoneId.of("America/Sao_Paulo");

    @Bean
    public Jackson2ObjectMapperBuilder jacksonBuilder() {
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
        builder.simpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        builder.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        builder.timeZone(TimeZone.getTimeZone("America/Sao_Paulo"));
        return builder;
    }
}
