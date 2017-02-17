package fi.aalto.drumbeat;

import fi.aalto.drumbeat.Internet;
import fi.aalto.drumbeat.concepts.DataStore;
import fi.aalto.drumbeat.security.concepts.Organization;
import fi.aalto.drumbeat.webid.WebIDCertificate;

public class Client {
	
	public Client()
	{
		try {
			Organization o = (Organization) Internet.get("http://company1/");
			WebIDCertificate wc=o.getWebID("mats");
			System.out.println("wc uri on:"+wc.getWebid_uri().toString());
			DataStore ds1=new DataStore("https://architectural.drb.cs.hut.fi/security/");
			if(ds1.connect(wc,"http://architectural.drb.cs.hut.fi/drumbeat/objects/smc2/architectural/3A248E14-4504-4891-902B-5E9216C64AB9")==true)
			  System.out.println("Connected...");	
				
		} catch (Exception e) {
			System.out.println("Result:" +Internet.get("http://company1/"));
			e.printStackTrace();
		}
	}
	

	public static void main(String[] args) {
		new Client();

	}
}