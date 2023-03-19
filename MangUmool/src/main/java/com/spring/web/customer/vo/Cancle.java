package com.spring.web.customer.vo;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(of={"canum"})
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Cancle {

	private int cannum;
	private int onum;
	private int oinum;
	private int cnum;
	private int itemcode;
	private int checked;
	private Date regDate;
	private String reason;
	
	
}
