package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.common.constants.CdbPropertyValue;
import gov.anl.aps.cdb.portal.constants.DisplayType;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.model.db.beans.PropertyValueFacade;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyType;
import gov.anl.aps.cdb.portal.model.db.entities.SettingEntity;
import gov.anl.aps.cdb.portal.model.db.entities.SettingType;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import gov.anl.aps.cdb.portal.model.jsf.handlers.PropertyTypeHandlerFactory;
import gov.anl.aps.cdb.portal.model.jsf.handlers.PropertyTypeHandlerInterface;
import gov.anl.aps.cdb.portal.utilities.GalleryUtility;
import gov.anl.aps.cdb.portal.utilities.StorageUtility;

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

@Named("propertyValueController")
@SessionScoped
public class PropertyValueController extends CdbEntityController<PropertyValue, PropertyValueFacade> implements Serializable {

    /*
     * Controller specific settings
     */
    private static final String DisplayNumberOfItemsPerPageSettingTypeKey = "PropertyValue.List.Display.NumberOfItemsPerPage";
    private static final String DisplayIdSettingTypeKey = "PropertyValue.List.Display.Id";
    private static final String DisplayDescriptionSettingTypeKey = "PropertyValue.List.Display.Description";
    private static final String DisplayEnteredByUserSettingTypeKey = "PropertyValue.List.Display.EnteredByUser";
    private static final String DisplayEnteredOnDateTimeSettingTypeKey = "PropertyValue.List.Display.EnteredOnDateTime";
    private static final String DisplayIsDynamicSettingTypeKey = "PropertyValue.List.Display.IsDynamic";
    private static final String DisplayIsUserWriteableSettingTypeKey = "PropertyValue.List.Display.IsUserWriteable";
    private static final String DisplayTagSettingTypeKey = "PropertyValue.List.Display.Tag";
    private static final String DisplayTypeCategorySettingTypeKey = "PropertyValue.List.Display.TypeCategory";
    private static final String DisplayUnitsSettingTypeKey = "PropertyValue.List.Display.Units";
    private static final String FilterByDescriptionSettingTypeKey = "PropertyValue.List.FilterBy.Description";
    private static final String FilterByEnteredByUserSettingTypeKey = "PropertyValue.List.FilterBy.EnteredByUser";
    private static final String FilterByEnteredOnDateTimeSettingTypeKey = "PropertyValue.List.FilterBy.EnteredOnDateTime";
    private static final String FilterByIsDynamicSettingTypeKey = "PropertyValue.List.FilterBy.IsDynamic";
    private static final String FilterByIsUserWriteableSettingTypeKey = "PropertyValue.List.FilterBy.IsUserWriteable";
    private static final String FilterByTagSettingTypeKey = "PropertyValue.List.FilterBy.Tag";
    private static final String FilterByTypeSettingTypeKey = "PropertyValue.List.FilterBy.Type";
    private static final String FilterByTypeCategorySettingTypeKey = "PropertyValue.List.FilterBy.TypeCategory";
    private static final String FilterByValueSettingTypeKey = "PropertyValue.List.FilterBy.Value";
    private static final String FilterByUnitsSettingTypeKey = "PropertyValue.List.FilterBy.Units";

    private Boolean displayEnteredByUser = null;
    private Boolean displayEnteredOnDateTime = null;
    private Boolean displayIsDynamic = null;
    private Boolean displayIsUserWriteable = null;
    private Boolean displayTag = null;
    private Boolean displayTypeCategory = null;
    private Boolean displayUnits = null;

    private String filterByEnteredByUser = null;
    private String filterByEnteredOnDateTime = null;
    private String filterByIsDynamic = null;
    private String filterByIsUserWriteable = null;
    private String filterByTag = null;
    private String filterByType = null;
    private String filterByTypeCategory = null;
    private String filterByValue = null;
    private String filterByUnits = null;

    @EJB
    private gov.anl.aps.cdb.portal.model.db.beans.PropertyValueFacade propertyValueFacade;

    private static final Logger logger = Logger.getLogger(PropertyValueController.class.getName());

    public PropertyValueController() {
        super();
    }

    @Override
    protected PropertyValueFacade getEntityDbFacade() {
        return propertyValueFacade;
    }

    @Override
    protected PropertyValue createEntityInstance() {
        return new PropertyValue();
    }

    @Override
    public String getEntityTypeName() {
        return "propertyValue";
    }

    @Override
    public String getDisplayEntityTypeName() {
        return "Property Value";
    }

    @Override
    public String getCurrentEntityInstanceName() {
        if (getCurrent() != null) {
            return getCurrent().getId().toString();
        }
        return "";
    }

