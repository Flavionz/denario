package com.denario.gateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.time.Instant;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/account-service")
    public ProblemDetail accountServiceFallback() {
        return buildFallback("account-service");
    }

    @GetMapping("/transaction-service")
    public ProblemDetail transactionServiceFallback() {
        return buildFallback("transaction-service");
    }

    @GetMapping("/notification-service")
    public ProblemDetail notificationServiceFallback() {
        return buildFallback("notification-service");
    }

    private ProblemDetail buildFallback(String service) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.SERVICE_UNAVAILABLE,
                service + " is currently unavailable. Please try again later.");
        problem.setType(URI.create("https://denario.com/errors/service-unavailable"));
        problem.setTitle("Service Unavailable");
        problem.setProperty("service", service);
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }
}
