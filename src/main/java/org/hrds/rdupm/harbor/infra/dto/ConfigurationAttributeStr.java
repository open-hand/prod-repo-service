package org.hrds.rdupm.harbor.infra.dto;

/**
 * description
 *
 * @author mofei.li@hand-china.com 2020/03/24 21:05
 */
public class ConfigurationAttributeStr {
    private String value;
    private Boolean editable;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Boolean getEditable() {
        return editable;
    }

    public void setEditable(Boolean editable) {
        this.editable = editable;
    }
}
