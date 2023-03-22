package com.spring.web.customer.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.spring.web.customer.vo.Cancle;
import com.spring.web.customer.vo.Customer;
import com.spring.web.customer.vo.Review;

@Mapper
public interface CustomerMapper {

	public List<Map<String,Object>> getmyorder(int cnum);
	
	public int regCancle(Cancle cancle);
	
	public int setConfirmByCancle(int oinum);
	
	public int addReview(Review review);
	
	public List<Map<String,Object>> detailorder(int oinum);

}
