# marketo-rest-client
A Java client library for [Marketo Lead Database REST API] (http://developers.marketo.com/rest-api/lead-database/).

## Features
This library can be used as a Java Client of Marketo Lead Database REST API. It supports querying following objects
* Leads
* Activities
* Opportunities
* Opportunity Roles
* Companies
* Sales Persons
* Named Accounts
* Custom Objects
This library requires following parameters 
* Marketo Client Id
* Marketo Client Secret
* Marketo Base URI

Follow the steps provided in [Marketo Quick Start Guide] (http://developers.marketo.com/blog/quick-start-guide-for-marketo-rest-api/) to get values for the above listed parameters

Marketo Base URI has to be specified with /rest. i.e it should be like https://119-AAA-888.mktorest.com

### Maven Dependency
```
<dependency>
    <groupId>com.springml</groupId>
    <artifactId>marketo-rest-client</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Code snippet to query single lead based on Id
```java
import com.springml.marketo.rest.client.MarketoClientFactory;
import com.springml.marketo.rest.client.LeadDatabaseClient;

LeadDatabaseClient leadDatabaseClient = MarketoClientFactory.getLeadDatabaseClient(CLIENT_ID, CLIENT_SECRET, MARKETO_BASEURI);
// Here we are fetching leads with Id 10
QueryResult queryResult = leadDatabaseClient.query("leads", "Id", "10")
// getResult() will return List. We are getting the first record assuming that "leads" with Id "10" exist in Marketo
// Key of the record is the field name
// Value of the record is the corresponding field value 
Map<String, String> record = queryResult.getResult().get(0);

```

### Code snippet to Lead Changes Activities
```java
import com.springml.marketo.rest.client.MarketoClientFactory;
import com.springml.marketo.rest.client.LeadDatabaseClient;

LeadDatabaseClient leadDatabaseClient = MarketoClientFactory.getLeadDatabaseClient(CLIENT_ID, CLIENT_SECRET, MARKETO_BASEURI);
// Here we are querying Lead Changes starting from 2016-10-06 and the changes done in firstName field
List<Map<String, String>> leadChangesActivities = leadDatabaseClient.getLeadChangesActivites("2016-10-06", "firstName")

```