/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.anl.aps.cdb.portal.constants;

public enum InventoryBillOfMaterialItemStates {
        placeholder("placeholder"),
        existingItem("existingItem"),
        newItem("newItem");

        private String value;

        private InventoryBillOfMaterialItemStates(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    };
