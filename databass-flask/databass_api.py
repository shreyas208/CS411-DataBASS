# Import all necessary Flask libraries and extensions
from flask import Flask, request, jsonify
import mysql.connector as MySQL # Connects Flask server to MySQL database
from flask_api import status # Handles error codes returned by Flask server
from flask_bcrypt import Bcrypt

# Generates access tokens
import binascii
import os

# Import libaries for checking the validity of usernames and email addresses
from string import ascii_letters
from string import digits
import re

from math import sin, cos, sqrt, atan2, radians

app = Flask(__name__)
bcrypt = Bcrypt(app)

# Connect to the project database on the VM
db = MySQL.connect(host="localhost", port=3306, user="flaskuser", password="tCU8PvBYEPP4qkun", database="cs_411_project")


# To-Do List:

# 1. Add checks to each function to see if any of the parameters are None/NULL
# 2. Add constraints to display_name and other parameters to avoid SQL Injection (a display name or parameter containing SQL queries)
# 3. Add foreign key constraints to the checkin and follow tables to ensure proper updates and deletions
# 4. Complete the search function
# 5. Update the profile function to only return checkins over the past (week?  3 days?)
# 6. Map country and region codes to their actual names
# 7. Optimize the checkin query (currently takes about 6 secons to run)
# 8. TEST THE FUNTIONALITY EXTENSIVELY!


# User Registration
@app.route("/api/user/register", methods=["POST"])
def register():
    # Read in registration input parameters
    username = request.form.get('username') # String (a-z, A-Z, 0-9, -, _)
    password = request.form.get('password') # String (6 <= characters <= 256)
    email_address = request.form.get('email_address') # String (valid email)
    display_name = request.form.get('display_name') # String (1 <= characters <= 265)

    # Check if all the registration input parameters are valid
    check = checkForNone("register", [("username", username), ("password", password), ("email", email_address), ("display_name", display_name)])

    if check is not None:
        return jsonify(check), status.HTTP_400_BAD_REQUEST

    check2 = validateParameters("register", username=username, password=password, email_address=email_address, display_name=display_name)

    if check2 is not None:
        return jsonify(check2), status.HTTP_400_BAD_REQUEST

    # Connect to the MySQL database
    cursor = None

    try:
        cursor = db.cursor()
    except:
        error_code = "connection_to_database_failed"

        content = {"success": False, "error_code": error_code}
        print(traceback.format_exc())
        return jsonify(content), status.HTTP_500_INTERNAL_SERVER_ERROR

    # Check if the username is new
    cursor.execute("SELECT username FROM user WHERE username='" + username + "';")
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

    cursor.execute("INSERT INTO user values('" +
                    username + "', '" + email_address + "', '" + display_name + "', '" +
                    password_hash + "', NOW(), NULL, 0);")

    db.commit()
    cursor.close()

    content = {"success": True}
    return jsonify(content), status.HTTP_200_OK


# User Login
@app.route("/api/user/login", methods=["POST"])
def login():
    # Read in login input parameters
    username = request.form.get('username') # String (a-z, A-Z, 0-9, -, _)
    password = request.form.get('password') # String (6 <= characters <= 256)

    # Check if all the login input parameters are valid
    check = checkForNone("login", [("username", username), ("password", password)])

    if check is not None:
        return jsonify(check), status.HTTP_400_BAD_REQUEST

    check2 = validateParameters("login", username=username, password=password)

    if check2 is not None:
        return jsonify(check2), status.HTTP_400_BAD_REQUEST

    # Connect to the MySQL database
    cursor = None

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
    cursor.execute("SELECT email_address, display_name, password_hash FROM user WHERE username='" + username + "';")
    result = cursor.fetchone()

    # Return a bad login credential error if the username isn't in the table
    if not result:
        error_code = "user_login_bad_credentials"
        cursor.close()

        content = {"success": False, "error_code": error_code}
        return jsonify(content), status.HTTP_400_BAD_REQUEST

    # Check if the input password matches the password hash in the user table and therefore, correct
    isCorrectPassword = bcrypt.check_password_hash(str(result[2]), password)

    # Return a bad login credential error if the password isn't correct
    if not isCorrectPassword:
        error_code = "user_login_bad_credentials"
        cursor.close()

        content = {"success": False, "error_code": error_code}
        return jsonify(content), status.HTTP_400_BAD_REQUEST
    else:
        email = result[0]
        display_name = result[1]

        # Generate an access token and insert it into the user table
        access_token = binascii.hexlify(os.urandom(32)).decode()

        cursor.execute("UPDATE user SET access_token='" + access_token + "' WHERE username='" + username + "';")
        db.commit()
        cursor.close()

        content = {"success": True, "email_address": email, "display_name": display_name, "access_token": access_token}
        return jsonify(content), status.HTTP_200_OK


