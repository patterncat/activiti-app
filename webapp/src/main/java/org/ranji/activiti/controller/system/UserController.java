package org.ranji.activiti.controller.system;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.ranji.activiti.model.pager.PagerModel;
import org.ranji.activiti.model.system.User;
import org.ranji.activiti.service.system.prototype.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = "/system/user")
public class UserController {

	@Autowired
	private IUserService userService;

	//-- 暂时注释掉安全框架的内容
	//@RequiresPermissions("user:list")
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView list() {
		ModelAndView mv = new ModelAndView();
//		Subject currentUser = SecurityUtils.getSubject();
//System.out.println(currentUser.isAuthenticated());
//System.out.println(currentUser.isPermitted("user:list"));
		mv.setViewName("/backend/system/user/list");
		return mv;
	}

	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@ResponseBody
	public String add(User user) {
		String result = "";
		try {
			userService.save(user);
			result = "{ \"success\":true}";
		} catch (Exception e) {
			result = "{ \"msg\":\"sorry, add failure!\"}";
		}
		return result;
	}

	@RequestMapping(value = "/update", method = RequestMethod.POST)
	@ResponseBody
	public String update(User user) {
		String result = "";
		try {
			userService.update(user);
			result = "{ \"success\":true}";
		} catch (Exception e) {
			result = "{ \"msg\":\"sorry, add failure!\"}";
		}
		return result;
	}

	@RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
	@ResponseBody
	public String delete(@PathVariable int id) {
		String result = "";
		try {
			userService.delete(id);
			result = "{ \"success\":true}";
		} catch (Exception e) {
			result = "{ \"msg\":\"sorry, add failure!\"}";
		}
		return result;
	}
	
	@RequestMapping(value = "/deleteByIDS", method = RequestMethod.POST)
	@ResponseBody
	public String deleteByIDS(@RequestParam("ids[]") Integer[] ids) {
		String result = "";
		try {
			userService.deleteByIDS(Arrays.asList(ids));
			result = "{ \"success\":true}";
		} catch (Exception e) {
			result = "{ \"msg\":\"sorry, add failure!\"}";
		}
		return result;
	}
	

	@RequestMapping(value = "/getusers", method = RequestMethod.POST)
	@ResponseBody
	public String getUsers(HttpServletRequest request) {
		
		// -- 1. 从request对象中获取params的字符串 (格式为JSON字符串的样式)
		String paramsJSONStr = request.getParameter("params");
		
		// -- 2. 把JSON字符串，转化为Map对象的键值对的样式 (利用JSON处理工具)
		ObjectMapper om = new ObjectMapper();
		Map<String, Object> params = null;
		try {
			if(paramsJSONStr != null)
				params = om.readValue(paramsJSONStr,new TypeReference<Map<String, Object>>() {});
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// -- 3. 查询参数条件传入业务方法，产生正确的结果
		PagerModel<User> pm = userService.findPaginated(params);
		
		// -- 4. 把结果数据转变为JSON串传输给页面
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("total", pm.getTotal());
		map.put("rows", pm.getData());
		String jsonData = "";
		try {
			jsonData = om.writerWithDefaultPrettyPrinter().writeValueAsString(map);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return jsonData;
	}
}
