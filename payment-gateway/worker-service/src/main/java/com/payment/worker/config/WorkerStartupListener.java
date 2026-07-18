package com.payment.worker.config;

import com.payment.worker.service.PaymentWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class WorkerStartupListener {
    
    @Autowired
    private PaymentWorker paymentWorker;
    
    @EventListener(ApplicationReadyEvent.class)
    public void startWorkers() {
        System.out.println("Application is ready, auto-starting async workers...");
        paymentWorker.worker1ProcessPayments();
        paymentWorker.worker2ProcessPayments();
        paymentWorker.worker3ProcessPayments();
    }
}
