package fi.aalto.drumbeat.ontology;

import org.apache.jena.ontology.DatatypeProperty;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.ObjectProperty;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.XSD;

import fi.aalto.drumbeat.Constants;
public class Ontology {
	static private OntModel schema = null;
	
	static 
	public class LBD {
		static public OntModel schema = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);

		static public OntClass DataSpaceNode = schema.createClass(Constants.security_ontology_base + "#DataSpaceNode");
		static public OntClass Collection = schema.createClass("http://drumbeat.cs.hut.fi/owl/lbdho.ttl#Collection");
		static public OntClass DataSource = schema.createClass("http://drumbeat.cs.hut.fi/owl/lbdho.ttl#DataSource");
		static public OntClass DataSet = schema.createClass("http://drumbeat.cs.hut.fi/owl/lbdho.ttl#DataSet");

		static public ObjectProperty hasCollection = schema
				.createObjectProperty(Constants.security_ontology_base + "#hasCollection");
		static {
			hasCollection.addDomain(DataSpaceNode);
			hasCollection.addRange(Collection);
		}

		static public ObjectProperty hasDataSource = schema
				.createObjectProperty(Constants.security_ontology_base + "#hasDataSource");
		static {
			hasDataSource.addDomain(Collection);
			hasDataSource.addRange(DataSource);
		}

