#/bin/bash

docker image build -t rt21 .
heroku container:login
heroku container:push web --app rt210
heroku container:release web --app rt210
