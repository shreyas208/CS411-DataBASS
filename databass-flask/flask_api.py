from flask import Flask
from flask.ext.mysql import MySQL
from flask.ext.api import status

app = Flask(__name__)

mysql = MySQL()

app.config['MYSQL_DATABASE_USER'] = 'flaskuser'
app.config['MYSQL_DATABASE_PASSWORD'] = 'tCU8PvBYEPP4qkun'
app.config['MYSQL_DATABASE_DB'] = 'srpatil2_cities_test'
app.config['MYSQL_DATABASE_HOST'] = 'localhost'
mysql.init_app(app)


'''
@app.route("/")
def hello():
    cursor = mysql.connect().cursor()
    cursor.execute('''SELECT * FROM world_cities WHERE City="paris"''')
    results = cursor.fetchall()
    return "Hello World! This is Flask." +
           "<br/>Here are all the cities in our database named Paris:<br/>" +
           "".join([s + "<br/>" for s in str(results).split("), (")])
'''

# User Registration
@app.route("/api/user/register")
def register():
    return 0


# User Login
@app.route("/api/user/login")
def login():
    return 0


# User Profile
@app.route("/api/user/profile")
def profile():
    return 0


# City Checkin
@app.route("/api/user/checkin")
def checkin():
    return 0
