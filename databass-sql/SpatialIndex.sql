# The preliminary steps to querying by a spacial index
ALTER TABLE city ADD location POINT;

UPDATE city
SET city.location = ST_PointFromText(CONCAT('POINT(', CAST(city.latitude AS CHAR), ' ', CAST(city.longitude AS CHAR), ')'));

ALTER TABLE city ADD SPATIAL INDEX(location);

# We had to use this command before we decided to upgrade our Mariadb database server instead to maintain the features we wanted like foreign keys.
ALTER TABLE city ENGINE=MyISAM;


# The checkin query we did by spatial index
SELECT *
FROM
(
	(
		SELECT *, (3959 * acos(cos(radians(48.864716)) * cos(radians(latitude)) * cos(radians(longitude) - radians(2.349014)) + sin(radians(48.864716)) * sin(radians(latitude)))) AS distance
		FROM
		(
			SELECT *
            FROM city
            WHERE ST_Distance(ST_PointFromText('POINT(48.864716 2.349014)'), location) < 0.05
		) as city_tmp
		HAVING distance < 3
		ORDER BY population DESC
		LIMIT 0, 1
	)
	UNION
	(
		SELECT *, (3959 * acos(cos(radians(48.864716)) * cos(radians(latitude)) * cos(radians(longitude) - radians(2.349014)) + sin(radians(48.864716)) * sin(radians(latitude)))) AS distance
		FROM
		(
			SELECT *
			FROM city
			WHERE ST_Distance(ST_PointFromText('POINT(48.864716 2.349014)'), location) < 0.05
		) as city_tmp
		HAVING distance < 3
		ORDER BY distance
		LIMIT 0, 1
	)
) AS distpop
ORDER BY distance ASC, population DESC
LIMIT 0,2;


# Our old checkin query
SELECT *
FROM
(
	(
		SELECT *, (3959 * acos(cos(radians(48.864716)) * cos(radians(latitude)) * cos(radians(longitude) - radians(2.349014)) + sin(radians(48.864716)) * sin(radians(latitude)))) AS distance
        FROM city
        HAVING distance < 3
        ORDER BY population DESC
        LIMIT 0, 1
	)
	UNION
	(
		SELECT *, (3959 * acos(cos(radians(48.864716)) * cos(radians(latitude)) * cos(radians(longitude) - radians(2.349014)) + sin(radians(48.864716)) * sin(radians(latitude)))) AS distance
        FROM city
        HAVING distance < 3
        ORDER BY distance
        LIMIT 0, 1
	)
) AS distpop
ORDER BY distance ASC, population DESC
LIMIT 0,2;


# Miscellaneous Tests on the spatial index
SELECT ST_PointFromText('POINT(0.0 0.0)');

SET @poly = 'POLYGON((48.985287 1.966553, 49.039331 2.664185, 48.670673 2.686157, 48.634383 1.873169))';
SELECT * FROM city WHERE ST_GeomFromText('POLYGON((48.985287 1.966553, 49.039331 2.664185, 48.670673 2.686157, 48.634383 1.873169))'), location);

SELECT *, ST_Distance(ST_PointFromText('POINT(48.864716 2.349014)'), location) FROM city WHERE name='paris';
SELECT * FROM city WHERE ST_Distance(ST_PointFromText('POINT(48.864716 2.349014)'), location) < 0.05;

// BETWEEN + gcdist stored function
SET @mylat = 35.689487;
SET @mylong = 139.691706;
SET @latbuffer = 0.1;
SELECT *, gcdist(@mylat, @mylong, latitude, longitude) AS distance_mi
FROM city
WHERE latitude BETWEEN (@mylat - @latbuffer) AND (@mylat + @latbuffer) HAVING distance_mi < 5 ORDER BY distance_mi;

// single closest city
SET @mylat = 51.5141;
SET @mylong = -0.0937;
SET @latbuffer = 0.1;
SELECT *, gcdist(@mylat, @mylong, latitude, longitude) AS distance_mi FROM city WHERE latitude BETWEEN (@mylat - @latbuffer) AND (@mylat + @latbuffer) HAVING distance_mi < 5
ORDER BY distance_mi LIMIT 0,1;

// (single closest city) UNION (closest city with non-zero population)

SET @mylat = 51.507351;
SET @mylong = -0.127758;
SET @latbuffer = 0.1;
(
	SELECT *
    FROM
    (
		SELECT *, gcdist(@mylat, @mylong, latitude, longitude) AS distance_mi
        FROM city
        WHERE latitude
        BETWEEN (@mylat - @latbuffer) AND (@mylat + @latbuffer)
        HAVING distance_mi < 5
        ORDER BY distance_mi
	) AS city_tmp
	WHERE population > 0
	ORDER BY distance_mi
	LIMIT 0,1
)
UNION
(
	SELECT *
    FROM
    (
		SELECT *, gcdist(@mylat, @mylong, latitude, longitude) AS distance_mi
        FROM city
        WHERE latitude BETWEEN (@mylat - @latbuffer) AND (@mylat + @latbuffer)
        HAVING distance_mi < 5
        ORDER BY distance_mi
	) AS city_tmp
	ORDER BY distance_mi
    LIMIT 0,1
);