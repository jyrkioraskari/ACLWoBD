package fi.ni.data_store_test_data;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.apache.jena.rdf.model.Model;

public class Permission extends AbstractData {
	public enum Right{
		CREATE,READ,UPDATE,DELETE
	}

	static Map<Right,Permission> permissions= new HashMap<Right,Permission>();
	static Permission getPermission(Right right, Model model)
	{
		Permission p=permissions.get(right);
		if(p==null)
			try {
				p=new Permission(right.toString(), model);
				permissions.put(right, p);
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		return p;
	}
	
	
	
	public Permission(String name, Model model) throws URISyntaxException {
			super(name, model);
	}



}
