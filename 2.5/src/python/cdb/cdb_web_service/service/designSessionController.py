#!/usr/bin/env python

import cherrypy

from cdb.common.service.cdbSessionController import CdbSessionController
from cdb.common.exceptions.invalidRequest import InvalidRequest
from cdb.common.utility.encoder import Encoder
from cdb.common.utility.valueUtility import ValueUtility
from cdb.cdb_web_service.impl.designControllerImpl import DesignControllerImpl

class DesignSessionController(CdbSessionController):

    def __init__(self):
        CdbSessionController.__init__(self)
        self.designControllerImpl = DesignControllerImpl()

    @cherrypy.expose
    @CdbSessionController.require(CdbSessionController.isLoggedIn())
    @CdbSessionController.execute
    def addDesign(self, **kwargs):
        if not kwargs.has_key('name'):
            raise InvalidRequest('Missing design name.')
        name = Encoder.decode(kwargs.get('name'))

        sessionUser = self.getSessionUser()
        createdByUserId = sessionUser.get('id')
        ownerUserId = kwargs.get('ownerUserId', createdByUserId)
        ownerGroupId = kwargs.get('ownerGroupId')
        if ownerGroupId is None:
            ownerGroup = sessionUser.getDefaultUserGroup()
            if ownerGroup is not None:
                ownerGroupId = ownerGroup.get('id')
        isGroupWriteable = kwargs.get('isGroupWriteable', False)
        description = kwargs.get('description')
        if description is not None:
            description = Encoder.decode(description)

        return self.designControllerImpl.addDesign(name, createdByUserId, ownerUserId, ownerGroupId, isGroupWriteable, description).getFullJsonRep()

    @cherrypy.expose
    @CdbSessionController.require(CdbSessionController.isLoggedIn())
    @CdbSessionController.execute
    def loadDesign(self, **kwargs):
        if not kwargs.has_key('name'):
            raise InvalidRequest('Missing design name.')
        name = Encoder.decode(kwargs.get('name'))

        sessionUser = self.getSessionUser()
        createdByUserId = sessionUser.get('id')
        ownerUserId = kwargs.get('ownerUserId', createdByUserId)
        ownerGroupId = kwargs.get('ownerGroupId')
        if ownerGroupId is None:
            ownerGroup = sessionUser.getDefaultUserGroup()
            if ownerGroup is not None:
                ownerGroupId = ownerGroup.get('id')
        isGroupWriteable = kwargs.get('isGroupWriteable', False)
        description = kwargs.get('description')
        if description is not None:
            description = Encoder.decode(description)

        designElementList = kwargs.get('designElementList')
        if designElementList is not None:
            designElementList = self.fromJson(Encoder.decode(designElementList))

        return self.designControllerImpl.loadDesign(name, createdByUserId, ownerUserId, ownerGroupId, isGroupWriteable, description, designElementList).getFullJsonRep()

    @cherrypy.expose
    @CdbSessionController.require(CdbSessionController.isLoggedIn())
    @CdbSessionController.execute
    def addDesignProperty(self, designId, propertyTypeId, **kwargs):
        tag = Encoder.decode(kwargs.get('tag'))
        units = Encoder.decode(kwargs.get('units'))
        value = Encoder.decode(kwargs.get('value'))
        description  = Encoder.decode(kwargs.get('description'))
        isDynamic = ValueUtility.toBoolean(kwargs.get('isDynamic', False))
        isUserWriteable = ValueUtility.toBoolean(kwargs.get('isUserWriteable', False))

        sessionUser = self.getSessionUser()
        enteredByUserId = sessionUser.get('id')

        return self.designControllerImpl.addDesignProperty(designId, propertyTypeId, tag, value, units, description, enteredByUserId, isDynamic, isUserWriteable).getFullJsonRep()

    @cherrypy.expose
    @CdbSessionController.require(CdbSessionController.isLoggedIn())
    @CdbSessionController.execute
    def addDesignElementProperty(self, designElementId, propertyTypeId, **kwargs):
        tag = Encoder.decode(kwargs.get('tag'))
        units = Encoder.decode(kwargs.get('units'))
        value = Encoder.decode(kwargs.get('value'))
        description  = Encoder.decode(kwargs.get('description'))
        isDynamic = ValueUtility.toBoolean(kwargs.get('isDynamic', False))
        isUserWriteable = ValueUtility.toBoolean(kwargs.get('isUserWriteable', False))

        sessionUser = self.getSessionUser()
        enteredByUserId = sessionUser.get('id')

        return self.designControllerImpl.addDesignElementProperty(designElementId, propertyTypeId, tag, value, units, description, enteredByUserId, isDynamic, isUserWriteable).getFullJsonRep()

