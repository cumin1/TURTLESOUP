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

-- 1. 用户
INSERT INTO user (username, password_hash, email, avatar_url, created_at, last_login) VALUES
                                                                                          ('alice', 'hash1', 'alice@example.com', NULL, NOW(), NOW()),
                                                                                          ('bob', 'hash2', 'bob@example.com', NULL, NOW(), NOW());

-- 2. 标签
INSERT INTO tag (name) VALUES
                           ('经典'), ('悬疑'), ('搞笑'), ('烧脑');

-- 3. 题库
INSERT INTO soup (title, description, answer, difficulty, creator_id, created_at, updated_at, is_public) VALUES
                                                                                                             ('电梯里的女人', '一个女人每天都要乘电梯上下班，但只有下班时才会直接到达家门口。为什么？', '女人是个侏儒，只有下班时有高个邻居帮她按高楼层按钮。', 3, 1, NOW(), NOW(), TRUE),
                                                                                                             ('红色的雨伞', '一个人打着红色雨伞走进咖啡馆，出来时却消失了。为什么？', '他其实是双胞胎兄弟，换人了。', 2, 2, NOW(), NOW(), TRUE);

-- 4. 题目-标签关联
INSERT INTO soup_tag (soup_id, tag_id) VALUES
                                           (1, 1), (1, 4), (2, 2), (2, 3);

-- 5. 游戏记录
INSERT INTO game_session (user_id, soup_id, start_time, end_time, status, used_hint_cnt) VALUES
                                                                                             (1, 1, NOW(), NULL, '进行中', 0),
                                                                                             (2, 2, NOW(), NOW(), '通关', 1);

-- 6. 提问记录
INSERT INTO question_log (game_session_id, question, ai_answer, created_at) VALUES
                                                                                (1, '她每天都住在同一层吗？', '是', NOW()),
                                                                                (1, '她有残疾吗？', '是', NOW()),
                                                                                (2, '雨伞是关键吗？', '不是', NOW()),
                                                                                (2, '有两个人吗？', '是', NOW());

-- 7. 收藏
INSERT INTO favorite (user_id, soup_id, created_at) VALUES
                                                        (1, 2, NOW()),
                                                        (2, 1, NOW());

-- 8. 评论
INSERT INTO soup_comment (soup_id, user_id, content, created_at) VALUES
                                                                     (1, 2, '这个题目很有趣！', NOW()),
                                                                     (2, 1, '我差点没猜出来！', NOW());

ALTER TABLE question_log MODIFY COLUMN ai_answer TEXT;