#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""


from cdb.common.utility.ldapUtility import LdapUtility
from authorizationPrincipalAuthenticator import AuthorizationPrincipalAuthenticator 

class LdapPasswordPrincipalAuthenticator(AuthorizationPrincipalAuthenticator):

    def __init__(self, serverUrl, dnFormat, serviceDn = None, servicePass = None, userLookupFilter= None):
        AuthorizationPrincipalAuthenticator.__init__(self, self.__class__.__name__)
        self.serviceDn = serviceDn
        self.servicePass = servicePass
        self.userLookupFilter = userLookupFilter
        self.ldapUtility = LdapUtility(serverUrl, dnFormat, serviceDn, servicePass, userLookupFilter)

    def authenticatePrincipal(self, principal, password):
        if principal is not None:
            try:
                self.logger.debug('Checking credentials for %s' % principal.getName())
                self.ldapUtility.checkCredentials(principal.getName(), password)
                return principal
            except Exception, ex:
                self.logger.debug(ex)
        return None

#######################################################################
# Testing.
if __name__ == '__main__':
    pass

