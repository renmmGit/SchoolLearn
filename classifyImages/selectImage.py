import os
import urllib.error as error
import shutil 
from PIL import Image

def selectWidth(file_path,select_path,select_width):
    for filenumber in os.walk(file_path):
        for files in filenumber[2]:
            image_path = file_path + files
            singleimg = Image.open(image_path)
            singleimg.close()
            if singleimg.width == select_width:
                try:
                    select_image_path = select_path + files
                    shutil.copy(image_path,select_image_path) 
                except error.URLError as e:
                    print('copy failed')
    print('Copy succeed')