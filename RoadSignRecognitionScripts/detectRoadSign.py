import cv2
import numpy as np

from tensorflow import keras
from skimage import io
from keras import models


model = models.load_model("classifier_model")
class_names = ["Speed limit (50km/h)", "General caution", "Stop"]
#img = cv2.imread("testing/50.jpg")
img = cv2.imread("StopSignDataset/6.jpg")

img = cv2.resize(img, (32, 32), interpolation = cv2.INTER_LINEAR)
prediction = model.predict(np.array([img]))
index = np.argmax(prediction)
print(class_names[index])