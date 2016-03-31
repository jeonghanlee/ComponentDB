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

import gov.anl.aps.cdb.common.utilities.CollectionUtility;
import gov.anl.aps.cdb.common.utilities.ObjectUtility;
import gov.anl.aps.cdb.portal.model.db.utilities.EntityInfoUtility;
import gov.anl.aps.cdb.portal.model.db.utilities.LogUtility;
import gov.anl.aps.cdb.portal.model.db.utilities.PropertyValueUtility;
import gov.anl.aps.cdb.portal.utilities.SearchResult;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Component entity class.
 */
@Entity
@Table(name = "component")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Component.findAll", query = "SELECT c FROM Component c ORDER BY c.name"),
    @NamedQuery(name = "Component.findById", query = "SELECT c FROM Component c WHERE c.id = :id"),
    @NamedQuery(name = "Component.findByName", query = "SELECT c FROM Component c WHERE c.name = :name"),
    @NamedQuery(name = "Component.findByNameAndModelNumber", query = "SELECT c FROM Component c WHERE c.name = :name AND c.modelNumber = :modelNumber"),
    @NamedQuery(name = "Component.findByNameWithNullModelNumber", query = "SELECT c FROM Component c WHERE c.name = :name AND c.modelNumber IS NULL"),
    @NamedQuery(name = "Component.findByModelNumber", query = "SELECT c FROM Component c WHERE c.modelNumber = :modelNumber"),
    @NamedQuery(name = "Component.findByDescription", query = "SELECT c FROM Component c WHERE c.description = :description")})
