CREATE TABLE password_recovery_token (
                                          id BIGSERIAL PRIMARY KEY,
                                          token VARCHAR(255) NOT NULL UNIQUE,
                                          member_id UUID NOT NULL,
                                          expiration TIMESTAMP NOT NULL,
                                          used BOOLEAN DEFAULT FALSE,
                                          created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
                                          updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
                                          CONSTRAINT fk_token_member FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE CASCADE
);
