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
	
	
/*	
	@GetMapping("/")
	public String home()
	{		
		return "thymeleaf/customer/home";
	}
	
	@GetMapping("/join")
	public String addForm()
	{		
		return "thymeleaf/customer/joinForm";
	}
*/	
	/*--휴대폰 본인인증으로 생년월일, 전화번호 구하기(테스트용 간편인증이라서 막아둠)
	@ResponseBody
	@PostMapping("/certification")
	public Map<String,String> certification(@RequestBody String imp_uid)
	{
		Map<String,String> map = new HashMap<>();
		IamportClient client = new IamportClient("api key", "secret key");
		
			try {
				IamportResponse<Certification> certificationResponse = client.certificationByImpUid(imp_uid);
				
				map.put("birth", certificationResponse.getResponse().getBirth().toString());
				map.put("phone", certificationResponse.getResponse().getPhone().toString());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		
		return map;
	}
	*/
	
	/*
	
	@PostMapping("/join")
	@ResponseBody
	public Map<String,Boolean> join(Customer customer)
	{
		boolean joined = svc.join(customer);
		Map<String,Boolean> map = new HashMap<>();
		map.put("joined", joined);		
		return map;
	}
	

	@PostMapping("/idDupliCheck")
	@ResponseBody
	public Map<String,Object> idCheck(@RequestParam("cid") String cid)
	{		
		Map<String,Object> map = svc.idChecked(cid);
		return map;
	}	
	
	
	
	@GetMapping("/login")
	public String loginForm()
	{
		return "thymeleaf/customer/loginForm";
	}	
	
	@PostMapping("/login")
	@ResponseBody
	public Map<String,Object> login(Customer customer)
	{
		Map<String,Object> map = svc.login(customer);		
		Customer c = (Customer) map.get("customer");		
		session.setAttribute("nickname", c.getNickname());
		session.setAttribute("cnum", c.getCnum());
		
		return map;
	}

	@GetMapping("/logout")
	public String logout()
	{
		session.invalidate();		
		return "thymeleaf/market/home";
	}
*/	
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
/*	
	@ResponseBody
	@PostMapping("/updatePw")
	public Map<String,Object> editPwd(@RequestParam("pastpw") String pastpwd,String pw)
	{
		int cnum = (int)session.getAttribute("cnum");
		Customer cus = new Customer();
		cus.setPwd(pastpwd);
		cus.setCnum(cnum);
		
		Map<String,Object> map = svc.editPwd(cus, pw);
		return map;
	}
*/	
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
	
	
/*	
	@GetMapping("/myinfo")
	public String myinfo(Model m)
	{
		int cnum = (int)session.getAttribute("cnum");
		m.addAttribute("cus", svc.getmyinfo(cnum));
		
		return "thymeleaf/customer/myInfo";
	}
	
	
	@PostMapping("/sendEmail")
	@ResponseBody
	public boolean sendEmail(@RequestParam("email") String email)
	{
		String code = svc.createRandomStr();
		session.setAttribute("code", code);
		boolean res = svc.sendHTMLMessage(email, code);
		return res;
	}
	
	@PostMapping("/codeCheck")
	@ResponseBody
	public boolean verifyEmail(@RequestParam("emailcode") String code,@RequestParam("email") String email)
	{
		
		int cnum = (int)session.getAttribute("cnum");
		boolean res = code.equals((String)session.getAttribute("code"));
		boolean editRes = false;
		if(res)
		{
			editRes = svc.updateEmail(cnum,email);			
		}
		
		return res && editRes ;
	}
	
	@ResponseBody
	@PostMapping("/editphone") 	//유저 연락처 수정
	public Map<String,Object> phoneAddEdit(Customer customer)
	{		
		int cnum = (int)session.getAttribute("cnum");						
		customer.setCnum(cnum);		
		Map<String,Object> map= svc.updateNewPhone(customer);
			
		return map;
	}	 
	
	@ResponseBody
	@PostMapping("/editaddress") 	//유저 주소 수정
	public Map<String,Object> addressAddEdit(Customer customer)
	{		
		int cnum = (int)session.getAttribute("cnum");						
		customer.setCnum(cnum);		
		Map<String,Object> map= svc.updateNewAddress(customer);
			
		return map;
	}	 
	*/
}
