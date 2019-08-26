package com.example.client.lowLevelRestClient;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.MOCK, classes=com.example.client.restclient.JavaRestClientApplication.class)
public class JavaLowerLevelClientApplicationTests {

	@Test
	public void contextLoads() {
	}

}
