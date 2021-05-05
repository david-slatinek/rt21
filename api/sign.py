import main


def get_sign(sign_id):
    if len(sign_id) != 24:
        return main.invalid_id()

    obj = main.LocationCollection.find_one({"_id": main.ObjectId(sign_id)})
    if obj:
        return main.json.loads(main.json_util.dumps(obj))
    else:
        return main.app.response_class(
            response=main.json.dumps({"error": "sign not found"}),
            status=404,
            mimetype='application/json'
        )


def create_sign():
    drive_id = main.request.form.get('drive_id', 'default_drive_id')

    if len(drive_id) != 24:
        return main.invalid_id()

    sign_type = main.request.form.get('sign_type', 'default_sign_type')
    latitude = main.request.form.get('latitude', 'default_latitude')
    longitude = main.request.form.get('longitude', 'default_longitude')

    if drive_id == "default_drive_id":
        return main.create_invalid('drive_id')
    if sign_type == "default_sign_type":
        return main.create_invalid('type')
    if latitude == "default_latitude":
        return main.create_invalid('latitude')
    if longitude == "default_longitude":
        return main.create_invalid('longitude')

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
        return main.app.response_class(
            response=main.json.dumps({"error": "error when creating sign"}),
            status=500,
            mimetype='application/json'
        )
