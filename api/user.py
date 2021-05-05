from main import app, json, UserCollection, ObjectId, create_invalid, invalid_id
from main import json_util, request


def get_user(object_id):
    if len(object_id) != 24:
        return invalid_id()

    obj = UserCollection.find_one({"_id": ObjectId(object_id)})
    if obj:
        return json.loads(json_util.dumps(obj))
    else:
        return app.response_class(
            response=json.dumps({"error": "user not found"}),
            status=404,
            mimetype='application/json'
        )


def register():
    name = request.form.get('name', 'default_name')
    last_name = request.form.get('last_name', 'default_last_name')
    try:
        age = int(request.form.get('age', -1))
    except ValueError:
        age = -1
    nickname = request.form.get('nickname', 'default_nickname')
    email = request.form.get('email', 'default_email')
    password = request.form.get('password', 'default_password')

    if name == 'default_name':
        return create_invalid('name')
    if last_name == 'default_last_name':
        return create_invalid('last_name')
    if age < 18:
        return create_invalid('age')
    if nickname == 'default_nickname':
        return create_invalid('nickname')
    if email == 'default_email':
        return create_invalid('email')
    if password == 'default_password':
        return create_invalid('password')

    obj = {
        'name': name,
        'last_name': last_name,
        'age': age,
        'nickname': nickname,
        'email': email,
        'password': password
    }

    obj_id = UserCollection.insert_one(obj).inserted_id
    if obj_id is not None:
        return json.loads(json_util.dumps(obj_id)), 201
    else:
        return app.response_class(
            response=json.dumps({"error": "error when creating user"}),
            status=500,
            mimetype='application/json'
        )


def login():
    email = request.form.get('email', 'default_email')
    password = request.form.get('password', 'default_password')

    if email == 'default_email':
        return create_invalid('email')
    if password == 'default_password':
        return create_invalid('password')

    obj = UserCollection.find_one({"$and": [{"email": email}, {"password": password}]})
    if obj is not None:
        return json.loads(json_util.dumps(obj))
    else:
        return app.response_class(
            response=json.dumps({"error": "invalid email/password"}),
            status=404,
            mimetype='application/json'
        )
