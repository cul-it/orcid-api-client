# References:

* [The API reference in GitHub](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.1/README.md)

* [API Tutorials](https://members.orcid.org/api/tutorial)

# Structure

Running `mvn install` will produce three artifacts in your repository:

* The project artifact: `orcid-api-client-project`
* The client artifact: `orcid-api-client`
* The test webapp artifact: `orcid-api-client-test-webapp`

# Using ORCIDs JAXB JAR file
## Orcid provides a JAR file

* They have created (somehow) a JAR file with JAXB-based classes. 
	* [Read all about it.](https://github.com/ORCID/orcid-conversion-lib/blob/master/orcid-model)
	* [Get the JAR file.](https://github.com/ORCID/orcid-conversion-lib/blob/master/orcid-model/orcid-model-2.1.jar)
* They use this file themselves.
* Note that this one JAR file contains classes for several versions of the API
	* The versions are distinguished by package names -- double-check your `import` statements!

## Installing the JAR into the project
* Perhaps the best way would have been to install it in the local repo, 
  and then use `maven-assembly-plugin` to include the classes in the distribution of `oric-api-client`.
  I haven't taken the time to do this.
  
I Instead, the process that I have documented is how to install this JAR as an artifact in the 
local repository. This doesn't handle the transitive dependency for the application that uses
`orcid-api-client`, but the workaround is not difficult.

## What did I do?

* Got a lot of info from [this stackoverflow topic](https://stackoverflow.com/questions/364114/can-i-add-jars-to-maven-2-build-classpath-without-installing-them)

* Deployed the JAR to the project:
	* download the JAR to `[project-directory]/api-client/lib` 

* Included instructions on installing this JAR to your local repository.

Note that the original JAR file, `[project-directory]/api-client/lib/orcid-model-2.1.jar`, 
is not used after this.

## Creating the dependency
* After doing the installation, I can create the dependency, as seen in `pom.xml`
		
# Running the test webapp
* Might need to set up a tunnel.
	* If the credentials list a callback URL as "http://myhost", but the webapp runs at `http://myhost:8080`, might need to set up a tunnel with something like this:

	```
	sudo ssh jeb228@localhost -L 80:loalhost:8080
	```
	
* Start it with `mvn jetty:run`
* Access log is found in `test-webapp/target` directory
* Logging goes to the console

