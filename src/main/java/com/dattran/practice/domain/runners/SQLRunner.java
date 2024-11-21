package com.dattran.practice.domain.runners;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;

@Component
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SQLRunner implements CommandLineRunner {
    JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        // Run only once
//        executeSQLFile("./sql/function.sql");
        executeSQLFile("./sql/procedure.sql");
    }

    private void executeSQLFile(String filePath) throws IOException {
        ClassPathResource resource = new ClassPathResource(filePath);
        if (resource.exists()) {
            String sql = new String(Files.readAllBytes(resource.getFile().toPath()));
            jdbcTemplate.execute(sql);
            log.info("Executed SQL file: {}", filePath);
        } else {
            log.error("File not found: {}", filePath);
        }
    }
}
