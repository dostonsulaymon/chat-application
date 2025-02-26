-- Add initial admin user
-- Note: In a real application, you should use a properly hashed password
-- This example uses 'admin123' as the password, but the hash shown
-- is just a placeholder - you should generate a real bcrypt hash

INSERT INTO attaches (
    id,
    origin_name,
    size,
    type,
    path,
    extension,
    visible,
    created_date
) VALUES (
             'default-admin-profile-pic',
             'admin_profile.png',
             1024,
             'image/png',
             '/Users/dostonsulaymon/Downloads/IMG_0810.JPG',
             'JPG',
             TRUE,
             CURRENT_TIMESTAMP
         );

INSERT INTO users (
    user_id,
    username,
    email,
    phone_number,
    password_hash,
    profile_picture_id,
    status,
    created_at,
    updated_at,
    last_login,
    account_locked,
    is_deleted
) VALUES (
             'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', -- predefined UUID for admin
             'admin',
             'admin@example.com',
             NULL, -- No phone number for admin
             '$2a$10$rM7lSK5VgN7CRGRs7VNZge9MjI5FdR8F9IzjOu.lK6QjOQHXRDyRK', -- bcrypt hash for 'admin123'
             'default-admin-profile-pic',
             'OFFLINE',
             CURRENT_TIMESTAMP,
             CURRENT_TIMESTAMP,
             NULL, -- hasn't logged in yet
             FALSE, -- not locked
             FALSE  -- not deleted
         );

-- Create a 'System' conversation for announcements
INSERT INTO conversations (
    conversation_id,
    type,
    name,
    created_at,
    updated_at,
    created_by
) VALUES (
             'b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', -- predefined UUID for system conversation
             'CHANNEL',
             'System Announcements',
             CURRENT_TIMESTAMP,
             CURRENT_TIMESTAMP,
             'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11' -- created by admin
         );

-- Add admin as owner of the System conversation
INSERT INTO conversation_members (
    id,
    conversation_id,
    user_id,
    role,
    joined_at
) VALUES (
             gen_random_uuid(),
             'b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', -- system conversation
             'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', -- admin user
             'OWNER',
             CURRENT_TIMESTAMP
         );

-- Add a welcome message
INSERT INTO messages (
    message_id,
    conversation_id,
    sender_id,
    content,
    sent_at
) VALUES (
             gen_random_uuid(),
             'b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', -- system conversation
             'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', -- admin user
             'Welcome to the chat system! This is a system channel for important announcements.',
             CURRENT_TIMESTAMP
         );