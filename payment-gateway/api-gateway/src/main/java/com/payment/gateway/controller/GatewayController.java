package com.payment.gateway.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

@RestController
@CrossOrigin(origins = "*")
public class GatewayController {

    @Autowired
    private RestTemplate restTemplate;

    @RequestMapping(value = {
        "/api/users/**",
        "/api/wallets/**",
        "/api/payments/**",
        "/api/transactions/**",
        "/api/notifications/**",
        "/api/workers/**"
    })
    public ResponseEntity<byte[]> proxy(
            @RequestBody(required = false) byte[] body,
            HttpMethod method,
            HttpServletRequest request) throws URISyntaxException {
        
        String path = request.getRequestURI();
        String query = request.getQueryString();
        
        String targetServiceUrl;
        if (path.startsWith("/api/users") || path.startsWith("/api/wallets")) {
            targetServiceUrl = "http://localhost:8081";
        } else if (path.startsWith("/api/payments")) {
            targetServiceUrl = "http://localhost:8082";
        } else if (path.startsWith("/api/transactions")) {
            targetServiceUrl = "http://localhost:8083";
        } else if (path.startsWith("/api/notifications")) {
            targetServiceUrl = "http://localhost:8084";
        } else if (path.startsWith("/api/workers")) {
            targetServiceUrl = "http://localhost:8085";
        } else {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("Route not found in Gateway".getBytes());
        }

        String targetUrl = targetServiceUrl + path;
        if (query != null) {
            targetUrl += "?" + query;
        }

        HttpHeaders headers = new HttpHeaders();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            if (headerName.equalsIgnoreCase("host")) {
                continue;
            }
            headers.put(headerName, Collections.list(request.getHeaders(headerName)));
        }

        HttpEntity<byte[]> httpEntity = new HttpEntity<>(body, headers);
        try {
            return restTemplate.exchange(new URI(targetUrl), method, httpEntity, byte[].class);
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .headers(e.getResponseHeaders())
                    .body(e.getResponseBodyAsByteArray());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Gateway Error: " + e.getMessage()).getBytes());
        }
    }
}
