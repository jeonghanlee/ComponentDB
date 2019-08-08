/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gov.anl.aps.cdb.portal.constants.ItemDomainName;
import gov.anl.aps.cdb.portal.controllers.ItemController;
import gov.anl.aps.cdb.portal.controllers.ItemDomainCatalogController;
import java.util.List;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 *
 * @author djarosz
 */
@Entity
@DiscriminatorValue(value = ItemDomainName.CATALOG_ID + "")  
public class ItemDomainCatalog extends Item {
    
    private transient String machineDesignPlaceholderName = null; 
    
    @JsonIgnore
    public List<ItemDomainInventory> getInventoryItemList() {
        return (List<ItemDomainInventory>)(List<?>) super.getDerivedFromItemList(); 
    }        

    @Override
    public Item createInstance() {
        return new ItemDomainCatalog(); 
    }        

    @JsonIgnore
    public String getMachineDesignPlaceholderName() {
        return machineDesignPlaceholderName;
    }

    public void setMachineDesignPlaceholderName(String machineDesignPlaceholderName) {
        this.machineDesignPlaceholderName = machineDesignPlaceholderName;
    }
    
    @Override
    public ItemController getItemDomainController() {
        return ItemDomainCatalogController.getInstance();
    }
   
}
