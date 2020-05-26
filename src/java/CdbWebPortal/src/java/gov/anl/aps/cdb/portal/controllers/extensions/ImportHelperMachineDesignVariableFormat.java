/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.extensions;

import gov.anl.aps.cdb.portal.controllers.ImportHelperBase;
import gov.anl.aps.cdb.portal.controllers.ItemDomainLocationController;
import gov.anl.aps.cdb.portal.controllers.ItemDomainMachineDesignController;
import gov.anl.aps.cdb.portal.controllers.ItemProjectController;
import gov.anl.aps.cdb.portal.model.db.beans.ItemFacade;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCatalog;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainInventory;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainLocation;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMachineDesign;
import gov.anl.aps.cdb.portal.model.db.entities.ItemProject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Row;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

/**
 *
 * @author craig
 */
public class ImportHelperMachineDesignVariableFormat extends ImportHelperBase<ItemDomainMachineDesign, ItemDomainMachineDesignController> {
    
    /**
     * Using custom handler so that we can have the machine design hierarchy
     * in multiple (variable numbered) columns in the import spreadsheet.
     * We expect columns like "Level 0", "Level 1" etc and then have to figure
     * out the parent for each item in the spreadsheet by looking for an item
     * at the previous indent level in the spreadsheet.  E.g., the parent of 
     * an item whose name appears in column "Level 1" is the last item whose
     * name was in "Level 0", etc.
     */
    private class NameHandler extends ColumnRangeInputHandler {
        
        protected int maxLength = 0;
        
        public NameHandler(int firstIndex, int lastIndex, int maxLength) {
            super(firstIndex, lastIndex);
            this.maxLength = maxLength;
        }
        
        public int getMaxLength() {
            return maxLength;
        }

        @Override
        public ValidInfo handleInput(
                Row row,
                Map<Integer, String> cellValueMap,
                ItemDomainMachineDesign entity) {
            
            boolean isValid = true;
            String validString = "";
            
            int currentIndentLevel = 1;
            int itemIndentLevel = 0;
            String itemName = null;
            for (int colIndex = getFirstColumnIndex();
                    colIndex <= getLastColumnIndex();
                    colIndex++) {
                
                String parsedValue = cellValueMap.get(colIndex);
                if ((parsedValue != null) && (!parsedValue.isEmpty())) {
                    if (itemName != null) {
                        // invalid, we have a value in 2 columns
                        isValid = false;
                        validString = "found name value in multiple columns";
                        return new ValidInfo(isValid, validString);
                    } else {
                        itemName = parsedValue;
                        itemIndentLevel = currentIndentLevel;
                    }
                }
                
                currentIndentLevel = currentIndentLevel + 1;                
            }
            
            if (itemName == null) {
                // didn't find a non-empty name column for this row
                isValid = false;
                validString = "name columns are all empty";
                return new ValidInfo(isValid, validString);
            }
            
            // check column length is valid
            if ((getMaxLength() > 0) && (itemName.length() > getMaxLength())) {
                isValid = false;
                validString = appendToString(validString, 
                        "Invalid name, length exceeds " + getMaxLength());
                return new ValidInfo(isValid, validString);
            }
            
            // set item info
            entity.setName(itemName);
            ImportInfo itemInfo = new ImportInfo();
            itemInfo.indentLevel = itemIndentLevel;
            itemInfoMap.put(entity, itemInfo);
                
            return new ValidInfo(isValid, validString);
        }
    }
    
    /**
     * Using a custom handler so that we can use catalog or inventory item id's
     * in a single column.  There is not a way to do this with IdRef handler, as
     * it needs a particular controller instance to use for the lookup.  The
     * query we need here is on the ItemFacade.
     */
    private class AssignedItemHandler extends SingleColumnInputHandler {
        
        public AssignedItemHandler(int columnIndex) {
            super(columnIndex);
        }
        
