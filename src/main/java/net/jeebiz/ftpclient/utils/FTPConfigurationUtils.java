package net.jeebiz.ftpclient.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.jeebiz.ftpclient.FTPClientConfig;
import net.jeebiz.ftpclient.enums.FTPServerTypeEnum;

public class FTPConfigurationUtils {

	protected static Logger LOG = LoggerFactory.getLogger(FTPConfigurationUtils.class);
	/**
	 * Windows 系统路径分割符号 \
	 */
	protected static String SLASHES = "\\";
	/**
	 * Linux，Unix 系统路径分割符号 /
	 */
	protected static String BACKSLASHES = "/";

	public static String getFileSeparator(FTPClientConfig configuration) {
		// 服务端系统类型：unix,unix-trim,vms,nt,os2,os400,as400,mvs,l8,netware,macos;默认 unix
		if (FTPServerTypeEnum.NT.equals(configuration.getServerType())) {
			return SLASHES;
		} else {
			return BACKSLASHES;
		}
	}
	
}
