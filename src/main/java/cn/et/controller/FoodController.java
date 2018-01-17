package cn.et.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.et.service.FoodService;

@RestController
public class FoodController {
	@Autowired
	FoodService service;

	@RequestMapping("/food")
	public List<String> search(String foodname){	
		return service.search(foodname);
	}
	
	@RequestMapping("/loadData")
	public void loading(){
		service.write();
	}
}
