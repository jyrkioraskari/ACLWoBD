package fi.ni.data_store_test_data;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;

import fi.ni.Constants;
import fi.ni.data_store_test_data.Permission.Right;

public class AuthenticationRule extends AbstractData {
	
	private Map<Right,Permission> permissions= new HashMap<Right,Permission>();
	private Set<RulePath> rulepaths= new HashSet<RulePath>();

	public AuthenticationRule(URI root,String name, Model model) {
		super(root, name, model);
		//DEFAULT
		try {
			addRight(Permission.Right.READ);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		
		// Set more decently
		try {
			addRulePath();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	
	public Permission addRight(Permission.Right right) throws URISyntaxException {
		Property hasPermission = model.getProperty(Constants.security_ontology_base + "#hasPermission");

		Permission p = Permission.getPermission(right, model);
		permissions.put(right, p);
		self.addProperty(hasPermission, p.self);
		return p;
	}

	
	public RulePath addRulePath() throws URISyntaxException {
		Property hasRulePath = model.getProperty(Constants.security_ontology_base + "#hasRulePath");

		RulePath rp =new RulePath(new URI(self.getURI()), model);
		rulepaths.add(rp);
		self.addProperty(hasRulePath, rp.self);
		return rp;
	}
}
