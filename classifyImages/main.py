import os
import selectImage
import downImage

def downAndSelect(url,dirpath,dirname,select_dirname):
    
    new_path = os.path.join(dirpath,dirname)
    select_path = os.path.join(dirpath,select_dirname)
    
    if not os.path.isdir(new_path):                                            
        os.makedirs(new_path)
    if not os.path.isdir(select_path):                                         
        os.makedirs(select_path)
    downImage.downAllHttpImage(url,new_path)
    selectImage.selectWidth(new_path,select_path,720)
   
if __name__ == "__main__":
    #知乎图片
    url = "http://www.zhihu.com/question/35874887"
    dirpath = './'                                                             
    dirname = 'zhihu/srcJPG/'
    select_dirname = 'zhihu/selectJPG/'
    downAndSelect(url,dirpath,dirname,select_dirname)
                