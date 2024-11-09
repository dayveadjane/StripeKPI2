package fr.stripekpi.start.entities.kpi;

import jakarta.persistence.*;
import java.time.LocalDateTime;


@Entity
@Table(name = "customer_kpis")
public class CustomerKPI {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "measurement_date")
    private LocalDateTime measurementDate;

    @Column(name = "total_customers")
    private Integer totalCustomers;

    @Column(name = "new_customers")
    private Integer newCustomers;

    @Column(name = "active_customers")
    private Integer activeCustomers;

    @Column(name = "churned_customers")
    private Integer churnedCustomers;

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

    public PaymentKPI.PeriodType getPeriodType() {
        return periodType;
    }

    public void setPeriodType(PaymentKPI.PeriodType periodType) {
        this.periodType = periodType;
    }
    
    
}