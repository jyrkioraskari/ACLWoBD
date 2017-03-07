package fi.aalto.drumbeat;

import java.util.stream.Collectors;

import org.junit.Test;

public class DataServerTests {

	@Test
	public void test() {
		DataServer ds=DataServer.getDataServer("https://architect.local.org/protected/data/architectural");
		String roles = ds.connect("https://jyrkio2.databox.me/profile/card#me", "https://architect.local.org/protected/data/hello").stream()
			     .collect(Collectors.joining(","));
		System.out.println("Roles:"+roles);
	}

}
