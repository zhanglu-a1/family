-- 家庭回忆录系统 · M0 初始化表结构
-- MySQL 8.0，字符集 utf8mb4

CREATE DATABASE IF NOT EXISTS family_memoir
    DEFAULT CHARACTER SET utf8mb4
    COLLATE utf8mb4_0900_ai_ci;

USE family_memoir;

CREATE TABLE IF NOT EXISTS sys_user (
    id            BIGINT       NOT NULL COMMENT '雪花 ID',
    username      VARCHAR(50)  NOT NULL COMMENT '登录名',
    password_hash VARCHAR(100) NOT NULL COMMENT 'BCrypt 密码',
    nickname      VARCHAR(50)  DEFAULT NULL COMMENT '昵称',
    avatar_url    VARCHAR(500) DEFAULT NULL COMMENT '头像',
    email         VARCHAR(100) DEFAULT NULL,
    phone         VARCHAR(100) DEFAULT NULL,
    status        TINYINT      NOT NULL DEFAULT 1 COMMENT '0 禁用 / 1 正常',
    created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_username (username)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '用户';

CREATE TABLE IF NOT EXISTS family_space (
    id           BIGINT       NOT NULL,
    name         VARCHAR(100) NOT NULL COMMENT '空间名称',
    cover_url    VARCHAR(500) DEFAULT NULL,
    description  VARCHAR(500) DEFAULT NULL,
    owner_id     BIGINT       NOT NULL COMMENT '所有者',
    invite_code  VARCHAR(20)  NOT NULL COMMENT '邀请码',
    member_limit INT          NOT NULL DEFAULT 20,
    created_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_invite_code (invite_code),
    KEY idx_owner (owner_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '家庭空间';

CREATE TABLE IF NOT EXISTS family_member (
    id        BIGINT      NOT NULL,
    space_id  BIGINT      NOT NULL,
    user_id   BIGINT      NOT NULL,
    role      VARCHAR(20) NOT NULL DEFAULT 'EDITOR' COMMENT 'OWNER / ADMIN / EDITOR / VIEWER',
    relation  VARCHAR(30) DEFAULT NULL COMMENT '家庭称谓',
    joined_at DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_space_user (space_id, user_id),
    KEY idx_user (user_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '空间成员';

CREATE TABLE IF NOT EXISTS person (
    id             BIGINT       NOT NULL,
    space_id       BIGINT       NOT NULL,
    name           VARCHAR(50)  NOT NULL,
    relation       VARCHAR(30)  DEFAULT NULL COMMENT '与家庭的称谓',
    gender         TINYINT      NOT NULL DEFAULT 0 COMMENT '0 未知 / 1 男 / 2 女',
    birth_date     DATE         DEFAULT NULL,
    death_date     DATE         DEFAULT NULL,
    date_precision VARCHAR(10)  NOT NULL DEFAULT 'DAY' COMMENT 'YEAR / MONTH / DAY',
    avatar_url     VARCHAR(500) DEFAULT NULL,
    bio            TEXT         DEFAULT NULL,
    is_alive       TINYINT      NOT NULL DEFAULT 1,
    created_at     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_space (space_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '人物档案';

CREATE TABLE IF NOT EXISTS memoir (
    id              BIGINT       NOT NULL,
    space_id        BIGINT       NOT NULL,
    title           VARCHAR(200) NOT NULL,
    summary         VARCHAR(500) DEFAULT NULL,
    content         LONGTEXT     COMMENT '富文本 HTML',
    content_text    TEXT         COMMENT '纯文本，供检索',
    cover_media_id  BIGINT       DEFAULT NULL,
    event_time      DATETIME     DEFAULT NULL,
    event_year      INT          DEFAULT NULL,
    event_month     INT          DEFAULT NULL,
    event_day       INT          DEFAULT NULL,
    event_precision VARCHAR(10)  NOT NULL DEFAULT 'UNKNOWN' COMMENT 'YEAR / MONTH / DAY / EXACT / UNKNOWN',
    event_time_text VARCHAR(100) DEFAULT NULL COMMENT '原文表达，如「1985 年夏天」',
    location_name   VARCHAR(200) DEFAULT NULL,
    longitude       DECIMAL(10, 6) DEFAULT NULL,
    latitude        DECIMAL(10, 6) DEFAULT NULL,
    mood            VARCHAR(20)  DEFAULT NULL,
    privacy         VARCHAR(20)  NOT NULL DEFAULT 'SPACE' COMMENT 'SPACE / PRIVATE',
    author_id       BIGINT       NOT NULL,
    status          VARCHAR(20)  NOT NULL DEFAULT 'DRAFT' COMMENT 'DRAFT / PUBLISHED',
    view_count      INT          NOT NULL DEFAULT 0,
    created_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted         TINYINT      NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    KEY idx_space_time (space_id, status, event_year, event_month),
    KEY idx_space_author (space_id, author_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '回忆录条目';

CREATE TABLE IF NOT EXISTS tag (
    id         BIGINT      NOT NULL,
    space_id   BIGINT      NOT NULL,
    name       VARCHAR(30) NOT NULL,
    color      VARCHAR(20) DEFAULT NULL,
    created_at DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_space_name (space_id, name)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '标签';

CREATE TABLE IF NOT EXISTS memoir_person (
    id        BIGINT NOT NULL,
    memoir_id BIGINT NOT NULL,
    person_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_memoir_person (memoir_id, person_id),
    KEY idx_person (person_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '回忆与人物关联';

CREATE TABLE IF NOT EXISTS memoir_tag (
    id        BIGINT NOT NULL,
    memoir_id BIGINT NOT NULL,
    tag_id    BIGINT NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_memoir_tag (memoir_id, tag_id),
    KEY idx_tag (tag_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '回忆与标签关联';

CREATE TABLE IF NOT EXISTS media (
    id          BIGINT       NOT NULL,
    space_id    BIGINT       NOT NULL,
    memoir_id   BIGINT       DEFAULT NULL COMMENT '可为空，先入媒体库后引用',
    type        VARCHAR(10)  NOT NULL COMMENT 'IMAGE / VIDEO / AUDIO',
    storage_key VARCHAR(500) NOT NULL COMMENT 'OSS object key',
    url         VARCHAR(500) DEFAULT NULL,
    thumb_url   VARCHAR(500) DEFAULT NULL,
    mime_type   VARCHAR(100) DEFAULT NULL,
    size        BIGINT       NOT NULL DEFAULT 0,
    width       INT          DEFAULT NULL,
    height      INT          DEFAULT NULL,
    duration    INT          DEFAULT NULL,
    exif_time   DATETIME     DEFAULT NULL COMMENT '拍摄时间',
    uploaded_by BIGINT       NOT NULL,
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted     TINYINT      NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    KEY idx_space_created (space_id, created_at),
    KEY idx_memoir (memoir_id),
    KEY idx_storage_key (storage_key)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '媒体文件';
