package com.payment.notification.dto;

public class NotificationRequest {
    private String paymentId;
    private String userId;
    private String type;
    private String message;

    public NotificationRequest() {}

    public NotificationRequest(String paymentId, String userId, String type, String message) {
        this.paymentId = paymentId;
        this.userId = userId;
        this.type = type;
        this.message = message;
    }

    public String getPaymentId() { return paymentId; }
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
