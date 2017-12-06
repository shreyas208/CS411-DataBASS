CREATE TRIGGER update_checkin_count
AFTER INSERT ON checkin
FOR EACH ROW
UPDATE user
SET checkin_count = checkin_count+1
WHERE username = NEW.username;

delimiter //

CREATE TRIGGER Welcome_Achievement
AFTER INSERT ON user
FOR EACH ROW
BEGIN
	UPDATE user
    SET score = score +
    (
		SELECT points
        FROM achievement
        WHERE id = "welcome"
    )
    WHERE username = NEW.username;
    
    INSERT INTO achieve
    VALUES(NEW.username, "welcome");
END//

delimiter ;

delimiter //

CREATE TRIGGER five_stars_achievement
AFTER INSERT ON checkin
FOR EACH ROW
BEGIN
IF
(
	SELECT COUNT(*)
	FROM checkin
	WHERE username = NEW.username
) = 5 THEN
CALL attain_achievement(NEW.username, "5_stars");
END IF;
END;//

delimiter ;

delimiter //

CREATE TRIGGER five_stars_achievement
AFTER INSERT ON checkin
FOR EACH ROW
BEGIN
IF
(
	SELECT COUNT(*)
	FROM checkin
	WHERE username = NEW.username
) = 5 THEN
CALL attain_achievement(NEW.username, "5_stars");
END IF;
END; //
delimiter ;


delimiter //

CREATE TRIGGER frequent_traveler
AFTER INSERT ON checkin
FOR EACH ROW
BEGIN
IF
(
	SELECT COUNT(DISTINCT city_id) AS unique_cities
	FROM checkin
	WHERE username = NEW.username
) = 10 THEN
CALL attain_achievement(NEW.username, "frequent_traveler");
END IF;
END; //
delimiter ;


delimiter //

CREATE TRIGGER just_getting_started
AFTER INSERT ON checkin
FOR EACH ROW
BEGIN
IF
(
	SELECT COUNT(*)
	FROM checkin
	WHERE username = NEW.username
) = 1 THEN
CALL attain_achievement(NEW.username, "just_getting_started");
END IF;
END; //
delimiter ;


delimiter //

CREATE TRIGGER nomad
AFTER INSERT ON checkin
FOR EACH ROW
BEGIN
IF
(
	SELECT COUNT(DISTINCT city_id) AS unique_cities
	FROM checkin
	WHERE username = NEW.username
) = 20 THEN
CALL attain_achievement(NEW.username, "nomad");
END IF;
END; //
delimiter ;

delimiter //

CREATE TRIGGER no_more_rookie_numbers
AFTER INSERT ON checkin
FOR EACH ROW
BEGIN
IF
(
	SELECT COUNT(*)
	FROM checkin
	WHERE username = NEW.username
) = 50 THEN
CALL attain_achievement(NEW.username, "no_more_rookie_numbers");
END IF;
END; //
delimiter ;


delimiter //

CREATE TRIGGER on_your_way
AFTER INSERT ON checkin
FOR EACH ROW
BEGIN
IF
(
	SELECT COUNT(*)
	FROM checkin
	WHERE username = NEW.username
) = 10 THEN
CALL attain_achievement(NEW.username, "on_your_way");
END IF;
END; //
delimiter ;


delimiter //

CREATE TRIGGER serial_stalker
AFTER INSERT ON follow
FOR EACH ROW
BEGIN
IF
(
	SELECT COUNT(*)
	FROM follow
	WHERE username_follower = NEW.username_follower
) = 10 THEN
CALL attain_achievement(NEW.username_follower, "serial_stalker");
END IF;
END; //
delimiter ;



delimiter //

CREATE TRIGGER stalker
AFTER INSERT ON follow
FOR EACH ROW
BEGIN
IF
(
	SELECT COUNT(*)
	FROM follow
	WHERE username_follower = NEW.username_follower
) = 1 THEN
CALL attain_achievement(NEW.username_follower, "stalker");
END IF;
END; //
delimiter ;


delimiter //

CREATE TRIGGER you_get_around
AFTER INSERT ON checkin
FOR EACH ROW
BEGIN
IF
(
	SELECT COUNT(DISTINCT city_id) AS unique_cities
	FROM checkin
	WHERE username = NEW.username
) = 5 THEN
CALL attain_achievement(NEW.username, "you_get_around");
END IF;
END; //
delimiter ;
