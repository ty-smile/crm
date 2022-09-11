package com.xxxx.crm.service;

import com.xxxx.crm.base.BaseService;
import com.xxxx.crm.dao.UserMapper;
import com.xxxx.crm.dao.UserRoleMapper;
import com.xxxx.crm.model.UserModel;
import com.xxxx.crm.utils.AssertUtil;
import com.xxxx.crm.utils.Md5Util;
import com.xxxx.crm.utils.PhoneUtil;
import com.xxxx.crm.utils.UserIDBase64;
import com.xxxx.crm.vo.User;
import com.xxxx.crm.vo.UserRole;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class UserService extends BaseService<User,Integer> {

    @Resource
    private UserMapper userMapper;
    @Resource
    private UserRoleMapper userRoleMapper;

    /**
     * 登录校验
         1.校验前台传来的参数（用户名和密码）
            如果为空，抛出异常
         2.调用dao层 用户名查询数据库，判断账号是否存在
             如果为空，抛出异常，账号不存在
         3.用前台接收来的密码和数据库中的密码对比（将前台密码加密，再比对）
            如果不一致，抛出异常，密码错误
         4.登录成功，返回数据 ResultInfo(封装了UserModel对象，前台需要存入cookie的用户数据)
     * @param userName
     * @param userPwd
     */
    public UserModel userLogin(String userName,String userPwd){
        // 校验前台传来的参数（用户名和密码）
        checkUserParams(userName,userPwd);
        //调用dao层 用户名查询数据库，判断账号是否存在
        User user = userMapper.selectUserByName(userName);
        //如果为空，抛出异常，账号不存在
        AssertUtil.isTrue(user == null,"用户不存在");
        //用前台接收来的密码和数据库中的密码对比（将前台密码加密，再比对）
        checkUserPwd(user.getUserPwd(),userPwd);
        //登录成功，返回数据 ResultInfo(封装了UserModel对象，前台需要存入cookie的用户数据)
        UserModel userModel = buildUserModel(user);
        return userModel;
    }

    /**
     * 封装了UserModel对象，前台需要存入cookie的用户数据
     * @param user
     */
    private UserModel buildUserModel(User user) {
        UserModel userModel = new UserModel();
        //将id加密
        String userIdStr = UserIDBase64.encoderUserID(user.getId());
        userModel.setUserIdStr(userIdStr);
        userModel.setTrueName(user.getTrueName());
        userModel.setUserName(user.getUserName());
        return userModel;
    }

    /**
     * 用前台接收来的密码和数据库中的密码对比（将前台密码加密，再比对）
     * @param userPwd
     * @param userPwd
     */
    private void checkUserPwd(String dbPwd, String userPwd) {
        //加密前台传来的密码
        String encodePwd = Md5Util.encode(userPwd);
        //比对密码
        AssertUtil.isTrue(!dbPwd.equals(encodePwd),"用户密码错误");
    }


    //校验前台传来的参数（用户名和密码）  是否为空
    private void checkUserParams(String userName, String userPwd) {
        AssertUtil.isTrue(StringUtils.isBlank(userName),"用户名不能为空");
        AssertUtil.isTrue(StringUtils.isBlank(userPwd),"密码不能为空");
    }


    /**修改用户密码
     */
    @Transactional
    public void updateUserPwd(Integer userId, String oldPassword, String newPassword,String confirmPassword){
        //通过id查询账户
        User user = userMapper.selectByPrimaryKey(userId);
        //id查询判断用户存在
        AssertUtil.isTrue(user == null,"用户未登录");
        //校验数据参数
        checkUserUpdateParams(oldPassword,newPassword,confirmPassword,user.getUserPwd());
        //设置新密码
        user.setUserPwd(Md5Util.encode(newPassword));
        user.setUpdateDate(new Date());

        //执行修改操作，返回结果信息ResultInfo
        AssertUtil.isTrue(userMapper.updateByPrimaryKeySelective(user) < 1,"修改密码失败");
    }

    /**
     * 校验修改密码的数据
             1.获取cookie中id  非空   id查询判断用户存在
             2.原始密码 非空   与数据库中密码保持一致
             3.新密码   非空   新密码与原始密码不能一致
             4.确认密码 非空   确认密码与新密码一致
             5.执行修改操作，返回结果信息ResultInfo
     * @param oldPassword  用户输入原始密码
     * @param newPassword  新密码
     * @param confirmPassword  确认密码
     * @param dbPassword    数据库中密码
     */
    private void checkUserUpdateParams(String oldPassword, String newPassword, String confirmPassword,String dbPassword) {
        // 2.原始密码 非空   与数据库中密码保持一致
        AssertUtil.isTrue(StringUtils.isBlank(oldPassword),"原始密码不能为空");
        AssertUtil.isTrue(!dbPassword.equals(Md5Util.encode(oldPassword)),"原始密码错误");

        // 3.新密码   非空   新密码与原始密码不能一致
        AssertUtil.isTrue(StringUtils.isBlank(newPassword),"新密码不能为空");
        AssertUtil.isTrue(newPassword.equals(oldPassword),"新密码与原始密码不能一致");

        //确认密码 非空   确认密码与新密码一致
        AssertUtil.isTrue(StringUtils.isBlank(confirmPassword),"确认密码不能为空");
        AssertUtil.isTrue(!confirmPassword.equals(newPassword),"新密码与确认密码不一致");
    }


    /**
     * 查询所有销售人员
     * @return
     */
    public List<Map<String,Object>> queryAllSales(){
        return userMapper.queryAllSales();
    }

    /**
     * 添加用户
     *  1.数据校验
     *      用户名       非空|唯一
     *      真实名称     非空
     *      手机号       非空|格式合法
     *      邮箱         非空
     *  2.设置默认值
     *      is_valid
     *      updateDate
     *      createDate
     *      userPwd      123456（加密MD5）
     *  3.执行添加操作
     * @param user
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void addUser(User user) {
        //数据校验
        checkAddUserParams(user);
        //设置默认值
        user.setUpdateDate(new Date());
        user.setCreateDate(new Date());
        user.setIsValid(1);
        user.setUserPwd(Md5Util.encode("123456"));
        //执行添加操作
        // AssertUtil.isTrue(userMapper.insertSelective(user) < 1,"用户添加失败");
        //添加操作返回主键  给sql添加具体属性，让其返回主键，主键会被设置到user(当前对象)中
        AssertUtil.isTrue(userMapper.insertHasKey(user) < 1,"用户添加失败");

        //绑定角色  需要给userId绑定
        relaionUserRole(user.getId(),user.getRoleIds());

    }


    /**
     * 绑定角色
     * @param userId
     * @param roleIds  格式1,2,3,4
     */
    private void relaionUserRole(Integer userId, String roleIds) {
        //查询当前用户下有没有绑定的角色，如果有角色直接删除
        Integer count = userRoleMapper.countUserRole(userId);
        if(count > 0){
            userRoleMapper.deleteUserRole(userId);
        }
        if(StringUtils.isNotBlank(roleIds)){
            //循环创建对象 设置数据     一个对象对应就是一条数据
            String[] splitIds = roleIds.split(",");
            //准备一个容器存储多条数据对象
            List<UserRole> urs = new ArrayList<>();

            for(String rId : splitIds){
                UserRole userRole = new UserRole();
                userRole.setCreateDate(new Date());
                userRole.setUpdateDate(new Date());
                userRole.setRoleId(Integer.parseInt(rId)); // 角色id
                userRole.setUserId(userId);

                //将数据添加到容器
                urs.add(userRole);
            }

            //批量添加
            AssertUtil.isTrue(userRoleMapper.insertBatch(urs) != urs.size(),"角色绑定失败");
        }
    }


    /**
     * 修改用户
     *  1.数据校验
     *      用户名       非空|唯一
     *      真实名称     非空
     *      手机号       非空|格式合法
     *      邮箱         非空
     *      id           非空
     *  2.设置默认值
     *      is_valid
     *      updateDate
     *      createDate
     *      userPwd      123456（加密MD5）
     *  3.执行添加操作
     * @param user
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateUser(User user) {
        //校验id
        AssertUtil.isTrue(user.getId() == null || userMapper.selectByPrimaryKey(user.getId()) == null ,"系统异常，请重试");

        //数据校验
        checkAddUserParams(user);
        //设置默认值
        user.setUpdateDate(new Date());
        //执行添加操作
        AssertUtil.isTrue(userMapper.updateByPrimaryKeySelective(user) < 1,"用户更新失败");

        //角色绑定修改
        relaionUserRole(user.getId(),user.getRoleIds());
    }



    /**
     * 校验登录参数
     * @param user
     */
    private void checkAddUserParams(User user) {
        AssertUtil.isTrue(StringUtils.isBlank(user.getUserName()),"用户名不为空");
        //用户名唯一
        User dbUser = userMapper.selectUserByName(user.getUserName());

        //修改账户名查询  情况一：没查到，可以直接使用  情况二：查到了 1.是自己数据的名称  2.是别人数据的
        if(user.getId() == null){
            //添加
            AssertUtil.isTrue(dbUser != null,"用户名已存在");
        }else{
            //修改
            //如果查到了并且数据不是自己的，那么名称不可用
            AssertUtil.isTrue(dbUser != null && !dbUser.getId().equals(user.getId()),"用户名不可用");
        }

        AssertUtil.isTrue(StringUtils.isBlank(user.getTrueName()),"真实名称不为空");
        AssertUtil.isTrue(StringUtils.isBlank(user.getPhone()) || !PhoneUtil.isMobile(user.getPhone()),"手机号格式不对");

        AssertUtil.isTrue(StringUtils.isBlank(user.getEmail()),"邮箱不能为空");
    }

    /**
     * 批量删除用户
     * @param ids
     */
    @Transactional
    public void deleteUserBatch(Integer[] ids) {
        //删除用户
        AssertUtil.isTrue(ids == null || ids.length < 1,"没有可删除的数据");
        AssertUtil.isTrue(userMapper.deleteBatch(ids) != ids.length,"删除失败");

        //删除角色
        for(Integer id : ids){
            //删除角色返回受影响行数
            AssertUtil.isTrue(userRoleMapper.deleteUserRole(id) < 1 ,"删除角色失败");
        }
    }
}
