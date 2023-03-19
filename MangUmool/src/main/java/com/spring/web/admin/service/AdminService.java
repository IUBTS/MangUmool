package com.spring.web.admin.service;


import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;

import com.spring.web.admin.mapper.AdminMapper;
import com.spring.web.admin.vo.Admin;
import com.spring.web.customer.vo.Customer;
import com.spring.web.market.vo.Order;
import com.spring.web.market.vo.OrderItem;
import com.spring.web.vendor.vo.Items;
import com.spring.web.vendor.vo.Vendor;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@Service
public class AdminService {
	
	@Autowired
	private AdminMapper mapper;
	
	public Boolean join(Admin admin)
	{	
		return mapper.join(admin)>0;
	}
	
	public Map<String,Object> login(Admin admin)
	{				
		 String aid = admin.getAid();
		 Map<String,Object> map = new HashMap<>();
		
		 Admin adminInfo = mapper.login(admin); 
		 
		 if(adminInfo!=null)
		 {		 
			String msg = "로그인 성공";
			String url = "/admin/";
			boolean login = true;
			
			map.put("vnum", adminInfo.getAdnum());
			map.put("vid", adminInfo.getAid());
			map.put("login", login);
			map.put("msg",msg);
			map.put("url", url);
				 
			return map;
		}
		else
		{
			String msg = "로그인에 실패하였습니다.";
			String url = "/admin/login";			 
			boolean login = false;
			
			map.put("login", login);			
			map.put("msg",msg);
			map.put("url", url);
				 
			return map;	
		}
	}	 	
	
	public List<Items> postRequest()
	{
		List<Items> list = mapper.postRequested();		
		return list;
	}
	
	public Map<String,Object> approvePost(int itemcode)
	{
		int res = mapper.postItems(itemcode);
		return permitResult(res);
	}
	
	public List<Vendor> getJoinVendorInfo()
	{
		return mapper.getNewVendorJoin();
	}
	
	public Map<String,Object> vendorJoinPermit(int vnum)
	{
		int res = mapper.vendorJoinPermit(vnum);
		return permitResult(res);
	}
	
	// 승인결과처리 메소드
	private Map<String,Object> permitResult(int res)
	{
		Map<String,Object> map = new HashMap<>();
		
		if(res>0)
		{
			map.put("apporve", true);
			map.put("msg","승인성공");
		}
		else
		{
			map.put("apporve", false);
			map.put("msg","승인실패");
		}		
		return map;
	}
	
	
	
	public Map<String,Object> getOrderByOinum(int oinum)
	{
		Map<String,Object> map = new HashMap<>();
		Map<String,Object> o = mapper.findOrderByOinum(oinum);
		if(o!=null)
		{
			BigDecimal onum = (java.math.BigDecimal)o.get("ONUM");
			BigDecimal cnum = (java.math.BigDecimal)o.get("CNUM");
			
			String fromphone = (String)o.get("FROMPHONE");
			String fromname = (String)o.get("FROMNAME");
			String requestmemo = (String)o.get("REQUESTMEMO");
			
			Timestamp regorderdate = (java.sql.Timestamp) o.get("ORDERDATE");
			Date orderdate = new Date(regorderdate.getTime());
			
			Order order = new Order();
			
			order.setOnum(onum.intValue());
			order.setCnum(cnum.intValue());
			order.setFromphone(fromphone);
			order.setFromname(fromname);
			order.setRequestmemo(requestmemo);
			order.setOrderdate(orderdate);				
		
			BigDecimal amount = (java.math.BigDecimal)o.get("AMOUNT");
			BigDecimal price = (java.math.BigDecimal)o.get("PRICE");
			BigDecimal itemcode = (java.math.BigDecimal)o.get("ITEMCODE");
			BigDecimal confirm = (java.math.BigDecimal)o.get("CONFIRM");
			String name = (String)o.get("NAME");
			OrderItem oitem = new OrderItem();
			
			oitem.setOinum(oinum);
			oitem.setItemcode(itemcode.intValue());
			oitem.setAmount(amount.intValue());
			oitem.setName(name);
			oitem.setPrice(price.intValue());
			oitem.setConfirm(confirm.intValue());
			
			map.put("order", order);
			map.put("oitem", oitem);
		}
		else
		{
			return null;
		}
		return map;
	}
	
	
	public Customer getUserInfoByCid(String cid)
	{		
		return mapper.findUserByID(cid);		
	}
	
	public Vendor getVendorByVid(String vid)
	{		
		return mapper.findVendorByID(vid);		
	}
	
	public List<Map<String,Object>> getCancleListByDate(Date start,Date end,String vid)
	{
		Map<String,Object> resourceMap = new HashMap<>();
		resourceMap.put("start", start);
		resourceMap.put("end", end);
		resourceMap.put("vid", vid);
		
		List<Map<String,Object>> mlist = mapper.findByCancleList(resourceMap);
			
		return getOrderList(mlist);
	}
	
	public List<Map<String,Object>> getOrderListByDate(Date start,Date end,String vid)
	{
		Map<String,Object> resourceMap = new HashMap<>();
		resourceMap.put("start", start);
		resourceMap.put("end", end);
		resourceMap.put("vid", vid);
		
		List<Map<String,Object>> mlist = mapper.findByOrderList(resourceMap);
			
		return getOrderList(mlist);
	}
	
	
	
	
	//주문목록 정렬
	private List<Map<String,Object>> getOrderList(List<Map<String,Object>> mlist)
	{
		List<Map<String,Object>> list = new ArrayList<>();
		
		if(mlist.size()>0)
		{
			for(int i = 0 ; i < mlist.size() ; i ++)
			{				
				Map<String,Object> o = mlist.get(i);
				
				BigDecimal onum = (java.math.BigDecimal)o.get("ONUM");
				BigDecimal cnum = (java.math.BigDecimal)o.get("CNUM");
				
				String fromphone = (String)o.get("FROMPHONE");
				String fromname = (String)o.get("FROMNAME");
				String requestmemo = (String)o.get("REQUESTMEMO");
				
				Timestamp regorderdate = (java.sql.Timestamp) o.get("ORDERDATE");
				Date orderdate = new Date(regorderdate.getTime());
				
				Order order = new Order();
				
				order.setOnum(onum.intValue());
				order.setCnum(cnum.intValue());
				order.setFromphone(fromphone);
				order.setFromname(fromname);
				order.setRequestmemo(requestmemo);
				order.setOrderdate(orderdate);
						
				BigDecimal oinum = (java.math.BigDecimal)o.get("OINUM");
				BigDecimal amount = (java.math.BigDecimal)o.get("AMOUNT");
				BigDecimal price = (java.math.BigDecimal)o.get("PRICE");
				BigDecimal itemcode = (java.math.BigDecimal)o.get("ITEMCODE");
				BigDecimal confirm = (java.math.BigDecimal)o.get("CONFIRM");
				String name = (String)o.get("NAME");
				OrderItem oitem = new OrderItem();
				
				oitem.setOinum(oinum.intValue());
				oitem.setItemcode(itemcode.intValue());
				oitem.setAmount(amount.intValue());
				oitem.setName(name);
				oitem.setPrice(price.intValue());
				oitem.setConfirm(confirm.intValue());
				Map<String,Object> map = new HashMap<>();
				map.put("order", order);
				map.put("oitem", oitem);
				
				list.add(map);			
			}
		}			
		return list;
	}
	
	
	
	
	
}
