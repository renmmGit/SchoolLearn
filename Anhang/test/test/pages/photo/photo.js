// pages/photo/photo.js
Page({
  data: {
    n: 0,
    p1: "",
    ppap: "../picture/"
  },
  onLoad: function () {
    this.setData({        //赋值给p1
      p1: "../picture/1.jpg"
    })
  },

  next: function () {
    var that = this
  	//调用应用实例的方法获取全局数据
    wx.vibrateShort({          //内部类
      success: function () {
        var nValue = that.data.n;
        if (nValue == 10) {
          // that.setData({
          //   n: 0
          // })
          nValue = 1; 
        } else if(nValue==0){
          nValue = 2;
        } else {
          nValue = nValue + 1;
        }
        console.log("success");
        that.setData({
          p1: that.data.ppap + nValue + ".jpg",
          n: nValue
        })
       
      }
    })
  }

})