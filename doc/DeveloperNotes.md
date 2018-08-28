# References:

* [The API reference in GitHub](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.1/README.md)

* [API Tutorials](https://members.orcid.org/api/tutorial)

Sources JAXB binding techniques:

* [JAXB bindings by example](https://coderleaf.wordpress.com/2016/11/15/jaxb-bindings-by-example/)
* [Customizing JAXB Bindings](https://docs.oracle.com/cd/E17802_01/webservices/webservices/docs/1.5/tutorial/doc/JAXBUsing4.html#wp148590)
* [Using JAXB Data Binding](https://docs.oracle.com/middleware/11119/wls/WSGET/data_types.htm)

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

# Using ORCIDs JAXB JAR file
## Orcid provides a JAR file
* They have created (somehow) a JAR file with JAXB-based classes. 
	* https://github.com/ORCID/orcid-conversion-lib/blob/master/orcid-model/orcid-model-2.1.jar
* They use this file themselves.
* Note that this one JAR file contains classes for several versions of the API
	* The versions are distinguished by package names -- double-check your `import` statements!


## Installing the JAR into the project
* Got a lot of info from [this stackoverflow topic](https://stackoverflow.com/questions/364114/can-i-add-jars-to-maven-2-build-classpath-without-installing-them)
* Deployed the JAR to the project:
	* download the JAR to `[project-directory]/api-client/lib` 
	* `cd` to a neutral directory (one that is not a maven project)
	* run these commands (substituting the path to the project directory):

```
mvn install:install-file \
  -DlocalRepositoryPath=[project-directory]/api-client/repo \
  -Dfile=[project-directory]/api-client/lib/orcid-model-2.1.jar \
  -DgroupId=org.orcid -DartifactId=orcid-model -Dversion=1.1.5-SNAPSHOT \
  -DcreateChecksum=true -Dpackaging=jar
```

So, on my machine, I ran these commands:

```
cd /Users/jeb228/Development/OrcidApiClient/projects
mvn install:install-file \
  -DlocalRepositoryPath=orcid-api-client/api-client/repo \
  -Dfile=orcid-api-client/api-client/lib/orcid-model-2.1.jar \
  -DgroupId=org.orcid -DartifactId=orcid-model -Dversion=1.1.5-SNAPSHOT \
  -DcreateChecksum=true -Dpackaging=jar
```

Note that the original JAR file, `orcid-api-client/api-client/lib/orcid-model-2.1.jar`, 
is not used after this.

## Creating the dependency
* After doing the installation, I can create the dependency, as seen in `pom.xml`
		
# Running the test webapp
* Might need to set up a tunnel.
	* If the credentials list a callback URL as "http://myhost", but the webapp runs at `htp://myhost:8080`, might need to set up a tunnel with something like this:

	```
	sudo ssh jeb228@localhost -L 80:loalhost:8080
	```
	
* Start it with `mvn jetty:run`
* Access log is found in `test-webapp/target` directory
* Logging goes to the console

