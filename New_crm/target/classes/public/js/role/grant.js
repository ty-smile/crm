//加载事件
$(function () {
    loadModuleInfo();
});

//权限树形结构的对象
var zTreeObj = null;

//加载树形结构
function loadModuleInfo() {

    //发送请求返回树形结构的数据
    $.ajax({
        type:'get',
        url:ctx + "/module/queryAllModule",
        data:{
            roleId: $("[name='roleId']").val()
        },
        dataType:'json',
        success:function (data) {
            // zTree 的参数配置，深入使用请参考 API 文档（setting 配置详解）
            var setting = {
                check: { //开启多选框
                    enable: true
                },
                callback: { //监听多选框/单选框的选中
                    onCheck: zTreeOnCheck
                },
                data: {
                    simpleData: {  //使用简单json数据
                        enable: true
                    }
                }
            };

            $(document).ready(function(){
                zTreeObj = $.fn.zTree.init($("#test1"), setting, data);
            });
        }
    })
}


//监听多选框/单选框的选中  执行的方法
function zTreeOnCheck(){
    //获取给哪一位角色授权的id
    var roleId = $('[name="roleId"]').val();  //准备后期填充当前角色已有的权限  需要的roleId

    /** 获取输入框被勾选 或 未勾选的节点集合
     *          checked = true 表示获取 被勾选 的节点集合
                checked = false 表示获取 未勾选 的节点集合
     */
    var nodes = zTreeObj.getCheckedNodes(true);
    //拼接出多个资源模块的id
    var mId = "mId=";
    for (var i = 0; i < nodes.length; i++) {
        //判断是不是倒数第二条个数据
        if (i < nodes.length - 1) {
            mId += nodes[i].id + "&mId=";
        } else {
            mId += nodes[i].id;
        }
    }

    //发送请求 授权
    $.ajax({
        type:'get',
        url:ctx + '/role/addGrant',
        data:mId + "&roleId=" + roleId,
        dataType:'json',
        success:function(data){
            if (data.code == 200) {
                console.log(data.msg)
            } else {
                layer.msg(data.msg, {icon: 5});
            }
        }
    })
}
