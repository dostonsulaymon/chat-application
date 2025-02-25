-- Users table
CREATE TABLE users (
                       user_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                       username VARCHAR(50) UNIQUE NOT NULL,
                       email VARCHAR(100) UNIQUE NOT NULL,
                       password_hash VARCHAR(255),
                       profile_picture_url VARCHAR(255),
                       status VARCHAR(20) NOT NULL DEFAULT 'OFFLINE',
                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       last_login TIMESTAMP,
                       oauth_provider VARCHAR(20),
                       oauth_id VARCHAR(100),
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
                                      role VARCHAR(20) NOT NULL DEFAULT 'MEMBER',
                                      joined_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                      left_at TIMESTAMP,
                                      CONSTRAINT unique_conversation_member UNIQUE (conversation_id, user_id)
);

-- Messages table
CREATE TABLE messages (
                          message_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                          conversation_id UUID NOT NULL REFERENCES conversations(conversation_id),
                          sender_id UUID NOT NULL REFERENCES users(user_id),
                          content TEXT NOT NULL,
                          sent_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          edited_at TIMESTAMP,
                          is_deleted BOOLEAN NOT NULL DEFAULT FALSE
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

-- Add indexes for performance
CREATE INDEX idx_messages_conversation_id ON messages(conversation_id);
CREATE INDEX idx_conversation_members_conversation_id ON conversation_members(conversation_id);
CREATE INDEX idx_conversation_members_user_id ON conversation_members(user_id);
CREATE INDEX idx_message_status_message_id ON message_status(message_id);
CREATE INDEX idx_messages_sender_id ON messages(sender_id);