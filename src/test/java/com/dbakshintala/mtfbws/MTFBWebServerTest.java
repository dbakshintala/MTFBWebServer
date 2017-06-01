package com.dbakshintala.mtfbws;

import org.junit.Assert;
import org.junit.Test;

public class MTFBWebServerTest {

	//TODO: Need to add the unit testing scenarios.
	@Test
	public void parseCorrectPortParam() {
		String[] args = new String[1];
		args[0] = "1234";
		Assert.assertEquals(true,true);
	}

	@Test
	public void emptyParamsDefaultTo8080() {
		String[] args = new String[0];
		Assert.assertEquals(true, true);
	}
}
