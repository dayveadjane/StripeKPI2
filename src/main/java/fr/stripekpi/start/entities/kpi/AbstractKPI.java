package fr.stripekpi.start.entities.kpi;
/**
 * Abstract base class for all Key Performance Indicators (KPIs).
 * Provides common functionality and attributes for KPI tracking and analysis.
 *
 * Features:
 * - Automatic timestamp management (creation and updates)
 * - Period type handling (HOURLY, DAILY, WEEKLY, MONTHLY, YEARLY)
 * - Base validation framework
 * 
 * Note: This class uses JPA annotations for ORM mapping
 *
 * @author ML (laurent.madarassou@gmail.com)
 * @version 1.0.0
 * @since 2024-11-07
 * 
 * @see PaymentKPI
 * @see CustomerKPI
 * @see SubscriptionKPI
 */
import jakarta.persistence.*;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@MappedSuperclass
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public abstract class AbstractKPI {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Column(name = "measurement_date", nullable = false)
    protected LocalDateTime measurementDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "period_type", nullable = false)
    protected PeriodType periodType;

    @Column(name = "created_at")
    protected LocalDateTime createdAt;

    @Column(name = "updated_at")
    protected LocalDateTime updatedAt;

    public enum PeriodType {
        HOURLY, DAILY, WEEKLY, MONTHLY, YEARLY
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

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

    public PeriodType getPeriodType() {
        return periodType;
    }

    public void setPeriodType(PeriodType periodType) {
        this.periodType = periodType;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    
}