import cv2
import numpy as np
import pytesseract
from scipy import misc
from PIL import Image
import os
import imutils



def test(imge):
	scale=25
	image = cv2.imread(imge)
	image = cv2.flip(image, 1)
	height, width, channels = image.shape

	centerX,centerY=int(height/2),int(width/2)
	radiusX,radiusY= int(scale*height/100),int(scale*width/100)

	minX,maxX=centerX-radiusX,centerX+radiusX
	minY,maxY=centerY-radiusY,centerY+radiusY

	cropped = image[minX:maxX, minY:maxY]

	image = cv2.resize(cropped, (width, height)) 

	gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)
	#gray = cv2.GaussianBlur(gray, (3, 3), 0)

	edged = cv2.Canny(gray, 10, 250)
	kernel = cv2.getStructuringElement(cv2.MORPH_RECT, (7, 7))
	closed = cv2.morphologyEx(edged, cv2.MORPH_CLOSE, kernel)
	(_,cnts, _) = cv2.findContours(closed.copy(), cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)

	
	for c in cnts:
		x,y,w,h = cv2.boundingRect(c)
		rect = cv2.minAreaRect(c)
		box = cv2.boxPoints(rect)
		box = np.int0(box)
		print (box)
		print ("x : ",x," ,y : ",y," ,w : ",w," ,h : ",h)

		warped = four_point_transform(image, box)
		
		end_row, end_col=int(h*.85),int(w*.85)
		#simply use indexing to crop out the rectangle we desire
		warped=warped[10:end_row, 10:end_col]
		warped = Image.fromarray(warped)
		new_folder = '0'
		outputfilename = os.path.join("/storage/emulated/", new_folder, "myoutputfile.png")
		warped.convert('RGBA').quantize().save(outputfilename)
		#cv2.drawContours(image,[box],0,(0,0,255),2)
		
		#if w>50 and h>50:
			
		#warped =rotate(warped,90)
			

			#warped = four_point_transform(image, box)
			#new_img=warped[3:h,0:w-20]
			#new_img=warped[3:h-60,3:w-50] irot
		#peri = cv2.arcLength(c, True)
		#approx = cv2.approxPolyDP(c, 0.02 * peri, True)
		#if len(approx) == 4:
			#cv2.drawContours(image, [approx], -1, (0, 255, 0), 4)

			#asd = Image.fromarray(new_img)
			#os.remove("/storage/emulated/0/myoutputfile.jpg")
			#new_folder = '0'
			#outputfilename = os.path.join("/storage/emulated/", new_folder, "myoutputfile.jpg")
			#asd.save(outputfilename, 'JPG', quality=90)		
		return warped

"""
def test(image):
	image = cv2.imread(image)
	gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)
	gray = cv2.GaussianBlur(gray, (5, 5), 0)   ######################Bunu sil 

	# threshold the image, then perform a series of erosions +
	# dilations to remove any small regions of noise
	thresh = cv2.threshold(gray, 45, 255, cv2.THRESH_BINARY)[1]
	thresh = cv2.erode(thresh, None, iterations=2)
	thresh = cv2.dilate(thresh, None, iterations=2)

	# find contours in thresholded image, then grab the largest
	# one
	cnts = cv2.findContours(thresh.copy(), cv2.RETR_EXTERNAL,
		cv2.CHAIN_APPROX_SIMPLE)
	cnts = imutils.grab_contours(cnts)
	c = max(cnts, key=cv2.contourArea)

	# determine the most extreme points along the contour
	extLeft = tuple(c[c[:, :, 0].argmin()][0])
	extRight = tuple(c[c[:, :, 0].argmax()][0])
	extTop = tuple(c[c[:, :, 1].argmin()][0])
	extBot = tuple(c[c[:, :, 1].argmax()][0])

	# draw the outline of the object, then draw each of the
	# extreme points, where the left-most is red, right-most
	# is green, top-most is blue, and bottom-most is teal

	cv2.drawContours(image, [c], -1, (0, 255, 255), 2)
	cv2.circle(image, extLeft, 8, (0, 0, 255), -1)
	cv2.circle(image, extRight, 8, (0, 255, 0), -1)
	cv2.circle(image, extTop, 8, (255, 0, 0), -1)
	cv2.circle(image, extBot, 8, (255, 255, 0), -1)

	arr = []
	arr.append(extLeft)
	arr.append(extTop)
	arr.append(extRight)
	arr.append(extBot)

	warped = four_point_transform(image, arr)
	image = Image.fromarray(image)
	new_folder = '0'
	outputfilename = os.path.join("/storage/emulated/", new_folder, "myoutputfile.jpg")
	image.save(outputfilename, 'JPEG', quality=90)
	return image"""


