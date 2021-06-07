import os
import cv2
import sys
import getopt

os.environ['TF_CPP_MIN_LOG_LEVEL'] = '3'    # INFO, ERROR AND WARNINGS are not prined
import tensorflow
import numpy as np
import pandas as pd

from skimage import io
from tensorflow.python.keras import models

def main(argv):
    image = ''
    
    # parse argv values
    try:
        opts, args = getopt.getopt(argv, "hi:", ["inputfile="])
    except getopt.GetoptError:
        print('python detectRoadSign.py -i <inputfile>')
        sys.exit(2)

    if opts != []:
        for opt, arg in opts:
            if opt == '-h': # print help for running script
                print('python detectRoadSign.py -i <inputfile>')
                sys.exit()
            elif opt in ('-i', '--inputfile'):  # parse given path for image
                image = arg
            else:   # error: incorect flags given
                print("Run: \"python detectRoadSign.py -h\" for help.") 
                sys.exit()
    else:   # no arguments give
        print("No arguments were given...")
        print("Run: \"python detectRoadSign.py -h\" for help.")
        sys.exit()
        # image = "StopSignDataset/16.jpg"

    # load model that contains data for predicting roadsings
    model = models.load_model("classifier_model")
    
    data= pd.read_csv("labels.csv")
    class_names = []
    # store roadsing names
    for _, row in data.iterrows():
        class_names.append(row["Name"])
 
    # prepare image for predicting the roadsings
    img = cv2.imread(image)
    img = np.asarray(img)
    img = cv2.resize(img, (32, 32), interpolation = cv2.INTER_LINEAR)
    img = cv2.cvtColor(img,cv2.COLOR_BGR2GRAY)
    img = img/255
    img = img.reshape(1, 32, 32, 1)

    prediction = model.predict(img)
    index = np.argmax(prediction)
    print(class_names[index])

if __name__ == "__main__":
    # python detectRoadSign.py -i testing/stop.png
    main(sys.argv[1:])