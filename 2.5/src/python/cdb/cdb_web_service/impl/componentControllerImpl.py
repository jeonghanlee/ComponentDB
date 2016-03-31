#!/usr/bin/env python

#
# Implementation for component controller.
#

from cdb.common.objects.cdbObject import CdbObject
from cdb.common.objects.cdbObjectManager import CdbObjectManager
from cdb.common.db.api.componentDbApi import ComponentDbApi
from cdb.common.impl.authorizationManager import AuthorizationManager

class ComponentControllerImpl(CdbObjectManager):
    """ Component controller implementation class. """

    def __init__(self):
        CdbObjectManager.__init__(self)
        self.componentDbApi = ComponentDbApi()

        adminGroupName = AuthorizationManager.getInstance().getAdminGroupName()
        self.componentDbApi.setAdminGroupName(adminGroupName)
        self.logger.debug('Using admin group: %s' % adminGroupName)

    def getComponents(self):
        return self.componentDbApi.getComponents()

    def getComponentsByName(self, name):
        return self.componentDbApi.getComponentsByName(name)

    def getComponentById(self, id):
        return self.componentDbApi.getComponentById(id)

    def getComponentByName(self, name):
        return self.componentDbApi.getComponentByName(name)

    def getComponentByModelNumber(self, modelNumber):
        return self.componentDbApi.getComponentByModelNumber(modelNumber)

    def getComponentInstanceById(self, id):
        return self.componentDbApi.getComponentInstanceById(id)

    def getComponentInstanceLocatonHistoryByComponentInstanceId(self, componentInstanceId):
        return self.componentDbApi.getComponentInstanceLocationHistoryByComponentInstanceId(componentInstanceId)

    def addComponent(self, name, modelNumber, componentTypeId, createdByUserId, ownerUserId, ownerGroupId, isGroupWriteable, description):
        return self.componentDbApi.addComponent(name, modelNumber, componentTypeId, createdByUserId, ownerUserId, ownerGroupId, isGroupWriteable, description)

    def addComponentProperty(self, componentId, propertyTypeId, tag, value, units, description, enteredByUserId, isDynamic, isUserWriteable):
        return self.componentDbApi.addComponentPropertyByTypeId(componentId, propertyTypeId, tag, value, units, description, enteredByUserId, isDynamic, isUserWriteable)

    def addComponentInstanceProperty(self, componentInstanceId, propertyTypeId, tag, value, units, description, enteredByUserId, isDynamic, isUserWriteable):
        return self.componentDbApi.addComponentInstancePropertyByTypeId(componentInstanceId, propertyTypeId, tag, value, units, description, enteredByUserId, isDynamic, isUserWriteable)

    def updateComponentInstanceLocation(self, componentInstanceId, locationId, locationDetails, enteredByUserId):
        return self.componentDbApi.updateComponentInstanceLocationByLocationId(componentInstanceId, locationId, locationDetails, enteredByUserId)
