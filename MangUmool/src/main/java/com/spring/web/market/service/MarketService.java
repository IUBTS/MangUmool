package com.spring.web.market.service;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.spring.web.customer.vo.Customer;
import com.spring.web.customer.vo.Review;
import com.spring.web.market.controller.MarketController;
import com.spring.web.market.mapper.MarketMapper;
import com.spring.web.market.vo.Answer;
import com.spring.web.market.vo.Cart;
import com.spring.web.market.vo.Order;
import com.spring.web.market.vo.OrderItem;
import com.spring.web.market.vo.Question;
import com.spring.web.vendor.vo.I_Attach;
import com.spring.web.vendor.vo.Items;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import retrofit2.http.GET;

@Slf4j
@Service
public class MarketService {

	@Autowired
	private MarketMapper mapper;
	
	private static final String URL = "https://www.google.com";
    private static final String GET = "GET";
    private static final String USER_AGENT = "Mozilla/5.0";
    private static final String DATA = "test data";	
    private static final String POST = "POST";	
	
	//전체상품리스트
	public List<Map<String,Object>> getMain(String keyword,int pg, int row)
	{
		PageHelper.startPage(pg, row);
		PageInfo<Map<String,Object>> pageinfo = new PageInfo<>(mapper.getMain(keyword));
		
		List<Map<String,Object>> mlist = pageinfo.getList();
		List<Map<String,Object>> list = new ArrayList<>();

		for(int i =0;i<mlist.size();i++)
		{
			Map<String,Object> map = new HashMap<>();
			
			map.put("begin", pageinfo.getNavigateFirstPage());
			map.put("end", pageinfo.getNavigateLastPage());
			
			Map<String,Object> imap = mlist.get(i);
			Items item = new Items();
			
			BigDecimal itemcode = (java.math.BigDecimal)imap.get("ITEMCODE");	
					
			BigDecimal alcohol = (java.math.BigDecimal)imap.get("ALCOHOL");
			BigDecimal price = (java.math.BigDecimal)imap.get("PRICE");
			BigDecimal quantity = (java.math.BigDecimal)imap.get("QUANTITY");
			
			item.setAlcohol(alcohol.doubleValue());
			item.setItemcode(itemcode.intValue());
			item.setPrice(price.intValue());
			item.setQuantity(quantity.intValue());
			item.setBrandname((String)imap.get("BRANDNAME"));
			item.setName((String)imap.get("NAME"));		
			
			boolean found = false;
			String attname = (String)imap.get("FNAME");	
						
			if(attname==null)
			{					
				map.put("drink", item);
				list.add(map);
				if(!found) continue;
			}	
			
			BigDecimal ianum = (java.math.BigDecimal)imap.get("IANUM");
			
			I_Attach linkImg = new I_Attach();
			linkImg.setFname(attname);
			linkImg.setIanum(ianum.intValue());
			
			map.put("drink", item);
			map.put("att", linkImg);
			
			Optional<Object> optional = Optional.ofNullable(imap.get("AVG"));
			if(optional.isPresent())
			{						
				BigDecimal avg = (java.math.BigDecimal)imap.get("AVG");
				BigDecimal cnt = (java.math.BigDecimal)imap.get("CNT");
				map.put("cnt", cnt.intValue());
				map.put("avg", avg.doubleValue());
			}
			else 
			{
				map.put("cnt", 0);
				map.put("avg", 0.0);
			}
			list.add(map);				
		}			
		return list;		
	}
	
