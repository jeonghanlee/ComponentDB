/*
 * Copyright (c) 2014-2015, Argonne National Laboratory.
 *
 * SVN Information:
 *   $HeadURL$
 *   $Date$
 *   $Revision$
 *   $Author$
 */
package gov.anl.aps.cdb.portal.model.db.utilities;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.common.exceptions.InvalidObjectState;
import gov.anl.aps.cdb.portal.model.db.entities.AssemblyComponent;
import gov.anl.aps.cdb.portal.model.db.entities.Component;
import gov.anl.aps.cdb.portal.model.db.entities.Design;
import gov.anl.aps.cdb.portal.model.db.entities.DesignElement;
import java.util.ArrayList;
import java.util.List;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

/**
 * DB utility class for design elements.
 */
public class DesignElementUtility {

    public static TreeNode createDesignElementRoot(Design parentDesign) throws CdbException {
        TreeNode designElementRoot = new DefaultTreeNode(new DesignElement(), null);
        if (parentDesign == null) {
            throw new CdbException("Cannot create design element tree view: parent design is not set.");
        }

        // Use "tree branch" list to prevent circular trees
        // Whenever new design is encountered, it will be added to the tree branch list before populating
        // element node, and removed from the branch list after population is done
        // If an object is encountered twice in the tree branch, this designates an error.
        List<Design> designTreeBranch = new ArrayList<>();
        populateDesignNode(designElementRoot, parentDesign, designTreeBranch);
        return designElementRoot;
    }

    private static void populateDesignNode(TreeNode designElementNode, Design design, List<Design> designTreeBranch) throws InvalidObjectState {
        List<DesignElement> designElementList = design.getDesignElementList();
        if (designElementList == null) {
            return;
        }
        designTreeBranch.add(design);
        for (DesignElement designElement : designElementList) {
            Component component = designElement.getComponent();
            Design childDesign = designElement.getChildDesign();
            if (component == null && childDesign == null) {
                continue;
            }

            TreeNode childDesignElementNode = new DefaultTreeNode(designElement, designElementNode);
            if (childDesign != null) {
                if (designTreeBranch.contains(childDesign)) {
                    throw new InvalidObjectState("Cannot create design element tree view: circular child/parent relationship found with designs "
                            + design.getName() + " and " + childDesign.getName() + ".");
                }
                populateDesignNode(childDesignElementNode, childDesign, designTreeBranch);
            }
            if (component != null) {
                populateAssemblyNode(childDesignElementNode, component);
            }
        }
        designTreeBranch.remove(design);
    }

    private static void populateAssemblyNode(TreeNode designElementNode, Component assembly) throws InvalidObjectState {
        List<AssemblyComponent> assemblyComponentList = assembly.getAssemblyComponentList();
        if (assemblyComponentList == null) {
            return;
        }
        for (AssemblyComponent assemblyComponent : assemblyComponentList) {
            Component component = assemblyComponent.getComponent();
            // Create fake design element for tree view display purpose
            DesignElement designElement = new DesignElement();
            designElement.setComponent(component);
            TreeNode childDesignElementNode = new DefaultTreeNode(designElement, designElementNode);
            populateAssemblyNode(childDesignElementNode, component);
        }
    }

}
