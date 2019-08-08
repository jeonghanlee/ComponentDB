/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.primefaces.model.TreeNode;
import org.primefaces.model.menu.DefaultMenuModel;

/**
 *
 * @author djarosz
 */
public abstract class LocatableItem extends Item {

    private transient TreeNode locationTree = null;
    private transient String locationDetails = null;
    private transient ItemDomainLocation location;
    private transient ItemElementRelationship locationRelationship; 
    private transient String locationString;
    private transient DefaultMenuModel locationMenuModel;

    // Needed to determine whenever location was removed in edit process. 
    private transient Boolean originalLocationLoaded = false;
    
    public void resetLocationVariables() {
        locationTree = null; 
        locationDetails = null; 
        location = null;
        locationString = null; 
        locationMenuModel = null; 
        originalLocationLoaded = false; 
        locationRelationship = null; 
    }

    @JsonIgnore
    public TreeNode getLocationTree() {
        return locationTree;
    }

    public void setLocationTree(TreeNode locationTree) {
        this.locationTree = locationTree;
    }

    @JsonIgnore
    public DefaultMenuModel getLocationMenuModel() {
        return locationMenuModel;
    }

    public void setLocationMenuModel(DefaultMenuModel locationMenuModel) {
        this.locationMenuModel = locationMenuModel;
    }

    @JsonIgnore
    public Boolean getOriginalLocationLoaded() {
        return originalLocationLoaded;
    }

    public void setOriginalLocationLoaded(Boolean originalLocationLoaded) {
        this.originalLocationLoaded = originalLocationLoaded;
    }

    @JsonIgnore
    public String getLocationDetails() {
        return locationDetails;
    }

    public void setLocationDetails(String locationDetails) {
        this.locationDetails = locationDetails;
    }

    @JsonIgnore
    public ItemDomainLocation getLocation() {
        return location;
    }

    public void setLocation(ItemDomainLocation location) {
        this.location = location;
    }

    @JsonIgnore
    public ItemElementRelationship getLocationRelationship() {
        return locationRelationship;
    }

    public void setLocationRelationship(ItemElementRelationship locationRelationship) {
        this.locationRelationship = locationRelationship;
    }

    @JsonIgnore
    public String getLocationString() {
        return locationString;
    }

    public void setLocationString(String locationString) {
        this.locationString = locationString;
    }
    
    @Override
    public Item clone() throws CloneNotSupportedException {
        LocatableItem clonedItem = (LocatableItem) super.clone();

        clonedItem.setLocationDetails(null);
        clonedItem.setLocation(null);
        
        return clonedItem;
    }

}
