package com.spring.web.customer.service;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.spring.web.customer.mapper.CustomerMapper;
import com.spring.web.customer.vo.Cancle;
import com.spring.web.customer.vo.Customer;
import com.spring.web.customer.vo.Review;
import com.spring.web.market.vo.Order;
import com.spring.web.market.vo.OrderItem;

import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CustomerService {

	@Autowired
	private CustomerMapper mapper;
/*	
	@Autowired
	private JavaMailSender sender;

	public Boolean join(Customer customer)
	{		
		System.out.println("서비스 : "+customer.getNickname());
		
		return mapper.join(customer)>0;
	}
	
	public Map<String,Object> idChecked(String cid)
	{
		 Map<String,Object> map = new HashMap<>();
		 
		 if(mapper.idDupliCheck(cid)==null)
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
	
	
	
	public Map<String,Object> login(Customer customer)
	{				
		 String cid = customer.getCid();
		 Map<String,Object> map = new HashMap<>();
		 
		 Customer customerInfo = mapper.login(customer);
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

	//내 정보수정보기
	public Customer getmyinfo(int cnum)
	{
		Customer cus = mapper.getmyinfo(cnum);		
		
		return cus;
	}

	
	public Map<String,Object> editPwd(Customer cus,String pw)
	{
		Map<String,Object> map = new HashMap<>();
		
		int res = mapper.checkPwd(cus);
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
			int res2 = mapper.updatePwd(ncus);
						
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
	
	*/
	
	
	
	
	//날짜별 주문목록
	public List<List<Map<String,Object>>> getmyorder(int cnum,int pg, int row)
	{
		//List<Map<String,Object>> mlist = mapper.getmyorder(cnum);
		
		PageHelper.startPage(pg, row);
		PageInfo<Map<String,Object>> pageinfo = new PageInfo<>(mapper.getmyorder(cnum));
		List<Map<String,Object>> mlist = pageinfo.getList();	
		
		List<List<Map<String,Object>>> orderlist = new ArrayList<>();
		List<Map<String,Object>> daylist = new ArrayList<>();	//같은날짜의 주문목록을 담는 리스트(내부에 들어가는 리스트)
		
		for(int i =0; i<mlist.size() ; i++)
		{			
			Map<String,Object> m = mlist.get(i);		
				
			Timestamp iorderdate =(java.sql.Timestamp)m.get("ORDERDATE");
			Date orderdate = new Date(iorderdate.getTime());				
			
			BigDecimal onum = (java.math.BigDecimal)m.get("ONUM");	
				
			if(daylist.size()!=0)
			{
				if(!orderdate.equals((Date)daylist.get(0).get("orderdate")))
				{
					orderlist.add(daylist);
					daylist = new ArrayList<>();
				}
			}
			Map<String,Object> orderitem_map = new HashMap<>();
			if(i==0)
			{
				orderitem_map.put("end", pageinfo.getNavigateLastPage());
			}
			
			if(m.get("RNUM")==null)
			{
				orderitem_map.put("rcheck", true);
			}
			else
			{
				orderitem_map.put("rcheck", false);
			}
			BigDecimal amount = (java.math.BigDecimal)m.get("AMOUNT");
			BigDecimal confirm = (java.math.BigDecimal)m.get("CONFIRM");
			BigDecimal price = (java.math.BigDecimal)m.get("PRICE");
			BigDecimal oinum = (java.math.BigDecimal)m.get("OINUM");
			BigDecimal itemcode = (java.math.BigDecimal)m.get("ITEMCODE");
			String name = (String)m.get("NAME");
			
			OrderItem oitem = new OrderItem();
			
			oitem.setOinum(oinum.intValue());
			oitem.setAmount(amount.intValue());
			oitem.setConfirm(confirm.intValue());
			oitem.setPrice(price.intValue());
			oitem.setName(name);
			oitem.setItemcode(itemcode.intValue());
			
			String fname = (String)m.get("FNAME");
		
			orderitem_map.put("onum", onum);
			orderitem_map.put("orderdate", orderdate);
			orderitem_map.put("orderitem", oitem );
			orderitem_map.put("fname", fname);
			
			daylist.add(orderitem_map);		

			if(i==mlist.size()-1)
			{	
				orderlist.add(daylist);
			}
		}

		return orderlist;
	}
	
	// 주문상세보기
	public List<Map<String,Object>> getDetailOrder(int oinum)
	{
		List<Map<String,Object>> mlist = mapper.detailorder(oinum);
		List<Map<String,Object>> list = new ArrayList<>();
		
		for(int i=0; i<mlist.size(); i++)
		{
			Map<String,Object> m = mlist.get(i);			
			Map<String,Object> map = new HashMap<>();		
			
			Timestamp iorderdate =(java.sql.Timestamp)m.get("ORDERDATE");
			Date orderdate = new Date(iorderdate.getTime());						
			BigDecimal onum = (java.math.BigDecimal)m.get("ONUM");	

			String toname = (String)m.get("TONAME");
			String toaddress = (String)m.get("TOADDRESS");
			String tophone = (String)m.get("TOPHONE"); 
			String requestmemo =  (String)m.get("REQUESTMEMO");
			
			Order order = new Order();
			order.setToaddress(toaddress);
			order.setToname(toname);
			order.setTophone(tophone);
			order.setRequestmemo(requestmemo);
			order.setOrderdate(orderdate);
			order.setOnum(onum.intValue());			
			
			BigDecimal amount = (java.math.BigDecimal)m.get("AMOUNT");
			BigDecimal confirm = (java.math.BigDecimal)m.get("CONFIRM");
			BigDecimal price = (java.math.BigDecimal)m.get("PRICE");
			BigDecimal itemcode = (java.math.BigDecimal)m.get("ITEMCODE");
			String name = (String)m.get("NAME");
			
			OrderItem oitem = new OrderItem();
			
			if(m.get("DELICOMP")!=null)
			{	
				BigDecimal deliverycode = (java.math.BigDecimal)m.get("DELIVERYCODE");
				String delicomp = (String)m.get("DELICOMP");
				
				oitem.setDelicomp(delicomp);
				oitem.setDeliverycode(deliverycode.longValue());
			}		
			
			oitem.setOinum(oinum);
			oitem.setAmount(amount.intValue());
			oitem.setConfirm(confirm.intValue());
			oitem.setPrice(price.intValue());
			oitem.setName(name);
			oitem.setItemcode(itemcode.intValue());
			
			String fname = (String)m.get("FNAME");
			
			
			map.put("order", order);
			map.put("orderitem", oitem);
			map.put("fname", fname);			
			
			list.add(map);
		}	
		return list;
	}
	
	@Transactional
	public Map<String,Object> addCancle(Cancle cancle)
	{		
		Map<String,Object> map = new HashMap<>();		
		int res = mapper.regCancle(cancle);
		int res1 = mapper.setConfirmByCancle(cancle.getOinum());
		
		if(res>0)
		{
			map.put("reg", true);
			map.put("msg", "취소 신청을 완료했습니다.");
		}
		else
		{
			map.put("reg", false);
			map.put("msg", "신청 오류가 발생하였습니다.");
		}		
		return map;
	}
	
	public Map<String,Object> regReview(Review review)
	{		
		Map<String,Object> map = new HashMap<>();		
		int res = mapper.addReview(review);
		
		if(res>0)
		{
			map.put("reg", true);
			map.put("msg", "리뷰작성을 완료했습니다.");
		}
		else
		{
			map.put("reg", false);
			map.put("msg", "리뷰 등록 실패.");
		}		
		return map;
	}
	
	
	
	
	
/*	
	
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
	
	public boolean updateEmail(int cnum, String email)
	{		
		Customer cus = new Customer();
		cus.setCnum(cnum);
		cus.setEmail(email);
		int res = mapper.editEmail(cus);
		
		return res>0;
	}
	
	public Map<String,Object> updateNewPhone(Customer customer)
	{
		int updated = mapper.updatePhone(customer);
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
	public Map<String,Object> updateNewAddress(Customer customer)
	{
		int updated = mapper.updateAddress(customer);
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
	*/
}