# User Logout
@app.route("/api/user/logout", methods=["POST"])
def logout():
    # Read in logout input parameters
    username = request.form.get('username') # String (a-z, A-Z, 0-9, -, _)
    access_token = request.form.get('access_token')

    # Check if all the logout input parameters are valid
    check = checkForNone("logout", [("username", username)])

    if check is not None:
        return jsonify(check), status.HTTP_400_BAD_REQUEST

    check2 = validateParameters("logout", username=username)

    if check2 is not None:
        return jsonify(check2), status.HTTP_400_BAD_REQUEST

    # Connect to the MySQL database
    cursor = None

    try:
        cursor = db.cursor()
    except:
        error_code = "connection_to_database_failed"

        content = {"success": False, "error_code": error_code}
        print(traceback.format_exc())
        return jsonify(content), status.HTTP_500_INTERNAL_SERVER_ERROR

    # Check if the access token is valid
    cursor.execute("SELECT access_token FROM user WHERE username='" + username + "';")
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
        cursor.execute("UPDATE user SET access_token=NULL WHERE username='" + username + "';")
        db.commit()
        cursor.close()

        content = {"success": True}
        return jsonify(content), status.HTTP_200_OK


# Search User
@app.route("/api/user/search", methods=["POST"])
def search():
    # Read in search input parameters
    username = request.form.get('username') # String (a-z, A-Z, 0-9, -, _)
    search_username = request.form.get('search_username') # String (a-z, A-Z, 0-9, -, _)
    access_token = request.form.get('access_token')

    # Check if all the search input parameters are valid
    check = checkForNone("search", [("username", username), ("search_username", search_username)])

    if check is not None:
        return jsonify(check), status.HTTP_400_BAD_REQUEST

    check2 = validateParameters("search", username=username, username2=search_username)

    if check2 is not None:
        return jsonify(check2), status.HTTP_400_BAD_REQUEST

    # Connect to the MySQL database
    cursor = None

    try:
        cursor = db.cursor()
    except:
        error_code = "connection_to_database_failed"

        content = {"success": False, "error_code": error_code}
        print(traceback.format_exc())
        return jsonify(content), status.HTTP_500_INTERNAL_SERVER_ERROR

    # Check if the access token is valid
    cursor.execute("SELECT access_token FROM user WHERE username='" + username + "';")
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
    cursor.execute("SELECT username, display_name FROM user WHERE username LIKE '" + search_username + "%';")
    results = cursor.fetchall()
    cursor.close()

    if not results:
        error_code = "user_search_no_results_found"

        content = {"success": False, "error_code": error_code}
        return jsonify(content), status.HTTP_400_BAD_REQUEST

    content = {"success": True, "results": results}
    return jsonify(content), status.HTTP_200_OK


# User Profile
@app.route("/api/user/profile", methods=["POST"])
def profile():
    # Read in profile input parameters
    username = request.form.get('username') # String (a-z, A-Z, 0-9, -, _)
    access_token = request.form.get('access_token')

    # Check if all the profile input parameters are valid
    check = checkForNone("profile", [("username", username)])

    if check is not None:
        return jsonify(check), status.HTTP_400_BAD_REQUEST

    check2 = validateParameters("profile", username=username)

    if check2 is not None:
        return jsonify(check2), status.HTTP_400_BAD_REQUEST

    # Connect to the MySQL database
    cursor = None

    try:
        cursor = db.cursor()
    except:
        error_code = "connection_to_database_failed"

        content = {"success": False, "error_code": error_code}
        print(traceback.format_exc())
        return jsonify(content), status.HTTP_500_INTERNAL_SERVER_ERROR

    # Check if the access token is valid
    cursor.execute("SELECT access_token FROM user WHERE username='" + username + "';")
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

    cursor.execute("SELECT email_address, display_name, join_date, num_checkins, city_id FROM user, checkin WHERE user.username = checkin.username and user.username ='" + username + "';") #query the database for that user
    checkins = cursor.fetchall()

    #we need to get email_address, display_name, join_datetime, num_checkins, and recent_checkins
    email_address = checkins[0][0]
    display_name = checkins[0][1]
    join_datetime = checkins[0][2]
    num_checkins = checkins[0][3]

    cursor.execute("SELECT name FROM city WHERE id IN (SELECT city_id FROM user, checkin WHERE user.username = '" + username + "' and checkin.username = '" + username + "');")
    city_names = cursor.fetchall()
    cursor.close()

    recent_checkins = [i[0] for i in city_names]
    content = {"success": True, "email_address": email_address, "display_name": display_name, "join_datetime": join_datetime, "num_checkins": num_checkins, "recent_checkins": recent_checkins}
    return jsonify(content), status.HTTP_200_OK


