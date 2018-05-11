package net.jeebiz.ftpclient.rename;

import java.io.File;

import net.jeebiz.ftpclient.FTPClientConfig;

public interface FileRenamePolicy {

	public abstract void setClientConfig(FTPClientConfig config);
	
	public abstract String rename(String filename);
	
	public abstract File rename(File file);
	
}