public class Component extends CdbAbstractDomainEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(max = 64)
    private String name;
    @Size(max = 64)
    @Column(name = "model_number")
    private String modelNumber;
    @Size(max = 256)
    private String description;
    @JoinTable(name = "component_log", joinColumns = {
        @JoinColumn(name = "component_id", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "log_id", referencedColumnName = "id")})
    @OrderBy("id DESC")
    @ManyToMany(cascade = CascadeType.ALL)
    private List<Log> logList;
    @JoinTable(name = "component_property", joinColumns = {
        @JoinColumn(name = "component_id", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "property_value_id", referencedColumnName = "id")})
    @ManyToMany(cascade = CascadeType.ALL)
    private List<PropertyValue> propertyValueList;
    @OneToMany(mappedBy = "linkComponentId")
    private List<DesignElementConnection> designElementConnectionList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "component")
    private List<ComponentConnector> componentConnectorList;
    @JoinColumn(name = "entity_info_id", referencedColumnName = "id")
    @ManyToOne(cascade = CascadeType.ALL, optional = false)
    private EntityInfo entityInfo;
    @JoinColumn(name = "component_type_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private ComponentType componentType;
    @OrderBy("qrId ASC")
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "component")
    private List<ComponentInstance> componentInstanceList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "component")
    private List<AssemblyComponent> assemblyList;
    @OrderBy("sortOrder ASC")
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "assembly")
    private List<AssemblyComponent> assemblyComponentList;
    @OneToMany(mappedBy = "component")
    private List<DesignElement> designElementMemberList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "component")
    private List<ComponentSource> componentSourceList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "componentId")
    private List<ComponentResource> componentResourceList;

    private transient boolean isCloned = false;

    public Component() {
    }

    public Component(Integer id) {
        this.id = id;
    }

    public Component(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getModelNumber() {
        return modelNumber;
    }

    public void setModelNumber(String modelNumber) {
        this.modelNumber = modelNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @XmlTransient
    @Override
    public List<Log> getLogList() {
        return logList;
    }

    public void setLogList(List<Log> logList) {
        this.logList = logList;
    }

    @XmlTransient
    @Override
    public List<PropertyValue> getPropertyValueList() {
        return propertyValueList;
    }

    public void setPropertyValueList(List<PropertyValue> propertyValueList) {
        this.propertyValueList = propertyValueList;
    }

    @XmlTransient
    public List<DesignElementConnection> getDesignElementConnectionList() {
        return designElementConnectionList;
    }

    public void setDesignElementConnectionList(List<DesignElementConnection> designElementConnectionList) {
        this.designElementConnectionList = designElementConnectionList;
    }

    @XmlTransient
    public List<ComponentConnector> getComponentConnectorList() {
        return componentConnectorList;
    }

    public void setComponentConnectorList(List<ComponentConnector> componentConnectorList) {
        this.componentConnectorList = componentConnectorList;
    }

    @Override
    public EntityInfo getEntityInfo() {
        return entityInfo;
    }

    public void setEntityInfo(EntityInfo entityInfo) {
        this.entityInfo = entityInfo;
    }

    public ComponentType getComponentType() {
        return componentType;
    }

    public void setComponentType(ComponentType componentType) {
        this.componentType = componentType;
    }

    @XmlTransient
    public List<ComponentInstance> getComponentInstanceList() {
        return componentInstanceList;
    }

    public void setComponentInstanceList(List<ComponentInstance> componentInstanceList) {
        this.componentInstanceList = componentInstanceList;
    }

    @XmlTransient
    public List<AssemblyComponent> getAssemblyList() {
        return assemblyList;
    }

    public void setAssemblyList(List<AssemblyComponent> assemblyList) {
        this.assemblyList = assemblyList;
    }

    @XmlTransient
    public List<AssemblyComponent> getAssemblyComponentList() {
        return assemblyComponentList;
    }

    public void setAssemblyComponentList(List<AssemblyComponent> assemblyComponentList) {
        this.assemblyComponentList = assemblyComponentList;
    }

    @Override
    @XmlTransient
    public List<DesignElement> getDesignElementMemberList() {
        return designElementMemberList;
    }

    public void setDesignElementMemberList(List<DesignElement> designElementMemberList) {
        this.designElementMemberList = designElementMemberList;
    }

    @XmlTransient
    public List<ComponentSource> getComponentSourceList() {
        return componentSourceList;
    }

    public void setComponentSourceList(List<ComponentSource> componentSourceList) {
        this.componentSourceList = componentSourceList;
    }

    @XmlTransient
    public List<ComponentResource> getComponentResourceList() {
        return componentResourceList;
    }

    public void setComponentResourceList(List<ComponentResource> componentResourceList) {
        this.componentResourceList = componentResourceList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    public boolean equalsByName(Component other) {
        if (other != null) {
            return ObjectUtility.equals(this.name, other.name);
        }
        return false;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Component)) {
            return false;
        }
        Component other = (Component) object;
        if (this.id == null && other.id == null) {
            return equalsByName(other);
        }

        if (this.id == null || other.id == null) {
            return false;
        }
        return this.id.equals(other.id);
    }

    public String getNameAndModelNumber() {
        if (modelNumber != null && !modelNumber.isEmpty()) {
            return name + "/" + modelNumber;
        }
        return name;
    }

    @Override
    public String toString() {
        return getNameAndModelNumber();
    }

    public void addComponentTypeProperties() {
        if (isCloned) {
            // this component has been cloned, do nothing
            return;
        }
        propertyValueList = new ArrayList<>();
        for (PropertyType propertyType : componentType.getPropertyTypeList()) {
            PropertyValue propertyValue = new PropertyValue();
            propertyValue.setPropertyType(propertyType);
            propertyValue.setValue(propertyType.getDefaultValue());
            propertyValue.setUnits(propertyType.getDefaultUnits());
            propertyValue.setEnteredByUser(entityInfo.getCreatedByUser());
            propertyValue.setEnteredOnDateTime(entityInfo.getCreatedOnDateTime());
            propertyValueList.add(propertyValue);
        }
    }

    @Override
    public Component clone() throws CloneNotSupportedException {
        Component cloned = (Component) super.clone();
        cloned.id = null;
        cloned.name = "Cloned from: " + cloned.name;
        if (modelNumber != null && !modelNumber.isEmpty()) {
            cloned.modelNumber = "Cloned from: " + cloned.modelNumber;
        }
        cloned.description = description;
        cloned.componentConnectorList = null;
        cloned.componentInstanceList = null;
        cloned.assemblyList = null;
        cloned.assemblyComponentList = null;
        cloned.designElementMemberList = null;
        cloned.componentResourceList = null;
        cloned.logList = null;
        cloned.propertyValueList = null;
        cloned.componentSourceList = null;
        cloned.entityInfo = null;
        cloned.isCloned = true;
        return cloned;
    }

    public Component copyAndSetEntityInfo(EntityInfo entityInfo) {
        Component copied = null;
        try {
            copied = clone();
            copied.entityInfo = entityInfo;
            copied.propertyValueList = new ArrayList<>();
            for (PropertyValue propertyValue : propertyValueList) {
                PropertyValue propertyValue2 = propertyValue.copyAndSetUserInfoAndDate(entityInfo.getLastModifiedByUser(), entityInfo.getLastModifiedOnDateTime());
                copied.propertyValueList.add(propertyValue2);
            }
            copied.componentSourceList = new ArrayList<>();
            for (ComponentSource componentSource : componentSourceList) {
                ComponentSource componentSource2 = componentSource.copyAndSetComponent(copied);
                copied.componentSourceList.add(componentSource2);
            }
            copied.assemblyComponentList = new ArrayList<>();
            for (AssemblyComponent assemblyComponent : assemblyComponentList) {
                AssemblyComponent assemblyComponent2 = assemblyComponent.copyAndSetAssembly(copied);
                copied.assemblyComponentList.add(assemblyComponent2);
            }
        } catch (CloneNotSupportedException ex) {
            // will not happen 
        }
        return copied;
    }

    @Override
    public SearchResult search(Pattern searchPattern) {
        SearchResult searchResult = new SearchResult(id, name);
        searchResult.doesValueContainPattern("name", name, searchPattern);
        searchResult.doesValueContainPattern("modelNumber", modelNumber, searchPattern);
        searchResult.doesValueContainPattern("description", description, searchPattern);
        LogUtility.searchLogList(logList, searchPattern, searchResult);
        PropertyValueUtility.searchPropertyValueList(propertyValueList, searchPattern, searchResult);
        EntityInfoUtility.searchEntityInfo(entityInfo, searchPattern, searchResult);
        return searchResult;
    }

    public String getDisplayNameWithTypeAndCategory() {
        String result = name + " [" + componentType.getNameWithCategory() + "]";
        return result;
    }

    public String getComponentSources() {
        if (componentSourceList == null) {
            return "";
        }
        return CollectionUtility.displayItemListWithoutOutsideDelimiters(componentSourceList, ";");
    }

    public Boolean getIsAssembly() {
        return (assemblyComponentList != null && !assemblyComponentList.isEmpty());
    }

    public void resetAttributesToNullIfEmpty() {
        if (modelNumber != null && modelNumber.isEmpty()) {
            modelNumber = null;
        }
    }

 
}
