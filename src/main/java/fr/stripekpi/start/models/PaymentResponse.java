package fr.stripekpi.start.models;


import lombok.Data;
import lombok.Builder;
import java.math.BigDecimal;

@Data
@Builder
public class PaymentResponse {
    private String paymentIntentId;
    private String clientSecret;
    private String status;
    private BigDecimal amount;
    private String currency;
}