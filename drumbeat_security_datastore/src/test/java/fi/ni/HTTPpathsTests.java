package fi.ni;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.client.utils.URIBuilder;
import org.junit.Test;

import junit.framework.TestCase;

public class HTTPpathsTests extends TestCase {

	public URI kanonisointi(String uri_txt ) throws URISyntaxException
	{
		URI uri=new URI(uri_txt);
		String path=uri.getPath();
		path=path.replaceFirst("/drumbeat/objects", "/security");
		path=path.replaceFirst("/drumbeat/collections", "/security");
		path=path.replaceFirst("/drumbeat/datasources", "/security");
		path=path.replaceFirst("/drumbeat/datasets", "/security");
		return new URIBuilder().setScheme("https").setHost(uri.getHost()).setPath(path).build();
	}
	
	
	@Test
	public void test_URLCanonization() {
		 try {
			System.out.println(kanonisointi("http://architectural.drb.cs.hut.fi/drumbeat/objects/smc2/architectural/3A248E14-4504-4891-902B-5E9216C64AB9")) ;
			System.out.println(kanonisointi("http://architectural.drb.cs.hut.fi/drumbeat/collections/smc-arc"));
			System.out.println(kanonisointi("http://architectural.drb.cs.hut.fi/drumbeat/datasources/smc-arc/architectural"));
			System.out.println(kanonisointi("http://architectural.drb.cs.hut.fi/drumbeat/datasets/smc-arc/architectural/v1"));
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

}
