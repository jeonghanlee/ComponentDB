#!/bin/sh

DB_NAME=irmis
DB_OWNER=irmis
DB_OWNER_PASSWORD=irmis
DB_HOST=127.0.0.1
DB_PORT=3306
DB_ADMIN_USER=root
DB_ADMIN_HOSTS="127.0.0.1 bluegill1.aps.anl.gov gaeaimac.aps.anl.gov
visa%.aps.anl.gov"
DB_ADMIN_PASSWORD=

MY_DIR=`dirname $0` && cd $MY_DIR && MY_DIR=`pwd`
if [ -z "${CMS_ROOT_DIR}" ]; then
    CMS_ROOT_DIR=$MY_DIR/..
fi
CMS_SQL_DIR=$CMS_ROOT_DIR/db/sql/irmis
CMS_ENV_FILE=${CMS_ROOT_DIR}/setup.sh
if [ ! -f ${CMS_ENV_FILE} ]; then
    echo "Environment file ${CMS_ENV_FILE} does not exist." 
    exit 2
fi
. ${CMS_ENV_FILE} > /dev/null 

# Read password
sttyOrig=`stty -g`
stty -echo
read -p "Enter MySQL root password: " DB_ADMIN_PASSWORD
stty $sttyOrig

mysqlCmd="mysql --port=$DB_PORT --host=$DB_HOST -u $DB_ADMIN_USER"
if [ ! -z "$DB_ADMIN_PASSWORD" ]; then
    mysqlCmd="$mysqlCmd -p$DB_ADMIN_PASSWORD"
fi

execute() {
    msg="$@"
    if [ ! -z "$DB_ADMIN_PASSWORD" ]; then
        sedCmd="s?$DB_ADMIN_PASSWORD?\\*\\*\\*\\*\\*\\*?g"
        echo $sedCmd
        echo "Executing: $@" | sed -e $sedCmd
    else
        echo "Executing: $@"
    fi
    eval "$@"
}



# drop old db
cd $CMS_SQL_DIR
sqlFile=/tmp/create_irmis_db.`id -u`.sql
rm -f $sqlFile
echo "DROP DATABASE IF EXISTS $DB_NAME;" >> $sqlFile
echo "CREATE DATABASE $DB_NAME;" >> $sqlFile
for host in $DB_ADMIN_HOSTS; do
    echo "GRANT ALL PRIVILEGES ON $DB_NAME.* TO '$DB_OWNER'@'$host' IDENTIFIED BY '$DB_OWNER_PASSWORD';" >> $sqlFile
done
execute "$mysqlCmd < $sqlFile"

# create db
mysqlCmd="$mysqlCmd -D $DB_NAME <"
execute $mysqlCmd create_irmis_tables.sql

# populate db
execute $mysqlCmd populate_chc_beamline_interest.sql

execute $mysqlCmd populate_rec_client_type.sql
execute $mysqlCmd populate_ioc_resource_type.sql 
execute $mysqlCmd populate_ioc_error_message.sql 

execute $mysqlCmd populate_role_name.sql 
execute $mysqlCmd populate_role.sql 
execute $mysqlCmd populate_group_name.sql 
execute $mysqlCmd populate_audit_action_type.sql

execute $mysqlCmd populate_form_factor.sql
execute $mysqlCmd populate_function.sql
execute $mysqlCmd populate_mfg.sql
execute $mysqlCmd populate_person.sql
execute $mysqlCmd populate_doc_type.sql

execute $mysqlCmd populate_component_rel_type.sql
execute $mysqlCmd populate_component_type_if_type.sql
execute $mysqlCmd populate_component_type.sql
execute $mysqlCmd populate_component_type_if.sql
execute $mysqlCmd populate_component_type_function.sql
execute $mysqlCmd populate_component_type_status.sql
execute $mysqlCmd populate_component_type_person.sql
execute $mysqlCmd populate_component_port_type.sql
execute $mysqlCmd populate_component_port_template.sql
execute $mysqlCmd populate_component_semaphore.sql

execute $mysqlCmd populate_port_pin_type.sql 
execute $mysqlCmd populate_port_pin_designator.sql
execute $mysqlCmd populate_port_pin_template.sql

execute $mysqlCmd populate_component.sql
execute $mysqlCmd populate_aps_component.sql

execute $mysqlCmd populate_component_state_category.sql
execute $mysqlCmd populate_component_state.sql
execute $mysqlCmd populate_component_rel.sql
execute $mysqlCmd populate_component_port.sql

execute $mysqlCmd populate_component_history.sql

execute $mysqlCmd populate_component_instance.sql
execute $mysqlCmd populate_component_instance_state.sql

execute $mysqlCmd populate_cable.sql
execute $mysqlCmd populate_port_pin.sql

# Add development rows
execute $mysqlCmd add_development_entries.sql

# cleanup
execute rm -f $sqlFile
