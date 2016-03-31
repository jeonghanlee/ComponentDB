/*
 * Copyright (c) 2014-2015, Argonne National Laboratory.
 *
 * SVN Information:
 *   $HeadURL$
 *   $Date$
 *   $Revision$
 *   $Author$
 */
package gov.anl.aps.cdb.portal.model.jsf.beans;

import gov.anl.aps.cdb.portal.model.db.beans.LocationDbFacade;
import gov.anl.aps.cdb.portal.model.db.entities.Location;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

/**
 * JSF bean for location list tree view.
 */
@Named("locationListTreeViewBean")
@RequestScoped
public class LocationListTreeViewBean implements Serializable {

    @EJB
    LocationDbFacade locationFacade;

    private TreeNode rootNode;

    @PostConstruct
    public void init() {
        rootNode = createLocationRoot();
    }

    public void resetRootNode() {
        rootNode = null;
    }

    public TreeNode getRootNode() {
        if (rootNode == null) {
            rootNode = createLocationRoot();
        }
        return rootNode;
    }

    public TreeNode createLocationRoot() {
        TreeNode locationRoot = new DefaultTreeNode(new Location(), null);
        List<Location> locationsWithoutParents = locationFacade.findLocationsWithoutParents();
        for (Location location : locationsWithoutParents) {
            TreeNode locationNode = new DefaultTreeNode(location, locationRoot);
            populateLocationNode(locationNode, location);
        }
        return locationRoot;
    }

    private void populateLocationNode(TreeNode locationNode, Location location) {
        for (Location childLocation : location.getChildLocationList()) {
            TreeNode childLocationNode = new DefaultTreeNode(childLocation, locationNode);
            populateLocationNode(childLocationNode, childLocation);
        }
    }

}
