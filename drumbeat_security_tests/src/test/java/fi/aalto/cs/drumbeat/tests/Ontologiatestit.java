package fi.aalto.cs.drumbeat.tests;

import java.io.FileWriter;
import java.io.IOException;

import org.apache.jena.ontology.DatatypeProperty;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.ObjectProperty;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.reasoner.Reasoner;
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
		OntModel m = ModelFactory.createOntologyModel( OntModelSpec.OWL_DL_MEM);
		
		OntClass Site = m.createClass( NS + "#Site" );
		OntClass Collection = m.createClass("http://drumbeat.cs.hut.fi/owl/lbdho.ttl#Collection");
		OntClass DataSource = m.createClass( "http://drumbeat.cs.hut.fi/owl/lbdho.ttl#DataSource");
		OntClass DataSet = m.createClass( "http://drumbeat.cs.hut.fi/owl/lbdho.ttl#DataSet" );
		
		
		
		ObjectProperty hasCollection = m.createObjectProperty( NS + "#hasCollection" );
		hasCollection.addDomain( Site );
		hasCollection.addRange( Collection );
		
		Site.addSubClass( m.createAllValuesFromRestriction( null, hasCollection, Collection ));
		
		ObjectProperty hasDataSource = m.createObjectProperty( NS + "#hasDataSource" );
		hasDataSource.addDomain( Collection );
		hasDataSource.addRange( DataSource );
		
		Collection.addSubClass( m.createAllValuesFromRestriction( null, hasDataSource, DataSource ));
		
		ObjectProperty hasDataSet = m.createObjectProperty( NS + "#hasDataSet" );
		hasDataSet.addDomain( DataSource );
		hasDataSet.addRange( DataSet );
		
		DataSource.addSubClass( m.createAllValuesFromRestriction( null, hasDataSet, DataSet ));
		
		
		OntClass ProtectedResource = m.createClass( NS + "#ProtectedResource" );
		OntClass AuthorizationRule = m.createClass( NS + "#AuthorizationRule" );
		
		OntClass Permission = m.createClass( NS + "#Permission" );
		Individual create = m.createIndividual(NS + "#CREATE",Permission);
		Individual read = m.createIndividual(NS + "#READ", Permission);
		Individual update = m.createIndividual(NS + "#UPDATE", Permission);
		Individual delete = m.createIndividual(NS + "#DELETE", Permission);
		
		RDFList enums = m.createList(); 
		enums = enums.cons(create); 
		enums = enums.cons(read);
		enums = enums.cons(update);
		enums = enums.cons(delete);
		
		OntClass PermittedRole = m.createEnumeratedClass(NS + "#PermittedRole" , enums);
		
		
		OntClass RulePath = m.createClass( NS + "#RulePath" );
		OntClass Occupation = m.createClass( NS + "#Occupation" );
		OntClass Project = m.createClass( NS + "#Project" );
		OntClass Contractor = m.createClass( NS + "#Contractor" );
		OntClass Person = m.createClass("http://xmlns.com/foaf/0.1/Person");
		
		Occupation.addSubClass(Project);
		Project.addSuperClass(Occupation);
		
		ProtectedResource.addSubClass(Collection);
		Collection.addSuperClass(ProtectedResource);
		ProtectedResource.addSubClass(DataSource);
		DataSource.addSuperClass(ProtectedResource);
		ProtectedResource.addSubClass(DataSet);
		DataSet.addSuperClass(ProtectedResource);

		ObjectProperty trusts = m.createObjectProperty( NS + "#trusts" );
		trusts.addDomain( Contractor );
		trusts.addRange( Person );
		Contractor.addSubClass( m.createAllValuesFromRestriction( null, trusts, Person ));
		
		
		ObjectProperty hasAuthorizationRule = m.createObjectProperty( NS + "#hasAuthorizationRule" );
		hasAuthorizationRule.addDomain( ProtectedResource );
		hasAuthorizationRule.addRange( AuthorizationRule );
		
		ProtectedResource.addSubClass( m.createAllValuesFromRestriction( null, hasAuthorizationRule, AuthorizationRule ));
		ProtectedResource.addSubClass( m.createMinCardinalityRestriction(null, hasAuthorizationRule, 0 ));
		
		
		ObjectProperty hasOccupation = m.createObjectProperty( NS + "#hasOccupation" );
		hasOccupation .addDomain( ProtectedResource );
		hasOccupation .addRange( Occupation );
		
		ProtectedResource.addSubClass( m.createAllValuesFromRestriction( null, hasOccupation, Occupation ));
		
		ObjectProperty hasProject = m.createObjectProperty( NS + "#hasProject" );
		hasProject.addDomain( ProtectedResource );
		hasProject.addRange( Project );
		
		hasProject.addSuperProperty(hasOccupation);
		hasOccupation.addSubProperty(hasProject);
		
		ProtectedResource.addSubClass( m.createAllValuesFromRestriction( null, hasProject, Project ));
		
		ObjectProperty hasContractor = m.createObjectProperty( NS + "#hasContractor" );
		hasContractor.addDomain( Occupation );
		hasContractor.addRange( Contractor );
		Occupation.addSubClass( m.createAllValuesFromRestriction( null, hasContractor, Contractor ));
		
		ObjectProperty hasSubContractor = m.createObjectProperty( NS + "#hasSubcontractor" );
		hasSubContractor.addDomain( Contractor );
		hasSubContractor.addRange( Contractor );
		
		Contractor.addSubClass( m.createAllValuesFromRestriction( null, hasSubContractor, Contractor ));
		
		ObjectProperty hasMainContractor = m.createObjectProperty( NS + "#hasMainContractor" );
		hasMainContractor.addDomain( Occupation );
		hasMainContractor.addRange( Contractor );
		
		Occupation.addSubClass( m.createAllValuesFromRestriction( null, hasMainContractor, Contractor ));

		
		hasContractor.addSubProperty( hasMainContractor );
		hasMainContractor.addSuperProperty( hasContractor );
		
		
		ObjectProperty hasPermittedRole = m.createObjectProperty( NS + "#hasPermittedRole" );
		hasPermittedRole.addDomain( AuthorizationRule );
		hasPermittedRole.addRange( PermittedRole);
		AuthorizationRule.addSubClass( m.createMinCardinalityRestriction(null, hasPermittedRole, 1));
		AuthorizationRule.addSubClass( m.createAllValuesFromRestriction( null, hasPermittedRole, PermittedRole ));
		
		ObjectProperty hasRulePath = m.createObjectProperty( NS + "#hasRulePath" );
		hasRulePath.addDomain( AuthorizationRule );
		hasRulePath.addRange( RulePath );
		
		AuthorizationRule.addSubClass( m.createMinCardinalityRestriction(null, hasRulePath, 1));
		AuthorizationRule.addSubClass( m.createAllValuesFromRestriction( null, hasRulePath, RulePath ));

		
		OntClass ListNode = m.createClass( NS + "#ListNode" );
		
		
		ObjectProperty hasPath = m.createObjectProperty( NS + "#hasPath" );
		hasPath.addDomain( RulePath );
		hasPath.addRange( ListNode );
		
		RulePath.addSubClass( m.createAllValuesFromRestriction( null, hasPath, ListNode ));
		
		ObjectProperty hasNext = m.createObjectProperty( NS + "#hasNext" );
		hasNext.addDomain( ListNode );
		hasNext.addRange( ListNode );
		
		ListNode.addSubClass( m.createAllValuesFromRestriction( null, hasNext, ListNode ));
		
		ObjectProperty hasFirst = m.createObjectProperty( NS + "#hasFirst" );
		hasFirst.addDomain( ListNode );
		hasFirst.addRange( RDF.Property );
		
		ListNode.addSubClass( m.createCardinalityRestriction( null, hasFirst, 1 ));
		ListNode.addSubClass( m.createMinCardinalityRestriction(null, hasNext, 0 ));
		ListNode.addSubClass( m.createMaxCardinalityRestriction(null, hasNext, 1 ));
		
		ListNode.addSubClass( m.createAllValuesFromRestriction( null, hasFirst, RDF.Property ));
		ListNode.addSubClass( m.createAllValuesFromRestriction( null, hasNext, ListNode ));

		OntClass SecurityMessage = m.createClass( NS + "#SecurityMessage" );
		OntClass SecurityQuery = m.createClass( NS + "#SecurityQuery" );
		OntClass SecurityResponse = m.createClass( NS + "#SecurityResponse" );
		SecurityMessage.addSubClass(SecurityQuery);
		SecurityMessage.addSubClass(SecurityResponse);
		SecurityQuery.addSuperClass(SecurityMessage);
		SecurityResponse.addSuperClass(SecurityMessage);
		
		
		DatatypeProperty hasTimeStamp = m.createDatatypeProperty(NS + "#hasTimeStamp" );
		hasTimeStamp.addDomain( SecurityMessage );
		hasTimeStamp.addRange( XSD.dateTime );
		
		
		
		OntClass Status = m.createClass( NS + "#Status" );
		Individual accepted = m.createIndividual(NS + "#ACCEPTED",Status);
		Individual denied = m.createIndividual(NS + "#DENIED", Status);
		
		RDFList status_enums = m.createList(); 
		status_enums = status_enums.cons(accepted); 
		status_enums = status_enums.cons(denied);
		
		OntClass PermissionStatus = m.createEnumeratedClass(NS + "#PermissionStatus" , status_enums);
		
		ObjectProperty hasPermissionStatus = m.createObjectProperty(NS + "#hasPermissionStatus" );
		hasPermissionStatus.addDomain( SecurityResponse );
		hasPermissionStatus.addRange( PermissionStatus);
		
		SecurityResponse.addSubClass( m.createAllValuesFromRestriction( null, hasPermissionStatus, PermissionStatus ));
		
		
		hasRulePath.addDomain( SecurityQuery );
		SecurityQuery.addSubClass( m.createAllValuesFromRestriction( null, hasRulePath, RulePath));
		
		
		ObjectProperty hasWebID = m.createObjectProperty( NS + "#hasWebID" );
		hasWebID.addDomain( SecurityQuery );
		hasWebID.addRange( Person );
		
		SecurityQuery.addSubClass( m.createAllValuesFromRestriction( null, hasWebID, Person ));
		
		
		Individual thisSite = m.createIndividual( NS + "#thisSite", Site );

		
		
		FileWriter out = null;
		try {
		  out = new FileWriter( "c:/jo/ontology/drumbeat_security.ttl" );
		  m.write( out, "Turtle" );
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
		  if (out != null) {
		    try {out.close();} catch (IOException ignore) {}
		  }
		}
	}

	public static void main(String[] args) {
		new Ontologiatestit();
	}
}
