# To-Do List:

# 1. Add checks to each function to see if any of the parameters are None/NULL
# 2. Add constraints to display_name and other parameters to avoid SQL Injection (a display name or parameter containing SQL queries)
# 3. Add foreign key constraints to the checkin and follow tables to ensure proper updates and deletions
# 4. Complete the search function
# 5. Update the profile function to only return checkins over the past (week?  3 days?)
# 6. Map country and region codes to their actual names
# 7. Optimize the checkin query (currently takes about 6 secons to run)
# 8. TEST THE FUNTIONALITY EXTENSIVELY!

# -------------------------------------------------------------------------------------------------------------------- #

# Import flask libraries
from flask import Flask, request, jsonify
from flask_api import status
from flask_bcrypt import Bcrypt

# Connects Flask server to MySQL database
import mysql.connector as MySQL

# Generates access tokens
import binascii
import os

# Import libraries for checking the validity of usernames and email addresses
from string import ascii_letters
from string import digits
from string import punctuation
import re

# Used for error checking
import traceback

# -------------------------------------------------------------------------------------------------------------------- #

# Initialize flask app
app = Flask(__name__)
bcrypt = Bcrypt(app)

# Connect to the project database on the VM
db = MySQL.connect(host="localhost",
                   port=3306,
                   user="flaskuser",
                   password="tCU8PvBYEPP4qkun",
                   database="cs_411_project")


# -------------------------------------------------------------------------------------------------------------------- #

# User Registration
@app.route("/api/user/register", methods=["POST"])
def register():
    # Read in registration input parameters
    username = request.form.get('username')  # String (a-z, A-Z, 0-9, -, _)
    password = request.form.get('password')  # String (6 <= characters <= 256)
    email_address = request.form.get('email_address')  # String (valid email)
    display_name = request.form.get('display_name')  # String (1 <= characters <= 265)

    # Check if all the registration input parameters are valid
    check = check_for_none("register", [("username", username),
                                        ("password", password),
                                        ("email", email_address),
                                        ("display_name", display_name)])

    if check is not None:
        return jsonify(check), status.HTTP_400_BAD_REQUEST

    check2 = validate_parameters("register",
                                 username=username,
                                 password=password,
                                 email_address=email_address,
                                 display_name=display_name)

    if check2 is not None:
        return jsonify(check2), status.HTTP_400_BAD_REQUEST

    try:
        cursor = db.cursor()
    except:
        error_code = "connection_to_database_failed"

        content = {"success": False, "error_code": error_code}
        print(traceback.format_exc())
        return jsonify(content), status.HTTP_500_INTERNAL_SERVER_ERROR

    # Check if the username is new
    cursor.execute("SELECT username FROM user WHERE username=%s;", (username,))
    result = cursor.fetchone()

    if result:
        error_code = "user_register_username_in_use"
        cursor.close()

        content = {"success": False, "error_code": error_code}
        return jsonify(content), status.HTTP_400_BAD_REQUEST

    # If this line of the register() function is reached,
    # all the registration input parameters are valid.

    # Insert registration information into user table
    password_hash = bcrypt.generate_password_hash(password)

    cursor.execute("INSERT INTO user values(%s, %s, %s, %s, NOW(), NULL, 0);",
                   (username, email_address, display_name, password_hash))

    db.commit()
    cursor.close()

    content = {"success": True}
    return jsonify(content), status.HTTP_200_OK


# -------------------------------------------------------------------------------------------------------------------- #

# User Login
@app.route("/api/user/login", methods=["POST"])
def login():
    # Read in login input parameters
    username = request.form.get('username')  # String (a-z, A-Z, 0-9, -, _)
    password = request.form.get('password')  # String (6 <= characters <= 256)

    # Check if all the login input parameters are valid
    check = check_for_none("login", [("username", username), ("password", password)])

    if check is not None:
        return jsonify(check), status.HTTP_400_BAD_REQUEST

    check2 = validate_parameters("login", username=username, password=password)

    if check2 is not None:
        return jsonify(check2), status.HTTP_400_BAD_REQUEST

    try:
        cursor = db.cursor()
    except:
        error_code = "connection_to_database_failed"

        content = {"success": False, "error_code": error_code}
        print(traceback.format_exc())
        return jsonify(content), status.HTTP_500_INTERNAL_SERVER_ERROR

    # If this line of the login() function is reached,
    # all the login input parameters are valid.

    # Search user table for password hash to check if the password is correct
    cursor.execute("SELECT email_address, display_name, password_hash FROM user WHERE username=%s;", (username,))
    result = cursor.fetchone()

    # Return a bad login credential error if the username isn't in the table
    if not result:
        error_code = "user_login_bad_credentials"
        cursor.close()

        content = {"success": False, "error_code": error_code}
        return jsonify(content), status.HTTP_400_BAD_REQUEST

    # Check if the input password matches the password hash in the user table and therefore, correct
    is_correct_password = bcrypt.check_password_hash(str(result[2]), password)

    # Return a bad login credential error if the password isn't correct
    if not is_correct_password:
        error_code = "user_login_bad_credentials"
        cursor.close()

        content = {"success": False, "error_code": error_code}
        return jsonify(content), status.HTTP_400_BAD_REQUEST

    email = result[0]
    display_name = result[1]

    # Generate an access token and insert it into the user table
    access_token = binascii.hexlify(os.urandom(32)).decode()

    cursor.execute("UPDATE user SET access_token=%s WHERE username=%s;", (access_token, username))
    db.commit()
    cursor.close()

    content = {"success": True, "email_address": email, "display_name": display_name, "access_token": access_token}
    return jsonify(content), status.HTTP_200_OK


