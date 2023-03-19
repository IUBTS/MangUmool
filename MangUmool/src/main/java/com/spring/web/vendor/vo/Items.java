package com.spring.web.vendor.vo;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import com.spring.web.customer.vo.Review;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(of="itemcode")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Items {

	public int itemcode;
	public int vnum;
	public String name;	
	public String type; //카테고리로 검색할려는데 얘도 따로 테이블을 만들어주는게 나을까.
	public double alcohol;
	public String material;
	public int price;
	public int quantity;
	public String brandname;     //상품마다 특정 회사에 속해있는 제품들이 있는데 이러면 회사 테이블을 따로 만드는게나을라나. 
	public String producingarea;
	public Date regdate;
	public String detailcontent;
	public int post;
	
	private List<I_Attach> imgList = new ArrayList<>();
	private List<Review> revList = new ArrayList<>();
	
	public Items(int itemcode) 
	{	
		this.itemcode = itemcode;
	}
	
	
	
}
