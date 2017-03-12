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
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.XSD;

import fi.aalto.drumbeat.Constants;

//TODO try this: http://vowl.visualdataweb.org/webvowl.html
public class Ontologiatestit {
	
	public Ontologiatestit() {
		String NS= Constants.security_ontology_base;
		OntModel m = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM );
		
		OntClass DrumbeatDataStore = m.createClass( NS + "#DrumbeatDataStore" );
		OntClass Collection = m.createClass("http://drumbeat.cs.hut.fi/owl/lbdho.ttl#Collection");
		OntClass DataSource = m.createClass( "http://drumbeat.cs.hut.fi/owl/lbdho.ttl#DataSource");
		OntClass DataSet = m.createClass( "http://drumbeat.cs.hut.fi/owl/lbdho.ttl#DataSet" );
		
		ObjectProperty hasCollection = m.createObjectProperty( NS + "#hasCollection" );
		hasCollection.addDomain( DrumbeatDataStore );
		hasCollection.addRange( Collection );
		
		ObjectProperty hasDataSource = m.createObjectProperty( NS + "#hasDataSource" );
		hasDataSource.addDomain( Collection );
		hasDataSource.addRange( DataSource );
		
		ObjectProperty hasDataSet = m.createObjectProperty( NS + "#hasDataSet" );
		hasDataSet.addDomain( DataSource );
		hasDataSet.addRange( DataSet );
		
		
		OntClass ProtectedResource = m.createClass( NS + "#ProtectedResource" );
		OntClass AuthorizationRule = m.createClass( NS + "#AuthorizationRule" );
		OntClass PermittedRole = m.createClass( NS + "#PermittedRole" );
		
		OntClass RulePath = m.createClass( NS + "#RulePath" );
		OntClass Project = m.createClass( NS + "#Project" );
		OntClass Contractor = m.createClass( NS + "#Contractor" );
		OntClass MainContractor = m.createClass( NS + "#MainContractor" );
		OntClass Person = m.createClass("http://xmlns.com/foaf/0.1/Person");
		
		ProtectedResource.addSubClass(Collection);
		Collection.addSuperClass(ProtectedResource);
		ProtectedResource.addSubClass(DataSource);
		DataSource.addSuperClass(ProtectedResource);
		ProtectedResource.addSubClass(DataSet);
		DataSet.addSuperClass(ProtectedResource);

		Contractor.addSubClass(MainContractor);
		MainContractor.addSuperClass(Contractor);
		
		ObjectProperty knowsPerson = m.createObjectProperty( NS + "#knowsPerson" );
		knowsPerson.addDomain( Contractor );
		knowsPerson.addRange( Person );
		
		
		ObjectProperty hasAuthorizationRule = m.createObjectProperty( NS + "#hasAuthorizationRule" );
		hasAuthorizationRule.addDomain( ProtectedResource );
		hasAuthorizationRule.addRange( AuthorizationRule );
		
		ObjectProperty hasProject = m.createObjectProperty( NS + "#hasProject" );
		hasProject.addDomain( ProtectedResource );
		hasProject.addRange( Project );
		
		ObjectProperty hasContractor = m.createObjectProperty( NS + "#hasContractor" );
		hasContractor.addDomain( Project );
		hasContractor.addRange( Contractor );
		
		ObjectProperty hasMainContractor = m.createObjectProperty( NS + "#hasMainContractor" );
		hasMainContractor.addDomain( Project );
		hasMainContractor.addRange( MainContractor );
		
		hasContractor.addSubProperty( hasMainContractor );
		hasMainContractor.addSuperProperty( hasContractor );
		
		
		ObjectProperty hasPermittedRole = m.createObjectProperty( NS + "#hasPermittedRole" );
		hasPermittedRole.addDomain( AuthorizationRule );
		hasPermittedRole.addRange( PermittedRole);
		
		ObjectProperty hasRulePath = m.createObjectProperty( NS + "#hasRulePath" );
		hasRulePath.addDomain( AuthorizationRule );
		hasRulePath.addRange( RulePath );
		
		OntClass ListNode = m.createClass( NS + "#ListNode" );
		
		
		ObjectProperty hasPath = m.createObjectProperty( NS + "#hasPath" );
		hasPath.addDomain( RulePath );
		hasPath.addRange( ListNode );
		
		ObjectProperty hasNext = m.createObjectProperty( NS + "#hasNext" );
		hasNext.addDomain( ListNode );
		hasNext.addRange( ListNode );
		
		ObjectProperty hasFirst = m.createObjectProperty( NS + "#hasFirst" );
		hasFirst.addDomain( ListNode );
		hasFirst.addRange( RDF.Property );
		

		OntClass DrumbeatSecurityMessage = m.createClass( NS + "#DrumbeatSecurityMessage" );
		OntClass DrumbeatSecurityQuery = m.createClass( NS + "#DrumbeatSecurityQuery" );
		OntClass DrumbeatSecurityResponse = m.createClass( NS + "#DrumbeatSecurityResponse" );
		DrumbeatSecurityMessage.addSubClass(DrumbeatSecurityQuery);
		DrumbeatSecurityMessage.addSubClass(DrumbeatSecurityResponse);
		DrumbeatSecurityQuery.addSuperClass(DrumbeatSecurityMessage);
		DrumbeatSecurityResponse.addSuperClass(DrumbeatSecurityMessage);
		
		
		DatatypeProperty hasTimeStamp = m.createDatatypeProperty(NS + "#hasTimeStamp" );
		hasTimeStamp.addDomain( DrumbeatSecurityMessage );
		hasTimeStamp.addRange( XSD.dateTime );
		
		DatatypeProperty status = m.createDatatypeProperty(NS + "#status" );
		status.addDomain( DrumbeatSecurityResponse );
		status.addRange( XSD.xstring );
		
		hasRulePath.addDomain( DrumbeatSecurityQuery );
		ObjectProperty hasWebID = m.createObjectProperty( NS + "#hasWebID" );
		hasWebID.addDomain( DrumbeatSecurityQuery );
		hasWebID.addRange( Person );
		
		
		Individual thisSite = m.createIndividual( NS + "#thisSite", DrumbeatDataStore );

		
		
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
