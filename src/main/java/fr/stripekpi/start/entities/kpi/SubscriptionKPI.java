package fr.stripekpi.start.entities.kpi;

import jakarta.persistence.*;
 
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "subscription_kpis")
public class SubscriptionKPI {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "measurement_date")
    private LocalDateTime measurementDate;

    @Column(name = "total_subscriptions")
    private Integer totalSubscriptions;

    @Column(name = "active_subscriptions")
    private Integer activeSubscriptions;

    @Column(name = "canceled_subscriptions")
    private Integer canceledSubscriptions;

    @Column(name = "mrr")
    private BigDecimal monthlyRecurringRevenue;

    @Column(name = "arr")
    private BigDecimal annualRecurringRevenue;

    @Column(name = "churn_rate")
    private BigDecimal churnRate;

    @Column(name = "period_type")
    @Enumerated(EnumType.STRING)
    private PaymentKPI.PeriodType periodType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getMeasurementDate() {
        return measurementDate;
    }

    public void setMeasurementDate(LocalDateTime measurementDate) {
        this.measurementDate = measurementDate;
    }

    public Integer getTotalSubscriptions() {
        return totalSubscriptions;
    }

    public void setTotalSubscriptions(Integer totalSubscriptions) {
        this.totalSubscriptions = totalSubscriptions;
    }

    public Integer getActiveSubscriptions() {
        return activeSubscriptions;
    }

    public void setActiveSubscriptions(Integer activeSubscriptions) {
        this.activeSubscriptions = activeSubscriptions;
    }

    public Integer getCanceledSubscriptions() {
        return canceledSubscriptions;
    }

    public void setCanceledSubscriptions(Integer canceledSubscriptions) {
        this.canceledSubscriptions = canceledSubscriptions;
    }

    public BigDecimal getMonthlyRecurringRevenue() {
        return monthlyRecurringRevenue;
    }

    public void setMonthlyRecurringRevenue(BigDecimal monthlyRecurringRevenue) {
        this.monthlyRecurringRevenue = monthlyRecurringRevenue;
    }

    public BigDecimal getAnnualRecurringRevenue() {
        return annualRecurringRevenue;
    }

    public void setAnnualRecurringRevenue(BigDecimal annualRecurringRevenue) {
        this.annualRecurringRevenue = annualRecurringRevenue;
    }

    public BigDecimal getChurnRate() {
        return churnRate;
    }

    public void setChurnRate(BigDecimal churnRate) {
        this.churnRate = churnRate;
    }

    public PaymentKPI.PeriodType getPeriodType() {
        return periodType;
    }

    public void setPeriodType(PaymentKPI.PeriodType periodType) {
        this.periodType = periodType;
    }
    
    
}
