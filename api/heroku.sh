#!/bin/bash

heroku container:login
#heroku create rt21-api --region eu
docker image build -t rt21-api .
heroku container:push web --app rt21-api
heroku container:release web --app rt21-api
