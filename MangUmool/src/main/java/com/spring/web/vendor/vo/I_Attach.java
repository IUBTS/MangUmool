package com.spring.web.vendor.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(exclude={"itemcode","fname"})
@AllArgsConstructor
@NoArgsConstructor
@Data
public class I_Attach {

	public int itemcode;
	public int ianum;
	public String fname;
	
}
