# The ORCID XSD files

* Most of the schema files came from [here](https://github.com/ORCID/ORCID-Source/tree/master/orcid-model/src/main/resources/record_2.0)

* The common schema file came from [here](https://github.com/ORCID/ORCID-Source/tree/master/orcid-model/src/main/resources/common_2.1)
	* There was no choice about where to put `common-2.1.xsd`, since the other XSD files use a relative URL to reference it:
	
		```
		<xs:import namespace="http://www.orcid.org/ns/common"
		schemaLocation="../common_2.1/common-2.1.xsd" />
		```

* The JAXB bindings file `jaxb_bindings_customization.xjb` helps to control the way that the XSD is 
compiled into Java classes.


		
