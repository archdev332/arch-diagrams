package com.cdc.demo;

import static org.springframework.boot.Banner.Mode.OFF;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class CdcDemoApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(CdcDemoApplication.class).bannerMode(OFF).run(args);
    }
}
