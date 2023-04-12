package com.ll.gramgram.base.initData;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Getter
    private static long likeablePersonMax;

    @Value("${custom.likeablePerson.max}")
    public void setLikeablePersonMax(long likeablePersonMax) {
        AppConfig.likeablePersonMax = likeablePersonMax;
    }
}
