package fr.stripekpi.start.entities.kpi;

/**
 * Entity class for tracking and analyzing payment-related KPIs. Stores metrics
 * about transaction success rates, volumes, and financial data.
 *
 * @author ML (laurent.madarassou@gmail.com)
 * @version 1.0.0
 * @since 2024-11-07
 */

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "payment_kpis")
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PaymentKPI extends AbstractKPI {

    @Column(name = "total_transactions", nullable = false)
    private Integer totalTransactions;

    @Column(name = "successful_transactions", nullable = false)
    private Integer successfulTransactions;

    @Column(name = "failed_transactions", nullable = false)
    private Integer failedTransactions;

    @Column(name = "total_amount", nullable = false, precision = 20, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "average_transaction_amount", precision = 20, scale = 2)
    private BigDecimal averageTransactionAmount;

    @Column(name = "success_rate", precision = 5, scale = 2)
    private BigDecimal successRate;

    @Column(name = "currency", length = 3)
    private String currency;


            
            
    @ElementCollection
    @CollectionTable(name = "payment_kpi_metrics",
            joinColumns = @JoinColumn(name = "payment_kpi_id"))
    @MapKeyColumn(name = "metric_name")
    @Column(name = "metric_value")
    private Map<String, String> additionalMetrics = new HashMap<>();

    public Integer getTotalTransactions() {
        return totalTransactions;
    }

    public void setTotalTransactions(Integer totalTransactions) {
        this.totalTransactions = totalTransactions;
    }

    public Integer getSuccessfulTransactions() {
        return successfulTransactions;
    }

    public void setSuccessfulTransactions(Integer successfulTransactions) {
        this.successfulTransactions = successfulTransactions;
    }

    public Integer getFailedTransactions() {
        return failedTransactions;
    }

    public void setFailedTransactions(Integer failedTransactions) {
        this.failedTransactions = failedTransactions;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getAverageTransactionAmount() {
        return averageTransactionAmount;
    }

    public void setAverageTransactionAmount(BigDecimal averageTransactionAmount) {
        this.averageTransactionAmount = averageTransactionAmount;
    }

    public BigDecimal getSuccessRate() {
        return successRate;
    }

    public void setSuccessRate(BigDecimal successRate) {
        this.successRate = successRate;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Map<String, String> getAdditionalMetrics() {
        return additionalMetrics;
    }

    public void setAdditionalMetrics(Map<String, String> additionalMetrics) {
        this.additionalMetrics = additionalMetrics;
    }





}