# -------------------------------------------------------------------------------------------------------------------- #

# User Logout
@app.route("/api/user/logout", methods=["POST"])
def logout():
    # Read in logout input parameters
    username = request.form.get('username')  # String (a-z, A-Z, 0-9, -, _)
    access_token = request.form.get('access_token')

    # Check if all the logout input parameters are valid
    check = check_for_none("logout", [("username", username)])

    if check is not None:
        return jsonify(check), status.HTTP_400_BAD_REQUEST

    check2 = validate_parameters("logout", username=username)

    if check2 is not None:
        return jsonify(check2), status.HTTP_400_BAD_REQUEST

    try:
        cursor = db.cursor()
    except:
        error_code = "connection_to_database_failed"

        content = {"success": False, "error_code": error_code}
        print(traceback.format_exc())
        return jsonify(content), status.HTTP_500_INTERNAL_SERVER_ERROR

    # Check if the access token is valid
    cursor.execute("SELECT access_token FROM user WHERE username=%s;", (username,))
    result = cursor.fetchone()

    # Return a bad username error if the username isn't in the table
    if not result:
        error_code = "user_logout_bad_username"
        cursor.close()

        content = {"success": False, "error_code": error_code}
        return jsonify(content), status.HTTP_400_BAD_REQUEST

    elif not (access_token == result[0]):
        error_code = "user_bad_access_token"
        cursor.close()

        content = {"success": False, "error_code": error_code}
        return jsonify(content), status.HTTP_403_FORBIDDEN

    # If this line of the logout() function is reached,
    # all the logout input parameters are valid.

    else:
        cursor.execute("UPDATE user SET access_token=NULL WHERE username=%s;", (username,))
        db.commit()
        cursor.close()

        content = {"success": True}
        return jsonify(content), status.HTTP_200_OK


# -------------------------------------------------------------------------------------------------------------------- #

# Search User
@app.route("/api/user/search", methods=["POST"])
def search():
    # Read in search input parameters
    username = request.form.get('username')  # String (a-z, A-Z, 0-9, -, _)
    search_username = request.form.get('search_username')  # String (a-z, A-Z, 0-9, -, _)
    access_token = request.form.get('access_token')

    # Check if all the search input parameters are valid
    check = check_for_none("search", [("username", username), ("search_username", search_username)])

    if check is not None:
        return jsonify(check), status.HTTP_400_BAD_REQUEST

    check2 = validate_parameters("search", username=username, username2=search_username)

    if check2 is not None:
        return jsonify(check2), status.HTTP_400_BAD_REQUEST

    try:
        cursor = db.cursor()
    except:
        error_code = "connection_to_database_failed"

        content = {"success": False, "error_code": error_code}
        print(traceback.format_exc())
        return jsonify(content), status.HTTP_500_INTERNAL_SERVER_ERROR

    # Check if the access token is valid
    cursor.execute("SELECT access_token FROM user WHERE username=%s;", (username,))
    result = cursor.fetchone()

    # Return a bad username error if the username isn't in the table
    if not result:
        error_code = "user_search_bad_username"
        cursor.close()

        content = {"success": False, "error_code": error_code}
        return jsonify(content), status.HTTP_400_BAD_REQUEST

    elif not (access_token == result[0]):
        error_code = "user_bad_access_token"
        cursor.close()

        content = {"success": False, "error_code": error_code}
        return jsonify(content), status.HTTP_403_FORBIDDEN

    # Find all usernames similar to the provided username
    cursor.execute("SELECT username, display_name FROM user WHERE username LIKE %s;", (search_username + "%",))
    results = cursor.fetchall()
    cursor.close()

    if not results:
        error_code = "user_search_no_results_found"

        content = {"success": False, "error_code": error_code}
        return jsonify(content), status.HTTP_400_BAD_REQUEST

    users = [{"username": result[0], "display_name": result[1]} for result in results]

    content = {"success": True, "results": users}
    return jsonify(content), status.HTTP_200_OK


