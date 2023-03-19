package com.spring.web.user;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.support.SessionStatus;

import com.spring.web.customer.vo.Customer;
import com.spring.web.vendor.vo.Vendor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserService svc;
	
	@Autowired
	private HttpSession session;
		
	
	
	@GetMapping("/loginsuccess")
	@ResponseBody
	public Map<String,Object> success(@AuthenticationPrincipal org.springframework.security.core.userdetails.User user,HttpSession session,Model m)
	{
		Map<String,Object> map = new HashMap<>();
		Collection<GrantedAuthority> col = user.getAuthorities();
		List<GrantedAuthority> list = new ArrayList(List.of(col.toArray()));
		String role = list.get(0).toString();
		switch (role) {
			case "ROLE_USER": {
				String cid = user.getUsername();
				Customer customer = svc.getCnumbyCidFromDB(cid);
				session.setAttribute("cnum", customer.getCnum());
				session.setAttribute("nickname", customer.getNickname());
				map.put("confirm",true);
				map.put("url","/drinks/");
				map.put("msg","로그인성공");
				log.info("여기 유저 로그인 성공시 옴");
				return map;
			}
			case "ROLE_VENDOR": {
				String vid =  user.getUsername();
				Vendor vendor = svc.getVendorInfobyVidFromDB(vid);				
				session.setAttribute("vnum", vendor.getVnum());
				session.setAttribute("vid", vid);
				map.put("confirm", true);
				if(vendor.getPermit()==1)
				{
					map.put("url","user/vendor/waitingJoin");
				}
				else {
					map.put("url","/vendor/orderlist");
				}
				map.put("permit", vendor.getPermit());
				map.put("msg","로그인성공");
				log.info("여기 벤더 로그인 성공시 옴");
				return map;
			}
		}
		return map;
	}
	
	
	
	
	
	
	
	
	
//customer	
		
	@GetMapping("/customer/join")
	public String customerJoinForm()
	{		
		return "thymeleaf/customer/joinForm";
	}
	
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
	}	*/	

	

	@PostMapping("/customer/join")
	@ResponseBody
	public Map<String,Boolean> CustomerJoin(Customer customer)
	{
		boolean joined = svc.CustomerJoinResult(customer);
		Map<String,Boolean> map = new HashMap<>();
		map.put("joined", joined);		
		return map;
	}


	@PostMapping("/customer/idDupliCheck")
	@ResponseBody
	public Map<String,Object> customerIdCheck(@RequestParam("cid") String cid)
	{		
		Map<String,Object> map = svc.CustomerIdCheck(cid);
		return map;
	}		
	
	@GetMapping("/customer/login")
	public String CustomerloginForm()
	{
		return "thymeleaf/customer/loginForm";
	}	
	
	@PostMapping("/customer/login")
	@ResponseBody
	public Map<String,Object> login(Customer customer)
	{
		Map<String,Object> map = svc.customerLoginResult(customer);		
		Customer c = (Customer) map.get("customer");		
		session.setAttribute("nickname", c.getNickname());
		session.setAttribute("cnum", c.getCnum());
		
		return map;
	}

	@GetMapping("/customer/logout")
	public String logout()
	{
		session.invalidate();		
		return "thymeleaf/market/home";
	}	

	@ResponseBody
	@PostMapping("/customer/updatePw")
	public Map<String,Object> editPwd(@RequestParam("pastpw") String pastpwd,String pw)
	{
		int cnum = (int)session.getAttribute("cnum");
		Customer cus = new Customer();
		cus.setPwd(pastpwd);
		cus.setCnum(cnum);
		
		Map<String,Object> map = svc.CustomerEditPwd(cus, pw);
		return map;
	}
	
	@GetMapping("/customer/myinfo")
	public String myinfo(Model m)
	{
		int cnum = (int)session.getAttribute("cnum");
		m.addAttribute("cus", svc.getCustomerInfo(cnum));
		
		return "thymeleaf/customer/myInfo";
	}
	
	
	@PostMapping("/customer/sendEmail")
	@ResponseBody
	public boolean sendEmail(@RequestParam("email") String email)
	{
		String code = svc.createRandomStr();
		session.setAttribute("code", code);
		boolean res = svc.sendHTMLMessage(email, code);
		return res;
	}
	
	@PostMapping("/customer/codeCheck")
	@ResponseBody
	public boolean verifyEmail(@RequestParam("emailcode") String code,@RequestParam("email") String email)
	{
		
		int cnum = (int)session.getAttribute("cnum");
		boolean res = code.equals((String)session.getAttribute("code"));
		boolean editRes = false;
		if(res)
		{
			editRes = svc.CustomerUpdateEmail(cnum,email);			
		}
		
		return res && editRes ;
	}
	
	@ResponseBody
	@PostMapping("/customer/editphone") 
	public Map<String,Object> phoneAddEdit(Customer customer)
	{		
		int cnum = (int)session.getAttribute("cnum");						
		customer.setCnum(cnum);		
		Map<String,Object> map= svc.customerUpdateNewPhone(customer);
			
		return map;
	}	 
	
	@ResponseBody
	@PostMapping("/customer/editaddress") 
	public Map<String,Object> addressAddEdit(Customer customer)
	{		
		int cnum = (int)session.getAttribute("cnum");						
		customer.setCnum(cnum);		
		Map<String,Object> map= svc.customerUpdateNewAddress(customer);
			
		return map;
	}	 

// Vendor	
	
	@GetMapping("/vendor/join")
	public String vendorJoinForm()
	{		
		return "thymeleaf/vendor/vjoinForm";
	}
	
	@PostMapping("/vendor/join")
	@ResponseBody
	public Map<String,Boolean> vendorJoin(Vendor vendor)
	{
		boolean joined = svc.vendorJoinResult(vendor);
		Map<String,Boolean> map = new HashMap<>();
		map.put("joined", joined);		
		return map;
	}

	@PostMapping("/vendor/idDupliCheck")
	@ResponseBody
	public Map<String,Object> VendorIdCheck(@RequestParam("vid") String vid)
	{		
		Map<String,Object> map = svc.vendorIdCheck(vid);
		return map;
	}	
		
	@GetMapping("/vendor/login")
	public String VendorLoginForm()
	{
		return "thymeleaf/vendor/vloginForm";	
	}	
	
	@PostMapping("/vendor/login")
	@ResponseBody
	public Map<String,Object> vendorLogin(Vendor vendor, HttpSession session)
	{
		Map<String,Object> map = svc.vendorLoginResult(vendor);
		
		session.setAttribute("vid", map.get("vid"));
		session.setAttribute("vnum", map.get("vnum"));
		
		return map;
	}
	
	@GetMapping("/vendor/waitingJoin")
	public String vendorWaitingJoinForm()
	{
		return "thymeleaf/vendor/joinWaitingForm";
	}
	
	
	
	@GetMapping("/vendor/logout")
	public String vendorLogout(SessionStatus status)
	{		
		session.invalidate();
		return "thymeleaf/vendor/vloginForm";
	}

	
}
