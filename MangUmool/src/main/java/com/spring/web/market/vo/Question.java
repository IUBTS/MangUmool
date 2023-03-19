package com.spring.web.market.vo;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(of={"qnum"})
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Question {
	
	private int qnum;
	private int itemcode;
	private int cnum;
	private String content;
	private Date regDate;
}
