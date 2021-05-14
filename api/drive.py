from datetime import datetime
import main


def create_drive():
    user_id = main.request.form.get('user_id', None)

    if not user_id:
        return main.create_response('error', 'user_id not given', 400)

    if len(user_id) != 24:
        return main.create_response('error', 'invalid id length', 400)

    if not main.UserCollection.find_one({"_id": main.ObjectId(user_id)}):
        return main.create_response('error', 'drive not found', 404)

    obj = {
        'user_id': user_id,
        'start': datetime.now(),
        'end': datetime.now(),
        'length': 0.0,
        'speed_limit_exceed': 0,
        'mean_speed': 0.0,
        'max_speed': 0.0,
        'nr_of_stops': 0
    }

    obj_id = main.DriveCollection.insert_one(obj).inserted_id
    if not obj_id:
        return main.json.loads(main.json_util.dumps(obj_id)), 201
    else:
        return main.create_response('error', 'error when creating drive', 500)


def get_drive(drive_id):
    if len(drive_id) != 24:
        return main.create_response('error', 'invalid id length', 400)

    obj = main.DriveCollection.find_one({"_id": main.ObjectId(drive_id)})
    if obj:
        return main.json.loads(main.json_util.dumps(obj))
    else:
        return main.create_response('error', 'drive not found', 404)


def update_drive(drive_id):
    if len(drive_id) != 24:
        return main.create_response('error', 'invalid id length', 400)

    if not main.DriveCollection.find_one({"_id": main.ObjectId(drive_id)}):
        return main.create_response('error', 'drive not found', 404)

    key = main.request.form.get('key', None)
    value = main.request.form.get('value', None)

    if not key or key == "_id" or key == "user_id":
        return main.create_response('error', 'key not given/valid', 400)
    if not value:
        return main.create_response('error', 'value not given', 400)

    if key != "start" and key != "end" and key != "length" and key != "speed_limit_exceed" \
            and key != "mean_speed" and key != "max_speed" and key != "nr_of_stops":
        return main.create_response('error', "key not valid", 400)

    if key == "speed_limit_exceed" or key == "nr_of_stops":
        try:
            value = int(value)
        except ValueError:
            return main.create_response('error', "value not valid", 400)
        except TypeError:
            return main.create_response('error', "value not valid", 400)

    if key == "length" or key == "mean_speed" or key == "max_speed":
        try:
            value = float(value)
        except ValueError:
            return main.create_response('error', "value not valid", 400)
        except TypeError:
            return main.create_response('error', "value not valid", 400)
    main.DriveCollection.update_one({'_id': main.ObjectId(drive_id)}, {'$set': {key: value}})

    return get_drive(drive_id)


def delete_drive(drive_id):
    if len(drive_id) != 24:
        return main.create_response('error', 'invalid id length', 400)

    if not main.DriveCollection.find_one({"_id": main.ObjectId(drive_id)}):
        return main.create_response('error', 'drive not found', 404)

    main.DriveCollection.delete_one({'_id': main.ObjectId(drive_id)})

    return main.create_response('success', 'drive deleted', 200)
