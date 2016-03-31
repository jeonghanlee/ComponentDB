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

import gov.anl.aps.cdb.portal.model.db.entities.PropertyType;
import gov.anl.aps.cdb.portal.model.db.beans.PropertyTypeDbFacade;
import gov.anl.aps.cdb.portal.model.db.entities.SettingType;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;

import java.io.Serializable;
import java.util.Map;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import org.primefaces.component.datatable.DataTable;

/**
 * Controller class for component type property type objects.
 */
@Named("componentTypePropertyTypeController")
@SessionScoped
public class ComponentTypePropertyTypeController extends CdbEntityController<PropertyType, PropertyTypeDbFacade> implements Serializable {

    /*
     * Controller specific settings 
     */
    private static final String DisplayIdSettingTypeKey = "ComponentTypePropertyType.List.Display.Id";
    private static final String DisplayNumberOfItemsPerPageSettingTypeKey = "ComponentTypePropertyType.List.Display.NumberOfItemsPerPage";
    private static final String FilterByPropertyTypeNameSettingTypeKey = "ComponentTypePropertyType.List.FilterBy.PropertyTypeName";

    @EJB
    private PropertyTypeDbFacade propertyTypeFacade;

    private String filterByPropertyTypeName = null;

    public ComponentTypePropertyTypeController() {
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
        return "componentTypePropertyType";
    }

    @Override
    public String getDisplayEntityTypeName() {
        return "component type property type";
    }

    @Override
    public String getCurrentEntityInstanceName() {
        if (getCurrent() != null) {
            return getCurrent().getName();
        }
        return "";
    }

    @Override
    public void updateSettingsFromSettingTypeDefaults(Map<String, SettingType> settingTypeMap) {
        if (settingTypeMap == null) {
            return;
        }

        displayId = Boolean.parseBoolean(settingTypeMap.get(DisplayIdSettingTypeKey).getDefaultValue());
        displayNumberOfItemsPerPage = Integer.parseInt(settingTypeMap.get(DisplayNumberOfItemsPerPageSettingTypeKey).getDefaultValue());
        filterByPropertyTypeName = settingTypeMap.get(FilterByPropertyTypeNameSettingTypeKey).getDefaultValue();
    }

    @Override
    public void updateSettingsFromSessionUser(UserInfo sessionUser) {
        if (sessionUser == null) {
            return;
        }
        displayId = sessionUser.getUserSettingValueAsBoolean(DisplayIdSettingTypeKey, displayId);
        displayNumberOfItemsPerPage = sessionUser.getUserSettingValueAsInteger(DisplayNumberOfItemsPerPageSettingTypeKey, displayNumberOfItemsPerPage);
        filterByPropertyTypeName = sessionUser.getUserSettingValueAsString(FilterByPropertyTypeNameSettingTypeKey, filterByPropertyTypeName);
    }

    @Override
    public void updateListSettingsFromListDataTable(DataTable dataTable) {
        super.updateListSettingsFromListDataTable(dataTable);
        if (dataTable == null) {
            return;
        }
        Map<String, Object> filters = dataTable.getFilters();
        filterByPropertyTypeName = (String) filters.get("propertyType.name");
    }

    @Override
    public void saveSettingsForSessionUser(UserInfo sessionUser) {
        if (sessionUser == null) {
            return;
        }

        sessionUser.setUserSettingValue(DisplayIdSettingTypeKey, displayId);
        sessionUser.setUserSettingValue(DisplayNumberOfItemsPerPageSettingTypeKey, displayNumberOfItemsPerPage);
        sessionUser.setUserSettingValue(FilterByPropertyTypeNameSettingTypeKey, filterByPropertyTypeName);
    }

    @Override
    public void clearListFilters() {
        super.clearListFilters();
        filterByPropertyTypeName = null;
    }

    public String getFilterByPropertyTypeName() {
        return filterByPropertyTypeName;
    }

    public void setFilterByPropertyTypeName(String filterByPropertyTypeName) {
        this.filterByPropertyTypeName = filterByPropertyTypeName;
    }

}
