package com.xxxx.crm.controller;

import com.xxxx.crm.base.BaseController;
import com.xxxx.crm.base.ResultInfo;
import com.xxxx.crm.exceptions.ParamsException;
import com.xxxx.crm.model.UserModel;
import com.xxxx.crm.query.UserQuery;
import com.xxxx.crm.service.UserService;
import com.xxxx.crm.utils.AssertUtil;
import com.xxxx.crm.utils.LoginUserUtil;
import com.xxxx.crm.vo.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("user")
public class UserController extends BaseController {

    @Resource
    private UserService userService;

    @RequestMapping("login")
    @ResponseBody
    public ResultInfo userLogin(String userName,String userPwd){
        ResultInfo resultInfo = new ResultInfo();
        /*try {
            UserModel userModel = userService.userLogin(userName, userPwd);
            resultInfo.setResult(userModel);
        }catch (ParamsException e) {
            e.printStackTrace();
            resultInfo.setCode(e.getCode());
            resultInfo.setMsg(e.getMsg());
        } catch (Exception e) {
            e.printStackTrace();
            resultInfo.setCode(500);
            resultInfo.setMsg("登录失败");
        }*/
        UserModel userModel = userService.userLogin(userName, userPwd);
        resultInfo.setResult(userModel);
        return resultInfo;
    }


    @PostMapping("update")
    @ResponseBody
    public ResultInfo updateUserPwd(HttpServletRequest request, String oldPassword, String newPassword, String confirmPassword){
        ResultInfo resultInfo = new ResultInfo();
        /*try {
            //获取id
            int userId = LoginUserUtil.releaseUserIdFromCookie(request);
            //调用service
            userService.updateUserPwd(userId,oldPassword,newPassword,confirmPassword);
        }catch (ParamsException e) {
            e.printStackTrace();
            resultInfo.setCode(e.getCode());
            resultInfo.setMsg(e.getMsg());
        } catch (Exception e) {
            e.printStackTrace();
            resultInfo.setCode(500);
            resultInfo.setMsg("修改密码失败");
        }*/
        //获取id
        int userId = LoginUserUtil.releaseUserIdFromCookie(request);
        //调用service
        userService.updateUserPwd(userId,oldPassword,newPassword,confirmPassword);
        return resultInfo;
    }


    @RequestMapping("toPasswordPage")
    public String toPasswordPage(){

        return "user/password";
    }

    /**
     * 查询所有销售人员
     * @return
     */
    @RequestMapping("queryAllSales")
    @ResponseBody
    public List<Map<String,Object>> queryAllSales(){
        return userService.queryAllSales();
    }


    @RequestMapping("list")
    @ResponseBody
    public Map<String,Object> queryUserList(UserQuery userQuery){
        return userService.queryByParamsForTable(userQuery);
    }

    @RequestMapping("index")
    public String toIndex(){
        return "user/user";
    }


    @RequestMapping("toAddUpdate")
    public String toAddUpdate(Integer id,HttpServletRequest request){
        //如果是更新需要查询返回当前行数据
        if(id != null){
            User user = userService.selectByPrimaryKey(id);
            request.setAttribute("user",user);
        }
        return "user/add_update";
    }


    /**
     * 添加用户
     * @param user
     * @return
     */
    @RequestMapping("addUser")
    @ResponseBody
    public ResultInfo addUser(User user){
        //如果是更新需要查询返回当前行数据
        // System.out.println(user.getRoleIds());
        userService.addUser(user);
        return success("添加成功");
    }

    @RequestMapping("updateUser")
    @ResponseBody
    public ResultInfo updateUser(User user){
        //如果是更新需要查询返回当前行数据
        userService.updateUser(user);
        return success("更新成功");
    }


    @RequestMapping("deleteBatch")
    @ResponseBody
    public ResultInfo deleteBatch(Integer[] ids){
        //如果是更新需要查询返回当前行数据
        userService.deleteUserBatch(ids);
        return success("删除成功");
    }
}
