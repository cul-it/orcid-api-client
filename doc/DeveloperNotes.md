# References:

* [The API reference in GitHub](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.1/README.md)

* [API Tutorials](https://members.orcid.org/api/tutorial)

Sources JAXB binding techniques:

* [JAXB bindings by example](https://coderleaf.wordpress.com/2016/11/15/jaxb-bindings-by-example/)
* [Customizing JAXB Bindings](https://docs.oracle.com/cd/E17802_01/webservices/webservices/docs/1.5/tutorial/doc/JAXBUsing4.html#wp148590)
* [Using JAXB Data Binding](https://docs.oracle.com/middleware/11119/wls/WSGET/data_types.htm)

# Structure

Running `mvn install` will produce three artifacts in your repository:

* The project artifact: `orcid-api-client-project`
* 
* The client artifact: `orcid-api-client`
* The test webapp artifact: `orcid-api-client-test-webapp`
* 

# Using JAXB to compile the XML Schemas

* Most of the schema files came from [here](https://github.com/ORCID/ORCID-Source/tree/master/orcid-model/src/main/resources/record_2.0)

* The common schema file came from [here](https://github.com/ORCID/ORCID-Source/tree/master/orcid-model/src/main/resources/common_2.1)
	* There was no choice about where to put `common-2.1.xsd`, since the other XSD files use a relative URL to reference it:
	
		```
		<xs:import namespace="http://www.orcid.org/ns/common"
		schemaLocation="../common_2.1/common-2.1.xsd" />
		```

* The JAXB bindings file `jaxb_bindings_customization.xjb` helps to control the way that the XSD is 
compiled into Java classes.
	* A package name is assigned for each schema file.
	* Generated element types are given the `Element` suffix, in order to avoid name collisions.
	* Property names on the `OrcidId` type are modified to remove a collision.

## JAXB and the schemas
* __*TBD*__
* They come from here: [the record directory](https://github.com/ORCID/ORCID-Source/tree/master/orcid-model/src/main/resources/record_2.1) and [the common directory](https://github.com/ORCID/ORCID-Source/tree/master/orcid-model/src/main/resources/common_2.1)
		
# Running the test webapp
* Might need to set up a tunnel.
	* If the credentials list a callback URL as "http://myhost", but the webapp runs at `http://myhost:8080`, might need to set up a tunnel with something like this:

	```
	sudo ssh jeb228@localhost -L 80:loalhost:8080
	```
	
* Start it with `mvn jetty:run`
* Access log is found in `test-webapp/target` directory
* Logging goes to the console

