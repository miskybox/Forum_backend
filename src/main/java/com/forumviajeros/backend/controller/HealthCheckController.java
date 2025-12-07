package com.forumviajeros.backend.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.forumviajeros.backend.repository.CountryRepository;
import com.forumviajeros.backend.repository.TriviaQuestionRepository;

import lombok.RequiredArgsConstructor;

/**
 * Controller for health check and data verification
 */
@RestController
@RequestMapping("/api/health")
@RequiredArgsConstructor
public class HealthCheckController {

    private final CountryRepository countryRepository;
    private final TriviaQuestionRepository triviaQuestionRepository;

    @GetMapping("/data-status")
    public ResponseEntity<Map<String, Object>> checkDataStatus() {
        Map<String, Object> status = new HashMap<>();

        try {
            long countryCount = countryRepository.count();
            long triviaCount = triviaQuestionRepository.count();

            status.put("countries_loaded", countryCount);
            status.put("trivia_questions_loaded", triviaCount);
            status.put("countries_expected", 30);
            status.put("trivia_expected", 120);
            status.put("countries_ok", countryCount >= 30);
            status.put("trivia_ok", triviaCount >= 120);
            status.put("status", (countryCount >= 30 && triviaCount >= 120) ? "OK" : "INCOMPLETE");

            return ResponseEntity.ok(status);
        } catch (Exception e) {
            status.put("status", "ERROR");
            status.put("error", e.getMessage());
            return ResponseEntity.status(500).body(status);
        }
    }
}
