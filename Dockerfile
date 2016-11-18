FROM websphere-liberty:webProfile7

MAINTAINER Katherine Stanley <katheris@uk.ibm.com>

# Install required features
RUN /opt/ibm/wlp/bin/installUtility install --acceptLicense \
    servlet-3.1 \
    jaxrs-2.0 \
    jsonp-1.0 \
    cdi-1.2 \
    ssl-1.0 \
    apiDiscovery-1.0 \
    jndi-1.0

ADD /liberty-starter-wlpcfg/servers/StarterServer /opt/ibm/wlp/usr/servers/defaultServer/

CMD ["/opt/ibm/wlp/bin/server", "run"]

EXPOSE 9082:9082