import main


def create_invalid(field_name):
    return main.app.response_class(
        response=main.json.dumps({"error": field_name + " not given or invalid"}),
        status=400,
        mimetype='application/json'
    )


def invalid_id():
    return main.app.response_class(
        response=main.json.dumps({"error": "invalid id length"}),
        status=400,
        mimetype='application/json'
    )
