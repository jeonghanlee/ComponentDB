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

import gov.anl.aps.cdb.common.constants.CdbRole;
import gov.anl.aps.cdb.common.exceptions.AuthorizationError;
import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.common.exceptions.InvalidRequest;
import gov.anl.aps.cdb.portal.model.db.beans.CdbEntityDbFacade;
import gov.anl.aps.cdb.portal.model.db.beans.LogTopicDbFacade;
import gov.anl.aps.cdb.portal.model.db.beans.SettingTypeDbFacade;
import gov.anl.aps.cdb.portal.model.db.entities.CdbEntity;
import gov.anl.aps.cdb.portal.model.db.entities.EntityInfo;
import gov.anl.aps.cdb.portal.model.db.entities.Log;
import gov.anl.aps.cdb.portal.model.db.entities.LogTopic;
import gov.anl.aps.cdb.portal.model.db.entities.SettingType;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import gov.anl.aps.cdb.portal.model.db.entities.UserSetting;
import gov.anl.aps.cdb.portal.model.db.utilities.LogUtility;
import gov.anl.aps.cdb.portal.utilities.AuthorizationUtility;
import gov.anl.aps.cdb.common.utilities.CollectionUtility;
import gov.anl.aps.cdb.portal.utilities.SearchResult;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import gov.anl.aps.cdb.portal.view.jsf.components.CdbCsvExporter;
import gov.anl.aps.cdb.portal.view.jsf.components.CdbExcelExporter;
import gov.anl.aps.cdb.portal.view.jsf.components.CdbPdfExporter;
import gov.anl.aps.cdb.portal.view.jsf.utilities.UiComponentUtility;
import gov.anl.aps.cdb.common.utilities.StringUtility;
import java.io.IOException;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.component.export.Exporter;

/**
 * Base class for all CDB entity controllers. It encapsulates common
 * functionality related to entity creation, views, updates and deletion,
 * manages user settings and session customization relevant for a given entity
 * type, entity search, etc.
 *
 * @param <EntityType> CDB entity type
 * @param <FacadeType> CDB DB facade type
 */
public abstract class CdbEntityController<EntityType extends CdbEntity, FacadeType extends CdbEntityDbFacade<EntityType>> implements Serializable {

    private static final Logger logger = Logger.getLogger(CdbEntityController.class.getName());

    @EJB
    private SettingTypeDbFacade settingTypeFacade;
    @EJB
    private LogTopicDbFacade logTopicFacade;

    protected EntityType current = null;

    protected DataModel listDataModel = null;
    protected DataTable listDataTable = null;
    protected boolean listDataModelReset = true;
    protected List<EntityType> filteredObjectList = null;

    protected Date settingsTimestamp = null;
    protected List<SettingType> settingTypeList;
    protected Map<String, SettingType> settingTypeMap;
    protected boolean settingsInitializedFromDefaults = false;

    protected DataModel selectDataModel = null;
    protected DataTable selectDataTable = null;
    protected boolean selectDataModelReset = false;
    protected EntityType selectedObject = null;
    protected List<EntityType> selectedObjectList = null;

    protected String logText = null;
    protected Integer logTopicId = null;

    protected Integer displayNumberOfItemsPerPage = null;
    protected Boolean displayId = null;
    protected Boolean displayDescription = null;
    protected Boolean displayOwnerUser = null;
    protected Boolean displayOwnerGroup = null;
    protected Boolean displayCreatedByUser = null;
    protected Boolean displayCreatedOnDateTime = null;
    protected Boolean displayLastModifiedByUser = null;
    protected Boolean displayLastModifiedOnDateTime = null;

    protected String filterByName = null;
    protected String filterByDescription = null;
    protected String filterByOwnerUser = null;
    protected String filterByOwnerGroup = null;
    protected String filterByCreatedByUser = null;
    protected String filterByCreatedOnDateTime = null;
    protected String filterByLastModifiedByUser = null;
    protected String filterByLastModifiedOnDateTime = null;

    protected Integer selectDisplayNumberOfItemsPerPage = null;
    protected Boolean selectDisplayId = false;
    protected Boolean selectDisplayDescription = false;
    protected Boolean selectDisplayOwnerUser = true;
    protected Boolean selectDisplayOwnerGroup = true;
    protected Boolean selectDisplayCreatedByUser = false;
    protected Boolean selectDisplayCreatedOnDateTime = false;
    protected Boolean selectDisplayLastModifiedByUser = false;
    protected Boolean selectDisplayLastModifiedOnDateTime = false;

    protected String selectFilterByName = null;
    protected String selectFilterByDescription = null;
    protected String selectFilterByOwnerUser = null;
    protected String selectFilterByOwnerGroup = null;
    protected String selectFilterByCreatedByUser = null;
    protected String selectFilterByCreatedOnDateTime = null;
    protected String selectFilterByLastModifiedByUser = null;
    protected String selectFilterByLastModifiedOnDateTime = null;

    protected String breadcrumbViewParam = null;
    protected String breadcrumbObjectIdViewParam = null;

    private boolean searchHasResults = false;
    private String searchString = null;
    private boolean caseInsensitive = true;
    private LinkedList<SearchResult> searchResultList;

    /**
     * Default constructor.
     */
    public CdbEntityController() {
    }

    /**
     * Initialize controller and update its settings.
     */
    @PostConstruct
    public void initialize() {
        updateSettings();
    }

    /**
     * Navigate to invalid request error page.
     *
     * @param error error message
     */
    public void handleInvalidSessionRequest(String error) {
        SessionUtility.setLastSessionError(error);
        SessionUtility.navigateTo("/views/error/invalidRequest?faces-redirect=true");
    }

    /**
     * Reset log text and topic fields.
     */
    public void resetLogText() {
        logText = "";
        logTopicId = null;
    }

    /**
     * Get list of setting types.
     *
     * If not set, this list is retrieved from the database.
     *
     * @return setting type list
     */
    public List<SettingType> getSettingTypeList() {
        if (settingTypeList == null) {
            settingTypeList = settingTypeFacade.findAll();
        }
        return settingTypeList;
    }

    /**
     * Get setting type map.
     *
     * If not set, this map is constructed from list of setting types retrieved
     * from the database.
     *
     * @return setting type map
     */
    public Map<String, SettingType> getSettingTypeMap() {
        if (settingTypeMap == null) {
            settingTypeMap = new HashMap<>();
            for (SettingType settingType : getSettingTypeList()) {
                settingTypeMap.put(settingType.getName(), settingType);
            }
        }
        return settingTypeMap;
    }

