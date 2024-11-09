package fr.stripekpi.start.models;


import java.math.BigDecimal;
import java.util.Map;
import lombok.Data;
import lombok.Builder;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;

@Data
@Builder
public class CreatePaymentRequest {
    @NotNull
    private BigDecimal amount;

    @NotEmpty
    private String currency;

    @NotEmpty
    private String customerId;

    private String paymentMethodId;

    private Map<String, String> metadata;
}
