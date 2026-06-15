package com.payment.paymentgateway.worker.controller;

import com.payment.paymentgateway.worker.service.PaymentWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/workers")
@CrossOrigin(origins = "*")
public class WorkerController {
    
    @Autowired
    private PaymentWorker paymentWorker;
    
    @PostMapping("/start/worker1")
    public ResponseEntity<Map<String, String>> startWorker1() {
        paymentWorker.worker1ProcessPayments();
        Map<String, String> response = new HashMap<>();
        response.put("message", "Worker 1 started");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    @PostMapping("/start/worker2")
    public ResponseEntity<Map<String, String>> startWorker2() {
        paymentWorker.worker2ProcessPayments();
        Map<String, String> response = new HashMap<>();
        response.put("message", "Worker 2 started");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    @PostMapping("/start/worker3")
    public ResponseEntity<Map<String, String>> startWorker3() {
        paymentWorker.worker3ProcessPayments();
        Map<String, String> response = new HashMap<>();
        response.put("message", "Worker 3 started");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
