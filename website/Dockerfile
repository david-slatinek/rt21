FROM node

RUN mkdir -p /usr/src/app

WORKDIR /usr/src/app

COPY package.json package.json

RUN npm install --silent && npm install create-react-app -g --silent

COPY . .

CMD ["npm", "start"]
