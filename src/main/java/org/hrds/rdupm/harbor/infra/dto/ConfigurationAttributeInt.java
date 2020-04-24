package org.hrds.rdupm.harbor.infra.dto;

/**
 * description
 *
 * @author mofei.li@hand-china.com 2020/03/24 21:11
 */
public class ConfigurationAttributeInt {
    private Integer value;
    private Boolean editable;

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public Boolean getEditable() {
        return editable;
    }

    public void setEditable(Boolean editable) {
        this.editable = editable;
    }

}
