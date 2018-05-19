package com.clickeat.restaurant.click_eatrestaurant.DataModel;

import java.util.List;

/**
 * Created by pivotech on 21/3/18.
 */

public class OTableGrid {
    private Integer tableNum;
    private Integer tableCapacity;
    private String tableId;
    private Integer roomNo;
    private Integer floorNo;
    private List<TimeSlots> listTimeslots;


    public OTableGrid(Integer tableNum, Integer tableCapacity, String tableId, Integer roomNo, Integer floorNo, List<TimeSlots> listTimeslots) {
        this.tableNum = tableNum;
        this.tableCapacity = tableCapacity;
        this.tableId = tableId;
        this.roomNo = roomNo;
        this.floorNo = floorNo;
        this.listTimeslots = listTimeslots;
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

    public List<TimeSlots> getListTimeslots() {
        return listTimeslots;
    }

    public void setListTimeslots(List<TimeSlots> listTimeslots) {
        this.listTimeslots = listTimeslots;
    }
}
