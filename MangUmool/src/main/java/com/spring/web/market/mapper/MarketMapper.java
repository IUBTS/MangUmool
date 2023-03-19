package com.spring.web.market.mapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.spring.web.customer.vo.Customer;
import com.spring.web.market.vo.Cart;
import com.spring.web.market.vo.Order;
import com.spring.web.market.vo.OrderItem;
import com.spring.web.market.vo.Question;
import com.spring.web.vendor.vo.I_Attach;
import com.spring.web.vendor.vo.Items;

@Mapper
public interface MarketMapper {

	public List<Map<String,Object>> getMain(String keyword);
	
	public List<Map<String,Object>> getItemByAlcohol(Map<String,Integer> map);

	public List<Map<String,Object>> getDetail(int itemcode);
	
	public List<Map<String,Object>> getQAList(int itemcode);
	
	public Integer addQuestion(Question question);
	
	public Integer addCart(Cart cart);
	
	public Customer getUserByCnum(int cnum);
	
	public List<Map<String,Object>> getCart(int cnum);
	
	public Integer delCartItems(List<Cart> list);
	
	public int addOrderList(Order order);
	
	public int addOrderItem(List<OrderItem> list);

}
