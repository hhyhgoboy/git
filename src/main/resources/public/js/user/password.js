layui.use(['form', 'jquery', 'jquery_cookie'], function () {
    var form = layui.form,
        layer = layui.layer,
        $ = layui.jquery,
        $ = layui.jquery_cookie($);

    /*用户密码修改 绑定表单提交*/
    form.on("submit(saveBtn)", function (data) {
        //获取表单元素内容
        var fieldData = data.field;
        $.ajax({
            type: "post",//发送的方式
            url: ctx + "/user/updatePassword",  //请求到哪
            data: {
                oldPassword:fieldData.old_password,
                newPassword:fieldData.new_password,
                confirmPassword:fieldData.again_password
            },//发送给controller的数据
            dataType: "json",//返回的数据类型
            success: function (data) {
                console.log(data);
                //判断是否成功
                if (data.code === 200) {
                    //修改成功后 用户自动退出系统
                    layer.msg("用户密码修改成功，系统将在3秒后退出", function () {
                        //退出系统删除cookie
                        $.removeCookie("userIdStr", {domain: "localhost", path: "/crm"})
                        $.removeCookie("userName", {domain: "localhost", path: "/crm"})
                        $.removeCookie("trueName", {domain: "localhost", path: "/crm"})
                        //跳转到登陆页面
                        window.parent.location.href = ctx + "/index";

                    });
                }else {
                    layer.msg(data.msg)
                }
            }

        })
        return  false;
    })


});