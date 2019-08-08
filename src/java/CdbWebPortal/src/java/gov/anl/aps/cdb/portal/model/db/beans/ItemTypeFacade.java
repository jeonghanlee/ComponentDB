/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.anl.aps.cdb.portal.model.db.beans;

import gov.anl.aps.cdb.portal.model.db.entities.ItemType;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author djarosz
 */
@Stateless
public class ItemTypeFacade extends CdbEntityFacade<ItemType> {

    @PersistenceContext(unitName = "CdbWebPortalPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ItemTypeFacade() {
        super(ItemType.class);
    }
    
    public List<ItemType> findByDomainName(String domainName) {
        return (List<ItemType>) em.createNamedQuery("ItemType.findByDomainName")
                .setParameter("domainName", domainName)
                .getResultList(); 
    }
    
}
