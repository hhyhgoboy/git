layui.use(['form', 'jquery', 'jquery_cookie', 'layer'], function () {
    var form = layui.form,
        layer = layui.layer,
        $ = layui.jquery,
        $ = layui.jquery_cookie($);
    form.on('submit(login)', function (data) {
        //获取表单元素的值
        var fileData = data.field;

        $.ajax({
            type: "post",//发送的请求
            url: ctx + "/user/login",
            data: {
                userName: fileData.username,
                userPwd: fileData.password
            }, //提交给后台controller的数据
            dataType: "json", //返回的类型
            success: function (data) {  //成功后的回调函数
               console.log(data);
                if (data.code === 200) {
                    layer.msg("登陆成功", function () {//登陆成功3s后执行该函数
                        //将用户信息存储到cookie中
                        var result = data.result;
                        $.cookie("userIdStr", result.userIdStr);
                        $.cookie("userName", result.userName);
                        $.cookie("trueName", result.trueName);

                        //如果用户选择记住密码  则设置cookie有效期为7天
                        //属性选择器 $("input[属性名=属性值]")
                        //表单属性选择器获得单选框选中的数据(":checked")
                        if($("input[type='checkbox']").is(":checked")){
                            $.cookie("userIdStr",result.userIdStr,{expires:7});
                            $.cookie("userName",result.userName,{expires:7});
                            $.cookie("trueName",result.trueName,{expires:7});
                        }
                        //跳转成功页面
                        window.location.href = ctx + "/main";
                    });
                } else {
                    //提示错误信息
                    layer.msg(data.msg)
                }
            }

        });
        //取消默认提交行为
        return false;
    });

});