    @Override
    public List<PropertyValue> getAvailableItems() {
        return super.getAvailableItems();
    }

    @Override
    public void updateSettingsFromSettingTypeDefaults(Map<String, SettingType> settingTypeMap) {
        if (settingTypeMap == null) {
            return;
        }

        displayDescription = Boolean.parseBoolean(settingTypeMap.get(DisplayDescriptionSettingTypeKey).getDefaultValue());
        displayEnteredByUser = Boolean.parseBoolean(settingTypeMap.get(DisplayEnteredByUserSettingTypeKey).getDefaultValue());
        displayEnteredOnDateTime = Boolean.parseBoolean(settingTypeMap.get(DisplayEnteredOnDateTimeSettingTypeKey).getDefaultValue());
        displayId = Boolean.parseBoolean(settingTypeMap.get(DisplayIdSettingTypeKey).getDefaultValue());
        displayIsDynamic = Boolean.parseBoolean(settingTypeMap.get(DisplayIsDynamicSettingTypeKey).getDefaultValue());
        displayIsUserWriteable = Boolean.parseBoolean(settingTypeMap.get(DisplayIsUserWriteableSettingTypeKey).getDefaultValue());
        displayNumberOfItemsPerPage = Integer.parseInt(settingTypeMap.get(DisplayNumberOfItemsPerPageSettingTypeKey).getDefaultValue());
        displayTag = Boolean.parseBoolean(settingTypeMap.get(DisplayTagSettingTypeKey).getDefaultValue());
        displayTypeCategory = Boolean.parseBoolean(settingTypeMap.get(DisplayTypeCategorySettingTypeKey).getDefaultValue());
        displayUnits = Boolean.parseBoolean(settingTypeMap.get(DisplayUnitsSettingTypeKey).getDefaultValue());

        filterByDescription = settingTypeMap.get(FilterByDescriptionSettingTypeKey).getDefaultValue();
        filterByEnteredByUser = settingTypeMap.get(FilterByEnteredByUserSettingTypeKey).getDefaultValue();
        filterByEnteredOnDateTime = settingTypeMap.get(FilterByEnteredOnDateTimeSettingTypeKey).getDefaultValue();
        filterByIsDynamic = settingTypeMap.get(FilterByIsDynamicSettingTypeKey).getDefaultValue();
        filterByIsUserWriteable = settingTypeMap.get(FilterByIsUserWriteableSettingTypeKey).getDefaultValue();
        filterByTag = settingTypeMap.get(FilterByTagSettingTypeKey).getDefaultValue();
        filterByType = settingTypeMap.get(FilterByTypeSettingTypeKey).getDefaultValue();
        filterByTypeCategory = settingTypeMap.get(FilterByTypeCategorySettingTypeKey).getDefaultValue();
        filterByUnits = settingTypeMap.get(FilterByUnitsSettingTypeKey).getDefaultValue();
        filterByValue = settingTypeMap.get(FilterByValueSettingTypeKey).getDefaultValue();

    }

    @Override
    public void updateSettingsFromSessionSettingEntity(SettingEntity settingEntity) {
        if (settingEntity == null) {
            return;
        }

        displayDescription = settingEntity.getSettingValueAsBoolean(DisplayDescriptionSettingTypeKey, displayDescription);
        displayEnteredByUser = settingEntity.getSettingValueAsBoolean(DisplayEnteredByUserSettingTypeKey, displayEnteredByUser);
        displayEnteredOnDateTime = settingEntity.getSettingValueAsBoolean(DisplayEnteredOnDateTimeSettingTypeKey, displayEnteredOnDateTime);
        displayId = settingEntity.getSettingValueAsBoolean(DisplayIdSettingTypeKey, displayId);
        displayIsDynamic = settingEntity.getSettingValueAsBoolean(DisplayIsDynamicSettingTypeKey, displayIsDynamic);
        displayIsUserWriteable = settingEntity.getSettingValueAsBoolean(DisplayIsUserWriteableSettingTypeKey, displayIsUserWriteable);
        displayNumberOfItemsPerPage = settingEntity.getSettingValueAsInteger(DisplayNumberOfItemsPerPageSettingTypeKey, displayNumberOfItemsPerPage);
        displayTag = settingEntity.getSettingValueAsBoolean(DisplayTagSettingTypeKey, displayTag);
        displayTypeCategory = settingEntity.getSettingValueAsBoolean(DisplayTypeCategorySettingTypeKey, displayTypeCategory);
        displayUnits = settingEntity.getSettingValueAsBoolean(DisplayUnitsSettingTypeKey, displayUnits);

        filterByDescription = settingEntity.getSettingValueAsString(FilterByDescriptionSettingTypeKey, filterByDescription);
        filterByEnteredByUser = settingEntity.getSettingValueAsString(FilterByEnteredByUserSettingTypeKey, filterByEnteredByUser);
        filterByEnteredOnDateTime = settingEntity.getSettingValueAsString(FilterByEnteredOnDateTimeSettingTypeKey, filterByEnteredOnDateTime);
        filterByIsDynamic = settingEntity.getSettingValueAsString(FilterByIsDynamicSettingTypeKey, filterByIsDynamic);
        filterByIsUserWriteable = settingEntity.getSettingValueAsString(FilterByIsUserWriteableSettingTypeKey, filterByIsUserWriteable);
        filterByTag = settingEntity.getSettingValueAsString(FilterByTagSettingTypeKey, filterByTag);
        filterByType = settingEntity.getSettingValueAsString(FilterByTypeSettingTypeKey, filterByType);
        filterByTypeCategory = settingEntity.getSettingValueAsString(FilterByTypeCategorySettingTypeKey, filterByTypeCategory);
        filterByUnits = settingEntity.getSettingValueAsString(FilterByUnitsSettingTypeKey, filterByUnits);
        filterByValue = settingEntity.getSettingValueAsString(FilterByValueSettingTypeKey, filterByValue);
    }

