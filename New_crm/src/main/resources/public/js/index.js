layui.use(['form','jquery','jquery_cookie'], function () {
    var form = layui.form,
        layer = layui.layer,
        $ = layui.jquery,
        $ = layui.jquery_cookie($);

    /**
     *  监听登录表单的提交功能
     *      on监听submit提交事件，submit后面跟着的是：触发提交/目标元素
     */
    form.on('submit(login)',function (data) {
        //当前容器的全部表单字段，名值对形式：{name: value}
        console.log(data.field);

        //ToDo 校验用户名和密码
        /*if(istrue(data.field.username)){
            layer.msg("用户不能为空",{icon:5});
            return ;
        }*/

        //发送请求
        $.ajax({
            type:'post',
            url: ctx + "/user/login",
            data:{
                userName:data.field.username,
                userPwd:data.field.password
            },
            dataType:'json',
            success:function(data){
                if(data.code == 200){
                    layer.msg("登录成功",function () {
                        //存cookie
                        $.cookie("userIdStr",data.result.userIdStr);
                        $.cookie("userName",data.result.userName);
                        $.cookie("trueName",data.result.trueName);

                        //如果用户选择了记住密码，将cookie设置成7天
                        if($("#rememberMe").prop("checked")){
                            $.cookie("userIdStr",data.result.userIdStr,{expires:7});
                            $.cookie("userName",data.result.userName,{expires:7});
                            $.cookie("trueName",data.result.trueName,{expires:7});
                        }

                        window.location.href = ctx + "/main";
                    });

                }else{
                    layer.msg(data.msg,{icon:5});
                }
            }
        });


        return false; //阻止表单跳转。如果需要表单跳转，去掉这段即可。
    });
});


/*
function istrue(str){
    if(str == null || str == ""){
        return true;
    }
    return false;
}*/
