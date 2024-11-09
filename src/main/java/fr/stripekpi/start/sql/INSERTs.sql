USE kreo;

-- Désactiver les contraintes de clés étrangères temporairement
SET FOREIGN_KEY_CHECKS = 0;

-- Vider les tables existantes
TRUNCATE TABLE payment_kpis;
TRUNCATE TABLE customer_kpis;
TRUNCATE TABLE subscription_kpis;
TRUNCATE TABLE stripe_transactions;

-- Réactiver les contraintes
SET FOREIGN_KEY_CHECKS = 1;

-- Insertion des transactions Stripe
INSERT INTO stripe_transactions (stripe_transaction_id, transaction_type, amount, currency, status, 
                               customer_id, payment_method_type, transaction_date)
SELECT 
    CONCAT('pi_', LPAD(FLOOR(RAND() * 1000000), 6, '0')),
    CASE FLOOR(RAND() * 3)
        WHEN 0 THEN 'PAYMENT'
        WHEN 1 THEN 'REFUND'
        ELSE 'DISPUTE'
    END,
    ROUND(RAND() * 1000 + 50, 2),
    CASE FLOOR(RAND() * 3)
        WHEN 0 THEN 'EUR'
        WHEN 1 THEN 'USD'
        ELSE 'GBP'
    END,
    CASE FLOOR(RAND() * 4)
        WHEN 0 THEN 'succeeded'
        WHEN 1 THEN 'failed'
        WHEN 2 THEN 'pending'
        ELSE 'canceled'
    END,
    CONCAT('cus_', LPAD(FLOOR(RAND() * 1000), 4, '0')),
    CASE FLOOR(RAND() * 3)
        WHEN 0 THEN 'card'
        WHEN 1 THEN 'sepa_debit'
        ELSE 'ideal'
    END,
    DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 30) DAY)
FROM 
    (SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5) t1,
    (SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5) t2,
    (SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4) t3
LIMIT 100;

-- Insertion des KPIs de paiement pour les 100 derniers jours
INSERT INTO payment_kpis (measurement_date, total_transactions, successful_transactions, 
                         failed_transactions, total_amount, average_transaction_amount, 
                         success_rate, currency, period_type)
SELECT 
    DATE_SUB(CURRENT_DATE(), INTERVAL numbers.seq DAY) as measurement_date,
    FLOOR(50 + RAND() * 100) as total_transactions,
    FLOOR(40 + RAND() * 50) as successful_transactions,
    FLOOR(1 + RAND() * 10) as failed_transactions,
    ROUND(5000 + RAND() * 10000, 2) as total_amount,
    ROUND(100 + RAND() * 200, 2) as average_transaction_amount,
    ROUND(75 + RAND() * 20, 2) as success_rate,
    CASE numbers.seq % 3
        WHEN 0 THEN 'EUR'
        WHEN 1 THEN 'USD'
        ELSE 'GBP'
    END as currency,
    CASE numbers.seq % 5
        WHEN 0 THEN 'HOURLY'
        WHEN 1 THEN 'DAILY'
        WHEN 2 THEN 'WEEKLY'
        WHEN 3 THEN 'MONTHLY'
        ELSE 'YEARLY'
    END as period_type
FROM (
    SELECT a.N + b.N * 10 + c.N * 100 as seq
    FROM (SELECT 0 as N UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) a,
         (SELECT 0 as N UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) b,
         (SELECT 0 as N) c
) numbers
WHERE numbers.seq < 100;

-- Insertion des KPIs client
INSERT INTO customer_kpis (measurement_date, total_customers, new_customers, 
                          active_customers, churned_customers, retention_rate, period_type)
SELECT 
    DATE_SUB(CURRENT_DATE(), INTERVAL numbers.seq DAY) as measurement_date,
    FLOOR(1000 + RAND() * 500) as total_customers,
    FLOOR(10 + RAND() * 50) as new_customers,
    FLOOR(800 + RAND() * 200) as active_customers,
    FLOOR(1 + RAND() * 20) as churned_customers,
    ROUND(70 + RAND() * 25, 2) as retention_rate,
    CASE numbers.seq % 5
        WHEN 0 THEN 'HOURLY'
        WHEN 1 THEN 'DAILY'
        WHEN 2 THEN 'WEEKLY'
        WHEN 3 THEN 'MONTHLY'
        ELSE 'YEARLY'
    END as period_type
FROM (
    SELECT a.N + b.N * 10 + c.N * 100 as seq
    FROM (SELECT 0 as N UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) a,
         (SELECT 0 as N UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) b,
         (SELECT 0 as N) c
) numbers
WHERE numbers.seq < 100;

-- Insertion des KPIs d'abonnement
INSERT INTO subscription_kpis (measurement_date, total_subscriptions, active_subscriptions,
                             canceled_subscriptions, mrr, arr, churn_rate, period_type)
SELECT 
    DATE_SUB(CURRENT_DATE(), INTERVAL numbers.seq DAY) as measurement_date,
    FLOOR(500 + RAND() * 200) as total_subscriptions,
    FLOOR(450 + RAND() * 150) as active_subscriptions,
    FLOOR(1 + RAND() * 20) as canceled_subscriptions,
    ROUND(10000 + RAND() * 5000, 2) as mrr,
    ROUND(120000 + RAND() * 60000, 2) as arr,
    ROUND(1 + RAND() * 5, 2) as churn_rate,
    CASE numbers.seq % 5
        WHEN 0 THEN 'HOURLY'
        WHEN 1 THEN 'DAILY'
        WHEN 2 THEN 'WEEKLY'
        WHEN 3 THEN 'MONTHLY'
        ELSE 'YEARLY'
    END as period_type
FROM (
    SELECT a.N + b.N * 10 + c.N * 100 as seq
    FROM (SELECT 0 as N UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) a,
         (SELECT 0 as N UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) b,
         (SELECT 0 as N) c
) numbers
WHERE numbers.seq < 100;

-- Vérification des insertions
SELECT 'stripe_transactions' as table_name, COUNT(*) as count FROM stripe_transactions
UNION ALL
SELECT 'payment_kpis', COUNT(*) FROM payment_kpis
UNION ALL
SELECT 'customer_kpis', COUNT(*) FROM customer_kpis
UNION ALL
SELECT 'subscription_kpis', COUNT(*) FROM subscription_kpis;