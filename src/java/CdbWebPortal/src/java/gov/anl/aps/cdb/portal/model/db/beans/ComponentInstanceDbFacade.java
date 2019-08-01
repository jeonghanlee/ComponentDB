/*
 * Copyright (c) 2014-2015, Argonne National Laboratory.
 *
 * SVN Information:
 *   $HeadURL$
 *   $Date$
 *   $Revision$
 *   $Author$
 */
package gov.anl.aps.cdb.portal.model.db.beans;

import gov.anl.aps.cdb.common.exceptions.ObjectAlreadyExists;
import gov.anl.aps.cdb.portal.model.db.entities.ComponentInstance;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

/**
 * DB facade for component instances.
 */
@Stateless
public class ComponentInstanceDbFacade extends CdbEntityDbFacade<ComponentInstance> {

    @PersistenceContext(unitName = "CdbWebPortalPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ComponentInstanceDbFacade() {
        super(ComponentInstance.class);
    }

    public ComponentInstance findById(Integer id) {
        try {
            return (ComponentInstance) em.createNamedQuery("ComponentInstance.findById")
                    .setParameter("id", id)
                    .getSingleResult();
        } catch (NoResultException ex) {
        }
        return null;
    }

    public ComponentInstance findByQrId(Integer qrId) {
        try {
            return (ComponentInstance) em.createNamedQuery("ComponentInstance.findByQrId")
                    .setParameter("qrId", qrId)
                    .getSingleResult();
        } catch (NoResultException ex) {
        }
        return null;
    }

    public void checkUniqueness(ComponentInstance componentInstance) throws ObjectAlreadyExists {
        if (componentInstance.getQrId() != null) {
            // Check QR Id
            ComponentInstance existingComponentInstance = findByQrId(componentInstance.getQrId());
            if (existingComponentInstance != null && !existingComponentInstance.getId().equals(componentInstance.getId())) {
                throw new ObjectAlreadyExists("Component instance with QR ID " + componentInstance.getQrId() + " already exists.");
            }
        }
    }

    public List<ComponentInstance> findAllOrderByQrId() {
        return (List<ComponentInstance>) em.createNamedQuery("ComponentInstance.findAllOrderByQrId")
                .getResultList();
    }
}
