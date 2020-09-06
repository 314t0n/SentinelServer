DROP DATABASE IF EXISTS sentinel;
CREATE DATABASE sentinel;
USE sentinel;

DROP TABLE IF EXISTS user_role;
CREATE TABLE user_role (
	id int NOT NULL AUTO_INCREMENT PRIMARY KEY,
	name text NOT NULL
);

DROP TABLE IF EXISTS user;
CREATE TABLE user (
	id int NOT NULL AUTO_INCREMENT PRIMARY KEY,
	created timestamp NOT NULL,
	email text NOT NULL,
	password text NOT NULL,
	active boolean NOT NULL,
	user_role_id int NOT NULL,
	constraint `fk_user_role` foreign key(user_role_id) references user_role(id)
);

DROP TABLE IF EXISTS session;
CREATE TABLE session (
	id int NOT NULL AUTO_INCREMENT PRIMARY KEY,
	session_id text NOT NULL,
	created timestamp NOT NULL,
	expired_at timestamp NOT NULL,
	user_id int NOT NULL,
	constraint `fk_user_session` foreign key(user_id) references user(id)
);

DROP TABLE IF EXISTS notification_type;
CREATE TABLE notification_type (
	id int NOT NULL AUTO_INCREMENT PRIMARY KEY,
	name text NOT NULL
);

DROP TABLE IF EXISTS device;
CREATE TABLE device (
	id int NOT NULL AUTO_INCREMENT PRIMARY KEY,
	created timestamp NOT NULL,
	name text NOT NULL,
	api_key text NOT NULL,
	user_id int NOT NULL,
	constraint `fk_user_id` foreign key(user_id) references user(id)
);

DROP TABLE IF EXISTS notification;
CREATE TABLE notification (
	id int NOT NULL AUTO_INCREMENT PRIMARY KEY,
	created timestamp NOT NULL,
	message text,
	device_id int NOT NULL,
	notification_type int NOT NULL,
	image_base64 text,
	constraint `fk_device`	foreign key(device_id) references device(id),
	constraint `fk_notification`	foreign key(notification_type) references notification_type(id)
);

INSERT INTO user_role(name)
VALUES ('ADMIN'), ('USER');

INSERT INTO user(email, password, active, created, user_role_id)
VALUES ('test@elek.bl', "", true, "2020-08-31 07:00:11", 1),
('test@bela.bl', "", true, "2020-08-31 07:00:11", 2);

INSERT INTO session(session_id, created, expired_at, user_id)
VALUES ('eyboss', "2020-08-31 07:00:11", "2020-07-31 07:00:11", 1),
('eyboss123', "2020-08-31 07:00:11", "2025-08-31 07:00:11", 2);

INSERT INTO notification_type(name)
VALUES ('ALERT'), ('MOTION DETECT'), ('INFO');

INSERT INTO device(created, name, api_key, user_id)
VALUES ("2020-08-31 07:00:10", 'TEST_DEVICE1', 'test1', 1),
("2020-08-31 07:00:11", 'TEST_DEVICE2', 'test1', 1),
("2020-08-31 07:00:12", 'TEST_DEVICE3', 'test1', 1),
("2020-08-31 07:00:13", 'TEST_DEVICE4', 'test1', 1),
("2020-08-31 07:00:14", 'TEST_DEVICE5', 'test1', 1),
("2020-08-31 07:00:40", 'TEST_DEVICE6', 'test1', 1),
("2020-08-31 07:50:50", 'TEST_DEVICE7', 'test1', 1),
("2020-08-31 07:55:50", 'TEST_DEVICE8', 'test1', 1),
("2020-08-31 07:56:20", 'TEST_DEVICE88', 'test1', 2),
("2020-08-31 07:57:30", 'TEST_DEVICE9', 'test123', 2);

INSERT INTO notification(created, message, device_id, notification_type)
VALUES
("2020-08-31 07:00:00", 'test message1', 1, 3),
("2020-08-31 07:10:00", 'test message2', 2, 2),
("2020-08-31 07:20:00", 'test message3', 3, 1),
("2020-08-31 07:30:00", 'test message4', 4, 2),
("2020-08-31 07:40:00", 'test message5', 5, 3),
("2020-08-31 07:50:00", 'test message6', 6, 1),
("2020-08-31 07:55:00", 'test message7', 1, 2),
("2020-08-31 07:56:00", 'test message8', 1, 3),
("2020-08-31 07:57:00", 'test message9', 1, 2),
("2020-08-31 07:58:00", 'test message10', 1, 3)
;