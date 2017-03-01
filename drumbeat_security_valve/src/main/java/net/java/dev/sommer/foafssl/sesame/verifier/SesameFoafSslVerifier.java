/*
New BSD license: http://opensource.org/licenses/bsd-license.php

Copyright (c) 2008-2009 Sun Microsystems, Inc.
901 San Antonio Road, Palo Alto, CA 94303 USA. 
All rights reserved.


Redistribution and use in source and binary forms, with or without 
modification, are permitted provided that the following conditions are met:

- Redistributions of source code must retain the above copyright notice, 
this list of conditions and the following disclaimer.
- Redistributions in binary form must reproduce the above copyright notice, 
this list of conditions and the following disclaimer in the documentation 
and/or other materials provided with the distribution.
- Neither the name of Sun Microsystems, Inc. nor the names of its contributors
may be used to endorse or promote products derived from this software 
without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE 
LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
POSSIBILITY OF SUCH DAMAGE.
 */
package net.java.dev.sommer.foafssl.sesame.verifier;

import java.math.BigInteger;
import java.security.PublicKey;
import java.security.interfaces.DSAPublicKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.query.Binding;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.sail.SailRepositoryConnection;

import net.java.dev.sommer.foafssl.claims.WebIdClaim;
import net.java.dev.sommer.foafssl.sesame.cache.GraphCache;
import net.java.dev.sommer.foafssl.sesame.cache.GraphCacheLookup;
import net.java.dev.sommer.foafssl.sesame.cache.MemoryGraphCache;
import net.java.dev.sommer.foafssl.verifier.FoafSslVerifier;


/**
 * This class verifies FOAF+SSL certificates by dereferencing the FOAF file at
 * the given Web ID URI.
 * 
 * @author Henry Story.
 * @author Bruno Harbulot.
 */
public class SesameFoafSslVerifier extends FoafSslVerifier {
    final static String cert = "http://www.w3.org/ns/auth/cert#";
    final static String xsd = "http://www.w3.org/2001/XMLSchema#";


       //WRONG! this should not be done here, but in a startup file
   static {
      try {
         GraphCacheLookup.setCache(new MemoryGraphCache());
      } catch (Exception ex) {
         Logger.getLogger(GraphCache.class.getName()).log(Level.SEVERE, null, ex);
      }
   }


    //static final transient Logger log = Logger.getLogger(SesameFoafSslVerifier.class.getName());
    //Changed by JO 25.8.2016
    private static final Log log = LogFactory.getLog(SesameFoafSslVerifier.class);

    
   @Override
    public boolean verify(WebIdClaim webid) {
	   log.info("DRUMBEAT Sesame Validator started!");
   
        GraphCache cache = GraphCacheLookup.getCache();

        // do a check that this is indeed a URL first
        SailRepositoryConnection rep = cache.fetch(webid);
        if (rep == null)
            return false;
         for(Throwable t: webid.getProblems())
        	 log.info("--- DRUMBEAT webid problem: "+t.getMessage());
        PublicKey publicKey = webid.getVerifiedPublicKey();

        if (publicKey instanceof RSAPublicKey) {
            RSAPublicKey certRsakey = (RSAPublicKey) publicKey;
            log.info("--- DRUMBEAT webid: cert public exp:"+certRsakey.getPublicExponent());
            log.info("--- DRUMBEAT webid: cert public mod:"+certRsakey.getModulus());
            
            
            TupleQuery query = null;
            try {
            	String qstring="PREFIX cert: <http://www.w3.org/ns/auth/cert#>"            							      
                        + "PREFIX rsa: <http://www.w3.org/ns/auth/rsa#>"
                        + "SELECT ?m ?e ?mod ?exp "
                        + "FROM <"+webid.getGraphName().toString()+">"
                        + "WHERE { "
                        + " { ?key cert:identity ?agent } "
                        + " UNION "
                        + " { ?agent cert:key ?key }"
                        + "  ?key cert:modulus ?m ;"
                        + "       cert:exponent ?e ."
                        + "   OPTIONAL { ?m cert:hex ?mod . }"
                        + "   OPTIONAL { ?e cert:decimal ?exp . }"
                        + "}";
                query = rep.prepareTupleQuery(QueryLanguage.SPARQL,qstring
                        );
                
                log.info("DRUMBEAT Sesame Validator: qstring : "+qstring);
            } catch (Exception e) { // MalformedQuery
            	log.fatal( "Error in Query String!", e);
            	e.printStackTrace();
                webid.fail("SERVER ERROR - Please warn administrator");
                return false;
            } /*catch (RepositoryException e) {
            	log.fatal("Error with repository", e);
                webid.fail("SERVER ERROR - Please warn administrator");
                return false;
            }*/

            ValueFactory vf = rep.getValueFactory();
            query.setBinding("agent", vf.createURI(webid.getWebId().toString()));
            
            log.info("DRUMBEAT Sesame Validator: agent : "+webid.getWebId().toString());
            TupleQueryResult answer = null;
            try {
                answer = query.evaluate();
            } catch (Exception e) {
            	log.fatal("Error evaluating Query", e);
            	e.printStackTrace();
                webid.fail("SERVER ERROR - Please warn administrator");
                return false;
            }
            try {
				log.info("DRUMBEAT Sesame Validator: answer has next: "+answer.hasNext());
			} catch (Exception e1) {				
				e1.printStackTrace();
			}
            try {
                while (answer.hasNext()) {
                    BindingSet bindingSet = answer.next();
                    log.info("DRUMBEAT Sesame Validator: bindingSet: "+bindingSet.toString());


                    
                    // 1. find the exponent
                    BigInteger exp = toInteger(bindingSet.getBinding("e"), cert + "decimal",
                            bindingSet.getBinding("exp"));
                    if (exp == null || !exp.equals(certRsakey.getPublicExponent())) {
                    	log.info("DRUMBEAT Sesame Validator: exp not correcct");
                    	log.info("DRUMBEAT Sesame Validator: exp:"+exp);
                    	log.info("DRUMBEAT Sesame Validator: cert exp:"+certRsakey.getPublicExponent());
                        continue;
                    }

                    // 2. Find the modulus
                    BigInteger mod = toInteger(bindingSet.getBinding("m"), cert + "hex", bindingSet
                            .getBinding("mod"));
                    if (mod == null || !mod.equals(certRsakey.getModulus())) {
                    	log.info("DRUMBEAT Sesame Validator: mod not correcct");
                    	log.info("DRUMBEAT Sesame Validator: mod:"+mod);
                    	log.info("DRUMBEAT Sesame Validator: cert mode:"+certRsakey.getModulus());
                        continue;
                    }
                    log.info("DRUMBEAT Sesame Validator: success!");
                    // success!
                    return true;
                }
            } catch (Exception e) {
            	log.fatal( "Error accessing query results", e);
            	e.printStackTrace();
                webid.fail("SERVER ERROR - Please warn administrator");
                return false;
            }
        } else if (publicKey instanceof DSAPublicKey) {
        	log.info("DRUMBEAT Sesame Validator: DSAPublicKey not handled");
        } else {
            // what else ?
        	log.info("DRUMBEAT Sesame Validator: Some other publicKey type not handled");
        }
        
        return false;
    }

