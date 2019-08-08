#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""


import cherrypy
from cdb.common.service.cdbController import CdbController
from cdb.cdb_web_service.impl.userGroupControllerImpl import UserGroupControllerImpl

class UserGroupController(CdbController):

    def __init__(self):
        CdbController.__init__(self)
        self.userGroupControllerImpl = UserGroupControllerImpl()

    @cherrypy.expose
    @CdbController.execute
    def getUserGroups(self, **kwargs):
        return self.listToJson(self.userGroupControllerImpl.getUserGroups())

    @cherrypy.expose
    @CdbController.execute
    def getUserGroupByName(self, groupName):
        return self.userGroupControllerImpl.getUserGroupByName(groupName).getFullJsonRep()


