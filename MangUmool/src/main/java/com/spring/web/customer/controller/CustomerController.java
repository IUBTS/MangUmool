package com.spring.web.customer.controller;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.response.Certification;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import com.spring.web.customer.service.CustomerService;
import com.spring.web.customer.vo.Cancle;
import com.spring.web.customer.vo.Customer;
import com.spring.web.customer.vo.Review;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@RequestMapping("/customer")
@Controller
public class CustomerController {
	
	@Autowired
	private CustomerService svc;
	
	@Autowired
	private HttpSession session;
	
	
	@GetMapping("/myorder")
	public String myOrder(Model m,@RequestParam(required =false, defaultValue="1") int pg)
	{
		int cnum = (int)session.getAttribute("cnum");
		List<List<Map<String,Object>>> list = svc.getmyorder(cnum,pg,5);
		m.addAttribute("order", list );
		if(list.size()!=0)
		{
			m.addAttribute("end",list.get(0).get(0).get("end"));
			m.addAttribute("pg",pg);
		}
		else { m.addAttribute("end",0); }
		
		return "thymeleaf/customer/cusOrder";
	}
	@PostMapping("/detailorder")
	public String detailOrder(@RequestParam("oinum") int oinum,Model m)
	{		
		m.addAttribute("list", svc.getDetailOrder(oinum));		
		return "thymeleaf/customer/customerOrderDetail";
	}
	
	@PostMapping("/cancle")
	public String cancleForm(@RequestParam("oinum") int oinum,Model m)
	{		
		m.addAttribute("list", svc.getDetailOrder(oinum));		
		return "thymeleaf/customer/orderCancelForm";
	}
	
	@ResponseBody
	@PostMapping("/regCancle")
	public  Map<String,Object> regCancle(Cancle cancle)
	{	
		 cancle.setCnum((int)session.getAttribute("cnum"));
		 Map<String,Object> map = svc.addCancle(cancle);		
		
		 return map;
	}	
	
	@ResponseBody
	@PostMapping("/addreview")
	public  Map<String,Object> review(Review review)
	{
		 int cnum=(int)session.getAttribute("cnum");
		 review.setCnum(cnum);
		 Map<String,Object> map = svc.regReview(review);	
	
		 return map;
	}
	
}