# -------------------------------------------------------------------------------------------------------------------- #

# User Profile
@app.route("/api/user/profile", methods=["POST"])
def profile():
    # Read in profile input parameters
    username = request.form.get('username')  # String (a-z, A-Z, 0-9, -, _)
    access_token = request.form.get('access_token')

    # Check if all the profile input parameters are valid
    check = check_for_none("profile", [("username", username)])

    if check is not None:
        return jsonify(check), status.HTTP_400_BAD_REQUEST

    check2 = validate_parameters("profile", username=username)

    if check2 is not None:
        return jsonify(check2), status.HTTP_400_BAD_REQUEST

    try:
        cursor = db.cursor()
    except:
        error_code = "connection_to_database_failed"

        content = {"success": False, "error_code": error_code}
        print(traceback.format_exc())
        return jsonify(content), status.HTTP_500_INTERNAL_SERVER_ERROR

    # Check if the access token is valid
    cursor.execute("SELECT access_token FROM user WHERE username=%s;", (username,))
    result = cursor.fetchone()

    # Return a bad username error if the username isn't in the table
    if not result:
        error_code = "user_profile_bad_username"
        cursor.close()

        content = {"success": False, "error_code": error_code}
        return jsonify(content), status.HTTP_400_BAD_REQUEST

    elif not (access_token == result[0]):
        error_code = "user_bad_access_token"
        cursor.close()

        content = {"success": False, "error_code": error_code}
        return jsonify(content), status.HTTP_403_FORBIDDEN

    # If this line of the profile() function is reached,
    # all the profile input parameters are valid.

    cursor.execute("SELECT email_address, display_name, join_date, checkin_count FROM user WHERE username=%s;",
                   (username,))  # query the database for that user
    user_info = cursor.fetchone()

    # we need to get email_address, display_name, join_datetime, checkin_count, and recent_checkins
    email_address = user_info[0]
    display_name = user_info[1]
    join_date = user_info[2]
    checkin_count = user_info[3]

    # The result of this call should be returned as "following_count":
    cursor.execute("SELECT COUNT(*) FROM follow WHERE username_follower=%s", (username,))
    following_count = cursor.fetchone()

    # The result of this call should be returned as "follower_count":
    cursor.execute("SELECT COUNT(*) FROM follow WHERE username_followee=%s", (username,))
    follower_count = cursor.fetchone()

    cursor.execute("SELECT name, checkin_time " +
                   "FROM city, checkin " +
                   "WHERE id = city_id AND username=%s " +
                   "ORDER BY checkin_time DESC " +
                   "LIMIT 0,15;", (username,))
    results = cursor.fetchall()
    cursor.close()

    recent_checkins = [{"city_name": result[0], "checkin_time": result[1]} for result in results]

    content = {"success": True, "email_address": email_address, "display_name": display_name, "join_date": join_date,
               "checkin_count": checkin_count, "recent_checkins": recent_checkins,
               "following_count": following_count[0], "follower_count": follower_count[0]}
    return jsonify(content), status.HTTP_200_OK


# -------------------------------------------------------------------------------------------------------------------- #

