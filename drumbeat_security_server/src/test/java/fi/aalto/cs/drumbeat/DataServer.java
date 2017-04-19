package fi.aalto.cs.drumbeat;

import static org.junit.Assert.assertEquals;

import java.util.stream.Collectors;

import org.junit.Test;

import fi.aalto.cs.drumbeat.controllers.AccessController;

public class DataServer {

	@Test
	public void test() {
		AccessController ds=AccessController.getAuthenticationController("https://architect.local.org/protected/musiikkitalo");
		String roles = ds.grantPermissions("https://jyrkio2.databox.me/profile/card#me", "https://architect.local.org/protected/musiikkitalo").stream()
			     .collect(Collectors.joining(","));
		System.out.println("Roles:"+roles);
		// Only after the first installation
		assertEquals("READ", roles);
	}

}
