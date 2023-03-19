package com.spring.web.user;

import org.apache.ibatis.annotations.Mapper;

import com.spring.web.customer.vo.Customer;
import com.spring.web.vendor.vo.Vendor;

@Mapper
public interface UserMapper {

// Customer	
	
	public int customerJoin(Customer customer);
	
	public int addTradiUser(Customer customer);
	
	public int endowAuthorityToCustomer(String cid);
	
	public String customeridDupliCheck(String cid);
	
	public Customer customerLogin(Customer customer);
	
	public Customer customerGetmyinfo(int cnum);
	
	public int customerCheckPwd(Customer customer);
	
	public int customerUpdatePwd(Customer customer);	
	
	public int customerEditEmail(Customer customer);
	
	public int customerUpdatePhone(Customer customer);
	
	public int customerUpdateAddress(Customer customer);
	
	public Customer getCnumByCid(String cid);
	
// Vendor
	
	public int vendorJoin(Vendor vendor);
	
	public int addTradiUserByVendor(Vendor vendor);
	
	public int endowAuthorityToVendor(String vid);
	
	public String vendordDupliCheck(String vid);
	
	public Vendor vendorLogin(Vendor vendor);
	
	public Vendor getVendorByVid(String vid);
}