# Change Password
@app.route("/api/user/changePassword", methods=["POST"])
def change_password():
    # Read in password change input parameters
    username = request.form.get('username')  # String (a-z, A-Z, 0-9, -, _)
    old_password = request.form.get('old_password')  # String (6 <= characters <= 256)
    new_password = request.form.get('new_password')  # String (6 <= characters <= 256)
    access_token = request.form.get('access_token')

    # Check if all the changePassword input parameters are valid
    check = check_for_none("changePassword",
                           [("username", username), ("old_password", old_password), ("new_password", new_password)])

    if check is not None:
        return jsonify(check), status.HTTP_400_BAD_REQUEST

    check2 = validate_parameters("changePassword", username=username, password=old_password, password2=new_password)

    if check2 is not None:
        return jsonify(check2), status.HTTP_400_BAD_REQUEST

    try:
        cursor = db.cursor()
    except:
        error_code = "connection_to_database_failed"

        content = {"success": False, "error_code": error_code}
        print(traceback.format_exc())
        return jsonify(content), status.HTTP_500_INTERNAL_SERVER_ERROR

    # Check if the old password and access token are valid

    # cursor.execute("SELECT password_hash, access_token FROM user WHERE username='" + username + "';")
    cursor.execute("SELECT password_hash, access_token FROM user WHERE username=%s;", (username,))
    result = cursor.fetchone()

    # Return a bad username error if the username isn't in the table
    if not result:
        error_code = "user_changePassword_bad_username"
        cursor.close()

        content = {"success": False, "error_code": error_code}
        return jsonify(content), status.HTTP_400_BAD_REQUEST

    # Check if the old password matches the password hash in the user table and therefore, correct
    is_correct_password = bcrypt.check_password_hash(str(result[0]), old_password)

    # Return a bad old_password error if the old password isn't correct
    if not is_correct_password:
        error_code = "user_changePassword_bad_old_password"
        cursor.close()

        content = {"success": False, "error_code": error_code}
        return jsonify(content), status.HTTP_400_BAD_REQUEST

    elif not (access_token == result[1]):
        error_code = "user_bad_access_token"
        cursor.close()

        content = {"success": False, "error_code": error_code}
        return jsonify(content), status.HTTP_403_FORBIDDEN

    # If this line of the changePassword() function is reached,
    # all the password change input parameters are valid.

    else:
        password_hash = bcrypt.generate_password_hash(new_password)

        cursor.execute("UPDATE user SET password_hash=%s WHERE username=%s;", (password_hash, username))
        db.commit()
        cursor.close()

        content = {"success": True}
        return jsonify(content), status.HTTP_200_OK


# -------------------------------------------------------------------------------------------------------------------- #

# Change Display Name
@app.route("/api/user/changeDisplayName", methods=["POST"])
def change_display_name():
    # Read in display name change input parameters
    username = request.form.get('username')  # String (a-z, A-Z, 0-9, -, _)
    display_name = request.form.get('display_name')  # String (6 <= characters <= 256)
    access_token = request.form.get('access_token')

    # Check if all the changeDisplayName input parameters are valid
    check = check_for_none("changeDisplayName", [("username", username), ("display_name", display_name)])

    if check is not None:
        return jsonify(check), status.HTTP_400_BAD_REQUEST

    check2 = validate_parameters("changeDisplayName", username=username, display_name=display_name)

    if check2 is not None:
        return jsonify(check2), status.HTTP_400_BAD_REQUEST

    try:
        cursor = db.cursor()
    except:
        error_code = "connection_to_database_failed"

        content = {"success": False, "error_code": error_code}
        print(traceback.format_exc())
        return jsonify(content), status.HTTP_500_INTERNAL_SERVER_ERROR

    # Check if the access token is valid

    # cursor.execute("SELECT access_token FROM user WHERE username='" + username + "';")
    cursor.execute("SELECT access_token FROM user WHERE username=%s;", (username,))
    result = cursor.fetchone()

    # Return a bad username error if the username isn't in the table
    if not result:
        error_code = "user_changeDisplayName_bad_username"
        cursor.close()

        content = {"success": False, "error_code": error_code}
        return jsonify(content), status.HTTP_400_BAD_REQUEST

    elif not (access_token == result[0]):
        error_code = "user_bad_access_token"
        cursor.close()

        content = {"success": False, "error_code": error_code}
        return jsonify(content), status.HTTP_403_FORBIDDEN

    # If this line of the changeDisplayName() function is reached,
    # all the display name change input parameters are valid.

    else:
        cursor.execute("UPDATE user SET display_name=%s WHERE username=%s;", (display_name, username))
        db.commit()
        cursor.close()

        content = {"success": True}
        return jsonify(content), status.HTTP_200_OK


# -------------------------------------------------------------------------------------------------------------------- #

