/*
 * Copyright (c) 2014-2015, Argonne National Laboratory.
 *
 * SVN Information:
 *   $HeadURL$
 *   $Date$
 *   $Revision$
 *   $Author$
 */
package gov.anl.aps.cdb.portal.model.db.entities;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Assembly component connection entity class.
 */
@Entity
@Table(name = "assembly_component_connection")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "AssemblyComponentConnection.findAll", query = "SELECT a FROM AssemblyComponentConnection a"),
    @NamedQuery(name = "AssemblyComponentConnection.findById", query = "SELECT a FROM AssemblyComponentConnection a WHERE a.id = :id"),
    @NamedQuery(name = "AssemblyComponentConnection.findByLinkAssemblyComponentQuantity", query = "SELECT a FROM AssemblyComponentConnection a WHERE a.linkAssemblyComponentQuantity = :linkAssemblyComponentQuantity"),
    @NamedQuery(name = "AssemblyComponentConnection.findByLabel", query = "SELECT a FROM AssemblyComponentConnection a WHERE a.label = :label"),
    @NamedQuery(name = "AssemblyComponentConnection.findByDescription", query = "SELECT a FROM AssemblyComponentConnection a WHERE a.description = :description")})
public class AssemblyComponentConnection extends CdbEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    private Integer id;
    @Column(name = "link_assembly_component_quantity")
    private Integer linkAssemblyComponentQuantity;
    @Size(max = 64)
    private String label;
    @Size(max = 256)
    private String description;
    @JoinColumn(name = "link_assembly_component_id", referencedColumnName = "id")
    @ManyToOne
    private AssemblyComponent linkAssemblyComponentId;
    @JoinColumn(name = "second_component_connector_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private ComponentConnector secondComponentConnectorId;
    @JoinColumn(name = "second_assembly_component_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private AssemblyComponent secondAssemblyComponentId;
    @JoinColumn(name = "first_component_connector_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private ComponentConnector firstComponentConnectorId;
    @JoinColumn(name = "first_assembly_component_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private AssemblyComponent firstAssemblyComponentId;

    public AssemblyComponentConnection() {
    }

    public AssemblyComponentConnection(Integer id) {
        this.id = id;
    }

    @Override
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getLinkAssemblyComponentQuantity() {
        return linkAssemblyComponentQuantity;
    }

    public void setLinkAssemblyComponentQuantity(Integer linkAssemblyComponentQuantity) {
        this.linkAssemblyComponentQuantity = linkAssemblyComponentQuantity;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public AssemblyComponent getLinkAssemblyComponentId() {
        return linkAssemblyComponentId;
    }

    public void setLinkAssemblyComponentId(AssemblyComponent linkAssemblyComponentId) {
        this.linkAssemblyComponentId = linkAssemblyComponentId;
    }

    public ComponentConnector getSecondComponentConnectorId() {
        return secondComponentConnectorId;
    }

    public void setSecondComponentConnectorId(ComponentConnector secondComponentConnectorId) {
        this.secondComponentConnectorId = secondComponentConnectorId;
    }

    public AssemblyComponent getSecondAssemblyComponentId() {
        return secondAssemblyComponentId;
    }

    public void setSecondAssemblyComponentId(AssemblyComponent secondAssemblyComponentId) {
        this.secondAssemblyComponentId = secondAssemblyComponentId;
    }

    public ComponentConnector getFirstComponentConnectorId() {
        return firstComponentConnectorId;
    }

    public void setFirstComponentConnectorId(ComponentConnector firstComponentConnectorId) {
        this.firstComponentConnectorId = firstComponentConnectorId;
    }

    public AssemblyComponent getFirstAssemblyComponentId() {
        return firstAssemblyComponentId;
    }

    public void setFirstAssemblyComponentId(AssemblyComponent firstAssemblyComponentId) {
        this.firstAssemblyComponentId = firstAssemblyComponentId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AssemblyComponentConnection)) {
            return false;
        }
        AssemblyComponentConnection other = (AssemblyComponentConnection) object;
        return (this.id != null || other.id == null) && (this.id == null || this.id.equals(other.id));
    }

    @Override
    public String toString() {
        return "AssemblyComponentConnection[ id=" + id + " ]";
    }

}