# Change Password
@app.route("/api/user/changePassword", methods=["POST"])
def changePassword():
    # Read in password change input parameters
    username = request.form.get('username') # String (a-z, A-Z, 0-9, -, _)
    old_password = request.form.get('old_password') # String (6 <= characters <= 256)
    new_password = request.form.get('new_password') # String (6 <= characters <= 256)
    access_token = request.form.get('access_token')

    # Check if all the changePassword input parameters are valid
    check = checkForNone("changePassword", [("username", username), ("old_password", old_password), ("new_password", new_password)])

    if check is not None:
        return jsonify(check), status.HTTP_400_BAD_REQUEST

    check2 = validateParameters("changePassword", username=username, password=old_password, password2=new_password)

    if check2 is not None:
        return jsonify(check2), status.HTTP_400_BAD_REQUEST

    # Connect to the MySQL database
    cursor = None

    try:
        cursor = db.cursor()
    except:
        error_code = "connection_to_database_failed"

        content = {"success": False, "error_code": error_code}
        print(traceback.format_exc())
        return jsonify(content), status.HTTP_500_INTERNAL_SERVER_ERROR

    # Check if the old password and access token are valid
    cursor.execute("SELECT password_hash, access_token FROM user WHERE username='" + username + "';")
    result = cursor.fetchone()

    # Return a bad username error if the username isn't in the table
    if not result:
        error_code = "user_changePassword_bad_username"
        cursor.close()

        content = {"success": False, "error_code": error_code}
        return jsonify(content), status.HTTP_400_BAD_REQUEST

    # Check if the old password matches the password hash in the user table and therefore, correct
    isCorrectPassword = bcrypt.check_password_hash(str(result[0]), old_password)

    # Return a bad old_password error if the old password isn't correct
    if not isCorrectPassword:
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

        cursor.execute("UPDATE user SET password_hash='" + password_hash + "' WHERE username='" + username + "';")
        db.commit()
        cursor.close()

        content = {"success": True}
        return jsonify(content), status.HTTP_200_OK


# Change Display Name
@app.route("/api/user/changeDisplayName", methods=["POST"])
def changeDisplayName():
    # Read in display name change input parameters
    username = request.form.get('username') # String (a-z, A-Z, 0-9, -, _)
    display_name = request.form.get('display_name') # String (6 <= characters <= 256)
    access_token = request.form.get('access_token')

    # Check if all the changeDisplayName input parameters are valid
    check = checkForNone("changeDisplayName", [("username", username), ("display_name", display_name)])

    if check is not None:
        return jsonify(check), status.HTTP_400_BAD_REQUEST

    check2 = validateParameters("changeDisplayName", username=username, display_name=display_name)

    if check2 is not None:
        return jsonify(check2), status.HTTP_400_BAD_REQUEST

    # Connect to the MySQL database
    cursor = None

    try:
        cursor = db.cursor()
    except:
        error_code = "connection_to_database_failed"

        content = {"success": False, "error_code": error_code}
        print(traceback.format_exc())
        return jsonify(content), status.HTTP_500_INTERNAL_SERVER_ERROR

    # Check if the access token is valid
    cursor.execute("SELECT access_token FROM user WHERE username='" + username + "';")
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
        cursor.execute("UPDATE user SET display_name='" + display_name + "' WHERE username='" + username + "';")
        db.commit()
        cursor.close()

        content = {"success": True}
        return jsonify(content), status.HTTP_200_OK


