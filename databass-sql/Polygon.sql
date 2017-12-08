# This is a rough draft of an achievement that never made it into the database.
# Nothing in this file is actually implemented on the database.

delimiter //

CREATE TRIGGER traveling_far_and_wide_achievement
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
	CALL attain_achievement(NEW.username, "traveling_far_and_wide");
	END IF;
    
	CLOSE cur;
END; //

delimiter ;

DROP TRIGGER traveling_far_and_wide_achievement;



SELECT *
FROM city
WHERE
Contains(GeomFromText('POLYGON((41.000497 -109.050149, 41.002380 -102.051881, 36.993237 -102.041959, 36.999037 -109.045220, 41.000497 -109.050149))'), location);



SELECT *
FROM city
WHERE id = 2923645;



SET @minLat = -10;
SET @maxLat = 10;
SET @minLong = -10;
SET @maxLong = 10;

SELECT id
FROM
(
	SELECT *
    FROM
    (
		SELECT id, latitude, longitude
        FROM city
        WHERE latitude
        BETWEEN @minLat AND @maxLat
	) AS city_tmp
	WHERE longitude
    BETWEEN @minLong AND @maxLong
) AS polygon
WHERE;



delimiter //

CREATE FUNCTION onSegment(p Point, q Point, r Point)
RETURNS BOOLEAN DETERMINISTIC
BEGIN
	IF (q.STX <= max(p.STX, r.STX) AND q.STX >= min(p.STX, r.STX) AND q.STY <= max(p.STY, r.STY) AND q.STY >= min(p.STY, r.STY)) THEN
	RETURN TRUE;
	ELSE
    RETURN FALSE;
	END IF;
END//

delimiter ;