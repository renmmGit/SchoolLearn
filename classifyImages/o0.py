# -*- coding: utf-8 -*-
"""
Created on Mon May  7 19:56:59 2018

@author: Administrator
"""
import urllib.request as request
import urllib.parse as parse
import re                                                                      #处理正则表达式regEXP的包
import os
import urllib.error as error

def baidu_zhihu(url):
    count = 0
    m = request.urlopen(url).read()                                            #打开路径‘url’并读取其内容       
    
    #创建目录保存每个网页上的图片
    dirpath = './'                                                             #当前目录
    dirname = 'zhihu/srcJPG'
    new_path = os.path.join(dirpath,dirname)
    if not os.path.isdir(new_path):                                            #若不存在就创建这个新目录
        os.makedirs(new_path)
# =============================================================================
# 数据s乱码————解决办法： 
# s.decode('gbk', ‘ignore').encode('utf-8′) 
# 因为decode的函数原型是decode([encoding], [errors='strict'])，可以用第二个参数控制错误处理的策略，默认的参数就是strict，代表遇到非法字符时抛出异常； 
# 如果设置为ignore，则会忽略非法字符； 
# 如果设置为replace，则会用?取代非法字符； 
# 如果设置为xmlcharrefreplace，则使用XML的字符引用。 
# =============================================================================
    page_data = m.decode('gbk','ignore')                                       #Python3中：下面的re.findall()不能识别utf-8的编码，所以此处不要再进行utf-8编码                              
#    print(page_data)
# =============================================================================
# 正则表达式： 
#  . 代表任意字符，
# +代表匹配一个或更多字符，
# ？代表非贪婪匹配
# \"(.+?)\"      匹配 "abc"def"   中的 "abc"  ， 捕获部分为  abc     
# \"(.+)\"       匹配 "abc"def"   中的 "abc"def"  ，捕获部分为 abc"def
# =============================================================================
# =============================================================================
#compile函数用于寻找和匹配括号      
#findall函数返回的总是正则表达式在字符串中所有匹配结果的列表list
#      https://blog.csdn.net/drdairen/article/details/51134816
# =============================================================================
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
                
if __name__ == "__main__":
    url = "http://www.zhihu.com/question/35874887"
    baidu_zhihu(url)