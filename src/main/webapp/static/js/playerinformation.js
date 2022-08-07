$("#leftplayer").click(function () {

    $("#left-information").html("<br>" + "积 分 : " + leftInformation.jiFen + "<br>" + "姓 名 : " + leftInformation.userName);
    $("#left-information").show().delay(3000).hide(0);
    // leftPai();
});

$("#rightplayer").click(function () {
    // alert("right");

    // removeChuPai("mychupai");
    // $("#leftplayer").css("background-image","url()");
    // $("#right-information").text("right");
    $("#right-information").html("<br>" + "积 分 : " + rightInformation.jiFen + "<br>" + "姓 名 : " + rightInformation.userName);
    $("#right-information").show().delay(3000).hide(0);
});

$("#acrossplayer").click(function () {
    // $("#leftplayer").css("background-image","url(img/suo1.png)");

    // woChuPai(2);
    // $("#across-information").text("right");
    $("#across-information").html("<br>" + "积 分 : " + acrossInformation.jiFen + "<br>" + "姓 名 : " + acrossInformation.userName);
    $("#across-information").show().delay(3000).hide(0);
});