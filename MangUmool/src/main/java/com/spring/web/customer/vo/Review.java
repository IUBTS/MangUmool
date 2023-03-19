package com.spring.web.customer.vo;

import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Review {
	
	private int rnum;
	private int cnum;
	private int itemcode;
	private int	oinum;
	private Date regDate;
	private String content;
	private int score;
}
