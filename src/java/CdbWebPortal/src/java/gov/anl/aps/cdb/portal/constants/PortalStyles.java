/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.anl.aps.cdb.portal.constants;

public enum PortalStyles {
    favoritesOn("favoriteOn"),
    favoritesOff("favoriteOff");

    private String value;

    private PortalStyles(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
};