def four_point_transform(image, pts):
	# obtain a consistent order of the points and unpack them
	# individually
	#rect = order_points(pts)
	rect =pts
	tl = rect[0]
	tr = rect[1]
	br = rect[2]
	bl =rect[3]
	rect = np.array([[tl],[tr],[br],[bl]], dtype = "float32")
 
	# compute the width of the new image, which will be the
	# maximum distance between bottom-right and bottom-left
	# x-coordiates or the top-right and top-left x-coordinates
	widthA = np.sqrt(((br[0] - bl[0]) ** 2) + ((br[1] - bl[1]) ** 2))
	widthB = np.sqrt(((tr[0]- tl[0]) ** 2) + ((tr[1] - tl[1]) ** 2))
	maxWidth = max(int(widthA), int(widthB))
 
	# compute the height of the new image, which will be the
	# maximum distance between the top-right and bottom-right
	# y-coordinates or the top-left and bottom-left y-coordinates
	heightA = np.sqrt(((tr[0] - br[0]) ** 2) + ((tr[1] - br[1]) ** 2))
	heightB = np.sqrt(((tl[0] - bl[0]) ** 2) + ((tl[1] - bl[1]) ** 2))
	maxHeight = max(int(heightA), int(heightB))
 
	# now that we have the dimensions of the new image, construct
	# the set of destination points to obtain a "birds eye view",
	# (i.e. top-down view) of the image, again specifying points
	# in the top-left, top-right, bottom-right, and bottom-left
	# order
	dst = np.array([
		[0, 0],
		[maxWidth - 1, 0],
		[maxWidth - 1, maxHeight - 1],
		[0, maxHeight - 1]], dtype = "float32")
 
	# compute the perspective transform matrix and then apply it
	M = cv2.getPerspectiveTransform(rect, dst)
	warped = cv2.warpPerspective(image, M, (maxWidth, maxHeight))
 
	# return the warped image
	return warped		

"""

def test(image):

	maze = cv2.imread(image)

	(h, w) = maze.shape[:2]

	center = (w / 2, h / 2)

	corners = getCorners(maze)
	b = corners[0][0]
	c = corners[1][0]
	a = (0,corners[0][0][1])
	gray = cv2.cvtColor(maze, cv2.COLOR_BGR2GRAY)
	gray = cv2.GaussianBlur(gray, (3, 3), 0)
	rotated =rotate(maze,0)

	blurred_frame = cv2.GaussianBlur(maze, (5, 5), 0)
	hsv = cv2.cvtColor(blurred_frame, cv2.COLOR_BGR2HSV)
	lower_blue = np.array([0, 0, 0])
	upper_blue = np.array([255, 255, 200])
	mask = cv2.inRange(hsv, lower_blue, upper_blue)
	_, contours, _ = cv2.findContours(mask, cv2.RETR_TREE, cv2.CHAIN_APPROX_NONE)
	
	for contour in contours:
		cv2.drawContours(maze, contour, -1, (0, 255, 0), 3)
		x2,y2,w23,h23 = cv2.boundingRect(contour)
		if (x2 == 0):
			continue	
		print ("x : ",x2," ,y : ",y2," ,w : ",w23," ,h : ",h23)
		if(w23 > 1 and h23 > 1 ):
			print("burdaaaaaaaaaaaa")
			new_img=maze[y2+11:y2+h23,x2+11:x2+w23]
			corner =getCorners(new_img)
			print ("corners ",corner)
			#print ("aasasdfafdfdas ",corner[1])
			cv2.circle(new_img, (corner[0][0][0],corner[0][0][1]), 5,(0, 100, 150),-1)
			cv2.circle(new_img, (corner[1][0][0],corner[1][0][1]), 5,(0, 100, 150),-1)
			#cv2.circle(new_img, (corner[2][0][0],corner[2][0][1]), 5,(0, 100, 150),-1)
			#cv2.circle(new_img, (corner[3][0][0],corner[3][0][1]), 5,(0, 100, 150),-1)
			img = Image.fromarray(new_img)
			new_folder = '0'
			outputfilename = os.path.join("/storage/emulated/", new_folder, "myoutputfile.jpg")
			img.save(outputfilename, 'JPEG', quality=1000)
			#return outputfilename
		print("girmiyooor")
		return outputfilename	

	or i in range(0,4):
		rotated =rotate(rotated,0)
		cropped =crop(rotated)
	
		#new_img=gray[0:50,0:50]
		img = Image.fromarray(cropped)
		#DATA_FOLDER = os.getenv('EXTERNAL_STORAGE')
		#img.save('/sdcard/myphoto.png')
		#img[1,5] = (0,0,0)
		new_folder = '0'
		outputfilename = os.path.join("/storage/emulated/", new_folder, "myoutputfile.jpg")
		img.save(outputfilename, 'JPEG', quality=90)
		return outputfilename		

		"""

def getCorners(image):

	gray = cv2.cvtColor(image,cv2.COLOR_BGR2GRAY)
	gray = np.float32(gray)
	corners = cv2.goodFeaturesToTrack(gray,4,0.01,20)
	corners = np.int0(corners)
	#print(corners)
	return corners  

def rotate(image,myAngle):
	(h, w) = image.shape[:2]
	center = (w / 2, h / 2)
	M = cv2.getRotationMatrix2D(center, myAngle, 1.0)
	rotated = cv2.warpAffine(image, M, (w, h),borderValue=(255,255,255))	
	return rotated	

def crop(image):
	#cv2.imshow("Gray", gray)
	#cv2.waitKey(0)

	# detect edges in the image
	edged = cv2.Canny(image, 10, 250)
	#cv2.imshow("Edged", edged)
	#cv2.waitKey(0)

	# construct and apply a closing kernel to 'close' gaps between 'white'
	# pixels
	kernel = cv2.getStructuringElement(cv2.MORPH_RECT, (7, 7))
	closed = cv2.morphologyEx(edged, cv2.MORPH_CLOSE, kernel)
	##cv2.imshow("Closed", closed)
	#cv2.waitKey(0)

	# find contours (i.e. the 'outlines') in the image and initialize the
	# total number of books found
	(_,cnts, _) = cv2.findContours(closed.copy(), cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)
	for c2 in cnts:
		x2,y2,w23,h23 = cv2.boundingRect(c2)

		print ("x : ",x2," ,y : ",y2," ,w : ",w23," ,h : ",h23)
		new_img=image[y2:y2+h23,x2:x2+w23]
		return new_img		