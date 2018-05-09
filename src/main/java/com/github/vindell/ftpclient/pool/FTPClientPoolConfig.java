package com.github.vindell.ftpclient.pool;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 基于apache-pool2的线程池初始化对象
 * @author 		： <a href="https://github.com/vindell">vindell</a>
 */
public class FTPClientPoolConfig extends GenericObjectPoolConfig {
	
	protected static Logger LOG = LoggerFactory.getLogger(FTPClientPoolConfig.class);
	
	public FTPClientPoolConfig(){
		
	}
	
}
