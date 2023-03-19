package com.spring.web.market.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


@EqualsAndHashCode(of={"canum"})
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Cart {
	
	private int canum;
	private int cnum;
	private int itemcode;
	private int amount;
	
}
