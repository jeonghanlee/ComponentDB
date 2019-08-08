/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.common.exceptions.ObjectAlreadyExists;
import gov.anl.aps.cdb.portal.controllers.settings.PropertyTypeCategorySettings;
import gov.anl.aps.cdb.portal.model.db.beans.PropertyTypeCategoryFacade;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyTypeCategory;
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

@Named("propertyTypeCategoryController")
@SessionScoped
public class PropertyTypeCategoryController extends CdbEntityController<PropertyTypeCategory, PropertyTypeCategoryFacade, PropertyTypeCategorySettings> implements Serializable {        

    private static final Logger logger = Logger.getLogger(PropertyTypeCategoryController.class.getName());

    @EJB
    private PropertyTypeCategoryFacade propertyTypeCategoryFacade;

    public PropertyTypeCategoryController() {
    }

    @Override
    protected PropertyTypeCategoryFacade getEntityDbFacade() {
        return propertyTypeCategoryFacade;
    }

    @Override
    protected PropertyTypeCategory createEntityInstance() {
        PropertyTypeCategory propertyCategory = new PropertyTypeCategory();
        return propertyCategory;
    }

    @Override
    public String getEntityTypeName() {
        return "propertyTypeCategory";
    }

    @Override
    public String getDisplayEntityTypeName() {
        return "Property Type Category";
    }

    @Override
    public String getCurrentEntityInstanceName() {
        if (getCurrent() != null) {
            return getCurrent().getName();
        }
        return "";
    }

    @Override
    public PropertyTypeCategory findById(Integer id) {
        return propertyTypeCategoryFacade.findById(id);
    }

    @Override
    public List<PropertyTypeCategory> getAvailableItems() {
        return super.getAvailableItems();
    }

    @Override
    public void prepareEntityInsert(PropertyTypeCategory propertyTypeCategory) throws ObjectAlreadyExists {
        PropertyTypeCategory existingPropertyTypeCategory = propertyTypeCategoryFacade.findByName(propertyTypeCategory.getName());
        if (existingPropertyTypeCategory != null) {
            throw new ObjectAlreadyExists("Property type category " + propertyTypeCategory.getName() + " already exists.");
        }
        logger.debug("Inserting new property type category " + propertyTypeCategory.getName());
    }

    @Override
    public void prepareEntityUpdate(PropertyTypeCategory propertyTypeCategory) throws ObjectAlreadyExists {
        PropertyTypeCategory existingPropertyTypeCategory = propertyTypeCategoryFacade.findByName(propertyTypeCategory.getName());
        if (existingPropertyTypeCategory != null && !existingPropertyTypeCategory.getId().equals(propertyTypeCategory.getId())) {
            throw new ObjectAlreadyExists("Property type category " + propertyTypeCategory.getName() + " already exists.");
        }
        logger.debug("Updating property type category " + propertyTypeCategory.getName());
    }

    @Override
    protected PropertyTypeCategorySettings createNewSettingObject() {
        return new PropertyTypeCategorySettings(this);
    }

    /**
     * Converter class for property type category objects.
     */
    @FacesConverter(value = "propertyTypeCategoryConverter", forClass = PropertyTypeCategory.class)
    public static class PropertyCategoryControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            try {
                PropertyTypeCategoryController controller = (PropertyTypeCategoryController) facesContext.getApplication().getELResolver().
                        getValue(facesContext.getELContext(), null, "propertyTypeCategoryController");
                return controller.getEntity(getIntegerKey(value));
            } catch (Exception ex) {
                // we cannot get entity from a given key
                logger.warn("Value " + value + " cannot be converted to property type category object.");
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
            if (object instanceof PropertyTypeCategory) {
                PropertyTypeCategory o = (PropertyTypeCategory) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + PropertyTypeCategory.class.getName());
            }
        }

    }


}
