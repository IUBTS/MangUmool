package com.spring.web.vendor.service;


import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.dialect.function.ListaggGroupConcatEmulation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.spring.web.customer.vo.Cancle;
import com.spring.web.customer.vo.Customer;
import com.spring.web.customer.vo.Review;
import com.spring.web.market.vo.Answer;
import com.spring.web.market.vo.Order;
import com.spring.web.market.vo.OrderItem;
import com.spring.web.market.vo.Question;
import com.spring.web.vendor.mapper.VendorMapper;
import com.spring.web.vendor.vo.I_Attach;
import com.spring.web.vendor.vo.Items;
import com.spring.web.vendor.vo.Vendor;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
public class VendorService {

	@Autowired
	private VendorMapper mapper;
		
	private Path fileStroageLocation;

	public String addItem(Map map)
	{
		MultipartFile[] mfiles = (MultipartFile[]) map.get("mfiles");
		MultipartFile[] thumimg = (MultipartFile[]) map.get("thumimg");
		HttpServletRequest request = (HttpServletRequest)map.get("request");
		Items items = (Items)map.get("items");
		
		String iname = items.getName();
		
		List<I_Attach> list = new ArrayList<>();		
		int itemsRes = mapper.additem(items);
							
		try 
		{		
			this.fileStroageLocation=Paths.get("./src/main/resources/static/"+iname).toAbsolutePath().normalize();
			Files.createDirectories(this.fileStroageLocation);				
			
			if(mfiles[0].getSize()!=0)
			{
				for(int i = 0 ; i<mfiles.length ; i++)
				{						
					I_Attach att = new I_Attach();
					String fname = mfiles[i].getOriginalFilename();		
					att.setFname(fname);		
					
					list.add(att);												
									
					Path targetLocation = this.fileStroageLocation.resolve(fname);
					Files.copy(mfiles[i].getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
				}	
			}
			if(thumimg[0].getSize()!=0)
			{
				for(int i=0;i<thumimg.length;i++) {
					 I_Attach att = new I_Attach();
					 String fname = "thumnail_"+thumimg[i].getOriginalFilename();
					 att.setFname(fname);
					 
					 list.add(att);
					 
					 Path targetLocation = this.fileStroageLocation.resolve(fname);
		             Files.copy(thumimg[i].getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
				}
			}
			
			int res = mapper.addimage(list);
			return res>0 ? "파일첨부 성공" : "파일 첨부 실패" ;				
		}
		catch (IOException e) 
		{					
			e.printStackTrace();
		}	
		return "이미지 첨부 실패";		
	}	
	
	
	
	public Map<String,Object> getList(int vnum,int pg,int row)
	{
		PageHelper.startPage(pg, row);
		PageInfo<Items> pageinfo = new PageInfo<>(mapper.itemlist(vnum));
		List<Items> list = pageinfo.getList();
		
		Map<String,Object> map = new HashMap<>();
		map.put("list", list);
		map.put("begin", pageinfo.getNavigateFirstPage());
		map.put("end", pageinfo.getNavigateLastPage());
		
		return map;
	}
	
	
	
	public Map<String,Object> getDetail(int itemcode,int vnum)
	{
		Items ditem = new Items();
		ditem.setItemcode(itemcode);
		ditem.setVnum(vnum);
		
		List<Map<String,Object>> list = mapper.getDetail(ditem);
		Map<String,Object> map = new HashMap<>();		
		
		Map<String,Object> mitem = list.get(0);
		Items item = new Items();
		
		BigDecimal alcohol = (java.math.BigDecimal)mitem.get("ALCOHOL");
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
		item.setVnum(vnum);
		item.setItemcode(itemcode);
		item.setRegdate(regdate);
		
		String fname = (String)mitem.get("FNAME");
		Optional<Object> optional = Optional.ofNullable(mitem.get("RNUM"));		
		float sumScore=0;		
		if(optional.isPresent())
		{				
			for(int i = 0 ; i <list.size();i++)
			{
				Map<String,Object> m = list.get(i);
				Review rev = new Review();
				BigDecimal rnum = (java.math.BigDecimal)m.get("RNUM");
				BigDecimal score = (java.math.BigDecimal)m.get("SCORE");
				rev.setRnum(rnum.intValue());
				rev.setScore(score.intValue());
				
				if(!(item.getRevList().contains(rev))) item.getRevList().add(rev);
				
				sumScore += score.intValue();				
			}

			float bfavg = sumScore/list.size();
			float avg = Math.round(bfavg*100)/100.0f;	// 평균을 소수점 첫째짜리 까지만 표현
			map.put("avg", avg);
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
		I_Attach thumatt = new I_Attach();
		map.put("thumatt", thumatt);
		
		for(int i =0 ; i<list.size() ; i++)
		{
			Map<String,Object> m = list.get(i);
			I_Attach att = new I_Attach();
			
			BigDecimal ianum = (java.math.BigDecimal)m.get("IANUM");
			String ifname = (String)m.get("FNAME");
			
			if(ifname.startsWith("thumnail_"))
			{
				att.setFname(ifname);
				att.setItemcode(itemcode);
				att.setIanum(ianum.intValue());			
				map.put("thumatt", att);		
				
				continue;
			}			
			
			att.setItemcode(itemcode);
			att.setFname(ifname);
			att.setIanum(ianum.intValue());
			
			if(!(item.getImgList().contains(att))) item.getImgList().add(att);
			
		}	
		map.put("item", item);
		
		return map;
	}
	
	public Map<String,Object> delimg(I_Attach ia, String name)
	{
		String fname = ia.getFname();
		Map<String,Object> map = new HashMap<>();		
		Path filePath=Paths.get("./src/main/resources/static/"+name+"/"+fname);
		
		try 
		{			
			Files.deleteIfExists(filePath);
			int res = mapper.deleteImg(ia);
			if(res>0)
			{
				map.put("deleted", true);			
			}
			else
			{
				map.put("deleted", false);
				map.put("msg", "삭제 실패");
			}	
			return map;	
			
		} 
		catch (IOException e) {
			
		}
		
		return map;
		
	}
	
	
	@Transactional
	public Map<String,Object> addImgfiles(Map map)
	{
		MultipartFile[] mfiles = (MultipartFile[]) map.get("mfiles");
		MultipartFile[] thumimg = (MultipartFile[]) map.get("thumimg");
		HttpServletRequest request = (HttpServletRequest)map.get("request");
		int itemcode = (int)map.get("itemcode");
		String name = (String)map.get("name");
		
		List<I_Attach> list = new ArrayList<>();		
		Map<String,Object> resmap = new HashMap<>();
		try 
		{		
			this.fileStroageLocation=Paths.get("./src/main/resources/static/"+name).toAbsolutePath().normalize();
			Files.createDirectories(this.fileStroageLocation);				
			
			if(mfiles[0].getSize()!=0)
			{
				for(int i = 0 ; i<mfiles.length ; i++)
				{						
					I_Attach att = new I_Attach();
					String fname = mfiles[i].getOriginalFilename();		
					att.setFname(fname);		
					att.setItemcode(itemcode);
					list.add(att);												
									
					Path targetLocation = this.fileStroageLocation.resolve(fname);
					Files.copy(mfiles[i].getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
				}	
			}
			if(thumimg[0].getSize()!=0)
			{
				for(int i=0;i<thumimg.length;i++) {
					 I_Attach att = new I_Attach();
					 String fname = "thumnail_"+thumimg[i].getOriginalFilename();
					 att.setFname(fname);
					 att.setItemcode(itemcode);
					 list.add(att);
					 
					 Path targetLocation = this.fileStroageLocation.resolve(fname);
		             Files.copy(thumimg[i].getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
				}
			}
			
			int res = mapper.addEditImage(list);
			int res1 = mapper.itemPostWait(itemcode);
			
			if(res>0&&res1>0)
			{
				resmap.put("added", true);
				resmap.put("msg", "파일첨부 성공");
			}
				
			else
			{
				resmap.put("added", false);
				resmap.put("msg", "파일첨부 실패");
			}
			
			return resmap ;				
		}
		catch (IOException e) 
		{					
			e.printStackTrace();
		}	
		resmap.put("added", false);
		resmap.put("msg", "파일첨부 실패");
		return resmap;		
	}	
	
	
	public Map<String,Object> editIteminfo(Items items)
	{
		Map<String,Object> map = new HashMap<>();
		int res = mapper.itemUpdate(items);
		
		if(res>0)
		{
			map.put("msg", "수정성공");
			map.put("updated", true);
		}
		else
		{
			map.put("msg", "수정실패");
			map.put("updated", false);
		}	
		return map;		
	}
	
	
	public List<Map<String,Object>> getNewOrder(int vnum,int pg, int row)
	{
		List<Map<String,Object>> list = new ArrayList<>();
		
		PageHelper.startPage(pg, row);
		PageInfo<Map<String,Object>> pageinfo = new PageInfo<>(mapper.requestOrder(vnum));
		List<Map<String,Object>> olist = pageinfo.getList();			
		
		for(int i = 0 ; i < olist.size() ; i ++)
		{
			Map<String,Object> o = olist.get(i);
			Map<String,Object> map = new HashMap<>();
			if(i==0)
			{
				map.put("end", pageinfo.getNavigateLastPage());
			}
			BigDecimal onum = (java.math.BigDecimal)o.get("ONUM");
			BigDecimal cnum = (java.math.BigDecimal)o.get("CNUM");
			BigDecimal price = (java.math.BigDecimal)o.get("PRICE");
			
			String tophone = (String)o.get("TOPHONE");
			String fromphone = (String)o.get("FROMPHONE");
			String toaddress = (String)o.get("TOADDRESS");
			String toname = (String)o.get("TONAME");
			String fromaddress = (String)o.get("FROMADDRESS");
			String fromname = (String)o.get("FROMNAME");
			String requestmemo = (String)o.get("REQUESTMEMO");
			
			Timestamp regorderdate = (java.sql.Timestamp) o.get("ORDERDATE");
			Date orderdate = new Date(regorderdate.getTime());
			
			Order order = new Order();
			
			order.setOnum(onum.intValue());
			order.setCnum(cnum.intValue());
			order.setTophone(tophone);
			order.setToname(toname);
			order.setToaddress(toaddress);
			order.setFromphone(fromphone);
			order.setFromname(fromname);
			order.setFromaddress(fromaddress);
			order.setRequestmemo(requestmemo);
			order.setOrderdate(orderdate);
					
			BigDecimal oinum = (java.math.BigDecimal)o.get("OINUM");
			BigDecimal amount = (java.math.BigDecimal)o.get("AMOUNT");
			BigDecimal itemcode = (java.math.BigDecimal)o.get("ITEMCODE");
			BigDecimal confirm = (java.math.BigDecimal)o.get("CONFIRM");
			BigDecimal deliverycode = (java.math.BigDecimal)o.get("DELIVERYCODE");
			
			String name = (String)o.get("NAME");
			String delicomp = (String)o.get("DELICOMP");
			OrderItem oitem = new OrderItem();
			
			oitem.setOinum(oinum.intValue());
			oitem.setItemcode(itemcode.intValue());
			oitem.setAmount(amount.intValue());
			oitem.setName(name);
			oitem.setConfirm(confirm.intValue());
			oitem.setDeliverycode(deliverycode.longValue());
			oitem.setDelicomp(delicomp);
						
			map.put("order", order);
			map.put("oitem", oitem);
			
			list.add(map);
		}
		return list;
	}
	
	@Transactional
	public Map<String,Object> writeDelicode(OrderItem orderitem)
	{
		int res = mapper.setDelicode(orderitem);
		int res1 = mapper.subQuantity(orderitem);
		Map<String,Object> map = new HashMap<>();		
		if(res>0&&res1>0)
		{
			map.put("update", true);
			map.put("msg", "송장번호 등록 성공");
		}
		else
		{
			map.put("update", false);
			map.put("msg", "송장번호 등록 실패");
		}		
		return map;
	}
	
	public void downOrderList(int vnum, HttpServletRequest request, HttpServletResponse response)
	{
		List<Map<String,Object>> list = mapper.downExcel(vnum);
		
		XSSFWorkbook workbook = new XSSFWorkbook();		
		XSSFSheet sheet = workbook.createSheet("order");		
		
		response.setHeader("Set-Cookie", "fileDownload=true; path=/");
		response.setHeader("Content-Disposition", String.format("attachment; filename=\"orderList.xlsx\""));
		
		sheet.setColumnWidth((short) 0, (short) 2500);
	    sheet.setColumnWidth((short) 1, (short) 4000);
	    sheet.setColumnWidth((short) 2, (short) 3200);
	    sheet.setColumnWidth((short) 3, (short) 3200);
	    sheet.setColumnWidth((short) 4, (short) 4000);
	    sheet.setColumnWidth((short) 5, (short) 3200);
	    sheet.setColumnWidth((short) 6, (short) 3200);
	    sheet.setColumnWidth((short) 7, (short) 4000);
	    sheet.setColumnWidth((short) 8, (short) 3300);
	    sheet.setColumnWidth((short) 9, (short) 7000);
	    sheet.setColumnWidth((short) 10, (short) 4000);
	
	    Row row = sheet.createRow(0);
		Cell cell = null;
	    
	    sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 10));

		cell = row.createCell(0);
		cell.setCellValue("미배송 주문건");
		
		row = sheet.createRow(1);
		cell = null;
		
		cell = row.createCell(0);
		cell.setCellValue("주문번호");
		 
		cell = row.createCell(1);
		cell.setCellValue("받는분 전화번호");	
		  
		cell = row.createCell(2);
		cell.setCellValue("받는분 주소");
		  
		cell = row.createCell(3);
		cell.setCellValue("받는분");
		  
		cell = row.createCell(4);
		cell.setCellValue("보내는 분 전화번호");		
		  
		cell = row.createCell(5);
		cell.setCellValue("보내는 분");	
		  
		cell = row.createCell(6);
		cell.setCellValue("보내는 분 주소");
		
		cell = row.createCell(7);
		cell.setCellValue("수량");
		
		cell = row.createCell(8);
		cell.setCellValue("품목");
		
		cell = row.createCell(9);
		cell.setCellValue("배송메모");
		
		cell = row.createCell(10);
		cell.setCellValue("주문날짜");
		
	
		for(int i=0; i <list.size();i++)
		{
			Map<String,Object> o = list.get(i);

			String tophone = (String)o.get("TOPHONE");
			String fromphone = (String)o.get("FROMPHONE");	
			String toaddress = (String)o.get("TOADDRESS");
			String toname = (String)o.get("TONAME");
			String fromaddress = (String)o.get("FROMADDRESS");
			String fromname = (String)o.get("FROMNAME");
			String requestmemo = (String)o.get("REQUESTMEMO");			
			
			Timestamp regorderdate = (java.sql.Timestamp) o.get("ORDERDATE");
			Date orderdate = new Date(regorderdate.getTime());
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String cretDate = sdf.format(orderdate);
			
			BigDecimal oinum = (java.math.BigDecimal)o.get("OINUM");
			BigDecimal amount = (java.math.BigDecimal)o.get("AMOUNT");		
			String name = (String)o.get("NAME");
			
		    row = sheet.createRow(i+2);
		    cell = null;
		 
		    cell = row.createCell(0);
		    cell.setCellValue(oinum.intValue());
		  
		    cell = row.createCell(1);
		    cell.setCellValue(tophone);
		  
		    cell = row.createCell(2);
		    cell.setCellValue(toaddress);
		  
		    cell = row.createCell(3);
		    cell.setCellValue(toname);
		  
		    cell = row.createCell(4);
		    cell.setCellValue(fromphone);
		  
		    cell = row.createCell(5);
		    cell.setCellValue(fromname);
		  
		    cell = row.createCell(6);
		    cell.setCellValue(fromaddress);
		    
		    cell = row.createCell(7);
		    cell.setCellValue(amount.intValue());
		    
		    cell = row.createCell(8);
		    cell.setCellValue(name);
		    
		    cell = row.createCell(9);
		    cell.setCellValue(requestmemo);
		    
		    cell = row.createCell(10);
		    cell.setCellValue(cretDate);			
		}		  
		try {
		workbook.write(response.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Map<String,Object> completeDeli(int oinum)
	{
		int res = mapper.completeDelivery(oinum);
		Map<String,Object> map = new HashMap<>();
		
		if(res>0)
		{
			map.put("updated", true);
			map.put("msg", "배송완료 등록 설정을 마쳤습니다");
		}
		else
		{
			map.put("updated", false);
			map.put("msg", "배송완료 등록 실패");
		}		
		return map;		
	}
	
	
	public List<List<Map<String,Object>>> getQAlist(int vnum,int pg, int row)
	{
		List<List<Map<String,Object>>> list = new ArrayList<>();		
		PageHelper.startPage(pg, row);
		PageInfo<Map<String,Object>> pageinfo = new PageInfo<>(mapper.showQuestion(vnum));
		List<Map<String,Object>> qlist = pageinfo.getList();		
		
		List<Map<String,Object>> itemqalist = new ArrayList<>();
		
		for(int i =0; i<qlist.size(); i++)
		{
			Map<String,Object> q = qlist.get(i);
			Map<String,Object> map = new HashMap<>();		
			if(i==0)
			{
				map.put("end", pageinfo.getNavigateLastPage());
			}			
			BigDecimal itemcode = (java.math.BigDecimal)q.get("ITEMCODE");
			map.put("name", (String)q.get("NAME"));
			if(itemqalist.size()!=0)
			{
				if((itemcode.intValue())!=((Question)itemqalist.get(0).get("question")).getItemcode())
				{
					list.add(itemqalist);
					itemqalist = new ArrayList<>();
				}
			}		
			BigDecimal qnum = (java.math.BigDecimal)q.get("QNUM");
			BigDecimal cnum = (java.math.BigDecimal)q.get("CNUM");
			
			String qcontent = (String)q.get("QCONTENT");
			
			Timestamp questionRegdate = (java.sql.Timestamp) q.get("QREGDATE");
			Date qregdate = new Date(questionRegdate.getTime());
			
			Question question = new Question();
			question.setQnum(qnum.intValue());
			question.setCnum(cnum.intValue());
			question.setItemcode(itemcode.intValue());
			question.setRegDate(qregdate);
			question.setContent(qcontent);
			
			if(q.get("ANNUM")!=null)
			{
				BigDecimal annum = (java.math.BigDecimal)q.get("ANNUM");
				String Acontent = (String)q.get("ACONTENT");
				
				Timestamp answerRegdate = (java.sql.Timestamp) q.get("AREGDATE");
				Date aregdate = new Date(answerRegdate.getTime());
				
				Answer answer = new Answer();
				answer.setAnnum(annum.intValue());
				answer.setContent(Acontent);
				answer.setRegDate(aregdate);
				answer.setVnum(vnum);
				
				map.put("answer", answer);					
			}
			else
			{
				map.put("answer", false);
			}
			map.put("question", question);
			itemqalist.add(map);
			
			if(i==qlist.size()-1)
			{	
				list.add(itemqalist);
			}							
		}				
		return list;				
	}
	
	
	public Map<String,Object> addAnswerByCnum(Answer answer)
	{
		int res = mapper.regAnswerByQnum(answer);
		Map<String,Object> map = new HashMap<>();
		
		if(res>0)
		{
			map.put("reg", true);
			map.put("msg", "해당 질문의 답변이 등록되었습니다.");
		}
		else
		{
			map.put("reg", false);
			map.put("msg", "답변등록이 실패되었습니다.");
		}		
		return map;
	}
	
	public List<Map<String,Object>> cancleList(int vnum, int pg, int row)
	{
		List<Map<String,Object>> list = new ArrayList<>();
		
		PageHelper.startPage(pg, row);
		PageInfo<Map<String,Object>> pageinfo = new PageInfo<>(mapper.getCancleRequest(vnum));
		List<Map<String,Object>> clist = pageinfo.getList();	
		
		if(clist.size()>0)
		{
			for(int i =0 ; i<clist.size();i++)
			{
				Map<String,Object> c = clist.get(i);
				Map<String,Object> map = new HashMap<>();
				if(i==0)
				{
					map.put("end", pageinfo.getNavigateLastPage());
				}				
				BigDecimal cannum = (java.math.BigDecimal)c.get("CANNUM");
				BigDecimal onum = (java.math.BigDecimal)c.get("ONUM");
				BigDecimal oinum = (java.math.BigDecimal)c.get("OINUM");
				BigDecimal cnum = (java.math.BigDecimal)c.get("CNUM");
				BigDecimal itemcode = (java.math.BigDecimal)c.get("ITEMCODE");
				String reason = (String)c.get("REASON");
				
				Timestamp ancleRegdate = (java.sql.Timestamp) c.get("REGDATE");
				Date regdate = new Date(ancleRegdate.getTime());
				
				
				Cancle cancle = new Cancle();
				
				cancle.setCannum(cannum.intValue());
				cancle.setCnum(cnum.intValue());
				cancle.setItemcode(itemcode.intValue());
				cancle.setOinum(oinum.intValue());
				cancle.setOnum(onum.intValue());
				cancle.setReason(reason);
				cancle.setRegDate(regdate);
				
				BigDecimal price = (java.math.BigDecimal)c.get("PRICE");
				BigDecimal amount = (java.math.BigDecimal)c.get("AMOUNT");
				BigDecimal confirm = (java.math.BigDecimal)c.get("CONFIRM");
				String name = (String)c.get("NAME");
				
				OrderItem oi = new OrderItem();
				oi.setPrice(price.intValue());
				oi.setAmount(amount.intValue());
				oi.setConfirm(confirm.intValue());
				oi.setName(name);			
				
				String phone = (String)c.get("PHONE");
				String nickname = (String)c.get("NICKNAME");
				String email = (String)c.get("EMAIL");
				
				Customer cus = new Customer();
				cus.setNickname(nickname);
				cus.setEmail(email);
				cus.setPhone(phone);
				
				Timestamp orderdated = (java.sql.Timestamp) c.get("ORDERDATE");
				Date orderdate = new Date(orderdated.getTime());
				
				map.put("customer", cus);
				map.put("orderItem", oi);
				map.put("cancle", cancle);
				map.put("orderdate", orderdate);
				
				list.add(map);
			}			
		}	
		return list;
	}
	
	
	public Map<String,Object> setConfirmByCancle(int oinum)
	{
		int res = mapper.approveCancleOrder(oinum);
		 Map<String,Object> map = new HashMap<>();
		
		 if(res>0)
		 {
			 map.put("suc", true);
			 map.put("msg", "취소신청을 승인하였습니다.");
		 }
		 else
		 {
			 map.put("suc", false);
			 map.put("msg", "취소신청을 승인에 실패하였습니다.");
		 }
		 		 
		return map;
	}
	
	
	
	
	
	
	
	
}
