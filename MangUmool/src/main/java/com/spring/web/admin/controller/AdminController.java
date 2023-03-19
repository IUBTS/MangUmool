package com.spring.web.admin.controller;


import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.support.SessionStatus;

import com.spring.web.admin.service.AdminService;
import com.spring.web.admin.vo.Admin;
import com.spring.web.customer.vo.Customer;
import com.spring.web.vendor.vo.Items;
import com.spring.web.vendor.vo.Vendor;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@RequestMapping("/admin")
@Controller
public class AdminController {
	
	@Autowired
	private AdminService svc;

	@GetMapping("/")
	public String home()
	{		
		return "thymeleaf/admin/home";
	}
	
	@GetMapping("/join")
	public String addForm()
	{		
		return "thymeleaf/admin/joinForm";
	}
	
	@PostMapping("/join")
	@ResponseBody
	public Map<String,Boolean> join(Admin admin)
	{
		boolean joined = svc.join(admin);
		Map<String,Boolean> map = new HashMap<>();
		map.put("joined", joined);		
		return map;
	}
	

	@GetMapping("/login")
	public String loginForm()
	{
		return "thymeleaf/admin/loginForm";	
	}	
	
	@PostMapping("/login")
	@ResponseBody
	public Map<String,Object> login(Admin admin, HttpSession session)
	{
		Map<String,Object> map = svc.login(admin);
		
		session.setAttribute("aid", map.get("aid"));
		session.setAttribute("adnum", map.get("adnum"));
		
		return map;
	}

	@GetMapping("/logout")
	public String logout(SessionStatus status)
	{		
		status.setComplete();
		boolean logout = status.isComplete();
		return "thymeleaf/admin/home";
	}
	
	
	@GetMapping("/main")
	public String adminMain(Model m)
	{
		List<Items> list = svc.postRequest();
		m.addAttribute("post",list);
		
		return "thymeleaf/admin/main";
	}
	
	@GetMapping("/requestPostItem")
	public String requestForItemPost(Model m)
	{
		List<Items> list = svc.postRequest();
		m.addAttribute("post",list);
		
		return "thymeleaf/admin/postWaitingList";
	}
	
	@ResponseBody
	@PostMapping("/postPermit")
	public Map<String,Object> postOk(@RequestParam("itemcode") int itemcode)
	{		
		return svc.approvePost(itemcode);		
	}
	
	
	@GetMapping("/requestJoinVendor")
	public String getRequestJoinVendor(Model m)
	{		
		List<Vendor> list = svc.getJoinVendorInfo();
		m.addAttribute("vendorlist", list);		
		return "thymeleaf/admin/vendorPermitRequestList";	
	}
	
	@ResponseBody
	@PostMapping("/joinPermit")
	public Map<String,Object> joinOk(@RequestParam("vnum") int vnum)
	{		
		return svc.vendorJoinPermit(vnum);		
	}
	
	@PostMapping("/getOrder")
	public String getOrder(int oinum,Model m)
	{
		Map<String,Object> map = svc.getOrderByOinum(oinum);
		m.addAttribute("map", map);

		return "thymeleaf/admin/OrderList";	
	}
	
	
	@PostMapping("/getCancleList")
	public String getCancleListByDate(Date start,Date end,String vid,Model m)
	{
		List<Map<String,Object>> list = svc.getCancleListByDate(start, end, vid);		
		m.addAttribute("list", list);
		
		return "thymeleaf/admin/OrderList";	
	}
	
	
	@PostMapping("/getOrderList")
	public String getOrderListByDate(Date start,Date end,String vid,Model m)
	{
		List<Map<String,Object>> list = svc.getOrderListByDate(start, end, vid);		
		m.addAttribute("list", list);
		
		return "thymeleaf/admin/OrderList";	
	}
	
	
	@PostMapping("/findUser")
	public String findUserByCid(String cid,Model m)
	{
		Customer cus = svc.getUserInfoByCid(cid);
		m.addAttribute("cus", cus);
		
		return "thymeleaf/admin/findUserInfo";	
	}
	
	@PostMapping("/findVendor")
	public String findVendorByVid(String vid,Model m)
	{
		Vendor ven = svc.getVendorByVid(vid);
		m.addAttribute("ven", ven);
		
		return "thymeleaf/admin/findVendorInfo";	
	}
	
	
	
	
}
