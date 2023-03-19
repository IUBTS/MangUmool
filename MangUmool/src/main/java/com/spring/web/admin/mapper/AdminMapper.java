package com.spring.web.admin.mapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.spring.web.admin.vo.Admin;
import com.spring.web.customer.vo.Customer;
import com.spring.web.vendor.vo.Items;
import com.spring.web.vendor.vo.Vendor;

@Mapper
public interface AdminMapper {

	public int join(Admin admin);
	
	public Admin login(Admin admin);
	
	public List<Items> postRequested(); 
	
	public int postItems(int itemcode);
	
	public List<Vendor> getNewVendorJoin();
	
	public int vendorJoinPermit(int vnum);
	
	public Customer findUserByID(String cid);
	
	public Vendor findVendorByID(String vid);
	
	public Map<String,Object> findOrderByOinum(int oinum);
	
	public List<Map<String,Object>> findByCancleList(Map map);
	
	public List<Map<String,Object>> findByOrderList(Map map);
}
