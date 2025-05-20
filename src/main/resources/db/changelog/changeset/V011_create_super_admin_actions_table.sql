CREATE TABLE IF NOT EXISTS logging_schema.super_admin_action (
    id UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
    admin_id UUID NOT NULL,
    user_id UUID NOT NULL,
    user_email VARCHAR(100) NOT NULL,
    user_role VARCHAR(100) NOT NULL,
    action_type VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL
);
