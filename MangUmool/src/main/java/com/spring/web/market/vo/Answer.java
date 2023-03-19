package com.spring.web.market.vo;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(of={"annum"})
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Answer {

	private int annum;
	private int itemcode;
	private int vnum;
	private int qnum;
	private String content;
	private Date regDate;
}
