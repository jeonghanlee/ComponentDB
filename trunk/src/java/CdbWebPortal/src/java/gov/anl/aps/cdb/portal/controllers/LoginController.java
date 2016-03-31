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
import gov.anl.aps.cdb.portal.model.db.beans.SettingTypeDbFacade;
import gov.anl.aps.cdb.portal.model.db.beans.UserInfoDbFacade;
import gov.anl.aps.cdb.portal.model.db.entities.EntityInfo;
import gov.anl.aps.cdb.portal.model.db.entities.SettingType;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import gov.anl.aps.cdb.portal.model.db.entities.UserSetting;
import gov.anl.aps.cdb.portal.utilities.AuthorizationUtility;
import gov.anl.aps.cdb.portal.utilities.ConfigurationUtility;
import gov.anl.aps.cdb.common.utilities.LdapUtility;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import gov.anl.aps.cdb.common.utilities.CryptUtility;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import org.apache.log4j.Logger;

/**
 * Login controller.
 */
@Named("loginController")
@SessionScoped
public class LoginController implements Serializable {

    private static final int MilisecondsInSecond = 1000;
    private static final int SessionTimeoutDecreaseInSeconds = 10;

    @EJB
    private UserInfoDbFacade userFacade;
    @EJB
    private SettingTypeDbFacade settingTypeFacade;

    private String username = null;
    private String password = null;
    private boolean loggedInAsAdmin = false;
    private boolean loggedInAsUser = false;
    private UserInfo user = null;
    private Integer sessionTimeoutInMiliseconds = null;

    private static final String AdminGroupListPropertyName = "cdb.portal.adminGroupList";
    private static final List<String> adminGroupNameList = ConfigurationUtility.getPortalPropertyList(AdminGroupListPropertyName);
    private static final Logger logger = Logger.getLogger(LoginController.class.getName());

