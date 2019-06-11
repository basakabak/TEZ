import cv2
import numpy as np
from PIL import Image
import pytesseract

lowerBound=np.array([33,80,40])
upperBound=np.array([178,255,255])
idx=0
cam= cv2.VideoCapture(0)
kernelOpen=np.ones((5,5))
kernelClose=np.ones((20,20))

font="asad"
while True:
    ret, img=cam.read()
    img=cv2.resize(img,(500,500))

    #convert BGR to HSV
    imgHSV= cv2.cvtColor(img,cv2.COLOR_BGR2HSV)
    # create the Mask
    mask=cv2.inRange(imgHSV,lowerBound,upperBound)
    #morphology
    maskOpen=cv2.morphologyEx(mask,cv2.MORPH_OPEN,kernelOpen)
    maskClose=cv2.morphologyEx(maskOpen,cv2.MORPH_CLOSE,kernelClose)

    maskFinal=maskClose
    _,conts,h=cv2.findContours(maskFinal.copy(),cv2.RETR_EXTERNAL,cv2.CHAIN_APPROX_NONE)
    
    cv2.drawContours(img,conts,-1,(255,0,0),3)
    for i in range(len(conts)):
        x,y,w,h=cv2.boundingRect(conts[i])
        cv2.rectangle(img,(x,y),(x+w,y+h),(0,0,255), 2)
        idx+=1
        new_img=img[y:y+h,x:x+w]
        cv2.imwrite(str(idx) + '.png', new_img)
        #cv2.cv.PutText(cv2.cv.fromarray(img), str(i+1),(x,y+h),font,(0,255,255))
    cv2.imshow("maskClose",maskClose)
    cv2.imshow("maskOpen",maskOpen)
    cv2.imshow("mask",mask)
    cv2.imshow("cam",img)
    cv2.waitKey(10)

    result = cv2.bitwise_and(img,img,mask=mask)
    cv2.imshow("Result",result)

    # Path of working folder on Disk
    src_path = "C:/Users/Basak/Desktop/TEZ/imageprocessing/"
    pytesseract.pytesseract.tesseract_cmd = r'C:\Program Files (x86)\Tesseract-OCR\tesseract'
    result = pytesseract.image_to_string(Image.open(str(idx) + '.png'),config='--psm 10')

    print ('--- Start recognize text from image ---')
    print (result)

    print ("------ Done -------")
    break;