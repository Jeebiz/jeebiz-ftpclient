package com.github.vindell.ftpclient.utils;


import com.github.vindell.ftpclient.FTPClient;
import com.github.vindell.ftpclient.io.CopyStreamProcessListener;

public class FTPCopyListenerUtils {

	public static void initCopyListener(FTPClient ftpClient,String filename){
		//进度监听
		CopyStreamProcessListener listener = ftpClient.getCopyStreamProcessListener();
		//判断监听存在
		if(listener != null){
	    	listener.setFileName(filename);
	    }
	}
	
}
