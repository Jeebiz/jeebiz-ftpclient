package net.jeebiz.ftpclient;

import org.junit.Before;

import net.jeebiz.ftpclient.client.FTPResourceClient;

public class FTPResourceClientTest extends FTPClientTest {
	
	@Before
	public void setUp() {
		builder = new FTPClientBuilder(null);
 		ftpClient = new FTPResourceClient(builder);
	}
 	
 	
 }
 