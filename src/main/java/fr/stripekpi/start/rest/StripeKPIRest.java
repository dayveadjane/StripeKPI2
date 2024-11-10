package fr.stripekpi.start.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.CustomerCollection;
import com.stripe.model.CustomerSearchResult;
import com.stripe.param.CustomerListParams;
import com.stripe.param.CustomerSearchParams;
import fr.stripekpi.start.entities.kpi.CustomerKPI;
import fr.stripekpi.start.entities.kpi.PaymentKPI;
import fr.stripekpi.start.services.StripeKPIService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import jakarta.inject.Inject;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Path("2")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class StripeKPIRest {

    @Inject
    private StripeKPIService kpiService;

    @GET
    @Path("/payments")
    public Response getPaymentKPIs(@QueryParam("period") @DefaultValue("DAILY") PaymentKPI.PeriodType periodType) {
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = calculateStartDate(endDate, periodType);

        PaymentKPI kpis = kpiService.calculatePaymentKPIs(startDate, endDate, periodType);
        return Response.ok(kpis).build();
    }

    @GET
    @Path("/customerskpi")
    public Response getCustomerKPIs(@QueryParam("period") @DefaultValue("DAILY") PaymentKPI.PeriodType periodType) {
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = calculateStartDate(endDate, periodType);

        CustomerKPI kpis = kpiService.calculateCustomerKPIs(startDate, endDate, periodType);
        return Response.ok(kpis).build();
    }

    @GET
    @Path("/customers")
    public Response getCustomers(@QueryParam("period") @DefaultValue("YEARLY") PaymentKPI.PeriodType periodType) {

        CustomerListParams params = CustomerListParams.builder().setLimit(100L).build();

        CustomerCollection customerCollection = null;
        try {
            customerCollection = Customer.list(params);
        } catch (StripeException ex) {
            Logger.getLogger(StripeKPIRest.class.getName()).log(Level.SEVERE, null, ex);
        }

        CustomerSearchParams params2 = CustomerSearchParams.builder().setQuery("name:'Jane Doe' AND metadata['foo']:'bar'").build();

        try {
            CustomerSearchResult customers2 = Customer.search(params2);

            Logger.getLogger(StripeKPIRest.class.getName()).log(Level.INFO, null, customers2.toJson());
        } catch (StripeException ex) {
            Logger.getLogger(StripeKPIRest.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Renvoyer la réponse en JSON
        return Response.ok(customerCollection.toJson())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    private String serializeCustomers(String customers) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(customers);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializing customers", e);
        }
    }

    private CustomerListParams getCustomerListParams(PaymentKPI.PeriodType periodType) {
        //LocalDateTime endDate = LocalDateTime.now();
        //LocalDateTime startDate = calculateStartDate(endDate, periodType);

        return CustomerListParams.builder()
                .setCreated(
                        CustomerListParams.Created.builder()
                                //.setGte(startDate.toEpochSecond(ZoneOffset.UTC))
                                //.setLte(endDate.toEpochSecond(ZoneOffset.UTC))
                                .build()
                )
                .setLimit(316L)
                .build();
    }

    private String serializeCustomerListParams(CustomerListParams params) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(params);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializing CustomerListParams", e);
        }
    }

    private LocalDateTime calculateStartDate(LocalDateTime endDate, PaymentKPI.PeriodType periodType) {
        // Implémentation de la logique de calcul de la date de début en fonction de la période
        switch (periodType) {
            case DAILY:
                return endDate.minusDays(1);
            case WEEKLY:
                return endDate.minusWeeks(1);
            case MONTHLY:
                return endDate.minusMonths(1);
            case YEARLY:
                return endDate.minusYears(1);
            default:
                return endDate.minusDays(1);
        }
    }
}
