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
import org.apache.jena.vocabulary.RDFS;
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

	static public class AccessContext {
		static public OntModel schema = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);
		static public OntClass AccessContex = schema.createClass(Constants.security_ontology_base + "#AccessContex");
		static public OntClass ConstructionProject = schema.createClass(Constants.security_ontology_base + "#ConstructionProject");

		static {
			AccessContex.addSubClass(ConstructionProject);
			ConstructionProject.addSuperClass(AccessContex);
		}
		static public ObjectProperty hasAccessContex = schema
				.createObjectProperty(Constants.security_ontology_base + "#hasAccessContex");
		static {
			hasAccessContex.addDomain(RDFS.Resource);
			hasAccessContex.addRange(AccessContex);
		}

		static public ObjectProperty hasProject = schema
				.createObjectProperty(Constants.security_ontology_base + "#hasProject");
		static {
			hasProject.addDomain(RDFS.Resource);
			hasProject.addRange(ConstructionProject);

			hasProject.addSuperProperty(hasAccessContex);
			hasAccessContex.addSubProperty(hasProject);
		}

	}
	
	static public class Contractor {
		static public OntModel schema = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);
		static public OntClass Company = schema.createClass(Constants.security_ontology_base + "#Company");
		static public OntClass Architect = schema.createClass(Constants.security_ontology_base + "#Architect");
		
		static public OntClass Contractor = schema.createClass(Constants.security_ontology_base + "#Contractor");
		static public OntClass MainContractor = schema.createClass(Constants.security_ontology_base + "#MainContractor");
		
		static public OntClass Person = schema.createClass("http://xmlns.com/foaf/0.1/Person");


		static public ObjectProperty hasEmployee = schema.createObjectProperty(Constants.security_ontology_base + "#hasEmployee");
		static public ObjectProperty hasManager = schema.createObjectProperty(Constants.security_ontology_base + "#hasManager");

		static {
			Company.addSubClass(Architect);
			Company.addSubClass(Contractor);
			Contractor.addSubClass(MainContractor);
			
			MainContractor.addSuperClass(Contractor);
			Contractor.addSuperClass(Company);
			Architect.addSuperClass(Company);
			
			
			hasEmployee.addDomain(Company);
			hasEmployee.addRange(Person);
			hasEmployee.addSubProperty(hasManager);
		
			
			hasManager.addDomain(Company);
			hasManager.addRange(Person);
			hasManager.addSuperProperty(hasEmployee);
			
			

		}
		
		
		static public ObjectProperty hasHeadDesigner = schema
				.createObjectProperty(Constants.security_ontology_base + "#hasHeadDesigner");
		static {
			hasHeadDesigner.addDomain(AccessContext.ConstructionProject);
			hasHeadDesigner.addRange(Architect);
		}
		
		
		static public ObjectProperty hasContractor = schema
				.createObjectProperty(Constants.security_ontology_base + "#hasContractor");
		static {
			hasContractor.addDomain(AccessContext.ConstructionProject);
			hasContractor.addRange(Contractor);
		}

		static public ObjectProperty hasMainContractor = schema
				.createObjectProperty(Constants.security_ontology_base + "#hasMainContractor");
		static {
			hasMainContractor.addDomain(AccessContext.ConstructionProject);
			hasMainContractor.addRange(MainContractor);
			
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
		static public OntClass AccessControlRule = schema
				.createClass(Constants.security_ontology_base + "#AccessControlRule");

		static public OntClass Permission = schema.createClass(Constants.security_ontology_base + "#Permission");
		static public Individual write = schema.createIndividual(Constants.security_ontology_base + "#Write",
				Permission);
		static public Individual read = schema.createIndividual(Constants.security_ontology_base + "#Read", Permission);
		static public Individual append = schema.createIndividual(Constants.security_ontology_base + "#Append",
				Permission);
		static public Individual delete = schema.createIndividual(Constants.security_ontology_base + "#Delete",
				Permission);
		
		
		
		static public ObjectProperty hasAccessControlRule = schema
				.createObjectProperty(Constants.security_ontology_base + "#hasAccessControlRule");
		static {
			hasAccessControlRule.addDomain(RDFS.Resource);
			hasAccessControlRule.addRange(AccessControlRule);
		}

		
		
		static public ObjectProperty hasPermission = schema
				.createObjectProperty(Constants.security_ontology_base + "#hasPermission");
		static {
			hasPermission.addDomain(AccessControlRule);
			Resource access=schema.createResource("http://www.w3.org/ns/auth/acl#Access");
			hasPermission.addRange(Permission);
			
		}
		static public OntClass Role = schema.createClass(Constants.security_ontology_base + "#Role");
		

		static public DatatypeProperty hasName = schema
				.createDatatypeProperty(Constants.security_ontology_base + "#hasName");
		static{
		hasName.addDomain(Role);
		hasName.addRange( XSD.xstring );
		}
		
		static public ObjectProperty hasRole = schema
				.createObjectProperty(Constants.security_ontology_base + "#hasRole");
		static {
			hasRole.addDomain(AccessControlRule);
			hasRole.addRange(Role);

		}

		static public ObjectProperty hasRolePath = schema
				.createObjectProperty(Constants.security_ontology_base + "#hasRolePath");
		static {
			hasRolePath.addDomain(Role);
		}
		
		

		static public OntClass ListNode = schema.createClass(Constants.security_ontology_base + "#ListNode");
		static public ObjectProperty first = schema.createObjectProperty(Constants.security_ontology_base + "#first");
		static {
			first.addDomain(ListNode);
			schema.createAllValuesFromRestriction(null, first, RDF.Property);
			hasRolePath.addRange(ListNode);

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
			Authorization.hasRolePath.addDomain( SecurityQuery );

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
			schema.add(AccessContext.schema);
			schema.add(Contractor.schema);
			schema.add(Authorization.schema);
			schema.add(Message.schema);
		}
		return schema;
	}

}
