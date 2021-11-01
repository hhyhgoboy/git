layui.use(['table', 'layer'], function () {
    var layer = parent.layer === undefined ? layui.layer : top.layer,
        $ = layui.jquery,
        table = layui.table;
    /**
     * 用户列表展示
     */
    var tableIns = table.render({
        elem: '#userList',
        url: ctx + '/user/list',
        cellMinWidth: 95,
        page: true,
        height: "full-125",
        limits: [10, 15, 20, 25],
        limit: 10,
        toolbar: "#toolbarDemo",
        id: "userListTable",
        cols: [[
            {type: "checkbox", fixed: "left", width: 50},
            {field: "id", title: '编号', fixed: "true", width: 80},
            {field: 'userName', title: '用户名', minWidth: 50, align: "center"},
            {field: 'email', title: '用户邮箱', minWidth: 100, align: 'center'},
            {field: 'phone', title: '用户电话', minWidth: 100, align: 'center'},
            {field: 'trueName', title: '真实姓名', align: 'center'},
            {field: 'createDate', title: '创建时间', align: 'center', minWidth: 150},
            {field: 'updateDate', title: '更新时间', align: 'center', minWidth: 150},
            {title: '操作', minWidth: 150, templet: '#userListBar', fixed: "right", align: "center"}
        ]]
    });
    /*实现搜索功能，页面重载*/
    $(".search_btn").click(function () {
        //这里以搜索为例
        tableIns.reload({
            where: { //设定异步数据接口的额外参数，任意设
                userName: $("input[name=userName]").val(),
                phone: $("input[name=phone]").val(),
                email: $("input[name=email]").val()
            }
            , page: {
                curr: 1 //重新从第 1 页开始
            }
        });
    });

    //绑定头工具栏
    table.on('toolbar(users)', function (obj) {
        var checkStatus = table.checkStatus(obj.config.id);
        switch (obj.event) {
            case 'add':
                 // layer.msg("添加")
                openAddOrUpdateUserDialog();
                break;
            case 'del':
                // layer.msg("删除")
                deleteUser(checkStatus.data);
                break;
        }
        ;
    });

    /**
     * 行监听事件
     *
     */
    //监听行工具事件
    table.on('tool(users)', function (obj) {
        var data = obj.data;
        //console.log(obj)
        if (obj.event === 'del') {
            layer.confirm('真的要删除么？', {
                btn: ["确认", "取消"]
            }, function (index) {
                //关闭弹窗
                layer.close(index);
                //发送ajax删除数据
                $.post(ctx + "/user/delete", {ids: data.id}, function (result) {
                    if (result.code=== 200) {
                        //重新加载数据
                        layer.msg("操作成功！");
                        tableIns.reload();
                    } else {
                        //提示一下
                        layer.msg(result.msg, {icon: 5})
                    }
                }, "json")

            });
        } else if (obj.event === 'edit') {
            // layer.msg("修改")
            openAddOrUpdateUserDialog(data.id);
        }
    });

    /**
     * 打开用户添加或更新对话框
     */
    function openAddOrUpdateUserDialog(userId) {
        var title = "<h2>用户模块---添加</h2>";
        var url = ctx + "/user/addOrUpdatePage";

        //判断是否修改还是添加
        if (userId) {
            title = "<h2>用户模块---更新</h2>";
            url = url + "?id=" + userId;
        }
        //
        layer.open({
            title: title,
            content: url,
            type: 2,//iframe
            area: ["650px", "400px"],
            maxmin: true,
        })
    }

});