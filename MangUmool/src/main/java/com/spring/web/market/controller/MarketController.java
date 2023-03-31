package com.spring.web.market.controller;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.spring.web.customer.vo.Customer;
import com.spring.web.market.service.MarketService;
import com.spring.web.market.vo.Cart;
import com.spring.web.market.vo.Order;
import com.spring.web.market.vo.Question;
import com.spring.web.vendor.vo.Items;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/drinks")
public class MarketController {

	@Autowired
	private MarketService svc;
	
	@Autowired
	private HttpSession session;
	

	@GetMapping("/")
	public String home()
	{		
		return "thymeleaf/market/home";
	}

	@GetMapping("/find")
	public String find(Model m,@RequestParam("keyword")String keyword, @RequestParam(required =false, defaultValue="1") int pg)
	{	
		List<Map<String,Object>> list = svc.getMain(keyword,pg,4);
		m.addAttribute("finditems", 1);
		m.addAttribute("list",list);
		m.addAttribute("keyword", keyword);
		if(list.size()!=0)
		{
			m.addAttribute("end",list.get(0).get("end"));
			m.addAttribute("pg",pg);
		}
		else { m.addAttribute("end",0); }
		if(session.getAttribute("cnum")!=null)
		{			
			try 
			{
				List<Map<String, Object>> recommendList = svc.pythonConnectGetIndividualRecommendItems((int)session.getAttribute("cnum"));
				m.addAttribute("recommendList", recommendList);
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
		return "thymeleaf/market/main";
	}
	
	@GetMapping("/findAlcohol")
	public String findByAlcoho(Model m,@RequestParam("keyword1")int keyword1 ,@RequestParam("keyword2") int keyword2, @RequestParam(required =false, defaultValue="1") int pg)
	{	
		List<Map<String,Object>> list = svc.getByAlcohol(keyword1,keyword2,pg,4);
		m.addAttribute("finditems", 2);
		m.addAttribute("list",list);
		m.addAttribute("keyword1", keyword1);
		m.addAttribute("keyword2", keyword2);
		if(list.size()!=0)
		{
			m.addAttribute("end",list.get(0).get("end"));
			m.addAttribute("pg",pg);
		}
		else { m.addAttribute("end",0); }
		if(session.getAttribute("cnum")!=null)
		{			
			try 
			{
				List<Map<String, Object>> recommendList = svc.pythonConnectGetIndividualRecommendItems((int)session.getAttribute("cnum"));
				m.addAttribute("recommendList", recommendList);
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
		return "thymeleaf/market/main";
	}
	

	@GetMapping("/detail/{itemcode}")
	public String detail(Model m,@PathVariable int itemcode)
	{
		Map<String,Object> map = svc.getDetail(itemcode);		
		m.addAttribute("drink", map);
		return "thymeleaf/market/detail";
	}
	
	@ResponseBody
	@PostMapping("/regquestion")
	public Map<String,Object> regQuestionOnDetail(Question question)
	{
		question.setCnum((int)session.getAttribute("cnum"));
		return 	svc.regisQuestion(question);
	}		


	@ResponseBody
	@PostMapping("/addcart")
	public Map<String,Object> addItemToCart(Cart cart)
	{
		cart.setCnum((int)session.getAttribute("cnum"));
		return svc.addCart(cart);
	}	
	
	@GetMapping("/cartlist")
	public String showCart(Model m)
	{
		int cnum = (int)session.getAttribute("cnum");	
		List<Map<String,Object>> list = svc.getCartByCnum(cnum);
		m.addAttribute("list",list);
		return "thymeleaf/market/cartForm";

	}
	
	@ResponseBody
	@PostMapping("/deltocart")
	public Map<String,Object> delCart(String[] icl)
	{
		int cnum = (int)session.getAttribute("cnum");		
		Map<String,Object> map = svc.delItemsToCart(icl,cnum);
		return map;
	}	
	
	@PostMapping("/buynow")
	public String getOrderForm(Items item,int amount,Model m)
	{
		int cnum = (int)session.getAttribute("cnum");	
		Customer cus = svc.getUserByNum(cnum);		
		
		m.addAttribute("cus",cus);
		m.addAttribute("oneitem",item);
		m.addAttribute("amount", amount);
		
		return "thymeleaf/market/orderForm";
	}
	
	@PostMapping("/buyitems")
	public String getOrderListForm(@RequestParam(value="orderlistform")String items,@RequestParam(value="orderprice")int ttlprice,Model m)
	{		
		int cnum = (int)session.getAttribute("cnum");	
		Customer cus = svc.getUserByNum(cnum);
		List<Map<String,Object>>list = svc.getOrderList(items);
		
		m.addAttribute("cus",cus);
		m.addAttribute("list", list);
		m.addAttribute("ttlprice", ttlprice);
		
		return "thymeleaf/market/orderForm";
	}
	
	
	@PostMapping("/saveorder")
	public String payrequest(@RequestBody MultiValueMap<String, String> formData,Model m)
	{	
		int cnum = (int)session.getAttribute("cnum");
		Map<String,Object> map =svc.addOrderList(formData,cnum);		
		m.addAttribute("res", map);
		
		return "thymeleaf/market/payComplete";
	}
	
	
	
}
