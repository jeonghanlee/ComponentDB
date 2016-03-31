/*
 * Copyright (c) 2014-2015, Argonne National Laboratory.
 *
 * SVN Information:
 *   $HeadURL$
 *   $Date$
 *   $Revision$
 *   $Author$
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.common.exceptions.ObjectAlreadyExists;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyType;
import gov.anl.aps.cdb.portal.model.db.beans.PropertyTypeDbFacade;
import gov.anl.aps.cdb.portal.model.db.entities.SettingType;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import gov.anl.aps.cdb.portal.model.db.entities.AllowedPropertyValue;
import gov.anl.aps.cdb.portal.model.db.entities.Component;
import gov.anl.aps.cdb.portal.model.db.entities.ComponentInstance;
import gov.anl.aps.cdb.portal.model.db.entities.ComponentType;
import gov.anl.aps.cdb.portal.model.db.entities.Design;
import gov.anl.aps.cdb.portal.model.db.entities.DesignElement;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import org.apache.log4j.Logger;
import org.primefaces.component.datatable.DataTable;

/**
 * Controller class for property types.
 */
@Named("propertyTypeController")
@SessionScoped
public class PropertyTypeController extends CdbEntityController<PropertyType, PropertyTypeDbFacade> implements Serializable {

    /*
     * Controller specific settings
     */
    private static final String DisplayNumberOfItemsPerPageSettingTypeKey = "PropertyType.List.Display.NumberOfItemsPerPage";
    private static final String DisplayIdSettingTypeKey = "PropertyType.List.Display.Id";
    private static final String DisplayDescriptionSettingTypeKey = "PropertyType.List.Display.Description";
    private static final String DisplayCategorySettingTypeKey = "PropertyType.List.Display.Category";
    private static final String DisplayDefaultUnitsSettingTypeKey = "PropertyType.List.Display.DefaultUnits";
    private static final String DisplayDefaultValueSettingTypeKey = "PropertyType.List.Display.DefaultValue";
    private static final String DisplayHandlerSettingTypeKey = "PropertyType.List.Display.Handler";
    private static final String FilterByNameSettingTypeKey = "PropertyType.List.FilterBy.Name";
    private static final String FilterByDescriptionSettingTypeKey = "PropertyType.List.FilterBy.Description";
    private static final String FilterByCategorySettingTypeKey = "PropertyType.List.FilterBy.Category";
    private static final String FilterByDefaultUnitsSettingTypeKey = "PropertyType.List.FilterBy.DefaultUnits";
    private static final String FilterByDefaultValueSettingTypeKey = "PropertyType.List.FilterBy.DefaultValue";
    private static final String FilterByHandlerSettingTypeKey = "PropertyType.List.FilterBy.Handler";

    private static final Logger logger = Logger.getLogger(PropertyTypeController.class.getName());

    @EJB
    private PropertyTypeDbFacade propertyTypeFacade;

    private Boolean displayCategory = null;
    private Boolean displayDefaultUnits = null;
    private Boolean displayDefaultValue = null;
    private Boolean displayHandler = null;

    private String filterByCategory = null;
    private String filterByDefaultUnits = null;
    private String filterByDefaultValue = null;
    private String filterByHandler = null;

    private Boolean selectDisplayCategory = true;
    private Boolean selectDisplayDefaultUnits = true;
    private Boolean selectDisplayDefaultValue = true;
    private Boolean selectDisplayHandler = true;

    private String selectFilterByCategory = null;
    private String selectFilterByDefaultUnits = null;
    private String selectFilterByDefaultValue = null;
    private String selectFilterByHandler = null;

    public PropertyTypeController() {
    }

    @Override
    protected PropertyTypeDbFacade getEntityDbFacade() {
        return propertyTypeFacade;
    }

    @Override
    protected PropertyType createEntityInstance() {
        PropertyType propertyType = new PropertyType();
        return propertyType;
    }

    @Override
    public String getEntityTypeName() {
        return "propertyType";
    }

    @Override
    public String getEntityTypeCategoryName() {
        return "propertyTypeCategory";
    }

    @Override
    public String getDisplayEntityTypeName() {
        return "property type";
    }

    @Override
    public String getCurrentEntityInstanceName() {
        if (getCurrent() != null) {
            return getCurrent().getName();
        }
        return "";
    }

    @Override
    public PropertyType findById(Integer id) {
        return propertyTypeFacade.findById(id);
    }
    
    public PropertyType findByName(String name) {
        return propertyTypeFacade.findByName(name); 
    }

    @Override
    public List<PropertyType> getAvailableItems() {
        return super.getAvailableItems();
    }

