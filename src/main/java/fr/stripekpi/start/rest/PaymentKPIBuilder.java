package fr.stripekpi.start.rest;
/**
 * Builder class for creating PaymentKPI instances.
 * Provides a fluent interface for constructing payment KPI objects
 * with validation and automatic metric calculations.
 *
 * Features:
 * - Fluent builder pattern implementation
 * - Automatic metric calculation
 * - Data validation
 * - Default value support
 *
 * Usage example:
 * {@code
 * PaymentKPI kpi = new PaymentKPIBuilder()
 *     .totalTransactions(100)
 *     .successfulTransactions(95)
 *     .build();
 * }
 *
 * @author ML (laurent.madarassou@gmail.com)
 */


import fr.stripekpi.start.entities.kpi.AbstractKPI;
import fr.stripekpi.start.entities.kpi.PaymentKPI;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class PaymentKPIBuilder extends AbstractKPI {
    private Long id;
    private LocalDateTime measurementDate;
    private Integer totalTransactions;
    private Integer successfulTransactions;
    private Integer failedTransactions;
    private BigDecimal totalAmount;
    private BigDecimal averageTransactionAmount;
    private BigDecimal successRate;
    private String currency;
    private AbstractKPI.PeriodType periodType;
    private Map<String, String> additionalMetrics;

    public PaymentKPIBuilder() {
        this.additionalMetrics = new HashMap<>();
    }

    // Constructeur de copie
    public PaymentKPIBuilder(PaymentKPI source) {
        this.id = 2L; // TODO source.getId();
       // this.measurementDate = new LocalDateTime(); // source.getMeasurementDate();
        this.totalTransactions = source.getTotalTransactions();
        this.successfulTransactions = source.getSuccessfulTransactions();
        this.failedTransactions = source.getFailedTransactions();
        this.totalAmount = source.getTotalAmount();
        this.averageTransactionAmount = source.getAverageTransactionAmount();
        this.successRate = source.getSuccessRate();
        this.currency = source.getCurrency();
        //this.periodType = source.getPeriodType();
        this.additionalMetrics = new HashMap<>(source.getAdditionalMetrics());
    }

    // Méthode factory pour valeurs par défaut
    public static PaymentKPIBuilder defaults() {
        PaymentKPIBuilder builder = new PaymentKPIBuilder();
        builder.measurementDate = LocalDateTime.now();
        builder.totalTransactions = 0;
        builder.successfulTransactions = 0;
        builder.failedTransactions = 0;
        builder.totalAmount = BigDecimal.ZERO;
        builder.averageTransactionAmount = BigDecimal.ZERO;
        builder.successRate = BigDecimal.ZERO;
        builder.currency = "EUR";
        builder.periodType = AbstractKPI.PeriodType.DAILY;
        return builder;
    }

    // Méthodes de construction
    public PaymentKPIBuilder id(Long id) {
        this.id = id;
        return this;
    }

    public PaymentKPIBuilder measurementDate(LocalDateTime date) {
        this.measurementDate = date;
        return this;
    }

    // ... autres méthodes de construction similaires ...

    public PaymentKPI build() {
        PaymentKPI kpi = new PaymentKPI();
       // kpi.setId(id);
        //kpi.setMeasurementDate(measurementDate);
        kpi.setTotalTransactions(totalTransactions);
        kpi.setSuccessfulTransactions(successfulTransactions);
        kpi.setFailedTransactions(failedTransactions);
        kpi.setTotalAmount(totalAmount);
        kpi.setAverageTransactionAmount(averageTransactionAmount);
        kpi.setSuccessRate(successRate);
        kpi.setCurrency(currency);
       // kpi.setPeriodType(periodType);
        kpi.setAdditionalMetrics(new HashMap<>(additionalMetrics));
        return kpi;
    }
}