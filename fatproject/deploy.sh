#!/bin/bash
CP=lib/elementdeployer.jar
CP=$CP:lib/commons-codec-1.4.jar
CP=$CP:lib/commons-httpclient-3.1.jar
CP=$CP:lib/commons-logging-1.1.1.jar
CP=$CP:lib/log4j-1.2.16.jar
if test -z "$1"
then arg=ElementDeployer.prp
else arg="$1"
fi
java -cp "$CP" com.sciabarra.fatwire.ElementDeployer "$arg"
