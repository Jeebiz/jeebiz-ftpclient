package com.github.vindell.ftpclient;

import org.junit.Before;

import com.github.vindell.ftpclient.client.FTPPooledResourceClient;
import com.github.vindell.ftpclient.pool.FTPClientPool;
import com.github.vindell.ftpclient.pool.FTPClientPoolConfig;
import com.github.vindell.ftpclient.pool.FTPPooledClientFactory;

public class FTPPooledResourceClientTest extends FTPClientTest {
	
	@Before
	public void setUp() {
		

		FTPClientConfig clientConfig = new FTPClientConfig();
		
		builder = new FTPClientBuilder(clientConfig);
		
		FTPPooledClientFactory factory = new FTPPooledClientFactory(builder);
		FTPClientPoolConfig config = new FTPClientPoolConfig();
		
		FTPClientPool clientPool = new FTPClientPool(factory, config);
		
 		ftpClient = new FTPPooledResourceClient(clientPool, clientConfig);
	}
	 
 }
 