# Welcome to the Liberty app accelerator
An application builder to allow you to construct starter applications for building your projects with Liberty.

Liberty app accelerator is still in its early stages so we will be adding new technologies and capabilities to the page over time. For now, feel free to <a href="http://liberty-starter.wasdev.developer.ibm.com/start/">try it out</a> and see how quickly you can have a fully fledged application. If you have any questions, comments or suggestions please raise an <a href="https://github.com/WASdev/tool.artisan.core/issues">issue</a> on our GitHub repository.

For an introduction see our <a href="https://developer.ibm.com/wasdev/blog/2015/11/27/introducing-the-liberty-starter-a-tool-to-get-you-writing-microservices-quickly/">blog post</a>. To learn how the Liberty Starter can be used to create the <a href="https://developer.ibm.com/wasdev/docs/an-evolved-plants-by-websphere-image-service-making-the-break/">image service</a> for the <a href="https://developer.ibm.com/wasdev/docs/starting-evolution-microservices-using-plants-websphere-sample/">Plants by WebSphere evolution</a> from monolith to microservice, see our new <a href="https://developer.ibm.com/wasdev/docs/creating-image-service-plants-websphere-using-liberty-starter"/>article</a>.

The current technologies we have are:

## Rest
This provides you with the jaxrs-2.0 feature and the jsonp-1.0 feature.

Inside the application project produced there is a application.rest package containing the <code>LibertyRestEndpoint</code> class. This adds a REST endpoint which you can access at /rest. Inside the wlpcfg project there is the <code>it.rest.LibertyRestEndpointTest</code> that will test the REST endpoint to ensure it is working.

For the complete feature documentation, see the <a href="http://www.ibm.com/support/knowledgecenter/SSAW57_8.5.5/com.ibm.websphere.wlp.nd.multiplatform.doc/ae/rwlp_feat.html%23rwlp_feat__jaxrs-2.0">jaxrs-2.0</a> feature description in IBM Knowledge Center.

## Servlet
This provides you with the servlet-3.1 feature.

Inside the application project there is a application.servlet package containing the <code>LibertyServlet</code> class. This adds a servlet with an endpoint which you can access at /servlet. Inside the wlpcfg project there is the <code>it.servlet.LibertyServletTest</code> that will test the servlet's endpoint to ensure it is working.

For the complete feature documentation, see the <a href="http://www.ibm.com/support/knowledgecenter/SSAW57_8.5.5/com.ibm.websphere.wlp.nd.multiplatform.doc/ae/rwlp_feat.html%23rwlp_feat__servlet-3.1">servlet-3.1</a> feature description in IBM Knowledge Center.

## Spring Boot with Spring MVC
This provides you with a SpringBoot application that will run on WebSphere Liberty.

Inside the application project there is a application.springboot package containing two classes:
* SpringBootLibertyApplication</code>: The entry point for the SpringBoot application.
* LibertyHelloController</code>: A Spring MVC endpoint which you can access at /springBoot.

Inside the wlpcfg project there is the <code>it.springboot.HelloControllerTest</code> that will test the Spring MVC endpoint to ensure it is working.

## Websockets
This provides you with the websocket-1.1 feature.

For the complete feature documentation, see the <a href="http://www.ibm.com/support/knowledgecenter/SSAW57_8.5.5/com.ibm.websphere.wlp.nd.multiplatform.doc/ae/rwlp_feat.html%23rwlp_feat__websocket-1.0">websocket-1.1</a> feature description in IBM Knowledge Center.

## Persistence
This provides you with jpa-2.1. For the complete feature documentation, see the <a href="http://www.ibm.com/support/knowledgecenter/SSAW57_8.5.5/com.ibm.websphere.wlp.nd.multiplatform.doc/ae/rwlp_feat.html%23rwlp_feat__jpa-2.1">jpa-2.1</a> feature description in IBM Knowledge Center.

## Watson SDK
The Watson SDK provides an API for accessing Watson Services. For the complete documentation, take a look at <a href="https://developer.ibm.com/watson/">the Watson developer pages</a> and <a href="https://github.com/watson-developer-cloud/java-sdk">the Watson SDK github project</a>.
