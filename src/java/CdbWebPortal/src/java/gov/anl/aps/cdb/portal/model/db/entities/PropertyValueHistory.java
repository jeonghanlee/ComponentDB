/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author djarosz
 */
@Entity
@Table(name = "property_value_history")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "PropertyValueHistory.findAll", query = "SELECT p FROM PropertyValueHistory p"),
    @NamedQuery(name = "PropertyValueHistory.findById", query = "SELECT p FROM PropertyValueHistory p WHERE p.id = :id"),
    @NamedQuery(name = "PropertyValueHistory.findByTag", query = "SELECT p FROM PropertyValueHistory p WHERE p.tag = :tag"),
    @NamedQuery(name = "PropertyValueHistory.findByValue", query = "SELECT p FROM PropertyValueHistory p WHERE p.value = :value"),
    @NamedQuery(name = "PropertyValueHistory.findByUnits", query = "SELECT p FROM PropertyValueHistory p WHERE p.units = :units"),
    @NamedQuery(name = "PropertyValueHistory.findByDescription", query = "SELECT p FROM PropertyValueHistory p WHERE p.description = :description"),
    @NamedQuery(name = "PropertyValueHistory.findByEnteredOnDateTime", query = "SELECT p FROM PropertyValueHistory p WHERE p.enteredOnDateTime = :enteredOnDateTime"),
    @NamedQuery(name = "PropertyValueHistory.findByDisplayValue", query = "SELECT p FROM PropertyValueHistory p WHERE p.displayValue = :displayValue"),
    @NamedQuery(name = "PropertyValueHistory.findByTargetValue", query = "SELECT p FROM PropertyValueHistory p WHERE p.targetValue = :targetValue")})
public class PropertyValueHistory extends PropertyValueBase implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    private Integer id;
    @Size(max = 64)
    private String tag;
    @Size(max = 256)
    private String value;
    @Size(max = 16)
    private String units;
    @Size(max = 256)
    private String description;
    @Basic(optional = false)
    @NotNull
    @Column(name = "entered_on_date_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date enteredOnDateTime;
    @Size(max = 256)
    @Column(name = "display_value")
    private String displayValue;
    @Size(max = 256)
    @Column(name = "target_value")
    private String targetValue;
    @JoinColumn(name = "property_value_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private PropertyValue propertyValue;
    @JoinColumn(name = "entered_by_user_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private UserInfo enteredByUser;    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "propertyValueHistory")
    @JsonIgnore
    private List<PropertyMetadataHistory> propertyMetadataHistoryList;
    
    private transient String infoActionCommand; 

    public PropertyValueHistory() {
    }

    public PropertyValueHistory(Integer id) {
        this.id = id;
    }

    public PropertyValueHistory(Integer id, Date enteredOnDateTime) {
        this.id = id;
        this.enteredOnDateTime = enteredOnDateTime;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    public Date getEnteredOnDateTime() {
        return enteredOnDateTime;
    }

    public void setEnteredOnDateTime(Date enteredOnDateTime) {
        this.enteredOnDateTime = enteredOnDateTime;
    }

    public String getDisplayValue() {
        return displayValue;
    }

    public void setDisplayValue(String displayValue) {
        this.displayValue = displayValue;
    }

    public String getTargetValue() {
        return targetValue;
    }

    public void setTargetValue(String targetValue) {
        this.targetValue = targetValue;
    }

    public PropertyValue getPropertyValue() {
        return propertyValue;
    }

    public void setPropertyValue(PropertyValue propertyValue) {
        this.propertyValue = propertyValue;
    }

    public UserInfo getEnteredByUser() {
        return enteredByUser;
    }

    public void setEnteredByUser(UserInfo enteredByUser) {
        this.enteredByUser = enteredByUser;
    }

    @XmlTransient
    public List<PropertyMetadataHistory> getPropertyMetadataHistoryList() {
        return propertyMetadataHistoryList;
    }
    
    @Override
    public List<PropertyMetadataBase> getPropertyMetadataBaseList() {
        return (List<PropertyMetadataBase>) (List<?>) getPropertyMetadataHistoryList();
    }

    public void setPropertyMetadataHistoryList(List<PropertyMetadataHistory> propertyMetadataHistoryList) {
        this.propertyMetadataHistoryList = propertyMetadataHistoryList;
    }
    
    public void updateFromPropertyValue(PropertyValue propertyValue) {
        this.propertyValue = propertyValue;
        this.tag = propertyValue.getTag();
        this.value = propertyValue.getValue();
        this.units = propertyValue.getUnits();
        this.description = propertyValue.getDescription();
        this.enteredByUser = propertyValue.getEnteredByUser();
        this.enteredOnDateTime = propertyValue.getEnteredOnDateTime();
        
        if (propertyValue.getIsHasPropertyMetadata()) {
            List<PropertyMetadataHistory> propertyMetadataHistoryList = new ArrayList<>(); 
            for (PropertyMetadata propertyMetadata : this.propertyValue.getPropertyMetadataList()) {
                PropertyMetadataHistory pmh = new PropertyMetadataHistory(); 
                pmh.setPropertyValueHistory(this);
                pmh.setMetadataKey(propertyMetadata.getMetadataKey());
                pmh.setMetadataValue(propertyMetadata.getMetadataValue()); 
                propertyMetadataHistoryList.add(pmh);
            }
            
            this.setPropertyMetadataHistoryList(propertyMetadataHistoryList);
        }
    }

    public String getInfoActionCommand() {
        return infoActionCommand;
    }

    public void setInfoActionCommand(String infoActionCommand) {
        this.infoActionCommand = infoActionCommand;
    }
    
    public void setDisplayValueToValue() {
        this.displayValue = value;
    }
    
    public void setTargetValueToValue() { 
        this.targetValue = value; 
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
        if (!(object instanceof PropertyValueHistory)) {
            return false;
        }
        PropertyValueHistory other = (PropertyValueHistory) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cdb.portal.model.db.entities.PropertyValueHistory[ id=" + id + " ]";
    }
    
}