        @Override
        public ValidInfo handleInput(
                Row row,
                Map<Integer, String> cellValueMap,
                ItemDomainMachineDesign entity) {
            
            boolean isValid = true;
            String validString = "";
            
            String parsedValue = cellValueMap.get(columnIndex);
            
            Item assignedItem = null;
            if ((parsedValue != null) && (!parsedValue.isEmpty())) {
                // assigned item is specified
                
                assignedItem = ItemFacade.getInstance().findById(Integer.valueOf(parsedValue));
                
                if (assignedItem == null) {
                    String msg = "Unable to find object for: " + columnNameForIndex(columnIndex)
                            + " with id: " + parsedValue;
                    isValid = false;
                    validString = msg;
                    LOGGER.info("AssignedItemHandler.handleInput() " + msg);
                    
                } else {
                    // set assigned item
                    if (assignedItem instanceof ItemDomainCatalog) {
                        entity.setImportAssignedCatalogItem((ItemDomainCatalog) assignedItem);
                    } else if (assignedItem instanceof ItemDomainInventory) {
                        entity.setImportAssignedInventoryItem((ItemDomainInventory) assignedItem);
                    } else {
                        String msg = "Invalid object type for assigned item: " + assignedItem.getClass().getName();
                        isValid = false;
                        validString = msg;
                        LOGGER.info("AssignedItemHandler.handleInput() " + msg);
                    }
                }
            }

            return new ValidInfo(isValid, validString);
        }
    }
    
    /**
     * Using a custom handler for location so we can ignore the word "parent"
     * in a column that otherwise expects location item id's.  We could use the
     * standard IdRef handler if we didn't need to worry about "parent".
     */
    private class LocationHandler extends SingleColumnInputHandler {
        
        public LocationHandler(int columnIndex) {
            super(columnIndex);
        }
        
        @Override
        public ValidInfo handleInput(
                Row row,
                Map<Integer, String> cellValueMap,
                ItemDomainMachineDesign entity) {
            
            boolean isValid = true;
            String validString = "";
            
            String parsedValue = cellValueMap.get(columnIndex);
            
            ItemDomainLocation itemLocation = null;
            if ((parsedValue != null) && (!parsedValue.isEmpty())) {
                // location is specified
                
                // ignore word "parent"
                if (!parsedValue.equalsIgnoreCase("parent")) {
                
                    itemLocation = ItemDomainLocationController.getInstance().findById(Integer.valueOf(parsedValue));

                    if (itemLocation == null) {
                        String msg = "Unable to find object for: " + columnNameForIndex(columnIndex)
                                + " with id: " + parsedValue;
                        isValid = false;
                        validString = msg;
                        LOGGER.info("LocationHandler.handleInput() " + msg);

                    } else {
                        // set location
                        entity.setImportLocationItem(itemLocation);
                    }
                }
            }

            return new ValidInfo(isValid, validString);
        }
    }
    
    public class ImportInfo {
        
        public int indentLevel;
        
    }
    
    private class InputColumnInfo {
        public String name;
        public boolean isRequired;
        public String description;
        
        public InputColumnInfo(String name, boolean isRequired, String description) {
            this.name = name;
            this.isRequired = isRequired;
            this.description = description;
        }
    }
    

    private static final Logger LOGGER = LogManager.getLogger(ImportHelperMachineDesignVariableFormat.class.getName());
    
    private static final String HEADER_PARENT = "Parent ID";
    private static final String HEADER_BASE_LEVEL = "Level";
    private static final String HEADER_ALT_NAME = "Machine Design Alternate Name";
    private static final String HEADER_DESCRIPTION = "Machine Design Item Description";
    private static final String HEADER_ASSIGNED_ITEM = "Assigned Catalog/Inventory Item";
    private static final String HEADER_ASSIGNED_ITEM_ID = "Assigned Catalog/Inventory Item ID";
    private static final String HEADER_LOCATION = "Location";
    private static final String HEADER_PROJECT = "Project ID";
    private static final String HEADER_TEMPLATE = "Is Template?";

    protected static String completionUrlValue = "/views/itemDomainMachineDesign/list?faces-redirect=true";
    
    private Map<String, InputColumnInfo> columnInfoMap = null;
    protected Map<String, ItemDomainMachineDesign> itemByNameMap = new HashMap<>();
    protected Map<String, TreeNode> treeNodeMap = new HashMap<>();
    protected Map<ItemDomainMachineDesign, ImportInfo> itemInfoMap = new HashMap<>();
    protected Map<Integer, ItemDomainMachineDesign> parentIndentMap = new HashMap<>();
    
