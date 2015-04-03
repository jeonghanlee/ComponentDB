/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.apps.componenthistory.echo2;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.Collections;

// EchoPointNG
import echopointng.tree.TreePath;

// persistence layer
import gov.anl.aps.irmis.persistence.component.Component;
import gov.anl.aps.irmis.persistence.component.ComponentRelationship;
import gov.anl.aps.irmis.persistence.component.ComponentRelationshipType;

// service layer
import gov.anl.aps.irmis.service.component.ComponentService;
import gov.anl.aps.irmis.service.component.ComponentTypeService;
import gov.anl.aps.irmis.service.IRMISException;


/**
 * Extension of ComponentTreeModel which can apply various application-specific filters to 
 * what components are shown in the tree. The main purpose is to handle the adjusted
 * child indices that occur when components are filtered out. 
 */
public class FilteredHousingComponentTreeModel extends ComponentTreeModel {

    List roomList = null;  // only show these rooms
    List buildingList = null;    // only show these buildings

    public FilteredHousingComponentTreeModel() {
        super(ComponentRelationshipType.HOUSING);
    }

    public TreePath getTreePath(Component node) {
        List tempPath = ComponentService.getComponentPathToRoot(node, hierarchy, buildingList);
        if (tempPath == null)
            return null;
        Collections.reverse(tempPath);
        return new TreePath(tempPath.toArray());
    }

    public Object getChild(Object parent, int index) {
        Component c = (Component)parent;
        List childList = c.getChildRelationships(hierarchy);
        if (childList == null || childList.size()==0) {
            return null;

        } else {
            // show only buildings that passed room constraints
            if (buildingList != null &&
                c.getComponentType().getComponentTypeName().equals("Site")) {
                return buildingList.get(index);

            } else if (buildingList != null &&
                       c.getComponentType().getComponentTypeName().equals("Building")) {
                return roomList.get(index);

            } else {  // no filter
                return super.getChild(parent, index);
            }
        }
    }

    public int getChildCount(Object parent) {
        Component c = (Component)parent;
        List childRelationships = c.getChildRelationships(hierarchy);
        if (childRelationships == null || childRelationships.size()==0) {
            return 0;
        } else {
            // apply housing filters
            // only count buildings that passed room constraints
            if (buildingList != null &&
                c.getComponentType().getComponentTypeName().equals("Site")) {
                return buildingList.size();

            } else if (buildingList != null &&
                       c.getComponentType().getComponentTypeName().equals("Building")) {
                return roomList.size();
                
            } else {
                return super.getChildCount(parent);
            }
        }
    }

    public int getIndexOfChild(Object parent, Object child) {
        Component pc = (Component)parent;
        Component childc = (Component)child;
        ComponentRelationship parentRel = childc.getParentRelationship(hierarchy);
        if (parentRel == null) {
            return -1;
        } else {
            // can't use logical order when we have filtered display
            if (buildingList != null &&
                pc.getComponentType().getComponentTypeName().equals("Site")) {
                return buildingList.indexOf(childc);

            } else if (buildingList != null &&
                       pc.getComponentType().getComponentTypeName().equals("Building")) {
                return roomList.indexOf(childc);

            } else {
                return super.getIndexOfChild(parent, child);
            }
        }
    }

    /**
     * Filters the housing display to only the given rooms.
     */
    public void setRoomConstraint(List roomList) {
        int hierarchy = ComponentRelationshipType.HOUSING;
        this.roomList = roomList;
        
        if (roomList != null && roomList.size() > 0) {
            buildingList = new ArrayList();
            List childList = root.getChildComponents(hierarchy);
            if (childList != null && childList.size() > 0) {
                Iterator childIt = childList.iterator();
                while (childIt.hasNext()) {
                    Component buildingComponent = (Component)childIt.next();
                    
                    // determine if building contains one of the rooms in roomList
                    Iterator roomIt = roomList.iterator();
                    while (roomIt.hasNext()) {
                        Component room = (Component)roomIt.next();
                        if (buildingComponent.contains(room, hierarchy, 1)) {
                            buildingList.add(buildingComponent);
                            break;
                        } 
                    }
                }
            }
            
        }  else {
            buildingList = null;
        }
    }


}