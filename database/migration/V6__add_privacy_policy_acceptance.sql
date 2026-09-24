ALTER TABLE users
    ADD COLUMN privacy_policy_accepted BIT(1) NOT NULL DEFAULT b'0',
    ADD COLUMN privacy_policy_version VARCHAR(30) NULL;
