wsgi_app = "app:app"

accesslog = "access.log"
errorlog = "error.log"
capture_output = True

limit_request_fields = 20

preload_app = True

backlog = 16

workers = 2
threads = 2
max_requests = 20
max_requests_jitter = 20