# Change Email Address
@app.route("/api/user/changeEmailAddress", methods=["POST"])
def change_email_address():
    # Read in email address change input parameters
    username = request.form.get('username')  # String (a-z, A-Z, 0-9, -, _)
    email_address = request.form.get('email_address')  # String (6 <= characters <= 256)
    access_token = request.form.get('access_token')

    # Check if all the changeEmailAddress input parameters are valid
    check = check_for_none("changeEmailAddress", [("username", username), ("email_address", email_address)])

    if check is not None:
        return jsonify(check), status.HTTP_400_BAD_REQUEST

    check2 = validate_parameters("changeEmailAddress", username=username, email_address=email_address)

    if check2 is not None:
        return jsonify(check2), status.HTTP_400_BAD_REQUEST

    try:
        cursor = db.cursor()
    except:
        error_code = "connection_to_database_failed"

        content = {"success": False, "error_code": error_code}
        print(traceback.format_exc())
        return jsonify(content), status.HTTP_500_INTERNAL_SERVER_ERROR

    # Check if the access token is valid

    # cursor.execute("SELECT access_token FROM user WHERE username='" + username + "';")
    cursor.execute("SELECT access_token FROM user WHERE username=%s;", (username,))
    result = cursor.fetchone()

    # Return a bad username error if the username isn't in the table
    if not result:
        error_code = "user_changeEmailAddress_bad_username"
        cursor.close()

        content = {"success": False, "error_code": error_code}
        return jsonify(content), status.HTTP_400_BAD_REQUEST

    elif not (access_token == result[0]):
        error_code = "user_bad_access_token"
        cursor.close()

        content = {"success": False, "error_code": error_code}
        return jsonify(content), status.HTTP_403_FORBIDDEN

    # If this line of the changeEmailAddress() function is reached,
    # all the email address change input parameters are valid.

    else:
        cursor.execute("UPDATE user SET email_address=%s WHERE username=%s;", (email_address, username))
        db.commit()
        cursor.close()

        content = {"success": True}
        return jsonify(content), status.HTTP_200_OK


# -------------------------------------------------------------------------------------------------------------------- #

# City Checkin
@app.route("/api/user/checkin", methods=["POST"])
def checkin():
    # Read in checkin input parameters
    username = request.form.get('username')  # String (a-z, A-Z, 0-9, -, _)
    access_token = request.form.get('access_token')  # String (6 <= characters <= 256)
    latitude = request.form.get('latitude')  # Float (-90 <= latitude <= 90)
    longitude = request.form.get('longitude')  # Float (-180 <= longitude <= 180)

    # Check if all the checkin input parameters are valid
    check = check_for_none("checkin", [("username", username), ("latlong", latitude), ("latlong", longitude)])

    if check is not None:
        return jsonify(check), status.HTTP_400_BAD_REQUEST

    check2 = validate_parameters("checkin", username=username, latitude=latitude, longitude=longitude)

    if check2 is not None:
        return jsonify(check2), status.HTTP_400_BAD_REQUEST

    try:
        cursor = db.cursor()
    except:
        error_code = "connection_to_database_failed"

        content = {"success": False, "error_code": error_code}
        print(traceback.format_exc())
        return jsonify(content), status.HTTP_500_INTERNAL_SERVER_ERROR

    # Check if the access token is valid

    # cursor.execute("SELECT access_token FROM user WHERE username='" + username + "';")
    cursor.execute("SELECT access_token FROM user WHERE username=%s;", (username,))
    result = cursor.fetchone()

    # Return a bad username error if the username isn't in the table
    if not result:
        error_code = "user_checkin_bad_username"
        cursor.close()

        content = {"success": False, "error_code": error_code}
        return jsonify(content), status.HTTP_400_BAD_REQUEST

    elif not (access_token == result[0]):
        error_code = "user_bad_access_token"
        cursor.close()

        content = {"success": False, "error_code": error_code}
        return jsonify(content), status.HTTP_403_FORBIDDEN

    # Distance is in miles
    # Paris latitude: 48.856062
    # Paris longitude: 2.347510

    # Suggestions for improving performance:
    # 1. Use a square then a circle to query for cities
    # 2. Parallel query execution- Run the two queries in Parallel
    # 3. Run the query once in SQL and sort it twice in Python
