CREATE TABLE Users(
    UserName varchar(50),
    JoinDate date NOT NULL,
    Password VARCHAR(50),
    PRIMARY KEY(username)
    );

CREATE TABLE city (
	id INT NOT NULL,
    country VARCHAR(2) NOT NULL,
    name VARCHAR(256) NOT NULL,
    region VARCHAR(2) NOT NULL,
    population INT,
    latitude DECIMAL(6, 4) NOT NULL,
    longitude FLOAT(7, 4) NOT NULL,
    PRIMARY KEY(id)
);

CREATE TABLE checkin (
	username VARCHAR(50),
    city_id INT NOT NULL,
    checkin_time TIMESTAMP,
    PRIMARY KEY(username, city_id, checkin_time)
);

CREATE TABLE follow(
	username_follower VARCHAR(50),
    username_followee VARCHAR(50),
    PRIMARY KEY(username_follower, username_followee)
);