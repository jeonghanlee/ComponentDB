/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.common.utilities.ObjectUtility;
import gov.anl.aps.cdb.portal.model.db.beans.CdbEntityFacade;
import gov.anl.aps.cdb.portal.model.db.entities.Domain;
import gov.anl.aps.cdb.portal.model.db.entities.ItemTypeCategoryEntity;
import java.util.List;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

public abstract class ItemTypeCategoryController<EntityType extends ItemTypeCategoryEntity, FacadeType extends CdbEntityFacade<EntityType>> extends CdbEntityController<EntityType, FacadeType> {

    protected Domain currentViewDomain = null;

    protected DataModel currentDomainListDataModel = null;

    public abstract List<EntityType> getItemTypeCategoryEntityListByDomainName(String domainName);

    public abstract EntityType createItemTypeCategoryEntity();

    public abstract String getDefaultDisplayEntityTypeName();

    @Override
    protected EntityType createEntityInstance() {
        EntityType itemTypeCategory;
        itemTypeCategory = createItemTypeCategoryEntity();
        itemTypeCategory.setDomain(currentViewDomain);
        return itemTypeCategory;
    }

    @Override
    public void resetListDataModel() {
        currentDomainListDataModel = null; 
        super.resetListDataModel(); 
    }

    public Domain getCurrentViewDomain() {
        return currentViewDomain;
    }

    public DataModel getCurrentDomainListDataModel() {
        if (currentViewDomain == null) {
            return super.getListDataModel();
        } else {
            if (currentDomainListDataModel == null) {
                List<EntityType> domainItemTypeList;
                String domainName = currentViewDomain.getName();
                domainItemTypeList = getItemTypeCategoryEntityListByDomainName(domainName);
                currentDomainListDataModel = new ListDataModel(domainItemTypeList);
            }
            return currentDomainListDataModel;
        }
    }
    
    private String getCompleteItemTypeCategoryTitle(String itemControllerTitle) {
        return currentViewDomain.getName() + " Item " + itemControllerTitle; 
    }

    public String getItemTypeTitle() {
        if (currentViewDomain != null) {
            String itemTypeTitle = ItemController.getItemItemTypeTitleForDomain(currentViewDomain); 
            return getCompleteItemTypeCategoryTitle(itemTypeTitle);
        }
        return "Item Type";
    }

    public String getItemCategoryTitle() {
        if (currentViewDomain != null) {
            String itemCategoryTitle = ItemController.getItemItemCategoryTitleForDomain(currentViewDomain); 
            return getCompleteItemTypeCategoryTitle(itemCategoryTitle);
        }
        return "Item Category";
    }

    private String getCurrentViewDisplayEntityTypeName() {
        if (currentViewDomain != null) {
            if (this instanceof ItemCategoryController) {
                return getItemCategoryTitle();
            } else if (this instanceof ItemTypeController) {
                return getItemTypeTitle();
            }
        }
        return null;
    }

    @Override
    public String getDisplayEntityTypeName() {
        String displayEntityTypeName;
        displayEntityTypeName = getCurrentViewDisplayEntityTypeName();
        if (displayEntityTypeName == null) {
            displayEntityTypeName = getDefaultDisplayEntityTypeName();
        }
        return displayEntityTypeName;
    }

    public String prepareItemTypeCategoryEntityList(Domain itemDomain) {
        if (!ObjectUtility.equals(itemDomain, currentViewDomain)) {
            currentDomainListDataModel = null;
        }
        currentViewDomain = itemDomain;

        return "/views/" + getEntityTypeName() + "/list.xhtml?faces-redirect=true";
    }

    public boolean isItemTypeCategoryDomainEditDisabled() {
        return currentViewDomain != null;
    }

}
