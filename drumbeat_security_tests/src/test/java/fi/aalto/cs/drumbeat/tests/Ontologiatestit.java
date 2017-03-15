package fi.aalto.cs.drumbeat.tests;

import org.apache.jena.ontology.DatatypeProperty;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.ObjectProperty;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.XSD;

import fi.aalto.drumbeat.Constants;

//TODO try this: http://vowl.visualdataweb.org/webvowl.html
public class Ontologiatestit {
	
	public Ontologiatestit() {
		//Reasoner reasoner = PelletReasonerFactory.theInstance().create();
		//ontModelSpec.setReasoner(reasoner);
		//OntModel ontModel = ModelFactory.createOntologyModel(ontModelSpec, model);

		String NS= Constants.security_ontology_base;
		OntModel schema = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM_MICRO_RULE_INF);
		
		OntClass Site = schema.createClass( NS + "#Site" );
		OntClass Collection = schema.createClass("http://drumbeat.cs.hut.fi/owl/lbdho.ttl#Collection");
		OntClass DataSource = schema.createClass( "http://drumbeat.cs.hut.fi/owl/lbdho.ttl#DataSource");
		OntClass DataSet = schema.createClass( "http://drumbeat.cs.hut.fi/owl/lbdho.ttl#DataSet" );
		
		
		
		ObjectProperty hasCollection = schema.createObjectProperty( NS + "#hasCollection" );
		hasCollection.addDomain( Site );
		hasCollection.addRange( Collection );
		
		Site.addSubClass( schema.createAllValuesFromRestriction( null, hasCollection, Collection ));
		
		ObjectProperty hasDataSource = schema.createObjectProperty( NS + "#hasDataSource" );
		hasDataSource.addDomain( Collection );
		hasDataSource.addRange( DataSource );
		
		Collection.addSubClass( schema.createAllValuesFromRestriction( null, hasDataSource, DataSource ));
		
		ObjectProperty hasDataSet = schema.createObjectProperty( NS + "#hasDataSet" );
		hasDataSet.addDomain( DataSource );
		hasDataSet.addRange( DataSet );
		
		DataSource.addSubClass( schema.createAllValuesFromRestriction( null, hasDataSet, DataSet ));
		
		
		OntClass ProtectedResource = schema.createClass( NS + "#ProtectedResource" );
		OntClass AuthorizationRule = schema.createClass( NS + "#AuthorizationRule" );
		
		OntClass Permission = schema.createClass( NS + "#Permission" );
		Individual create = schema.createIndividual(NS + "#CREATE",Permission);
		Individual read = schema.createIndividual(NS + "#READ", Permission);
		Individual update = schema.createIndividual(NS + "#UPDATE", Permission);
		Individual delete = schema.createIndividual(NS + "#DELETE", Permission);
		
		RDFList enums = schema.createList(); 
		enums = enums.cons(create); 
		enums = enums.cons(read);
		enums = enums.cons(update);
		enums = enums.cons(delete);
		
		OntClass PermittedRole = schema.createEnumeratedClass(NS + "#PermittedRole" , enums);
		
		
		OntClass RulePath = schema.createClass( NS + "#RulePath" );
		OntClass Occupation = schema.createClass( NS + "#Occupation" );
		OntClass Project = schema.createClass( NS + "#Project" );
		OntClass Contractor = schema.createClass( NS + "#Contractor" );
		OntClass Person = schema.createClass("http://xmlns.com/foaf/0.1/Person");
		
		Occupation.addSubClass(Project);
		Project.addSuperClass(Occupation);
		
		ProtectedResource.addSubClass(Collection);
		Collection.addSuperClass(ProtectedResource);
		ProtectedResource.addSubClass(DataSource);
		DataSource.addSuperClass(ProtectedResource);
		ProtectedResource.addSubClass(DataSet);
		DataSet.addSuperClass(ProtectedResource);

		ObjectProperty trusts = schema.createObjectProperty( NS + "#trusts" );
		trusts.addDomain( Contractor );
		trusts.addRange( Person );
		Contractor.addSubClass( schema.createAllValuesFromRestriction( null, trusts, Person ));
		
		
		ObjectProperty hasAuthorizationRule = schema.createObjectProperty( NS + "#hasAuthorizationRule" );
		hasAuthorizationRule.addDomain( ProtectedResource );
		hasAuthorizationRule.addRange( AuthorizationRule );
		
		ProtectedResource.addSubClass( schema.createAllValuesFromRestriction( null, hasAuthorizationRule, AuthorizationRule ));
		ProtectedResource.addSubClass( schema.createMinCardinalityRestriction(null, hasAuthorizationRule, 0 ));
		
		
		ObjectProperty hasOccupation = schema.createObjectProperty( NS + "#hasOccupation" );
		hasOccupation .addDomain( ProtectedResource );
		hasOccupation .addRange( Occupation );
		
		ProtectedResource.addSubClass( schema.createAllValuesFromRestriction( null, hasOccupation, Occupation ));
		
		ObjectProperty hasProject = schema.createObjectProperty( NS + "#hasProject" );
		hasProject.addDomain( ProtectedResource );
		hasProject.addRange( Project );
		
		hasProject.addSuperProperty(hasOccupation);
		hasOccupation.addSubProperty(hasProject);
		
		ProtectedResource.addSubClass( schema.createAllValuesFromRestriction( null, hasProject, Project ));
		
		ObjectProperty hasContractor = schema.createObjectProperty( NS + "#hasContractor" );
		hasContractor.addDomain( Occupation );
		hasContractor.addRange( Contractor );
		Occupation.addSubClass( schema.createAllValuesFromRestriction( null, hasContractor, Contractor ));
		
