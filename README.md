# Welcome to the [Liberty app accelerator](https://liberty-app-accelerator.wasdev.developer.ibm.com/start/) [![Build Status](https://travis-ci.org/WASdev/tool.accelerate.core.svg?branch=master)](https://travis-ci.org/WASdev/tool.accelerate.core)
An application builder to allow you to construct starter Java applications that run on [WebSphere Liberty](https://developer.ibm.com/wasdev/).

## Table of Contents
* [Summary](#summary)
* [Technology Options](#technology-options)
* [Running the Generated App](#running-the-generated-app)
* [Contributing to app accelerator](#contributing-to-app-accelerator)
  * [Building and Running](#building-and-running)
  * [Project Structure](#project-structure)
  * [Adding Technology Options](#adding-technology-options)
  * [User Interface](#user-interface)
  * [Application Generation](#application-generation)
  * [Testing Create on GitHub Capability](#testing-create-on-github-capability)

## Summary
Liberty app accelerator is constantly being developed and we will be adding new technologies and capabilities to the page over time.

Try out app accelerator [here](http://liberty-starter.wasdev.developer.ibm.com/start/) and see how quickly you can have a fully fledged application.

If you have any questions, comments or suggestions please raise an [issue](https://github.com/WASdev/tool.accelerate.core/issues) on our GitHub repository.

For an introduction see our [blog post](https://developer.ibm.com/wasdev/blog/2015/11/27/introducing-the-liberty-starter-a-tool-to-get-you-writing-microservices-quickly/). To learn how the Liberty Starter can be used to create the [image service](https://developer.ibm.com/wasdev/docs/an-evolved-plants-by-websphere-image-service-making-the-break/) for the [Plants by WebSphere evolution](https://developer.ibm.com/wasdev/docs/starting-evolution-microservices-using-plants-websphere-sample/) from monolith to microservice, see this [WASdev article](https://developer.ibm.com/wasdev/docs/creating-image-service-plants-websphere-using-liberty-starter).

## Technology Options

The current technologies you can choose to build into your application are:
* [Rest](#rest)
* [Servlet](#servlet)
* [Spring Boot with Spring REST](#spring-boot-with-spring-rest)
* [Websockets](#websockets)
* [Persistence](#persistence)
* [Watson SDK](#watson-sdk)
* [MicroProfile](#microprofile)
* [Microservice Builder](#microservice-builder)
* [Swagger](#swagger)

### Rest
This provides you with the jaxrs-2.0 feature and the jsonp-1.0 feature.

Inside the project produced there is a application.rest package containing the <code>LibertyRestEndpoint</code> class. This adds a REST endpoint which you can access at /rest. There is also a test class named <code>it.rest.LibertyRestEndpointTest</code> that will test the REST endpoint to ensure it is working.

For the complete feature documentation, see the [jaxrs-2.0](http://www.ibm.com/support/knowledgecenter/en/SSEQTP_liberty/com.ibm.websphere.wlp.nd.multiplatform.doc/ae/rwlp_feature_jaxrs-2.0.html) and [jsonp-1.0](https://www.ibm.com/support/knowledgecenter/en/SSEQTP_liberty/com.ibm.websphere.liberty.autogen.nd.doc/ae/rwlp_feature_jsonp-1.0.html) feature descriptions in IBM Knowledge Center.

### Servlet
This provides you with the servlet-3.1 feature.

Inside the project there is a application.servlet package containing the <code>LibertyServlet</code> class. This adds a servlet with an endpoint which you can access at /servlet. There is also a test class named <code>it.servlet.LibertyServletTest</code> that will test the servlet's endpoint to ensure it is working.

For the complete feature documentation, see the [servlet-3.1](http://www.ibm.com/support/knowledgecenter/en/SSEQTP_liberty/com.ibm.websphere.wlp.nd.multiplatform.doc/ae/rwlp_feature_servlet-3.1.html) feature description in IBM Knowledge Center.

### Spring Boot with Spring REST
This provides you with a SpringBoot application that will run on WebSphere Liberty.

Inside the project there is a application.springboot.web package containing two classes:
* SpringBootLibertyApplication</code>: The entry point for the SpringBoot application.
* LibertyHelloController</code>: A Spring REST endpoint which you can access at /springbootweb.

There is also a test class named <code>it.springboot.web.HelloControllerTest</code> that will test the Spring REST endpoint to ensure it is working.

### Websockets
This provides you with the websocket-1.1 feature.

For the complete feature documentation, see the [websocket-1.1](http://www.ibm.com/support/knowledgecenter/en/SSEQTP_liberty/com.ibm.websphere.wlp.nd.multiplatform.doc/ae/rwlp_feature_websocket-1.1.html) feature description in IBM Knowledge Center.

### Persistence
This provides you with jpa-2.1. For the complete feature documentation, see the [jpa-2.1](http://www.ibm.com/support/knowledgecenter/en/SSEQTP_liberty/com.ibm.websphere.wlp.nd.multiplatform.doc/ae/rwlp_feature_jpa-2.1.html) feature description in IBM Knowledge Center.

### Watson SDK
The Watson SDK provides an API for accessing Watson Services. For the complete documentation, take a look at <a href="https://developer.ibm.com/watson/">the Watson developer pages</a> and <a href="https://github.com/watson-developer-cloud/java-sdk">the Watson SDK github project</a>.

### MicroProfile
The [MicroProfile project](http://microprofile.io/) is an open
community with the aim of optimizing Enterprise Java for a microservices
architecture.  MicroProfile will be evolving with guidance from the community.

If you want to share your thoughts you can post straight to the
[MicroProfile Google group](https://groups.google.com/forum/#!forum/microprofile).

For the complete feature documentation, see the [microProfile-1.0 feature](http://www.ibm.com/support/knowledgecenter/en/SSEQTP_liberty/com.ibm.websphere.wlp.nd.multiplatform.doc/ae/rwlp_feature_microProfile-1.0.html) feature description in IBM Knowledge Center.

### Microservice Builder
This provides support for the technologies required by the [Microservice Builder solution](https://microservicebuilder.mybluemix.net/docs/index.html).

Microservice Builder delivers a turnkey solution incorporating a runtime, tooling, DevOps, fabric, and customer-managed container orchestration.

### Swagger
Swagger is a simple yet powerful representation of RESTful APIs.

This provides you with the apiDiscovery-1.0 feature, which allows you to discover REST APIs that are available on the Liberty server and then invoke the found REST endpoints using the Swagger user interface.

You can also easily expose the REST endpoints available from your web modules running on Liberty server by documenting the endpoints using the Swagger 2.0 Specification.

It is also possible to follow a design-first approach by creating the Swagger documentation first and then generating the server code from it.

For the complete feature documentation, see the [apiDiscovery-1.0](http://www.ibm.com/support/knowledgecenter/en/SSEQTP_liberty/com.ibm.websphere.wlp.nd.multiplatform.doc/ae/rwlp_feature_apiDiscovery-1.0.html) feature description in IBM Knowledge Center.

## Running the Generated App

### Build command

If you have chosen to generate the application as a [Maven](https://maven.apache.org/) project run:

```
mvn install
```

If you have chosen to generate the application as a [Gradle](https://gradle.org/) project run:

```
gradle build
```

### Running Locally

Using [Maven](https://maven.apache.org/): `mvn liberty:run-server`

Using [Gradle](https://gradle.org/): `gradle libertyStart`

The application can be accessed at http://localhost:9080/mylibertyApp

### Deploying to IBM Cloud

To deploy an application to IBM Cloud you first need an [IBM Cloud account](https://console.eu-gb.bluemix.net/). Once you have created an IBM Cloud account you can build and deploy your application.

Using Maven:

```
mvn install -Dcf.org=[your email address] -Dcf.username=[your username] -Dcf.password=[your password]
```

Using Gradle:

```
gradle build cfPush -PcfOrg=[your email address] -PcfUsername=[your username] -PcfPassword=[your password]
```
Where `cf.org` is the IBM Cloud organization you want to deploy to and `cf.username` and `cf.password` are your credentials for IBM Cloud. Once the build has been run see your command line output to find the endpoint for your application or look for it in the IBM Cloud dashboard.

You can optionally supply the following IBM Cloud configurations in the command line or in the top level pom.xml:
* <cf.context>eu-gb.mybluemix.net</cf.context>
* <cf.target>https://api.eu-gb.bluemix.net</cf.target>
* <cf.space>dev</cf.space>
* <cf.context.root>${cf.host}.${cf.context}/${warContext}</cf.context.root>

## Contributing to app accelerator

To contribute new features to app accelerator, either fork this GitHub repo or create a new branch. Make your changes then create a pull request back into the project. A member of the WASdev team will review your request.

### Building and Running

The app accelerator project is built using [Gradle](https://gradle.org/).

Build the application using: `gradle clean build`

Run the application: `gradle liberty-starter-application:localRun`

The application should be available at http://localhost:9082/start.

To run the application locally with it calling bx codegen:
`gradle liberty-starter-application:libertyStart -PappAccelStarterkit=<app accelerator starter kit url> -PbxCodegenEndpoint=<url for the bx codegen service to use>`

### Project Structure
The project is split up into several different pieces.

* `liberty-starter-application` contains the code to build the main application including the code for the UI
* `liberty-starter-common` contains common api code for the projects
* `liberty-filter-application` is a simple war that redirects people to the context `/start`
* `liberty-starter-wlpcfg` contains the Liberty usr directory where the logs and apps for the app accelerator will be put after a build
* `liberty-starter-test` contains a test microservice used during the testing phase

There are then a set of `starter-microservice-techId` projects that contain the code for the individual technology types.

### Adding Technology Options
To see an example of everything you can include in a technology see the starter-microservice-test project. This is the example project that is used to test the main piece of the app against.

#### Create a technology from the template service

1. Copy the contents of the `starter-microservice-template` directory into a new directory. The convention is that the last part of the name is related to the technology. So, if you were creating a technology based on `SuperTech` then the directory would be `starter-microservice-supertech`. (SuperTech will be the name used for the rest of these instructions).

2. Update the id's and context root in the `build.gradle` file. The context root would change to `/supertech`, the id in the `installAllPoms` task would be `supertech` and the id in the `fvt` task would be `starter-microservice-supertech`.

2. Change the group ID values in the POM files under `starter-microservice-supertech/repository/0.0.1`, compile-pom.xml, provided-pom.xml and runtime-pom.xml. `<groupId>net.wasdev.wlp.starters.template</groupId>` becomes `<groupId>net.wasdev.wlp.starters.supertech</groupId>`.

3. Refactor the packages and classes under src to SuperTech i.e. `starter-microservice-supertech/src/main/java/com/ibm/liberty/starter/service/template` becomes `starter-microservice-supertech/src/main/java/com/ibm/liberty/starter/service/supertech`

4. Change the `GROUP_SUFFIX` constant in the `ProviderEndpoint` class to `supertech`.

6. Change the value of `context-root` in `src/main/webapp/WEB-INF/ibm-web-ext.xml` to `supertech`.

7. Edit `src/main/webapp/WEB-INF/classes/description.html` to tell everyone about how SuperTech works and it's benefits.

8. (If you don't want to provide sample code for your technology type skip to step 10). Put the application sample code into `src/main/webapp/sample`.

9. Put the Liberty configuration for the sample application into `src/main/webapp/sample`.

10. Change the tests package to `src/test/java/com/ibm/liberty/starter/service/supertech/api/v1/it` and then the test classes to expect the correct responses for SuperTech.

#### Configuring a new technology
1. In `liberty-starter-application/src/main/resources` update the services.json file to add your new technology, including an id, name, description and the endpoint you want to use. This will add your technology as an option on the main page.
  1. By convention the id should be `supertech` and the endpoint should be `/supertech`.
  1. The name and description are used in the UI to give the user information about the technology type.

2. In the settings.gradle file add `starter-microservice-supertech` to the `include` list. This will add your project into the build lifecycle.

2. In the build.gradle file in `liberty-starter-application` in the last set of `dependsOn` commands add your technology to the `war.dependsOn` list. You need to add `:nameOfYourProject:publishWar`. This will make sure your project is built before the `liberty-starter-application` project.

3. In `liberty-starter-wlpcfg/servers/StarterServer/server.xml` add your application to the list. You need to provide the name of the war file being created in location, the context-root that matches the endpoint specified in the `services.json` file in step 1 and the id you specified in step 1.

If you run `gradle clean build` your new project should now be built and the war should be put into the apps directory of your server.

### User interface

The code for the UI is under `liberty-starter-application/src/main/webapp`. It is built using [AngularJS](https://angularjs.org/). The content is defined in html files and then controlled using javascript files.

There are three top level html files:
* `index.html` is the main page for app accelerator
* `wdt.html` provides content for the 'deploying to WDT' [instruction page](https://liberty-app-accelerator.wasdev.developer.ibm.com/start/wdt.html)
* `plugin.html` is a test html page for allowing developers an easy way to extend app acceleratot and is not currently linked from the main page

There are three html files in `webap/includes` that build up the key pieces of the UI:
* `technologies.html` represents Step 1 of the app accelerator UI
* `download.html` represents Step 2 of the app accelerator UI
* `footer.html` provides the footer for the web pages

The `js` directory contains the javascript that controls the Angular tags in the html. The javascript files are split into controllers, directives and services.

* `appCtrl.js` controller provides the core functionality that controls which parts of the html is shown at any one time.

* `appacc.js` service handles calls to the app accelerator backend and stores the users selections as they move through the page.

* `ga.js` service passes information to Google Analytics for processing.

* `techoptions.js` directive enables a specific technology to provide additional options. Thw Swagger technology type is one example of this.

The `/webapp/options` directory contains additional html and js files for technologies that require additional options. For example `options/swagger` provides html and javascript for the buttons to allow a user to upload a swagger.yaml file.

### Application Generation

Application generation is performed by the classes in `liberty-starter-application/src/main/java/com/ibm/liberty/starter`.

Applications are generated following calls to either the `LibertyTechnologySelector` or `GitHubProjectEndpoint` APIs. When creating in GitHub the actual generation is invoked when the `GitHubCallback` class is invoked.

Input validation is performed using the `ProjectContructionInput`, `ProjectContructionInputData` and `PatternValidation` classes.

The `ProjectConstructor` class contains the core generation logic.

Template files are located in `liberty-starter-application/skeletions`. The files in `base` are copied straight into the generated application. Files from the technology microservices are also copied directly into the generated application.

Files in `skeletons/specialFiles` are processed before being written out. Gradle files are processed using commands in `liberty-starter-application/src/main/java/com/ibm/liberty/starter/build/gradle` and Maven files are processed using commands in `liberty-starter-application/src/main/java/com/ibm/liberty/starter/build/maven`.

### Testing Create on GitHub Capability

The app accelerator provides an option for users to create their application in GitHub. To test this capability locally you will be required to register the application with GitHub and provide the client id and secret as well as an extra app accelerator secret (for signing the state sent to GitHub) as environment variables.

To do this go to [GitHub's OAuth applications page](https://github.com/settings/developers). Register your application with the following settings:

 * Application Name: Anything you want
 * Homepage URL: `http://localhost:9082/start`
 * Application description: Anything you want
 * Authorization callback URL: `http://localhost:9082/start/api/v1/github/callback`

Once you have done this GitHub will give you a client ID and client secret set the following environment variables prior to starting the server:

 * gitHubClientId
 * gitHubClientSecret
 * appAcceleratorSecret (this can be anything you want it to be)

 You should now be able to run the application locally and test the 'Create on GitHub' capability.
