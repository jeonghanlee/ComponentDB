#!/usr/bin/env python

#
# PdmLink route descriptor.
#

from cdb.common.utility.configurationManager import ConfigurationManager
from pdmLinkController import PdmLinkController


class PDMLinkRouteDescriptor:

    @classmethod
    def getRoutes(cls):
        contextRoot = ConfigurationManager.getInstance().getContextRoot()

        # Static instances shared between different routes
        pdmLinkController = PdmLinkController()

        # Define routes.
        routes = [

            # Get drawing 
            {
                'name': 'getDrawing',
                'path': '%s/pdmLink/drawings/:(drawingNumber)' % contextRoot,
                'controller': pdmLinkController,
                'action': 'getDrawing',
                'method': ['GET']
            },

            # Get multiple drawings using keywords/wildcards
            {
                'name': 'getDrawings',
                'path': '%s/pdmLink/drawingsByKeyword/:(searchPattern)' % contextRoot,
                'controller': pdmLinkController,
                'action': 'getDrawings',
                'method': ['GET']
            },

            # Search for drawings using using keywords/wildcards
            {
                'name': 'getDrawingSearchResults',
                'path': '%s/pdmLink/search/:(searchPattern)' % contextRoot,
                'controller': pdmLinkController,
                'action': 'getDrawingSearchResults',
                'method': ['GET']
            },

            # Search for related drawings using a drawing number or drawing number without extension.
            {
                'name': 'getRelatedDrawingSearchResults',
                'path': '%s/pdmLink/searchRelated/:(drawingNumberBase)' % contextRoot,
                'controller': pdmLinkController,
                'action': 'getRelatedDrawingSearchResults',
                'method': ['GET']
            },

            # Complete drawing info using ufid and oid from search result
            {
                'name': 'getDrawings',
                'path': '%s/pdmLink/completeDrawings/:(ufid)/:(oid)' % contextRoot,
                'controller': pdmLinkController,
                'action': 'completeDrawingInformation',
                'method': ['GET']
            },

            # Get PdmLink drawing thumbnail 
            {
                'name': 'getDrawingThumbnail',
                'path': '%s/pdmLink/drawingThumbnails/:(ufid)' % contextRoot,
                'controller': pdmLinkController,
                'action': 'getDrawingThumbnail',
                'method': ['GET']
            },

            # Get PdmLink drawing image 
            {
                'name': 'getDrawingImage',
                'path': '%s/pdmLink/drawingImages/:(ufid)' % contextRoot,
                'controller': pdmLinkController,
                'action': 'getDrawingImage',
                'method': ['GET']
            },

            # Generate PdmLink Component info
            {
                'name': 'generateComponentInfo',
                'path': '%s/pdmLink/componentInfo/:(drawingNumber)' % contextRoot,
                'controller': pdmLinkController,
                'action': 'generateComponentInfo',
                'method': ['GET']
            }

        ]
       
        return routes


