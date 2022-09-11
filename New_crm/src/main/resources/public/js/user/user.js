layui.use(['table','layer'],function(){
    var layer = parent.layer === undefined ? layui.layer : top.layer,
        $ = layui.jquery,
        table = layui.table;
    /**
     * 用户列表展示
     */
    var  tableIns = table.render({
        elem: '#userList',
        url : ctx + '/user/list',
        cellMinWidth : 95,
        page : true,
        height : "full-125",
        limits : [10,15,20,25],
        limit : 10,
        toolbar: "#toolbarDemo",
        id : "userListTable",
        cols : [[
            {type: "checkbox", fixed:"left", width:50},
            {field: "id", title:'编号',fixed:"true", width:80},
            {field: 'userName', title: '用户名', minWidth:50, align:"center"},
            {field: 'email', title: '用户邮箱', minWidth:100, align:'center'},
            {field: 'phone', title: '用户电话', minWidth:100, align:'center'},
            {field: 'trueName', title: '真实姓名', align:'center'},
            {field: 'createDate', title: '创建时间', align:'center',minWidth:150},
            {field: 'updateDate', title: '更新时间', align:'center',minWidth:150},
            {title: '操作', minWidth:150, templet:'#userListBar',fixed:"right",align:"center"}
        ]]
    });


    /**
     * 数据表格重新加载
     */
    $("#btnSearch").click(function(){
        tableIns.reload({
            where: { //设定异步数据接口的额外参数，任意设
                userName:$('[name = "userName"]').val(),
                email:$('[name = "email"]').val(),
                phone:$('[name = "phone"]').val()
            }
            ,page: {
                curr: 1 //重新从第 1 页开始
            }
        });
    });


    //监听头部工具栏
    table.on("toolbar(users)",function(obj){
        if(obj.event == "add"){
            //打开窗口
            openAddOrUpdateDialog();
        }else if(obj.event == "del"){
            var checkStatus = table.checkStatus(obj.config.id);
            console.log(checkStatus);
            deleteBatch(checkStatus.data);
        }
    })

    //监听行工机具兰
    table.on("tool(users)",function(obj){
        if(obj.event == "edit"){
            //打开窗口
            openAddOrUpdateDialog(obj.data.id);
        }else if(obj.event == "del"){
            var arr = [obj.data];
            //删除
            deleteBatch(arr);
        }
    })


    //处理头部工具栏的删除方法
    function deleteBatch(data) {
        // 判断用户是否选择了要删除的记录
        if (data.length == 0) {
            layer.msg("请选择要删除的记录！");
            return;
        }
        // 询问用户是否确认删除
        layer.confirm("您确定要删除选中的记录吗？", {
            btn: ["确认", "取消"],
        }, function (index) {
            //关闭弹出层
            layer.close(index);
            //拼接删除数据的数组 id
            // ids=10&ids=20
            var ids = "ids=";
            for (var i = 0; i < data.length; i++) {
                //判断是不是倒数第二条个数据
                if (i < data.length - 1) {
                    ids += data[i].id + "&ids=";
                } else {
                    ids += data[i].id;
                }
            }

            $.ajax({
                type: 'post',
                url: ctx + '/user/deleteBatch',
                data: ids,
                dataType: 'json',
                success: function (data) {
                    if (data.code == 200) {

                        //刷新表格数据
                        tableIns.reload();
                    } else {
                        layer.msg(data.msg, {icon: 5});
                    }
                }
            });
        });
    }


    //添加和修改
    function openAddOrUpdateDialog(id){
        //定义添加
        var title = "用户管理-用户添加";
        var url = ctx + "/user/toAddUpdate";
        //定义更新
        if(id != null){
            title = "用户管理-用户更新";
            url += "?id=" + id;
        }
        //打开窗口
        layer.open({
            type:2,
            title:title,
            content:url,
            maxmin:true,
            area:["650px","400px"]
        });
    }
});