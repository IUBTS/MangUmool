package com.spring.web.market.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(of={"onum"})
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Order {

	private int onum;
	private int cnum;
	private int ttlprice;
	private String toname;
	private String toaddress;
	private String tophone;
	private String fromname;
	private String fromaddress;
	private String fromphone;
	private String requestmemo;
	private Date orderdate;
	
	private List<OrderItem> OrderItemList = new ArrayList<>(); 
	
}