# Change Email Address
@app.route("/api/user/changeEmailAddress", methods=["POST"])
def changeEmailAddress():
    # Read in email address change input parameters
    username = request.form.get('username') # String (a-z, A-Z, 0-9, -, _)
    email_address = request.form.get('email_address') # String (6 <= characters <= 256)
    access_token = request.form.get('access_token')

    # Check if all the changeEmailAddress input parameters are valid
    check = checkForNone("changeEmailAddress", [("username", username), ("email_address", email_address)])

    if check is not None:
        return jsonify(check), status.HTTP_400_BAD_REQUEST

    check2 = validateParameters("changeEmailAddress", username=username, email_address=email_address)

    if check2 is not None:
        return jsonify(check2), status.HTTP_400_BAD_REQUEST

    # Connect to the MySQL database
    cursor = None

    try:
        cursor = db.cursor()
    except:
        error_code = "connection_to_database_failed"

        content = {"success": False, "error_code": error_code}
        print(traceback.format_exc())
        return jsonify(content), status.HTTP_500_INTERNAL_SERVER_ERROR

    # Check if the access token is valid
    cursor.execute("SELECT access_token FROM user WHERE username='" + username + "';")
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
        cursor.execute("UPDATE user SET email_address='" + email_address + "' WHERE username='" + username + "';")
        db.commit()
        cursor.close()

        content = {"success": True}
        return jsonify(content), status.HTTP_200_OK


# City Checkin
@app.route("/api/user/checkin", methods=["POST"])
def checkin():
    # Read in checkin input parameters
    username = request.form.get('username') # String (a-z, A-Z, 0-9, -, _)
    access_token = request.form.get('access_token') # String (6 <= characters <= 256)
    latitude = request.form.get('latitude') # Float (-90 <= latitude <= 90)
    longitude = request.form.get('longitude') # Float (-180 <= longitude <= 180)

    # Check if all the checkin input parameters are valid
    check = checkForNone("checkin", [("username", username), ("latlong", latitude), ("latlong", longitude)])

    if check is not None:
        return jsonify(check), status.HTTP_400_BAD_REQUEST

    check2 = validateParameters("checkin", username=username, latitude=latitude, longitude=longitude)

    if check2 is not None:
        return jsonify(check2), status.HTTP_400_BAD_REQUEST

    # Connect to the MySQL database
    cursor = None

    try:
        cursor = db.cursor()
    except:
        error_code = "connection_to_database_failed"

        content = {"success": False, "error_code": error_code}
        print(traceback.format_exc())
        return jsonify(content), status.HTTP_500_INTERNAL_SERVER_ERROR

    # Check if the access token is valid
    cursor.execute("SELECT access_token FROM user WHERE username='" + username + "';")
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

    #cursor.execute("SELECT * " +
    #               "FROM " +
    #               "(" +
    #                   "(" +
    #                       "SELECT *, (3959 * acos(cos(radians(" + latitude + ")) * cos(radians(latitude)) * " +
    #                       "cos(radians(longitude) - radians(" + longitude + ")) + sin(radians(" + latitude + ")) * " +
    #                       "sin(radians(latitude)))) AS distance " +
    #                       "FROM city " +
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
    CONSTANT = 3959
    DISTANCE_THRESHOLD = 3

    cursor.execute("SELECT * " +
                   "FROM " +
                   "(" +
                       "(" +
                           "SELECT *, (" + CONSTANT + " * acos(cos(radians(" + latitude + ")) * cos(radians(latitude)) * " +
                           "cos(radians(longitude) - radians(" + longitude + ")) + sin(radians(" + latitude + ")) * " +
                           "sin(radians(latitude)))) AS distance " +
                           "FROM city " +
                           "HAVING distance < " + DISTANCE_THRESHOLD + " " +
                           "ORDER BY population DESC " +
                           "LIMIT 0, 1" +
                       ")" +
                       "UNION" +
                       "(" +
                           "SELECT *, (" + CONSTANT + " * acos(cos(radians(" + latitude + ")) * cos(radians(latitude)) * " +
                           "cos(radians(longitude) - radians(" + longitude + ")) + sin(radians(" + latitude + ")) * " +
                           "sin(radians(latitude)))) AS distance " +
                           "FROM city " +
                           "HAVING distance < " + DISTANCE_THRESHOLD + " " +
                           "ORDER BY distance " +
                           "LIMIT 0, 1" +
                       ")" +
                   ") AS distpop " +
                   "ORDER BY distance ASC, population DESC " +
                   "LIMIT 0,2;")

    results = cursor.fetchall()

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
    final_result = None

    if len(results) == 1:
        final_result = results[0]
    elif (results[0][4] == 0) and not(results[1][4] == 0):
        final_result = results[1]
    else:
        final_result = results[0]

    cursor.execute("UPDATE user " +
                   "SET num_checkins=" +
                   "(" +
                       "SELECT num_checkins + 1 " +
                       "FROM (SELECT num_checkins FROM user WHERE username='" + username + "') AS intermediate" +
                   ") " +
                   "WHERE username='" + username + "';")

    cursor.execute("INSERT INTO checkin values('" + username + "', " + str(final_result[0]) + ", NOW());")
    db.commit()
    cursor.close()

    content = {"success": True, "city_name": final_result[2], "region_name": "NA", "region_code": final_result[3], "country_name": "NA", "country_code": final_result[1]}
    return jsonify(content), status.HTTP_200_OK