    /**
     * Parse setting value as integer.
     *
     * @param settingValue setting string value
     * @return integer value, or null in case string value cannot be parsed
     */
    public static Integer parseSettingValueAsInteger(String settingValue) {
        try {
            return Integer.parseInt(settingValue);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    /**
     * Abstract method for returning entity DB facade.
     *
     * @return entity DB facade
     */
    protected abstract FacadeType getEntityDbFacade();

    /**
     * Abstract method for creating new entity instance.
     *
     * @return created entity instance
     */
    protected abstract EntityType createEntityInstance();

    /**
     * Abstract method for retrieving entity type name.
     *
     * @return entity type name
     */
    public abstract String getEntityTypeName();

    /**
     * Get entity type group name.
     *
     * By default this method returns null, and should be overridden in derived
     * controllers when corresponding entities have groups.
     *
     * @return entity type group name
     */
    public String getEntityTypeGroupName() {
        return null;
    }

    /**
     * Get entity type category name.
     *
     * By default this method returns null, and should be overridden in derived
     * controllers when corresponding entities have categories.
     *
     * @return entity type category name
     */
    public String getEntityTypeCategoryName() {
        return null;
    }

    /**
     * Get entity type type name.
     *
     * By default this method returns null, and should be overridden in derived
     * controllers when corresponding entities have types.
     *
     * @return entity type type name
     */
    public String getEntityTypeTypeName() {
        return null;
    }

    /**
     * Get display string for entity type name.
     *
     * By default this method simply returns entity type name, and should be
     * overridden in derived controllers for entities with complex names (i.e.,
     * those that consist of two or more words).
     *
     * @return entity type name display string
     */
    public String getDisplayEntityTypeName() {
        return getEntityTypeName();
    }

    /**
     * Get current entity instance name.
     *
     * By default this method returns null, and should be overridden in derived
     * controllers.
     *
     * @return current entity instance name
     */
    public abstract String getCurrentEntityInstanceName();

    /**
     * Get current entity instance.
     *
     * @return entity
     */
    public EntityType getCurrent() {
        return current;
    }

    /**
     * Find entity instance by id.
     *
     * @param id entity instance id
     * @return entity instance
     */
    public EntityType findById(Integer id) {
        return null;
    }

    /**
     * Set current entity instance.
     *
     * @param current entity instance
     */
    public void setCurrent(EntityType current) {
        this.current = current;
    }

    /**
     * Get entity info object that belongs to the given entity.
     *
     * By default this method returns null. It should be overridden in derived
     * controllers for those entities that contain entity info object.
     *
     * @param entity entity object
     * @return entity info
     */
    public EntityInfo getEntityInfo(EntityType entity) {
        return null;
    }

    /**
     * Process view request parameters.
     *
     * If request is not valid, user will be redirected to appropriate error
     * page.
     */
    public void processViewRequestParams() {
        try {
            EntityType entity = selectByViewRequestParams();
            if (entity != null) {
                prepareEntityView(entity);
            }
        } catch (CdbException ex) {
            handleInvalidSessionRequest(ex.getErrorMessage());
        }
    }

    /**
     * Set breadcrumb variables from request parameters.
     */
    protected void setBreadcrumbRequestParams() {
        if (breadcrumbViewParam == null) {
            breadcrumbViewParam = SessionUtility.getRequestParameterValue("breadcrumb");
        }
        if (breadcrumbObjectIdViewParam == null) {
            breadcrumbObjectIdViewParam = SessionUtility.getRequestParameterValue("breadcrumbObjectId");
        }
    }

    /**
     * Select current entity instance for view from request parameters.
     *
     * @return selected entity instance
     * @throws CdbException in case of invalid request parameter values
     */
    public EntityType selectByViewRequestParams() throws CdbException {
        setBreadcrumbRequestParams();
        Integer idParam = null;
        String paramValue = SessionUtility.getRequestParameterValue("id");
        try {
            if (paramValue != null) {
                idParam = Integer.parseInt(paramValue);
            }
        } catch (NumberFormatException ex) {
            throw new InvalidRequest("Invalid value supplied for " + getDisplayEntityTypeName() + " id: " + paramValue);
        }
        if (idParam != null) {
            EntityType entity = findById(idParam);
            if (entity == null) {
                throw new InvalidRequest(StringUtility.capitalize(getDisplayEntityTypeName()) + " id " + idParam + " does not exist.");
            }
            setCurrent(entity);
            return entity;
        } else if (current == null || current.getId() == null) {
            throw new InvalidRequest(StringUtility.capitalize(getDisplayEntityTypeName()) + " has not been selected.");
        }
        return current;
    }

    /**
     * Process edit request parameters.
     *
     * If request is not valid or not authorized, user will be redirected to
     * appropriate error page.
     */
    public void processEditRequestParams() {
        try {
            selectByEditRequestParams();
        } catch (CdbException ex) {
            handleInvalidSessionRequest(ex.getErrorMessage());
        }
    }

    /**
     * Select current entity instance for edit from request parameters.
     *
     * @return selected entity instance
     * @throws CdbException in case of invalid request parameter values
     */
    public EntityType selectByEditRequestParams() throws CdbException {
        setBreadcrumbRequestParams();
        Integer idParam = null;
        String paramValue = SessionUtility.getRequestParameterValue("id");
        try {
            if (paramValue != null) {
                idParam = Integer.parseInt(paramValue);
            }
        } catch (NumberFormatException ex) {
            throw new InvalidRequest("Invalid value supplied for " + getDisplayEntityTypeName() + " id: " + paramValue);
        }
        if (idParam != null) {
            EntityType entity = findById(idParam);
            if (entity == null) {
                throw new InvalidRequest(StringUtility.capitalize(getDisplayEntityTypeName()) + " id " + idParam + " does not exist.");
            }
            setCurrent(entity);
        }

        if (current == null || current.getId() == null) {
            throw new InvalidRequest(StringUtility.capitalize(getDisplayEntityTypeName()) + " has not been selected.");
        }

        // Make sure user is logged in
        UserInfo sessionUser = (UserInfo) SessionUtility.getUser();
        if (sessionUser == null) {
            SessionUtility.pushViewOnStack("/views/" + getEntityTypeName() + "/edit.xhtml?id=" + idParam + "&faces-redirect=true");
            SessionUtility.navigateTo("/views/login.xhtml?faces-redirect=true");
            return null;
        } else {
            CdbRole sessionRole = (CdbRole) SessionUtility.getRole();
            if (sessionRole != CdbRole.ADMIN) {
                // Make sure user is authorized to edit entity
                // Try entity info first, then entity itself second
                boolean userAuthorized = AuthorizationUtility.isEntityWriteableByUser(getEntityInfo(current), sessionUser);
                if (!userAuthorized) {
                    userAuthorized = AuthorizationUtility.isEntityWriteableByUser(current, sessionUser);
                    if (!userAuthorized) {
                        throw new AuthorizationError("User " + sessionUser.getUsername() + " is not authorized to edit "
                                + getDisplayEntityTypeName() + " object with id " + current.getId() + ".");
                    }
                }
            }
        }
        return current;
    }

    /**
     * Process create request parameters.
     *
     * If request is not valid or not authorized, user will be redirected to
     * appropriate error page.
     */
    public void processCreateRequestParams() {
        try {
            selectByCreateRequestParams();
        } catch (CdbException ex) {
            handleInvalidSessionRequest(ex.getErrorMessage());
        }
    }

    /**
     * Create new entity instance based on request parameters.
     *
     * @return new entity instance
     * @throws CdbException in case of invalid request parameter values
     */
    public EntityType selectByCreateRequestParams() throws CdbException {
        setBreadcrumbRequestParams();

        // Make sure user is logged in
        UserInfo sessionUser = (UserInfo) SessionUtility.getUser();
        if (sessionUser == null) {
            SessionUtility.pushViewOnStack("/views/" + getEntityTypeName() + "/create.xhtml?faces-redirect=true");
            SessionUtility.navigateTo("/views/login.xhtml?faces-redirect=true");
        } else {
            CdbRole sessionRole = (CdbRole) SessionUtility.getRole();
            if (sessionRole != CdbRole.ADMIN) {
                // Make sure user is authorized to create entity
                boolean userAuthorized = entityCanBeCreatedByUsers();
                if (!userAuthorized) {
                    throw new AuthorizationError("User " + sessionUser.getUsername() + " is not authorized to create "
                            + getDisplayEntityTypeName() + " objects.");
                }
            }
        }
        return null;
    }

    /**
     * Get selected entity instance, or create new instance if none has been
     * selected previously.
     *
     * @return entity instance
     */
    public EntityType getSelected() {
        if (current == null) {
            current = createEntityInstance();
        }
        return current;
    }

    /**
     * Listener for updating controller session settings.
     *
     * @param actionEvent event
     */
    public void updateSettingsActionListener(ActionEvent actionEvent) {
        updateSettings();
    }

    /**
     * Update controller session settings based on session user and settings
     * modification date.
     */
    public void updateSettings() {
        try {
            UserInfo sessionUser = (UserInfo) SessionUtility.getUser();
            boolean settingsUpdated = false;
            if (sessionUser != null) {
                List<UserSetting> userSettingList = sessionUser.getUserSettingList();
                if (userSettingList != null && !userSettingList.isEmpty() && sessionUser.areUserSettingsModifiedAfterDate(settingsTimestamp)) {
                    updateSettingsFromSessionUser(sessionUser);
                    settingsUpdated = true;
                }
            }

            if (!settingsUpdated && !settingsInitializedFromDefaults) {
                settingsInitializedFromDefaults = true;
                updateSettingsFromSettingTypeDefaults(getSettingTypeMap());
            }
        } catch (Exception ex) {
            logger.error(ex);
        }
    }

    /**
     * Customize display for entity list.
     *
     * @return current view URL for page reload
     */
    public String customizeListDisplay() {
        String returnPage = SessionUtility.getCurrentViewId() + "?faces-redirect=true";
        logger.debug("Returning to page: " + returnPage);
        return returnPage;
    }

    /**
     * Customize display for selection dialog.
     *
     * @return current view URL for page reload
     */
    public String customizeSelectDisplay() {
        resetSelectDataModel();
        String returnPage = SessionUtility.getCurrentViewId() + "?faces-redirect=true";
        logger.debug("Returning to page: " + returnPage);
        return returnPage;
    }

    /**
     * Update controller session settings with setting type default values.
     *
     * This method should be overridden in any derived controller class that has
     * its own settings.
     *
     * @param settingTypeMap map of setting types
     */
    public void updateSettingsFromSettingTypeDefaults(Map<String, SettingType> settingTypeMap) {
    }

    /**
     * Update controller session settings with user-specific values from the
     * database.
     *
     * This method should be overridden in any derived controller class that has
     * its own settings.
     *
     * @param sessionUser current session user
     */
    public void updateSettingsFromSessionUser(UserInfo sessionUser) {
    }

    /**
     * Save controller session settings for the current user.
     *
     * This method should be overridden in any derived controller class that has
     * its own settings.
     *
     * @param sessionUser current session user
     */
    public void saveSettingsForSessionUser(UserInfo sessionUser) {
    }

    /**
     * Listener for saving session user settings.
     *
     * @param actionEvent event
     */
    public void saveListSettingsForSessionUserActionListener(ActionEvent actionEvent) {
        logger.debug("Saving settings");
        UserInfo sessionUser = (UserInfo) SessionUtility.getUser();
        if (sessionUser != null) {
            logger.debug("Updating list settings for session user");
            saveSettingsForSessionUser(sessionUser);
            resetListDataModel();
            settingsTimestamp = new Date();
        }
    }

    /**
     * Update entity list display settings using current data table values.
     *
     * This method should be overridden in any derived controller class that has
     * its own settings.
     *
     * @param dataTable entity list data table
     */
    public void updateListSettingsFromListDataTable(DataTable dataTable) {
        if (dataTable == null) {
            return;
        }

        Map<String, Object> filters = dataTable.getFilters();
        filterByName = (String) filters.get("name");
        filterByDescription = (String) filters.get("description");
        filterByOwnerUser = (String) filters.get("entityInfo.ownerUser.username");
        filterByOwnerGroup = (String) filters.get("entityInfo.ownerUserGroup.name");
        filterByCreatedByUser = (String) filters.get("entityInfo.createdByUser.username");
        filterByCreatedOnDateTime = (String) filters.get("entityInfo.createdOnDateTime");
        filterByLastModifiedByUser = (String) filters.get("entityInfo.lastModifiedByUser.username");
        filterByLastModifiedOnDateTime = (String) filters.get("entityInfo.lastModifiedOnDateTime");
    }

    /**
     * Clear entity list filters.
     *
     * This method should be overridden in any derived controller class that has
     * its own filters.
     */
    public void clearListFilters() {
        filterByName = null;
        filterByDescription = null;
        filterByOwnerUser = null;
        filterByOwnerGroup = null;
        filterByCreatedByUser = null;
        filterByCreatedOnDateTime = null;
        filterByLastModifiedByUser = null;
        filterByLastModifiedOnDateTime = null;
    }

    /**
     * Clear entity selection list filters.
     *
     * This method should be overridden in any derived controller class that has
     * its own select filters.
     */
    public void clearSelectFilters() {
        if (selectDataTable != null) {
            selectDataTable.getFilters().clear();
        }
        selectFilterByName = null;
        selectFilterByDescription = null;
        selectFilterByOwnerUser = null;
        selectFilterByOwnerGroup = null;
        selectFilterByCreatedByUser = null;
        selectFilterByCreatedOnDateTime = null;
        selectFilterByLastModifiedByUser = null;
        selectFilterByLastModifiedOnDateTime = null;
    }

    /**
     * Reset list variables and associated filter values and data model.
     *
     * @return URL to entity list view
     */
    public String resetList() {
        logger.debug("Resetting list data model for " + getDisplayEntityTypeName());
        clearListFilters();
        resetListDataModel();
        return prepareList();
    }

    /**
     * Prepare entity list view.
     *
     * @return URL to entity list view
     */
    public String prepareList() {
        logger.debug("Preparing list data model for " + getDisplayEntityTypeName());
        current = null;
        if (listDataTable != null) {
            updateListSettingsFromListDataTable(listDataTable);
        }
        return "list?faces-redirect=true";
    }

    /**
     * Prepare list view from a given view path.
     *
     * @param viewPath
     * @return URL to entity list view
     */
    public String prepareListFromViewPath(String viewPath) {
        return viewPath + "/" + prepareList();
    }

    /**
     * Reset list and return entity view.
     *
     * @return URL to selected entity instance view
     */
    public String resetListForView() {
        logger.debug("Resetting list for " + getDisplayEntityTypeName() + " view ");
        clearListFilters();
        resetListDataModel();
        return view();
    }

    /**
     * Reset list and return entity instance edit page.
     *
     * @return URL to selected entity instance edit page.
     */
    public String resetListForEdit() {
        logger.debug("Resetting list for " + getDisplayEntityTypeName() + " edit");
        clearListFilters();
        resetListDataModel();
        return edit();
    }

    /**
     * Follow breadcrumb if it is set, or prepare entity list view.
     *
     * @return previous view if breadcrumb parameters are set, or entity list
     * view otherwise
     */
    public String followBreadcrumbOrPrepareList() {
        String loadView = breadcrumbViewParam;
        if (loadView == null) {
            loadView = prepareList();
        } else {
            if (breadcrumbObjectIdViewParam != null) {
                Integer entityId = Integer.parseInt(breadcrumbObjectIdViewParam);
                loadView = prepareView(getEntityDbFacade().find(entityId));
            }
        }
        breadcrumbViewParam = null;
        breadcrumbObjectIdViewParam = null;
        return loadView;
    }

    /**
     * Check if user settings changed or not.
     *
     * @return true if user just logged in or modified settings, false otherwise
     */
    public boolean userSettingsChanged() {
        UserInfo sessionUser = (UserInfo) SessionUtility.getUser();
        if (sessionUser == null) {
            return false;
        }

        if (settingsTimestamp == null || sessionUser.areUserSettingsModifiedAfterDate(settingsTimestamp)) {
            logger.debug("Updating settings from session user (settings timestamp: " + sessionUser.getUserSettingsModificationDate() + ")");
            updateSettingsFromSessionUser(sessionUser);
            settingsTimestamp = new Date();
            return true;
        }
        return false;
    }

    /**
     * Get entity list data table.
     *
     * @return list data table
     */
    public DataTable getListDataTable() {
        if (userSettingsChanged()) {
            resetListDataModel();
        }
        if (listDataTable == null) {
            logger.debug("Recreating list data table for " + getDisplayEntityTypeName());
            listDataTable = new DataTable();
        }
        return listDataTable;
    }

    /**
     * Set entity list data table.
     *
     * @param listDataTable list data table
     */
    public void setListDataTable(DataTable listDataTable) {
        this.listDataTable = listDataTable;
        updateListSettingsFromListDataTable(listDataTable);
    }

    /**
     * Get entity selection list data table.
     *
     * @return selection list data table
     */
    public DataTable getSelectDataTable() {
        return selectDataTable;
    }

    /**
     * Set entity selection list data table.
     *
     * @param selectDataTable selection list data table
     */
    public void setSelectDataTable(DataTable selectDataTable) {
        this.selectDataTable = selectDataTable;
    }

    /**
     * Clear all list filters.
     */
    public void clearAllListFilters() {
        if (listDataTable == null) {
            return;
        }
        Map<String, Object> filterMap = listDataTable.getFilters();
        for (String filterName : filterMap.keySet()) {
            filterMap.put(filterName, "");
        }
    }

    /**
     * Check if any list filter is set.
     *
     * @return true if filter is set, false otherwise
     */
    public boolean isAnyListFilterSet() {
        if (listDataTable == null) {
            return false;
        }
        Map<String, Object> filterMap = listDataTable.getFilters();
        for (Object filterValue : filterMap.values()) {
            String filter = (String) filterValue;
            if (filter != null && !filter.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Update session view settings based on settings modification timestamp.
     */
    public void updateViewSettings() {
        UserInfo sessionUser = (UserInfo) SessionUtility.getUser();
        boolean settingsUpdated = false;
        if (sessionUser != null) {
            List<UserSetting> userSettingList = sessionUser.getUserSettingList();
            if (userSettingList != null && !userSettingList.isEmpty() && sessionUser.areUserSettingsModifiedAfterDate(settingsTimestamp)) {
                updateSettingsFromSessionUser(sessionUser);
                settingsUpdated = true;
            }
        }

        if (!settingsUpdated && !settingsInitializedFromDefaults) {
            settingsInitializedFromDefaults = true;
            updateSettingsFromSettingTypeDefaults(getSettingTypeMap());
        }
    }

    /**
     * Listener for saving user session settings.
     *
     * @param actionEvent event
     */
    public void saveViewSettingsForSessionUserActionListener(ActionEvent actionEvent) {
        UserInfo sessionUser = (UserInfo) SessionUtility.getUser();
        if (sessionUser != null) {
            logger.debug("Saving settings for session user");
            saveSettingsForSessionUser(sessionUser);
            settingsTimestamp = new Date();
        }
    }

    /**
     * Customize view display and reload current page.
     *
     * @return URL for the current view
     */
    public String customizeViewDisplay() {
        String returnPage = SessionUtility.getCurrentViewId() + "?faces-redirect=true";
        logger.debug("Returning to page: " + returnPage);
        return returnPage;
    }

    /**
     * Verify that view is valid.
     *
     * If not, redirect user to appropriate error page.
     *
     * @throws IOException in case of IO errors
     */
    public void verifyView() throws IOException {
        if (current != null) {
            SessionUtility.redirectTo("/views/error/invalidRequest.xhtml");
        }
    }

    /**
     * Prepare entity view.
     *
     * This method should be overridden in the derived controller.
     *
     * @param entity entity instance
     */
    protected void prepareEntityView(EntityType entity) {
    }

    /**
     * Prepare entity view and return view URL.
     *
     * @param entity entity instance
     * @return entity view URL
     */
    public String prepareView(EntityType entity) {
        logger.debug("Preparing view");
        current = entity;
        updateViewSettings();
        prepareEntityView(entity);
        return view();
    }

    /**
     * Return entity view page.
     *
     * @return URL to view page in the entity folder
     */
    public String view() {
        return "view?faces-redirect=true";
    }

    /**
     * Return entity create page.
     *
     * @return URL to create page in the entity folder
     */
    public String prepareCreate() {
        current = createEntityInstance();
        return "create?faces-redirect=true";
    }

    /**
     * Clone entity instance.
     *
     * @param entity entity instance to be cloned
     * @return cloned entity instance
     */
    public EntityType cloneEntityInstance(EntityType entity) {
        EntityType clonedEntity;
        try {
            clonedEntity = (EntityType) (entity.clone());
        } catch (CloneNotSupportedException ex) {
            logger.error("Object cannot be cloned: " + ex);
            clonedEntity = createEntityInstance();
        }
        return clonedEntity;
    }

    /**
     * Prepare entity instance clone and return view to the cloned instance.
     *
     * @param entity entity instance to be cloned
     * @return URL to cloned instance view
     */
    public String prepareClone(EntityType entity) {
        current = cloneEntityInstance(entity);
        return "/views/" + getEntityTypeName() + "/create?faces-redirect=true";
    }

    /**
     * Prepare entity insert.
     *
     * This method should be overridden in the derived controller.
     *
     * @param entity entity instance
     * @throws CdbException in case of any errors
     */
    protected void prepareEntityInsert(EntityType entity) throws CdbException {
    }

    /**
     * Create new entity instance and return view to the new instance.
     *
     * @return URL to the new entity instance view
     */
    public String create() {
        try {
            EntityType newEntity = current;
            prepareEntityInsert(current);
            getEntityDbFacade().create(current);
            SessionUtility.addInfoMessage("Success", "Created " + getDisplayEntityTypeName() + " " + getCurrentEntityInstanceName() + ".");
            resetListDataModel();
            resetSelectDataModel();
            current = newEntity;
            return view();
        } catch (CdbException ex) {
            SessionUtility.addErrorMessage("Error", "Could not create " + getDisplayEntityTypeName() + ": " + ex.getMessage());
            return null;
        } catch (RuntimeException ex) {
            Throwable t = ExceptionUtils.getRootCause(ex);
            logger.error("Could not create " + getDisplayEntityTypeName() + ": " + t.getMessage());
            SessionUtility.addErrorMessage("Error", "Could not create " + getDisplayEntityTypeName() + ": " + t.getMessage());
            return null;
        }
    }

    /**
     * Prepare entity instance edit.
     *
     * @param entity entity instance to be updated
     * @return URL to edit page
     */
    public String prepareEdit(EntityType entity) {
        resetLogText();
        current = entity;
        return edit();
    }

    /**
     * Return entity edit page.
     *
     * @return URL to edit page in the entity folder
     */
    public String edit() {
        clearSelectFiltersAndResetSelectDataModel();
        return "edit?faces-redirect=true";
    }

    /**
     * Prepare entity update.
     *
     * This method should be overridden in the derived controller.
     *
     * @param entity entity instance
     * @throws CdbException in case of any errors
     */
    protected void prepareEntityUpdate(EntityType entity) throws CdbException {
    }

    /**
     * Update current entity and save changes in the database.
     *
     * @return URL to current entity instance view page
     */
    public String update() {
        try {
            logger.debug("Updating " + getDisplayEntityTypeName() + " " + getCurrentEntityInstanceName());
            prepareEntityUpdate(current);
            EntityType updatedEntity = getEntityDbFacade().edit(current);
            SessionUtility.addInfoMessage("Success", "Updated " + getDisplayEntityTypeName() + " " + getCurrentEntityInstanceName() + ".");
            resetListDataModel();
            resetSelectDataModel();
            resetLogText();
            current = updatedEntity;
            return view();
        } catch (CdbException ex) {
            SessionUtility.addErrorMessage("Error", "Could not update " + getDisplayEntityTypeName() + ": " + ex.getMessage());
            return null;
        } catch (RuntimeException ex) {
            Throwable t = ExceptionUtils.getRootCause(ex);
            logger.error("Could not update " + getDisplayEntityTypeName() + " "
                    + getCurrentEntityInstanceName() + ": " + t.getMessage());
            SessionUtility.addErrorMessage("Error", "Could not update " + getDisplayEntityTypeName() + ": " + t.getMessage());
            return null;
        }
    }

    /**
     * Prepare entity update when changes involve removing associated objects
     * from the database.
     *
     * This method should be overridden in the derived controller.
     *
     * @param entity entity instance
     * @throws CdbException in case of any errors
     */
    protected void prepareEntityUpdateOnRemoval(EntityType entity) throws CdbException {
    }

    /**
     * Update current entity and save changes in the database.
     *
     * This method is used when changes involve only removing associated objects
     * from the database.
     *
     * @return URL to current entity instance view page
     */
    public String updateOnRemoval() {
        try {
            logger.debug("Updating " + getDisplayEntityTypeName() + " " + getCurrentEntityInstanceName());
            prepareEntityUpdateOnRemoval(current);
            EntityType updatedEntity = getEntityDbFacade().edit(current);
            SessionUtility.addInfoMessage("Success", "Updated " + getDisplayEntityTypeName() + " " + getCurrentEntityInstanceName() + ".");
            resetListDataModel();
            resetSelectDataModel();
            resetLogText();
            current = updatedEntity;
            return view();
        } catch (CdbException ex) {
            SessionUtility.addErrorMessage("Error", "Could not update " + getDisplayEntityTypeName() + ": " + ex.getMessage());
            return null;
        } catch (RuntimeException ex) {
            Throwable t = ExceptionUtils.getRootCause(ex);
            logger.error("Could not update " + getDisplayEntityTypeName() + " "
                    + getCurrentEntityInstanceName() + ": " + t.getMessage());
            SessionUtility.addErrorMessage("Error", "Could not update " + getDisplayEntityTypeName() + ": " + t.getMessage());
            return null;
        }
    }

    /**
     * Prepare entity removal from the database.
     *
     * This method should be overridden in the derived controller.
     *
     * @param entity entity instance
     * @throws CdbException in case of any errors
     */
    protected void prepareEntityDestroy(EntityType entity) throws CdbException {
    }

    /**
     * Remove entity instance from the database.
     *
     * @param entity entity instance to be deleted
     */
    public void destroy(EntityType entity) {
        current = entity;
        destroy();
    }

    /**
     * Remove current (selected) entity instance from the database and reset
     * list variables and data model.
     *
     * @return URL to entity list page
     */
    public String destroy() {
        if (current == null) {
            logger.warn("Current item is not set");
            // Do nothing if current item is not set.
            return null;
        } else if (current.getId() == null) {
            logger.warn("Current item id is null");
            // Do nothing if there is no id.
            return null;
        }
        try {
            logger.debug("Destroying " + getDisplayEntityTypeName() + " " + getCurrentEntityInstanceName());
            prepareEntityDestroy(current);
            getEntityDbFacade().remove(current);
            SessionUtility.addInfoMessage("Success", "Deleted " + getDisplayEntityTypeName() + " " + getCurrentEntityInstanceName() + ".");
            resetListDataModel();
            resetSelectDataModel();
            clearListFilters();
            return prepareList();
        } catch (CdbException ex) {
            SessionUtility.addErrorMessage("Error", "Could not delete " + getDisplayEntityTypeName() + ": " + ex.getMessage());
            return null;
        } catch (RuntimeException ex) {
            Throwable t = ExceptionUtils.getRootCause(ex);
            logger.error("Could not delete " + getDisplayEntityTypeName() + " "
                    + getCurrentEntityInstanceName() + ": " + t.getMessage());
            SessionUtility.addErrorMessage("Error", "Could not delete " + getDisplayEntityTypeName() + ": " + t.getMessage());
            return null;
        }
    }

    /**
     * Create data model from list of all available entity instances.
     *
     * @return created list data model
     */
    public DataModel createListDataModel() {
        listDataModel = new ListDataModel(getEntityDbFacade().findAll());
        return listDataModel;
    }

    /**
     * Get list data model.
     *
     * If model is not set, this method will create it.
     *
     * @return list data model
     */
    public DataModel getListDataModel() {
        if (listDataModel == null) {
            createListDataModel();
        }
        return listDataModel;
    }

    /**
     * Prepare entity list for selection.
     *
     * This method should be overridden in the derived controller.
     *
     * @param selectEntityList entity list to be used for selection
     */
    public void prepareEntityListForSelection(List<EntityType> selectEntityList) {
    }

    /**
     * Create data model for entity selection.
     *
     * @return selection data model
     */
    public DataModel createSelectDataModel() {
        List<EntityType> selectEntityList = getEntityDbFacade().findAll();
        prepareEntityListForSelection(selectEntityList);
        selectDataModel = new ListDataModel(selectEntityList);
        return selectDataModel;
    }

    /**
     * Create data model for entity selection using provided list.
     *
     * @param selectEntityList entity list to be used for selection
     * @return selection data model
     */
    public DataModel createSelectDataModel(List<EntityType> selectEntityList) {
        selectDataModel = new ListDataModel(selectEntityList);
        return selectDataModel;
    }

    /**
     * Get data model for entity selection.
     *
     * If selection data model is null, it will be created.
     *
     * @return selection data model
     */
    public DataModel getSelectDataModel() {
        if (selectDataModel == null) {
            createSelectDataModel();
        }
        return selectDataModel;
    }

    /**
     * Create data model for entity selection, but without current (selected)
     * entity.
     *
     * @return selection data model
     */
    public DataModel createSelectDataModelWithoutCurrent() {
        List<EntityType> selectEntityList = getAvailableItemsWithoutCurrent();

        prepareEntityListForSelection(selectEntityList);
        selectDataModel = new ListDataModel(selectEntityList);
        return selectDataModel;
    }

    /**
     * Get data model for entity selection, but without current (selected)
     * entity.
     *
     * If selection data model is null, it will be created.
     *
     * @return selection data model
     */
    public DataModel getSelectDataModelWithoutCurrent() {
        if (selectDataModel == null) {
            createSelectDataModelWithoutCurrent();
        }
        return selectDataModel;
    }

    /**
     * Get data model using list of all entity instances.
     *
     * @return data model
     */
    public DataModel getItems() {
        return getListDataModel();
    }

    /**
     * Get list of selected objects and reset selection data model.
     *
     * @return list of selected objects
     */
    public List<EntityType> getSelectedObjectListAndResetSelectDataModel() {
        List<EntityType> returnList = selectedObjectList;
        resetSelectDataModel();
        return returnList;
    }

    public List<EntityType> getSelectedObjectList() {
        return selectedObjectList;
    }

    public List<EntityType> getFilteredObjectList() {
        return filteredObjectList;
    }

    public List<EntityType> getFilteredItems() {
        return filteredObjectList;
    }

    /**
     * Reset selected object list to null.
     */
    public void resetSelectedObjectList() {
        selectedObjectList = null;
    }

    public void setSelectedObjectList(List<EntityType> selectedObjectList) {
        this.selectedObjectList = selectedObjectList;
    }

    public void setFilteredObjectList(List<EntityType> filteredObjectList) {
        this.filteredObjectList = filteredObjectList;
    }

    public void setFilteredItems(List<EntityType> filteredItems) {
        this.filteredObjectList = filteredItems;
    }

    public EntityType getSelectedObject() {
        return selectedObject;
    }

    public void setSelectedObject(EntityType selectedObject) {
        this.selectedObject = selectedObject;
    }

    /**
     * Reset list data model and set current entity.
     *
     * @param currentEntity current entity
     */
    public void resetListDataModelAndSetCurrent(EntityType currentEntity) {
        resetListDataModel();
        current = currentEntity;
    }

    /**
     * Reset various list variables to null so that they can be recreated for
     * next request.
     */
    public void resetListDataModel() {
        listDataModel = null;
        listDataTable = null;
        listDataModelReset = true;
        filteredObjectList = null;
        current = null;
        // Flush cache 
        //getFacade().flush();
    }

    /**
     * Reset various selection variables so that they can be recreated for next
     * request.
     */
    public void resetSelectDataModel() {
        selectDataModel = null;
        selectDataTable = null;
        selectedObjectList = null;
        selectDataModelReset = true;
        // Flush cache 
        //getFacade().flush();
    }

    public void clearListFiltersAndResetListDataModel() {
        clearListFilters();
        resetListDataModel();
    }

    public void clearSelectFiltersAndResetSelectDataModelActionListener(ActionEvent actionEvent) {
        clearSelectFiltersAndResetSelectDataModel();
    }

    public void clearSelectFiltersAndResetSelectDataModel() {
        clearSelectFilters();
        resetSelectDataModel();
    }

    /**
     * Create log object if log text is not null.
     *
     * @return new log entry
     */
    public Log prepareLogEntry() {
        Log logEntry = null;
        if (logText != null && !logText.isEmpty()) {
            logEntry = LogUtility.createLogEntry(logText);
            if (logTopicId != null) {
                LogTopic logTopic = logTopicFacade.find(logTopicId);
                logEntry.setLogTopic(logTopic);
            }
            resetLogText();
        }
        return logEntry;
    }

    public List<EntityType> getAvailableItems() {
        return getEntityDbFacade().findAll();
    }

    public List<EntityType> getAvailableItemsWithoutCurrent() {
        List<EntityType> entityList = getEntityDbFacade().findAll();
        if (current.getId() != null) {
            entityList.remove(current);
        }
        return entityList;
    }

    public EntityType getEntity(Integer id) {
        return getEntityDbFacade().find(id);
    }

    public SelectItem[] getAvailableItemsForSelectMany() {
        return CollectionUtility.getSelectItems(getEntityDbFacade().findAll(), false);
    }

    public SelectItem[] getAvailableItemsForSelectOne() {
        return CollectionUtility.getSelectItems(getEntityDbFacade().findAll(), true);
    }

    public String getCurrentViewId() {
        return SessionUtility.getCurrentViewId();
    }

    public static String displayEntityList(List<?> entityList) {
        String itemDelimiter = ", ";
        return CollectionUtility.displayItemListWithoutOutsideDelimiters(entityList, itemDelimiter);
    }

    /**
     * Search all entities for a given string.
     *
     * @param searchString search string
     * @param caseInsensitive use case insensitive search
     * @return list of search results
     */
    public List<SearchResult> getSearchResultList(String searchString, boolean caseInsensitive) {
        searchHasResults = false;
        if (searchString == null || searchString.isEmpty()) {
            searchResultList = new LinkedList<>();
            return searchResultList;
        }
        if (searchString.equals(this.searchString) && caseInsensitive == this.caseInsensitive) {
            // Return old results
            return searchResultList;
        }

        // Start new search
        this.searchString = searchString;
        this.caseInsensitive = caseInsensitive;
        searchResultList = new LinkedList<>();

        Pattern searchPattern;
        if (caseInsensitive) {
            searchPattern = Pattern.compile(Pattern.quote(searchString), Pattern.CASE_INSENSITIVE);
        } else {
            searchPattern = Pattern.compile(Pattern.quote(searchString));
        }
        DataModel<EntityType> dataModel = getListDataModel();
        Iterator<EntityType> iterator = dataModel.iterator();
        while (iterator.hasNext()) {
            EntityType entity = iterator.next();
            SearchResult searchResult = entity.search(searchPattern);
            if (!searchResult.isEmpty()) {
                searchResultList.add(searchResult);
            }
        }
        if (!searchResultList.isEmpty()) {
            searchHasResults = true;
        }
        return searchResultList;
    }

    public boolean searchHasResults() {
        return searchHasResults;
    }

    public boolean entityHasCategories() {
        return getEntityTypeCategoryName() != null;
    }

    public boolean entityHasTypes() {
        return getEntityTypeTypeName() != null;
    }

    public boolean entityHasGroups() {
        return getEntityTypeGroupName() != null;
    }

    public boolean entityCanBeCreatedByUsers() {
        return false;
    }

    public String getLogText() {
        return logText;
    }

    public void setLogText(String logText) {
        this.logText = logText;
    }

    public Integer getLogTopicId() {
        return logTopicId;
    }

    public void setLogTopicId(Integer logTopicId) {
        this.logTopicId = logTopicId;
    }

    public Integer getDisplayNumberOfItemsPerPage() {
        return displayNumberOfItemsPerPage;
    }

    public void setDisplayNumberOfItemsPerPage(Integer displayNumberOfItemsPerPage) {
        this.displayNumberOfItemsPerPage = displayNumberOfItemsPerPage;
    }

    public Boolean getDisplayId() {
        return displayId;
    }

    public void setDisplayId(Boolean displayId) {
        this.displayId = displayId;
    }

    public Boolean getDisplayDescription() {
        return displayDescription;
    }

    public void setDisplayDescription(Boolean displayDescription) {
        this.displayDescription = displayDescription;
    }

    public Boolean getDisplayOwnerUser() {
        return displayOwnerUser;
    }

    public void setDisplayOwnerUser(Boolean displayOwnerUser) {
        this.displayOwnerUser = displayOwnerUser;
    }

    public Boolean getDisplayOwnerGroup() {
        return displayOwnerGroup;
    }

    public void setDisplayOwnerGroup(Boolean displayOwnerGroup) {
        this.displayOwnerGroup = displayOwnerGroup;
    }

    public Boolean getDisplayCreatedByUser() {
        return displayCreatedByUser;
    }

    public void setDisplayCreatedByUser(Boolean displayCreatedByUser) {
        this.displayCreatedByUser = displayCreatedByUser;
    }

    public Boolean getDisplayCreatedOnDateTime() {
        return displayCreatedOnDateTime;
    }

    public void setDisplayCreatedOnDateTime(Boolean displayCreatedOnDateTime) {
        this.displayCreatedOnDateTime = displayCreatedOnDateTime;
    }

    public Boolean getDisplayLastModifiedByUser() {
        return displayLastModifiedByUser;
    }

    public void setDisplayLastModifiedByUser(Boolean displayLastModifiedByUser) {
        this.displayLastModifiedByUser = displayLastModifiedByUser;
    }

    public Boolean getDisplayLastModifiedOnDateTime() {
        return displayLastModifiedOnDateTime;
    }

    public void setDisplayLastModifiedOnDateTime(Boolean displayLastModifiedOnDateTime) {
        this.displayLastModifiedOnDateTime = displayLastModifiedOnDateTime;
    }

    public void setFilterByName(String filterByName) {
        this.filterByName = filterByName;
    }

    public String getFilterByName() {
        return filterByName;
    }

    public String getFilterByDescription() {
        return filterByDescription;
    }

    public void setFilterByDescription(String filterByDescription) {
        this.filterByDescription = filterByDescription;
    }

    public String getFilterByOwnerUser() {
        return filterByOwnerUser;
    }

    public void setFilterByOwnerUser(String filterByOwnerUser) {
        this.filterByOwnerUser = filterByOwnerUser;
    }

    public String getFilterByOwnerGroup() {
        return filterByOwnerGroup;
    }

    public void setFilterByOwnerGroup(String filterByOwnerGroup) {
        this.filterByOwnerGroup = filterByOwnerGroup;
    }

    public String getFilterByCreatedByUser() {
        return filterByCreatedByUser;
    }

    public void setFilterByCreatedByUser(String filterByCreatedByUser) {
        this.filterByCreatedByUser = filterByCreatedByUser;
    }

    public String getFilterByCreatedOnDateTime() {
        return filterByCreatedOnDateTime;
    }

    public void setFilterByCreatedOnDateTime(String filterByCreatedOnDateTime) {
        this.filterByCreatedOnDateTime = filterByCreatedOnDateTime;
    }

    public String getFilterByLastModifiedByUser() {
        return filterByLastModifiedByUser;
    }

    public void setFilterByLastModifiedByUser(String filterByLastModifiedByUser) {
        this.filterByLastModifiedByUser = filterByLastModifiedByUser;
    }

    public String getFilterByLastModifiedOnDateTime() {
        return filterByLastModifiedOnDateTime;
    }

    public void setFilterByLastModifiedOnDateTime(String filterByLastModifiedOnDateTime) {
        this.filterByLastModifiedOnDateTime = filterByLastModifiedOnDateTime;
    }

    public boolean isListDataModelReset() {
        if (listDataModelReset) {
            listDataModelReset = false;
            return true;
        }
        return false;
    }

    public boolean isSelectDataModelReset() {
        if (selectDataModelReset) {
            selectDataModelReset = false;
            return true;
        }
        return false;
    }

    public Integer getSelectDisplayNumberOfItemsPerPage() {
        return selectDisplayNumberOfItemsPerPage;
    }

    public void setSelectDisplayNumberOfItemsPerPage(Integer selectDisplayNumberOfItemsPerPage) {
        this.selectDisplayNumberOfItemsPerPage = selectDisplayNumberOfItemsPerPage;
    }

    public Boolean getSelectDisplayId() {
        return selectDisplayId;
    }

    public void setSelectDisplayId(Boolean selectDisplayId) {
        this.selectDisplayId = selectDisplayId;
    }

    public Boolean getSelectDisplayDescription() {
        return selectDisplayDescription;
    }

    public void setSelectDisplayDescription(Boolean selectDisplayDescription) {
        this.selectDisplayDescription = selectDisplayDescription;
    }

    public Boolean getSelectDisplayOwnerUser() {
        return selectDisplayOwnerUser;
    }

    public void setSelectDisplayOwnerUser(Boolean selectDisplayOwnerUser) {
        this.selectDisplayOwnerUser = selectDisplayOwnerUser;
    }

    public Boolean getSelectDisplayOwnerGroup() {
        return selectDisplayOwnerGroup;
    }

    public void setSelectDisplayOwnerGroup(Boolean selectDisplayOwnerGroup) {
        this.selectDisplayOwnerGroup = selectDisplayOwnerGroup;
    }

    public Boolean getSelectDisplayCreatedByUser() {
        return selectDisplayCreatedByUser;
    }

    public void setSelectDisplayCreatedByUser(Boolean selectDisplayCreatedByUser) {
        this.selectDisplayCreatedByUser = selectDisplayCreatedByUser;
    }

    public Boolean getSelectDisplayCreatedOnDateTime() {
        return selectDisplayCreatedOnDateTime;
    }

    public void setSelectDisplayCreatedOnDateTime(Boolean selectDisplayCreatedOnDateTime) {
        this.selectDisplayCreatedOnDateTime = selectDisplayCreatedOnDateTime;
    }

    public Boolean getSelectDisplayLastModifiedByUser() {
        return selectDisplayLastModifiedByUser;
    }

    public void setSelectDisplayLastModifiedByUser(Boolean selectDisplayLastModifiedByUser) {
        this.selectDisplayLastModifiedByUser = selectDisplayLastModifiedByUser;
    }

    public Boolean getSelectDisplayLastModifiedOnDateTime() {
        return selectDisplayLastModifiedOnDateTime;
    }

    public void setSelectDisplayLastModifiedOnDateTime(Boolean selectDisplayLastModifiedOnDateTime) {
        this.selectDisplayLastModifiedOnDateTime = selectDisplayLastModifiedOnDateTime;
    }

    public String getSelectFilterByName() {
        return selectFilterByName;
    }

    public void setSelectFilterByName(String selectFilterByName) {
        this.selectFilterByName = selectFilterByName;
    }

    public String getSelectFilterByDescription() {
        return selectFilterByDescription;
    }

    public void setSelectFilterByDescription(String selectFilterByDescription) {
        this.selectFilterByDescription = selectFilterByDescription;
    }

    public String getSelectFilterByOwnerUser() {
        return selectFilterByOwnerUser;
    }

    public void setSelectFilterByOwnerUser(String selectFilterByOwnerUser) {
        this.selectFilterByOwnerUser = selectFilterByOwnerUser;
    }

    public String getSelectFilterByOwnerGroup() {
        return selectFilterByOwnerGroup;
    }

    public void setSelectFilterByOwnerGroup(String selectFilterByOwnerGroup) {
        this.selectFilterByOwnerGroup = selectFilterByOwnerGroup;
    }

    public String getSelectFilterByCreatedByUser() {
        return selectFilterByCreatedByUser;
    }

    public void setSelectFilterByCreatedByUser(String selectFilterByCreatedByUser) {
        this.selectFilterByCreatedByUser = selectFilterByCreatedByUser;
    }

    public String getSelectFilterByCreatedOnDateTime() {
        return selectFilterByCreatedOnDateTime;
    }

    public void setSelectFilterByCreatedOnDateTime(String selectFilterByCreatedOnDateTime) {
        this.selectFilterByCreatedOnDateTime = selectFilterByCreatedOnDateTime;
    }

    public String getSelectFilterByLastModifiedByUser() {
        return selectFilterByLastModifiedByUser;
    }

    public void setSelectFilterByLastModifiedByUser(String selectFilterByLastModifiedByUser) {
        this.selectFilterByLastModifiedByUser = selectFilterByLastModifiedByUser;
    }

    public String getSelectFilterByLastModifiedOnDateTime() {
        return selectFilterByLastModifiedOnDateTime;
    }

    public void setSelectFilterByLastModifiedOnDateTime(String selectFilterByLastModifiedOnDateTime) {
        this.selectFilterByLastModifiedOnDateTime = selectFilterByLastModifiedOnDateTime;
    }

    public String getBreadcrumbViewParam() {
        return breadcrumbViewParam;
    }

    public void setBreadcrumbViewParam(String breadcrumbViewParam) {
        this.breadcrumbViewParam = breadcrumbViewParam;
    }

    public Date getSettingsTimestamp() {
        return settingsTimestamp;
    }

    public void setSettingsTimestamp(Date settingsTimestamp) {
        this.settingsTimestamp = settingsTimestamp;
    }

    public void exportListDataTableAsPdf(String filename) throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        Exporter exporter = new CdbPdfExporter();
        exporter.export(context, listDataTable, filename, false, false, "UTF-8", null, null);
        context.responseComplete();
    }

    public static void exportDataTableAsPdf(String dataTableId, String filename) throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        DataTable dataTable = (DataTable) UiComponentUtility.findComponent(dataTableId);
        Exporter exporter = new CdbPdfExporter();
        exporter.export(context, dataTable, filename, false, false, "UTF-8", null, null);
        context.responseComplete();
    }

    public void exportListDataTableAsXls(String filename) throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        Exporter exporter = new CdbExcelExporter();
        exporter.export(context, listDataTable, filename, false, false, "UTF-8", null, null);
        context.responseComplete();
    }

    public static void exportDataTableAsXls(String dataTableId, String filename) throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        DataTable dataTable = (DataTable) UiComponentUtility.findComponent(dataTableId);
        Exporter exporter = new CdbExcelExporter();
        exporter.export(context, dataTable, filename, false, false, "UTF-8", null, null);
        context.responseComplete();
    }

    public void exportListDataTableAsCsv(String filename) throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        Exporter exporter = new CdbCsvExporter();
        exporter.export(context, listDataTable, filename, false, false, "UTF-8", null, null);
        context.responseComplete();
    }

    public static void exportDataTableAsCsv(String dataTableId, String filename) throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        DataTable dataTable = (DataTable) UiComponentUtility.findComponent(dataTableId);
        Exporter exporter = new CdbCsvExporter();
        exporter.export(context, dataTable, filename, false, false, "UTF-8", null, null);
        context.responseComplete();
    }
}