	public List<Map<String,Object>> getByAlcohol(int keyword1,int keyword2,int pg, int row)
	{
		Map<String,Integer> keymap = new HashMap<>();
		keymap.put("firstspot", keyword1);
		keymap.put("secondspot", keyword2);		
		
		PageHelper.startPage(pg, row);
		PageInfo<Map<String,Object>> pageinfo = new PageInfo<>(mapper.getItemByAlcohol(keymap));
		
		List<Map<String,Object>> mlist = pageinfo.getList();
		List<Map<String,Object>> list = new ArrayList<>();

		for(int i =0;i<mlist.size();i++)
		{
			Map<String,Object> map = new HashMap<>();
			
			map.put("begin", pageinfo.getNavigateFirstPage());
			map.put("end", pageinfo.getNavigateLastPage());

			Map<String,Object> imap = mlist.get(i);
			Items item = new Items();
			
			BigDecimal itemcode = (java.math.BigDecimal)imap.get("ITEMCODE");	
					
			BigDecimal alcohol = (java.math.BigDecimal)imap.get("ALCOHOL");
			BigDecimal price = (java.math.BigDecimal)imap.get("PRICE");
			BigDecimal quantity = (java.math.BigDecimal)imap.get("QUANTITY");
			
			item.setAlcohol(alcohol.doubleValue());
			item.setItemcode(itemcode.intValue());
			item.setPrice(price.intValue());
			item.setQuantity(quantity.intValue());
			item.setBrandname((String)imap.get("BRANDNAME"));
			item.setName((String)imap.get("NAME"));		
			
			boolean found = false;
			String attname = (String)imap.get("FNAME");	
						
			if(attname==null)
			{					
				map.put("drink", item);
				list.add(map);
				if(!found) continue;
			}	
			
			BigDecimal ianum = (java.math.BigDecimal)imap.get("IANUM");
			
			I_Attach linkImg = new I_Attach();
			linkImg.setFname(attname);
			linkImg.setIanum(ianum.intValue());
			
			map.put("drink", item);
			map.put("att", linkImg);
			
			Optional<Object> optional = Optional.ofNullable(imap.get("AVG"));
			if(optional.isPresent())
			{						
				BigDecimal avg = (java.math.BigDecimal)imap.get("AVG");
				BigDecimal cnt = (java.math.BigDecimal)imap.get("CNT");
				map.put("cnt", cnt.intValue());
				map.put("avg", avg.doubleValue());
			}
			else 
			{
				map.put("cnt", 0);
				map.put("avg", 0.0);
			}
			list.add(map);				
		}	
		return list;		
	}

	
	//아이템 상세보기	
	public Map<String,Object> getDetail(int itemcode)
	{		
		List<Map<String,Object>> list = mapper.getDetail(itemcode);
		Map<String,Object> map = new HashMap<>();		
		
		Map<String,Object> mitem = list.get(0);
		Items item = new Items();
		
		BigDecimal alcohol = (java.math.BigDecimal)mitem.get("ALCOHOL");
		BigDecimal vnum = (java.math.BigDecimal)mitem.get("VNUM");
		BigDecimal price = (java.math.BigDecimal)mitem.get("PRICE");
		BigDecimal quantity = (java.math.BigDecimal)mitem.get("QUANTITY");
		Timestamp iregdate =(java.sql.Timestamp)mitem.get("REGDATE");
		Date regdate = new Date(iregdate.getTime());				
		
		item.setAlcohol(alcohol.doubleValue());
		item.setMaterial((String)mitem.get("MATERIAL"));
		item.setPrice(price.intValue());
		item.setBrandname((String)mitem.get("BRANDNAME"));
		item.setName((String)mitem.get("NAME"));
		item.setType((String)mitem.get("TYPE"));
		item.setProducingarea((String)mitem.get("PRODUCINGAREA"));
		item.setDetailcontent((String)mitem.get("DETAILCONTENT"));
		item.setMaterial((String)mitem.get("MATERIAL"));
		item.setQuantity(quantity.intValue());
		item.setVnum(vnum.intValue());
		item.setItemcode(itemcode);
		item.setRegdate(regdate);
		
		String fname = (String)mitem.get("FNAME");
		Optional<Object> optional = Optional.ofNullable(mitem.get("RNUM"));		
		float sumScore=0;		
		
		List<String> nicklist = new ArrayList<>();
		if(optional.isPresent())
		{				
			for(int i = 0 ; i <list.size();i++)
			{
				Map<String,Object> m = list.get(i);
				Review rev = new Review();
				BigDecimal rnum = (java.math.BigDecimal)m.get("RNUM");
				BigDecimal cnum = (java.math.BigDecimal)m.get("CNUM");
				BigDecimal score = (java.math.BigDecimal)m.get("SCORE");
				String nickname = (String)m.get("NICKNAME");
				Timestamp reviewDate =(java.sql.Timestamp)m.get("REVIEWDATE");
				Date revregdate = new Date(reviewDate.getTime());				
				
				
				rev.setRnum(rnum.intValue());
				rev.setScore(score.intValue());
				rev.setCnum(cnum.intValue());
				rev.setContent((String)m.get("CONTENT"));
				rev.setRegDate(revregdate);
				
				if(!(item.getRevList().contains(rev))) item.getRevList().add(rev);
				
				sumScore += score.intValue();
				nicklist.add(nickname);
			}

			float bfavg = sumScore/list.size();
			float avg = Math.round(bfavg*100)/100.0f;	// 평균을 소수점 첫째짜리 까지만 표현
			map.put("avg", avg);
			map.put("nicklist", nicklist);
		}
		else 
		{
			map.put("avg", optional.isPresent());
		}
		if(fname==null)
		{
			map.put("item", item);			
			return map;
		}		
		for(int i =0 ; i<list.size() ; i++)
		{
			Map<String,Object> m = list.get(i);
			I_Attach att = new I_Attach();
			
			BigDecimal ianum = (java.math.BigDecimal)m.get("IANUM");
			String ifname = (String)m.get("FNAME");
			
			att.setItemcode(itemcode);
			att.setFname(ifname);
			att.setIanum(ianum.intValue());
			
			if(!(item.getImgList().contains(att))) item.getImgList().add(att);
			
		}
		List<Map<String,Object>> qlist = mapper.getQAList(itemcode);
		List<Map<String,Object>> qalist = new ArrayList<>();
		
		if(qlist.size()>0)
		{
			for(int i=0;i<qlist.size();i++)
			{
				Map<String,Object> qmap = new HashMap<>();
				Map<String,Object> q = qlist.get(i);
							
				BigDecimal qnum = (java.math.BigDecimal)q.get("QNUM");
				BigDecimal cnum = (java.math.BigDecimal)q.get("CNUM");
				Timestamp q_regdate =(java.sql.Timestamp)q.get("QREGDATE");
				Date qregdate = new Date(q_regdate.getTime());
				
				Question question = new Question();
				
				question.setQnum(qnum.intValue());
				question.setItemcode(itemcode);
				question.setRegDate(qregdate);
				question.setContent((String)q.get("QCONTENT"));
				question.setCnum(cnum.intValue());

				qmap.put("question", question);
								
				Answer answer = new Answer();						
				if(q.get("ANNUM")!=null)
				{					
					BigDecimal annum = (java.math.BigDecimal)q.get("ANNUM");
					Timestamp a_regdate =(java.sql.Timestamp)q.get("ANSREGDATE");
					Date aregdate = new Date(q_regdate.getTime());
					
					answer.setAnnum(annum.intValue());
					answer.setItemcode(itemcode);
					answer.setRegDate(aregdate);
					answer.setVnum(vnum.intValue());
					answer.setContent((String)q.get("ANSCONTENT"));
					
					qmap.put("answer", answer);
				}
				else
				{
					qmap.put("answer", false);	
				}	
				qalist.add(qmap);
			}
		}
		map.put("qalist", qalist);
		map.put("item", item);		
		try 
		{
			List<Map<String, Object>> similarItemList = pythonConnectGetSimilarItems(itemcode);
			map.put("similarItemList", similarItemList);
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}		
		
		return map;
	}
	
