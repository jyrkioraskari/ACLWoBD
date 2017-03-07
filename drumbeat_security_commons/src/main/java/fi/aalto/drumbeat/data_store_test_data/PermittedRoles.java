package fi.aalto.drumbeat.data_store_test_data;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.apache.jena.rdf.model.Model;

public class PermittedRoles extends AbstractData {
	public enum Right{
		CREATE,READ,UPDATE,DELETE
	}

	static Map<Right,PermittedRoles> permissions= new HashMap<Right,PermittedRoles>();
	static PermittedRoles getPermission(Right right, Model model)
	{
		PermittedRoles p=permissions.get(right);
		if(p==null)
			try {
				p=new PermittedRoles(right.toString(), model);
				permissions.put(right, p);
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		return p;
	}
	
	
	
	public PermittedRoles(String name, Model model) throws URISyntaxException {
			super(name, model);
	}



}