    private void initColumnInfoMap() {
        
        columnInfoMap = new HashMap<>();
        
        columnInfoMap.put(HEADER_PARENT, new InputColumnInfo(
                HEADER_PARENT, 
                false, 
                "CDB ID of parent machine design item.  Can only be provided for level 0 item."));
        
        columnInfoMap.put(HEADER_BASE_LEVEL, new InputColumnInfo(
                HEADER_BASE_LEVEL, 
                false, 
                "Machine design hierarchy column level"));
        
        columnInfoMap.put(HEADER_ALT_NAME, new InputColumnInfo(
                HEADER_ALT_NAME, 
                false, 
                "Alternate machine design item name."));
        
        columnInfoMap.put(HEADER_DESCRIPTION, new InputColumnInfo(
                HEADER_DESCRIPTION, 
                false, 
                "Textual description of machine design item."));
        
        columnInfoMap.put(HEADER_ASSIGNED_ITEM, new InputColumnInfo(
                HEADER_ASSIGNED_ITEM, 
                false, 
                "Name of assigned catalog or inventory item (optional, for reference only)."));
        
        columnInfoMap.put(HEADER_ASSIGNED_ITEM_ID, new InputColumnInfo(
                HEADER_ASSIGNED_ITEM_ID, 
                false, 
                "CDB ID of assigned catalog or inventory item."));
        
        columnInfoMap.put(HEADER_LOCATION, new InputColumnInfo(
                HEADER_LOCATION, 
                false, 
                "CDB ID of CDB location item (use of word 'parent' allowed for documentation purposes, it is ignored)."));
        
        columnInfoMap.put(HEADER_PROJECT, new InputColumnInfo(
                HEADER_PROJECT, 
                true, 
                "CDB ID or name of item project."));
        
        columnInfoMap.put(HEADER_TEMPLATE, new InputColumnInfo(
                HEADER_TEMPLATE, 
                true, 
                "TRUE if item is template, false otherwise."));
        
    }
    
    private Map<String, InputColumnInfo> getColumnInfoMap() {
        if (columnInfoMap == null) {
            initColumnInfoMap();
        }
        return columnInfoMap;
    }
    
    /**
     * Returns list of columns for download template file feature.  Because
     * initialize_() creates the columns dynamically after the spreadsheet file
     * is opened and read, the sample list of columns is hardwired here.
     * The number of "Level" columns is variable, and depends on the levels of
     * hierarchy to be added for a particular import.  Updates to input columns
     * must unfortunately be made in two places, here and in initialize_().
     */
    @Override
    protected List<InputColumnModel> getTemplateColumns() {
        
        List<InputColumnModel> inputColumns = new ArrayList<>();
        
        InputColumnInfo colInfo = getColumnInfoMap().get(HEADER_PARENT);
        inputColumns.add(new InputColumnModel(0, HEADER_PARENT, colInfo.isRequired, colInfo.description));
        
        colInfo = getColumnInfoMap().get(HEADER_BASE_LEVEL);
        inputColumns.add(new InputColumnModel(1, HEADER_BASE_LEVEL + " 0", true, colInfo.description + " 0"));
        inputColumns.add(new InputColumnModel(2, HEADER_BASE_LEVEL + " 1", false, colInfo.description + " 1"));
        inputColumns.add(new InputColumnModel(3, HEADER_BASE_LEVEL + " 2", false, colInfo.description + " 2"));
        
        colInfo = getColumnInfoMap().get(HEADER_ALT_NAME);
        inputColumns.add(new InputColumnModel(4, HEADER_ALT_NAME, colInfo.isRequired, colInfo.description));
        
        colInfo = getColumnInfoMap().get(HEADER_DESCRIPTION);
        inputColumns.add(new InputColumnModel(5, HEADER_DESCRIPTION, colInfo.isRequired, colInfo.description));

        colInfo = getColumnInfoMap().get(HEADER_ASSIGNED_ITEM);
        inputColumns.add(new InputColumnModel(6, HEADER_ASSIGNED_ITEM, colInfo.isRequired, colInfo.description));

        colInfo = getColumnInfoMap().get(HEADER_ASSIGNED_ITEM_ID);
        inputColumns.add(new InputColumnModel(7, HEADER_ASSIGNED_ITEM_ID, colInfo.isRequired, colInfo.description));

        colInfo = getColumnInfoMap().get(HEADER_LOCATION);
        inputColumns.add(new InputColumnModel(8, HEADER_LOCATION, colInfo.isRequired, colInfo.description));

        colInfo = getColumnInfoMap().get(HEADER_PROJECT);
        inputColumns.add(new InputColumnModel(9, HEADER_PROJECT, colInfo.isRequired, colInfo.description));

        colInfo = getColumnInfoMap().get(HEADER_TEMPLATE);
        inputColumns.add(new InputColumnModel(10, HEADER_TEMPLATE, colInfo.isRequired, colInfo.description));

        return inputColumns;
    }
    
