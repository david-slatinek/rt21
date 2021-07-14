# rt21

<p>
    <img alt="Docker" src="https://img.shields.io/badge/docker-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white"/>
    <img alt="Shell Script" src="https://img.shields.io/badge/shell_script-%23121011.svg?style=for-the-badge&logo=gnu-bash&logoColor=white"/>
    <img alt="Heroku" src="https://img.shields.io/badge/heroku-%23430098.svg?style=for-the-badge&logo=heroku&logoColor=white"/>
    <img alt="Nginx" src="https://img.shields.io/badge/nginx-%23009639.svg?style=for-the-badge&logo=nginx&logoColor=white"/>
    <img alt="Linux" src="https://img.shields.io/badge/Linux-FCC624?style=for-the-badge&logo=linux&logoColor=black">
    <img alt="Linux" src="https://img.shields.io/badge/Linux_Mint-87CF3E?style=for-the-badge&logo=linux-mint&logoColor=white">
    <img alt="MongoDB" src ="https://img.shields.io/badge/MongoDB-%234ea94b.svg?style=for-the-badge&logo=mongodb&logoColor=white"/>
    <img alt="Python" src="https://img.shields.io/badge/python-%2314354C.svg?style=for-the-badge&logo=python&logoColor=white"/>
    <img alt="Flask" src="https://img.shields.io/badge/flask-%23000.svg?style=for-the-badge&logo=flask&logoColor=white"/>
    <img alt="Postman" src="https://img.shields.io/badge/Postman-FF6C37?style=for-the-badge&logo=postman&logoColor=red" />
    <img alt="HTML5" src="https://img.shields.io/badge/html5-%23E34F26.svg?style=for-the-badge&logo=html5&logoColor=white"/>
    <img alt="Bootstrap" src="https://img.shields.io/badge/bootstrap-%23563D7C.svg?style=for-the-badge&logo=bootstrap&logoColor=white"/>
    <img alt="React" src="https://img.shields.io/badge/react-%2320232a.svg?style=for-the-badge&logo=react&logoColor=%2361DAFB"/>
    <img alt="Npm" src="https://img.shields.io/badge/npm-CB3837?style=for-the-badge&logo=npm&logoColor=white"/>
    <img alt="Android" src="https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white" />
    <img alt="Java" src="https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=java&logoColor=white"/>
    <img alt="OpenCV" src="https://img.shields.io/badge/OpenCV-27338e?style=for-the-badge&logo=OpenCV&logoColor=white"/>
    <img alt="TensorFlow" src="https://img.shields.io/badge/TensorFlow-%23FF6F00.svg?style=for-the-badge&logo=TensorFlow&logoColor=white" />
    <img alt="Keras" src="https://img.shields.io/badge/Keras-%23D00000.svg?style=for-the-badge&logo=Keras&logoColor=white"/>
    <img alt="NumPy" src="https://img.shields.io/badge/numpy-%23013243.svg?style=for-the-badge&logo=numpy&logoColor=white" />
    <img alt="Pandas" src="https://img.shields.io/badge/pandas-%23150458.svg?style=for-the-badge&logo=pandas&logoColor=white" />
    <img alt="Markdown" src="https://img.shields.io/badge/markdown-%23000000.svg?style=for-the-badge&logo=markdown&logoColor=white"/>
    <img alt="LaTeX" src="https://img.shields.io/badge/latex-%23008080.svg?style=for-the-badge&logo=latex&logoColor=white"/>
    <img alt="Git" src="https://img.shields.io/badge/git-%23F05033.svg?style=for-the-badge&logo=git&logoColor=white"/>
    <img alt="GitHub" src="https://img.shields.io/badge/github-%23121011.svg?style=for-the-badge&logo=github&logoColor=white"/>
</p>

# About
Project at big data analysis for real-world applications at FERI, 2nd year, CS. The project theme was traffic. The android application takes camera feed and sends that image to API for road signs recognition. Android app also uses GPS to track location and detects vibration to determine road quality. API stores all essential info in the database. The website displays stored data and other statistical info to the user.

<br>
The project consists of 4 main components: database, API, website and android application as shown in the following image and table:

<p align="center">
  <img alt="Project components" src="documents/images/project_components.png">
</p>

<br>

| Project section | Technologies used         |
| --------------- | ------------------------- |
| Server          | Docker, bash              |
| Database        | MongoDB                   |
| API             | Flask                     |
| Android app     | Java                      |
| Computer vision | OpenCV, TensorFlow, Keras |
| Website         | HTML5, Bootstrap, React   |
| Documents       | Latex                     |

# Table of Contents
- [Server](#server)
- [Database](#database)
- [API](#api)
- [Android app](#android-app)
- [Computer vision](#computer-vision)
- [Website](#website)

# Server
For website and API hosting, we choose [Heroku](https://www.heroku.com). Both applications are running inside docker containers, and by doing that, we achieve effortless transfer to another hosting provider in case of necessity. By using Heroku, all apps automatically get support for HTTPS protocol. For the web server, we choose Nginx.

Contributor: [David Slatinek](https://github.com/david-slatinek).

# Database
For the database, we choose the NoSQL database type, specifically MongoDB. The database is being hosted by [MongoDB Atlas](https://www.mongodb.com/cloud/atlas). In the database, we store information about the user, his drives, the locations of these drives, and information about traffic signs.

Collections can be seen from the following image:
<p align="center">
  <img alt="Collections" src="documents/images/collections.png">
</p>

Contributor: [David Slatinek](https://github.com/david-slatinek).

# API
The API serves as an intermediate link between the clients and the database. It limits unauthorized access to the database and makes the development of front-end applications simpler, as the developers working on it are not involved in retrieving data from the database, but instead retrieve it in a specific format and then use it in further development.

The API was made with python framework flask, follows the REST architectural style, and returns data in JSON format. On the security aspect, the system contains the following security mechanisms:
1. API key.
2. HTTPS protocol.

One of the methods:
```python
@app.route('/api/user/<user_id>', methods=['GET'])
def get_user(user_id):
    if request.headers.get('X-API-Key') != api_key:
        return create_response("error", "api key not given or invalid", 401)
    return user.get_user(user_id)
```

The API supports all CRUD operations and can also identify traffic signs from a picture.

Contributor: [David Slatinek](https://github.com/david-slatinek).

# Android app
Android app was made with java. The main app functionality is an image and data capture from sensors and sending them to the server. The app uses GPS to track location and detects vibration to determine road quality. In addition to that, the app also monitors speed.

<p align="center">
  <img alt="App main form" src="documents/images/app_main.png" height=520 width=300>
</p>

Contributors: [Marcel Iskrač](https://github.com/iskraM), [Marko Hiršel](https://github.com/markoHirsel).

# Computer vision 
For traffic sign recognition, we made a program with a convolutional neural network. The program is called by API when it receives an appropriate request.

```python
prediction = model.predict(img)
index = np.argmax(prediction)
return class_names[index]
```

```python
from detectRoadSign import recognize
return main.create_response('sign_type', recognize("image" + file_ext), 200)
```

Contributor: [Marcel Iskrač](https://github.com/iskraM).

# Website
The website was created using the React library, HTML and CSS, and Boostrap, which was used for easy design. We used React for the layout and calls to the application components and for communication between the API and the website. The main website functionality is data visualization.

![Website - road sign](/documents/images/website_sign.png)
![Website - road quality](/documents/images/website_road.png)

Contributor: [Marcel Iskrač](https://github.com/iskraM).
