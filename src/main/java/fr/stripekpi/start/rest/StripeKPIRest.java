package fr.stripekpi.start.rest;



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
import java.time.temporal.ChronoUnit;

@Path("z")
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
    @Path("/customers")
    public Response getCustomerKPIs(@QueryParam("period") @DefaultValue("DAILY") PaymentKPI.PeriodType periodType) {
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = calculateStartDate(endDate, periodType);
        
        CustomerKPI kpis = kpiService.calculateCustomerKPIs(startDate, endDate, periodType);
        return Response.ok(kpis).build();
    }
    
    private LocalDateTime calculateStartDate(LocalDateTime endDate, PaymentKPI.PeriodType periodType) {
        switch (periodType) {
            case HOURLY:
                return endDate.minus(1, ChronoUnit.HOURS);
            case DAILY:
                return endDate.minus(1, ChronoUnit.DAYS);
            case WEEKLY:
                return endDate.minus(7, ChronoUnit.DAYS);
            case MONTHLY:
                return endDate.minus(1, ChronoUnit.MONTHS);
            case YEARLY:
                return endDate.minus(1, ChronoUnit.YEARS);
            default:
                return endDate.minus(1, ChronoUnit.DAYS);
        }
    }
}