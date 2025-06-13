CREATE TABLE IF NOT EXISTS logging_schema.hr_action (
    id UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
    hr_id UUID NOT NULL,
    user_id UUID NOT NULL,
    hr_action_type VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL
);