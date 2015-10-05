###The main classes in the project to check what I've developed are:

**org / dynamicloud / api / DynamicSessionImpl**<br>
**org / dynamicloud / api / DynamiCloudUtil**<br>
**org / dynamicloud / api / annotation/Bind**<br>
**org / dynamicloud / api / RecordQuery**<br>
**org / dynamicloud / net / http/RecordClientImpl**<br>
**org / dynamicloud / service / ServiceCallerImpl**<br>
**The classes in package org / dynamiclud / api / criteria /*.***

###Now, I want to explain a little about this sample code

I'm currently working on the way to launch a Website where developer, testers and little sites could store data in cloud.  The whole data in this site will be available throught **_API (Java and Ruby)_** and the developer will be able to access it with API keys.

The heart of this service are **Models** and **Fields**.

A **Model** is a structure that defines something in real world.  That definition is a set of **Fields** that promote the features in a model.

Additional, this service provides a way to fetch data related to **Models** that we called **Records**.  The available services will return a well formed JSON.

Likewise, the services follow a Rest arquitecture, so you will be able to call CRUD operations using the Http METHODS **(GET, POST, DELETE and PUT)**.

The website has all the documentation about how to operate with these services, but we offer APIs that ease the path to meet your goal.

# Java API

###**Main concepts in this API:**

**DynamicSession & RecordQuery**
They are the main objects that contain all the necessary methods to execute CRUD operations

**RecordModel**
Is a simple bean with the ModelID, Name and description of a Model. 

**RecordCredential**
This object must contain the API keys

**BoundInstance**
Is a interface with specific declared methods.

###How to fetch the current records of a Model:
```Java
try {
  DynamicSession session = DynamicSession.Impl.getInstance(new RecordCredential(CSK, ACI));
  Quey<ModelFields> query = session.createQuery(new RecordModel(14L, ModelFields.class));

  RecordResults<ModelFields> results = query.add(Conditions.like("name", "%eleaz%")).list();
  System.out.println("results.getRecords().get(0).getRecordId() = " + results.getRecords().get(0).getRecordId());

  results = query.next();
  System.out.println("results.getRecords().get(0).getRecordId() = " + results.getRecords().get(0).getRecordId());
} catch (DynamiCloudSessionException e) {
  //ignore this exception
}
```

**Explanation of this piece of code:**

This line instantates the main object to execute operations, we have to pass the credentials to gain access to the servers.<br>
```Java 
DynamicSession session = DynamicSession.Impl.getInstance(new RecordCredential(CSK, ACI));
```

This line builds a query that will fetch records related to ModelID(14) and the returned records will be bind to ModelFields objects.<br>
```Java 
Quey<ModelFields> query = session.createQuery(new RecordModel(14L, ModelFields.class));
```

This line adds a criteria to filter records. In this case, we will fetch the records where the field 'name' contains the word 'eleaz'
```Java 
RecordResults<ModelFields> results = query.add(Conditions.like("name", "%eleaz%")).list();
```

Finally, we have the method next(). This method will execute the same operation, but with an offset that will be incremented by a count previously settled, this example doesn't have that operation and the default count to increment the offset is 15.

####What it means a BoundIntance?

A BoundInstace is an interface that declares getRecordId and setRecordId.  Every record must be bound to one BoundIntance in your code.

###How to bind the fields in a model with your BoundIntace?

This API has an Annotation called `@Bind`.  This annotation bind a field with a **set method**, for example:

```Java 
@Bind(field = "username")
public void setUsername(String username) {
  this.username = username;
}
```
Those four lines of code above bind the method `setUsername` with the field `username` in the **Model**.  So, when you execute a query, all of these bound attributes will be filled.
