#!/bin/bash

heroku container:login
#heroku create rt21-website --region eu
docker image build -t rt21-web .
heroku container:push web --app rt21-website
heroku container:release web --app rt21-website
