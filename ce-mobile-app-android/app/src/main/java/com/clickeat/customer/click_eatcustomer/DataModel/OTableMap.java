package com.clickeat.customer.click_eatcustomer.DataModel;

/**
 * Created by pivotech on 28/2/18.
 */

public class OTableMap {
    protected String floorName;
    protected String roomName;
    protected String images;

    public OTableMap(String floorName, String roomName, String images) {
        this.floorName = floorName;
        this.roomName = roomName;
        this.images = images;
    }

    public String getFloorName() {
        return floorName;
    }

    public void setFloorName(String floorName) {
        this.floorName = floorName;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }
}
