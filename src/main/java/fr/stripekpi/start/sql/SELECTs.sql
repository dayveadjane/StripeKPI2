-- 1. Analyse des paiements par période
SELECT 
    DATE(measurement_date) as date,
    total_transactions,
    successful_transactions,
    ROUND((successful_transactions / total_transactions) * 100, 2) as success_percentage,
    total_amount,
    currency,
    period_type
FROM payment_kpis
WHERE period_type = 'DAILY'
ORDER BY measurement_date DESC
LIMIT 10;

-- 2. Tendance des transactions sur les 7 derniers jours
SELECT 
    DATE(measurement_date) as date,
    SUM(successful_transactions) as total_successful,
    SUM(failed_transactions) as total_failed,
    SUM(total_amount) as daily_volume,
    ROUND(AVG(success_rate), 2) as avg_success_rate
FROM payment_kpis
WHERE measurement_date >= DATE_SUB(NOW(), INTERVAL 7 DAY)
GROUP BY DATE(measurement_date)
ORDER BY date DESC;

-- 3. Analyse des clients par mois
SELECT 
    DATE_FORMAT(measurement_date, '%Y-%m') as month,
    AVG(total_customers) as avg_total_customers,
    SUM(new_customers) as total_new_customers,
    SUM(churned_customers) as total_churned_customers,
    ROUND(AVG(retention_rate), 2) as avg_retention_rate
FROM customer_kpis
GROUP BY DATE_FORMAT(measurement_date, '%Y-%m')
ORDER BY month DESC;

-- 4. Performance des abonnements
SELECT 
    DATE(measurement_date) as date,
    total_subscriptions,
    active_subscriptions,
    canceled_subscriptions,
    mrr,
    ROUND((canceled_subscriptions / total_subscriptions) * 100, 2) as churn_percentage
FROM subscription_kpis
ORDER BY measurement_date DESC
LIMIT 10;

-- 5. Analyse des transactions Stripe par type de paiement
SELECT 
    payment_method_type,
    COUNT(*) as total_transactions,
    COUNT(CASE WHEN status = 'succeeded' THEN 1 END) as successful,
    COUNT(CASE WHEN status = 'failed' THEN 1 END) as failed,
    ROUND(AVG(amount), 2) as avg_amount,
    currency
FROM stripe_transactions
GROUP BY payment_method_type, currency
ORDER BY total_transactions DESC;

-- 6. Evolution du MRR (Monthly Recurring Revenue)
SELECT 
    DATE_FORMAT(measurement_date, '%Y-%m') as month,
    ROUND(AVG(mrr), 2) as average_mrr,
    ROUND(MAX(mrr), 2) as max_mrr,
    ROUND(MIN(mrr), 2) as min_mrr,
    ROUND(AVG(arr), 2) as average_arr
FROM subscription_kpis
GROUP BY DATE_FORMAT(measurement_date, '%Y-%m')
ORDER BY month DESC;

-- 7. Analyse des taux de conversion par jour
WITH daily_metrics AS (
    SELECT 
        DATE(transaction_date) as date,
        COUNT(*) as total_attempts,
        COUNT(CASE WHEN status = 'succeeded' THEN 1 END) as successes
    FROM stripe_transactions
    WHERE transaction_type = 'PAYMENT'
    GROUP BY DATE(transaction_date)
)
SELECT 
    date,
    total_attempts,
    successes,
    ROUND((successes / total_attempts) * 100, 2) as conversion_rate
FROM daily_metrics
ORDER BY date DESC;

-- 8. Top clients par volume de transactions
SELECT 
    customer_id,
    COUNT(*) as total_transactions,
    SUM(CASE WHEN status = 'succeeded' THEN amount ELSE 0 END) as total_amount,
    currency
FROM stripe_transactions
GROUP BY customer_id, currency
HAVING total_transactions > 1
ORDER BY total_amount DESC
LIMIT 10;

-- 9. Analyse horaire des transactions
SELECT 
    HOUR(transaction_date) as hour_of_day,
    COUNT(*) as total_transactions,
    ROUND(AVG(amount), 2) as avg_amount,
    COUNT(CASE WHEN status = 'succeeded' THEN 1 END) as successful_transactions
FROM stripe_transactions
GROUP BY HOUR(transaction_date)
ORDER BY hour_of_day;

-- 10. Dashboard récapitulatif
SELECT 
    'Dernières 24h' as period,
    COUNT(*) as transactions,
    ROUND(SUM(CASE WHEN status = 'succeeded' THEN amount ELSE 0 END), 2) as volume,
    COUNT(DISTINCT customer_id) as unique_customers
FROM stripe_transactions
WHERE transaction_date >= DATE_SUB(NOW(), INTERVAL 24 HOUR)
UNION ALL
SELECT 
    'Derniers 7 jours',
    COUNT(*),
    ROUND(SUM(CASE WHEN status = 'succeeded' THEN amount ELSE 0 END), 2),
    COUNT(DISTINCT customer_id)
FROM stripe_transactions
WHERE transaction_date >= DATE_SUB(NOW(), INTERVAL 7 DAY)
UNION ALL
SELECT 
    'Dernier mois',
    COUNT(*),
    ROUND(SUM(CASE WHEN status = 'succeeded' THEN amount ELSE 0 END), 2),
    COUNT(DISTINCT customer_id)
FROM stripe_transactions
WHERE transaction_date >= DATE_SUB(NOW(), INTERVAL 30 DAY);

-- 11. Analyse des méthodes de paiement qui échouent le plus
SELECT 
    payment_method_type,
    status,
    COUNT(*) as count,
    ROUND((COUNT(*) * 100.0 / SUM(COUNT(*)) OVER (PARTITION BY payment_method_type)), 2) as percentage
FROM stripe_transactions
GROUP BY payment_method_type, status
ORDER BY payment_method_type, count DESC;

-- 12. Comparaison mois par mois
SELECT 
    DATE_FORMAT(measurement_date, '%Y-%m') as month,
    ROUND(AVG(total_amount), 2) as avg_daily_amount,
    ROUND(AVG(success_rate), 2) as avg_success_rate,
    ROUND(AVG(average_transaction_amount), 2) as avg_transaction_size
FROM payment_kpis
GROUP BY DATE_FORMAT(measurement_date, '%Y-%m')
ORDER BY month DESC;