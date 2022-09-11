layui.use(['table','layer','form'],function(){
    var layer = parent.layer === undefined ? layui.layer : top.layer,
        form = layui.form,
        $ = layui.jquery,
        table = layui.table;

    //发送请求获取销售人员
    $.get(ctx + "/user/queryAllSales",function (data) {
        var assignMan = $("#saleAM").val();
        console.log(data);
        //获取下拉框
        var am = $("#assignMan");
        if(data != null && data.length > 0){
            for(var i = 0; i < data.length; i++){
                var str = "";
                //拼接下拉选项
                //判断当前的下拉选项哪个跟修改前的分配人一致
                if(assignMan == data[i].id){ //如果一样了默认选中显示出来
                    var str = "<option selected value="+data[i].id+">"+data[i].uname+"</option>";
                }else{
                    str = "<option value="+data[i].id+">"+data[i].uname+"</option>";
                }
                //添加到下拉框中
                am.append(str);
            }
        }

        // 重新渲染下拉框内容
        layui.form.render("select");
    });

    /**
     * 监听form表单提交
     */
    form.on("submit(addOrUpdateSaleChance)",function (data) {
        //获取表单中的键值对形式的数据
        console.log(data.field);
        // 提交数据时的加载层 （https://layer.layui.com/）
        var index = layer.msg("数据提交中,请稍后...",{
            icon:16, // 图标
            time:false, // 不关闭
            shade:0.8 // 设置遮罩的透明度
        });

        //请求地址
        var url = ctx + "/sale_chance/save";
        if($("[name = 'id']").val()){
            url = ctx + "/sale_chance/update";
        }

        //发送请求
        $.post(url,data.field,function(data){
            console.log(data);
            if(data.code == 200){
                //提示用户添加成功
                layer.msg("数据添加成功",{icon:6});
                //关闭加载层
                layer.close(index);
                //关闭添加窗口
                layer.closeAll("iframe");

                //刷新页面的营销记录
                parent.location.reload();
            }else{
                layer.msg(data.msg,{icon:5});
            }
        });


        //阻止表单提交
        return false;
    });




    //关闭
    $("#qx").click(function () {
        layer.closeAll();
    });

});