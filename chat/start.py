#coding=utf-8

import os
import tornado.ioloop
import tornado.web
import tornado.websocket


#WebSocket connection to 'ws://127.0.0.1:8181/websocket' failed: Error during WebSocket handshake: Unexpected response code: 403
#重写websocket中的check_origin方法，否则会出现以上403错误
#class WebSocketHandler(tornado.websocket.WebSocketHandler):
#    def check_origin(self,origin):
#        return True

#执行业务逻辑的类，继承tornado.web.RequestHandler
#tornado会将接收到的get请求指向Handler中的get方法
class MainHandler(tornado.web.RequestHandler):
    def get(self):
        self.render("index.html")       #可以从模板路径中找到对应的文件，不用每次都写完整路径


class ChatSocketHandler(tornado.websocket.WebSocketHandler):

    connects = set()

    def open(self):                             #刷新进入页面时，执行open
        #print ("socket opened")
        ChatSocketHandler.connects.add(self)

    def on_message(self, message):
        ChatSocketHandler.send_all(message)

    def on_close(self):
        print ("socketed closed")

    def check_origin(self,origin):
        return True
    
    @classmethod
    def send_all(cls, chat):
        for connect in cls.connects:
            try:
                connect.write_message(chat)     #发送到所有的客户端
            except:
                pass




if __name__ == "__main__":

    settings = {
                "template_path": os.path.join(os.path.dirname(__file__), "templates"),   #模板路径：当前路径os.path.dirname(__file__)下的文件夹templates
                "debug":True,   #修改文件时，时时重启
               }

#Application是tornado.web的类,做一些配置相关的工作
#*是元组，**字典
#settings前的**代表把settings中的配置全都引入到app中，自动解包的过程
    app = tornado.web.Application([
        (r"/", MainHandler),                #路由，指向MainHandler：客户端
        (r"/websocket", ChatSocketHandler), #指向ChatSocketHandler：websocket服务端
        ],**settings)

    app.listen(8181)                        #监听8181端口
    tornado.ioloop.IOLoop.instance().start()#启动服务进行死循环，让服务一直执行，调取请求，并将请求转发到路由指定的MainHandler中



