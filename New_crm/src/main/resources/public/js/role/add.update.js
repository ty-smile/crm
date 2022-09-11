layui.use(['form', 'layer'], function () {
    var form = layui.form,
        layer = parent.layer === undefined ? layui.layer : top.layer,
        $ = layui.jquery;


    //监听表单提交事件
    form.on("submit(addOrUpdateRole)", function (data) {
        //弹出加载层
        var index = top.layer.msg('数据提交中，请稍候', {icon: 16, time: false, shade: 0.8});

        var url=ctx + "/role/save";
        if($("input[name='id']").val()){
            url=ctx + "/role/update";
        }
        $.post(url, data.field, function (data) {
            if (data.code == 200) {
                top.layer.close(index);
                top.layer.msg("操作成功！",{icon:5});
                layer.closeAll("iframe");

                //刷新父页面
                parent.location.reload();
            } else {
                layer.msg(data.msg,{icon:5});
            }
        });
        return false;
    });
});