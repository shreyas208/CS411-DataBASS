delimiter //

CREATE PROCEDURE query_checkin(IN lat DECIMAL(6,4), IN lon FLOAT(7,4))
BEGIN
	SET @CONSTANT = 3959, @DISTANCE_THRESHOLD = 3;
    
	SELECT *
	FROM
	(
		(
			SELECT *, (@CONSTANT * acosacos(cos(radians(lat)) * cos(radians(latitude)) *
			cos(radians(longitude) - radians(lon)) + sin(radians(lat)) *
			sin(radians(latitude)))) AS distance
			FROM city
			HAVING distance < @DISTANCE_THRESHOLD
			ORDER BY population DESC
			LIMIT 0, 1
		)
		UNION
		(
			SELECT *, (@CONSTANT * acos(cos(radians(lat)) * cos(radians(latitude)) *
			cos(radians(longitude) - radians(lon)) + sin(radians(lat)) *
			sin(radians(latitude)))) AS distance
			FROM city
			HAVING distance < @DISTANCE_THRESHOLD
			ORDER BY distance
			LIMIT 0, 1
		)
	) AS distpop
	ORDER BY distance ASC, population DESC
    LIMIT 0, 2;
END//

delimiter ;

DROP PROCEDURE query_checkin;



delimiter //

CREATE PROCEDURE attain_achievement(IN username VARCHAR(255), IN achievement_name VARCHAR(255))
BEGIN
	UPDATE user U
    SET score = score +
    (
		SELECT points
        FROM achievement
        WHERE id = achievement_name
    )
    WHERE U.username = username;
    
    INSERT INTO achieve
    VALUES(username, achievement_name);
END//

delimiter ;

DROP PROCEDURE attain_achievement;