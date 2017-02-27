package fi.aalto.cs.drumbeat;

import java.io.ByteArrayOutputStream;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;

import fi.aalto.cs.drumbeat.vo.Data;

/*
* 
Jyrki Oraskari, Aalto University, 2016 

This research has partly been carried out at Aalto University in DRUMBEAT 
�Web-Enabled Construction Lifecycle� (2014-2017) �funded by Tekes, 
Aalto University, and the participating companies.

The MIT License (MIT)
Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

public class FOAFCreator {
	Data data_store = Data.Singleton.INSTANCE.getSingleton();
	
	
	public String create() {
		String me_URI=data_store.getCLIENT_SUBJECT_ALT_NAME_URI();
		int pos=me_URI.indexOf("#me");
		String profile_URI;
		if(pos!=-1)
		{
			   profile_URI=me_URI.substring(0, pos);
		}
		else
		{
		   profile_URI=me_URI;
           me_URI+="#me";           
		}
		 String  cert = "http://www.w3.org/ns/auth/cert#";
	     String  rdf  = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
		 String  foaf = "http://xmlns.com/foaf/0.1/";
		 String  dc   =  "http://purl.org/dc/elements/1.1/";

		Model m = ModelFactory.createDefaultModel();
		m.setNsPrefix("cert", cert);
		m.setNsPrefix("rdf", rdf);
		m.setNsPrefix("foaf", foaf);
		m.setNsPrefix("dc", dc);
		
		Property property_title=m.createProperty(dc+"title");
		Property property_foaf_maker=m.createProperty(foaf+"maker");
		Property property_foaf_name=m.createProperty(foaf+"name");
		Property property_foaf_primartTopic=m.createProperty(foaf+"primaryTopic");
		
		Property property_cert_key=m.createProperty(cert+"key");
		Property property_cert_exponent=m.createProperty(cert+"exponent");
		Property property_cert_modulus=m.createProperty(cert+"modulus");
		
		Resource profile  = m.createResource(profile_URI);
		Resource me  = m.createResource(me_URI);
		Resource foaf_PersonalProfileDocument  = m.createResource(foaf+"PersonalProfileDocument");
		Resource foaf_Person  = m.createResource(foaf+"Person");
		Resource cert_rsapublickey=m.createProperty(cert+"RSAPublicKey");	
		
		profile.addProperty(RDF.type,foaf_PersonalProfileDocument);
		profile.addProperty(property_title,data_store.getClient_certificate().getCommon_name()+"'s FOAF Profile");
		profile.addProperty(property_foaf_maker,me);
		profile.addProperty(property_foaf_primartTopic,me);
		
        me.addProperty(RDF.type,foaf_Person);
        me.addProperty(property_foaf_name, data_store.getClient_certificate().getCommon_name());
		Resource certificate_key = m.createResource();
		me.addProperty(property_cert_key, certificate_key);
		certificate_key.addProperty(RDF.type, cert_rsapublickey);
		
		
		String modulus=data_store.getClient_certificate().getModulus();
		String exponent=data_store.getClient_certificate().getExponent();
		
		if(modulus!=null)
		{
			Literal literal_modulus= m.createTypedLiteral(modulus,"http://www.w3.org/2001/XMLSchema#hexBinary");
			certificate_key.addProperty(property_cert_modulus,literal_modulus);
		}
		
		if(exponent!=null)
		{
			certificate_key.addProperty(property_cert_exponent,exponent);
		}

		//m.write(System.out,"TURTLE");
		ByteArrayOutputStream baos=new ByteArrayOutputStream();
		m.write(baos,"TURTLE");
		return baos.toString();
	}


}