    /**
     * Builds the input columns, handlers, and output columns for the helper
     * framework.  Note that because the number of "Level" columns is variable
     * the column layout is not fixed as is the case for some of the other
     * helpers.  Changes to input columns, therefore, must be made in two places,
     * here and in getTemplateColumns().
     */
    @Override
    protected InitializeInfo initialize_(
            int actualColumnCount,
            Map<Integer, String> headerValueMap) {
        
        String methodLogName = "initialize_() ";

        boolean isValid = true;
        String validString = "";
        
        List<InputColumnModel> inputColumns = new ArrayList<>();
        List<InputHandler> handlers = new ArrayList<>();
        List<OutputColumnModel> outputColumns = new ArrayList<>();
                
        boolean foundLevel = false;
        int firstLevelIndex = -1;
        int lastLevelIndex = -1;
        
        InputColumnInfo colInfo;
        
        for (Entry<Integer, String> entry : headerValueMap.entrySet()) {
            
            int columnIndex = entry.getKey();
            String columnHeader = entry.getValue();
            
            // check to see if this is a "level" column
            if (columnHeader.startsWith(HEADER_BASE_LEVEL)) {
                colInfo = getColumnInfoMap().get(HEADER_BASE_LEVEL);
                inputColumns.add(new InputColumnModel(columnIndex, columnHeader, false, colInfo.description));
                foundLevel = true;
                if (firstLevelIndex == -1) {
                    firstLevelIndex = columnIndex;
                }
                lastLevelIndex = columnIndex;
            } else {
            
                switch (columnHeader) {

                    case HEADER_PARENT:
                        colInfo = getColumnInfoMap().get(HEADER_PARENT);
                        inputColumns.add(new InputColumnModel(columnIndex, columnHeader, colInfo.isRequired, colInfo.description));
                        handlers.add(new IdRefInputHandler(columnIndex, "setImportContainerItem", ItemDomainMachineDesignController.getInstance(), ItemDomainMachineDesign.class));
                        break;

                    case HEADER_ALT_NAME:
                        colInfo = getColumnInfoMap().get(HEADER_ALT_NAME);
                        inputColumns.add(new InputColumnModel(columnIndex, columnHeader, colInfo.isRequired, colInfo.description));
                        handlers.add(new StringInputHandler(columnIndex, "setAlternateName", 32));
                        break;

                    case HEADER_DESCRIPTION:
                        colInfo = getColumnInfoMap().get(HEADER_DESCRIPTION);
                        inputColumns.add(new InputColumnModel(columnIndex, columnHeader, colInfo.isRequired, colInfo.description));
                        handlers.add(new StringInputHandler(columnIndex, "setDescription", 256));
                        break;

                    case HEADER_ASSIGNED_ITEM:
                        colInfo = getColumnInfoMap().get(HEADER_ASSIGNED_ITEM);
                        inputColumns.add(new InputColumnModel(columnIndex, columnHeader, colInfo.isRequired, colInfo.description));
                        break;

                    case HEADER_ASSIGNED_ITEM_ID:
                        colInfo = getColumnInfoMap().get(HEADER_ASSIGNED_ITEM_ID);
                        inputColumns.add(new InputColumnModel(columnIndex, columnHeader, colInfo.isRequired, colInfo.description));
                        handlers.add(new AssignedItemHandler(columnIndex));
                        break;

                    case HEADER_LOCATION:
                        colInfo = getColumnInfoMap().get(HEADER_LOCATION);
                        inputColumns.add(new InputColumnModel(columnIndex, columnHeader, colInfo.isRequired, colInfo.description));
                        handlers.add(new LocationHandler(columnIndex));
                        break;

                    case HEADER_PROJECT:
                        colInfo = getColumnInfoMap().get(HEADER_PROJECT);
                        inputColumns.add(new InputColumnModel(columnIndex, columnHeader, colInfo.isRequired, colInfo.description));
                        handlers.add(new IdRefInputHandler(columnIndex, "setProjectValue", ItemProjectController.getInstance(), ItemProject.class));
                        break;

                    case HEADER_TEMPLATE:
                        colInfo = getColumnInfoMap().get(HEADER_TEMPLATE);
                        inputColumns.add(new InputColumnModel(columnIndex, columnHeader, colInfo.isRequired, colInfo.description));
                        handlers.add(new BooleanInputHandler(columnIndex, "setImportIsTemplate"));
                        break;

                    default:
                        // unexpected column found, so fail
                        isValid = false;
                        String msg = "found unexpected column header: " + columnHeader;
                        validString = msg;
                        LOGGER.info(methodLogName + msg);
                }
            }
        }
        
        if (!foundLevel) {
            // didn't find any "Level" columns, so fail
            isValid = false;
            String msg = "one or more 'Level' columns is required";
            validString = msg;
            LOGGER.info(methodLogName + msg);
            
        } else {
            // add handler for multiple "level" columns
            handlers.add(new NameHandler(firstLevelIndex, lastLevelIndex, 128));
        }
        
        // output columns are fixed
        outputColumns.add(new OutputColumnModel("Name", "name"));
        outputColumns.add(new OutputColumnModel("Is Template", "importIsTemplateString"));
        outputColumns.add(new OutputColumnModel("Project", "itemProjectString"));
        outputColumns.add(new OutputColumnModel("Alt Name", "alternateName"));
        outputColumns.add(new OutputColumnModel("Description", "description"));
        outputColumns.add(new OutputColumnModel("Parent Item", "importContainerString"));
        outputColumns.add(new OutputColumnModel("Assigned Catalog Item", "importAssignedCatalogItemString"));
        outputColumns.add(new OutputColumnModel("Assigned Inventory Item", "importAssignedInventoryItemString"));
        outputColumns.add(new OutputColumnModel("Location", "importLocationItemString"));
        
        ValidInfo validInfo = new ValidInfo(isValid, validString);
        InitializeInfo initInfo = new InitializeInfo(inputColumns, handlers, outputColumns, validInfo);
                
        return initInfo;
    }
    
