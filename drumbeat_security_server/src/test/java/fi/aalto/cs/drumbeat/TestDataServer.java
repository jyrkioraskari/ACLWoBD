package fi.aalto.cs.drumbeat;

import static org.junit.Assert.assertEquals;

import java.util.stream.Collectors;

import org.junit.Test;

import fi.aalto.drumbeat.controllers.DataProtectionController;

public class TestDataServer {

	@Test
	public void test() {
		DataProtectionController ds=DataProtectionController.getDataServer("https://architect.local.org/protected/musiikkitalo");
		String roles = ds.autenticate("https://jyrkio2.databox.me/profile/card#me", "https://architect.local.org/protected/musiikkitalo").stream()
			     .collect(Collectors.joining(","));
		System.out.println("Roles:"+roles);
		assertEquals("READ", roles);
	}

}