		ObjectProperty hasSubContractor = schema.createObjectProperty( NS + "#hasSubcontractor" );
		hasSubContractor.addDomain( Contractor );
		hasSubContractor.addRange( Contractor );
		
		Contractor.addSubClass( schema.createAllValuesFromRestriction( null, hasSubContractor, Contractor ));
		
		ObjectProperty hasMainContractor = schema.createObjectProperty( NS + "#hasMainContractor" );
		hasMainContractor.addDomain( Occupation );
		hasMainContractor.addRange( Contractor );
		
		Occupation.addSubClass( schema.createAllValuesFromRestriction( null, hasMainContractor, Contractor ));

		
		hasContractor.addSubProperty( hasMainContractor );
		hasMainContractor.addSuperProperty( hasContractor );
		
		
		ObjectProperty hasPermittedRole = schema.createObjectProperty( NS + "#hasPermittedRole" );
		hasPermittedRole.addDomain( AuthorizationRule );
		hasPermittedRole.addRange( PermittedRole);
		AuthorizationRule.addSubClass( schema.createMinCardinalityRestriction(null, hasPermittedRole, 1));
		AuthorizationRule.addSubClass( schema.createAllValuesFromRestriction( null, hasPermittedRole, PermittedRole ));
		
		ObjectProperty hasRulePath = schema.createObjectProperty( NS + "#hasRulePath" );
		hasRulePath.addDomain( AuthorizationRule );
		hasRulePath.addRange( RulePath );
		
		AuthorizationRule.addSubClass( schema.createMinCardinalityRestriction(null, hasRulePath, 1));
		AuthorizationRule.addSubClass( schema.createAllValuesFromRestriction( null, hasRulePath, RulePath ));

		
		OntClass ListNode = schema.createClass( NS + "#ListNode" );
		
		ObjectProperty rest = schema.createObjectProperty( NS + "#rerst" );
		rest.addDomain( RulePath );
		rest.addDomain( ListNode );
		rest.addRange( ListNode );
		
		ListNode.addSubClass( schema.createAllValuesFromRestriction( null, rest, ListNode ));
		
		ObjectProperty first = schema.createObjectProperty( NS + "#first" );
		first.addDomain( ListNode );
		first.addRange( RDF.Property );
		
		ListNode.addSubClass( schema.createCardinalityRestriction( null, first, 1 ));
		ListNode.addSubClass( schema.createMinCardinalityRestriction(null, rest, 0 ));
		ListNode.addSubClass( schema.createMaxCardinalityRestriction(null, rest, 1 ));
		
		ListNode.addSubClass( schema.createAllValuesFromRestriction( null, first, RDF.Property ));
		ListNode.addSubClass( schema.createAllValuesFromRestriction( null, rest, ListNode ));

		OntClass SecurityMessage = schema.createClass( NS + "#SecurityMessage" );
		OntClass SecurityQuery = schema.createClass( NS + "#SecurityQuery" );
		OntClass SecurityResponse = schema.createClass( NS + "#SecurityResponse" );
		SecurityMessage.addSubClass(SecurityQuery);
		SecurityMessage.addSubClass(SecurityResponse);
		SecurityQuery.addSuperClass(SecurityMessage);
		SecurityResponse.addSuperClass(SecurityMessage);
		
		
		DatatypeProperty hasTimeStamp = schema.createDatatypeProperty(NS + "#hasTimeStamp" );
		hasTimeStamp.addDomain( SecurityMessage );
		hasTimeStamp.addRange( XSD.dateTime );
		
		
		
		OntClass Status = schema.createClass( NS + "#Status" );
		Individual accepted = schema.createIndividual(NS + "#ACCEPTED",Status);
		Individual denied = schema.createIndividual(NS + "#DENIED", Status);
		
		RDFList status_enums = schema.createList(); 
		status_enums = status_enums.cons(accepted); 
		status_enums = status_enums.cons(denied);
		
		OntClass PermissionStatus = schema.createEnumeratedClass(NS + "#PermissionStatus" , status_enums);
		
		ObjectProperty hasPermissionStatus = schema.createObjectProperty(NS + "#hasPermissionStatus" );
		hasPermissionStatus.addDomain( SecurityResponse );
		hasPermissionStatus.addRange( PermissionStatus);
		
		SecurityResponse.addSubClass( schema.createAllValuesFromRestriction( null, hasPermissionStatus, PermissionStatus ));
		
		
		hasRulePath.addDomain( SecurityQuery );
		SecurityQuery.addSubClass( schema.createAllValuesFromRestriction( null, hasRulePath, RulePath));
		
		
		ObjectProperty hasWebID = schema.createObjectProperty( NS + "#hasWebID" );
		hasWebID.addDomain( SecurityQuery );
		hasWebID.addRange( Person );
		
		SecurityQuery.addSubClass( schema.createAllValuesFromRestriction( null, hasWebID, Person ));
		

		
		
		
		Individual occupation = schema.createIndividual( "http://occupation/", Occupation );
		Individual maincontractor= schema.createIndividual( "http://mcontractor/", Contractor );
		occupation.addProperty(hasMainContractor, maincontractor);	
		StmtIterator iter=occupation.listProperties();
		System.out.println("starts");
		while(iter.hasNext()) {
			System.out.println(iter.next());
		}
		
		
		
		// @formatter:off
		/*
		FileWriter out = null;
		try {
		  out = new FileWriter( "c:/jo/ontology/drumbeat_security.ttl" );
		  schema.write( out, "Turtle" );
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
		  if (out != null) {
		    try {out.close();} catch (IOException ignore) {}
		  }
		}
		
		*/
		// @formatter:on
	}

	public static void main(String[] args) {
		new Ontologiatestit();
	}
}
