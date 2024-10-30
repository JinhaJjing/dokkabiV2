package com.goya.dokkabiv2.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SampleController {
    private static final Logger logger = LoggerFactory.getLogger(SampleController.class);

    @GetMapping("/test")
    public String test() {
        logger.error("Test Exception occurred");
        throw new RuntimeException("Test Exception");
    }

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/test-schema")
    public String testSchema() {
        String query = "SELECT name FROM test_table WHERE id = 1";
        return jdbcTemplate.queryForObject(query, String.class);
    }
}
