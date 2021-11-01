package com.yjxxt.crm;

import com.alibaba.fastjson.JSON;
import com.yjxxt.crm.base.ResultInfo;
import com.yjxxt.crm.exceptions.NoLoginException;
import com.yjxxt.crm.exceptions.ParamsException;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Component
public class GlobalExceptionResolver implements HandlerExceptionResolver {
    /**
     * 返回视图
     * 返回json
     *
     * @param req
     * @param resp
     * @param handler
     * @param ex
     * @return
     */
    @Override
    public ModelAndView resolveException(HttpServletRequest req, HttpServletResponse resp, Object handler, Exception ex) {
        /**
         * 判断异常类型，如果是未登录异常跳转到登录页面
         */
        if (ex instanceof NoLoginException) {
            ModelAndView mav= new ModelAndView("redirect:/index");
            return  mav;
        }
        //设置默认异常处理
        //实例化对象
        ModelAndView mav = new ModelAndView();
        mav.setViewName("error");
        //存储数据
        mav.addObject("code", 400);
        mav.addObject("msg", "系统异常，请稍后再试");
        //判断  HandlerMethod
        if (handler instanceof HandlerMethod) {
            //handler处理器
            //handlerMethod中封装了很多属性，在访问请求方法时访问到方法、方法参数、方法上的注解
            //类型转换
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            //获取方法上的注解
            ResponseBody responseBody = handlerMethod.getMethod().getDeclaredAnnotation(ResponseBody.class);
            if (responseBody == null) {
                //方法返回视图
                if (ex instanceof ParamsException) {
                    ParamsException pe = (ParamsException) ex;
                    mav.addObject("code", pe.getCode());
                    mav.addObject("msg", pe.getMsg());

                }
                return mav;

            } else {
                //方法返回JSON
                ResultInfo resultInfo = new ResultInfo();
                resultInfo.setCode(300);
                resultInfo.setMsg("系统异常，请重试");
                if (ex instanceof ParamsException) {
                    ParamsException pe = (ParamsException) ex;
                    resultInfo.setCode(pe.getCode());
                    resultInfo.setMsg(pe.getMsg());
                }
                //设置响应的类型和编码格式
                resp.setContentType("application/json;charset=utf-8");
                //得到输出流
                PrintWriter out = null;
                try {
                    out = resp.getWriter();
                    //将对象转换成JSON格式  通过输出流输出  响应给请求的前台
                    out.write(JSON.toJSONString(resultInfo));
                    out.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (out != null) {
                        out.close();
                    }
                }
                return null;

            }
        }
        return mav;
    }
}
