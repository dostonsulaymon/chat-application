-- Add firstName and lastName fields to users table
ALTER TABLE users
    ADD COLUMN first_name VARCHAR(50),
ADD COLUMN last_name VARCHAR(50);