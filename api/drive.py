import json
import time

from bson import json_util
from bson.objectid import ObjectId
from flask import request

from __init__ import DriveCollection, UserCollection
from common import create_response


def create_drive():
    user_id = request.form.get('user_id', None)

    if not user_id:
        return create_response('error', 'user_id not given', 400)

    if len(user_id) != 24:
        return create_response('error', 'invalid id length', 400)

    if not UserCollection.find_one({'_id': ObjectId(user_id)}):
        return create_response('error', 'user not found', 404)

    obj = {
        'user_id': user_id,
        'start': time.time(),
        'end': time.time(),
        'length': 0.0,
        'speed_limit_exceed': 0,
        'mean_speed': 0.0,
        'max_speed': 0.0,
        'nr_of_stops': 0
    }

    obj_id = DriveCollection.insert_one(obj).inserted_id
    if obj_id:
        return json.loads(json_util.dumps(obj_id)), 201
    else:
        return create_response('error', 'error when creating drive', 500)


def get_drive(drive_id):
    if len(drive_id) != 24:
        return create_response('error', 'invalid id length', 400)

    obj = DriveCollection.find_one({'_id': ObjectId(drive_id)})
    if obj:
        return json.loads(json_util.dumps(obj))
    else:
        return create_response('error', 'drive not found', 404)


def update_drive(drive_id):
    if len(drive_id) != 24:
        return create_response('error', 'invalid id length', 400)

    if not DriveCollection.find_one({'_id': ObjectId(drive_id)}):
        return create_response('error', 'drive not found', 404)

    key = request.form.get('key', None)
    value = request.form.get('value', None)

    if not key or key == '_id' or key == 'user_id':
        return create_response('error', 'key not given/valid', 400)
    if not value:
        return create_response('error', 'value not given', 400)

    keys = ['start', 'end', 'length', 'speed_limit_exceed', 'mean_speed', 'max_speed', 'nr_of_stops']

    if key not in keys:
        return create_response('error', 'key not valid', 400)

    if key == 'speed_limit_exceed' or key == 'nr_of_stops':
        try:
            value = int(value)
        except ValueError as error:
            return create_response('error', str(error), 400)
        except TypeError as error:
            return create_response('error', str(error), 400)

    if key == 'length' or key == 'mean_speed' or key == 'max_speed':
        try:
            value = float(value)
        except ValueError as error:
            return create_response('error', str(error), 400)
        except TypeError as error:
            return create_response('error', str(error), 400)
    DriveCollection.update_one({'_id': ObjectId(drive_id)}, {'$set': {key: value}})

    return get_drive(drive_id)


def delete_drive(drive_id):
    if len(drive_id) != 24:
        return create_response('error', 'invalid id length', 400)

    if not DriveCollection.find_one({'_id': ObjectId(drive_id)}):
        return create_response('error', 'drive not found', 404)

    DriveCollection.delete_one({'_id': ObjectId(drive_id)})

    return create_response('success', 'drive deleted', 200)


def get_drives(user_id):
    if len(user_id) != 24:
        return create_response('error', 'invalid id length', 400)

    if not UserCollection.find_one({'_id': ObjectId(user_id)}):
        return create_response('error', 'user not found', 404)

    drives = DriveCollection.find({'user_id': user_id})

    result = {}
    i_d = 0
    for x in drives:
        result[i_d] = json_util.loads(json_util.dumps(x))
        i_d += 1

    return json.loads(json_util.dumps(result))
