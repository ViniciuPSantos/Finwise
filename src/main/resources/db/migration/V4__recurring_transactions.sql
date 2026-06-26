CREATE TABLE recurring_transactions (
    id BIGINT NOT NULL AUTO_INCREMENT,
    created_at DATETIME(6) NOT NULL,
    deleted_at DATETIME(6) NULL,
    amount DECIMAL(19,2) NOT NULL,
    type ENUM('EXPENSE','INCOME') NOT NULL,
    description VARCHAR(255) NOT NULL,
    frequency ENUM('DAILY','WEEKLY','MONTHLY','YEARLY') NOT NULL,
    next_execution_date DATE NOT NULL,
    active BIT(1) NOT NULL DEFAULT 1,
    account_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_recurring_account FOREIGN KEY (account_id) REFERENCES accounts (id),
    CONSTRAINT fk_recurring_category FOREIGN KEY (category_id) REFERENCES categories (id),
    CONSTRAINT fk_recurring_user FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
