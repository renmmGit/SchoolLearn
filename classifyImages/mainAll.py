import urllib.request as request
import re                                                                      #处理正则表达式regEXP的包
import os
import urllib.error as error
import shutil 
from PIL import Image

def baidu_zhihu(url):
    count = 0
    m = request.urlopen(url).read()                                            #打开路径‘url’并读取其内容       
    
    #创建目录保存每个网页上的图片
    dirpath = './'                                                             #当前目录
    dirname = 'zhihu/srcJPG/'
    new_path = os.path.join(dirpath,dirname)
    
    select_dirname = 'zhihu/selectJPG/'
    select_path = os.path.join(dirpath,select_dirname)
    
    if not os.path.isdir(new_path):                                            #若不存在就创建这个新目录
        os.makedirs(new_path)
    if not os.path.isdir(select_path):                                            #若不存在就创建这个新目录
        os.makedirs(select_path)
    page_data = m.decode('gbk','ignore')     
    page_image = re.compile('<img src=\"(.+?)\"')
    for image in page_image.findall(page_data):
        pattern = re.compile(r'^https://.*.jpg$')                           #Python中字符串前面加上 r 表示原生字符串，避免编程语言和正则表达式中繁琐的使用反斜杠‘\’
        if pattern.match(image):
#            print(image)
            try:
                image_data = request.urlopen(image).read()
                image_path = dirpath + dirname + './' + str(count) + '.jpg'
                count += 1
#                print(image_path)
                with open(image_path,'wb') as image_file:                      #wb 以二进制格式打开一个文件只用于写入。如果该文件已存在则将其覆盖。如果该文件不存在，创建新文件。
                    image_file.write(image_data)
                    image_file.close()
            except error.URLError as e:
                print('src images Download failed')
    print('Download succeed')
    
    for filenumber in os.walk(new_path):
#        print(filenumber)
        for files in filenumber[2]:
#            print(files)
            file_path = new_path + files
            singleimg = Image.open(file_path)
            singleimg.close()
#            print(singleimg.size, singleimg.width, singleimg.height)
            if singleimg.width == 720:
#                print(singleimg)
                try:
                    select_image_path = select_path + files
                    shutil.copy(file_path,select_image_path) 
                except error.URLError as e:
                    print('copy failed')
    print('Copy succeed')
   
if __name__ == "__main__":
    url = "http://www.zhihu.com/question/35874887"
    baidu_zhihu(url)
                