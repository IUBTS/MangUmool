package com.spring.web.customer.vo;

import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Customer {

	public int cnum;
	public String cid;
	public String pwd;
	public String cname;
	public String nickname;
	public String phone;
	public String email;
	public String address;
	public Date birth;
	public int adultpass;	

	
}
