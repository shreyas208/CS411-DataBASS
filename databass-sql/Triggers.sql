# Increments a user's number of checkins and adds a point to his/her score
# every time a checkin occurs (whenever there is an insertion on the checkin table)
CREATE TRIGGER update_checkin_count_and_score
AFTER INSERT ON checkin
FOR EACH ROW
UPDATE user
SET checkin_count = checkin_count+1, score = score + 1
WHERE username = NEW.username;

DROP TRIGGER update_checkin_count;



# Updates a user's score upon receiving the "Welcome!" achievement
delimiter //

CREATE TRIGGER Welcome_Achievement_Before
BEFORE INSERT ON user
FOR EACH ROW
IF (check_achievement(NEW.username, "welcome") = TRUE) THEN
	BEGIN
		SET NEW.score = 
		(
			SELECT points
			FROM achievement
			WHERE id = "welcome"
		);
	END;
END IF//

delimiter ;

DROP TRIGGER Welcome_Achievement_Before;



# Gives a user an achievement for creating an account (insertion on user table)
# by inserting a record of the user and the "Welcome!" achievement into the achieve table
delimiter //

CREATE TRIGGER Welcome_Achievement_After
AFTER INSERT ON user
FOR EACH ROW
IF (check_achievement(NEW.username, "welcome") = TRUE) THEN
	BEGIN
		INSERT INTO achieve
		VALUES(NEW.username, "welcome");
	END;
END IF//

delimiter ;

DROP TRIGGER Welcome_Achievement_After;



# Gives a user an achievement for checking into 5 cities
delimiter //

CREATE TRIGGER five_stars_achievement
AFTER INSERT ON checkin
FOR EACH ROW
IF (check_achievement(NEW.username, "5_stars") = TRUE) THEN
	IF
	(
		SELECT COUNT(*)
		FROM checkin
		WHERE username = NEW.username
	) = 5 THEN
	CALL attain_achievement(NEW.username, "5_stars");
	END IF;
END IF//

delimiter ;

DROP TRIGGER five_stars_achievement;



# Gives a user an achievement for checking into 10 distinct cities
delimiter //

CREATE TRIGGER frequent_traveler
AFTER INSERT ON checkin
FOR EACH ROW
IF (check_achievement(NEW.username, "frequent_traveler") = TRUE) THEN
	IF
	(
		SELECT COUNT(DISTINCT city_id) AS unique_cities
		FROM checkin
		WHERE username = NEW.username
	) = 10 THEN
	CALL attain_achievement(NEW.username, "frequent_traveler");
	END IF;
END IF//

delimiter ;

DROP TRIGGER frequent_traveler;



# Gives a user an achievement for checking into a city
delimiter //

CREATE TRIGGER just_getting_started
AFTER INSERT ON checkin
FOR EACH ROW
IF (check_achievement(NEW.username, "just_getting_started") = TRUE) THEN
	IF
	(
		SELECT COUNT(*)
		FROM checkin
		WHERE username = NEW.username
	) = 1 THEN
	CALL attain_achievement(NEW.username, "just_getting_started");
	END IF;
END IF//

delimiter ;

DROP TRIGGER just_getting_started;



# Gives a user an achievement for checking into 20 distinct cities
delimiter //

CREATE TRIGGER nomad
AFTER INSERT ON checkin
FOR EACH ROW
IF (check_achievement(NEW.username, "nomad") = TRUE) THEN
	IF
	(
		SELECT COUNT(DISTINCT city_id) AS unique_cities
		FROM checkin
		WHERE username = NEW.username
	) = 20 THEN
	CALL attain_achievement(NEW.username, "nomad");
	END IF;
END IF//

delimiter ;

DROP TRIGGER nomad;



# Gives a user an achievement for checking into 50 cities
delimiter //

CREATE TRIGGER no_more_rookie_numbers
AFTER INSERT ON checkin
FOR EACH ROW
IF (check_achievement(NEW.username, "no_more_rookie_numbers") = TRUE) THEN
	IF
	(
		SELECT COUNT(*)
		FROM checkin
		WHERE username = NEW.username
	) = 50 THEN
	CALL attain_achievement(NEW.username, "no_more_rookie_numbers");
	END IF;
END IF//

delimiter ;

DROP TRIGGER no_more_rookie_numbers;



# Gives a user an achievement for checking into 10 cities
delimiter //

CREATE TRIGGER on_your_way
AFTER INSERT ON checkin
FOR EACH ROW
IF (check_achievement(NEW.username, "on_your_way") = TRUE) THEN
	IF
	(
		SELECT COUNT(*)
		FROM checkin
		WHERE username = NEW.username
	) = 10 THEN
	CALL attain_achievement(NEW.username, "on_your_way");
	END IF;
END IF//

delimiter ;

DROP TRIGGER on_your_way;



# Gives a user an achievement for following 10 users
delimiter //

CREATE TRIGGER serial_stalker
AFTER INSERT ON follow
FOR EACH ROW
IF (check_achievement(NEW.username_from, "serial_stalker") = TRUE) THEN
	IF
	(
		SELECT COUNT(*)
		FROM follow
		WHERE username_from = NEW.username_from
	) = 10 THEN
	CALL attain_achievement(NEW.username_from, "serial_stalker");
	END IF;
END IF//

delimiter ;

DROP TRIGGER serial_stalker;



# Gives a user an achievement for following a user
delimiter //

CREATE TRIGGER stalker
AFTER INSERT ON follow
FOR EACH ROW
IF (check_achievement(NEW.username_from, "stalker") = TRUE) THEN
	IF
	(
		SELECT COUNT(*)
		FROM follow
		WHERE username_from = NEW.username_from
	) = 1 THEN
	CALL attain_achievement(NEW.username_from, "stalker");
	END IF;
END IF//

delimiter ;

DROP TRIGGER stalker;



# Gives a user an achievement for checking into 5 distinct cities
delimiter //

CREATE TRIGGER you_get_around
AFTER INSERT ON checkin
FOR EACH ROW
IF (check_achievement(NEW.username, "you_get_around") = TRUE) THEN
	IF
	(
		SELECT COUNT(DISTINCT city_id) AS unique_cities
		FROM checkin
		WHERE username = NEW.username
	) = 5 THEN
	CALL attain_achievement(NEW.username, "you_get_around");
	END IF;
END IF//

delimiter ;

DROP TRIGGER you_get_around;