    @Override
    protected String getCompletionUrlValue() {
        return completionUrlValue;
    }
    
    @Override
    public ItemDomainMachineDesignController getEntityController() {
        return ItemDomainMachineDesignController.getInstance();
    }

    @Override
    public String getTemplateFilename() {
        return "Machine Design Template";
    }
    
    @Override
    protected ItemDomainMachineDesign createEntityInstance() {
        return getEntityController().createEntityInstance();
    }
    
    @Override
    protected void reset_() {
        itemByNameMap = new HashMap<>();
        treeNodeMap = new HashMap<>();
    }
    
    /**
     * Specifies whether the subclass will provide a tree view.  Default is false,
     * subclass should override to customize.
     */
    public boolean hasTreeView() {
        return true;
    }

    /**
     * Set parent/child relationships between items, and create new item at
     * specified path within container item.  The path contains the names of 
     * the parent nodes between the new item and the container item, separated by
     * Unix style slashes.
     */
    @Override
    protected ImportHelperBase.ValidInfo postParseRow(ItemDomainMachineDesign item) {

        String methodLogName = "postParseRow() ";

        boolean isValid = true;
        String validString = "";
        
        boolean isValidLocation = true;
        boolean isValidAssignedItem = true;
        
        ImportInfo itemInfo = itemInfoMap.get(item);
        if (itemInfo == null) {
            String msg = "Unable to find indent level for item";
            LOGGER.info(methodLogName + msg);
            validString = appendToString(validString, msg);
            isValid = false;
            
            // invalidate parent map, it is probably messed up by missing item
            parentIndentMap.clear();
            LOGGER.info(methodLogName + "Invalidating parentIndentMap");
            return new ValidInfo(isValid, validString);
        }
        
        // find parent for this item
        int itemIndentLevel = itemInfo.indentLevel;
        ItemDomainMachineDesign itemParent = null;
        if (itemIndentLevel > 1) {
            
            // not allowed to specify parent for non level 0 item
            if (item.getImportContainerItem() != null) {
                String msg = "Can only specify existing parent for level 0 item";
                LOGGER.info(methodLogName + msg);
                validString = appendToString(validString, msg);
                isValid = false;
            }
            
            // find parent at previous indent level
            itemParent = parentIndentMap.get(itemIndentLevel - 1);
            
            if (itemParent == null) {
                // should have a parent for this item in map
                String msg = "Unable to determine parent for item";
                LOGGER.info(methodLogName + msg);
                validString = appendToString(validString, msg);
                isValid = false;
            }
            
        } else {
            // this is either a top-level item, or a parent item is explicitly specified for it
            ItemDomainMachineDesign container = item.getImportContainerItem();
            if (container == null) {
                // new item is a top-level machine design with no parent
                String msg = "creating top-level machine design item: " + item.getName();
                LOGGER.debug(methodLogName + msg);
                itemParent = null;
            } else {
                itemParent = container;
            }
        }
        
        // check for template restrictions
        if (item.getIsItemTemplate()) {
            
            if ((item.getImportAssignedInventoryItem() != null)) {
                
                // template not allowed to have assigned inventory
                String msg = "Template cannot have assigned inventory item";
                LOGGER.info(methodLogName + msg);
                validString = appendToString(validString, msg);
                isValid = false;
                isValidAssignedItem = false;
            }
            
            if (item.getImportLocationItem() != null) {
                // template not allowed to have location
                String msg = "Template cannot have location item";
                LOGGER.info(methodLogName + msg);
                validString = appendToString(validString, msg);
                isValid = false;
                isValidLocation = false;
            }
            
        } else {
            // non-template item restrictions
            
            if (itemParent == null) {
                    if ((item.getImportAssignedCatalogItem() != null)
                        || (item.getImportAssignedInventoryItem() != null)) {
                        // top-level item cannot have assigned item
                    String msg = "Top-level item cannot have assigned catalog/inventory item";
                    LOGGER.info(methodLogName + msg);
                    validString = appendToString(validString, msg);
                    isValid = false;
                    isValidAssignedItem = false;
                }
            }
        }
        
        if (itemParent != null) {
            // restrictions for all items with parent, template or non-template

            if (!Objects.equals(item.getIsItemTemplate(), itemParent.getIsItemTemplate())) {
                // parent and child must both be templates or both not be
                String msg = "parent and child must both be templates or both not be templates";
                LOGGER.info(methodLogName + msg);
                validString = appendToString(validString, msg);
                isValid = false;
            }
        }
                                
        // establish parent/child relationship, set location info etc
        item.applyImportValues(itemParent, isValidAssignedItem, isValidLocation);
                 
        // set current item as last parent at its indent level
        parentIndentMap.put(itemIndentLevel, item);
        
        // update tree view with item and parent
        updateTreeView(item, itemParent);
        
        // add entry to name map for new item
        itemByNameMap.put(item.getName(), item);
               
        return new ValidInfo(isValid, validString);
    }