    @Override
    public void prepareEntityInsert(PropertyType propertyType) throws ObjectAlreadyExists {
        PropertyType existingPropertyType = propertyTypeFacade.findByName(propertyType.getName());
        if (existingPropertyType != null) {
            throw new ObjectAlreadyExists("Property type " + propertyType.getName() + " already exists.");
        }
        logger.debug("Inserting new property type " + propertyType.getName());
    }

    @Override
    public void prepareEntityUpdate(PropertyType propertyType) throws ObjectAlreadyExists {
        PropertyType existingPropertyType = propertyTypeFacade.findByName(propertyType.getName());
        if (existingPropertyType != null && !existingPropertyType.getId().equals(propertyType.getId())) {
            throw new ObjectAlreadyExists("Property type " + propertyType.getName() + " already exists.");
        }
        logger.debug("Updating property type " + propertyType.getName());
    }

    @Override
    public void updateSettingsFromSettingTypeDefaults(Map<String, SettingType> settingTypeMap) {
        if (settingTypeMap == null) {
            return;
        }

        displayNumberOfItemsPerPage = Integer.parseInt(settingTypeMap.get(DisplayNumberOfItemsPerPageSettingTypeKey).getDefaultValue());
        displayId = Boolean.parseBoolean(settingTypeMap.get(DisplayIdSettingTypeKey).getDefaultValue());
        displayDescription = Boolean.parseBoolean(settingTypeMap.get(DisplayDescriptionSettingTypeKey).getDefaultValue());

        displayCategory = Boolean.parseBoolean(settingTypeMap.get(DisplayCategorySettingTypeKey).getDefaultValue());
        displayDefaultUnits = Boolean.parseBoolean(settingTypeMap.get(DisplayDefaultUnitsSettingTypeKey).getDefaultValue());
        displayDefaultValue = Boolean.parseBoolean(settingTypeMap.get(DisplayDefaultValueSettingTypeKey).getDefaultValue());
        displayHandler = Boolean.parseBoolean(settingTypeMap.get(DisplayHandlerSettingTypeKey).getDefaultValue());

        filterByName = settingTypeMap.get(FilterByNameSettingTypeKey).getDefaultValue();
        filterByDescription = settingTypeMap.get(FilterByDescriptionSettingTypeKey).getDefaultValue();

        filterByCategory = settingTypeMap.get(FilterByCategorySettingTypeKey).getDefaultValue();
        filterByDefaultUnits = settingTypeMap.get(FilterByDefaultUnitsSettingTypeKey).getDefaultValue();
        filterByDefaultValue = settingTypeMap.get(FilterByDefaultValueSettingTypeKey).getDefaultValue();
        filterByHandler = settingTypeMap.get(FilterByHandlerSettingTypeKey).getDefaultValue();
    }

    @Override
    public void updateSettingsFromSessionUser(UserInfo sessionUser) {
        if (sessionUser == null) {
            return;
        }

        displayNumberOfItemsPerPage = sessionUser.getUserSettingValueAsInteger(DisplayNumberOfItemsPerPageSettingTypeKey, displayNumberOfItemsPerPage);
        displayId = sessionUser.getUserSettingValueAsBoolean(DisplayIdSettingTypeKey, displayId);
        displayDescription = sessionUser.getUserSettingValueAsBoolean(DisplayDescriptionSettingTypeKey, displayDescription);

        displayCategory = sessionUser.getUserSettingValueAsBoolean(DisplayCategorySettingTypeKey, displayCategory);
        displayDefaultUnits = sessionUser.getUserSettingValueAsBoolean(DisplayDefaultUnitsSettingTypeKey, displayDefaultUnits);
        displayDefaultValue = sessionUser.getUserSettingValueAsBoolean(DisplayDefaultValueSettingTypeKey, displayDefaultValue);
        displayHandler = sessionUser.getUserSettingValueAsBoolean(DisplayHandlerSettingTypeKey, displayHandler);

        filterByName = sessionUser.getUserSettingValueAsString(FilterByNameSettingTypeKey, filterByName);
        filterByDescription = sessionUser.getUserSettingValueAsString(FilterByDescriptionSettingTypeKey, filterByDescription);

        filterByCategory = sessionUser.getUserSettingValueAsString(FilterByCategorySettingTypeKey, filterByCategory);
        filterByDefaultUnits = sessionUser.getUserSettingValueAsString(FilterByDefaultUnitsSettingTypeKey, filterByDefaultUnits);
        filterByDefaultValue = sessionUser.getUserSettingValueAsString(FilterByDefaultValueSettingTypeKey, filterByDefaultValue);
        filterByHandler = sessionUser.getUserSettingValueAsString(FilterByHandlerSettingTypeKey, filterByHandler);
    }

