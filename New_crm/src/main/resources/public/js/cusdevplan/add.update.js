layui.use(['form', 'layer'], function () {
    var form = layui.form,
        layer = parent.layer === undefined ? layui.layer : top.layer,
        $ = layui.jquery;

    /**
     * 监听修改/添加操作
     *
     */
    form.on("submit(addOrUpdateCusDevPlan)",function(data){
        console.log(data.field);
        // 弹出loading层
        var index = top.layer.msg('数据提交中，请稍候', {icon: 16, time: false, shade: 0.8});
        //添加
        var url = ctx + "/cus_dev_plan/addCusDevPlan";
        //修改
        if($("[name = 'id']").val()){
            var url = ctx + "/cus_dev_plan/updateCusDevPlan";
        }
        //发送请求
        $.post(url,data.field,function (data) {
            if(data.code == 200){
                //关闭加载层
                layer.close(index);
                //
                layer.msg("操作成功");
                layer.closeAll("iframe");
                //刷新页面
                parent.location.reload();
            }else{
                layer.msg(data.msg,{icon:5});
            }
        });

        return false;//阻止表单提交

    });


    /**
     * 关闭弹出层
     */
    $("#closeBtn").click(function () {
        var index = parent.layer.getFrameIndex(window.name); //先得到当前iframe层的索引
        parent.layer.close(index); //再执行关闭
    });

});