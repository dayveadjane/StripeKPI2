package fr.stripekpi.start.rest;
/**
 * Entity class for subscription-based Key Performance Indicators.
 * Tracks metrics related to subscription services and recurring revenue.
 *
 * Key metrics:
 * - Subscription counts (total, active, canceled)
 * - Monthly Recurring Revenue (MRR)
 * - Annual Recurring Revenue (ARR)
 * - Churn rates and retention metrics
 * - Plan distribution analysis
 *
 * Database table: subscription_kpis
 * Indices: measurement_date, period_type
 *
 * @author ML (laurent.madarassou@gmail.com)
 * @version 1.0.0
 * @since 2024-11-07
 *
 * @see AbstractKPI
 * @see SubscriptionService
 */

import fr.stripekpi.start.entities.kpi.AbstractKPI;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "subscription_kpis")
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class SubscriptionKPI extends AbstractKPI {

    @Column(name = "total_subscriptions", nullable = false)
    private Integer totalSubscriptions;

    @Column(name = "active_subscriptions", nullable = false)
    private Integer activeSubscriptions;

    @Column(name = "canceled_subscriptions", nullable = false)
    private Integer canceledSubscriptions;

    @Column(name = "mrr", precision = 20, scale = 2)
    private BigDecimal monthlyRecurringRevenue;

    @Column(name = "arr", precision = 20, scale = 2)
    private BigDecimal annualRecurringRevenue;

    @Column(name = "churn_rate", precision = 5, scale = 2)
    private BigDecimal churnRate;

    @ElementCollection
    @CollectionTable(name = "subscription_plans",
            joinColumns = @JoinColumn(name = "subscription_kpi_id"))
    @MapKeyColumn(name = "plan_name")
    @Column(name = "subscribers_count")
    private Map<String, Integer> planDistribution = new HashMap<>();

    public void calculateMetrics() {
        if (totalSubscriptions > 0) {
            // Calculer le taux de désabonnement
            this.churnRate = new BigDecimal(canceledSubscriptions)
                .divide(new BigDecimal(totalSubscriptions), 4, BigDecimal.ROUND_HALF_UP)
                .multiply(new BigDecimal("100"));
        }

        // Calculer ARR basé sur MRR
        if (monthlyRecurringRevenue != null) {
            this.annualRecurringRevenue = monthlyRecurringRevenue.multiply(new BigDecimal("12"));
        }
    }

//    @Override
    public void validate() {
        if (totalSubscriptions < 0) {
            throw new IllegalStateException("Total subscriptions cannot be negative");
        }
        if (activeSubscriptions < 0) {
            throw new IllegalStateException("Active subscriptions cannot be negative");
        }
        if (canceledSubscriptions < 0) {
            throw new IllegalStateException("Canceled subscriptions cannot be negative");
        }
        if (activeSubscriptions + canceledSubscriptions != totalSubscriptions) {
            throw new IllegalStateException("Sum of active and canceled subscriptions must equal total subscriptions");
        }
        if (monthlyRecurringRevenue != null && monthlyRecurringRevenue.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalStateException("MRR cannot be negative");
        }
    }
}