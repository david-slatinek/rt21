#/bin/bash

heroku container:login
#heroku create rt21-api
docker image build -t rt21 .
heroku container:push web --app rt21-api
heroku container:release web --app rt21-api
