CREATE TRIGGER update_checkin_count
AFTER INSERT ON checkin
FOR EACH ROW
UPDATE user
SET checkin_count = checkin_count+1
WHERE username = NEW.username;

delimiter //

CREATE TRIGGER Welcome_Achievement_Before
BEFORE INSERT ON user
FOR EACH ROW
BEGIN
	SET NEW.score = 
    (
		SELECT points
        FROM achievement
        WHERE id = "welcome"
    );
END//

delimiter ;

DROP TRIGGER Welcome_Achievement;

delimiter //

CREATE TRIGGER Welcome_Achievement_After
AFTER INSERT ON user
FOR EACH ROW
BEGIN
    INSERT INTO achieve
    VALUES(NEW.username, "welcome");
END//

delimiter ;


DROP TRIGGER welcome_achievement;


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
	WHERE username_from = NEW.username_from
) = 10 AND check_achievement(NEW.username_from, "serial_stalker")
THEN
CALL attain_achievement(NEW.username_from, "serial_stalker");
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
	WHERE username_from = NEW.username_from
) = 1 AND check_achievement(NEW.username_from, "stalker")
THEN
CALL attain_achievement(NEW.username_from, "stalker");
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



delimiter //

CREATE TRIGGER recent_checkin_polygon_achievement
AFTER INSERT ON checkin
FOR EACH ROW
BEGIN
	DECLARE CITY_THRESHOLD INT DEFAULT 5000;
	
	DECLARE done BOOLEAN DEFAULT FALSE;
    DECLARE beginning POINT;
    DECLARE p POINT;
    DECLARE poly VARCHAR(255) DEFAULT 'POLYGON((';
    DECLARE count INT DEFAULT 0;
        
	DECLARE cur CURSOR FOR
    (
		SELECT DISTINCT location
		FROM city, checkin
		WHERE id = city_id AND checkin.username = NEW.username
		ORDER BY checkin_time DESC
		LIMIT 0,15
	);
    
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = 1;
	
    OPEN cur;
	
	read_loop: LOOP
		IF count = 0 THEN
			FETCH cur INTO beginning;
            
            IF done THEN
				LEAVE read_loop;
            ELSE
				SET p = beginning;
			END IF;
		ELSE
			FETCH cur INTO p;
		END IF;
        
		IF done THEN
			LEAVE read_loop;
		END IF;
        
        SET poly = CONCAT(poly, p.STX, ' ', p.STY, ', ');
        
        SET count = count + 1;
	END LOOP;
    
    SET poly = CONCAT(poly, beginning.STX, ' ', beginning.STY, '))');
    #Contains(GeomFromText('POLYGON((41.000497 -109.050149, 41.002380 -102.051881, 36.993237 -102.041959, 36.999037 -109.045220, 41.000497 -109.050149))'), location);
	
	IF
	(
		SELECT COUNT(*)
        FROM city
        WHERE Contains(GeomFromText(poly), location)
	) >= CITY_THRESHOLD THEN
	CALL attain_achievement(NEW.username, "recent_checkin_polygon");
	END IF;
    
	CLOSE cur;
END; //

delimiter ;

DROP TRIGGER recent_checkin_polygon_achievement;