# Follow User
@app.route("/api/user/follow", methods=["POST"])
def follow():
    follower_username = request.form.get('follower_username') # String (a-z, A-Z, 0-9, -, _)
    followee_username = request.form.get('followee_username') # String (a-z, A-Z, 0-9, -, _)
    access_token = request.form.get('access_token')

    # Check if all the follow input parameters are valid
    check = checkForNone("follow", [("follower_username", follower_username), ("followee_username", followee_username)])

    if check is not None:
        return jsonify(check), status.HTTP_400_BAD_REQUEST

    check2 = validateParameters("follow", username=follower_username, username2=followee_username)

    if check2 is not None:
        return jsonify(check2), status.HTTP_400_BAD_REQUEST

    # Connect to the MySQL database
    cursor = None

    try:
        cursor = db.cursor()
    except:
        error_code = "connection_to_database_failed"

        content = {"success": False, "error_code": error_code}
        print(traceback.format_exc())
        return jsonify(content), status.HTTP_500_INTERNAL_SERVER_ERROR

    # Check if the access token is valid
    cursor.execute("SELECT access_token FROM user WHERE username='" + follower_username + "';")
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

    cursor.execute("SELECT * FROM user WHERE username='" + followee_username + "';")
    result = cursor.fetchone()

    #return a bad username error if the username isn't in the table
    if not result:
        error_code = "user_follow_bad_followee_username"
        cursor.close()

        content = {"success": False, "error_code": error_code}
        return jsonify(content), status.HTTP_400_BAD_REQUEST
    #finished argument checking here

    #add the follow to the table
    cursor.execute("INSERT INTO follow VALUES ('" + follower_username + "', '" + followee_username + "');")
    db.commit()
    cursor.close()

    content = {"success": True}
    return jsonify(content), status.HTTP_200_OK


# Unfollow User
@app.route("/api/user/unfollow", methods=["POST"])
def unfollow():
    follower_username = request.form.get('follower_username') # String (a-z, A-Z, 0-9, -, _)
    followee_username = request.form.get('followee_username') # String (a-z, A-Z, 0-9, -, _)
    access_token = request.form.get('access_token')

    # Check if all the unfollow input parameters are valid
    check = checkForNone("unfollow", [("follower_username", follower_username), ("followee_username", followee_username)])

    if check is not None:
        return jsonify(check), status.HTTP_400_BAD_REQUEST

    check2 = validateParameters("unfollow", username=follower_username, username2=followee_username)

    if check2 is not None:
        return jsonify(check2), status.HTTP_400_BAD_REQUEST

    # Connect to the MySQL database
    cursor = None

    try:
        cursor = db.cursor()
    except:
        error_code = "connection_to_database_failed"

        content = {"success": False, "error_code": error_code}
        print(traceback.format_exc())
        return jsonify(content), status.HTTP_500_INTERNAL_SERVER_ERROR

    # Check if the access token is valid
    cursor.execute("SELECT access_token FROM user WHERE username='" + follower_username + "';")
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

    cursor.execute("SELECT * FROM user WHERE username='" + followee_username + "';")
    result = cursor.fetchone()

    #return a bad username error if the username isn't in the table
    if not result:
        error_code = "user_unfollow_bad_followee_username"
        cursor.close()

        content = {"success": False, "error_code": error_code}
        return jsonify(content), status.HTTP_400_BAD_REQUEST
    #finished argument checking here

    #remove the follow to the table
    cursor.execute("DELETE FROM follow WHERE username_follower = '" + follower_username + "' AND username_followee = '" + followee_username + "';")
    db.commit()
    cursor.close()

    content = {"success": True}
    return jsonify(content), status.HTTP_200_OK


