# orcid-api-client

A library to help your Java application use the ORCID API

# Summary

[ORCID][ORCID home] provides an [API][Orcid API] so authorized applications can read and modify an ORCID record, on behalf of the record's owner.

This library provides Java classes that wrap that API, and make it easier to write such an application.

# Usage
## Include in your application

### Obtain the JAXB-entity library from ORCID
__*TBD*__

### Build the client classes from the repository
* Clone the repository from [Github][Github repo]
* From the top-level directory in the repo, run `mvn install`

### If your application is based on maven:
Include this dependency in your `pom.xml`

```
<dependency>
	<groupId>edu.cornell.library</groupId>
	<artifactId>orcid-api-client</artifactId>
	<version>${project.version}</version>
</dependency>
```

### If your application is not based on maven:
The `mvn install` command created a JAR file here:

```
~/.m2/repository/edu/cornell/library/orcid-api-client/1.0-SNAPSHOT/orcid-api-client-1.0-SNAPSHOT.jar
```
Copy the JAR file into your application.

	
## Configuration

When your application starts up, initialize the `OrcidClientContext` class with a call like this one:

```
	private void initializeOrcidContext(Map<String, String> settings) {
		try {
			OrcidClientContext.initialize(new OrcidClientContextImpl(settings));
		} catch (OrcidClientException e) {
			log.error("Failed to initialize OrcidClientContent", e);
		}
	}
```

The settings map must contain values for these keys

| Key | Meaning | Example value |
| --- | --- | --- |
| `"API_PLATFORM"` | Should the client connect with the sandbox or with the production environment? Specify `"sandbox"` or `"production"`. Upper-case or lower-case. | `"sandbox"` |
| `"CLIENT_ID"` | The Client ID of your ORCID credentials. If the credentials are for the sandbox, you must specify `sandbox` as the platform, and similarly for production. | `"APP-3I45XXXX3H5ZZ1AA"` |
| `"CLIENT_SECRET"` | The Client Secret for your Client ID. | `"abcd1234-ffff-9999-abcd-99663311bbee"` |
| `"WEBAPP_BASE_URL"` | The URL where your application will be running. (used with `CALLBACK_PATH`, below). | `"http://localhost:8080/myOrcidClientApp"` |
| `"CALLBACK_PATH"` | When evaluated relative to `WEBAPP_BASE_URL`, provides a URL to be used as part of OAuth's "3-legged dance". The OAuth server will redirect the user's browser to this URL when they have logged in to ORCID. The handler at this URL must read the auth code from the server and trade it for an access token. | `"orcidHandler/callback"` |

## Examples of usage

This project includes a "test" webapp which is helpful when developing and/or maintaining the client. The "test" webapp also provides real-life illustrations of how the client is invoked.

Some relevant examples:

```
	// Utility method
	protected OrcidAuthorizationClient getAuthorizationClient()
			throws OrcidClientException {
		return new OrcidAuthorizationClient(
				OrcidClientContext.getInstance(),
				OauthProgressCacheImpl.instance(req),
				AccessTokenCacheSessionImpl.getInstance(req.getSession()), 
				new BaseHttpWrapper());
	}
	
	// Utility method
	protected OrcidActionClient getActionClient() {
		return new OrcidActionClient(
				OrcidClientContext.getInstance(), 
				new BaseHttpWrapper());
	}
```

```	
	// Redirect the user's browser into the 3-legged OAuth dance
	public void startTheOauthDance(ApiScope scope, HttpServletResponse resp) {
		OrcidAuthorizationClient authClient = getAuthorizationClient();
		OauthProgress progress = authClient.createProgressObject(scope,
				successUrl(), failureUrl(), deniedUrl());

		resp.sendRedirect(authClient.buildAuthorizationCall(progress));
	}
```

```	
	// Process the callback from the 3-legged OAuth dance
	public void startTheOauthDance(ApiScope scope, HttpServletRequest req, 
			HttpServletResponse resp) {
		OrcidAuthorizationClient authClient = getAuthorizationClient();
		String redirectUrl = authClient.processAuthorizationResponse(new ParameterMap(req));
		resp.sendRedirect(redirectUrl);
	}
```

```	
	// Read the external identifiers from the ORCID record
	public List<PersonExternalIdentifier> readExternalIdentifiers(AccessToken token) {
		Record record = getActionClient().createReadRecordAction().read(token);
		return record.getPerson().getExternalIdentifiers().getExternalIdentifiers();
	}
```

```	
	// Add an external identifier to the ORCID record
	public void addVivoId(String vivoLocalName, String vivoUrl) throws OrcidClientException {
		PersonExternalIdentifier externalId = new PersonExternalIdentifier();
		externalId.setType("VIVO Cornell");
		externalId.setValue(vivoLocalName);
		externalId.setUrl(new Url(vivoUrl));
		externalId.setVisibility(Visibility.PUBLIC);
		getActionClient.createEditExternalIdsAction().add(token, externalId);
	}
```

# Status

## API coverage

This driver does not wrap all of the available methods from the ORCID API.

API methods were wrapped as the need arose, for the various projects that used this driver (see "History" below).
The "AuthClient" section that deals with OAuth is complete. The "ActionClient" section is not complete, 
but the pattern should be clear. 

To add more functionality, add a method to `OrcidActionClient`. 
If you are adding a read function, create a new implementation of `AbstractReadAction`.
If you are adding a write/update function, you may be able to create a simple implementation
of `AbstractRecordElementEditAction`.

# History

## release 0.2, March 8, 2016

This library was created for use in two applications:

* An integration of ORCID with VIVO Cornell (vivo.cornell.edu), which allowed faculty to establish links between their VIVO profile pages and their ORCID records.
* An application which provided guided enrollment in ORCID for Cornell graduate students (orcid.cornell.edu), and allowed the students' departments to track their ORCID iDs.

Both of these applications have since been removed.

This release was also contributed to the Open Source [VIVO project][VIVO Orcid client].

## release 0.3, Feb 20, 2017

Changed the JSON engine to [Jackson][Jackson portal].

## additional development at VIVO

The VIVO team made additional changes, including 

* compatibility with release 2.1 of the ORCID API
* restructuring the library as a Maven artifact.

## release 1.0, September, 2018

In 2018, the decision was made to integrate Scholars@Cornell (scholars.cornell.edu) with ORCID. Faculty would be allowed to populate their ORCID records with the publications listed on their Scholars@Cornell profile page.

The approach would be different from the earlier VIVO integration: instead of VIVO calling the ORCID API, there would be a separate service called the [Scholars-ORCID Connection][SCORconn repo].

This resulted in a complete rewrite of the library, with little connection to the original releases, or to the 
work done by the VIVO team.

Salient features include:

* compatibility with release 2.1 of the ORCID API
* use of ORCID's library of [JAXB-based JAVA classes][ORCID JAXB library]

The Scholars@Cornell project was cancelled before this release could be deployed into production.

# More information
Some coding notes for developers are [here][Developer notes].



[ORCID home]: https://orcid.org
[Orcid API]: https://members.orcid.org/api/tutorial
[VIVO Orcid client]: https://github.com/vivo-project/orcid-api-client
[Github repo]: https://github.com/cul-it/orcid-api-client
[Jackson portal]: https://github.com/FasterXML/jackson
[SCORconn repo]: https://github.com/cul-it/scholars-orcid-connection
[ORCID JAXB library]: https://github.com/ORCID/orcid-conversion-lib/tree/master/orcid-model
[Developer notes]: ./doc/DeveloperNotes.md