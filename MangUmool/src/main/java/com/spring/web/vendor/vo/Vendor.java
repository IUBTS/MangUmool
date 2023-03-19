package com.spring.web.vendor.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Vendor {

	public int vnum;
	public String vid;
	public String pwd;
	public int com_number;
	public String brandname;
	public String ceoname;
	public String address;
	public String website;
	public String phone;
	public int permit;
	
}
