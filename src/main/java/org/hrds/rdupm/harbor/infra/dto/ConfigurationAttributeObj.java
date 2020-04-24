package org.hrds.rdupm.harbor.infra.dto;

/**
 * description
 *
 * @author mofei.li@hand-china.com 2020/03/24 21:39
 */
public class ConfigurationAttributeObj {
    private Object value;
    private Boolean editable;

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Boolean getEditable() {
        return editable;
    }

    public void setEditable(Boolean editable) {
        this.editable = editable;
    }
}
