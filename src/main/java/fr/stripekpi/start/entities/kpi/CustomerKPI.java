package fr.stripekpi.start.entities.kpi;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "customer_kpis")
@Data
//@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class CustomerKPI extends AbstractKPI {

    @Column(name = "total_customers", nullable = false)
    private Integer totalCustomers;

    @Column(name = "new_customers", nullable = false)
    private Integer newCustomers;

    @Column(name = "active_customers", nullable = false)
    private Integer activeCustomers;

    @Column(name = "churned_customers", nullable = false)
    private Integer churnedCustomers;

    @Column(name = "retention_rate", precision = 5, scale = 2)
    private BigDecimal retentionRate;

    @Column(name = "churn_rate", precision = 5, scale = 2)
    private BigDecimal churnRate;

    @Column(name = "average_customer_value", precision = 10, scale = 2)
    private BigDecimal averageCustomerValue;


    public void calculateMetrics() {
        if (totalCustomers > 0) {
            // Calculer le taux de rétention
            this.retentionRate = new BigDecimal(activeCustomers)
                    .divide(new BigDecimal(totalCustomers), 4, BigDecimal.ROUND_HALF_UP)
                    .multiply(new BigDecimal("100"));

            // Calculer le taux de désabonnement
            this.churnRate = new BigDecimal(churnedCustomers)
                    .divide(new BigDecimal(totalCustomers), 4, BigDecimal.ROUND_HALF_UP)
                    .multiply(new BigDecimal("100"));
        }
    }

    // s@Override
    public void validate() {
        if (totalCustomers < 0) {
            throw new IllegalStateException("Total customers cannot be negative");
        }
        if (newCustomers < 0) {
            throw new IllegalStateException("New customers cannot be negative");
        }
        if (activeCustomers < 0) {
            throw new IllegalStateException("Active customers cannot be negative");
        }
        if (churnedCustomers < 0) {
            throw new IllegalStateException("Churned customers cannot be negative");
        }
        if (activeCustomers + churnedCustomers > totalCustomers) {
            throw new IllegalStateException("Sum of active and churned customers cannot exceed total customers");
        }
    }

    public Integer getTotalCustomers() {
        return totalCustomers;
    }

    public void setTotalCustomers(Integer totalCustomers) {
        this.totalCustomers = totalCustomers;
    }

    public Integer getNewCustomers() {
        return newCustomers;
    }

    public void setNewCustomers(Integer newCustomers) {
        this.newCustomers = newCustomers;
    }

    public Integer getActiveCustomers() {
        return activeCustomers;
    }

    public void setActiveCustomers(Integer activeCustomers) {
        this.activeCustomers = activeCustomers;
    }

    public Integer getChurnedCustomers() {
        return churnedCustomers;
    }

    public void setChurnedCustomers(Integer churnedCustomers) {
        this.churnedCustomers = churnedCustomers;
    }

    public BigDecimal getRetentionRate() {
        return retentionRate;
    }

    public void setRetentionRate(BigDecimal retentionRate) {
        this.retentionRate = retentionRate;
    }

    public BigDecimal getChurnRate() {
        return churnRate;
    }

    public void setChurnRate(BigDecimal churnRate) {
        this.churnRate = churnRate;
    }

    public BigDecimal getAverageCustomerValue() {
        return averageCustomerValue;
    }

    public void setAverageCustomerValue(BigDecimal averageCustomerValue) {
        this.averageCustomerValue = averageCustomerValue;
    }



    public PeriodType getPeriodType() {
        return periodType;
    }

    public void setPeriodType(PeriodType periodType) {
        this.periodType = periodType;
    }

}
