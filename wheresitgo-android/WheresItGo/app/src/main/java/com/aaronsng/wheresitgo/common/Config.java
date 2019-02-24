/* Copyright (C) - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Source code Written by Eric Lee Zheng Xian <eric@Lzxe.net>, Written by YngTzer Tan <tanyngtzer@gmail.com>
 * Designed by Lai Sen Hui <mrlai999@gmail.com>
 * December 2013 
 */package com.aaronsng.wheresitgo.common;

public class Config {

	public static final int ActivityLogID = 1050;
	public static final int VolleyID = 2525;
	//public static final String url = "http://10.27.186.198:8080/wheres-it-go/";
	//public static final String url = "http://10.27.97.112/wheres-it-go/";
	public static final String url = "http://192.168.1.69:8080/wheres-it-go/";
//	public static final String url = "http://10.27.186.198:8080/wheres-it-go/";
	//public static final String url = "http://localhost:8080/wheresitgo/";
	public static String log_id = "WheresItGo";
	public enum RequestStatusCode {
		SUCCESS,FAILURE, AUTH_FAILED, GENERAL_ERROR
	}
	public enum HC_AccessType {
	}


}




