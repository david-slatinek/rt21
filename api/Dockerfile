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

FROM python:latest

WORKDIR /usr/src/app

COPY requirements.txt ./
RUN pip install --no-cache-dir -r requirements.txt

COPY . .

EXPOSE 5000

CMD [ "python", "./main.py" ]