	//파이썬 서버 접속_유사상품추천
	public List<Map<String,Object>> pythonConnectGetSimilarItems(int itemcode) throws IOException {
		
	    URL url = new URL("http://localhost:7717/similar/"+itemcode);	   
	    List<Integer> recommendListByPython = getRecommendList(url);
	    List<Map<String,Object>> recommendListFromDB = getRecommendListFromDB(recommendListByPython);
	    
	    return recommendListFromDB;
	}
	
	//파이썬 서버 접속_개인맞춤추천시스템
	public List<Map<String,Object>> pythonConnectGetIndividualRecommendItems(int cnum) throws IOException {
		
	    URL url = new URL("http://localhost:7717/indiv_recommend/"+cnum);	    
	    List<Integer> recommendListByPython = getRecommendList(url);
	    List<Map<String,Object>> recommendListFromDB = getRecommendListFromDB(recommendListByPython);
	    
	    return recommendListFromDB;
	}
	
	//파이썬 서버접속_리스트가져오기
	public List<Integer> getRecommendList(URL url)  throws IOException 
	{
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		
	    connection.setRequestMethod(POST);     // POST 방식 요청
	    connection.setRequestProperty("User-Agent", USER_AGENT);
	    connection.setDoOutput(true);
	
	    DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
	    outputStream.writeBytes(DATA);
	    outputStream.flush();
	    outputStream.close();
	
	    int responseCode = connection.getResponseCode();
	
	    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
	    StringBuffer stringBuffer = new StringBuffer();
	    String inputLine;
	
	    while ((inputLine = bufferedReader.readLine()) != null)  {
	        stringBuffer.append(inputLine);
	    }
	    bufferedReader.close();
	
	    String response = stringBuffer.toString();
	    String[] strArr = response.replaceAll("\\[|\\]|\\s", "").split(",");
	    List<Integer> list = new ArrayList<>();
	    for(int i=0; i<strArr.length ; i++ )
	    {	    	
	    	int res1 = Integer.valueOf(strArr[i]);
	    	list.add(res1);
	    }	        
	    return list;
		
	}
	
