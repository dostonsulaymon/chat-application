-- Attaches table for media files
CREATE TABLE attaches (
                          id VARCHAR(255) PRIMARY KEY,
                          origin_name VARCHAR(255) NOT NULL,
                          size BIGINT NOT NULL,
                          type VARCHAR(50) NOT NULL,
                          path VARCHAR(255) NOT NULL,
                          duration VARCHAR(50),
                          created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          extension VARCHAR(20) NOT NULL,
                          visible BOOLEAN NOT NULL DEFAULT TRUE
);

-- Users table
CREATE TABLE users (
                       user_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                       username VARCHAR(50) UNIQUE NOT NULL,
                       email VARCHAR(100) UNIQUE,
                       phone_number VARCHAR(20) UNIQUE,
                       password_hash VARCHAR(255),
                       profile_picture_id VARCHAR(255) REFERENCES attaches(id),
                       status VARCHAR(20) NOT NULL DEFAULT 'OFFLINE',
                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       last_login TIMESTAMP,
                       account_locked BOOLEAN DEFAULT FALSE,
                       account_locked_until TIMESTAMP,
                       is_deleted BOOLEAN DEFAULT FALSE,
                       deleted_at TIMESTAMP,
                       oauth_provider VARCHAR(20),
                       oauth_id VARCHAR(100),
                       CONSTRAINT email_or_phone_provided CHECK (email IS NOT NULL OR phone_number IS NOT NULL),
                       CONSTRAINT unique_oauth UNIQUE (oauth_provider, oauth_id)
);

-- Conversations table
CREATE TABLE conversations (
                               conversation_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                               type VARCHAR(20) NOT NULL CHECK (type IN ('DIRECT', 'GROUP', 'CHANNEL')),
                               name VARCHAR(100),
                               created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                               updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                               created_by UUID REFERENCES users(user_id)
);

-- Conversation members table
CREATE TABLE conversation_members (
                                      id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                      conversation_id UUID NOT NULL REFERENCES conversations(conversation_id),
                                      user_id UUID NOT NULL REFERENCES users(user_id),
                                      role VARCHAR(20) NOT NULL DEFAULT 'MEMBER' CHECK (role IN ('MEMBER', 'MODERATOR', 'ADMIN', 'OWNER')),
                                      joined_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                      left_at TIMESTAMP,
                                      CONSTRAINT unique_conversation_member UNIQUE (conversation_id, user_id)
);

-- Messages table
CREATE TABLE messages (
                          message_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                          conversation_id UUID NOT NULL REFERENCES conversations(conversation_id),
                          sender_id UUID NOT NULL REFERENCES users(user_id),
                          replied_to_id UUID REFERENCES messages(message_id),
                          content TEXT,
                          attach_id VARCHAR(255) REFERENCES attaches(id),
                          sent_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          edited_at TIMESTAMP,
                          is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
                          CHECK (content IS NOT NULL OR attach_id IS NOT NULL)
);

-- Message status table
CREATE TABLE message_status (
                                id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                message_id UUID NOT NULL REFERENCES messages(message_id),
                                user_id UUID NOT NULL REFERENCES users(user_id),
                                status VARCHAR(20) NOT NULL CHECK (status IN ('DELIVERED', 'READ')),
                                updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                CONSTRAINT unique_message_status UNIQUE (message_id, user_id)
);

-- Reactions table (for future use)
CREATE TABLE reactions (
                           reaction_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                           message_id UUID NOT NULL REFERENCES messages(message_id),
                           user_id UUID NOT NULL REFERENCES users(user_id),
                           reaction_type VARCHAR(20) NOT NULL,
                           created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           CONSTRAINT unique_user_reaction UNIQUE (message_id, user_id, reaction_type)
);


-- Email History table
CREATE TABLE email_history (
                               id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                               to_account VARCHAR(100) NOT NULL,
                               subject VARCHAR(255) NOT NULL,
                               message TEXT,
                               sent_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                               status VARCHAR(20) NOT NULL,
                               attempt_count INTEGER DEFAULT 0,
                               error_message TEXT,
                               verification_code INTEGER,
                               user_id UUID REFERENCES users(user_id)
);

-- SMS History table
CREATE TABLE sms_history (
                             id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                             phone_number VARCHAR(20) NOT NULL,
                             message TEXT NOT NULL,
                             sent_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                             status VARCHAR(20) NOT NULL,
                             attempt_count INTEGER DEFAULT 0,
                             error_message TEXT,
                             verification_code INTEGER,
                             user_id UUID REFERENCES users(user_id)
);

-- Add indexes for performance
CREATE INDEX idx_email_history_user_id ON email_history(user_id);
CREATE INDEX idx_email_history_to_account ON email_history(to_account);
CREATE INDEX idx_email_history_sent_at ON email_history(sent_at);
CREATE INDEX idx_email_history_status ON email_history(status);

CREATE INDEX idx_sms_history_user_id ON sms_history(user_id);
CREATE INDEX idx_sms_history_phone_number ON sms_history(phone_number);
CREATE INDEX idx_sms_history_sent_at ON sms_history(sent_at);
CREATE INDEX idx_sms_history_status ON sms_history(status);

-- Add indexes for performance
CREATE INDEX idx_messages_conversation_id ON messages(conversation_id);
CREATE INDEX idx_conversation_members_conversation_id ON conversation_members(conversation_id);
CREATE INDEX idx_conversation_members_user_id ON conversation_members(user_id);
CREATE INDEX idx_message_status_message_id ON message_status(message_id);
CREATE INDEX idx_messages_sender_id ON messages(sender_id);
CREATE INDEX idx_messages_attach_id ON messages(attach_id);
CREATE INDEX idx_messages_replied_to_id ON messages(replied_to_id);
CREATE INDEX idx_users_profile_picture_id ON users(profile_picture_id);