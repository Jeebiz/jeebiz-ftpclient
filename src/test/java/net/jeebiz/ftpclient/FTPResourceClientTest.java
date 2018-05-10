package net.jeebiz.ftpclient;

import org.junit.Before;

import net.jeebiz.ftpclient.client.FTPResourceClient;

public class FTPResourceClientTest extends FTPClientTest {
	
	@Before
	public void setUp() {
		
		
		FTPClientConfig clientConfig = new FTPClientConfig();
		
		
		
		
		
		builder = new FTPClientBuilder(clientConfig);
 		ftpClient = new FTPResourceClient(builder);
	}
 	
 	
 }
 