    @Override
    public void updateListSettingsFromListDataTable(DataTable dataTable) {
        super.updateListSettingsFromListDataTable(dataTable);
        if (dataTable == null) {
            return;
        }
        Map<String, Object> filters = dataTable.getFilters();
        filterByCategory = (String) filters.get("propertyTypeCategory.name");
        filterByDefaultUnits = (String) filters.get("defaultUnits");
        filterByDefaultValue = (String) filters.get("defaultValue");
        filterByHandler = (String) filters.get("handlerName");
    }

    @Override
    public void saveSettingsForSessionUser(UserInfo sessionUser) {
        if (sessionUser == null) {
            return;
        }

        sessionUser.setUserSettingValue(DisplayNumberOfItemsPerPageSettingTypeKey, displayNumberOfItemsPerPage);
        sessionUser.setUserSettingValue(DisplayIdSettingTypeKey, displayId);
        sessionUser.setUserSettingValue(DisplayDescriptionSettingTypeKey, displayDescription);

        sessionUser.setUserSettingValue(DisplayCategorySettingTypeKey, displayCategory);
        sessionUser.setUserSettingValue(DisplayDefaultUnitsSettingTypeKey, displayDefaultUnits);
        sessionUser.setUserSettingValue(DisplayDefaultValueSettingTypeKey, displayDefaultValue);
        sessionUser.setUserSettingValue(DisplayHandlerSettingTypeKey, displayHandler);

        sessionUser.setUserSettingValue(FilterByNameSettingTypeKey, filterByName);
        sessionUser.setUserSettingValue(FilterByDescriptionSettingTypeKey, filterByDescription);

        sessionUser.setUserSettingValue(FilterByCategorySettingTypeKey, filterByCategory);
        sessionUser.setUserSettingValue(FilterByDefaultUnitsSettingTypeKey, filterByDefaultUnits);
        sessionUser.setUserSettingValue(FilterByDefaultValueSettingTypeKey, filterByDefaultValue);
        sessionUser.setUserSettingValue(FilterByHandlerSettingTypeKey, filterByHandler);
    }

    @Override
    public void clearListFilters() {
        super.clearListFilters();
        filterByCategory = null;
        filterByDefaultUnits = null;
        filterByDefaultValue = null;
        filterByHandler = null;
    }

    @Override
    public void clearSelectFilters() {
        super.clearSelectFilters();
        selectFilterByCategory = null;
        selectFilterByDefaultUnits = null;
        selectFilterByDefaultValue = null;
        selectFilterByHandler = null;
    }

    @Override
    public boolean entityHasCategories() {
        return true;
    }

    public void prepareAddAllowedPropertyValue(String value) {
        PropertyType propertyType = getCurrent();
        List<AllowedPropertyValue> allowedPropertyValueList = propertyType.getAllowedPropertyValueList();
        AllowedPropertyValue allowedPropertyValue = new AllowedPropertyValue();
        allowedPropertyValue.setValue(value);
        allowedPropertyValue.setPropertyType(propertyType);
        allowedPropertyValueList.add(allowedPropertyValue);
    }

    public void saveAllowedPropertyValueList() {
        update();
    }

    public void deleteAllowedPropertyValue(AllowedPropertyValue allowedPropertyValue) {
        PropertyType propertyType = getCurrent();
        List<AllowedPropertyValue> allowedPropertyValueList = propertyType.getAllowedPropertyValueList();
        allowedPropertyValueList.remove(allowedPropertyValue);
    }

    public void prepareSelectPropertyTypesForComponentType(ComponentType componentType) {
        clearSelectFilters();
        resetSelectDataModel();
        List<PropertyType> selectPropertyTypeList = getEntityDbFacade().findAll();
        List<PropertyType> componentTypePropertyList = componentType.getPropertyTypeList();
        selectPropertyTypeList.removeAll(componentTypePropertyList);
        createSelectDataModel(selectPropertyTypeList);
    }

    public void prepareSelectPropertyTypesForComponent(Component component) {
        clearSelectFilters();
        resetSelectDataModel();
        List<PropertyType> selectPropertyTypeList = getEntityDbFacade().findAll();
        createSelectDataModel(selectPropertyTypeList);
    }

    public void prepareSelectPropertyTypesForComponentInstance(ComponentInstance componentInstance) {
        clearSelectFilters();
        resetSelectDataModel();
        List<PropertyType> selectPropertyTypeList = getEntityDbFacade().findAll();
        createSelectDataModel(selectPropertyTypeList);
    }

    public void prepareSelectPropertyTypesForDesign(Design design) {
        clearSelectFilters();
        resetSelectDataModel();
        List<PropertyType> selectPropertyTypeList = getEntityDbFacade().findAll();
        createSelectDataModel(selectPropertyTypeList);
    }