    public LoginController() {
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isLoggedIn() {
        return (loggedInAsAdmin || loggedInAsUser);
    }

    public boolean isLoggedInAsAdmin() {
        return loggedInAsAdmin;
    }

    public void setLoggedInAsAdmin(boolean loggedInAsAdmin) {
        this.loggedInAsAdmin = loggedInAsAdmin;
    }

    public boolean isLoggedInAsUser() {
        return loggedInAsUser;
    }

    public void setLoggedInAsUser(boolean loggedInAsUser) {
        this.loggedInAsUser = loggedInAsUser;
    }

    private boolean isAdmin(String username) {
        for (String adminGroupName : adminGroupNameList) {
            if (userFacade.isUserMemberOfUserGroup(username, adminGroupName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Login action.
     *
     * @return URL to home page if login is successful, or null in case of
     * errors
     */
    public String login() {
        loggedInAsAdmin = false;
        loggedInAsUser = false;
        if (username == null || password == null || username.isEmpty() || password.isEmpty()) {
            SessionUtility.addWarningMessage("Incomplete Input", "Please enter both username and password.");
            return (username = password = null);
        }

        user = userFacade.findByUsername(username);
        if (user == null) {
            SessionUtility.addErrorMessage("Unknown User", "Username " + username + " is not registered.");
            return (username = password = null);
        }

        boolean isAdminUser = isAdmin(username);
        logger.debug("User " + username + " is admin: " + isAdminUser);
        boolean validCredentials = false;
        if (user.getPassword() != null && CryptUtility.verifyPasswordWithPbkdf2(password, user.getPassword())) {
            logger.debug("User " + username + " is authorized by CDB");
            validCredentials = true;
        } else if (LdapUtility.validateCredentials(username, password)) {
            logger.debug("User " + username + " is authorized by LDAP");
            validCredentials = true;
        } else {
            logger.debug("User " + username + " is not authorized");
        }

        if (validCredentials) {
            if (!user.hasUserSettings()) {
                setSessionUserSettingsFromSettingTypeDefaults(user);
            }
            SessionUtility.setUser(user);
            if (isAdminUser) {
                loggedInAsAdmin = true;
                SessionUtility.setRole(CdbRole.ADMIN);
                SessionUtility.addInfoMessage("Successful Login", "Administrator " + username + " is logged in.");

            } else {
                loggedInAsUser = true;
                SessionUtility.setRole(CdbRole.USER);
                SessionUtility.addInfoMessage("Successful Login", "User " + username + " is logged in.");
            }

            return getLandingPage();
        } else {
            SessionUtility.addErrorMessage("Invalid Credentials", "Username/password combination could not be verified.");
            return (username = password = null);
        }

    }

    private void setSessionUserSettingsFromSettingTypeDefaults(UserInfo sessionUser) {
        List<SettingType> settingTypeList = settingTypeFacade.findAll();
        List<UserSetting> userSettingList = new ArrayList<>();
        for (SettingType settingType : settingTypeList) {
            UserSetting userSetting = new UserSetting();
            userSetting.setSettingType(settingType);
            userSetting.setUser(sessionUser);
            userSetting.setValue(settingType.getDefaultValue());
            userSettingList.add(userSetting);
        }
        sessionUser.setUserSettingList(userSettingList);
    }

    public String getLandingPage() {
        String landingPage = SessionUtility.getCurrentViewId();
        if (landingPage.contains("login")) {
            landingPage = SessionUtility.popViewFromStack();
            if (landingPage == null) {
                landingPage = "/views/home";
            }
        }
        landingPage += "?faces-redirect=true";
        logger.debug("Landing page: " + landingPage);
        return landingPage;
    }

    public String dropAdminRole() {
        loggedInAsAdmin = false;
        loggedInAsUser = true;
        SessionUtility.setRole(CdbRole.USER);
        return getLandingPage();
    }

    public String displayUsername() {
        if (isLoggedIn()) {
            return username;
        } else {
            return "Not Logged In";
        }
    }

    public String displayRole() {
        if (isLoggedInAsAdmin()) {
            return "Administrator";
        } else {
            return "User";
        }
    }

    public boolean isEntityWriteable(EntityInfo entityInfo) {
        // If user is not logged in, object is not writeable
        if (!isLoggedIn()) {
            return false;
        }

        if (entityInfo == null) {
            return false;
        }

        // Admins can write any object.
        if (isLoggedInAsAdmin()) {
            return true;
        }

        return AuthorizationUtility.isEntityWriteableByUser(entityInfo, user);

    }

    public boolean isUserWriteable(UserInfo user) {
        if (!isLoggedIn()) {
            return false;
        }
        return isLoggedInAsAdmin() || this.user.getId() == user.getId();
    }

    private void resetLoginInfo() {
        loggedInAsAdmin = false;
        loggedInAsUser = false;
        user = null;
    }

    public void handleInvalidSessionRequest() {
        SessionUtility.addWarningMessage("Warning", "Invalid session request");
        SessionUtility.navigateTo("/views/home?faces-redirect=true");
    }

    /**
     * Logout action.
     *
     * @return URL to home page
     */
    public String logout() {
        logger.debug("Logging out user: " + user);
        SessionUtility.clearSession();
        ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
        context.invalidateSession();
        resetLoginInfo();
        return "/views/home?faces-redirect=true";
    }

    public void sessionIdleListener() {
        String msg = "Session expired ";
        if (user != null) {
            msg += "for user " + user;
        } else {
            msg += "for anonymous user";
        }
        SessionUtility.addWarningMessage("Warning", msg);
        logger.debug(msg);
        if (isLoggedIn()) {
            resetLoginInfo();
        }
        SessionUtility.navigateTo("/views/home?faces-redirect=true");
    }

    public int getSessionTimeoutInMiliseconds() {
        if (sessionTimeoutInMiliseconds == null) {
            int timeoutInSeconds = SessionUtility.getSessionTimeoutInSeconds();
            logger.debug("Session timeout in seconds: " + timeoutInSeconds);
            // reduce configured value slightly to avoid premature session expiration issues
            sessionTimeoutInMiliseconds = (timeoutInSeconds - SessionTimeoutDecreaseInSeconds) * MilisecondsInSecond;
        }
        // logger.debug("Idle timeout in miliseconds: " + sessionTimeoutInMiliseconds);
        return sessionTimeoutInMiliseconds;
    }
}
