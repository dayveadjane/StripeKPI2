package fr.stripekpi.start.services;

import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.param.CustomerListParams;
import com.stripe.param.PaymentIntentListParams;
import fr.stripekpi.start.entities.kpi.CustomerKPI;
import fr.stripekpi.start.entities.kpi.PaymentKPI;
import fr.stripekpi.start.qualifiers.Database;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

@ApplicationScoped
public class StripeKPIService {

    private static final Logger logger = LoggerFactory.getLogger(StripeKPIService.class);

    @Inject
    @Database
    private EntityManager em;

    public PaymentKPI calculatePaymentKPIs(LocalDateTime startDate, LocalDateTime endDate, PaymentKPI.PeriodType periodType) {
        try {
            em.getTransaction().begin();

            PaymentIntentListParams params = PaymentIntentListParams.builder()
                    .setCreated(
                            PaymentIntentListParams.Created.builder()
                                    .setGte(startDate.toEpochSecond(ZoneOffset.UTC))
                                    .setLte(endDate.toEpochSecond(ZoneOffset.UTC))
                                    .build()
                    )
                    .setLimit(100L)
                    .build();

            List<PaymentIntent> paymentIntents = null; // getAllPaymentIntents(params);

            PaymentKPI kpi = new PaymentKPI();
            kpi.setMeasurementDate(endDate);
            kpi.setPeriodType(periodType);

            int total = paymentIntents.size();
            int successful = 0;
            BigDecimal totalAmount = BigDecimal.ZERO;

            for (PaymentIntent pi : paymentIntents) {
                if ("succeeded".equals(pi.getStatus())) {
                    successful++;
                    //totalAmount = totalAmount.add(new BigDecimal(pi.getAmount()).divide(new BigDecimal(100)));
                }
            }

            kpi.setTotalTransactions(total);
            kpi.setSuccessfulTransactions(successful);
            kpi.setFailedTransactions(total - successful);
            kpi.setTotalAmount(totalAmount);

            if (total > 0) {
                kpi.setAverageTransactionAmount(totalAmount.divide(new BigDecimal(total), 2, RoundingMode.HALF_UP));
                kpi.setSuccessRate(new BigDecimal(successful).multiply(new BigDecimal(100))
                        .divide(new BigDecimal(total), 2, RoundingMode.HALF_UP));
            }

            em.persist(kpi);
            em.getTransaction().commit();

            return kpi;

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            logger.error("Error calculating payment KPIs", e);
            throw new RuntimeException("Failed to calculate payment KPIs", e);
        }
    }

    public CustomerKPI calculateCustomerKPIs(LocalDateTime startDate, LocalDateTime endDate, PaymentKPI.PeriodType periodType) {
        try {
            em.getTransaction().begin();

            CustomerListParams params = CustomerListParams.builder()
                    .setCreated(
                            CustomerListParams.Created.builder()
                                    .setGte(startDate.toEpochSecond(ZoneOffset.UTC))
                                    .setLte(endDate.toEpochSecond(ZoneOffset.UTC))
                                    .build()
                    )
                    .setLimit(100L)
                    .build();

            List<Customer> customers = getAllCustomers(params);

            CustomerKPI kpi = new CustomerKPI();
            kpi.setMeasurementDate(endDate);
            kpi.setPeriodType(periodType);
            kpi.setTotalCustomers(customers.size());
            kpi.setNewCustomers(countNewCustomers(customers, startDate));

            em.persist(kpi);
            em.getTransaction().commit();

            return kpi;

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            logger.error("Error calculating customer KPIs", e);
            throw new RuntimeException("Failed to calculate customer KPIs", e);
        }
    }

    private List<Customer> getAllCustomers(CustomerListParams initialParams) throws StripeException {
        List<Customer> allCustomers = new ArrayList<>();
        String lastId = null;

        do {
            Map<String, Object> params = new HashMap<>();
            if (lastId != null) {
                params.put("starting_after", lastId);
                params.put("limit", 100);
            } else {
                params = initialParams.toMap();
            }

            CustomerCollection collection = Customer.list(params);
            List<Customer> data = collection.getData();
            allCustomers.addAll(data);

            if (data.isEmpty() || !collection.getHasMore()) {
                break;
            }

            lastId = data.get(data.size() - 1).getId();
        } while (true);

        return allCustomers;
    }

    private int countNewCustomers(List<Customer> customers, LocalDateTime startDate) {
        long startTimestamp = startDate.toEpochSecond(ZoneOffset.UTC);
        return (int) customers.stream()
                .filter(c -> c.getCreated() >= startTimestamp)
                .count();
    }
}
