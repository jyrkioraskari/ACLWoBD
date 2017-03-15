package fi.aalto.cs.drumbeat;

import static org.junit.Assert.assertEquals;

import java.util.stream.Collectors;

import org.junit.Test;

import fi.aalto.cs.drumbeat.controllers.AuthenticationController;

public class DataServer {

	@Test
	public void test() {
		AuthenticationController ds=AuthenticationController.getAuthenticationController("https://architect.local.org/protected/musiikkitalo");
		String roles = ds.autenticate("https://jyrkio2.databox.me/profile/card#me", "https://architect.local.org/protected/musiikkitalo").stream()
			     .collect(Collectors.joining(","));
		System.out.println("Roles:"+roles);
		// Only after the first installation
		assertEquals("READ", roles);
	}

}
