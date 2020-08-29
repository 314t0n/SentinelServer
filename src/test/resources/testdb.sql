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
(now(), 'test message1', 1, 3),
(now(), 'test message2', 1, 3),
(now(), 'test message3', 1, 3),
(now(), 'test message4', 1, 3),
(now(), 'test message5', 1, 3)
;