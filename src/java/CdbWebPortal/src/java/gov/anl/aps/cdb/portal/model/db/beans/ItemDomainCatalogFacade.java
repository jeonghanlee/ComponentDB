/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.beans;

import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCatalog;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import javax.ejb.Stateless;

/**
 *
 * @author djarosz
 */

@Stateless
public class ItemDomainCatalogFacade extends ItemFacadeBase<ItemDomainCatalog> {
    
    public ItemDomainCatalogFacade() {
        super(ItemDomainCatalog.class);
    }
    
    public static ItemDomainCatalogFacade getInstance() {
        return (ItemDomainCatalogFacade) SessionUtility.findFacade(ItemDomainCatalogFacade.class.getSimpleName()); 
    }
    
}
