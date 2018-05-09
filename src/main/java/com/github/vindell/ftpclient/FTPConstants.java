/*
 * Copyright (c) 2010-2020, wandalong (hnxyhcwdl1003@163.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.github.vindell.ftpclient;

public final class FTPConstants {

	//获取文件存储路径
	public static String DEFAULT_FTPCLIEN_WEB_LOCALDIR = "tmpdir";
	//获取请求过滤前缀
	public static String DEFAULT_FTPCLIEN_WEB_REQUESTPREFIX = "/ftp/";
	//获取是否缓存FTP文件到本地存储路径
	public static boolean DEFAULT_FTPCLIEN_WEB_CACHELOCAL = false;
	//获取共享文件在本地缓存的时间;默认10分钟
	public static long DEFAULT_FTPCLIEN_WEB_CACHEEXPIRY = 10 * 60 * 1000;
	
	//获取文件存储路径
	public static String FTPCLIEN_WEB_LOCALDIR_KEY = "ftpclient.web.tmpdir";
	//获取请求过滤前缀
	public static String FTPCLIEN_WEB_REQUESTPREFIX_KEY = "ftpclient.web.requestPrefix";
	//获取是否缓存FTP文件到本地存储路径
	public static String FTPCLIEN_WEB_CACHELOCAL_KEY = "ftpclient.web.cacheLocal";
	//获取FTP文件在本地缓存的时间;默认10分钟
	public static String FTPCLIEN_WEB_CACHEEXPIRY_KEY = "ftpclient.web.cacheExpiry";
	//异常信息重定向路径
	public static String FTPCLIEN_WEB_REDIRECTURL_KEY = "ftpclient.web.redirectURL";
	
}
