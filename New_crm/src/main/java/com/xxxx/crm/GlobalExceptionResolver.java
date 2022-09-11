package com.xxxx.crm;

import com.alibaba.fastjson.JSON;
import com.xxxx.crm.base.ResultInfo;
import com.xxxx.crm.exceptions.NoLoginException;
import com.xxxx.crm.exceptions.ParamsException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.reflect.Method;

/**
 * 全局异常处理
 */
@Component
public class GlobalExceptionResolver implements HandlerExceptionResolver {

    /**
     * 方法返回值类型
     *    视图
     *    JSON
     * 如何判断方法的返回类型：
     *    如果方法级别配置了 @ResponseBody 注解，表示方法返回的是JSON；
     *	  反之，返回的是视图页面
     * @param httpServletRequest
     * @param httpServletResponse
     * @param handler
     * @param e
     * @return
     */
    @Override
    public ModelAndView resolveException(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object handler, Exception e) {

        /**
         * 判断异常类型
         *     如果是未登录异常，则先执行相关的拦截操作
         */
        if(e instanceof NoLoginException){
            // 如果捕获的是未登录异常，则重定向到登录页面
            ModelAndView mv = new ModelAndView();
            mv.setViewName("redirect:/index");
            return mv;
        }


        //设置默认返回值  (视图默认返回值)
        ModelAndView mv = new ModelAndView();
        mv.setViewName("error");
        mv.addObject("code",300);
        mv.addObject("msg","系统异常，请重试");

        //判断当前异常的 接口返回的是哪种数据(视图/JSON)
        if(handler instanceof HandlerMethod){
            //转换对象，controller的接口对象
            HandlerMethod handlerMethod = (HandlerMethod)handler;
            //获取接口方法
            Method method = handlerMethod.getMethod();
            //获取 判断当前方法是否包含@ResponseBody的注解
            ResponseBody responseBody = method.getDeclaredAnnotation(ResponseBody.class);

            //判断是否有ResponseBody
            if(responseBody == null){

                //返回的是视图
                if(e instanceof ParamsException){
                    ParamsException ex = (ParamsException)e;
                    mv.addObject("code",ex.getCode());
                    mv.addObject("msg",ex.getMsg());
                }
                return mv;

            }else{
                //返回的是json数据

                //设置默认json返回的接口，返回的数据
                ResultInfo resultInfo = new ResultInfo();
                resultInfo.setCode(400);
                resultInfo.setMsg("系统异常，请重试");

                //如果是自定义的异常，那么更换里面的信息
                if(e instanceof ParamsException){
                    ParamsException ex = (ParamsException)e;
                    resultInfo.setCode(ex.getCode());
                    resultInfo.setMsg(ex.getMsg());
                }

                //将数据传输出去
                //设置编码格式，响应类型
                httpServletResponse.setContentType("application/json;charset=utf-8");

                PrintWriter writer = null;
                try {
                    //获取输出流
                    writer = httpServletResponse.getWriter();
                    writer.write(JSON.toJSONString(resultInfo));

                    writer.flush();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }finally {
                    if(writer != null){
                        //关闭资源
                        writer.close();
                    }
                }


            }

        }
        return null;
    }
}