    @Override
    public void updateListSettingsFromListDataTable(DataTable dataTable) {
        super.updateListSettingsFromListDataTable(dataTable);
        if (dataTable == null) {
            return;
        }
        Map<String, Object> filters = dataTable.getFilters();
        filterByEnteredByUser = (String) filters.get("enteredByUser");
        filterByEnteredOnDateTime = (String) filters.get("enteredOnDateTime");
        filterByIsDynamic = (String) filters.get("isDynamic");
        filterByIsUserWriteable = (String) filters.get("isUserWriteable");
        filterByTag = (String) filters.get("tag");
        filterByType = (String) filters.get("type");
        filterByTypeCategory = (String) filters.get("typeCategory");
        filterByUnits = (String) filters.get("units");
        filterByValue = (String) filters.get("value");
    }

    @Override
    public void saveSettingsForSessionSettingEntity(SettingEntity settingEntity) {
        if (settingEntity == null) {
            return;
        }

        settingEntity.setSettingValue(DisplayDescriptionSettingTypeKey, displayDescription);
        settingEntity.setSettingValue(DisplayIdSettingTypeKey, displayId);
        settingEntity.setSettingValue(DisplayNumberOfItemsPerPageSettingTypeKey, displayNumberOfItemsPerPage);
        settingEntity.setSettingValue(DisplayEnteredByUserSettingTypeKey, displayEnteredByUser);
        settingEntity.setSettingValue(DisplayEnteredOnDateTimeSettingTypeKey, displayEnteredOnDateTime);
        settingEntity.setSettingValue(DisplayIsDynamicSettingTypeKey, displayIsDynamic);
        settingEntity.setSettingValue(DisplayIsUserWriteableSettingTypeKey, displayIsUserWriteable);
        settingEntity.setSettingValue(DisplayTagSettingTypeKey, displayTag);
        settingEntity.setSettingValue(DisplayTypeCategorySettingTypeKey, displayTypeCategory);
        settingEntity.setSettingValue(DisplayUnitsSettingTypeKey, displayUnits);

        settingEntity.setSettingValue(FilterByDescriptionSettingTypeKey, filterByDescription);
        settingEntity.setSettingValue(FilterByEnteredByUserSettingTypeKey, filterByEnteredByUser);
        settingEntity.setSettingValue(FilterByEnteredOnDateTimeSettingTypeKey, filterByEnteredOnDateTime);
        settingEntity.setSettingValue(FilterByIsDynamicSettingTypeKey, filterByIsDynamic);
        settingEntity.setSettingValue(FilterByIsUserWriteableSettingTypeKey, filterByIsUserWriteable);
        settingEntity.setSettingValue(FilterByTagSettingTypeKey, filterByTag);
        settingEntity.setSettingValue(FilterByTypeSettingTypeKey, filterByType);
        settingEntity.setSettingValue(FilterByTypeCategorySettingTypeKey, filterByTypeCategory);
        settingEntity.setSettingValue(FilterByUnitsSettingTypeKey, filterByUnits);
        settingEntity.setSettingValue(FilterByValueSettingTypeKey, filterByValue);

    }