# lowBoundLat = latitude - 1
# highBoundLat = latitude + 1
# lowBoundLong = longitude - 1
# highBoundLong = longitude + 1
# cursor.execute("SELECT * " +
#               "FROM " +
#               "(" +
#                   "(" +
#                       "SELECT *, (3959 * acos(cos(radians(" + latitude + ")) * cos(radians(latitude)) * " +
#                       "cos(radians(longitude) - radians(" + longitude + ")) + sin(radians(" + latitude + ")) * " +
#                       "sin(radians(latitude)))) AS distance " +
#                       "FROM (SELECT * FROM city WHERE city.latitude > (" + lowBoundLat +") AND city.latitude < (" + highBoundLat +") AND city.longitude > (" + lowBoundLong +") AND city.longitude < (" + highBoundLong +")) " +
#                       "HAVING distance < 5 " +
#                       "ORDER BY population DESC " +
#                       "LIMIT 0, 1" +
#                   ")" +
#                   "UNION" +
#                   "(" +
#                       "SELECT *, (3959 * acos(cos(radians(" + latitude + ")) * cos(radians(latitude)) * " +
#                       "cos(radians(longitude) - radians(" + longitude + ")) + sin(radians(" + latitude + ")) * " +
#                       "sin(radians(latitude)))) AS distance " +
#                       "FROM city " +
#                       "HAVING distance < 5 " +
#                       "ORDER BY distance " +
#                       "LIMIT 0, 1" +
#                   ")" +
#               ") AS distpop " +
#               "ORDER BY population DESC, distance ASC " +
#               "LIMIT 0,1")
    results = cursor.callproc("query_checkin", (latitude, longitude))
    print(results)
    #CONSTANT = 3959
    #DISTANCE_THRESHOLD = 3
    #
    #cursor.execute("SELECT * " +
    #               "FROM " +
    #               "(" +
    #               "(" +
    #               "SELECT *, (%s * acos(cos(radians(%s)) * cos(radians(latitude)) * " +
    #               "cos(radians(longitude) - radians(%s)) + sin(radians(%s)) * " +
    #               "sin(radians(latitude)))) AS distance " +
    #               "FROM city " +
    #               "HAVING distance < %s " +
    #               "ORDER BY population DESC " +
    #               "LIMIT 0, 1" +
    #               ")" +
    #               "UNION" +
    #               "(" +
    #               "SELECT *, (%s * acos(cos(radians(%s)) * cos(radians(latitude)) * " +
    #               "cos(radians(longitude) - radians(%s)) + sin(radians(%s)) * " +
    #               "sin(radians(latitude)))) AS distance " +
    #               "FROM city " +
    #               "HAVING distance < %s " +
    #               "ORDER BY distance " +
    #               "LIMIT 0, 1" +
    #               ")" +
    #               ") AS distpop " +
    #               "ORDER BY distance ASC, population DESC " +
    #               "LIMIT 0,2;", (
    #                   CONSTANT, latitude, longitude, latitude, DISTANCE_THRESHOLD, CONSTANT, latitude, longitude,
    #                   latitude,
    #                   DISTANCE_THRESHOLD))

    #results = cursor.fetchall()

    if not results:
        error_code = "user_checkin_not_close_enough_to_city"
        cursor.close()

        content = {"success": False, "error_code": error_code}
        return jsonify(content), status.HTTP_400_BAD_REQUEST

    # Determine which city to return and check the user into.
    #
    # If the closest city has no population and the most populated city
    # within 5 miles doesn't, return the most populated city.  Otherwise,
    # return the closest city.

    if len(results) == 1:
        final_result = results[0]
    elif (results[0][4] == 0) and not (results[1][4] == 0):
        final_result = results[1]
    else:
        final_result = results[0]

    cursor.execute("UPDATE user " +
                   "SET checkin_count=" +
                   "(" +
                   "SELECT checkin_count + 1 " +
                   "FROM (SELECT checkin_count FROM user WHERE username=%s) AS intermediate" +
                   ") " +
                   "WHERE username=%s;", (username, username))

    cursor.execute("INSERT INTO checkin values(%s, %s, NOW());", (username, str(final_result[0])))
    db.commit()
    cursor.close()

    content = {"success": True, "city_name": final_result[2], "region_name": "NA", "region_code": final_result[3],
               "country_name": "NA", "country_code": final_result[1]}
    return jsonify(content), status.HTTP_200_OK


# -------------------------------------------------------------------------------------------------------------------- #

# Follow User
@app.route("/api/user/follow", methods=["POST"])
def follow():
    follower_username = request.form.get('follower_username')  # String (a-z, A-Z, 0-9, -, _)
    followee_username = request.form.get('followee_username')  # String (a-z, A-Z, 0-9, -, _)
    access_token = request.form.get('access_token')

    # Check if all the follow input parameters are valid
    check = check_for_none("follow",
                           [("follower_username", follower_username), ("followee_username", followee_username)])

    if check is not None:
        return jsonify(check), status.HTTP_400_BAD_REQUEST

    check2 = validate_parameters("follow", username=follower_username, username2=followee_username)

    if check2 is not None:
        return jsonify(check2), status.HTTP_400_BAD_REQUEST

    try:
        cursor = db.cursor()
    except:
        error_code = "connection_to_database_failed"

        content = {"success": False, "error_code": error_code}
        print(traceback.format_exc())
        return jsonify(content), status.HTTP_500_INTERNAL_SERVER_ERROR

    # Check if the access token is valid

    # cursor.execute("SELECT access_token FROM user WHERE username='" + follower_username + "';")
    cursor.execute("SELECT access_token FROM user WHERE username=%s;", (follower_username,))
    result = cursor.fetchone()

    # Return a bad username error if the username isn't in the table
    if not result:
        error_code = "user_follow_bad_follower_username"
        cursor.close()

        content = {"success": False, "error_code": error_code}
        return jsonify(content), status.HTTP_400_BAD_REQUEST

    elif not (access_token == result[0]):
        error_code = "user_bad_access_token"
        cursor.close()

        content = {"success": False, "error_code": error_code}
        return jsonify(content), status.HTTP_403_FORBIDDEN

    cursor.execute("SELECT * FROM user WHERE username=%s;", (followee_username,))
    result = cursor.fetchone()

    # return a bad username error if the username isn't in the table
    if not result:
        error_code = "user_follow_bad_followee_username"
        cursor.close()

        content = {"success": False, "error_code": error_code}
        return jsonify(content), status.HTTP_400_BAD_REQUEST
    # finished argument checking here

    # add the follow to the table
    cursor.execute("INSERT INTO follow VALUES (%s, %s);", (follower_username, followee_username))
    db.commit()
    cursor.close()

    content = {"success": True}
    return jsonify(content), status.HTTP_200_OK


