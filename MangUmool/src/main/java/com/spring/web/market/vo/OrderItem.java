package com.spring.web.market.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


@EqualsAndHashCode(of={"oinum"})
@NoArgsConstructor
@AllArgsConstructor
@Data
public class OrderItem {

	private int oinum;
	private int onum;
	private int cnum;
	private int price;
	private int itemcode;
	private String name;
	private int amount;
	private int confirm;
	private long deliverycode;
	private String delicomp;
}
