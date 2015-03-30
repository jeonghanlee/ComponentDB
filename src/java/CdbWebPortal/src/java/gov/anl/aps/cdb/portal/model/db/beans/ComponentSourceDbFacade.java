/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.anl.aps.cdb.portal.model.db.beans;

import gov.anl.aps.cdb.portal.model.db.entities.ComponentSource;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author sveseli
 */
@Stateless
public class ComponentSourceDbFacade extends CdbEntityDbFacade<ComponentSource> {

    @PersistenceContext(unitName = "CdbWebPortalPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ComponentSourceDbFacade() {
        super(ComponentSource.class);
    }

    public List<ComponentSource> findAllByComponentId(Integer componentId) {
        return (List<ComponentSource>) em.createNamedQuery("ComponentSource.findAllByComponentId")
                .setParameter("componentId", componentId)
                .getResultList();
    }
}