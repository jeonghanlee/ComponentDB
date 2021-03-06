#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""


from cdb.common.db.entities.cdbDbEntity import CdbDbEntity
from cdb.common.objects import propertyValueHistory

class PropertyValueHistory(CdbDbEntity):

    entityDisplayName = 'property value history'

    mappedColumnDict = { 
        'property_value_id' : 'propertyValue',
        'entered_on_date_time' : 'enteredOnDateTime',
        'entered_by_user_id' : 'enteredByUserId',
        'display_value' : 'display_value',
        'target_value' : 'target_value',
    }
    cdbObjectClass = propertyValueHistory.PropertyValueHistory

    def __init__(self, **kwargs):
        CdbDbEntity.__init__(self, **kwargs)


