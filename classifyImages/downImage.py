import urllib.request as request
import re  
import urllib.error as error

def downAllHttpImage(url,new_path):
    count = 0
    m = request.urlopen(url).read()
    page_data = m.decode('gbk','ignore')                                       
    page_image = re.compile('<img src=\"(.+?)\"')
    for image in page_image.findall(page_data):
        pattern = re.compile(r'^https://.*.jpg$')                           
        if pattern.match(image):
#            print(image)
            try:
                image_data = request.urlopen(image).read()
                image_path = new_path + str(count) + '.jpg'
                count += 1
#                print(image_path)
                with open(image_path,'wb') as image_file:                     
                    image_file.write(image_data)
                    image_file.close()
            except error.URLError as e:
                print('src images Download failed')
    print('Download succeed')