package com.example.esnalytics.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.MOCK, classes=com.example.esanalytics.EsAnalyticsApplication.class)
public class JavaAnalyticsApplicationTests {

	@Test
	public void contextLoads() {
	}

}
