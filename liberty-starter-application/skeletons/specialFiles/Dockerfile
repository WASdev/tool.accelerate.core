FROM websphere-liberty:webProfile7

MAINTAINER https://github.com/WASdev/tool.accelerate.core

ADD ./target/wlp/usr/servers/LibertyProjectServer /opt/ibm/wlp/usr/servers/defaultServer/

CMD ["/opt/ibm/wlp/bin/server", "run"]