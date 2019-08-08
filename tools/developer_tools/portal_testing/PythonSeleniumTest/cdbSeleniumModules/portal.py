#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""

from cdbSeleniumModules.cdbSeleniumModuleBase import CdbSeleniumModuleBase

CDB_LOGIN = 'cdb'
CDB_PASSWORD = 'cdb'
LOGIN_BUTTON_ID = 'loginButton'
LOGOUT_BUTTON_ID = 'logoutButton'

class Portal(CdbSeleniumModuleBase):

	def login(self, login=CDB_LOGIN, password=CDB_PASSWORD):
		self._clickOnId(LOGIN_BUTTON_ID)

		self._typeInId('loginForm:username', login)
		self._typeInId('loginForm:password', password)
		self._clickOnId('loginForm:loginButton')

		self._waitForId(LOGOUT_BUTTON_ID)

	def logout(self):
		self._clickOnId(LOGOUT_BUTTON_ID)
		self._waitForId(LOGIN_BUTTON_ID)