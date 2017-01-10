package org.recap.model.search;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Created by rajesh on 18-Jul-16.
 */
@ApiModel(value="SearchItemResultRow", description="Model for Displaying Item Result")
public class SearchItemResultRow implements Comparable<SearchItemResultRow> {

    @ApiModelProperty(name= "callNumber", value= "Call Number",position = 0)
    private String callNumber;
    @ApiModelProperty(name= "chronologyAndEnum", value= "Chronology And Enum",position = 1)
    private String chronologyAndEnum;
    @ApiModelProperty(name= "customerCode", value= "Customer Code",position = 2)
    private String customerCode;
    @ApiModelProperty(name= "barcode", value= "barcode",position = 3)
    private String barcode;
    @ApiModelProperty(name= "useRestriction", value= "use Restriction",position = 4)
    private String useRestriction;
    @ApiModelProperty(name= "collectionGroupDesignation", value= "collection Group Designation",position = 5)
    private String collectionGroupDesignation;
    @ApiModelProperty(name= "availability", value= "Availability",position = 6)
    private String availability;
    @ApiModelProperty(name= "selectedItem", value= "selected Item",position = 7)
    private boolean selectedItem = false;

    public String getCallNumber() {
        return callNumber;
    }

    public void setCallNumber(String callNumber) {
        this.callNumber = callNumber;
    }

    public String getChronologyAndEnum() {
        return chronologyAndEnum;
    }

    public void setChronologyAndEnum(String chronologyAndEnum) {
        this.chronologyAndEnum = chronologyAndEnum;
    }

    public String getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getUseRestriction() {
        return useRestriction;
    }

    public void setUseRestriction(String useRestriction) {
        this.useRestriction = useRestriction;
    }

    public String getCollectionGroupDesignation() {
        return collectionGroupDesignation;
    }

    public void setCollectionGroupDesignation(String collectionGroupDesignation) {
        this.collectionGroupDesignation = collectionGroupDesignation;
    }

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }

    public boolean isSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(boolean selectedItem) {
        this.selectedItem = selectedItem;
    }

    @Override
    public int compareTo(SearchItemResultRow searchItemResultRow) {
        return this.getChronologyAndEnum().compareTo(searchItemResultRow.getChronologyAndEnum());
    }
}