# -------------------------------------------------------------------------------------------------------------------- #

# Unfollow User
@app.route("/api/user/unfollow", methods=["POST"])
def unfollow():
    follower_username = request.form.get('follower_username')  # String (a-z, A-Z, 0-9, -, _)
    followee_username = request.form.get('followee_username')  # String (a-z, A-Z, 0-9, -, _)
    access_token = request.form.get('access_token')

    # Check if all the unfollow input parameters are valid
    check = check_for_none("unfollow",
                           [("follower_username", follower_username), ("followee_username", followee_username)])

    if check is not None:
        return jsonify(check), status.HTTP_400_BAD_REQUEST

    check2 = validate_parameters("unfollow", username=follower_username, username2=followee_username)

    if check2 is not None:
        return jsonify(check2), status.HTTP_400_BAD_REQUEST

    try:
        cursor = db.cursor()
    except:
        error_code = "connection_to_database_failed"

        content = {"success": False, "error_code": error_code}
        print(traceback.format_exc())
        return jsonify(content), status.HTTP_500_INTERNAL_SERVER_ERROR

    # Check if the access token is valid

    # cursor.execute("SELECT access_token FROM user WHERE username='" + follower_username + "';")
    cursor.execute("SELECT access_token FROM user WHERE username=%s;", (follower_username,))
    result = cursor.fetchone()

    # Return a bad username error if the username isn't in the table
    if not result:
        error_code = "user_unfollow_bad_follower_username"
        cursor.close()

        content = {"success": False, "error_code": error_code}
        return jsonify(content), status.HTTP_400_BAD_REQUEST

    elif not (access_token == result[0]):
        error_code = "user_bad_access_token"
        cursor.close()

        content = {"success": False, "error_code": error_code}
        return jsonify(content), status.HTTP_403_FORBIDDEN

    cursor.execute("SELECT * FROM user WHERE username=%s;", (followee_username,))
    result = cursor.fetchone()

    # return a bad username error if the username isn't in the table
    if not result:
        error_code = "user_unfollow_bad_followee_username"
        cursor.close()

        content = {"success": False, "error_code": error_code}
        return jsonify(content), status.HTTP_400_BAD_REQUEST
    # finished argument checking here

    # remove the follow to the table
    cursor.execute("DELETE FROM follow WHERE username_follower=%s AND username_followee=%s;",
                   (follower_username, followee_username))
    db.commit()
    cursor.close()

    content = {"success": True}
    return jsonify(content), status.HTTP_200_OK


# -------------------------------------------------------------------------------------------------------------------- #

