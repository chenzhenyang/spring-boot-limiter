package com.fengxin58.limiter.web;

import java.util.Map;

public class MapUtils {
	
	public static String toString(Map<?,?> map){
		StringBuilder sb = new StringBuilder();
		map.forEach((key,value)->{
			sb.append(key);
			sb.append(" = ");
			sb.append(value);
			sb.append("\n");
		});
		return sb.toString();
	}
	
}
