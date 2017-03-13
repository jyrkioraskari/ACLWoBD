package fi.aalto.drumbeat.data_store_test_data;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.jena.ontology.OntModel;

import fi.aalto.drumbeat.RDFConstants;
import fi.aalto.drumbeat.data_store_test_data.PermittedRoles.Right;

public class AuthenticationRule extends AbstractData {
	
	private Map<Right,PermittedRoles> permissions= new HashMap<Right,PermittedRoles>();
	private Set<RulePath> rulepaths= new HashSet<RulePath>();

	public AuthenticationRule(URI root,String name, OntModel model) {
		super(root, name, model);
		//DEFAULT
		try {
			addAllowedRole(PermittedRoles.Right.READ);
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

	
	public PermittedRoles addAllowedRole(PermittedRoles.Right right) throws URISyntaxException {
		PermittedRoles p = PermittedRoles.getPermission(right, model);
		permissions.put(right, p);
		self.addProperty(RDFConstants.property_hasPermittedRole, p.self);
		return p;
	}

	
	public RulePath addRulePath() throws URISyntaxException {
		RulePath rp =new RulePath(new URI(self.getURI()), model);
		rulepaths.add(rp);
		self.addProperty(RDFConstants.property_hasRulePath, rp.self);
		return rp;
	}
}
