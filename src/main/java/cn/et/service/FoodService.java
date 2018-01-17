package cn.et.service;

import java.util.List;
import java.util.Map;

public interface FoodService {
	/**
	 * 获取数据库数据
	 * @return
	 */
	public List<Map<String,Object>> getFood();
	
	/**
	 * 创建分词库（将数据库的数据写入）
	 *
	 */
	public void write();
	
	/**
	 * 搜索
	 */
	public List<String> search(String foodname);
}