	//추천리스트 DB에서 순서대로 가져와서 List에 정렬
	public List<Map<String,Object>> getRecommendListFromDB(List<Integer> recommendList)
	{		
		
			List<Map<String,Object>> slist = mapper.getRecommendList(recommendList);
			List<Map<String,Object>> list = new ArrayList<>();
			
			for(int i =0;i<slist.size();i++)
			{
				Map<String,Object> map = new HashMap<>();				

				Map<String,Object> imap = slist.get(i);
				Items item = new Items();
										
				BigDecimal alcohol = (java.math.BigDecimal)imap.get("ALCOHOL");
				BigDecimal price = (java.math.BigDecimal)imap.get("PRICE");
				BigDecimal quantity = (java.math.BigDecimal)imap.get("QUANTITY");
				
				item.setAlcohol(alcohol.doubleValue());
				item.setItemcode(recommendList.get(i));
				item.setPrice(price.intValue());
				item.setQuantity(quantity.intValue());
				item.setBrandname((String)imap.get("BRANDNAME"));
				item.setName((String)imap.get("NAME"));		
				
				boolean found = false;
				String attname = (String)imap.get("FNAME");	
							
				if(attname==null)
				{					
					map.put("drink", item);
					list.add(map);
					if(!found) continue;
				}					
				BigDecimal ianum = (java.math.BigDecimal)imap.get("IANUM");
				
				I_Attach linkImg = new I_Attach();
				linkImg.setFname(attname);
				linkImg.setIanum(ianum.intValue());
				
				map.put("drink", item);
				map.put("att", linkImg);
				
				Optional<Object> optional = Optional.ofNullable(imap.get("AVG"));
				if(optional.isPresent())
				{						
					BigDecimal avg = (java.math.BigDecimal)imap.get("AVG");
					BigDecimal cnt = (java.math.BigDecimal)imap.get("CNT");
					map.put("cnt", cnt.intValue());
					map.put("avg", avg.doubleValue());
				}
				else 
				{
					map.put("cnt", 0);
					map.put("avg", 0.0);
				}
				list.add(map);				
			}			
			return list;					
	}	
	
	

	
	
	
	
	
	public Map<String,Object> regisQuestion(Question question)
	{
		Map<String,Object> map = new HashMap<>();
		int res = mapper.addQuestion(question);
		
		if(res>0)
		{
			map.put("reg", true);
			map.put("msg", "기재하신 문의사항을 등록하였습니다.");
		}
		else
		{
			map.put("reg", false);
			map.put("msg", "문의사항 등록 실패");
		}		
		return map;
	}		
	
	
	public Map<String,Object> addCart(Cart cart)
	{
		int res = mapper.addCart(cart);
		Map<String,Object> map = new HashMap<>();
				
		if(res>0)
		{
			map.put("added", true);
			map.put("msg", "물품을 장바구니에 담았습니다.");
		}
		else 
		{
			map.put("added", false);
			map.put("msg", "장바구니에 담기 실패");
		}
		return map;
	}
	
	public Customer getUserByNum(int cnum) 
	{				
		Customer cus = mapper.getUserByCnum(cnum);
		
		return cus; 
	}
	
	public List<Map<String,Object>> getCartByCnum(int cnum)
	{
		List<Map<String,Object>> mlist = mapper.getCart(cnum);
		List<Map<String,Object>> list = new ArrayList<>();
		
		for(int i=0; i<mlist.size();i++)
		{
			Map<String,Object> map = new HashMap<>();
			Map<String,Object> m = mlist.get(i);
			
			BigDecimal canum = (java.math.BigDecimal)m.get("CANUM");
			BigDecimal amount = (java.math.BigDecimal)m.get("AMOUNT");
			BigDecimal itemcode = (java.math.BigDecimal)m.get("ITEMCODE");
			BigDecimal price = (java.math.BigDecimal)m.get("PRICE");
			
			Cart cart = new Cart();
			cart.setCanum(canum.intValue());
			cart.setAmount(amount.intValue());
			cart.setItemcode(itemcode.intValue());
						
			map.put("cart", cart);
			map.put("price", price.intValue());
			map.put("name", (String)m.get("NAME"));
			map.put("fname", (String)m.get("FNAME"));
			
			list.add(map);
		}	
		return list;
	}
	
	
	public Map<String,Object> delItemsToCart(String[] icl,int cnum)
	{
		List<Cart> list = new ArrayList<>();
		for(int i =0; i<icl.length;i++)
		{
			Cart cart = new Cart();
			cart.setCnum(cnum);
			cart.setItemcode(Integer.parseInt(icl[i]));
			
			list.add(cart);
		}
		int res = mapper.delCartItems(list);		
		Map<String,Object> map = new HashMap<>();
		if(res>0)
		{
			map.put("deleted", true);
			map.put("msg", "장바구니 삭제 성공");
		}
		else
		{
			map.put("deleted", false);
			map.put("msg", "장바구니 삭제 실패");
		}
		
		return map;
	}
	