# Delete User
@app.route("/api/user/remove", methods=["POST"])
def remove():
    # Read in profile input parameters
    username = request.form.get('username')  # String (a-z, A-Z, 0-9, -, _)
    access_token = request.form.get('access_token')

    # Check if all the remove input parameters are valid
    check = check_for_none("remove", [("username", username)])

    if check is not None:
        return jsonify(check), status.HTTP_400_BAD_REQUEST

    check2 = validate_parameters("remove", username=username)

    if check2 is not None:
        return jsonify(check2), status.HTTP_400_BAD_REQUEST

    try:
        cursor = db.cursor()
    except:
        error_code = "connection_to_database_failed"

        content = {"success": False, "error_code": error_code}
        print(traceback.format_exc())
        return jsonify(content), status.HTTP_500_INTERNAL_SERVER_ERROR

    # Check if the access token is valid
    cursor.execute("SELECT access_token FROM user WHERE username=%s;", (username,))
    result = cursor.fetchone()

    # Return a bad username error if the username isn't in the table
    if not result:
        error_code = "user_remove_bad_username"
        cursor.close()

        content = {"success": False, "error_code": error_code}
        return jsonify(content), status.HTTP_400_BAD_REQUEST

    elif not (access_token == result[0]):
        error_code = "user_bad_access_token"
        cursor.close()

        content = {"success": False, "error_code": error_code}
        return jsonify(content), status.HTTP_403_FORBIDDEN

    # arguments are valid

    # remove user from checkin table
    cursor.execute("DELETE FROM checkin WHERE username=%s;", (username,))

    # remove user from follow table
    cursor.execute("DELETE FROM follow WHERE username_follower=%s OR username_followee=%s;", (username, username))

    # remove user from user table
    cursor.execute("DELETE FROM user WHERE username=%s;", (username,))
    db.commit()
    cursor.close()

    content = {"success": True}
    return jsonify(content), status.HTTP_200_OK


# -------------------------------------------------------------------------------------------------------------------- #

# Root
@app.route("/")
def root():
    return "You have reached our Flask server."


# -------------------------------------------------------------------------------------------------------------------- #

# Check if any of the parameters are None
def check_for_none(function_name, params):
    for param in params:
        if param[1] is None:
            error_code = "user_" + function_name + "_invalid_" + param[0]
            content = {"success": False, "error_code": error_code}
            return content
    return None


# -------------------------------------------------------------------------------------------------------------------- #

# Validates parameters for different functions
def validate_parameters(function_name, username=None, username2=None, password=None, password2=None,
                        email_address=None, display_name=None, latitude=None, longitude=None):
    # Check if username is valid
    if username is not None:
        if not all((c in ascii_letters + digits + '-' + '_') for c in username):
            if function_name == "follow" or function_name == "unfollow":
                error_code = "user_" + function_name + "_invalid_follower_username"
            else:
                error_code = "user_" + function_name + "_invalid_username"

            content = {"success": False, "error_code": error_code}
            return content

    # Check if username is valid
    if username2 is not None:
        if not all((c in ascii_letters + digits + '-' + '_') for c in username2):
            if function_name == "follow" or function_name == "unfollow":
                error_code = "user_" + function_name + "_invalid_followee_username"
            elif function_name == "search":
                error_code = "user_" + function_name + "_invalid_search_username"
            else:
                error_code = "user_" + function_name + "_invalid_username"

            content = {"success": False, "error_code": error_code}
            return content

    # Check if password is valid
    if password is not None:
        if (not all((c in ascii_letters + digits + punctuation) for c in password)) \
                or (not (6 <= len(password) <= 256)):
            if function_name == "changePassword":
                error_code = "user_" + function_name + "_invalid_old_password"
            else:
                error_code = "user_" + function_name + "_invalid_password"

            content = {"success": False, "error_code": error_code}
            return content

    # Check if password2 is valid
    if password2 is not None:
        if (not all((c in ascii_letters + digits + punctuation) for c in password2)) \
                or (not (6 <= len(password2) <= 256)):
            if function_name == "changePassword":
                error_code = "user_" + function_name + "_invalid_new_password"
            else:
                error_code = "user_" + function_name + "_invalid_password"

            content = {"success": False, "error_code": error_code}
            return content

    # Check if email_address is valid
    if email_address is not None:
        email_regex = re.compile(r"([a-zA-Z0-9]+)@([a-zA-Z0-9]+)\.([a-zA-Z0-9]+)")

        if not email_regex.match(email_address):
            error_code = "user_" + function_name + "_invalid_email"

            content = {"success": False, "error_code": error_code}
            return content

    # Check if display_name is valid
    if display_name is not None:
        if (not all((c in ascii_letters + digits + '-' + '_' + ' ') for c in display_name)) \
                or (not (1 <= len(display_name) <= 256)):
            error_code = "user_" + function_name + "_invalid_display_name"

            content = {"success": False, "error_code": error_code}
            return content

    # Check if latitude is valid
    if latitude is not None:
        if not (-90 <= float(latitude) <= 90):
            error_code = "user_" + function_name + "_invalid_latlong"

            content = {"success": False, "error_code": error_code}
            return content

    # Check if longitude is valid
    if longitude is not None:
        if not (-180 <= float(longitude) <= 180):
            error_code = "user_" + function_name + "_invalid_latlong"

            content = {"success": False, "error_code": error_code}
            return content

    return None


# -------------------------------------------------------------------------------------------------------------------- #

# Runs the app
if __name__ == "__main__":
    app.run()
