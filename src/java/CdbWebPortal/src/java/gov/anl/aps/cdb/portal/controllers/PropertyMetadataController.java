/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.portal.controllers.settings.PropertyMetadataSettings;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyMetadata;
import gov.anl.aps.cdb.portal.model.db.beans.PropertyMetadataFacade;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValueBase;

import java.io.Serializable;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;

@Named("propertyMetadataController")
@SessionScoped
public class PropertyMetadataController extends CdbEntityController<PropertyMetadata, PropertyMetadataFacade, PropertyMetadataSettings> implements Serializable {

    @EJB
    private PropertyMetadataFacade propertyMetadataFacade; 
    
    private PropertyValueBase currentMetadataShown; 
    
    @Override
    protected PropertyMetadataSettings createNewSettingObject() {
        return new PropertyMetadataSettings(this); 
    }

    @Override
    protected PropertyMetadataFacade getEntityDbFacade() {
        return propertyMetadataFacade; 
    }

    @Override
    protected PropertyMetadata createEntityInstance() {
        return new PropertyMetadata(); 
    }

    @Override
    public String getEntityTypeName() {
        return "propertyMetadata";
    }

    @Override
    public String getCurrentEntityInstanceName() {
        return current.toString(); 
    }

    public PropertyValueBase getCurrentMetadataShown() {
        return currentMetadataShown;
    }

    public void setCurrentMetadataShown(PropertyValueBase currentMetadataShown) {
        this.currentMetadataShown = currentMetadataShown;
    }
    
}
