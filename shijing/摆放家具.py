# -*- coding: utf-8 -*-




"""
面向对象封装案例
封装：
1.封装是面向对象编程的一大特点
2.将属性和方法封装到一个抽象的类中
3.外界使用类创建对象，然后让对象调用方法
4.对象方法的细节都被封装在类的内部
"""

# 摆放家具

class HouseItem:
    
    def __init__(self, name, area):
        self.name = name
        self.area = area
        
    def __str__(self):
        return "[%s]占地%.2f" %(self.name, self.area)

    
class House:
    
    def __init__(self, house_type, area):
        self.house_type = house_type
        self.area = area
        
        # 剩余面积
        self.free_area = area
        
        # 家具名称列表
        self.item_list = []
        
    def __str__(self):
        # python能够自动的将一对括号内部的代码连接在一起
        return ("户型：%s\n总面积：%.2f[剩余：%.2f]\n家具：%s"
                %(self.house_type, self.area,
                  self.free_area, self.item_list))
        
    def add_item(self, item):
        
        print("要添加 %s" %item)
        #1.判断家具的面积
        if item.area > self.free_area:
            print("%s的面积太大了，无法添加" % item.name)
            
            return
        #2.将家具的名称添加到列表中
        self.item_list.append(item.name)
        
        #3.计算剩余面积
        self.free_area -= item.area

#1.创建家具对象
bed = HouseItem("席梦思", 4)
chest = HouseItem("衣柜", 2)
table = HouseItem("餐桌", 1.5)

print(bed)
print(chest)
print(table)

#2.创建房子对象
my_home = House("两室一厅", 60)

my_home.add_item(bed)
my_home.add_item(chest)
my_home.add_item(table)

print(my_home)


"""
小结：
1.主程序只负责创建房子对象和家具对象
2.让房子对象调用add_item方法将家具添加到房子中
3.面积计算、剩余面积、家具列表等处理都被封装到房子类的内部
"""
