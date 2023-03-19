package com.spring.web.user;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spring.web.customer.mapper.CustomerMapper;
import com.spring.web.customer.vo.Customer;
import com.spring.web.vendor.vo.Vendor;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserService {

	@Autowired
	private UserMapper mapper;	
	
	@Autowired
	private JavaMailSender sender;
	
	
	// Customer	
	
	@Transactional
	public Boolean CustomerJoinResult(Customer customer)
	{		
		int res = mapper.customerJoin(customer);
				
		BCryptPasswordEncoder enc = new BCryptPasswordEncoder();
		customer.setPwd(enc.encode(customer.getPwd()));
		
		int res1 = mapper.addTradiUser(customer);
		int res2 = mapper.endowAuthorityToCustomer(customer.getCid());		
		
		return res2>0;
	}

	public Map<String,Object> CustomerIdCheck(String cid)
	{
		 Map<String,Object> map = new HashMap<>();
		 
		 if(mapper.customeridDupliCheck(cid)==null)
		 {
			 map.put("checked", true);
			 map.put("msg", "사용가능한 아이디입니다.");			 
		 }
		 else
		 {
			 map.put("checked", false);
			 map.put("msg", "입력하신 아이디가 존재합니다.");
		 }	 
		 return map;
	}	
	
	public Customer getCnumbyCidFromDB(String cid)
	{
		return mapper.getCnumByCid(cid);			
	}
	
	public Vendor getVendorInfobyVidFromDB(String vid)
	{
		return mapper.getVendorByVid(vid);			
	}
	
	
	public Map<String,Object> customerLoginResult(Customer customer)
	{				
		 String cid = customer.getCid();
		 Map<String,Object> map = new HashMap<>();
		 
		 Customer customerInfo = mapper.customerLogin(customer);
		 if(customerInfo!=null)
		 {		 
			String msg = "로그인 성공";
			String url = "/drinks/";
			boolean login = true;
			
			map.put("customer", customerInfo);
			map.put("login", login);
			map.put("msg",msg);
			map.put("url", url);
				 
			return map;
		}
		else
		{
			String msg = "로그인에 실패하였습니다.";
			String url = "/customer/login";			 
			boolean login = false;
			
			map.put("login", login);			
			map.put("msg",msg);
			map.put("url", url);
				 
			return map;	
		}
	}	 


	public Customer getCustomerInfo(int cnum)
	{
		Customer cus = mapper.customerGetmyinfo(cnum);			
		return cus;
	}

	
	public Map<String,Object> CustomerEditPwd(Customer cus,String pw)
	{
		Map<String,Object> map = new HashMap<>();
		
		int res = mapper.customerCheckPwd(cus);
		if(res!=cus.getCnum())
		{
			map.put("msg","현재 비밀번호가 일치하지 않습니다.");
			map.put("updated", false);			
			return map;
		}
		else
		{	
			Customer ncus = new Customer();
			ncus.setCnum(cus.getCnum());
			ncus.setPwd(pw);
			int res2 = mapper.customerUpdatePwd(ncus);
						
			if(res>0)
			{
				map.put("msg", "새 비밀번호 설정을 마쳤습니다.");
				map.put("updated", true);
			}
			else
			{	
				map.put("msg", "비밀번호 수정에 실패하였습니다.");
				map.put("updated", false);
			}	
		}		
		return map;
	}
		
	public boolean sendHTMLMessage(String email,String code)
    {
      MimeMessage mimeMessage = sender.createMimeMessage();

      try {
         InternetAddress[] addressTo = new InternetAddress[1];
         addressTo[0] = new InternetAddress(email);

         mimeMessage.setRecipients(Message.RecipientType.TO, addressTo);
         mimeMessage.setSubject("망우물 이메일 인증");	         
         mimeMessage.setContent("<h1>인증코드 :"+code+"</h1><br>", "text/html;charset=utf-8");
         
         sender.send(mimeMessage);
         return true;
      } catch (MessagingException e) {
         log.error("에러={}", e);
      }

      return false;
    }
		
	public String createRandomStr()
	{
        UUID randomUUID = UUID.randomUUID();
        return randomUUID.toString().replaceAll("-", "");
	}
	
	public boolean CustomerUpdateEmail(int cnum, String email)
	{		
		Customer cus = new Customer();
		cus.setCnum(cnum);
		cus.setEmail(email);
		int res = mapper.customerEditEmail(cus);
		
		return res>0;
	}
	
	public Map<String,Object> customerUpdateNewPhone(Customer customer)
	{
		int updated = mapper.customerUpdatePhone(customer);
		Map<String,Object> map = new HashMap<>();
		if(updated>0)
		{
			map.put("updated", updated);
			map.put("msg", "수정성공");
		}
		else
		{
			map.put("updated", updated);
			map.put("msg", "수정실패");
		}
		
		return map;
	}
	public Map<String,Object> customerUpdateNewAddress(Customer customer)
	{
		int updated = mapper.customerUpdateAddress(customer);
		Map<String,Object> map = new HashMap<>();
		if(updated>0)
		{
			map.put("updated", updated);
			map.put("msg", "수정성공");
		}
		else
		{
			map.put("updated", updated);
			map.put("msg", "수정실패");
		}
		
		return map;
	}
	
// Vendor 
	
	@Transactional
	public Boolean vendorJoinResult(Vendor vendor)
	{
		int res = mapper.vendorJoin(vendor);
		
		BCryptPasswordEncoder enc = new BCryptPasswordEncoder();
		vendor.setPwd(enc.encode(vendor.getPwd()));
		
		int res1 = mapper.addTradiUserByVendor(vendor);
		int res2 = mapper.endowAuthorityToVendor(vendor.getVid());		
		
		return res2>0;
	}

	public Map<String,Object> vendorIdCheck(String vid)
	{
		 Map<String,Object> map = new HashMap<>();
		 
		 if(mapper.vendordDupliCheck(vid)==null)
		 {
			 map.put("checked", true);
			 map.put("msg", "사용가능한 아이디입니다.");			 
		 }
		 else
		 {
			 map.put("checked", false);
			 map.put("msg", "입력하신 아이디가 존재합니다.");
		 }	 
		 return map;
	}		
	
	public Map<String,Object> vendorLoginResult(Vendor vendor)
	{				
		 String vid = vendor.getVid();
		 Map<String,Object> map = new HashMap<>();
		
		 Vendor vendorInfo = mapper.vendorLogin(vendor); 
		 
		 if(vendorInfo!=null)
		 {		 
			String msg = "로그인 성공";
			String url = "/vendor/";
			boolean login = true;
			
			map.put("vnum", vendorInfo.getVnum());
			map.put("permit", vendorInfo.getPermit());			
			map.put("vid", vendorInfo.getVid());
			map.put("login", login);
			map.put("msg",msg);
			map.put("url", url);
				 
			return map;
		}
		else
		{
			String msg = "로그인에 실패하였습니다.";
			String url = "/vendor/login";			 
			boolean login = false;
			
			map.put("login", login);			
			map.put("msg",msg);
			map.put("url", url);
				 
			return map;	
		}
	}	 	
}
