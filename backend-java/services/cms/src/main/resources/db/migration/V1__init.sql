-- Create shows table with optimizations for write-heavy operations
-- Includes provider column to track data source (internal vs external providers)
CREATE TABLE shows (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    type VARCHAR(50) NOT NULL CHECK (type IN ('podcast', 'documentary')),
    title VARCHAR(255) NOT NULL,
    description TEXT,
    language VARCHAR(10),
    duration_sec INTEGER CHECK (duration_sec >= 0),
    published_at DATE,
    created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    provider VARCHAR(50) NOT NULL DEFAULT 'internal' CHECK (provider IN ('internal', 'vimeo', 'youtube', 'mock'))
);

-- Create indexes for efficient querying
CREATE INDEX idx_shows_type ON shows(type);
CREATE INDEX idx_shows_language ON shows(language);
CREATE INDEX idx_shows_published_at ON shows(published_at);
CREATE INDEX idx_shows_created_date ON shows(created_date);
CREATE INDEX idx_shows_title ON shows(title);
CREATE INDEX idx_shows_provider ON shows(provider);

-- Create unique constraint on title to prevent duplicates
CREATE UNIQUE INDEX idx_shows_title_unique ON shows(LOWER(title));

-- Create composite indexes for common query patterns
CREATE INDEX idx_shows_type_provider ON shows(type, provider);
CREATE INDEX idx_shows_language_provider ON shows(language, provider);
CREATE INDEX idx_shows_created_date_provider ON shows(created_date, provider);

-- Add comments for documentation
COMMENT ON TABLE shows IS 'Content Management System shows table for podcasts and documentaries with provider tracking';
COMMENT ON COLUMN shows.type IS 'Type of show: podcast or documentary';
COMMENT ON COLUMN shows.title IS 'Title of the show (must be unique)';
COMMENT ON COLUMN shows.description IS 'Detailed description of the show content';
COMMENT ON COLUMN shows.language IS 'ISO 639-1 language code (e.g., en, es, fr)';
COMMENT ON COLUMN shows.duration_sec IS 'Duration of the show in seconds';
COMMENT ON COLUMN shows.published_at IS 'Date when the show was published';
COMMENT ON COLUMN shows.created_date IS 'Timestamp when the record was created';
COMMENT ON COLUMN shows.updated_date IS 'Timestamp when the record was last updated';
COMMENT ON COLUMN shows.created_by IS 'Email of the user who created the show';
COMMENT ON COLUMN shows.updated_by IS 'Email of the user who last updated the show';
COMMENT ON COLUMN shows.provider IS 'Data source provider: internal for manual creation, vimeo/youtube/etc for imports';

-- Create users table for authentication and authorization
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL CHECK (role IN ('ADMIN', 'EDITOR')),
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

-- Create indexes for users table
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_users_created_date ON users(created_date);
CREATE UNIQUE INDEX idx_users_email_unique ON users(LOWER(email));

-- Add comments for users table
COMMENT ON TABLE users IS 'CMS users table for authentication and role-based access control';
COMMENT ON COLUMN users.email IS 'Unique email address for user authentication';
COMMENT ON COLUMN users.password IS 'BCrypt hashed password';
COMMENT ON COLUMN users.role IS 'User role: ADMIN (full access) or EDITOR (limited access)';
COMMENT ON COLUMN users.is_active IS 'Whether the user account is active';
COMMENT ON COLUMN users.created_date IS 'Timestamp when the user was created';
COMMENT ON COLUMN users.updated_date IS 'Timestamp when the user was last updated';
COMMENT ON COLUMN users.created_by IS 'Email of the user who created this user';
COMMENT ON COLUMN users.updated_by IS 'Email of the user who last updated this user';
