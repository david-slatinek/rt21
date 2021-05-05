import flask
from flask_pymongo import PyMongo
from bson.objectid import ObjectId
from bson import json_util
import json
from flask import request
import user
import config

key = config.KEY
app = flask.Flask(__name__)

app.config["MONGO_URI"] = "mongodb+srv://david:" + key + "@apicluster.knc1y.mongodb.net/rt21Db?retryWrites=true"
mongo = PyMongo(app)
db = mongo.db
UserCollection = db.user


@app.route('/api/user/createUser', methods=['POST'])
def create_user():
    return user.create_user()


@app.route('/api/user/getUser/<object_id>', methods=['GET'])
def get_user(object_id):
    return user.get_user(object_id)


if __name__ == "__main__":
    app.run(debug=True)
