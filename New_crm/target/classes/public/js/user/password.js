layui.use(['form','jquery','jquery_cookie'], function () {
    var form = layui.form,
        layer = layui.layer,
        $ = layui.jquery,
        $ = layui.jquery_cookie($);


    form.on("submit(saveBtn)",function(data){
        //获取数据
        var oldPassword = data.field.old_password;
        var newPassword = data.field.new_password;
        var confirmPassword = data.field.again_password;

        //ToDo

        //数据校验
        //原始密码与新密码不能一样
        if(oldPassword == newPassword){
            layer.msg("原始密码与新密码不能一样",{icon:5})
            return;
        }
        //确认密码密码与新密码需要一样
        if(!confirmPassword == newPassword){
            layer.msg("确认密码密码与新密码不一致",{icon:5})
            return;
        }

        //发送请求
        $.ajax({
            type:'post',
            url: ctx + "/user/update",
            data:{
                oldPassword : oldPassword,
                newPassword : newPassword,
                confirmPassword : confirmPassword
            },
            dataType:'json',
            success:function (data) {
                if(data.code == 200){
                    //退出登录状态，删除cookie
                    $.removeCookie("userIdStr",{domain:"localhost",path:"/crm"});
                    $.removeCookie("userName",{domain:"localhost",path:"/crm"});
                    $.removeCookie("trueName",{domain:"localhost",path:"/crm"});

                    //在父窗口/顶级窗口 跳转到登录页面
                    window.parent.location.href = ctx + "/index";
                }else{
                    layer.msg(data.msg,{icon:5});
                }
            }
        });


        return false;
    });


});


function istrue(str){
    if(str == null || str == ""){
        return true;
    }
    return false;
}
