DROP DATABASE IF EXISTS sentinel;
CREATE DATABASE sentinel;
USE sentinel;

DROP TABLE IF EXISTS notification_type;
CREATE TABLE notification_type (
	id int NOT NULL AUTO_INCREMENT PRIMARY KEY,
	name text
);

DROP TABLE IF EXISTS device;
CREATE TABLE device (
	id int NOT NULL AUTO_INCREMENT PRIMARY KEY,
	created timestamp NOT NULL,
	name text,
	api_key text
);

DROP TABLE IF EXISTS image;
CREATE TABLE image (
	id int NOT NULL AUTO_INCREMENT PRIMARY KEY,
	image_data text
);

DROP TABLE IF EXISTS notification;
CREATE TABLE notification (
	id int NOT NULL AUTO_INCREMENT PRIMARY KEY,
	created timestamp NOT NULL,
	message text,
	device_id int NOT NULL,
	notification_type int NOT NULL,
	image_id int,
	constraint `fk_device`	foreign key(device_id) references device(id),
	constraint `fk_notification`	foreign key(notification_type) references notification_type(id),
	constraint `fk_image`	foreign key(image_id) references image(id)
);

INSERT INTO notification_type(name)
VALUES ('ALERT'), ('MOTION DETECT'), ('INFO');

INSERT INTO device(created, name, api_key )
VALUES (now(), 'TEST_DEVICE', 'test123');

INSERT INTO notification(created, message, device_id, notification_type)
VALUES
("2020-08-31 07:00:00", 'test message1', 1, 3),
("2020-08-31 07:10:00", 'test message2', 1, 3),
("2020-08-31 07:20:00", 'test message3', 1, 1),
("2020-08-31 07:30:00", 'test message4', 1, 2),
("2020-08-31 07:40:00", 'test message5', 1, 3),
("2020-08-31 07:50:00", 'test message6', 1, 1),
("2020-08-31 07:55:00", 'test message7', 1, 2),
("2020-08-31 07:56:00", 'test message8', 1, 3),
("2020-08-31 07:57:00", 'test message9', 1, 3),
("2020-08-31 07:58:00", 'test message10', 1, 3)
;