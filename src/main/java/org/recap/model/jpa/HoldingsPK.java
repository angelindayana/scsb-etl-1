package org.recap.model.jpa;

import java.io.Serializable;

/**
 * Created by rajeshbabuk on 15/9/16.
 */
public class HoldingsPK implements Serializable {
    private Integer owningInstitutionId;
    private String owningInstitutionHoldingsId;

    public HoldingsPK() {

    }

    public HoldingsPK(Integer owningInstitutionId, String owningInstitutionHoldingsId) {
        this.owningInstitutionId = owningInstitutionId;
        this.owningInstitutionHoldingsId = owningInstitutionHoldingsId;
    }

    public Integer getOwningInstitutionId() {
        return owningInstitutionId;
    }

    public void setOwningInstitutionId(Integer owningInstitutionId) {
        this.owningInstitutionId = owningInstitutionId;
    }

    public String getOwningInstitutionHoldingsId() {
        return owningInstitutionHoldingsId;
    }

    public void setOwningInstitutionHoldingsId(String owningInstitutionHoldingsId) {
        this.owningInstitutionHoldingsId = owningInstitutionHoldingsId;
    }

    @Override
    public int hashCode() {
        return Integer.valueOf(owningInstitutionId.toString() + owningInstitutionHoldingsId);
    }

    @Override
    public boolean equals(Object obj) {
        HoldingsPK holdingsPK = (HoldingsPK) obj;
        if (holdingsPK.getOwningInstitutionId().equals(owningInstitutionId) && holdingsPK.getOwningInstitutionHoldingsId().equals(owningInstitutionHoldingsId)) {
            return true;
        }

        return false;
    }
}

