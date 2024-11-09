-- Création de la base de données si elle n'existe pas
CREATE DATABASE IF NOT EXISTS kreo CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE kreo;

-- Table pour les KPIs de paiement
CREATE TABLE IF NOT EXISTS payment_kpis (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    measurement_date DATETIME NOT NULL,
    total_transactions INT NOT NULL DEFAULT 0,
    successful_transactions INT NOT NULL DEFAULT 0,
    failed_transactions INT NOT NULL DEFAULT 0,
    total_amount DECIMAL(20,2) NOT NULL DEFAULT 0.00,
    average_transaction_amount DECIMAL(20,2) NOT NULL DEFAULT 0.00,
    success_rate DECIMAL(5,2) NOT NULL DEFAULT 0.00,
    currency VARCHAR(3) NOT NULL DEFAULT 'EUR',
    period_type ENUM('HOURLY', 'DAILY', 'WEEKLY', 'MONTHLY', 'YEARLY') NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_measurement_date (measurement_date),
    INDEX idx_period_type (period_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table pour les KPIs client
CREATE TABLE IF NOT EXISTS customer_kpis (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    measurement_date DATETIME NOT NULL,
    total_customers INT NOT NULL DEFAULT 0,
    new_customers INT NOT NULL DEFAULT 0,
    active_customers INT NOT NULL DEFAULT 0,
    churned_customers INT NOT NULL DEFAULT 0,
    retention_rate DECIMAL(5,2) NOT NULL DEFAULT 0.00,
    period_type ENUM('HOURLY', 'DAILY', 'WEEKLY', 'MONTHLY', 'YEARLY') NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_measurement_date (measurement_date),
    INDEX idx_period_type (period_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table pour les KPIs d'abonnement
CREATE TABLE IF NOT EXISTS subscription_kpis (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    measurement_date DATETIME NOT NULL,
    total_subscriptions INT NOT NULL DEFAULT 0,
    active_subscriptions INT NOT NULL DEFAULT 0,
    canceled_subscriptions INT NOT NULL DEFAULT 0,
    mrr DECIMAL(20,2) NOT NULL DEFAULT 0.00,  -- Monthly Recurring Revenue
    arr DECIMAL(20,2) NOT NULL DEFAULT 0.00,  -- Annual Recurring Revenue
    churn_rate DECIMAL(5,2) NOT NULL DEFAULT 0.00,
    period_type ENUM('HOURLY', 'DAILY', 'WEEKLY', 'MONTHLY', 'YEARLY') NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_measurement_date (measurement_date),
    INDEX idx_period_type (period_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table pour stocker l'historique des transactions Stripe
CREATE TABLE IF NOT EXISTS stripe_transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    stripe_transaction_id VARCHAR(100) NOT NULL,
    transaction_type ENUM('PAYMENT', 'REFUND', 'DISPUTE') NOT NULL,
    amount DECIMAL(20,2) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    status VARCHAR(50) NOT NULL,
    customer_id VARCHAR(100),
    payment_method_type VARCHAR(50),
    transaction_date DATETIME NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_stripe_transaction_id (stripe_transaction_id),
    INDEX idx_customer_id (customer_id),
    INDEX idx_transaction_date (transaction_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Vue pour les métriques quotidiennes
CREATE OR REPLACE VIEW daily_payment_metrics AS
SELECT 
    DATE(transaction_date) as date,
    COUNT(*) as total_transactions,
    SUM(CASE WHEN status = 'succeeded' THEN 1 ELSE 0 END) as successful_transactions,
    SUM(CASE WHEN status != 'succeeded' THEN 1 ELSE 0 END) as failed_transactions,
    SUM(CASE WHEN status = 'succeeded' THEN amount ELSE 0 END) as total_amount,
    currency,
    (SUM(CASE WHEN status = 'succeeded' THEN 1 ELSE 0 END) / COUNT(*) * 100) as success_rate
FROM 
    stripe_transactions
WHERE 
    transaction_type = 'PAYMENT'
GROUP BY 
    DATE(transaction_date), currency;

-- Procédure stockée pour calculer les KPIs
DELIMITER //
CREATE PROCEDURE calculate_daily_kpis(IN calculation_date DATE)
BEGIN
    -- Calcul des KPIs de paiement
    INSERT INTO payment_kpis (
        measurement_date,
        total_transactions,
        successful_transactions,
        failed_transactions,
        total_amount,
        average_transaction_amount,
        success_rate,
        currency,
        period_type
    )
    SELECT 
        calculation_date,
        COUNT(*) as total_transactions,
        SUM(CASE WHEN status = 'succeeded' THEN 1 ELSE 0 END) as successful_transactions,
        SUM(CASE WHEN status != 'succeeded' THEN 1 ELSE 0 END) as failed_transactions,
        SUM(CASE WHEN status = 'succeeded' THEN amount ELSE 0 END) as total_amount,
        SUM(CASE WHEN status = 'succeeded' THEN amount ELSE 0 END) / 
            NULLIF(SUM(CASE WHEN status = 'succeeded' THEN 1 ELSE 0 END), 0) as avg_amount,
        (SUM(CASE WHEN status = 'succeeded' THEN 1 ELSE 0 END) / COUNT(*) * 100) as success_rate,
        currency,
        'DAILY'
    FROM stripe_transactions
    WHERE 
        DATE(transaction_date) = calculation_date
        AND transaction_type = 'PAYMENT'
    GROUP BY currency;
END //
DELIMITER ;

-- Trigger pour mettre à jour les KPIs après chaque transaction
DELIMITER //
CREATE TRIGGER after_stripe_transaction_insert
AFTER INSERT ON stripe_transactions
FOR EACH ROW
BEGIN
    CALL calculate_daily_kpis(DATE(NEW.transaction_date));
END //
DELIMITER ;

-- Index pour améliorer les performances
CREATE INDEX idx_transaction_status ON stripe_transactions(status);
CREATE INDEX idx_transaction_type_date ON stripe_transactions(transaction_type, transaction_date);
