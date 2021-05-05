from datetime import datetime
import main


def create_drive():
    user_id = main.request.form.get('user_id', 'default_user_id')

    if user_id == "default_user_id":
        return main.create_invalid('user_id')

    if len(user_id) != 24:
        return main.invalid_id()

    value = main.get_user(user_id)
    if not isinstance(value, dict):
        return value

    obj = {
        'user_id': user_id,
        'start': datetime.now(),
        'end': datetime.now(),
        'length': 0,
        'speed_limit_exceed': 0,
        'mean_speed': 0.0,
        'max_speed': 0.0,
        'nr_of_stops': 0
    }

    obj_id = main.DriveCollection.insert_one(obj).inserted_id
    if obj_id is not None:
        return main.json.loads(main.json_util.dumps(obj_id)), 201
    else:
        return main.app.response_class(
            response=main.json.dumps({"error": "error when creating drive"}),
            status=500,
            mimetype='application/json'
        )


def get_drive(drive_id):
    if len(drive_id) != 24:
        return main.invalid_id()

    obj = main.DriveCollection.find_one({"_id": main.ObjectId(drive_id)})
    if obj:
        return main.json.loads(main.json_util.dumps(obj))
    else:
        return main.app.response_class(
            response=main.json.dumps({"error": "drive not found"}),
            status=404,
            mimetype='application/json'
        )
