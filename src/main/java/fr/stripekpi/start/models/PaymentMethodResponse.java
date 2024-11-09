package fr.stripekpi.start.models;

import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class PaymentMethodResponse {
    private String id;
    private String type;
    private String brand;
    private String last4;
    private String expiryMonth;
    private String expiryYear;
}
