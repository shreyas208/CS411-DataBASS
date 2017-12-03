ALTER TABLE city ENGINE=MyISAM;

ALTER TABLE city ADD location POINT;

UPDATE city
SET city.location = ST_PointFromText(CONCAT('POINT(', CAST(city.latitude AS CHAR), ' ', CAST(city.longitude AS CHAR), ')'));

ALTER TABLE city ADD SPATIAL INDEX(location);



SELECT *
FROM
(
	(
		SELECT *, (3959 * acos(cos(radians(48.864716)) * cos(radians(latitude)) *
        cos(radians(longitude) - radians(2.349014)) + sin(radians(48.864716)) *
        sin(radians(latitude)))) AS distance
        FROM
        (
			SELECT *
            FROM city
            WHERE ST_Distance(ST_PointFromText('POINT(48.864716 2.349014)'), location) < 0.05
		) AS city_tmp
        HAVING distance < 3
        ORDER BY population DESC
        LIMIT 0, 1
	)
	UNION
	(
		SELECT *, (3959 * acos(cos(radians(48.864716)) * cos(radians(latitude)) *
        cos(radians(longitude) - radians(2.349014)) + sin(radians(48.864716)) *
        sin(radians(latitude)))) AS distance
        FROM
        (
			SELECT *
            FROM city
            WHERE ST_Distance(ST_PointFromText('POINT(48.864716 2.349014)'), location) < 0.05
		) AS city_tmp
        HAVING distance < 3
        ORDER BY distance
        LIMIT 0, 1
	)
) AS distpop
ORDER BY distance ASC, population DESC
LIMIT 0,2;

title VARCHAR()
description VARCHAR()
point_total INT


SELECT ST_PointFromText('POINT(0.0 0.0)');

SET @poly = 'POLYGON((48.985287 1.966553, 49.039331 2.664185, 48.670673 2.686157, 48.634383 1.873169))';
SELECT * FROM city_test WHERE ST_GeomFromText('POLYGON((48.985287 1.966553, 49.039331 2.664185, 48.670673 2.686157, 48.634383 1.873169))'), location);

SELECT *, ST_Distance(ST_PointFromText('POINT(48.864716 2.349014)'), location) FROM city_test WHERE name='paris';
SELECT * FROM city_test WHERE ST_Distance(ST_PointFromText('POINT(48.864716 2.349014)'), location) < 0.05;


SELECT *
FROM
(
	(
		SELECT *, (3959 * acos(cos(radians(48.864716)) * cos(radians(latitude)) *
        cos(radians(longitude) - radians(2.349014)) + sin(radians(48.864716)) *
        sin(radians(latitude)))) AS distance
        FROM city_test
        HAVING distance < 3
        ORDER BY population DESC
        LIMIT 0, 1
	)
	UNION
	(
		SELECT *, (3959 * acos(cos(radians(48.864716)) * cos(radians(latitude)) *
        cos(radians(longitude) - radians(2.349014)) + sin(radians(48.864716)) *
        sin(radians(latitude)))) AS distance
        FROM city_test
        HAVING distance < 3
        ORDER BY distance
        LIMIT 0, 1
	)
) AS distpop
ORDER BY distance ASC, population DESC
LIMIT 0,2;