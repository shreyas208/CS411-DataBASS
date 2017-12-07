# Latest checkin query
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



# Old checkin queries

SELECT *
FROM
(
	(
		SELECT *, (3959 * acos(cos(radians(40.110588)) * cos(radians(latitude)) * 
		cos(radians(longitude) - radians(-88.207270)) + sin(radians(40.110588)) * 
		sin(radians(latitude)))) AS distance 
		FROM city 
		HAVING distance < 5 
		ORDER BY population DESC 
		LIMIT 0, 1 
	) 
	UNION 
	( 
		SELECT *, (3959 * acos( cos(radians(40.110588)) * cos(radians(latitude)) * 
		cos(radians(longitude) - radians(-88.207270)) + sin(radians(40.110588)) * 
		sin(radians(latitude)))) AS distance 
		FROM city 
		HAVING distance < 5 
		ORDER BY distance 
		LIMIT 0, 1 
	) 
) AS distpop 
ORDER BY population DESC, distance ASC 
LIMIT 0,1;



SELECT *
FROM
(
	(
		SELECT *, (3959 * acos(cos(radians(40.110588)) * cos(radians(latitude)) * 
		cos(radians(longitude) - radians(-88.207270)) + sin(radians(40.110588)) * 
		sin(radians(latitude)))) AS distance 
		FROM city 
		HAVING distance < 5 
		ORDER BY population DESC 
		LIMIT 0, 1 
	) 
	UNION 
	( 
		SELECT *, (3959 * acos( cos(radians(40.110588)) * cos(radians(latitude)) * 
		cos(radians(longitude) - radians(-88.207270)) + sin(radians(40.110588)) * 
		sin(radians(latitude)))) AS distance 
		FROM city 
		HAVING distance < 5 
		ORDER BY distance 
		LIMIT 0, 1 
	) 
) AS distpop 
ORDER BY distance ASC, population DESC 
LIMIT 0,2