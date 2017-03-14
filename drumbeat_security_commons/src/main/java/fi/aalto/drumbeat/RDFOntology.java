package fi.aalto.drumbeat;

import org.apache.jena.ontology.DatatypeProperty;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.ObjectProperty;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.XSD;

public class RDFOntology {
	static private OntModel m = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);

	
	static public class LBD {
		static public OntClass Site = m.createClass(Constants.security_ontology_base + "#Site");
		static public OntClass Collection = m.createClass("http://drumbeat.cs.hut.fi/owl/lbdho.ttl#Collection");
		static public OntClass DataSource = m.createClass("http://drumbeat.cs.hut.fi/owl/lbdho.ttl#DataSource");
		static public OntClass DataSet = m.createClass("http://drumbeat.cs.hut.fi/owl/lbdho.ttl#DataSet");

		static public ObjectProperty hasCollection = m
				.createObjectProperty(Constants.security_ontology_base + "#hasCollection");
		static {
			hasCollection.addDomain(Site);
			hasCollection.addRange(Collection);
			Site.addSubClass(m.createAllValuesFromRestriction(null, hasCollection, Collection));
		}

		static public ObjectProperty hasDataSource = m
				.createObjectProperty(Constants.security_ontology_base + "#hasDataSource");
		static {
			hasDataSource.addDomain(Collection);
			hasDataSource.addRange(DataSource);

			Collection.addSubClass(m.createAllValuesFromRestriction(null, hasDataSource, DataSource));
		}

		static public ObjectProperty hasDataSet = m
				.createObjectProperty(Constants.security_ontology_base + "#hasDataSet");
		static {
			hasDataSet.addDomain(DataSource);
			hasDataSet.addRange(DataSet);
		}
	}

	static public class Authorization {
		static public OntClass ProtectedResource = m
				.createClass(Constants.security_ontology_base + "#ProtectedResource");
		static public OntClass AuthorizationRule = m
				.createClass(Constants.security_ontology_base + "#AuthorizationRule");
		static public OntClass RulePath = m.createClass(Constants.security_ontology_base + "#RulePath");

		static public OntClass Permission = m.createClass(Constants.security_ontology_base + "#Permission");
		static public Individual create = m.createIndividual(Constants.security_ontology_base + "#CREATE", Permission);
		static public Individual read = m.createIndividual(Constants.security_ontology_base + "#READ", Permission);
		static public Individual update = m.createIndividual(Constants.security_ontology_base + "#UPDATE", Permission);
		static public Individual delete = m.createIndividual(Constants.security_ontology_base + "#DELETE", Permission);
		static private RDFList enums = m.createList();
		static {
			enums = enums.cons(create);
			enums = enums.cons(read);
			enums = enums.cons(update);
			enums = enums.cons(delete);
		}
		static public OntClass PermittedRole = m
				.createEnumeratedClass(Constants.security_ontology_base + "#PermittedRole", enums);

		static {
			ProtectedResource.addSubClass(LBD.Collection);
			LBD.Collection.addSuperClass(ProtectedResource);
			ProtectedResource.addSubClass(LBD.DataSource);
			LBD.DataSource.addSuperClass(ProtectedResource);
			ProtectedResource.addSubClass(LBD.DataSet);
			LBD.DataSet.addSuperClass(ProtectedResource);

		}

		static public ObjectProperty hasAuthorizationRule = m
				.createObjectProperty(Constants.security_ontology_base + "#hasAuthorizationRule");
		static {
			hasAuthorizationRule.addDomain(ProtectedResource);
			hasAuthorizationRule.addRange(AuthorizationRule);
		}

		static {
			ProtectedResource
					.addSubClass(m.createAllValuesFromRestriction(null, hasAuthorizationRule, AuthorizationRule));
			ProtectedResource.addSubClass(m.createMinCardinalityRestriction(null, hasAuthorizationRule, 0));
		}

		static public ObjectProperty hasPermittedRole = m
				.createObjectProperty(Constants.security_ontology_base + "#hasPermittedRole");
		static {
			hasPermittedRole.addDomain(AuthorizationRule);
			hasPermittedRole.addRange(PermittedRole);
			AuthorizationRule.addSubClass(m.createMinCardinalityRestriction(null, hasPermittedRole, 1));
			AuthorizationRule.addSubClass(m.createAllValuesFromRestriction(null, hasPermittedRole, PermittedRole));
		}

		static public ObjectProperty hasRulePath = m
				.createObjectProperty(Constants.security_ontology_base + "#hasRulePath");
		static {
			hasRulePath.addDomain(AuthorizationRule);
			hasRulePath.addRange(RulePath);

			AuthorizationRule.addSubClass(m.createMinCardinalityRestriction(null, hasRulePath, 1));
			AuthorizationRule.addSubClass(m.createAllValuesFromRestriction(null, hasRulePath, RulePath));
			

		}

		static public OntClass ListNode = m.createClass(Constants.security_ontology_base + "#ListNode");
		static public ObjectProperty first = m.createObjectProperty(Constants.security_ontology_base + "#first");
		static {
			first.addDomain(ListNode);
			first.addRange(RDF.Property);
			ListNode.addSubClass(m.createAllValuesFromRestriction(null, first, RDF.Property));
			ListNode.addSubClass(m.createCardinalityRestriction(null, first, 1));

		}

		static public ObjectProperty rest = m.createObjectProperty(Constants.security_ontology_base + "#rest");
		static {
			rest.addDomain(RulePath);
			rest.addDomain(ListNode);
			rest.addRange(ListNode);

			ListNode.addSubClass(m.createAllValuesFromRestriction(null, rest, ListNode));
			ListNode.addSubClass(m.createMinCardinalityRestriction(null, rest, 0));
			ListNode.addSubClass(m.createMaxCardinalityRestriction(null, rest, 1));
		}
		static {

		}
	}

	static public class Occupation {
		static public OntClass Occupation = m.createClass(Constants.security_ontology_base + "#Occupation");
		static public OntClass Project = m.createClass(Constants.security_ontology_base + "#Project");

		static {
			Occupation.addSubClass(Project);
			Project.addSuperClass(Occupation);
		}
		static public ObjectProperty hasOccupation = m
				.createObjectProperty(Constants.security_ontology_base + "#hasOccupation");
		static {
			hasOccupation.addDomain(Authorization.ProtectedResource);
			hasOccupation.addRange(Occupation);

			Authorization.ProtectedResource
					.addSubClass(m.createAllValuesFromRestriction(null, hasOccupation, Occupation));
		}

		static public ObjectProperty hasProject = m
				.createObjectProperty(Constants.security_ontology_base + "#hasProject");
		static {
			hasProject.addDomain(Authorization.ProtectedResource);
			hasProject.addRange(Project);

			hasProject.addSuperProperty(hasOccupation);
			hasOccupation.addSubProperty(hasProject);
			Authorization.ProtectedResource.addSubClass(m.createAllValuesFromRestriction(null, hasProject, Project));
		}

		
	}
	
	static public class Contractor {
		static public OntClass Contractor = m.createClass(Constants.security_ontology_base + "#Contractor");
		// WebID profile ontology class:
		static public OntClass Person = m.createClass("http://xmlns.com/foaf/0.1/Person");


		static public ObjectProperty trusts = m.createObjectProperty(Constants.security_ontology_base + "#trusts");
		static {
			trusts.addDomain(Contractor);
			trusts.addRange(Person);
			Contractor.addSubClass(m.createAllValuesFromRestriction(null, trusts, Person));
		}

		static public ObjectProperty hasContractor = m
				.createObjectProperty(Constants.security_ontology_base + "#hasContractor");
		static {
			hasContractor.addDomain(Occupation.Occupation);
			hasContractor.addRange(Contractor);
			Occupation.Occupation.addSubClass(m.createAllValuesFromRestriction(null, hasContractor, Contractor));
		}

		static public ObjectProperty hasMainContractor = m
				.createObjectProperty(Constants.security_ontology_base + "#hasMainContractor");
		static {
			hasMainContractor.addDomain(Occupation.Occupation);
			hasMainContractor.addRange(Contractor);
			Occupation.Occupation.addSubClass(m.createAllValuesFromRestriction(null, hasMainContractor, Contractor));
			hasContractor.addSubProperty(hasMainContractor);
			hasMainContractor.addSuperProperty(hasContractor);
		}

		static public ObjectProperty hasSubContractor = m
				.createObjectProperty(Constants.security_ontology_base + "#hasSubcontractor");
		static {
			hasSubContractor.addDomain(Contractor);
			hasSubContractor.addRange(Contractor);
			Contractor.addSubClass(m.createAllValuesFromRestriction(null, hasSubContractor, Contractor));
		}

	}

	static public class Message {
		static public OntClass SecurityMessage = m.createClass(Constants.security_ontology_base + "#SecurityMessage");
		static public OntClass SecurityQuery = m.createClass(Constants.security_ontology_base + "#SecurityQuery");
		static public OntClass SecurityResponse = m.createClass(Constants.security_ontology_base + "#SecurityResponse");
		static {
			SecurityMessage.addSubClass(SecurityQuery);
			SecurityMessage.addSubClass(SecurityResponse);
			SecurityQuery.addSuperClass(SecurityMessage);
			SecurityResponse.addSuperClass(SecurityMessage);
		}
		static public DatatypeProperty hasTimeStamp = m
				.createDatatypeProperty(Constants.security_ontology_base + "#hasTimeStamp");
		static {
			hasTimeStamp.addDomain(SecurityMessage);
			hasTimeStamp.addRange(XSD.dateTime);
		}

		static public OntClass Status = m.createClass(Constants.security_ontology_base + "#Status");
		static public Individual accepted = m.createIndividual(Constants.security_ontology_base + "#ACCEPTED", Status);
		static public Individual denied = m.createIndividual(Constants.security_ontology_base + "#DENIED", Status);

		static private RDFList status_enums = m.createList();
		static {
			status_enums = status_enums.cons(accepted);
			status_enums = status_enums.cons(denied);
		}

		static public OntClass PermissionStatus = m
				.createEnumeratedClass(Constants.security_ontology_base + "#PermissionStatus", status_enums);
		
		static public ObjectProperty hasPermissionStatus = m.createObjectProperty(Constants.security_ontology_base  + "#hasPermissionStatus" );
		static {
			hasPermissionStatus.addDomain( SecurityResponse );
			hasPermissionStatus.addRange( PermissionStatus);
			
			SecurityResponse.addSubClass( m.createAllValuesFromRestriction( null, hasPermissionStatus, PermissionStatus ));
		}
		
		static {
			Authorization.hasRulePath.addDomain( SecurityQuery );
			SecurityQuery.addSubClass( m.createAllValuesFromRestriction( null, Authorization.hasRulePath, Authorization.RulePath));

		}
		
		static public ObjectProperty hasWebID = m.createObjectProperty( Constants.security_ontology_base + "#hasWebID" );
		static {
			hasWebID.addDomain( SecurityQuery );
			hasWebID.addRange( Contractor.Person );
			SecurityQuery.addSubClass( m.createAllValuesFromRestriction( null, hasWebID, Contractor.Person));
			
		}
		
		static public DatatypeProperty hasMessage = m
				.createDatatypeProperty(Constants.security_ontology_base + "#hasMessage");
		static {
			hasMessage.addDomain(SecurityResponse);
			hasMessage.addRange(XSD.xstring);
		}

	}
	static PropertyOperation property = (a) -> ResourceFactory.createProperty(Constants.security_ontology_base + "#"+a);
	interface PropertyOperation {
		Property create(String name);
	   }
	static private Property create(PropertyOperation operation,String name){
	      return operation.create(name);
	   }
	 static public Property property_hasName = create(property, "hasName");
	 static public Property property_hasPublicKey = create(property,"hasPublicKey");
	
	 
	 
}
