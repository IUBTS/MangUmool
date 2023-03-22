package com.spring.web.vendor.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;

import com.spring.web.customer.controller.CustomerController;
import com.spring.web.customer.vo.Customer;
import com.spring.web.market.vo.Answer;
import com.spring.web.market.vo.OrderItem;
import com.spring.web.vendor.service.VendorService;
import com.spring.web.vendor.vo.I_Attach;
import com.spring.web.vendor.vo.Items;
import com.spring.web.vendor.vo.Vendor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping("/vendor")
@Controller
public class VendorController {

	@Autowired
	private VendorService svc;
	
	@Autowired
	private HttpSession session;
	
	@GetMapping("/")
	public String home()
	{		
		return "thymeleaf/vendor/vloginForm";
	}
	
	@GetMapping("/add")//아이템 등록form
	public String addItemForm()
	{
		return "thymeleaf/vendor/addItems";
	}
	
	@ResponseBody
	@PostMapping("/add")//아이템 정보등록 + 이미지 경로를 DB에 저장해서 이미지띄울때 바로 쓸 수 있게 되는가.
	public String addItem(@RequestParam("files") MultipartFile[] mfiles, @RequestParam("thumimg")MultipartFile[] thumimg,HttpServletRequest request,Items items)
	{
		int vnum = (int) session.getAttribute("vnum");
		items.setVnum(vnum);
		
		Map<String,Object> addmap = new HashMap<>();
		addmap.put("mfiles", mfiles);
		addmap.put("thumimg", thumimg);
		addmap.put("request", request);
		addmap.put("items", items);
		
		String msg = svc.addItem(addmap);
	
		return msg;
	}
	
	@GetMapping("/list") //vnum으로 본인이 등록한 제품 리스트 호출
	public String getList(@RequestParam(required =false, defaultValue="1") int pg,Model m)
	{
		int vnum = (int)session.getAttribute("vnum");
		Map<String,Object> map = svc.getList(vnum,pg,3);
		List<Items> list = (List<Items>)map.get("list");
		
		m.addAttribute("list",list);
		if(list.size()!=0)
		{
			m.addAttribute("end",(int)map.get("end"));
			m.addAttribute("pg",pg);
		}
		else { m.addAttribute("end",0); }
								
		return "thymeleaf/vendor/regList";
	}
	
	@GetMapping("/detail/{itemcode}") //제품 상세보기
	public String getDetail(@PathVariable("itemcode") int itemcode,Model m)
	{
		
		int vnum = (int)session.getAttribute("vnum");		
		Map<String,Object> map = svc.getDetail(itemcode,vnum);		

		m.addAttribute("items", map);
		
						
		return "thymeleaf/vendor/itemdetail";
	}
	
	@ResponseBody
	@PostMapping("/delimg")
	public Map<String,Object> delimg(I_Attach ia,String name)
	{
		Map<String,Object> map = svc.delimg(ia, name);
		
		return map;
	}
	
	
	@ResponseBody
	@PostMapping("/addimg")
	public Map<String,Object> addItemImg(@RequestParam("ifiles") MultipartFile[] mfiles, @RequestParam("thumimg")MultipartFile[] thumimg,HttpServletRequest request,@RequestParam("itemcode")int itemcode,@RequestParam("name") String name)
	{
		int vnum = (int) session.getAttribute("vnum");
		
		Map<String,Object> addmap = new HashMap<>();
		addmap.put("mfiles", mfiles);
		addmap.put("thumimg", thumimg);
		addmap.put("request", request);
		addmap.put("itemcode", itemcode);
		addmap.put("name", name);
				
		Map<String,Object> map = svc.addImgfiles(addmap);
		
		return map;
	}
	
	@ResponseBody
	@PostMapping("/updateItem")
	public Map<String,Object> updateItem(Items item)
	{	
		Map<String,Object> map = svc.editIteminfo(item);
		
		return map;
	}	
	
	
	@GetMapping("/orderlist")
	public String orderList(Model m,@RequestParam(required =false, defaultValue="1") int pg)
	{
		int vnum = (int)session.getAttribute("vnum");	
		List<Map<String,Object>> list = svc.getNewOrder(vnum,pg,3);
		m.addAttribute("list",list);
		if(list.size()!=0)
		{
			m.addAttribute("end",list.get(0).get("end"));
			m.addAttribute("pg",pg);
		}
		else { m.addAttribute("end",0); }
		
		return "thymeleaf/vendor/orderList";
	}
	
	@ResponseBody
	@PostMapping("/godelivery")
	public Map<String,Object> setDelivery(OrderItem orderitem)
	{		
		Map<String,Object> map = svc.writeDelicode(orderitem);	
		return map;
	}
	
	@GetMapping("/exceldown")
	public void excelDownloader( HttpServletRequest request, HttpServletResponse response)
	{
		int vnum =(int)session.getAttribute("vnum");
		svc.downOrderList(vnum,request,response);
	}
	
	@ResponseBody
	@PostMapping("/completed")
	public Map<String,Object> completeDelivery(@RequestParam("oinum") int oinum)
	{		
		Map<String,Object> map = svc.completeDeli(oinum);	
		return map;
	}
	
	@GetMapping("/getQA")
	public String QAListByVnum(Model m,@RequestParam(required =false, defaultValue="1") int pg)
	{
		int vnum =(int)session.getAttribute("vnum");
		List<List<Map<String,Object>>> list = svc.getQAlist(vnum,pg,5);
		m.addAttribute("list", list);
		if(list.size()!=0)
		{
			m.addAttribute("end",list.get(0).get(0).get("end"));
			m.addAttribute("pg",pg);
		}
		else { m.addAttribute("end",0); }
		return "thymeleaf/vendor/QAList";
	}
	
	
	@ResponseBody
	@PostMapping("/regAnswer")
	public Map<String,Object> AddAnswer(Answer answer)
	{
		int vnum =(int)session.getAttribute("vnum");
		answer.setVnum(vnum);		
		Map<String,Object> map = svc.addAnswerByCnum(answer);		
		return map;
	}
	
	@GetMapping("/showCancleList")
	public String getCancleList(Model m,@RequestParam(required =false, defaultValue="1") int pg)
	{
		int vnum =(int)session.getAttribute("vnum");	
		List<Map<String,Object>> list = svc.cancleList(vnum,pg,3);
		m.addAttribute("list",list);
		if(list.size()!=0)
		{
			m.addAttribute("end",list.get(0).get("end"));
			m.addAttribute("pg",pg);
		}
		else { m.addAttribute("end",0); }
		
		return "thymeleaf/vendor/CancleOrderList";
	}
	
	
	@ResponseBody
	@PostMapping("/appCancle")
	public Map<String,Object> regCancleApporve(@RequestParam("oinum") int oinum)
	{	
		Map<String,Object> map = svc.setConfirmByCancle(oinum);		
		return map;
	}
	
	
	
	
}
