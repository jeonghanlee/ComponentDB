/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gov.anl.aps.cdb.portal.controllers.ItemController;
import java.io.Serializable;

public abstract class ItemTypeCategoryEntity extends CdbEntity implements Serializable {
    
    private transient boolean hasCategories; 
    private transient boolean hasTypes; 

    public abstract Domain getDomain();

    public abstract void setDomain(Domain domain);

    protected String getItemTypeTitle() {
        if (getDomain() != null) {
            return ItemController.getItemItemTypeTitleForDomain(getDomain());
        }
        return "Type";
    }

    protected String getItemCategoryTitle() {
        if (getDomain() != null) {
            return ItemController.getItemItemCategoryTitleForDomain(getDomain());
        }
        return "Category"; 
    }
    
    @JsonIgnore
    public boolean getHasCategories() {
        if (this instanceof ItemCategory) {
            return false; 
        }
        if (getDomain() != null) {
            return ItemController.getItemHasItemCategoriesForDomain(getDomain()); 
        }
        return false; 
    }
    
    @JsonIgnore
    public boolean getHasTypes() {
        if (this instanceof ItemType) {
            return false; 
        }        
        if (getDomain() != null) {
            return ItemController.getItemHasItemTypesForDomain(getDomain()); 
        }
        return false; 
    }

}
