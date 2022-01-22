FROM python:buster
WORKDIR /app
RUN apt-get update
RUN apt-get install ffmpeg libsm6 libxext6  -y
COPY . .
RUN pip install -r requirements.txt
CMD [ "gunicorn" ]
