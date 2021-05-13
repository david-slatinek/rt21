import main


def get_sign(sign_id):
    if len(sign_id) != 24:
        return main.invalid_id()

    obj = main.LocationCollection.find_one({"_id": main.ObjectId(sign_id)})
    if obj:
        return main.json.loads(main.json_util.dumps(obj))
    else:
        return main.create_invalid('sign not found', 404)


def create_sign():
    drive_id = main.request.form.get('drive_id', None)

    if not drive_id:
        return main.create_invalid('drive_id not given')

    if len(drive_id) != 24:
        return main.invalid_id()

    sign_type = main.request.form.get('sign_type', None)
    latitude = main.request.form.get('latitude', None)
    longitude = main.request.form.get('longitude', None)

    if not sign_type:
        return main.create_invalid('type not given')
    if not latitude:
        return main.create_invalid('latitude not given')
    if not longitude:
        return main.create_invalid('longitude not given')

    value = main.get_drive(drive_id)
    if not isinstance(value, dict):
        return value

    obj = {
        'drive_id': drive_id,
        'type': sign_type,
        'latitude': latitude,
        'longitude': longitude,
    }

    obj_id = main.SignCollection.insert_one(obj).inserted_id
    if obj_id is not None:
        return main.json.loads(main.json_util.dumps(obj_id)), 201
    else:
        return main.create_invalid('error when creating sign', 500)
