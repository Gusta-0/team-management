CREATE TABLE member (
                         id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                         name VARCHAR(255) NOT NULL,
                         email VARCHAR(255) UNIQUE NOT NULL,
                         password VARCHAR(255) NOT NULL,
                         role VARCHAR(50) NOT NULL,
                         department VARCHAR(255),
                         phone VARCHAR(50),
                         join_date TIMESTAMPTZ,
                         status VARCHAR(50) DEFAULT 'ACTIVE',
                         image TEXT,
                         failed_attempts INTEGER DEFAULT 0,
                         account_locked BOOLEAN DEFAULT FALSE,
                         lock_time TIMESTAMP,
                         created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
                         updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);
