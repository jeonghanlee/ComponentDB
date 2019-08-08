package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.portal.model.db.entities.UserGroup;
import gov.anl.aps.cdb.portal.model.db.beans.UserGroupFacade;
import gov.anl.aps.cdb.portal.model.db.entities.SettingEntity;
import gov.anl.aps.cdb.portal.model.db.entities.SettingType;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;

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

@Named("userGroupController")
@SessionScoped
public class UserGroupController extends CdbEntityController<UserGroup, UserGroupFacade>implements Serializable {

    /*
     * Controller specific settings
     */
    private static final String DisplayNumberOfItemsPerPageSettingTypeKey = "UserGroup.List.Display.NumberOfItemsPerPage";
    private static final String DisplayIdSettingTypeKey = "UserGroup.List.Display.Id";
    private static final String DisplayDescriptionSettingTypeKey = "UserGroup.List.Display.Description";
    private static final String FilterByNameSettingTypeKey = "UserGroup.List.FilterBy.Name";
    private static final String FilterByDescriptionSettingTypeKey = "UserGroup.List.FilterBy.Description";

    private static final Logger logger = Logger.getLogger(UserGroupController.class.getName());

    @EJB
    private UserGroupFacade userGroupFacade;

    public UserGroupController() {
    }

    @Override
    protected UserGroupFacade getEntityDbFacade() {
        return userGroupFacade;
    }

    @Override
    protected UserGroup createEntityInstance() {
        return new UserGroup();
    }
    
    @Override
    public String getDisplayEntityTypeName() {
        return "Registered User Group";
    }

    @Override
    public String getEntityTypeName() {
        return "userGroup";
    }

    @Override
    public String getCurrentEntityInstanceName() {
        if (getCurrent() != null) {
            return getCurrent().getName();
        }
        return "";
    }

    @Override
    public List<UserGroup> getAvailableItems() {
        return super.getAvailableItems();
    }
    
    @Override
    public UserGroup findById(Integer id) {
        return userGroupFacade.findById(id);
    }


    @Override
    public void updateSettingsFromSettingTypeDefaults(Map<String, SettingType> settingTypeMap) {
        if (settingTypeMap == null) {
            return;
        }

        displayNumberOfItemsPerPage = Integer.parseInt(settingTypeMap.get(DisplayNumberOfItemsPerPageSettingTypeKey).getDefaultValue());
        displayId = Boolean.parseBoolean(settingTypeMap.get(DisplayIdSettingTypeKey).getDefaultValue());
        displayDescription = Boolean.parseBoolean(settingTypeMap.get(DisplayDescriptionSettingTypeKey).getDefaultValue());

        filterByName = settingTypeMap.get(FilterByNameSettingTypeKey).getDefaultValue();
        filterByDescription = settingTypeMap.get(FilterByDescriptionSettingTypeKey).getDefaultValue();
    }

    @Override
    public void updateSettingsFromSessionSettingEntity(SettingEntity settingEntity) {
        if (settingEntity == null) {
            return;
        }

        displayNumberOfItemsPerPage = settingEntity.getSettingValueAsInteger(DisplayNumberOfItemsPerPageSettingTypeKey, displayNumberOfItemsPerPage);
        displayId = settingEntity.getSettingValueAsBoolean(DisplayIdSettingTypeKey, displayId);
        displayDescription = settingEntity.getSettingValueAsBoolean(DisplayDescriptionSettingTypeKey, displayDescription);

        filterByName = settingEntity.getSettingValueAsString(FilterByNameSettingTypeKey, filterByName);
        filterByDescription = settingEntity.getSettingValueAsString(FilterByDescriptionSettingTypeKey, filterByDescription);
    }

    @Override
    public void saveSettingsForSessionSettingEntity(SettingEntity settingEntity) {
        if (settingEntity == null) {
            return;
        }

        settingEntity.setSettingValue(DisplayNumberOfItemsPerPageSettingTypeKey, displayNumberOfItemsPerPage);
        settingEntity.setSettingValue(DisplayIdSettingTypeKey, displayId);
        settingEntity.setSettingValue(DisplayDescriptionSettingTypeKey, displayDescription);

        settingEntity.setSettingValue(FilterByNameSettingTypeKey, filterByName);
        settingEntity.setSettingValue(FilterByDescriptionSettingTypeKey, filterByDescription);
    }

    /**
     * Converter class for user group objects.
     */
    @FacesConverter(value = "userGroupConverter", forClass = UserGroup.class)
    public static class UserGroupControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0 || value.equals("Select")) {
                return null;
            }
            try {
                UserGroupController controller = (UserGroupController) facesContext.getApplication().getELResolver().
                        getValue(facesContext.getELContext(), null, "userGroupController");
                return controller.getEntity(getIntegerKey(value));
            } catch (Exception ex) {
                // we cannot get entity from a given key
                logger.warn("Value " + value + " cannot be converted to user group object.");
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
            if (object instanceof UserGroup) {
                UserGroup o = (UserGroup) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + UserGroup.class.getName());
            }
        }

    }

}
