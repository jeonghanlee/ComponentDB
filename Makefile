# The top level makefile. Targets like "all" and "clean"
# are defined in the RULES file.

TOP = .
#SUBDIRS = irmis src
SUBDIRS = src

.PHONY: support dev-config
.PHONY: db backup db-dev deploy-web-portal undeploy-web-portal deploy-web-service undeploy-web-service
.PHONY: db-dev backup-dev deploy-web-portal-dev undeploy-web-portal-dev deploy-web-service-dev undeploy-web-service-dev

default:

prepare-dev-env: support db dev-config

dev-config:
	$(TOP)/sbin/cdb_prepare_dev_config.sh

support:
	$(TOP)/sbin/cdb_install_support.sh

db:
	$(TOP)/sbin/cdb_create_db.sh

backup:
	$(TOP)/sbin/cdb_backup_all.sh 

configure-web-portal: dist
	$(TOP)/sbin/cdb_configure_web_portal.sh

deploy-web-portal: dist
	$(TOP)/sbin/cdb_deploy_web_portal.sh

deploy-web-service: 
	$(TOP)/sbin/cdb_deploy_web_service.sh

unconfigure-web-portal: 
	$(TOP)/sbin/cdb_unconfigure_web_portal.sh

undeploy-web-portal: 
	$(TOP)/sbin/cdb_undeploy_web_portal.sh

undeploy-web-service: 
	$(TOP)/sbin/cdb_undeploy_web_service.sh

db-dev:
	$(TOP)/sbin/cdb_create_db.sh cdb_dev

backup-dev:
	$(TOP)/sbin/cdb_backup_all.sh cdb_dev

deploy-web-portal-dev: dist
	$(TOP)/sbin/cdb_deploy_web_portal.sh cdb_dev

configure-web-portal-dev: dist
	$(TOP)/sbin/cdb_configure_web_portal.sh cdb_dev

deploy-web-service-dev: 
	$(TOP)/sbin/cdb_deploy_web_service.sh cdb_dev

unconfigure-web-portal-dev: 
	$(TOP)/sbin/cdb_unconfigure_web_portal.sh cdb_dev

undeploy-web-portal-dev: 
	$(TOP)/sbin/cdb_undeploy_web_portal.sh cdb_dev

undeploy-web-service-dev: 
	$(TOP)/sbin/cdb_undeploy_web_service.sh cdb_dev

db-lcls2:
	$(TOP)/sbin/cdb_create_db.sh cdb_lcls2

backup-lcls2:
	$(TOP)/sbin/cdb_backup_all.sh cdb_lcls2

deploy-web-portal-lcls2: dist
	$(TOP)/sbin/cdb_deploy_web_portal.sh cdb_lcls2

configure-web-portal-lcls2: dist
	$(TOP)/sbin/cdb_configure_web_portal.sh cdb_lcls2

deploy-web-service-lcls2: 
	$(TOP)/sbin/cdb_deploy_web_service.sh cdb_lcls2

unconfigure-web-portal-lcls2: 
	$(TOP)/sbin/cdb_unconfigure_web_portal.sh cdb_lcls2

undeploy-web-portal-lcls2: 
	$(TOP)/sbin/cdb_undeploy_web_portal.sh cdb_lcls2

undeploy-web-service-lcls2: 
	$(TOP)/sbin/cdb_undeploy_web_service.sh cdb_lcls2

include $(TOP)/tools/make/RULES_CDB

