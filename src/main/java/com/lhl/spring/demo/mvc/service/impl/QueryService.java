package com.lhl.spring.demo.mvc.service.impl;

import com.lhl.spring.annotation.MyService;
import com.lhl.spring.demo.mvc.service.IQueryService;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 查询业务
 * @author Tom
 *
 */
@MyService
public class QueryService implements IQueryService {

	/**
	 * 查询
	 */
	public String query(String name) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time = sdf.format(new Date());
		String json = "{name:\"" + name + "\",time:\"" + time + "\"}";
		System.out.println("Query Service query() ====" + json);
		return json;
	}

}
