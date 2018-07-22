# -*- coding:utf-8 -*-
from model import *
import PIL.Image



def main():
    print("hello world")
    print(sub(1,2))
    im = PIL.Image.open("diaochan.jpg")
    w,h = im.size
    print("image size %s %s"%(w,h))
    im.thumbnail((w/2,h/2))
    im.save("xiaodiaochan.jpg","jpeg")
####################################################################
#
####################################################################
if __name__=="__main__":
    main()