	public List<Map<String,Object>> getOrderList(String items)
	{		
		List<Map<String,Object>> list = new ArrayList<>();
		JSONParser parser = new JSONParser(); 
		try 
		{ 
			JSONArray jsArr= (JSONArray)parser.parse(items);
			for( int i=0; i<jsArr.size();i++) 
			{ 
				Map<String,Object> map = new HashMap<>();
				JSONObject jsObj= (JSONObject) jsArr.get(i); 
				int itemcode = Integer.valueOf((String) jsObj.get("itemcode"));
				int amount = Integer.valueOf((String) jsObj.get("amount"));
				int itemttlprice = Integer.valueOf((String) jsObj.get("itemttlprice"));
				String name = (String) jsObj.get("name");
				
				map.put("itemttlprice", itemttlprice);
				map.put("itemcode", itemcode);
				map.put("amount", amount);
				map.put("name", name);

				list.add(map);
			}
		} 
		catch (ParseException e) { 
			e.printStackTrace(); 
			}
				
		return list;
	}	
	
	@Transactional
	public Map<String,Object> addOrderList(MultiValueMap<String, String> formData,int cnum)
	{		
		Order order = new Order();
		order.setCnum(cnum);
		order.setTtlprice(Integer.valueOf(formData.get("ttlprice").get(0)));
		order.setFromaddress(formData.get("fromaddress").get(0));
		order.setFromname(formData.get("fromname").get(0));
		order.setFromphone(formData.get("fromphone").get(0));
		order.setToaddress(formData.get("toaddress").get(0));
		order.setToname(formData.get("toname").get(0));
		order.setTophone(formData.get("tophone").get(0));
		order.setRequestmemo(formData.get("requestmemo").get(0));
		
		List<String> itemcodelist = formData.get("itemcode");
		List<String> amountlist = formData.get("amount");
		List<String> namelist = formData.get("name");
		List<String> itemttlpricelist = formData.get("itemttlprice");	
		
		List<OrderItem> list = new ArrayList<>(); 
		List<Cart> dellist = new ArrayList<>(); 
		
		for(int i=0;i<itemcodelist.size();i++)
		{
			OrderItem oi = new OrderItem();
			Cart cart = new Cart();
						
			oi.setCnum(cnum);			
			oi.setItemcode(Integer.valueOf(itemcodelist.get(i)));
			oi.setName(namelist.get(i));
			oi.setAmount(Integer.valueOf(amountlist.get(i)));
			oi.setPrice(Integer.valueOf(itemttlpricelist.get(i)));
			
			cart.setCnum(cnum);
			cart.setItemcode(Integer.valueOf(itemcodelist.get(i)));
			
			list.add(oi);
			dellist.add(cart);
		}
		
		Map<String,Object> map = new HashMap<>();
		if(Integer.valueOf(formData.get("rootnum").get(0))==1)
		{
			int res = mapper.addOrderList(order);
			int res1 = mapper.addOrderItem(list);		
			if(res>0 && res1>0)
			{
				map.put("order", order);
				map.put("msg", "주문이 완료되었습니다.");
			}			
			else
			{
				map.put("order", null);
				map.put("msg", "주문이 실패되었습니다.");
			}
		}
		else
		{
			int res = mapper.addOrderList(order);
			int res1 = mapper.addOrderItem(list);
			int res2 = mapper.delCartItems(dellist);	
			if(res>0 && res1>0 && res2>0)
			{
				map.put("order", order);
				map.put("msg", "주문이 완료되었습니다.");
			}			
			else
			{
				map.put("order", null);
				map.put("msg", "주문이 실패되었습니다.");
			}
		}		
		return map;
	}
}
