#Secured Linked Data
##The project can be complied using the following command:

git clone https://github.com/jyrkio/SecuredLinkedData.git
cd drumbeat_security
mvn clean install

## #xample Tomcat 9 Server.xml connector setting

- The certificateVerification="optional" is for the client certificate 

 <Connector port="80" protocol="HTTP/1.1"
               connectionTimeout="20000"
               redirectPort="443" />

    
   	<Connector port="443" protocol="org.apache.coyote.http11.Http11NioProtocol"
			maxThreads="150" SSLEnabled="true">
			<SSLHostConfig truststoreFile="c:\jo\keystore.jks"
				truststorePass="tomcat" certificateVerification="optional">
				<Certificate certificateKeystoreFile="c:\jo\keystore.jks"
					certificateKeystorePassword="password" certificateKeyAlias="local.org"
					type="RSA" />
			</SSLHostConfig>
		</Connector>


## The WenID Creator
![User interface](https://github.com/jyrkio/LinkedDataAuthorization/blob/master/drumbeat_security_webid_creator/FOAF_SSL_Creator.png)


