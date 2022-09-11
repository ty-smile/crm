package com.xxxx.crm.service;

import com.xxxx.crm.base.BaseService;
import com.xxxx.crm.dao.CusDevPlanMapper;
import com.xxxx.crm.dao.SaleChanceMapper;
import com.xxxx.crm.utils.AssertUtil;
import com.xxxx.crm.vo.CusDevPlan;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;

@Service
public class CusDevPlanService extends BaseService<CusDevPlan,Integer> {

    @Resource
    private CusDevPlanMapper cusDevPlanMapper;

    @Resource
    private SaleChanceMapper saleChanceMapper;


    /**
     * 添加计划项
     *  1.参数校验
     *      营销机会id     非空  记录存在
     *      计划项内容     非空
     *      计划项时间     非空
     *  2.设置默认值
     *      is_valid      1
     *      updateDate   系统当前时间
     *      createDate   系统当前时间
     *  3.执行添加操作
     *
     */
    @Transactional
    public void addCusDevPlan(CusDevPlan cusDevPlan){
        System.out.println(cusDevPlan);
        //校验数据
        checkCusDevPlanParams(cusDevPlan.getSaleChanceId(),cusDevPlan.getPlanDate(),cusDevPlan.getPlanItem());
        //设置默认值
        cusDevPlan.setCreateDate(new Date());
        cusDevPlan.setUpdateDate(new Date());
        cusDevPlan.setIsValid(1);
        //执行添加操作
        AssertUtil.isTrue(cusDevPlanMapper.insertSelective(cusDevPlan) < 1,"添加计划项失败");
    }



    /**
     * 修改计划项
     *  1.参数校验
     *      计划项id       非空
     *      营销机会id     非空  记录存在
     *      计划项内容     非空
     *      计划项时间     非空
     *  2.设置默认值
     *      updateDate   系统当前时间
     *  3.执行修改操作
     *
     */
    @Transactional
    public void updateCusDevPlan(CusDevPlan cusDevPlan){
        //校验计划项id
        AssertUtil.isTrue(cusDevPlan.getId() == null ||
                cusDevPlanMapper.selectByPrimaryKey(cusDevPlan.getId()) == null,"系统异常，请重试");
        //校验数据
        checkCusDevPlanParams(cusDevPlan.getSaleChanceId(),cusDevPlan.getPlanDate(),cusDevPlan.getPlanItem());

        //设置需要改动数据
        cusDevPlan.setUpdateDate(new Date());

        //执行修改操作
        AssertUtil.isTrue(cusDevPlanMapper.updateByPrimaryKeySelective(cusDevPlan) < 1,"修改计划项失败");
    }


    /**
     * 删除计划项数据
     */
    @Transactional
    public void deleteCusDevPlan(Integer id){
        CusDevPlan cusDevPlan = selectByPrimaryKey(id);
        AssertUtil.isTrue(id == null || cusDevPlan == null ,"计划项数据不存在");
        //设置数据删除
        cusDevPlan.setIsValid(0);
        cusDevPlan.setUpdateDate(new Date());
        AssertUtil.isTrue(cusDevPlanMapper.updateByPrimaryKeySelective(cusDevPlan) < 1,"计划项删除失败");
    }




    /**
     * 添加/修改校验数据
     * @param saleChanceId
     * @param planDate
     * @param planItem
     */
    private void checkCusDevPlanParams(Integer saleChanceId, Date planDate, String planItem) {
        AssertUtil.isTrue(saleChanceId == null,"系统异常，请重试");
        AssertUtil.isTrue(saleChanceMapper.selectByPrimaryKey(saleChanceId) == null,"营销机会不存在");
        AssertUtil.isTrue(planDate == null,"计划项时间不能为空");
        AssertUtil.isTrue(StringUtils.isBlank(planItem),"计划项内容不能为空");
    }




}
