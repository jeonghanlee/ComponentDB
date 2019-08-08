/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.common.exceptions.ObjectAlreadyExists;
import gov.anl.aps.cdb.portal.controllers.settings.SourceSettings;
import gov.anl.aps.cdb.portal.model.db.beans.SourceFacade;
import gov.anl.aps.cdb.portal.model.db.entities.Source;
import java.io.Serializable;
import java.util.List;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import org.apache.log4j.Logger;

@Named("sourceController")
@SessionScoped
public class SourceController extends CdbEntityController<Source, SourceFacade, SourceSettings> implements Serializable {    

    private static final Logger logger = Logger.getLogger(SourceController.class.getName());   

    @EJB
    private SourceFacade sourceFacade;

    public SourceController() {
    }

    @Override
    protected SourceFacade getEntityDbFacade() {
        return sourceFacade;
    }

    @Override
    protected Source createEntityInstance() {
        Source source = new Source();
        return source;
    }

    @Override
    public String getEntityTypeName() {
        return "source";
    }

    @Override
    public String getCurrentEntityInstanceName() {
        if (getCurrent() != null) {
            return getCurrent().getName();
        }
        return "";
    }

    @Override
    public Source findById(Integer id) {
        return sourceFacade.findById(id);
    }

    @Override
    public List<Source> getAvailableItems() {
        return super.getAvailableItems();
    }

    public List<Source> getAvailableSourcesSortedByName() {
        return sourceFacade.findAllSortedByName();
    }

    @Override
    public void prepareEntityInsert(Source source) throws ObjectAlreadyExists {
        Source existingSource = sourceFacade.findByName(source.getName());
        if (existingSource != null) {
            throw new ObjectAlreadyExists("Source " + source.getName() + " already exists.");
        }
        logger.debug("Inserting new source " + source.getName());
    }

    @Override
    public void prepareEntityUpdate(Source source) throws ObjectAlreadyExists {
        Source existingSource = sourceFacade.findByName(source.getName());
        if (existingSource != null && !existingSource.getId().equals(source.getId())) {
            throw new ObjectAlreadyExists("Source " + source.getName() + " already exists.");
        }
        logger.debug("Updating source " + source.getName());
    }
    
    @Override
    protected SourceSettings createNewSettingObject() {
        return new SourceSettings(this);
    }

    /**
     * Converter class for source objects.
     */
    @FacesConverter(forClass = Source.class)
    public static class SourceControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            try {
                SourceController controller = (SourceController) facesContext.getApplication().getELResolver().
                        getValue(facesContext.getELContext(), null, "sourceController");
                return controller.getEntity(getIntegerKey(value));
            } catch (Exception ex) {
                // we cannot get entity from a given key
                logger.warn("Value " + value + " cannot be converted to source object.");
                return null;
            }
        }

        Integer getIntegerKey(String value) {
            return Integer.valueOf(value);
        }

        String getStringKey(Integer value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof Source) {
                Source o = (Source) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Source.class.getName());
            }
        }

    }   

    @Override
    public boolean entityCanBeCreatedByUsers() {
        return true;
    }    

    
    
}
