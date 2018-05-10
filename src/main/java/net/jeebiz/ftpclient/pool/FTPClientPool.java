package net.jeebiz.ftpclient.pool;

import org.apache.commons.pool2.impl.GenericObjectPool;

import net.jeebiz.ftpclient.FTPClient;

/**
 * FTPClient连接池
 * @author 		： <a href="https://github.com/vindell">vindell</a>
 */
public class FTPClientPool extends GenericObjectPool<FTPClient> {
	
	/**
	 * 初始化连接池，需要注入一个工厂来提供FTPClient实例和连接池初始化对象
	 * @param factory
	 * @param config
	 */
	public FTPClientPool(FTPPooledClientFactory factory,FTPClientPoolConfig config){
		super(factory,config);
	}
	
}