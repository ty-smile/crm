package com.xxxx.crm.controller;

import com.xxxx.crm.base.BaseController;
import com.xxxx.crm.base.ResultInfo;
import com.xxxx.crm.query.CusDevPlanQuery;
import com.xxxx.crm.service.CusDevPlanService;
import com.xxxx.crm.service.SaleChanceService;
import com.xxxx.crm.utils.AssertUtil;
import com.xxxx.crm.vo.CusDevPlan;
import com.xxxx.crm.vo.SaleChance;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("cus_dev_plan")
public class CusDevPlanController extends BaseController {

    @Resource
    private CusDevPlanService cusDevPlanService;
    @Resource
    private SaleChanceService saleChanceService;

    @RequestMapping("index")
    public String toIndex(){
        return "cusdevplan/cus_dev_plan";
    }


    @RequestMapping("toCusDevPlanDataPage")
    public String toCusDevPlanDataPage(Integer sId, HttpServletRequest request){
        //sId是营销机会的id
        if(sId != null){
            SaleChance saleChance = saleChanceService.selectByPrimaryKey(sId);
            request.setAttribute("saleChance",saleChance);
        }
        return "cusdevplan/cus_dev_plan_data";
    }


    @RequestMapping("list")
    @ResponseBody
    public Map<String, Object> toCusDevPlanDataPage(CusDevPlanQuery query){
        return cusDevPlanService.queryByParamsForTable(query);
    }


    /**
     * 添加
     * @param cusDevPlan
     * @return
     */
    @RequestMapping("addCusDevPlan")
    @ResponseBody
    public ResultInfo addCusDevPlan(CusDevPlan cusDevPlan){
        cusDevPlanService.addCusDevPlan(cusDevPlan);
        return success("计划项添加成功");
    }

    /**
     * 修改
     * @param cusDevPlan
     * @return
     */
    @RequestMapping("updateCusDevPlan")
    @ResponseBody
    public ResultInfo updateCusDevPlan(CusDevPlan cusDevPlan){
        cusDevPlanService.updateCusDevPlan(cusDevPlan);
        return success("计划项修改成功");
    }


    /**
     * 跳转到修改/添加的页面
     *      如果是修改那么查询数据存作用域，前台回显数据
     * @param id
     * @param request
     * @param sId  营销机会id  方便修改添加需要的营销数据
     * @return
     */
    @RequestMapping("addOrUpdateCusDevPlanPage")
    public String toaddOrUpdateCusDevPlanPage(Integer id,HttpServletRequest request,Integer sId){
        System.out.println(sId);
        //将营销机会id  传输到前台add_update页面  zuoyongyu
        request.setAttribute("sId",sId);
        if(id != null){
            CusDevPlan cusDevPlan = cusDevPlanService.selectByPrimaryKey(id);
            request.setAttribute("cusDevPlan",cusDevPlan);
        }
        return "cusdevplan/add_update";
    }


    @RequestMapping("deleteCusDevPlan")
    @ResponseBody
    public ResultInfo deleteCusDevPlan(Integer id){
        cusDevPlanService.deleteCusDevPlan(id);
        return success("计划项删除成功");
    }




}
