$(function () {
	//初始化
    updateUserStatus();
/*
 * 点击subnav第一个li，如果是“登录”就执行登录的操作；
 *                  否则是“欢迎：用户名”，不进行任何操作
 */
    $(".uk-subnav li:eq(0)").find("a").click(function () {
        var navLi0 = $(".uk-subnav li:eq(0)").find("a").html();
        if(navLi0 == "登录"){
            /*
             * 登录
             */
                //鼠标的聚焦和失焦
            $(".uk-modal-dialog p:eq(0)").find("input").val("");
            $(".uk-modal-dialog p:eq(1)").html("");
            $(".uk-modal-dialog p:eq(1)").addClass("uk-hidden");
            $(".uk-modal-dialog p:eq(2)").find("input").val("");
            $(".uk-modal-dialog p:eq(3)").find("input").val("登录");
                //点击登录按钮
               $(".uk-modal-dialog p:eq(3)").find("input").one("click", function(){//使执行一次，否则会记忆前几次的结果执行前几次次数和次 
                var dialogLogin = $(".uk-modal-dialog p:eq(3)").find("input").val();
                var oLoginUsername = $(".uk-modal-dialog p:eq(0)").find("input").val();
                var oLoginPassword = $(".uk-modal-dialog p:eq(2)").find("input").val();
                //判断是登录按钮就操作ajax的登录操作
                if(dialogLogin == "登录"){
                    ajax('post', 'guestBook/index.php', 'm=index&a=login&username='+encodeURI(oLoginUsername)+'&password=' + oLoginPassword, function(data) {
                                var d = JSON.parse(data);
                                alert(d.message);
                                if (!d.code) {
                                        updateUserStatus();
                                }
                        });
                }
            });
        }else{
            $("#modal-center").hide();
            $("#modal-center").removeAttr("style");
            $("#modal-center").removeClass("uk-open");
        }
    });   
/*
 * 点击subnav第二个li，如果是“注册”，就进行注册的操作
 *                  否则是“退出”，即执行退出的操作
 */
    $(".uk-subnav li:eq(1)").find("a").click(function () {
        var navLi1 = $(".uk-subnav li:eq(1)").find("a").html();
        if(navLi1 == "注册"){
            /*
             * 注册
             */
                //鼠标的聚焦和失焦
            $(".uk-modal-dialog p:eq(0)").find("input").val("");
            $(".uk-modal-dialog p:eq(1)").removeClass("uk-hidden");
            $(".uk-modal-dialog p:eq(1)").html("");
            $(".uk-modal-dialog p:eq(2)").find("input").val("");
            $(".uk-modal-dialog p:eq(3)").find("input").val("注册");
            var dialogReg = $(".uk-modal-dialog p:eq(3)").find("input").val();
            if(dialogReg == "注册"){
                $(".uk-modal-dialog p:eq(0)").find("input").blur(function () {
                    ajax('get', 'guestBook/index.php', 'm=index&a=verifyUserName&username=' + $(this).val(), function (data) {
                        var d = JSON.parse(data);
                        $(".uk-modal-dialog p:eq(1)").html(d.message);
                        if (d.code) {
                            $(".uk-modal-dialog p:eq(1)").css("color", "red");
                        } else {
                            $(".uk-modal-dialog p:eq(1)").css("color", "green");
                        }
                    });
                });
            //点击注册按钮
             $(".uk-modal-dialog p:eq(3)").find("input").one("click", function(){ //使执行一次，否则会记忆前几次的结果执行前几次次数和次 
                var oRegUsername = $(".uk-modal-dialog p:eq(0)").find("input").val();
                var oRegPassword = $(".uk-modal-dialog p:eq(2)").find("input").val();
                //判断是注册按钮就操作ajax的注册操作
                if(dialogReg == "注册"){
                    ajax('post', 'guestBook/index.php', 'm=index&a=reg&username='+encodeURI(oRegUsername)+'&password=' + oRegPassword, function(data) {
                            var d = JSON.parse(data);
                            alert(d.message);
                    });
                }
             });
            }
        }else{
    /*
     * 退出
     */           
            ajax('get', 'guestBook/index.php', 'm=index&a=logout', function(data) {			
                var d = JSON.parse(data);
                alert(d.message);
                if (!d.code) {
                        //退出成功
                        updateUserStatus();
                }
                $("#modal-center").removeAttr("style");
                $("#modal-center").removeClass("uk-open");
            });
            $("#modal-center").hide();
            return false;//阻止点击a链接时跳转
        }
    });
})


function getCookie(key) {
    var arr1 = document.cookie.split('; ');
    for (var i = 0; i < arr1.length; i++) {
        var arr2 = arr1[i].split('=');
        if (arr2[0] == key) {
            return arr2[1];
        }
    }
}
function updateUserStatus() {
		var uid = getCookie('uid');
		var username = getCookie('username');
		if (uid) {
			//如果是登陆状态
                        var str = "欢迎:" + username;
                        $(".uk-subnav li:eq(0)").find("a").html(str);
                        $(".uk-subnav li:eq(1)").find("a").html("退出");   
                        $("#modal-center").addClass("uk-hidden"); 
                        $(".uk-subnav li").find("a").removeAttr("uk-toggle");
		} else {
                        $(".uk-subnav li:eq(0)").find("a").html("登录");
                        $(".uk-subnav li:eq(1)").find("a").html("注册");
                        $("#modal-center").removeClass("uk-hidden"); 
                        $(".uk-subnav li").find("a").attr("uk-toggle","target: #modal-center");
		}
}


