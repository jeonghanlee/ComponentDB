/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.settings;

import gov.anl.aps.cdb.portal.controllers.UserSettingController;
import gov.anl.aps.cdb.portal.model.db.entities.SettingEntity;
import gov.anl.aps.cdb.portal.model.db.entities.SettingType;
import java.util.Map;
import org.primefaces.component.datatable.DataTable;

/**
 *
 * @author djarosz
 */
public class UserSettingSettings extends SettingsBase<UserSettingController> {
    
    private static final String DisplayNumberOfItemsPerPageSettingTypeKey = "UserSetting.List.Display.NumberOfItemsPerPage";
    
    private String filterBySettingType = null;
    private String filterByValue = null;
    

    public UserSettingSettings(UserSettingController parentController) {
        super(parentController);
    }

    @Override
    protected void updateSettingsFromSettingTypeDefaults(Map<String, SettingType> settingTypeMap) {
        displayNumberOfItemsPerPage = Integer.parseInt(settingTypeMap.get(DisplayNumberOfItemsPerPageSettingTypeKey).getDefaultValue());
    }

    @Override
    protected void updateSettingsFromSessionSettingEntity(SettingEntity settingEntity) {
        displayNumberOfItemsPerPage = settingEntity.getSettingValueAsInteger(DisplayNumberOfItemsPerPageSettingTypeKey, displayNumberOfItemsPerPage);
    }

    @Override
    protected void saveSettingsForSessionSettingEntity(SettingEntity settingEntity) {
        settingEntity.setSettingValue(DisplayNumberOfItemsPerPageSettingTypeKey, displayNumberOfItemsPerPage);
    }
    
    @Override
    public void clearListFilters() {
        super.clearListFilters();
        filterBySettingType = null;
        filterByValue = null;
    }
    
    @Override
    public void updateListSettingsFromListDataTable(DataTable dataTable) {
        super.updateListSettingsFromListDataTable(dataTable);
        if (dataTable == null) {
            return;
        }

        Map<String, Object> filters = dataTable.getFilters();
        filterBySettingType = (String) filters.get("settingType");
        filterByValue = (String) filters.get("value");
    }

    public String getFilterBySettingType() {
        return filterBySettingType;
    }

    public void setFilterBySettingType(String filterBySettingType) {
        this.filterBySettingType = filterBySettingType;
    }

    public String getFilterByValue() {
        return filterByValue;
    }

    public void setFilterByValue(String filterByValue) {
        this.filterByValue = filterByValue;
    }
    
}
