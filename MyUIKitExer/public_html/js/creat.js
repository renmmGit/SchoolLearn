$(function () {
    var flg = false;
    var This = $("");
    var iPage = 1;

    showMoreList();

    /*
     * 评论
     */
    $(document).on("click", ".uk-link-muted", function () {
        This = $(this);
        flg = true; //true表示点击了“回复”
    })

    $("#show-comment").click(function () {
        ajax('post', 'guestBook/index.php', 'm=index&a=send&content=' + encodeURI($("textarea").val()), function (data) {
       
            var d = JSON.parse(data);
            alert(d.message);
            if (!d.code) {
                //当可以创建元素时，先去除“<p class='noneContent'>现在还没有留言，快来抢沙发...</p>”
                if ($(".uk-comment-list").find(".noneContent").length) {
                    $("p").remove(".noneContent");
                }
                if (flg) { //点击“回复”再点击“发表”，在对应评论的对话框下回复
                    var oList = This.parents('.uk-list-striped');
                    if (oList.length) { //有了一个子回复，再次添加子回复
                        createLiList(oList, d.data, "uk-margin-small");
                        //跳到新添加的元素的位置
                        var t = oList.find("li:last").offset().top;
                        $(window).scrollTop(t);
                    } else { //还没有子回复，添加子回复
                        var nextUl = This.parents('.uk-comment').next();
                        createLiList(nextUl, d.data, "uk-margin-small");
                        //跳到新添加的元素的位置
                        var t = nextUl.find("li:last").offset().top;
                        $(window).scrollTop(t);
                    }
                    flg = false;
                } else { //只点击“发表”，发表一个新的评论
                    createLiList($(".uk-comment-list"), d.data, "uk-margin-medium-top", true);
                }
            }
            $("textarea").val("");
        });

    })

    /*
     * 当点击回复后，又不想回复了，就点击页面其他地方取消回复
     */
    $("#cancel-comment").click(function () {
        $("textarea").val("");
        flg = false;
    })

    /*
     * 点击显示更多的内容
     */
    $("#showMore").click(function () {
        iPage++;
        showMoreList();
    })

    function showMoreList() {
        ajax('get', 'guestBook/index.php', 'm=index&a=getList&n=4&page=' + iPage, function (data) {
            
            var d = JSON.parse(data);
            var data = d.data;
            if (data) {

                for (var i = 0; i < data.list.length; i++) {
                    createLiList($(".uk-comment-list"), data.list[i], "uk-margin-medium-top");
                }
            } else {
                if (iPage == 1) {
                    $(".uk-comment-list").html("<p class='noneContent'>现在还没有留言，快来抢沙发...</p>");
                }
                $("#showMore").css("display", "none");
            }

        });
    }
});
/*
 * 添加对话框
 */
function createLiList(oList, data, liMargin, insert) {

    var oLi = $("<li class='" + liMargin + "'></li>");
    var oArticle = $("<article class='uk-comment uk-visible-toggle'></article>");
    var oHeader = $("<header class='uk-comment-header uk-position-relative'></header>");
    var oHDiv1 = $("<div class='uk-grid-medium uk-flex-middle' uk-grid></div>");

    var oHDdiv11 = $("<div class='uk-width-auto'></div>");
    var oImg = $("<img class='uk-comment-avatar' src='imgs/1.jpg' width='80' height='80' alt=''>");
//    oImg.attr("src","imgs/"+ImgIndex+".jpg"); //注意创建一个存放图头像的数据库
    oHDdiv11.append(oImg);

    var oHDdiv12 = $("<div class='uk-width-expand'></div>");
    var oH4 = $("<h4 class='uk-comment-title uk-margin-remove'></h4>");
    var oA1 = $("<a class='uk-link-reset'></a>");
    oA1.html(data.username);
    oA1.attr("herf", "#");
    var oHP = $("<p class='uk-comment-meta uk-margin-remove-top'></p>");
    var oA2 = $("<a class='uk-link-reset'></a>");
    var currentDate = getDate(); //注意获取当前时间
    oA2.html(currentDate);
    oA2.attr("herf", "#");
    oH4.append(oA1);
    oHP.append(oA2);
    oHDdiv12.append(oH4);
    oHDdiv12.append(oHP);

    oHDiv1.append(oHDdiv11);
    oHDiv1.append(oHDdiv12);

    var oHDiv2 = $("<div class='uk-position-top-right uk-position-small'></div>");
    var oA3 = $("<a class='uk-link-muted' href='#'>回复</a>");
    var oA4 = $("<a class='uk-button' href='#'>赞</a>");
    oHDiv2.append(oA3);
    oHDiv2.append(oA4);

    var oDiv = $("<div class='uk-comment-body'></div>");
    var oP = $("<p style='text-indent:2em;'></p>");
    oP.html(data.content);
    oDiv.append(oP);

    oHeader.append(oHDiv1);
    oHeader.append(oHDiv2);

    oArticle.append(oHeader);
    oArticle.append(oDiv);

    oLi.append(oArticle);
    if (liMargin == "uk-margin-medium-top") {
        var oUl = $("<ul class='uk-list-striped uk-margin-small'></ul>");
        oLi.append(oUl);
    }
    if (insert && oList.children("li:first-child").length) {
        oList.prepend(oLi);
    } else {
        oList.append(oLi);
    }
}
/*
 * 获取当前时间
 */
function getDate() {
    var date = new Date();
    var year = date.getFullYear();
    var month = date.getMonth() + 1;
    var day = date.getDate();

    if (month >= 1 && month <= 9) {
        month = "0" + month;
    }
    if (day >= 1 && day <= 9) {
        day = "0" + day;
    }
    var currentDate = year + "-" + month + "-" + day;

    return currentDate;
}


/*
 * 计算过去距当前的时间差，将该值显示在页面中
 */
//function showTime(){
//    $("article").each(function(i){
//        var timeStr = $(this).find(".uk-comment-meta a").html();
//        var todayTime = new Date();
//        var today = todayTime.getDate();
//        if(timeStr.indexOf("-") == -1){
//            //3天前
//          
//            
//        }
//    })
//}