import cv2
import glob
from cv2 import data
import numpy as np
from numpy.lib.polynomial import RankWarning
from tensorflow.python.keras import layers
from keras import layers, models


data_indexes = [2, 14, 18]
class_names = ["Speed limit (50km/h)", "Stop", "General caution"]

images = []
labels = []

print("Importing data...")
print("Number of roadsings to learn: ", len(data_indexes))
for i in range(0, len(data_indexes)):
    path = "myData/" + str(data_indexes[i]) + "/*"
    for filename in glob.glob(path):
        img = cv2.imread(filename)
        img = cv2.cvtColor(img,cv2.COLOR_BGR2RGB)
        img = cv2.resize(img, (32, 32), interpolation = cv2.INTER_LINEAR)
        images.append(img)
        labels.append(i)

    print(data_indexes[i], end=" ")
print()
print("Import done")

images = np.array(images)
labels = np.array(labels)

model = models.Sequential()
model.add(layers.Conv2D(32,(3,3),activation="relu",input_shape=(32,32,3)))
model.add(layers.MaxPooling2D((2,2)))
model.add(layers.Conv2D(64,(3,3),activation="relu"))
model.add(layers.MaxPooling2D((2,2)))
model.add(layers.Conv2D(64,(3,3),activation="relu"))
model.add(layers.Flatten())
model.add(layers.Dense(64,activation="relu"))
model.add(layers.Dense(10,activation="softmax"))

model.compile(optimizer="adam",loss="sparse_categorical_crossentropy",metrics=["accuracy"])
model.fit(images,labels,epochs=10,verbose=1)
model.save("classifier_model")