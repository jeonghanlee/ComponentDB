#!/bin/sh

#
# Script used for deploying CDB web portal
# Deployment configuration can be set in etc/$CDB_DB_NAME.deploy.conf file
#
# Usage:
#
# $0 [CDB_DB_NAME]
#

MY_DIR=`dirname $0` && cd $MY_DIR && MY_DIR=`pwd`
if [ -z "${CDB_ROOT_DIR}" ]; then
    CDB_ROOT_DIR=$MY_DIR/..
fi
CDB_ENV_FILE=${CDB_ROOT_DIR}/setup.sh
if [ ! -f ${CDB_ENV_FILE} ]; then
    echo "Environment file ${CDB_ENV_FILE} does not exist." 
    exit 2
fi
. ${CDB_ENV_FILE} > /dev/null

# Use first argument as db name, if provided
CDB_DB_NAME=${CDB_DB_NAME:=cdb}
if [ ! -z "$1" ]; then
    CDB_DB_NAME=$1
fi
echo "Using DB name: $CDB_DB_NAME"

# Look for deployment file in etc directory, and use it to override
# default entries
deployConfigFile=$CDB_ROOT_DIR/etc/${CDB_DB_NAME}.deploy.conf
if [ -f $deployConfigFile ]; then
    echo "Using deployment config file: $deployConfigFile"
    . $deployConfigFile
else
    echo "Deployment config file $deployConfigFile not found, using defaults"
fi

CDB_HOST_ARCH=`uname | tr [A-Z] [a-z]`-`uname -m`
CDB_CONTEXT_ROOT=${CDB_CONTEXT_ROOT:=cdb}
CDB_DATA_DIR=${CDB_DATA_DIR:=/cdb}
GLASSFISH_DIR=$CDB_SUPPORT_DIR/glassfish/$CDB_HOST_ARCH
CDB_DEPLOY_DIR=$GLASSFISH_DIR/glassfish/domains/domain1/autodeploy
CDB_DIST_DIR=$CDB_ROOT_DIR/src/java/CdbWebPortal/dist
CDB_BUILD_WAR_FILE=CdbWebPortal.war
CDB_WAR_FILE=$CDB_CONTEXT_ROOT.war
JAVA_HOME=$CDB_SUPPORT_DIR/java/$CDB_HOST_ARCH
CDB_DATE=`date +%Y.%m.%d`

if [ ! -f $CDB_DIST_DIR/$CDB_BUILD_WAR_FILE ]; then
    echo "$CDB_BUILD_WAR_FILE not found in $CDB_DIST_DIR."
    exit 1
fi

# Create needed data directories
mkdir -p $CDB_DATA_DIR/propertyValue
mkdir -p $CDB_DATA_DIR/log

# Modify war file for proper settings and repackage it into new war 
echo "Repackaging war file for context root $CDB_CONTEXT_ROOT"
cd $CDB_DIST_DIR
rm -rf $CDB_CONTEXT_ROOT
mkdir -p $CDB_CONTEXT_ROOT
cd $CDB_CONTEXT_ROOT
jar xf ../$CDB_BUILD_WAR_FILE

configFile=WEB-INF/glassfish-web.xml
cmd="cat $configFile | sed 's?<context-root.*?<context-root>${CDB_CONTEXT_ROOT}</context-root>?g' > $configFile.2 && mv $configFile.2 $configFile"
eval $cmd

cmd="cat $configFile | sed 's?dir=.*\"?dir=${CDB_DATA_DIR}\"?g' > $configFile.2 && mv $configFile.2 $configFile"
eval $cmd

configFile=WEB-INF/classes/META-INF/persistence.xml
cmd="cat $configFile | sed 's?<jta-data-source.*?<jta-data-source>${CDB_DB_NAME}_DataSource</jta-data-source>?g' > $configFile.2 && mv $configFile.2 $configFile"
eval $cmd

configFile=WEB-INF/classes/cdb.portal.properties
cmd="cat $configFile | sed 's?storageDirectory=.*?storageDirectory=${CDB_DATA_DIR}?g' > $configFile.2 && mv $configFile.2 $configFile"
eval $cmd

configFile=WEB-INF/classes/resources.properties
cmd="cat $configFile | sed 's?CdbPortalTitle=.*?CdbPortalTitle=${CDB_PORTAL_TITLE}?g' > $configFile.2 && mv $configFile.2 $configFile"
eval $cmd
cmd="cat $configFile | sed 's?CdbSoftwareVersion=.*?CdbSoftwareVersion=${CDB_SOFTWARE_VERSION}?g' | sed 's?CDB_DATE?$CDB_DATE?g' > $configFile.2 && mv $configFile.2 $configFile"
eval $cmd

for cssFile in login portal components componentInstances designs; do
    configFile=resources/css/$cssFile.css
    cmd="cat $configFile | sed 's?color:.*CDB_CSS_PORTAL_TITLE_COLOR.*?color: ${CDB_CSS_PORTAL_TITLE_COLOR};?g' > $configFile.2 && mv $configFile.2 $configFile"
    eval $cmd
done

jar cf ../$CDB_WAR_FILE *

export AS_JAVA=$JAVA_HOME
ASADMIN_CMD=$GLASSFISH_DIR/bin/asadmin

# copy war file
echo "Copying war file $CDB_DIST_DIR/$CDB_WAR_FILE to $CDB_DEPLOY_DIR"
rm -f $CDB_DEPLOY_DIR/${CDB_WAR_FILE}_*
cp $CDB_DIST_DIR/$CDB_WAR_FILE $CDB_DEPLOY_DIR

# wait on deployment
echo "Waiting on war deployment..."
WAIT_TIME=60
cd $CDB_DEPLOY_DIR
t=0
while [ $t -lt $WAIT_TIME ]; do
    sleep 1
    deploymentStatus=`ls -c1 ${CDB_WAR_FILE}_* 2> /dev/null | sed 's?.*war_??g'`
    if [ ! -z "$deploymentStatus" ]; then
        break
    fi
    t=`expr $t + 1`
done
echo "Deployment Status: $deploymentStatus"


