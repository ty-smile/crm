layui.use(['table','layer','form'],function(){
    var layer = parent.layer === undefined ? layui.layer : top.layer,
        form = layui.form,
        $ = layui.jquery,
        table = layui.table;

    /**
     * 营销机会列表展示
     */
    var  tableIns = table.render({
        elem: '#saleChanceList', // 表格绑定的ID
        url : ctx + '/sale_chance/list', // 访问数据的地址
        cellMinWidth : 95,
        page : true, // 开启分页
        height : "full-125",
        limits : [10,15,20,25],
        limit : 10,
        toolbar: "#toolbarDemo",  //设置头部工具栏
        id : "saleChanceListTable", //给当前渲染的方式一个id
        cols : [[  //设置表头信息
            {type: "checkbox", fixed:"center"},
            {field: "id", title:'编号',fixed:"true"},
            {field: 'chanceSource', title: '机会来源',align:"center"},
            {field: 'customerName', title: '客户名称',  align:'center'},
            {field: 'cgjl', title: '成功几率', align:'center'},
            {field: 'overview', title: '概要', align:'center'},
            {field: 'linkMan', title: '联系人',  align:'center'},
            {field: 'linkPhone', title: '联系电话', align:'center'},
            {field: 'description', title: '描述', align:'center'},
            {field: 'createMan', title: '创建人', align:'center'},
            {field: 'createDate', title: '创建时间', align:'center'},
            {field: 'uname', title: '指派人', align:'center'},
            {field: 'assignTime', title: '分配时间', align:'center'},
            {field: 'state', title: '分配状态', align:'center',templet:function(d){
                    return formatterState(d.state);
                }},
            {field: 'devResult', title: '开发状态', align:'center',templet:function (d) {
                    return formatterDevResult(d.devResult);
                }},
            {title: '操作', templet:'#saleChanceListBar',fixed:"right",align:"center", minWidth:150}
        ]]
    });

    /**
     * 格式化分配状态
     *  0 - 未分配
     *  1 - 已分配
     *  其他 - 未知
     * @param state
     * @returns {string}
     */
    function formatterState(state){
        if(state==0) {
            return "<div style='color: yellow'>未分配</div>";
        } else if(state==1) {
            return "<div style='color: green'>已分配</div>";
        } else {
            return "<div style='color: red'>未知</div>";
        }
    }

    /**
     * 格式化开发状态
     *  0 - 未开发
     *  1 - 开发中
     *  2 - 开发成功
     *  3 - 开发失败
     * @param value
     * @returns {string}
     */
    function formatterDevResult(value){
        if(value == 0) {
            return "<div style='color: yellow'>未开发</div>";
        } else if(value==1) {
            return "<div style='color: #00FF00;'>开发中</div>";
        } else if(value==2) {
            return "<div style='color: #00B83F'>开发成功</div>";
        } else if(value==3) {
            return "<div style='color: red'>开发失败</div>";
        } else {
            return "<div style='color: #af0000'>未知</div>"
        }
    }


    /**
     * 数据表格重新加载
     */
    $("#btnSearch").click(function(){
        tableIns.reload({
            where: { //设定异步数据接口的额外参数，任意设
                customerName:$('[name = "customerName"]').val(),
                createMan:$('[name = "createMan"]').val(),
                state:$('[name = "state"]').val()
            }
            ,page: {
                curr: 1 //重新从第 1 页开始
            }
        });
    });



    /**
     * 监听头部工具栏的事件
     */
    //监听事件
    table.on('toolbar(saleChances)', function(obj){
        var checkStatus = table.checkStatus(obj.config.id);
        switch(obj.event){
            case 'add':
                openAddOrUpdate(); //打开添加的窗口
                break;
            case 'del':
                deleteBatch(checkStatus.data);
                break;
        };
    });


    //处理头部工具栏的删除方法
    function deleteBatch(data){
        // 判断用户是否选择了要删除的记录
        if (data.length == 0) {
            layer.msg("请选择要删除的记录！");
            return;
        }
        // 询问用户是否确认删除
        layer.confirm("您确定要删除选中的记录吗？",{
            btn:["确认","取消"],
        },function (index) {
            //关闭弹出层
            layer.close(index);
            //拼接删除数据的数组
            // ids=10&ids=20
            var ids = "ids=";
            for(var i = 0; i < data.length; i++){
                //判断是不是倒数第二条个数据
                if(i < data.length - 1){
                    ids += data[i].id + "&ids=";
                }else{
                    ids += data[i].id;
                }
            }

            $.ajax({
                type:'post',
                url:ctx + '/sale_chance/deleteBatch',
                data:ids,
                dataType:'json',
                success:function (data) {
                    if(data.code == 200){

                        //刷新表格数据
                        tableIns.reload();
                    }else{
                        layer.msg(data.msg,{icon:5});
                    }
                }
            });
        });
    }



    /**
     * 监听行工具栏的事件
     */
    //监听事件
    table.on('tool(saleChances)', function(obj){
        console.log(obj);
        console.log(obj.event);
        //判断用户行为
        if(obj.event == "edit"){
            console.log("abc``````");
            //显示窗口
            openAddOrUpdate(obj.data.id); //传出当前行的id
        }else if(obj.event == "del"){

            // 询问用户是否确认删除
            layer.confirm("您确定要删除选中的记录吗？",{
                btn:["确认","取消"],
            },function (index) {
                //关闭弹出层
                layer.close(index);

                $.ajax({
                    type:'post',
                    url:ctx + '/sale_chance/deleteBatch',
                    data:{
                        ids: obj.data.id
                    },
                    dataType:'json',
                    success:function (data) {
                        if(data.code == 200){

                            //刷新表格数据
                            tableIns.reload();
                        }else{
                            layer.msg(data.msg,{icon:5});
                        }
                    }
                });
            });
        }

    });



    /**
     * 打开添加的窗口页面
     */
    function openAddOrUpdate(saleChanceId) {
        //定义添加弹出框标题
        var title = "<h3>营销机会管理 - 机会添加</h3>";
        //请求添加 后台显示页面地址
        var url = ctx + "/sale_chance/toAddUpdatePage";

        //判断当前操作是添加还是修改修改
        if(saleChanceId != null){
            var title = "<h3>营销机会管理 - 机会修改</h3>";
            url += "?saleChanceId=" + saleChanceId;
        }

        layer.open({
            type:2,   //弹出框的类型
            title:title,
            content:url, //弹出层的页面内容
            area:["500px","620px"],     //宽高
            maxmin:true //最大最小化
        });
    }






});