    @Override
    public void clearListFilters() {
        super.clearListFilters();
        filterByEnteredByUser = null;
        filterByEnteredOnDateTime = null;
        filterByIsDynamic = null;
        filterByIsUserWriteable = null;
        filterByTag = null;
        filterByType = null;
        filterByTypeCategory = null;
        filterByUnits = null;
        filterByValue = null;
    }

    public PropertyTypeHandlerInterface getPropertyTypeHandler(PropertyValue propertyValue) {
        return PropertyTypeHandlerFactory.getHandler(propertyValue);
    }

    public DisplayType configurePropertyValueDisplay(PropertyValue propertyValue) {
        PropertyTypeHandlerInterface propertyTypeHandler = PropertyTypeHandlerFactory.getHandler(propertyValue);
        propertyTypeHandler.setInfoActionCommand(propertyValue);
        propertyTypeHandler.setDisplayValue(propertyValue);
        propertyTypeHandler.setTargetValue(propertyValue);
        PropertyType propertyType = propertyValue.getPropertyType();
        DisplayType displayType = propertyTypeHandler.getValueDisplayType();
        if (displayType == null) {
            displayType = DisplayType.FREE_FORM_TEXT;
            if (propertyType.hasAllowedPropertyValues()) {
                displayType = DisplayType.SELECTED_TEXT;
            }
        }
        propertyType.setDisplayType(displayType);
        propertyValue.setHandlerInfoSet(true);
        propertyTypeHandler.resetOneTimeUseVariables();
        return displayType;
    }

    public String getPropertyEditPage(PropertyValue propertyValue) {
        PropertyTypeHandlerInterface propertyTypeHandler = PropertyTypeHandlerFactory.getHandler(propertyValue);
        return propertyTypeHandler.getPropertyEditPage();
    }

    public DisplayType getPropertyValueDisplayType(PropertyValue propertyValue) {
        if (propertyValue == null) {
            return DisplayType.FREE_FORM_TEXT;
        }
        String displayValue = propertyValue.getDisplayValue();
        DisplayType result = propertyValue.getPropertyType().getDisplayType();
        if (result == null || displayValue == null || displayValue.isEmpty() || !propertyValue.isHandlerInfoSet()) {
            result = configurePropertyValueDisplay(propertyValue);
        }
        return result;
    }

    public boolean displayFreeFormTextValue(PropertyValue propertyValue) {
        return getPropertyValueDisplayType(propertyValue).equals(DisplayType.FREE_FORM_TEXT);
    }

    public boolean displaySelectedTextValue(PropertyValue propertyValue) {
        return getPropertyValueDisplayType(propertyValue).equals(DisplayType.SELECTED_TEXT);
    }

    public boolean displayImageValue(PropertyValue propertyValue) {
        return getPropertyValueDisplayType(propertyValue).equals(DisplayType.IMAGE);
    }

    public boolean displayHttpLinkValue(PropertyValue propertyValue) {
        return getPropertyValueDisplayType(propertyValue).equals(DisplayType.HTTP_LINK);
    }

    public boolean displayDocumentValue(PropertyValue propertyValue) {
        return getPropertyValueDisplayType(propertyValue).equals(DisplayType.DOCUMENT);
    }

    public boolean displayBooleanValue(PropertyValue propertyValue) {
        return getPropertyValueDisplayType(propertyValue).equals(DisplayType.BOOLEAN);
    }

    public boolean displayDateValue(PropertyValue propertyValue) {
        return getPropertyValueDisplayType(propertyValue).equals(DisplayType.DATE);
    }

    public boolean displayTableRecordReference(PropertyValue propertyValue) {
        return getPropertyValueDisplayType(propertyValue).equals(DisplayType.TABLE_RECORD_REFERENCE);
    }

    public boolean displayInfoActionValue(PropertyValue propertyValue) {
        return getPropertyValueDisplayType(propertyValue).equals(DisplayType.INFO_ACTION);
    }

    public static String getOriginalImageApplicationPath(PropertyValue propertyValue) {
        return getOriginalImageApplicationPathByValue(propertyValue.getValue());
    }

    public static String getOriginalImageApplicationPathByValue(String imageName) {
        if (imageName != null) {
            return StorageUtility.getApplicationPropertyValueImagePath(imageName);
        }
        return null;
    }

    public static String getThumbnailImagePath(PropertyValue propertyValue) {
        return getThumbnailImagePathByValue(propertyValue.getValue());
    }

    public static String getThumbnailImagePathByValue(String imageName) {
        if (imageName != null) {
            return StorageUtility.getPropertyValueImagePath(imageName, CdbPropertyValue.THUMBNAIL_IMAGE_EXTENSION);
        }
        return null;
    }

    public static String getScaledImagePath(PropertyValue propertyValue) {
        return getScaledImagePathByValue(propertyValue.getValue());
    }

