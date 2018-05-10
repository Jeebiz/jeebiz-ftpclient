package net.jeebiz.ftpclient.io;

import org.apache.commons.net.io.CopyStreamEvent;
import org.apache.commons.net.io.CopyStreamListener;

/**
 * 数据处理进度监听抽象实现，可继承该对象进行进度检测
 * @author 		： <a href="https://github.com/vindell">vindell</a>
 */
public abstract class CopyStreamProcessListener implements CopyStreamListener {
	
	// 文件名称
	protected String fileName;
	
	public CopyStreamProcessListener(){
		
	}
	 
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	@Override
	public void bytesTransferred(CopyStreamEvent event) {
		 bytesTransferred(event.getTotalBytesTransferred(), event.getBytesTransferred(), event.getStreamSize());
	}
	
}
