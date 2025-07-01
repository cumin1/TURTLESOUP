CREATE TABLE user (
                      id BIGINT PRIMARY KEY AUTO_INCREMENT,
                      username VARCHAR(50) NOT NULL UNIQUE,
                      password_hash VARCHAR(255) NOT NULL,
                      email VARCHAR(100) UNIQUE,
                      avatar_url VARCHAR(255),
                      created_at DATETIME NOT NULL,
                      last_login DATETIME
);
CREATE TABLE soup (
                      id BIGINT PRIMARY KEY AUTO_INCREMENT,
                      title VARCHAR(100) NOT NULL,
                      description TEXT NOT NULL,
                      answer TEXT NOT NULL,
                      difficulty INT,
                      creator_id BIGINT,
                      created_at DATETIME NOT NULL,
                      updated_at DATETIME,
                      is_public BOOLEAN DEFAULT TRUE,
                      FOREIGN KEY (creator_id) REFERENCES user(id)
);
CREATE TABLE tag (
                     id BIGINT PRIMARY KEY AUTO_INCREMENT,
                     name VARCHAR(50) NOT NULL UNIQUE
);
CREATE TABLE soup_tag (
                          soup_id BIGINT NOT NULL,
                          tag_id BIGINT NOT NULL,
                          PRIMARY KEY (soup_id, tag_id),
                          FOREIGN KEY (soup_id) REFERENCES soup(id),
                          FOREIGN KEY (tag_id) REFERENCES tag(id)
);
CREATE TABLE game_session (
                              id BIGINT PRIMARY KEY AUTO_INCREMENT,
                              user_id BIGINT NOT NULL,
                              soup_id BIGINT NOT NULL,
                              start_time DATETIME NOT NULL,
                              end_time DATETIME,
                              status VARCHAR(20),
                              used_hint_cnt INT,
                              FOREIGN KEY (user_id) REFERENCES user(id),
                              FOREIGN KEY (soup_id) REFERENCES soup(id)
);
CREATE TABLE question_log (
                              id BIGINT PRIMARY KEY AUTO_INCREMENT,
                              game_session_id BIGINT NOT NULL,
                              question TEXT NOT NULL,
                              ai_answer VARCHAR(10) NOT NULL,
                              created_at DATETIME NOT NULL,
                              FOREIGN KEY (game_session_id) REFERENCES game_session(id)
);
CREATE TABLE favorite (
                          id BIGINT PRIMARY KEY AUTO_INCREMENT,
                          user_id BIGINT NOT NULL,
                          soup_id BIGINT NOT NULL,
                          created_at DATETIME NOT NULL,
                          FOREIGN KEY (user_id) REFERENCES user(id),
                          FOREIGN KEY (soup_id) REFERENCES soup(id)
);
CREATE TABLE soup_comment (
                              id BIGINT PRIMARY KEY AUTO_INCREMENT,
                              soup_id BIGINT NOT NULL,
                              user_id BIGINT NOT NULL,
                              content TEXT NOT NULL,
                              created_at DATETIME NOT NULL,
                              FOREIGN KEY (soup_id) REFERENCES soup(id),
                              FOREIGN KEY (user_id) REFERENCES user(id)
);

ALTER TABLE user CHANGE COLUMN password_hash password VARCHAR(255) NOT NULL;

ALTER TABLE question_log MODIFY COLUMN ai_answer TEXT;