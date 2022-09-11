package com.xxxx.crm.controller;

import com.xxxx.crm.annotation.RequirePermission;
import com.xxxx.crm.base.BaseController;
import com.xxxx.crm.base.ResultInfo;
import com.xxxx.crm.query.SaleChanceQuery;
import com.xxxx.crm.service.SaleChanceService;
import com.xxxx.crm.utils.AssertUtil;
import com.xxxx.crm.utils.CookieUtil;
import com.xxxx.crm.utils.LoginUserUtil;
import com.xxxx.crm.vo.SaleChance;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Map;

@Controller
@RequestMapping("sale_chance")
public class SaleChanceController extends BaseController {

    @Resource
    private SaleChanceService saleChanceService;

    /**
     * 如果flag有值那么相当于是客户开发计划模块的主页数据展示
     * @param saleChanceQuery
     * @param flag
     * @return
     */
    @RequestMapping("list")
    @ResponseBody
    @RequirePermission(code = 101001)
    public Map<String,Object> querySaleChanceByParams(SaleChanceQuery saleChanceQuery,Integer flag,HttpServletRequest request){
        if(flag !=null && flag == 1){
            //获取cookie中的id
            int id = LoginUserUtil.releaseUserIdFromCookie(request);
            saleChanceQuery.setAssignMan(id);
        }
        return saleChanceService.querySaleChanceByParams(saleChanceQuery);
    }



    @RequestMapping("index")
    public String toIndex(){
        return "saleChance/sale_chance";
    }


    @PostMapping("save")
    @ResponseBody
    @RequirePermission(code = 101002)
    public ResultInfo saveSaleChance(HttpServletRequest request, SaleChance saleChance){
        //cookie获取当前操作工作人员的名称
        String userName = CookieUtil.getCookieValue(request, "userName");
        //TODO判断当前username是否存在

        saleChance.setCreateMan(userName);

        saleChanceService.saveSaleChance(saleChance);
        return success();
    }


    @PostMapping("update")
    @ResponseBody
    @RequirePermission(code = 101004)
    public ResultInfo updateSaleChance(SaleChance saleChance){
        saleChanceService.updateSaleChance(saleChance);
        return success();
    }


    @RequestMapping("toAddUpdatePage")
    public String toAddUpdatePage(HttpServletRequest request,Integer saleChanceId){
        //如果参数不为空执行查询操作
        if(saleChanceId != null){
            SaleChance saleChance = saleChanceService.selectByPrimaryKey(saleChanceId);
            request.setAttribute("saleChance",saleChance);
        }
        return "saleChance/add_update";
    }



    /**
     * 批量删除数据
     */
    @RequestMapping("deleteBatch")
    @ResponseBody
    @RequirePermission(code = 101003)
    public ResultInfo deleteSaleChance(Integer[] ids){
        System.out.println(Arrays.toString(ids));
        AssertUtil.isTrue(ids == null || ids.length < 1,"未选中删除的数据");
        saleChanceService.deleteBatch(ids);
        return success("营销机会删除成功");
    }



    /**
     * 更新营销机会状态
     */
    @RequestMapping("updateSaleChanceDevResult")
    @ResponseBody
    public ResultInfo updateSaleChanceDev(Integer id,Integer devResult){
        saleChanceService.updateSaleChanceDevResult(id,devResult);
        return success("营销状态修改成功");
    }
}
