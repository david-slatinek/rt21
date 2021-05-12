#FROM alpine:latest
#
#RUN apk add cmd:pip3
#RUN apk add --no-cache python3-dev \
#    && pip3 install --upgrade pip
#
#WORKDIR /app
#
#COPY . /app
#
#RUN pip3 --no-cache install -r requirements.txt
#
#EXPOSE 5000
#
#ENTRYPOINT ["python3"]
#CMD ["main.py"]

FROM python:3.9.5

WORKDIR /app

COPY requirements.txt .

RUN pip install --no-cache-dir -r requirements.txt

COPY . .

CMD [ "python", "./main.py" ]