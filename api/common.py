import main


def create_invalid(message, code=400):
    return main.app.response_class(
        response=main.json.dumps({"error": message}),
        status=code,
        mimetype='application/json'
    )


def invalid_id():
    return main.app.response_class(
        response=main.json.dumps({"error": "invalid id length"}),
        status=400,
        mimetype='application/json'
    )


def invalid_api_key():
    return main.app.response_class(
        response=main.json.dumps({"error": "api key not given or invalid"}),
        status=401,
        mimetype='application/json'
    )
