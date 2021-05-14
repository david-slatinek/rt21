import main


def create_sign():
    drive_id = main.request.form.get('drive_id', None)

    if not drive_id:
        return main.create_response('error', 'drive_id not given', 400)

    if len(drive_id) != 24:
        return main.create_response('error', 'invalid id length', 400)

    if not main.DriveCollection.find_one({"_id": main.ObjectId(drive_id)}):
        return main.create_response('error', 'drive not found', 404)

    sign_type = main.request.form.get('sign_type', None)

    try:
        latitude = float(main.request.form.get('latitude', None))
    except ValueError:
        latitude = None
    except TypeError:
        latitude = None

    try:
        longitude = main.request.form.get('longitude', None)
    except ValueError:
        longitude = None
    except TypeError:
        longitude = None

    if not sign_type:
        return main.create_response('error', 'type not given', 400)
    if not latitude:
        return main.create_response('error', 'latitude not given', 400)
    if not longitude:
        return main.create_response('error', 'longitude not given', 400)

    obj = {
        'drive_id': drive_id,
        'type': sign_type,
        'latitude': latitude,
        'longitude': longitude,
    }

    obj_id = main.SignCollection.insert_one(obj).inserted_id
    if not obj_id:
        return main.json.loads(main.json_util.dumps(obj_id)), 201
    else:
        return main.create_response('error', 'error when creating sign', 500)


def get_sign(sign_id):
    if len(sign_id) != 24:
        return main.create_response('error', 'invalid id length', 400)

    obj = main.LocationCollection.find_one({"_id": main.ObjectId(sign_id)})
    if obj:
        return main.json.loads(main.json_util.dumps(obj))
    else:
        return main.create_response('error', 'sign not found', 404)


def update_sign(sign_id):
    if len(sign_id) != 24:
        return main.create_response('error', 'invalid id length', 400)

    if not main.SignCollection.find_one({"_id": main.ObjectId(sign_id)}):
        return main.create_response('error', 'sign not found', 404)

    key = main.request.form.get('key', None)
    value = main.request.form.get('value', None)

    if not key or key == "_id" or key == "drive_id":
        return main.create_response('error', 'key not given/valid', 400)
    if not value:
        return main.create_response('error', 'value not given', 400)

    if key != "type" and key != "latitude" and key != "longitude":
        return main.create_response('error', "key not valid", 400)

    if key == "latitude" or key == "longitude":
        try:
            value = float(value)
        except ValueError:
            return main.create_response('error', "value not valid", 400)
        except TypeError:
            return main.create_response('error', "value not valid", 400)

    main.SignCollection.update_one({'_id': main.ObjectId(sign_id)}, {'$set': {key: value}})

    return get_sign(sign_id)


def delete_sign(sign_id):
    if len(sign_id) != 24:
        return main.create_response('error', 'invalid id length', 400)

    if not main.SignCollection.find_one({"_id": main.ObjectId(sign_id)}):
        return main.create_response('error', 'location not found', 404)

    main.SignCollection.delete_one({'_id': main.ObjectId(sign_id)})

    return main.create_response('success', 'location deleted', 200)
