DROP TABLE IF EXISTS tb_thumbnail;
DROP TABLE IF EXISTS tb_file;
DROP TABLE IF EXISTS comment;
DROP TABLE IF EXISTS tb_inquiry;
DROP TABLE IF EXISTS tb_notice;
DROP TABLE IF EXISTS tb_post;
DROP TABLE IF EXISTS tb_sub_code;
DROP TABLE IF EXISTS tb_code_group;
DROP TABLE IF EXISTS tb_manager;
DROP TABLE IF EXISTS tb_user;

CREATE TABLE `tb_user` (
    `user_idx` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `id` VARCHAR(50) NOT NULL,
    `password` VARBINARY(255) NOT NULL,
    `name` VARCHAR(15) NOT NULL,
    `nickname` VARCHAR(50) NOT NULL,
    UNIQUE (`id`),
    UNIQUE (`nickname`)
);

CREATE TABLE `tb_manager` (
    `manager_idx` INT PRIMARY KEY AUTO_INCREMENT,
    `id` VARCHAR(50) NOT NULL,
    `password` VARBINARY(255) NOT NULL,
    `nickname` VARCHAR(50) NOT NULL,
    UNIQUE (`id`),
    UNIQUE (`nickname`)
);

CREATE TABLE `tb_code_group` (
    `code_group_idx` INT PRIMARY KEY AUTO_INCREMENT,
    `code` VARCHAR(20) NOT NULL,
    `code_name` VARCHAR(100) NOT NULL,
    `description` VARCHAR(500) NOT NULL,
    UNIQUE (`code`),
    UNIQUE (`code_name`)
);

CREATE TABLE `tb_post` (
    `post_idx` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `sub_code_idx` INT NOT NULL,
    `title` VARCHAR(200) NOT NULL,
    `user_idx` BIGINT NOT NULL,
    `reg_date` DATETIME NOT NULL,
    `mod_date` DATETIME NOT NULL,
    `content` VARCHAR(2000) NOT NULL,
    `view_count` INT NOT NULL,
    `like_count` INT NOT NULL,
    `dislike_count` INT NOT NULL,
	CONSTRAINT `FK_TB_POST_USER` FOREIGN KEY (`user_idx`) REFERENCES `tb_user` (`user_idx`)
);

CREATE TABLE `tb_notice` (
    `notice_idx` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `sub_code_idx` INT NOT NULL,
    `title` VARCHAR(200) NOT NULL,
    `manager_idx` INT NOT NULL,
    `reg_date` DATETIME NOT NULL,
    `mod_date` DATETIME NOT NULL,
    `content` VARCHAR(2000) NOT NULL,
    `view_count` INT NOT NULL,
    `like_count` INT NOT NULL,
    `dislike_count` INT NOT NULL,
	CONSTRAINT `FK_TB_NOTICE_MANAGER` FOREIGN KEY (`manager_idx`) REFERENCES `tb_manager` (`manager_idx`)
);

CREATE TABLE `tb_sub_code` (
    `sub_code_idx` INT PRIMARY KEY AUTO_INCREMENT,
    `code` VARCHAR(20) NOT NULL,
    `code_name` VARCHAR(100) NOT NULL,
    `description` VARCHAR(200) NOT NULL,
    `code_group_idx` INT NOT NULL,
    UNIQUE (`code`),
    UNIQUE (`code_name`),
	CONSTRAINT `FK_TB_SUB_CODE_CODE_GROUP` FOREIGN KEY (`code_group_idx`) REFERENCES `tb_code_group` (`code_group_idx`)
);

CREATE TABLE `tb_inquiry` (
    `inquiry_idx` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `post_idx` BIGINT NOT NULL,
    `secret` BOOLEAN NOT NULL,
    `answer_completed` BOOLEAN NOT NULL,
    `manager_idx` INT NULL,
    `password` VARBINARY(255) NULL,
    `answer_content` VARCHAR(2000) NULL,
    `answer_reg_date` DATETIME NULL,
	CONSTRAINT `FK_TB_INQUIRY_POST` FOREIGN KEY (`post_idx`) REFERENCES `tb_post` (`post_idx`),
  	CONSTRAINT `FK_TB_INQUIRY_MANAGER` FOREIGN KEY (`manager_idx`) REFERENCES `tb_manager` (`manager_idx`)
);

CREATE TABLE `comment` (
    `comment_idx` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `user_idx` BIGINT NOT NULL,
    `post_idx` BIGINT NOT NULL,
    `content` VARCHAR(2000) NOT NULL,
    `reg_date` DATETIME NOT NULL,
	    CONSTRAINT `FK_COMMENT_USER` FOREIGN KEY (`user_idx`) REFERENCES `tb_user` (`user_idx`),
	    CONSTRAINT `FK_COMMENT_POST` FOREIGN KEY (`post_idx`) REFERENCES `tb_post` (`post_idx`)
);

CREATE TABLE `tb_file` (
    `file_idx` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `saved_name` VARCHAR(255) NOT NULL,
    `original_name` VARCHAR(500) NOT NULL,
    `extension` VARCHAR(10) NOT NULL,
    `size` INT NOT NULL,
    `post_idx` BIGINT NOT NULL,
	CONSTRAINT `FK_TB_FILE_POST` FOREIGN KEY (`post_idx`) REFERENCES `tb_post` (`post_idx`)
);

CREATE TABLE `tb_thumbnail` (
    `thumbnail_idx` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `post_idx` BIGINT NOT NULL,
    `file_idx` BIGINT NOT NULL,
    CONSTRAINT `FK_TB_THUMBNAIL_POST` FOREIGN KEY (`post_idx`) REFERENCES `tb_post` (`post_idx`),
    CONSTRAINT `FK_TB_THUMBNAIL_FILE` FOREIGN KEY (`file_idx`) REFERENCES `tb_file` (`file_idx`)
);


ALTER TABLE `tb_post`
ADD CONSTRAINT `FK_TB_POST_SUB_CODE`
FOREIGN KEY (`sub_code_idx`) REFERENCES `tb_sub_code` (`sub_code_idx`);

ALTER TABLE `tb_notice`
ADD CONSTRAINT `FK_TB_NOTICE_SUB_CODE`
FOREIGN KEY (`sub_code_idx`) REFERENCES `tb_sub_code` (`sub_code_idx`);

