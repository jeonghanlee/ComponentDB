/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.portal.controllers.settings.RoleTypeSettings;
import gov.anl.aps.cdb.portal.model.db.entities.RoleType;
import gov.anl.aps.cdb.portal.model.db.beans.RoleTypeFacade;

import java.io.Serializable;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@Named("roleTypeController")
@SessionScoped
public class RoleTypeController extends CdbEntityController<RoleType, RoleTypeFacade, RoleTypeSettings> implements Serializable {

    @EJB
    private RoleTypeFacade roleTypeFacade;

    public RoleType getRoleType(java.lang.Integer id) {
        return roleTypeFacade.find(id);
    }

    @Override
    protected RoleTypeFacade getEntityDbFacade() {
        return roleTypeFacade; 
    }

    @Override
    protected RoleType createEntityInstance() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getEntityTypeName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getCurrentEntityInstanceName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected RoleTypeSettings createNewSettingObject() {
        return new RoleTypeSettings();
    }

    @FacesConverter(value = "roleTypeConverter" ,forClass = RoleType.class)
    public static class RoleTypeControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            RoleTypeController controller = (RoleTypeController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "roleTypeController");
            return controller.getRoleType(getKey(value));
        }

        java.lang.Integer getKey(String value) {
            java.lang.Integer key;
            key = Integer.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Integer value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof RoleType) {
                RoleType o = (RoleType) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + RoleType.class.getName());
            }
        }

    }

}