# Delete User
@app.route("/api/user/remove", methods=["POST"])
def remove():
    # Read in profile input parameters
    username = request.form.get('username') # String (a-z, A-Z, 0-9, -, _)
    access_token = request.form.get('access_token')

    # Check if all the remove input parameters are valid
    check = checkForNone("remove", [("username", username)])

    if check is not None:
        return jsonify(check), status.HTTP_400_BAD_REQUEST

    check2 = validateParameters("remove", username=username)

    if check2 is not None:
        return jsonify(check2), status.HTTP_400_BAD_REQUEST

    # Connect to the MySQL database
    cursor = None

    try:
        cursor = db.cursor()
    except:
        error_code = "connection_to_database_failed"

        content = {"success": False, "error_code": error_code}
        print(traceback.format_exc())
        return jsonify(content), status.HTTP_500_INTERNAL_SERVER_ERROR

    # Check if the access token is valid
    cursor.execute("SELECT access_token FROM user WHERE username='" + username + "';")
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

    #arguments are valid

    #remove user from checkin table
    cursor.execute("DELETE FROM checkin WHERE username = '" + username + "';")

    #remove user from follow table
    cursor.execute("DELETE FROM follow WHERE username_follower = '" + username + "' OR username_followee = '" + username + "';")

    #remove user from user table
    cursor.execute("DELETE FROM user WHERE username = '" + username + "';")
    db.commit()
    cursor.close()

    content = {"success": True}
    return jsonify(content), status.HTTP_200_OK


@app.route("/")
def root():
    return "You have reached our Flask server."


def checkForNone(functionName, params):
    for param in params:
        if param[1] is None:
            error_code = "user_" + functionName + "_invalid_" + param[0]

            content = {"success": False, "error_code": error_code}
            return content

    return None


def validateParameters(functionName, username=None, username2=None, password=None, password2=None, email_address=None, display_name=None, latitude=None, longitude=None):
    # Check if username is valid
    if username is not None:
        if not all((c in ascii_letters + digits + '-' + '_') for c in username):
            if functionName == "follow" or functionName == "unfollow":
                error_code = "user_" + functionName + "_invalid_follower_username"
            else:
                error_code = "user_" + functionName + "_invalid_username"

            content = {"success": False, "error_code": error_code}
            return content

    # Check if username is valid
    if username2 is not None:
        if not all((c in ascii_letters + digits + '-' + '_') for c in username2):
            if functionName == "follow" or functionName == "unfollow":
                error_code = "user_" + functionName + "_invalid_followee_username"
            elif functionName == "search":
                error_code = "user_" + functionName + "_invalid_search_username"
            else:
                error_code = "user_" + functionName + "_invalid_username"

            content = {"success": False, "error_code": error_code}
            return content

    # Check if password is valid
    if password is not None:
        if (not all((c in ascii_letters + digits + '-' + '_') for c in password)) or (not (len(password) >= 6 and len(password) <= 256)):
            if functionName == "changePassword":
                error_code = "user_" + functionName + "_invalid_old_password"
            else:
                error_code = "user_" + functionName + "_invalid_password"

            content = {"success": False, "error_code": error_code}
            return content

    # Check if password2 is valid
    if password2 is not None:
        if (not all((c in ascii_letters + digits + '-' + '_') for c in password2)) or (not (len(password2) >= 6 and len(password2) <= 256)):
            if functionName == "changePassword":
                error_code = "user_" + functionName + "_invalid_new_password"
            else:
                error_code = "user_" + functionName + "_invalid_password"

            content = {"success": False, "error_code": error_code}
            return content

    # Check if email_address is valid
    if email_address is not None:
        emailRegex = re.compile(r"([a-zA-Z0-9]+)@([a-zA-Z0-9]+)\.([a-zA-Z0-9][a-zA-Z0-9][a-zA-Z0-9])")

        if (not all((c in ascii_letters + digits + '-' + '_') for c in email_address)) or (not emailRegex.match(email_address)):
            error_code = "user_" + functionName + "_invalid_email"

            content = {"success": False, "error_code": error_code}
            return content

    # Check if display_name is valid
    if display_name is not None:
        if (not all((c in ascii_letters + digits + '-' + '_') for c in display_name)) or (not (len(display_name) >= 1 and len(display_name) <= 265)):
            error_code = "user_" + functionName + "_invalid_display_name"

            content = {"success": False, "error_code": error_code}
            return content

    # Check if latitude is valid
    if latitude is not None:
        if not (float(latitude) >= -90 and float(latitude) <= 90):
            error_code = "user_" + functionName + "_invalid_latlong"

            content = {"success": False, "error_code": error_code}
            return content

    # Check if longitude is valid
    if longitude is not None:
        if not (float(longitude) >= -180 and float(longitude) <= 180):
            error_code = "user_" + functionName + "_invalid_latlong"

            content = {"success": False, "error_code": error_code}
            return content

    return None


if __name__ == "__main__":
    app.run()