    public void prepareSelectPropertyTypesForDesignElement(DesignElement designElement) {
        clearSelectFilters();
        resetSelectDataModel();
        List<PropertyType> selectPropertyTypeList = getEntityDbFacade().findAll();
        createSelectDataModel(selectPropertyTypeList);
    }

    /**
     * Converter class for property type objects.
     */
    @FacesConverter(forClass = PropertyType.class)
    public static class PropertyTypeControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            try {
                PropertyTypeController controller = (PropertyTypeController) facesContext.getApplication().getELResolver().
                        getValue(facesContext.getELContext(), null, "propertyTypeController");
                return controller.getEntity(getIntegerKey(value));
            } catch (Exception ex) {
                // we cannot get entity from a given key
                logger.warn("Value " + value + "cannot be converted to property type object.");
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
            if (object instanceof PropertyType) {
                PropertyType o = (PropertyType) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + PropertyType.class.getName());
            }
        }

    }

    public Boolean getDisplayCategory() {
        return displayCategory;
    }

    public void setDisplayCategory(Boolean displayCategory) {
        this.displayCategory = displayCategory;
    }

    public Boolean getDisplayDefaultUnits() {
        return displayDefaultUnits;
    }

    public void setDisplayDefaultUnits(Boolean displayDefaultUnits) {
        this.displayDefaultUnits = displayDefaultUnits;
    }

    public Boolean getDisplayDefaultValue() {
        return displayDefaultValue;
    }

    public void setDisplayDefaultValue(Boolean displayDefaultValue) {
        this.displayDefaultValue = displayDefaultValue;
    }

    public Boolean getDisplayHandler() {
        return displayHandler;
    }

    public void setDisplayHandler(Boolean displayHandler) {
        this.displayHandler = displayHandler;
    }

    public String getFilterByCategory() {
        return filterByCategory;
    }

    public void setFilterByCategory(String filterByCategory) {
        this.filterByCategory = filterByCategory;
    }

    public String getFilterByDefaultUnits() {
        return filterByDefaultUnits;
    }

    public void setFilterByDefaultUnits(String filterByDefaultUnits) {
        this.filterByDefaultUnits = filterByDefaultUnits;
    }

    public String getFilterByDefaultValue() {
        return filterByDefaultValue;
    }

    public void setFilterByDefaultValue(String filterByDefaultValue) {
        this.filterByDefaultValue = filterByDefaultValue;
    }

    public String getFilterByHandler() {
        return filterByHandler;
    }

    public void setFilterByHandler(String filterByHandler) {
        this.filterByHandler = filterByHandler;
    }

    public Boolean getSelectDisplayCategory() {
        return selectDisplayCategory;
    }

    public void setSelectDisplayCategory(Boolean selectDisplayCategory) {
        this.selectDisplayCategory = selectDisplayCategory;
    }

    public Boolean getSelectDisplayDefaultUnits() {
        return selectDisplayDefaultUnits;
    }

    public void setSelectDisplayDefaultUnits(Boolean selectDisplayDefaultUnits) {
        this.selectDisplayDefaultUnits = selectDisplayDefaultUnits;
    }

    public Boolean getSelectDisplayDefaultValue() {
        return selectDisplayDefaultValue;
    }

    public void setSelectDisplayDefaultValue(Boolean selectDisplayDefaultValue) {
        this.selectDisplayDefaultValue = selectDisplayDefaultValue;
    }

    public Boolean getSelectDisplayHandler() {
        return selectDisplayHandler;
    }

    public void setSelectDisplayHandler(Boolean selectDisplayHandler) {
        this.selectDisplayHandler = selectDisplayHandler;
    }

    public String getSelectFilterByCategory() {
        return selectFilterByCategory;
    }

    public void setSelectFilterByCategory(String selectFilterByCategory) {
        this.selectFilterByCategory = selectFilterByCategory;
    }

    public String getSelectFilterByDefaultUnits() {
        return selectFilterByDefaultUnits;
    }

    public void setSelectFilterByDefaultUnits(String selectFilterByDefaultUnits) {
        this.selectFilterByDefaultUnits = selectFilterByDefaultUnits;
    }

    public String getSelectFilterByDefaultValue() {
        return selectFilterByDefaultValue;
    }

    public void setSelectFilterByDefaultValue(String selectFilterByDefaultValue) {
        this.selectFilterByDefaultValue = selectFilterByDefaultValue;
    }

    public String getSelectFilterByHandler() {
        return selectFilterByHandler;
    }

    public void setSelectFilterByHandler(String selectFilterByHandler) {
        this.selectFilterByHandler = selectFilterByHandler;
    }

}
