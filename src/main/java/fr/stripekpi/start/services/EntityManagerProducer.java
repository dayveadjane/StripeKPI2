package fr.stripekpi.start.services;

import com.stripe.Stripe;
import fr.stripekpi.start.qualifiers.Database;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Initialized;

import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.Disposes;
import jakarta.enterprise.inject.Produces;

import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class EntityManagerProducer {
    
    private static final Logger logger = LoggerFactory.getLogger(EntityManagerProducer.class);
    
    private EntityManagerFactory emf;
    private EntityManager em;
    
    private static final String STRIPE_SECRET_KEY = "sk_test_51Jew34KlZcGG4Dc3pug5BRnlrjQ8C8D55sYvpQs5dQbvLg7awLbmiC239BrOBmNxPEmjEEIKKBavBodbOaAL7uJa00AVNp1PxS";
    private static final String WEBHOOK_SECRET = "whsec_e216cc140dce72541a5dcbd4ed89bbec6b5918c37f995715e1cae0b027db3be5";
    
    public void init(@Observes @Initialized(ApplicationScoped.class) Object init) {
       Stripe.apiKey = STRIPE_SECRET_KEY;
        logger.info("EntityManagerProducer-->init()");
   
        
        
        
        
        
        
        try {
            emf = Persistence.createEntityManagerFactory("jpa_kreoLocal");
            em = emf.createEntityManager();
            logger.info("EntityManagerFactory and EntityManager initialized successfully");
        } catch (Exception e) {
            logger.error("Error initializing EntityManagerFactory", e);
            throw new RuntimeException("Failed to initialize EntityManagerFactory", e);
        }
    }
    
    @Produces
    @Named("stripeSecretKey")
    public String getStripeSecretKey() {
        return STRIPE_SECRET_KEY;
    }
    
    @Produces
    @Named("stripeWebhookSecret")
    public String getWebhookSecret() {
        return WEBHOOK_SECRET;
    }
    
    @Produces
    @Database
    @ApplicationScoped
    public EntityManager createEntityManager() {
        logger.info("Providing EntityManager");
        return em;
    }
    
    public void closeEntityManager(@Disposes @Database EntityManager em) {
        if (em != null && em.isOpen()) {
            logger.info("Closing EntityManager");
            try {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                em.close();
            } catch (Exception e) {
                logger.error("Error during EntityManager disposal", e);
            }
        }
    }
    
    public void destroy() {
        if (em != null && em.isOpen()) {
            em.close();
        }
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}