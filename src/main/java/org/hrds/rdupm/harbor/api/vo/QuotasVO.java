package org.hrds.rdupm.harbor.api.vo;

/**
 * Created by wangxiang on 2021/10/10
 */
public class QuotasVO {

    /**
     * Quotas Id
     */
    private Integer id;

    private QuotasProjectVO ref;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public QuotasProjectVO getRef() {
        return ref;
    }

    public void setRef(QuotasProjectVO ref) {
        this.ref = ref;
    }
}
