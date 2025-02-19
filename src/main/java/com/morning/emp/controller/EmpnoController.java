package com.morning.emp.controller;

import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotEmpty;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;


import com.morning.emp.model.EmpService;
import com.morning.emp.model.EmpVO;

@Controller
@Validated
@RequestMapping("/emp")
public class EmpnoController {

	@Autowired
	EmpService empSvc;
	
	
	//接收getOne_For_Display
	@PostMapping("getOne_For_Display")
	public String getOne_For_Display(
			/***************************1.接收請求參數 - 輸入格式的錯誤處理*************************/
			@NotEmpty(message="編號:請勿空白")
			@Digits(integer = 3, fraction = 0,message ="員工編號: 請填數字-請勿超過{integer}位數")
			@RequestParam("empid") String empid,
			ModelMap model) {
		
		
		/***************************2.開始查詢資料*********************************************/
//		EmpService empSvc = new EmpService();
//		上面已經注入
		EmpVO empVO = empSvc.getOneEmp(Integer.valueOf(empid));
		
		List<EmpVO> list =empSvc.getAll();
		model.addAttribute("empListData", list);//前面變數名稱後面屬性名稱
		
		if(empVO ==null) {
			model.addAttribute("errorMessage", "查無資料");
			return "back-end/emp/select_page";
		}
		/***************************3.查詢完成,準備轉交(Send the Success view)*****************/
		
		model.addAttribute("empVO", empVO);
		model.addAttribute("functions", empVO.getFunctions());
//		這段是 如果我要在頁面上顯示片段才需要
		model.addAttribute("getOne_For_Display", "true");
//		然後配上這行
		return"back-end/emp/select_page";
//		return "back-end/emp/listOneEmp";  //查詢完成後轉交listOneEmp
		
	}
	
	@ExceptionHandler(value = { ConstraintViolationException.class })
	//@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	public ModelAndView handleError(HttpServletRequest req,ConstraintViolationException e,Model model) {
	    Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
	    StringBuilder strBuilder = new StringBuilder();
	    for (ConstraintViolation<?> violation : violations ) {
	          strBuilder.append(violation.getMessage() + "<br>");
	    }
	    //==== 以下第92~96行是當前面第77行返回 /src/main/resources/templates/back-end/emp/select_page.html用的 ====   
//	    model.addAttribute("empVO", new EmpVO());
//    	EmpService empSvc = new EmpService();
		List<EmpVO> list = empSvc.getAll();
		model.addAttribute("empListData", list);     // for select_page.html 第97 109行用
//		model.addAttribute("deptVO", new DeptVO());  // for select_page.html 第133行用
//		List<DeptVO> list2 = deptSvc.getAll();
//    	model.addAttribute("deptListData",list2);    // for select_page.html 第135行用
		String message = strBuilder.toString();
	    return new ModelAndView("back-end/emp/select_page", "errorMessage", "請修正以下錯誤:<br>"+message);
	}
}
