CREATE TABLE IF NOT EXISTS logging_schema.mentor_action (
    id UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
    mentor_id UUID NOT NULL,
    resource_id UUID NOT NULL,
    mentor_action_type VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL
);