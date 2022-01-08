import cv2
import numpy as np
import pandas as pd
from tensorflow.python.keras import models



def recognize(path):
    model = models.load_model("./classifier_model")

    data = pd.read_csv("labels.csv")
    class_names = []
    for _, row in data.iterrows():
        class_names.append(row["Name"])

    img = cv2.imread(path)
    img = np.asarray(img)
    img = cv2.resize(img, (32, 32), interpolation=cv2.INTER_LINEAR)
    img = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
    img = img / 255
    img = img.reshape(1, 32, 32, 1)

    prediction = model.predict(img)
    index = np.argmax(prediction)
    return class_names[index]
