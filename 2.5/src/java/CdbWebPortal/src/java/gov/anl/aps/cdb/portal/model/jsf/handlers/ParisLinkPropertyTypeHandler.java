/*
 * Copyright (c) 2014-2015, Argonne National Laboratory.
 *
 * SVN Information:
 *   $HeadURL$
 *   $Date$
 *   $Revision$
 *   $Author$
 */
package gov.anl.aps.cdb.portal.model.jsf.handlers;

import gov.anl.aps.cdb.common.constants.CdbProperty;
import gov.anl.aps.cdb.portal.constants.DisplayType;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValueHistory;
import gov.anl.aps.cdb.portal.utilities.ConfigurationUtility;

/**
 * PARIS link property type handler.
 */
public class ParisLinkPropertyTypeHandler extends AbstractPropertyTypeHandler {

    public static final String HANDLER_NAME = "PARIS Link";

    private static final String ParisUrl = ConfigurationUtility.getPortalProperty(
            CdbProperty.PARIS_URL_STRING_PROPERTY_NAME);

    public ParisLinkPropertyTypeHandler() {
        super(HANDLER_NAME, DisplayType.HTTP_LINK);
    }

    public static String formatParisLink(String poId) {
        // Property Handler:  For a purchase req number such as Fy-nnnnnn create link 
        // https://apps.inside.anl.gov/paris/req.jsp?reqNbr=Fy-nnnnnn
        // Example: F3-326054  
        // https://apps.inside.anl.gov/paris/req.jsp?reqNbr=F3-326054          
        if (poId == null) {
            return null;
        }

        String url = ParisUrl.replace("PO_ID", poId);
        return url;
    }

    @Override
    public void setTargetValue(PropertyValue propertyValue) {
        String targetLink = formatParisLink(propertyValue.getValue());
        propertyValue.setTargetValue(targetLink);
    }

    @Override
    public void setTargetValue(PropertyValueHistory propertyValueHistory) {
        String targetLink = formatParisLink(propertyValueHistory.getValue());
        propertyValueHistory.setTargetValue(targetLink);
    }
}
