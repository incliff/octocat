package com.example.demo.utils;

import java.util.Random;

public class AuthCodeUtils {
	
	public static String genCode(int len){
		 String code="";
		 for (int i = 0; i < len; i++) {
		     int rand=  new Random().nextInt(10);
		     code+=rand;
		 }
		 
		return code;
	}

}