    /**
     * Transform an RDF representation of a number into a BigInteger
     * <p/>
     * Passes a statement as two bindings and the relation between them. The
     * subject is the number. If num is already a literal number, that is
     * returned, otherwise if enough information from the relation to optstr
     * exists, that is used.
     * 
     * @param num
     *            the number node
     * @param optRel
     *            name of the relation to the literal
     * @param optstr
     *            the literal representation if it exists
     * @return the big integer that num represents, or null if undetermined
     */
    static BigInteger toInteger(Binding num, String optRel, Binding optstr) {
    	log.info("DRUMBEAT Sesame toIntegerHelper num:"+num.getValue().stringValue());
        if (null == num)
            return null;
        Value numVal = num.getValue();
        if (numVal instanceof Literal) { // we do in fact have "ddd"^^type
        	log.info("DRUMBEAT Sesame toIntegerHelper literal!");
            Literal ln = (Literal) numVal;
            String type = ln.getDatatype().toString();
            return toInteger_helper(ln.getLabel(), type);
        } else if (numVal instanceof Resource) { // we had _:n type "ddd" .
            Value strVal = optstr.getValue();
            if (strVal != null && strVal instanceof Literal) {
                Literal ls = (Literal) strVal;
                return toInteger_helper(ls.getLabel(), optRel);
            }
        }
        return null;
    }

    /**
     * This transforms a literal into a number if possible ie, it returns the
     * BigInteger of "ddd"^^type
     * 
     * @param num
     *            the string representation of the number
     * @param tpe
     *            the type of the string representation
     * @return the number
     */
    private static BigInteger toInteger_helper(String num, String tpe) {
        if (tpe.equals(cert + "decimal") || tpe.equals(cert + "int")
                || tpe.equals(xsd + "integer") || tpe.equals(xsd + "int")
                || tpe.equals(xsd + "nonNegativeInteger")) {
            // cert:decimal is deprecated
            return new BigInteger(num.trim(), 10);
        } else if (tpe.equals(cert + "hex")) {
            String strval = cleanHex(num);
            return new BigInteger(strval, 16);
        }
        // new name JO
        else if (tpe.equals(xsd + "hexBinary")) {
                String strval = cleanHex(num);
                return new BigInteger(strval, 16);
            } else 
        {
            // it could be some other encoding - one should really write a
            // special literal transformation class
        }
        return null;
    }

    static final private char[] hexchars = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A',
            'a', 'B', 'b', 'C', 'c', 'D', 'd', 'E', 'e', 'F', 'f' };

    static {
        Arrays.sort(hexchars);
    }

    /**
     * This takes any string and returns in order only those characters that are
     * part of a hex string
     * 
     * @param strval
     *            any string
     * @return a pure hex string
     */

    private static String cleanHex(String strval) {
        StringBuffer cleanval = new StringBuffer();
        for (char c : strval.toCharArray()) {
            if (Arrays.binarySearch(hexchars, c) >= 0) {
                cleanval.append(c);
            }
        }
        return cleanval.toString();
    }

}
