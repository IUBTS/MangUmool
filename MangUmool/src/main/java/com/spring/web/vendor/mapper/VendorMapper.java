package com.spring.web.vendor.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.spring.web.market.vo.Answer;
import com.spring.web.market.vo.OrderItem;
import com.spring.web.vendor.vo.I_Attach;
import com.spring.web.vendor.vo.Items;
import com.spring.web.vendor.vo.Vendor;



@Mapper
public interface VendorMapper {

	public int additem(Items items);
	
	public int addimage(List<I_Attach> list);
	
	public List<Items> itemlist(int vnum);
	
	public List<Map<String,Object>> getDetail(Items items);
	
	public int addEditImage(List<I_Attach> list);
	
	public int deleteImg(I_Attach ia);
	
	public int itemUpdate(Items items);	

	public int itemPostWait(int itemcode);
	
	public List<Map<String,Object>> requestOrder(int vnum);
	
	public int setDelicode(OrderItem orderitem);
	
	public int subQuantity(OrderItem orderitem);
	
	public List<Map<String,Object>> downExcel(int vnum);
	
	public int completeDelivery(int oinum);
	
	public List<Map<String,Object>> showQuestion(int vnum);
	
	public int regAnswerByQnum(Answer answer);

	public List<Map<String,Object>> getCancleRequest(int vnum);
	
	public int approveCancleOrder(int oinum);
}
