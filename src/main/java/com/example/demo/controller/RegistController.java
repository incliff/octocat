package com.example.demo.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.service.UserService;
import com.example.demo.utils.AuthCodeUtils;
import com.example.demo.utils.VerifyCodeConfig;

import redis.clients.jedis.Jedis;

@Controller
public class RegistController {
	
	@Autowired
	private UserService userService;
	
	@RequestMapping("/regist")
	public String regist() {
		return "regist";
	}
	/**
	 * 发送验证码到邮箱，并且判断发送验证码在24小时内的次数是否超过三次
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("/verifi_code/CodeSenderServlet")
	public void sendCode(HttpServletRequest request,HttpServletResponse response) throws IOException {
		//1获取邮箱
		String email = request.getParameter("emailaddr");
		System.out.println(email);
		if(email==null) {
			return;
		}
		//验证生成验证码的次数是否已满三次
		Jedis jedis = new Jedis("104.153.100.186", VerifyCodeConfig.PORT);
		jedis.incr(VerifyCodeConfig.COUNT_SUFFIX);
		jedis.expire(VerifyCodeConfig.COUNT_SUFFIX, 24*60*60);
		if(jedis.get(VerifyCodeConfig.COUNT_SUFFIX)!=null||Integer.parseInt(jedis.get(VerifyCodeConfig.COUNT_SUFFIX))<=3) {
			//2、生成6位数的验证码
			String code = AuthCodeUtils.genCode(VerifyCodeConfig.CODE_LEN);
			//
			String codeKey = VerifyCodeConfig.EMAIL_PREFIX+email+VerifyCodeConfig.EMAIL_SUFFIX;
			jedis.setex(codeKey, VerifyCodeConfig.CODE_TIMEOUT, code);
			jedis.close();
			//响应
			
			System.out.println(code);
			response.getWriter().print(true);
		}else {
			response.getWriter().print(false);
		}
		
	}
	/**
	 * 检查验证码是否正确从而检验邮箱是否属于本人，如果正确保存用户信息注册成功，返回主页面。否则注册失败，重定向到注册页面
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("/verifi_code/CodeVerifyServlet")
	public String checkRegist(HttpServletRequest request,HttpServletResponse response) throws IOException {
		String email = request.getParameter("emailaddr");
		String verify_code = request.getParameter("verify_code");
		String password = request.getParameter("password");
		String confirm_password = request.getParameter("confirm_password");
		if(password!=confirm_password) {
			response.getWriter().print(false);
			return "redirect:/regist";
		}
		if(email==null||verify_code==null||password==null||confirm_password==null) {
			response.getWriter().print(false);
			return "redirect:/regist";
		}
		
		//判断输入的验证码是否正确
		Jedis jedis = new Jedis("104.153.100.186", VerifyCodeConfig.PORT);
		String codeKey = VerifyCodeConfig.EMAIL_PREFIX+email+VerifyCodeConfig.EMAIL_SUFFIX;
		String code = jedis.get(codeKey);
		jedis.close();
		if(verify_code.equals(code)) {
			Map<String,Object> map = new HashMap<>();
			map.put("username", email);
			map.put(password, password);
			userService.saveUser(map);
			return "main";
		}else {
			response.getWriter().print(false);
			return "redirect:/regist";
			
		}
		
	}

}
