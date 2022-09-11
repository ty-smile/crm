package com.xxxx.crm.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xxxx.crm.base.BaseService;
import com.xxxx.crm.dao.SaleChanceMapper;
import com.xxxx.crm.enums.DevResult;
import com.xxxx.crm.enums.StateStatus;
import com.xxxx.crm.query.SaleChanceQuery;
import com.xxxx.crm.utils.AssertUtil;
import com.xxxx.crm.utils.PhoneUtil;
import com.xxxx.crm.vo.SaleChance;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SaleChanceService extends BaseService<SaleChance,Integer> {

    @Resource
    private SaleChanceMapper saleChanceMapper;


    /**
     * 多条件查询所有客户记录
             {
                 code: 0,
                 msg: "",
                 count: 1000,
                 data: []
             }
     * @return
     */
    public Map<String,Object> querySaleChanceByParams(SaleChanceQuery saleChanceQuery){
        HashMap<String, Object> map = new HashMap<>();
        //开启分页
        PageHelper.startPage(saleChanceQuery.getPage(),saleChanceQuery.getLimit());
        //调用dao层查询
        List<SaleChance> saleChances = saleChanceMapper.selectByParams(saleChanceQuery);
        //将数据封装到pageinfo中
        PageInfo<SaleChance> saleChancePageInfo = new PageInfo<>(saleChances);


        map.put("code",0);
        map.put("msg","success");
        map.put("count",saleChancePageInfo.getTotal());
        map.put("data",saleChances);
        return map;
    }



    /**
     * 营销机会添加
     *  1.必填项
            customerName:非空
     *      linkMan:     非空
     *      linkPhone:   非空 11位手机号
     *  2.设置默认值
     *      isvalid    数据是否有效  1有效  0无效
     *      createDate   数据创建时间、当前系统时间
     *      updateDate   数据修改时间、当前系统时间
     *      createMan    添加数据的人员（在cookie中获取登录用户名称）交给controller直接设置进去
     *
     *      判断前台是否选择了分配人
     *         如果分配了人员
     *              assignTime分配时间   当前时间
     *              state分配状态        已分配  0未分配   1已分配
     *              devResult开发状态    开发中0-未开发 1-开发中 2-开发成功 3-开发失败
     *         如果未分配人员
     *              assignTime分配时间    null
     *              state分配状态        未分配   0未分配   1已分配
     *              devResult开发状态    未开发 0-未开发 1-开发中 2-开发成功 3-开发失败
     *
     *  3.添加数据，调用dao，判断是否添加成功
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void saveSaleChance(SaleChance saleChance){
        //校验必填项
        checkParams(saleChance.getCustomerName(),saleChance.getLinkMan(),saleChance.getLinkPhone());
        //设置默认值
        saleChance.setIsValid(1);
        saleChance.setCreateDate(new Date());
        saleChance.setUpdateDate(new Date());

        // 判断前台是否选择了分配人
        if(StringUtils.isBlank(saleChance.getAssignMan())){
            //如果未分配人员 设置的默认数据
            saleChance.setAssignTime(null);
            saleChance.setState(StateStatus.UNSTATE.getType());
            saleChance.setDevResult(DevResult.UNDEV.getStatus());
        }else{
            //如果分配了人员 设置的默认数据
            saleChance.setAssignTime(new Date());
            saleChance.setState(1);
            saleChance.setDevResult(1);
        }

        //执行添加
        AssertUtil.isTrue(saleChanceMapper.insertSelective(saleChance) < 1,"营销机会添加数据失败");

    }


    /**
     * 营销机会修改
       1.必填项
            id   主键
            customerName:非空
            linkMan:     非空
            linkPhone:   非空 11位手机号
       2.设置默认值
            updateTime    修改时间
            1.修改前未分配
                修改后未分配    不做任何操作
                修改后已分配
                    assignTime  分配时间当前时间
                    state       分配状态  1分配
                    devResult   开发状态  1开发中

            2.修改前已分配
                修改后未分配
                     assignTime   null
                     state       分配状态  0未分配
                     devResult   开发状态  0未开发中
                修改后分配（换了一个分配人）
                    如果分配人改动，那么assignTime需要更改

       3.修改营销机会的数据，判断返回值

     * @param saleChance
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateSaleChance(SaleChance saleChance){
        //在执行之前务必确认id的存在
        AssertUtil.isTrue(saleChance.getId() == null,"账户异常，请重试");
        //校验数据
        checkParams(saleChance.getCustomerName(),saleChance.getLinkMan(),saleChance.getLinkPhone());
        //查询修改之前的数据
        SaleChance dbSaleChance = saleChanceMapper.selectByPrimaryKey(saleChance.getId());

        //设置默认值
        saleChance.setUpdateDate(new Date());

        if(StringUtils.isBlank(dbSaleChance.getAssignMan())){//修改前未分配

            //判断修改后是否分配
            if(StringUtils.isNotBlank(saleChance.getAssignMan())){
                //修改后已分配
                saleChance.setAssignTime(new Date());
                saleChance.setState(1);
                saleChance.setDevResult(1);
            }

            //修改后未分配，什么都不做

        }else{//修改前已分配

            //判断修改后是否分配
            if(StringUtils.isBlank(saleChance.getAssignMan())){//修改后未分配

                saleChance.setAssignTime(null);
                saleChance.setState(0);
                saleChance.setDevResult(0);
            }else{
                //修改后已分配，判断分配人是否更改
                if(!saleChance.getAssignMan().equals(dbSaleChance.getAssignMan())){
                    //分配人修改了
                    saleChance.setAssignTime(new Date());
                }else{

                    //分配人未修改，时间应该也不修改，但是因为sql语句中此字段没有非空判断，所以设置原始的分配时间
                    saleChance.setAssignTime(dbSaleChance.getAssignTime());
                }
            }
        }

        //执行修改
        AssertUtil.isTrue(saleChanceMapper.updateByPrimaryKeySelective(saleChance) < 1,"营销机会修改失败");


    }


    /**
     * 校验数据的必填项
     * @param customerName
     * @param linkMan
     * @param linkPhone
     */
    private void checkParams(String customerName, String linkMan, String linkPhone) {
        AssertUtil.isTrue(StringUtils.isBlank(customerName),"客户名称不能空");
        AssertUtil.isTrue(StringUtils.isBlank(linkMan),"联系人不能为空");
        AssertUtil.isTrue(StringUtils.isBlank(linkPhone),"联系人号码不能空");
        //校验号码的合法性
        AssertUtil.isTrue(!PhoneUtil.isMobile(linkPhone),"手机号码格式有误");
    }

    /**
     * 批量删除数据
     */
    @Transactional
    public void deleteSaleChance(Integer[] ids){
        AssertUtil.isTrue(ids == null || ids.length < 1,"未选中删除的数据");
        saleChanceMapper.deleteBatch(ids);
    }



    /**
     * 更新数据状态
     */
    @Transactional
    public void updateSaleChanceDevResult(Integer id,Integer devResult){
        AssertUtil.isTrue(id == null ,"营销机会数据不存在");
        AssertUtil.isTrue(devResult == null ,"开发状态不存在");
        //查询数据是否存在
        SaleChance saleChance = saleChanceMapper.selectByPrimaryKey(id);
        AssertUtil.isTrue(saleChance == null ,"营销机会数据不存在");

        //设置更新的状态
        saleChance.setDevResult(devResult);
        saleChance.setUpdateDate(new Date());

        //执行更新
        AssertUtil.isTrue(saleChanceMapper.updateByPrimaryKeySelective(saleChance) < 1,"营销状态更新失败");
    }
}