		static public ObjectProperty hasDataSet = schema
				.createObjectProperty(Constants.security_ontology_base + "#hasDataSet");
		static {
			hasDataSet.addDomain(DataSource);
			hasDataSet.addRange(DataSet);
		}
	}

	static public class Club {
		static public OntModel schema = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);
		static public OntClass Club = schema.createClass(Constants.security_ontology_base + "#Club");
		static public OntClass Project = schema.createClass(Constants.security_ontology_base + "#Project");

		static {
			Club.addSubClass(Project);
			Project.addSuperClass(Club);
		}
		static public ObjectProperty hasClub = schema
				.createObjectProperty(Constants.security_ontology_base + "#hasClub");
		static {
			hasClub.addDomain(Authorization.ProtectedResource);
			hasClub.addRange(Club);
		}

		static public ObjectProperty hasProject = schema
				.createObjectProperty(Constants.security_ontology_base + "#hasProject");
		static {
			hasProject.addDomain(Authorization.ProtectedResource);
			hasProject.addRange(Project);

			hasProject.addSuperProperty(hasClub);
			hasClub.addSubProperty(hasProject);
		}

	}
	
	static public class Contractor {
		static public OntModel schema = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);
		static public OntClass Contractor = schema.createClass(Constants.security_ontology_base + "#Contractor");
		static public OntClass Person = schema.createClass("http://xmlns.com/foaf/0.1/Person");


		static public ObjectProperty trusts = schema.createObjectProperty(Constants.security_ontology_base + "#trusts");
		static {
			trusts.addDomain(Contractor);
			trusts.addRange(Person);
		}

		static public ObjectProperty hasContractor = schema
				.createObjectProperty(Constants.security_ontology_base + "#hasContractor");
		static {
			hasContractor.addDomain(Club.Club);
			hasContractor.addRange(Contractor);
		}

		static public ObjectProperty hasMainContractor = schema
				.createObjectProperty(Constants.security_ontology_base + "#hasMainContractor");
		static {
			hasMainContractor.addDomain(Club.Club);
			hasMainContractor.addRange(Contractor);
			hasContractor.addSubProperty(hasMainContractor);
			hasMainContractor.addSuperProperty(hasContractor);
		}

		static public ObjectProperty hasSubContractor = schema
				.createObjectProperty(Constants.security_ontology_base + "#hasSubcontractor");
		static {
			hasSubContractor.addDomain(Contractor);
			hasSubContractor.addRange(Contractor);
		}

	}


	static public class Authorization {
		static public OntModel schema = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);
		static public OntClass ProtectedResource = schema
				.createClass(Constants.security_ontology_base + "#ProtectedResource");
		static public OntClass AccessControlRule = schema
				.createClass(Constants.security_ontology_base + "#AccessControlRule");

		/*static public OntClass Permission = schema.createClass(Constants.security_ontology_base + "#PermissionRole");
		static public Individual create = schema.createIndividual(Constants.security_ontology_base + "#CREATE",
				Permission);
		static public Individual read = schema.createIndividual(Constants.security_ontology_base + "#READ", Permission);
		static public Individual update = schema.createIndividual(Constants.security_ontology_base + "#UPDATE",
				Permission);
		static public Individual delete = schema.createIndividual(Constants.security_ontology_base + "#DELETE",
				Permission);*/
		
		static public Resource  read = schema.createResource("http://www.w3.org/ns/auth/acl#Read");

		static {
			ProtectedResource.addSubClass(LBD.Collection);
			LBD.Collection.addSuperClass(ProtectedResource);
			ProtectedResource.addSubClass(LBD.DataSource);
			LBD.DataSource.addSuperClass(ProtectedResource);
			ProtectedResource.addSubClass(LBD.DataSet);
			LBD.DataSet.addSuperClass(ProtectedResource);

		}

		static public ObjectProperty hasAccessControlRule = schema
				.createObjectProperty(Constants.security_ontology_base + "#hasAccessControlRule");
		static {
			hasAccessControlRule.addDomain(ProtectedResource);
			hasAccessControlRule.addRange(AccessControlRule);
		}

		static {
		}

		static public ObjectProperty hasPermission = schema
				.createObjectProperty(Constants.security_ontology_base + "#hasPermission");
		static {
			schema.read("c://jo/ontology/acl.rdf");
			schema.setNsPrefix("acl", "http://www.w3.org/ns/auth/acl#");
			hasPermission.addDomain(AccessControlRule);
			Resource access=schema.createResource("http://www.w3.org/ns/auth/acl#Access");
			hasPermission.addRange(access);
			//hasPermission.addRange(Permission);
			
		}

		static public ObjectProperty rolePath = schema
				.createObjectProperty(Constants.security_ontology_base + "#hasRolePath");
		static {
			rolePath.addDomain(AccessControlRule);

		}

		static public OntClass ListNode = schema.createClass(Constants.security_ontology_base + "#ListNode");
		static public ObjectProperty first = schema.createObjectProperty(Constants.security_ontology_base + "#first");
		static {
			first.addDomain(ListNode);
			schema.createAllValuesFromRestriction(null, first, RDF.Property);
			rolePath.addRange(ListNode);

		}

		static public ObjectProperty rest = schema.createObjectProperty(Constants.security_ontology_base + "#rest");
		static {
			rest.addDomain(ListNode);
			rest.addRange(ListNode);
		}
		static {

		}
	}
	
	static public class Message {
		static public OntModel schema = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);

		static public OntClass SecurityMessage = schema.createClass(Constants.security_ontology_base + "#SecurityMessage");
		static public OntClass SecurityQuery = schema.createClass(Constants.security_ontology_base + "#SecurityQuery");
		static public OntClass SecurityResponse = schema.createClass(Constants.security_ontology_base + "#SecurityResponse");
		static {
			SecurityMessage.addSubClass(SecurityQuery);
			SecurityMessage.addSubClass(SecurityResponse);
			SecurityQuery.addSuperClass(SecurityMessage);
			SecurityResponse.addSuperClass(SecurityMessage);
		}
		static public DatatypeProperty hasTimeStamp = schema
				.createDatatypeProperty(Constants.security_ontology_base + "#hasTimeStamp");
		static {
			hasTimeStamp.addDomain(SecurityMessage);
			hasTimeStamp.addRange(XSD.dateTime);
		}

		static public final OntClass Status = schema.createClass(Constants.security_ontology_base + "#Status");
		static public final Individual accepted = schema.createIndividual(Constants.security_ontology_base + "#ACCEPTED", Status);
		static public final Individual denied = schema.createIndividual(Constants.security_ontology_base + "#DENIED", Status);

		static private RDFList status_enums = schema.createList();
		static {
			status_enums = status_enums.cons(accepted);
			status_enums = status_enums.cons(denied);
		}

		static public OntClass PermissionStatus = schema
				.createEnumeratedClass(Constants.security_ontology_base + "#PermissionStatus", status_enums);
		
		static public ObjectProperty hasPermissionStatus = schema.createObjectProperty(Constants.security_ontology_base  + "#hasPermissionStatus" );
		static {
			hasPermissionStatus.addDomain( SecurityResponse );
			hasPermissionStatus.addRange( PermissionStatus);
			
		}
		
		static {
			Authorization.rolePath.addDomain( SecurityQuery );

		}
		
		static public ObjectProperty hasWebID = schema.createObjectProperty( Constants.security_ontology_base + "#hasWebID" );
		static {
			hasWebID.addDomain( SecurityQuery );
			hasWebID.addRange( Contractor.Person );
			
		}
		
		static public DatatypeProperty hasMessage = schema
				.createDatatypeProperty(Constants.security_ontology_base + "#hasMessage");
		static {
			hasMessage.addDomain(SecurityResponse);
			hasMessage.addRange(XSD.xstring);
		}

	}


	static PropertyOperation property = (a) -> ResourceFactory
			.createProperty(Constants.security_ontology_base + "#" + a);

	interface PropertyOperation {
		Property create(String name);
	}

	static private Property create(PropertyOperation operation, String name) {
		return operation.create(name);
	}

	static public Property property_hasName = create(property, "hasName");
	static public Property property_hasPublicKey = create(property, "hasPublicKey");

	static public OntModel getSchema() {
		if (schema == null) {
			schema = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);
			schema.add(LBD.schema); 		
			schema.add(Club.schema);
			schema.add(Contractor.schema);
			schema.add(Authorization.schema);
			schema.add(Message.schema);
		}
		return schema;
	}

}
