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

import gov.anl.aps.cdb.common.utilities.ObjectUtility;
import gov.anl.aps.cdb.portal.utilities.SearchResult;
import java.util.List;
import java.util.regex.Pattern;
import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Property type category entity class.
 */
@Entity
@Table(name = "property_type_category")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "PropertyTypeCategory.findAll", query = "SELECT p FROM PropertyTypeCategory p ORDER BY p.name"),
    @NamedQuery(name = "PropertyTypeCategory.findById", query = "SELECT p FROM PropertyTypeCategory p WHERE p.id = :id"),
    @NamedQuery(name = "PropertyTypeCategory.findByName", query = "SELECT p FROM PropertyTypeCategory p WHERE p.name = :name"),
    @NamedQuery(name = "PropertyTypeCategory.findByDescription", query = "SELECT p FROM PropertyTypeCategory p WHERE p.description = :description")})
public class PropertyTypeCategory extends CdbEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(max = 64)
    private String name;
    @Size(max = 256)
    private String description;
    @OneToMany(mappedBy = "propertyTypeCategory")
    private List<PropertyType> propertyTypeList;

    public PropertyTypeCategory() {
    }

    public PropertyTypeCategory(Integer id) {
        this.id = id;
    }

    public PropertyTypeCategory(Integer id, String name) {
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @XmlTransient
    public List<PropertyType> getPropertyTypeList() {
        return propertyTypeList;
    }

    public void setPropertyTypeList(List<PropertyType> propertyTypeList) {
        this.propertyTypeList = propertyTypeList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    public boolean equalsByName(PropertyTypeCategory other) {
        if (other == null) {
            return false;
        }
        return ObjectUtility.equals(this.name, other.name);
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof PropertyTypeCategory)) {
            return false;
        }
        PropertyTypeCategory other = (PropertyTypeCategory) object;
        if (this.id == null && other.id == null) {
            return equalsByName(other);
        }

        if (this.id == null || other.id == null) {
            return false;
        }
        return this.id.equals(other.id);
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public SearchResult search(Pattern searchPattern) {
        SearchResult searchResult = new SearchResult(id, name);
        searchResult.doesValueContainPattern("name", name, searchPattern);
        searchResult.doesValueContainPattern("description", description, searchPattern);
        return searchResult;
    }
}
