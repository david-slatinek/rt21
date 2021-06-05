from os import truncate
import cv2
import sys, getopt
import numpy as np
from numpy.core.fromnumeric import shape
from numpy.lib.type_check import imag
from scipy.stats import itemfreq

def get_dominant_color(image, n_colors):
    pixels = np.float32(image).reshape((-1, 3))
    criteria = (cv2.TERM_CRITERIA_EPS + cv2.TERM_CRITERIA_MAX_ITER, 200, .1)
    flags = cv2.KMEANS_RANDOM_CENTERS
    flags, labels, centroids = cv2.kmeans(
        pixels, n_colors, None, criteria, 10, flags)
    palette = np.uint8(centroids)
    return palette[np.argmax(itemfreq(labels)[:, -1])]

def main(argv):
    image = ''
    
    #parse argv values
    try:
        opts, args = getopt.getopt(argv, "hi:", ["inputfile="])
    except getopt.GetoptError:
        print('python main.py -i <inputfile>')
        sys.exit(2)

    if opts != []:
        for opt, arg in opts:
            if opt == '-h':
                print('python main.py -i <inputfile>')
                sys.exit()
            elif opt in ('-i', '--inputfile'):
                image = arg
            else:
                print("Run: \"python main.py -h\" for help.")
                sys.exit()
    else:
        print("No arguments were given...")
        print("Run: \"python main.py -h\" for help.")
        sys.exit()
        #image = "StopSignDataset/16.jpg"

    #input images convert to gray and applay medianblur
    image = cv2.imread(image)
    gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)
    gray = cv2.medianBlur(gray, 35)

    # cv2.imshow("gray", gray)
    # cv2.waitKey()

    circles = cv2.HoughCircles(gray, cv2.HOUGH_GRADIENT, 1, 50, param1=120, param2=40)

    if not circles is None:
        circles = np.uint16(np.around(circles))
        max_r, max_i = 0, 0
        for i in range(len(circles[:, :, 2][0])):
            if circles[:, :, 2][0][i] > 50 and circles[:, :, 2][0][i] > max_r:
                max_i = i
                max_r = circles[:, :, 2][0][i]

        x, y, r = circles[:, :, :][0][max_i]
        if y > r and x > r:
            square = image[y-r:y+r, x-r:x+r]

            dominant_color = get_dominant_color(square, 2)
            if dominant_color[2] > 100:
                print("STOP")
            else:
                print("N/A")

        for i in circles[0, :]:
            cv2.circle(image, (i[0], i[1]), i[2], (0, 255, 0), 2)
            cv2.circle(image, (i[0], i[1]), 2, (0, 0, 255), 3)
    cv2.imshow('camera', image)
    cv2.waitKey()

cv2.destroyAllWindows()

if __name__ == "__main__":
    # TODO - change circle detection to octagon detection
    # TODO - check if detected ocagon has (white) text on it and red background
    main(sys.argv[1:])