from os import environ

from dotenv import load_dotenv
from flask import Flask
from flask_bcrypt import Bcrypt
from flask_cors import CORS
from flask_pymongo import PyMongo

load_dotenv()

app = Flask(__name__)

app.config['MAX_CONTENT_LENGTH'] = 1024 * 1024
app.config['UPLOAD_EXTENSIONS'] = ['.jpg', '.png', '.jpeg']
app.config['MONGO_URI'] = environ.get('MONGO_URI')
app.config['API_KEY'] = environ.get('API_KEY')

mongo = PyMongo(app)
db = mongo.db
UserCollection = db.user
DriveCollection = db.drive
LocationCollection = db.location
SignCollection = db.sign

CORS(app)
bcrypt = Bcrypt()
