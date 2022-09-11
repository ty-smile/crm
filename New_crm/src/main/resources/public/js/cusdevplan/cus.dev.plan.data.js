layui.use(['table','layer'],function(){
    var layer = parent.layer === undefined ? layui.layer : top.layer,
        $ = layui.jquery,
        table = layui.table;

    /**
     * 计划项数据展示
     */
    var  tableIns = table.render({
        elem: '#cusDevPlanList',
        url : ctx+'/cus_dev_plan/list?sId=' + $("input[name='id']").val(),
        cellMinWidth : 95,
        page : true,
        height : "full-125",
        limits : [10,15,20,25],
        limit : 10,
        toolbar: "#toolbarDemo",
        id : "cusDevPlanListTable",
        cols : [[
            {type: "checkbox", fixed:"center"},
            {field: "id", title:'编号',fixed:"true"},
            {field: 'planItem', title: '计划项',align:"center"},
            {field: 'exeAffect', title: '执行效果',align:"center"},
            {field: 'planDate', title: '执行时间',align:"center"},
            {field: 'createDate', title: '创建时间',align:"center"},
            {field: 'updateDate', title: '更新时间',align:"center"},
            {title: '操作',fixed:"right",align:"center", minWidth:150,templet:"#cusDevPlanListBar"}
        ]]
    });



    //监听头部工具栏
    table.on("toolbar(cusDevPlans)",function(obj){
        switch (obj.event) {
            case "add":
                //打开新窗口
                openAddOrUpdateDialog();
                break;
            case "success":
                //打开新窗口
                updateSaleChanceDevResult(2);
                break;
            case "failed":
                //打开新窗口
                updateSaleChanceDevResult(3);
                break;
        }
    });


    /**
     * 更新营销机会开发状态
     */
    function updateSaleChanceDevResult(devResult){
        //获取需要更新状态的数据的id
        var id = $('[name="id"]').val();
        // 弹出提示框询问用户
        layer.confirm("确认执行当前操作？", {icon:3, title:"计划项维护"}, function (index) {

            $.post(ctx+"/sale_chance/updateSaleChanceDevResult",{devResult:devResult,id:id} , function(data){
                if(data.code == 200){
                    layer.msg(data.msg);
                    layer.closeAll("iframe");

                    //刷新页面
                    parent.location.reload();
                }else {
                    layer.msg(data.msg,{icon:5});
                }
            })
        });
    }



    //监听行工具栏
    table.on("tool(cusDevPlans)",function(obj){
        if(obj.event == "edit"){
            //打开新窗口
            openAddOrUpdateDialog(obj.data.id);
        }else{
            //删除数据
            deleteCusDevPlan(obj.data.id);
        }
    });


    //删除数据
    function deleteCusDevPlan(id){
        // 询问用户是否确认删除
        layer.confirm("确定删除当前数据？", {icon:3, title:"开发计划管理"}, function (index) {
            //发送请求删除数据
            $.post(ctx+"/cus_dev_plan/deleteCusDevPlan",{id:id},function (data) {
                if(data.code == 200){
                    layer.msg(data.msg);
                    //刷新表格
                    tableIns.reload();
                }else {
                    layer.msg(data.msg,{icon:5});
                }
            });
        });
    }

    //添加和修改的窗口打开
    function openAddOrUpdateDialog(id){
        //打开窗口的页面地址
        var url = ctx + "/cus_dev_plan/addOrUpdateCusDevPlanPage?sId=" + $("[name = 'id']").val();  //获取salechanceid  营销机会id
        var title = "计划项管理-添加计划项";
        if(id){
            url = url + "&id=" + id;
            title = "计划项管理-更新计划项";
        }
        layui.layer.open({
            title : title,
            type : 2,
            area:["500px","300px"],
            maxmin:true,
            content : url
        });
    }

});
