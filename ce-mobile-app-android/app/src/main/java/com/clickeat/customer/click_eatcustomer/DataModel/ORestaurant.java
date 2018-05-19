package com.clickeat.customer.click_eatcustomer.DataModel;

import android.widget.ListView;

import java.util.HashMap;
import java.util.List;

/**
 * Created by pivotech on 28/10/17.
 */

public class ORestaurant {

    private String _id;
    private String restaurant_id;
    private String title;
    private String distance;
    private String image;
    private String address;
    private String latitude;
    private String longitude;
    private String hygieneRating;
    private List<String> cuisines;
    private HashMap<String, String> facilities;
    private HashMap<String, List<String>> timeTable;
    private HashMap<String, Boolean> isOpenDay;
    private Boolean collection;
    private Boolean delivery;
    private Integer collectionDiscount;
    private Integer averageCollectionTime;
    private Double deliveryCharge;
    private Double deliveryDistance;
    private Integer averageDeliveryTime;
    private List<String> likedBy;
    private String aboutRestaurant;
    private List<String> imagesList;
    private HashMap<Integer, String> additionalService;

    public ORestaurant(String _id, String restaurant_id, String title, String aboutRestaurant, String distance, String image, String address,
                       String longitude, String latitude, String hygieneRating, List<String> cuisines,
                       HashMap<String, String> facilities, HashMap<String, List<String>> timeTable,
                       HashMap<String, Boolean> isOpenDay, Boolean collection, Boolean delivery, Integer collectionDiscount,
                       Integer averageCollectionTime, Double deliveryCharge, Double deliveryDistance,
                       Integer averageDeliveryTime, List<String> likedBy, List<String> imagesList,
                       HashMap<Integer, String> additionalService) {
        this._id = _id;
        this.restaurant_id = restaurant_id;
        this.title = title;
        this.aboutRestaurant = aboutRestaurant;
        this.distance = distance;
        this.image = image;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.hygieneRating = hygieneRating;
        this.cuisines = cuisines;
        this.facilities = facilities;
        this.timeTable = timeTable;
        this.isOpenDay = isOpenDay;
        this.collection = collection;
        this.delivery = delivery;
        this.collectionDiscount = collectionDiscount;
        this.averageCollectionTime = averageCollectionTime;
        this.deliveryCharge = deliveryCharge;
        this.deliveryDistance = deliveryDistance;
        this.averageDeliveryTime = averageDeliveryTime;
        this.likedBy = likedBy;
        this.imagesList = imagesList;
        this.additionalService = additionalService;
    }

    public String getId() {
        return _id;
    }

    public void setId(String _id) {
        this._id = _id;
    }

    public String getRestaurant_id() {
        return restaurant_id;
    }

    public void setRestaurant_id(String restaurant_id) {
        this.restaurant_id = restaurant_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public List<String> getCuisines() {
        return cuisines;
    }

    public String getHygieneRating() {
        return hygieneRating;
    }

    public void setHygieneRating(String hygieneRating) {
        this.hygieneRating = hygieneRating;
    }

    public void setCuisines(List<String> cuisines) {
        this.cuisines = cuisines;
    }

    public HashMap<String, String> getFacilities() {
        return facilities;
    }

    public void setFacilities(HashMap<String, String> facilities) {
        this.facilities = facilities;
    }

    public HashMap<String, List<String>> getTimeTable() {
        return timeTable;
    }

    public void setTimeTable(HashMap<String, List<String>> timeTable) {
        this.timeTable = timeTable;
    }

    public Boolean getCollection() {
        return collection;
    }

    public void setCollection(Boolean collection) {
        this.collection = collection;
    }

    public Boolean getDelivery() {
        return delivery;
    }

    public void setDelivery(Boolean delivery) {
        this.delivery = delivery;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public Integer getCollectionDiscount() {
        return collectionDiscount;
    }

    public void setCollectionDiscount(Integer collectionDiscount) {
        this.collectionDiscount = collectionDiscount;
    }

    public Integer getAverageCollectionTime() {
        return averageCollectionTime;
    }

    public void setAverageCollectionTime(Integer averageCollectionTime) {
        this.averageCollectionTime = averageCollectionTime;
    }

    public Double getDeliveryCharge() {
        return deliveryCharge;
    }

    public void setDeliveryCharge(Double deliveryCharge) {
        this.deliveryCharge = deliveryCharge;
    }

    public Double getDeliveryDistance() {
        return deliveryDistance;
    }

    public void setDeliveryDistance(Double deliveryDistance) {
        this.deliveryDistance = deliveryDistance;
    }

    public Integer getAverageDeliveryTime() {
        return averageDeliveryTime;
    }

    public void setAverageDeliveryTime(Integer averageDeliveryTime) {
        this.averageDeliveryTime = averageDeliveryTime;
    }

    public List<String> getLikedBy() {
        return likedBy;
    }

    public void setLikedBy(List<String> likedBy) {
        this.likedBy = likedBy;
    }

    public HashMap<String, Boolean> getIsOpenDay() {
        return isOpenDay;
    }

    public void setIsOpenDay(HashMap<String, Boolean> isOpenDay) {
        this.isOpenDay = isOpenDay;
    }

    public String getAboutRestaurant() {
        return aboutRestaurant;
    }

    public void setAboutRestaurant(String aboutRestaurant) {
        this.aboutRestaurant = aboutRestaurant;
    }

    public List<String> getImagesList() {
        return imagesList;
    }

    public void setImagesList(List<String> imagesList) {
        this.imagesList = imagesList;
    }

    public HashMap<Integer, String> getAdditionalService() {
        return additionalService;
    }

    public void setAdditionalService(HashMap<Integer, String> additionalService) {
        this.additionalService = additionalService;
    }
}