    protected void updateTreeView(ItemDomainMachineDesign item, 
            ItemDomainMachineDesign parent) {
        
        TreeNode itemNode = new DefaultTreeNode(item);
        itemNode.setExpanded(true);
        treeNodeMap.put(item.getName(), itemNode);
        
        if (parent != null) {
            TreeNode parentNode = treeNodeMap.get(parent.getName());
            if (parentNode != null) {
                // parent tree node already exists so add child to it
                parentNode.getChildren().add(itemNode);
                
            } else {
                // parent tree node doesn't exist, so create new tree nodes for
                // parent and its ancestors, and add child to parent
                parentNode = new DefaultTreeNode(parent);
                parentNode.setExpanded(true);
                parentNode.getChildren().add(itemNode);
                treeNodeMap.put(parent.getName(), parentNode);
                ItemDomainMachineDesign ancestor = parent.getParentMachineDesign();
                TreeNode childNode = parentNode;
                while (ancestor != null) {
                    TreeNode ancestorNode = new DefaultTreeNode(ancestor);
                    ancestorNode.setExpanded(true);
                    treeNodeMap.put(ancestor.getName(), ancestorNode);
                    ancestorNode.getChildren().add(childNode);
                    ancestor = ancestor.getParentMachineDesign();
                    childNode = ancestorNode;
                }
                rootTreeNode.getChildren().add(childNode);
            }
            
        } else {
            // top level machine design item, so add to root tree node
            rootTreeNode.getChildren().add(itemNode);
        }
        
    }
}