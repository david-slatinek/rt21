import main


def create_response(message_type, message, code):
    return main.app.response_class(
        response=main.json.dumps({message_type: message}),
        status=code,
        mimetype='application/json'
    )