    public static String getScaledImagePathByValue(String imageName) {
        if (imageName != null) {
            return StorageUtility.getPropertyValueImagePath(imageName, CdbPropertyValue.SCALED_IMAGE_EXTENSION);
        }
        return null;
    }

    public Boolean getDisplayEnteredByUser() {
        return displayEnteredByUser;
    }

    public void setDisplayEnteredByUser(Boolean displayEnteredByUser) {
        this.displayEnteredByUser = displayEnteredByUser;
    }

    public Boolean getDisplayEnteredOnDateTime() {
        return displayEnteredOnDateTime;
    }

    public void setDisplayEnteredOnDateTime(Boolean displayEnteredOnDateTime) {
        this.displayEnteredOnDateTime = displayEnteredOnDateTime;
    }

    public Boolean getDisplayIsDynamic() {
        return displayIsDynamic;
    }

    public void setDisplayIsDynamic(Boolean displayIsDynamic) {
        this.displayIsDynamic = displayIsDynamic;
    }

    public Boolean getDisplayIsUserWriteable() {
        return displayIsUserWriteable;
    }

    public void setDisplayIsUserWriteable(Boolean displayIsUserWriteable) {
        this.displayIsUserWriteable = displayIsUserWriteable;
    }

    public Boolean getDisplayTypeCategory() {
        return displayTypeCategory;
    }

    public void setDisplayTypeCategory(Boolean displayTypeCategory) {
        this.displayTypeCategory = displayTypeCategory;
    }

    public Boolean getDisplayUnits() {
        return displayUnits;
    }

    public void setDisplayUnits(Boolean displayUnits) {
        this.displayUnits = displayUnits;
    }

    public String getFilterByEnteredByUser() {
        return filterByEnteredByUser;
    }

    public void setFilterByEnteredByUser(String filterByEnteredByUser) {
        this.filterByEnteredByUser = filterByEnteredByUser;
    }

    public String getFilterByEnteredOnDateTime() {
        return filterByEnteredOnDateTime;
    }

    public void setFilterByEnteredOnDateTime(String filterByEnteredOnDateTime) {
        this.filterByEnteredOnDateTime = filterByEnteredOnDateTime;
    }

    public String getFilterByIsDynamic() {
        return filterByIsDynamic;
    }

    public void setFilterByIsDynamic(String filterByIsDynamic) {
        this.filterByIsDynamic = filterByIsDynamic;
    }

    public String getFilterByIsUserWriteable() {
        return filterByIsUserWriteable;
    }

    public void setFilterByIsUserWriteable(String filterByIsUserWriteable) {
        this.filterByIsUserWriteable = filterByIsUserWriteable;
    }

    public Boolean getDisplayTag() {
        return displayTag;
    }

    public void setDisplayTag(Boolean displayTag) {
        this.displayTag = displayTag;
    }

    public String getFilterByTag() {
        return filterByTag;
    }

    public void setFilterByTag(String filterByTag) {
        this.filterByTag = filterByTag;
    }

    public String getFilterByType() {
        return filterByType;
    }

    public void setFilterByType(String filterByType) {
        this.filterByType = filterByType;
    }

    public String getFilterByTypeCategory() {
        return filterByTypeCategory;
    }

    public void setFilterByTypeCategory(String filterByTypeCategory) {
        this.filterByTypeCategory = filterByTypeCategory;
    }

    public String getFilterByValue() {
        return filterByValue;
    }

    public void setFilterByValue(String filterByValue) {
        this.filterByValue = filterByValue;
    }

    public String getFilterByUnits() {
        return filterByUnits;
    }

    public void setFilterByUnits(String filterByUnits) {
        this.filterByUnits = filterByUnits;
    }

    public boolean isPropertyValueViewable(PropertyValue propertyValue) {
        return GalleryUtility.viewableFileName(propertyValue.getValue());
    }

    /**
     * Converter class for property value objects.
     */
    @FacesConverter(forClass = PropertyValue.class)
    public static class PropertyValueControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            try {
                if (value == null || value.length() == 0) {
                    return null;
                }
                PropertyValueController controller = (PropertyValueController) facesContext.getApplication().getELResolver().
                        getValue(facesContext.getELContext(), null, "propertyValueController");
                return controller.getEntity(getIntegerKey(value));
            } catch (Exception ex) {
                // we cannot get entity from a given key
                logger.warn("Value " + value + " cannot be converted to property value object.");
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
            if (object instanceof PropertyValue) {
                PropertyValue o = (PropertyValue) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + PropertyValue.class.getName());
            }
        }

    }

}
