var util = require('../../utils/util.js')
Page({
  data: {
    logs: []
  },
  onLoad: function () {
    this.setData({
      logs: (wx.getStorageSync('logs') || []).map(function (log) {
        console.log(new Date(log));
        return util.formatTime(new Date(log))
      })
    })
  }
})
