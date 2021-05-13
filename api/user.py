from main import app, json, UserCollection, ObjectId, create_response
from main import json_util, request, bcrypt


def register():
    name = request.form.get('name', None)
    last_name = request.form.get('last_name', None)
    try:
        age = int(request.form.get('age', None))
    except ValueError:
        age = None
    nickname = request.form.get('nickname', None)
    email = request.form.get('email', None)
    password = request.form.get('password', None)

    if not name:
        return create_response('error', 'name not given', 400)
    if not last_name:
        return create_response('error', 'last_name not given', 400)
    if not age:
        return create_response('error', 'age not given', 400)
    if not nickname:
        return create_response('error', 'nickname not given', 400)
    if not email:
        return create_response('error', 'email not given', 400)
    if not password:
        return create_response('error', 'password not given', 400)

    if UserCollection.find_one({"email": email}):
        return create_response('error', 'email already exists', 400)

    obj = {
        'name': name,
        'last_name': last_name,
        'age': age,
        'nickname': nickname,
        'email': email,
        'password': bcrypt.generate_password_hash(password.encode('utf-8')).decode('utf-8')
    }

    obj_id = UserCollection.insert_one(obj).inserted_id
    if obj_id:
        return json.loads(json_util.dumps(obj_id)), 201
    else:
        return create_response('error', 'error when creating user', 500)


def login():
    email = request.form.get('email', None)
    password = request.form.get('password', None)

    if not email:
        return create_response('error', 'email not given', 400)
    if not password:
        return create_response('error', 'password not given', 400)

    obj = UserCollection.find_one({"email": email})
    if obj:
        user = json.loads(json_util.dumps(obj))

        if bcrypt.check_password_hash(user["password"], password.encode('utf-8')):
            user.pop('password')
            return user
        else:
            return create_response('error', 'invalid email/password', 404)
    else:
        return create_response('error', 'invalid email/password', 404)


def get_user(user_id):
    if len(user_id) != 24:
        return create_response('error', 'invalid id length', 400)

    obj = UserCollection.find_one({"_id": ObjectId(user_id)})
    if obj:
        user = json.loads(json_util.dumps(obj))
        user.pop("password")
        return user
    else:
        return create_response('error', 'user not found', 404)


def update_user(user_id):
    if len(user_id) != 24:
        return create_response('error', 'invalid id length', 400)

    if not UserCollection.find_one({"_id": ObjectId(user_id)}):
        return create_response('error', 'user not found', 404)

    key = request.form.get('key', None)
    value = request.form.get('value', None)

    if not key or key == "_id":
        return create_response('error', 'key not given/valid', 400)
    if not value:
        return create_response('error', 'value not given', 400)

    if key != "name" and key != "last_name" and key != "age" and key != "nickname" \
            and key != "email" and key != "password":
        return create_response('error', "key not valid", 400)

    if key == "age":
        try:
            value = int(value)
        except ValueError:
            return create_response('age', "age not valid", 400)

    if key == "password":
        value = bcrypt.generate_password_hash(value.encode('utf-8')).decode('utf-8')

    UserCollection.update_one({'_id': ObjectId(user_id)}, {'$set': {key: value}})

    return get_user(user_id)
