layui.use(['table','layer'],function(){
    var layer = parent.layer === undefined ? layui.layer : top.layer,
        $ = layui.jquery,
        table = layui.table;

    //角色列表展示
    var  tableIns = table.render({
        elem: '#roleList',
        url : ctx+'/role/list',
        cellMinWidth : 95,
        page : true,
        height : "full-125",
        limits : [10,15,20,25],
        limit : 10,
        toolbar: "#toolbarDemo",
        id : "roleListTable",
        cols : [[
            {type: "checkbox", fixed:"left", width:50},
            {field: "id", title:'编号',fixed:"true", width:80},
            {field: 'roleName', title: '角色名', minWidth:50, align:"center"},
            {field: 'roleRemark', title: '角色备注', minWidth:100, align:'center'},
            {field: 'createDate', title: '创建时间', align:'center',minWidth:150},
            {field: 'updateDate', title: '更新时间', align:'center',minWidth:150},
            {title: '操作', minWidth:150, templet:'#roleListBar',fixed:"right",align:"center"}
        ]]
    });


    // 多条件搜索
    $(".search_btn").on("click",function(){
        table.reload("roleListTable",{
            page: {
                curr: 1 //重新从第 1 页开始
            },
            where: {
                roleName: $("input[name='roleName']").val()
            }
        })
    });


    /**
     * 监听头工具栏事件
     */
    table.on('toolbar(roles)', function(obj){
        var checkStatus = table.checkStatus(obj.config.id);
        switch(obj.event){
            case "add":
                openAddOrUpdateRoleDialog();
                break;
            case "grant":
                openAddGrantDailog(checkStatus);
                break;
        };
    });


    /**
     * 打开授权窗口
     */
    function openAddGrantDailog(data){
        if(data.data.length == 0){
            layer.msg("请选择角色",{icon:5});
            return;
        }
        if(data.data.length > 1){
            layer.msg("暂不支持批量授权",{icon:5});
            return;
        }

        layer.open({
            title: "角色管理-角色授权",
            maxmin: true,
            content:ctx + "/role/toGrantPage?roleId="+data.data[0].id,
            area:["400px","480px"],
            type:2
        })
    }


    /**
     * 监听行工具栏事件
     */
    table.on("tool(roles)", function(obj){
        var layEvent = obj.event;
        if(layEvent === "edit") {
            openAddOrUpdateRoleDialog(obj.data.id);
        }else if(layEvent === "del"){
            layer.confirm('确定删除当前角色？', {icon: 3, title: "角色管理"}, function (index) {
                $.post(ctx+"/role/delete",{id:obj.data.id},function (data) {
                    if(data.code==200){
                        layer.msg("操作成功！",{icon:6});
                        tableIns.reload();//表格数据重新加载
                    }else{
                        layer.msg(data.msg, {icon: 5});
                    }
                });
            })
        }
    });


    // 打开添加页面
    function openAddOrUpdateRoleDialog(uid){
        var url  =  ctx+"/role/addOrUpdateRolePage";
        var title="角色管理-角色添加";
        if(uid){
            url = url+"?id="+uid;
            title="角色管理-角色更新";
        }
        layui.layer.open({
            title : title,
            type : 2,
            area:["600px","280px"],
            maxmin:true,
            content : url
        });
    }





});