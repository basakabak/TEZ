import cv2
import numpy as np
import pytesseract
from scipy import misc
from PIL import Image
import os
import imutils



def test2(imge):
	scale=25
	image = cv2.imread(imge)
	image = cv2.flip(image, 1)
	height, width, channels = image.shape

	centerX,centerY=int(height/2),int(width/2)
	radiusX,radiusY= int(scale*height/100),int(scale*width/100)

	minX,maxX=centerX-radiusX,centerX+radiusX
	minY,maxY=centerY-radiusY,centerY+radiusY

	cropped = image[minX:maxX, minY:maxY]
	resized_cropped = cv2.resize(cropped, (width, height)) 
	warped = Image.fromarray(resized_cropped)
	new_folder = '0'
	outputfilename = os.path.join("/storage/emulated/", new_folder, "myoutputfile.jpg")
	warped.save(outputfilename, 'JPEG', quality=360)
	
	return warped