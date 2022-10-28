package com.example.myapp;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.boot.SpringApplication;

@RunWith(PowerMockRunner.class)
@PrepareForTest({SpringApplication.class})
public class MyappApplicationTests {

	@Test
	public void mainRunSpringApplicationIfCommon() {
		PowerMockito.mockStatic(SpringApplication.class);

		String [] args = new String[0];
		MyappApplication.main(args);

		PowerMockito.verifyStatic();
		SpringApplication.run(MyappApplication.class, args);
	}

}
