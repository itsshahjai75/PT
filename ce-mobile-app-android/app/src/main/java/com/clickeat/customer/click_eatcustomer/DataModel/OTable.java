package com.clickeat.customer.click_eatcustomer.DataModel;

/**
 * Created by pivotech on 25/2/18.
 */

public class OTable {
    protected Integer tableNum;
    protected Integer tableCapacity;
    protected String tableId;
    protected Integer roomNo;
    protected Integer floorNo;

    public OTable(Integer tableNum, Integer tableCapacity, String tableId, Integer roomNo, Integer floorNo) {
        this.tableNum = tableNum;
        this.tableCapacity = tableCapacity;
        this.tableId = tableId;
        this.roomNo = roomNo;
        this.floorNo = floorNo;
    }

    public Integer getTableNum() {
        return tableNum;
    }

    public void setTableNum(Integer tableNum) {
        this.tableNum = tableNum;
    }

    public Integer getTableCapacity() {
        return tableCapacity;
    }

    public void setTableCapacity(Integer tableCapacity) {
        this.tableCapacity = tableCapacity;
    }

    public String getTableId() {
        return tableId;
    }

    public void setTableId(String tableId) {
        this.tableId = tableId;
    }

    public Integer getRoomNo() {
        return roomNo;
    }

    public void setRoomNo(Integer roomNo) {
        this.roomNo = roomNo;
    }

    public Integer getFloorNo() {
        return floorNo;
    }

    public void setFloorNo(Integer floorNo) {
        this.floorNo = floorNo;
    }
}
