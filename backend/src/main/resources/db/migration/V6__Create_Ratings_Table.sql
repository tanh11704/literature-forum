CREATE TABLE ratings (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    submission_id UUID NOT NULL REFERENCES submissions(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    score SMALLINT NOT NULL CHECK (score BETWEEN 1 AND 5),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_ratings_submission_user UNIQUE (submission_id, user_id)
);

CREATE INDEX idx_ratings_submission_id ON ratings(submission_id);
CREATE INDEX idx_ratings_user_id ON ratings(user_id);

CREATE TRIGGER trg_ratings_updated_at
    BEFORE UPDATE ON ratings
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

ALTER TABLE submissions
    ADD COLUMN avg_score NUMERIC(3,2) NOT NULL DEFAULT 0.00,
    ADD COLUMN rating_count INTEGER NOT NULL DEFAULT 0;
