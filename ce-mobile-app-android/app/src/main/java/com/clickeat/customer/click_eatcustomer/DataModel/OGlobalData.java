package com.clickeat.customer.click_eatcustomer.DataModel;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pivotech on 30/11/17.
 */

public class OGlobalData {

    private ArrayList<OUser> userData;
    private ArrayList<String> cuisineList;
    private ArrayList<String> facilitiesList;
    private ArrayList<String> dietsList;
    private ArrayList<String> collectionList;
    private ArrayList<OFacilities> OFacilitiesList;
    private ArrayList<OSpecialDiet> specialDietsList;
    private ArrayList<OCuisines> oCuisinesList;
    private ArrayList<String> demo;
    private Integer cuisineCount;
    private Integer sliderValue;
    private Integer avgPrice;
    private String userId = "";
    private Boolean isVeg;
    private Boolean isOpen;
    private Boolean isDelivery;
    private Boolean isClubMember;
    private Boolean isMealsDeals;
    private ArrayList<MenuDetailsModel> menuDetailsModels;
    private ArrayList<OTable> oTableList;
    private List<OBookTimeSlot> oBookTimeSlots;
    private List<OBookings> oBookingsList;
    private List<OBookingsforPast> oBookingsforPasts;
    private ArrayList<ORestaurant> restaurantDetails;
    private ArrayList<RestaurantDetailModel> restaurantsData;
    private ArrayList<AllReviews> allReviews;
    private ArrayList<OTableGrid> tableGridData;
    private ArrayList<String> thumblinesPaths;
    private ArrayList<OMealDeals> mealDealsList;
    private ArrayList<OAddressList> addressList;

    public OGlobalData() {
        sliderValue = 5;
        avgPrice = 20;
        cuisineList = new ArrayList<>();
        OFacilitiesList = new ArrayList<>();
        oCuisinesList = new ArrayList<>();
        collectionList = new ArrayList<>();
        specialDietsList = new ArrayList<>();
        facilitiesList = new ArrayList<>();
        dietsList = new ArrayList<>();
        demo = new ArrayList<>();
        userData = new ArrayList<>();
        isVeg = false;
        isOpen = false;
        isDelivery = false;
        isClubMember = false;
        isMealsDeals = false;
        menuDetailsModels = new ArrayList<>();
        oTableList = new ArrayList<>();
        oBookTimeSlots = new ArrayList<>();
        oBookingsList = new ArrayList<>();
        oBookingsforPasts = new ArrayList<>();
        restaurantDetails = new ArrayList<>();
        restaurantsData = new ArrayList<>();
        allReviews = new ArrayList<>();
        tableGridData = new ArrayList<>();
        thumblinesPaths = new ArrayList<>();
        mealDealsList = new ArrayList<>();
        addressList = new ArrayList<>();
    }

    public void addDemo(List<String> classObjs) {
        Log.d("<>cancel-", " add cuisine list in GD ==> " + classObjs.toString());
        demo.clear();
        int length = classObjs.size();
        if (length > 0) {
            for (int i = 0; i < classObjs.size(); i++) {
                demo.add(classObjs.get(i));
            }
            String strInfo = String.format("add cuisine info [SUCCESS]");
            Log.d(getClass().toString(), strInfo);
        }
        return;
    }

    public ArrayList<String> getDemo() {
        return demo;
    }

    public void addImageThumblinePath(List<String> classObjs) {
        Log.d("<>cancel-", " add cuisine list in GD ==> " + classObjs.toString());
        thumblinesPaths.clear();
        int length = classObjs.size();
        if (length > 0) {
            for (int i = 0; i < classObjs.size(); i++) {
                thumblinesPaths.add(classObjs.get(i));
            }
            String strInfo = String.format("add thumblinesPaths info [SUCCESS]");
            Log.d(getClass().toString(), strInfo);
        }
        return;
    }

    public ArrayList<String> getImageThumblinePath() {
        return thumblinesPaths;
    }

    public void addTimeSlotsforBook(List<OBookTimeSlot> classObjs) {
        Log.d("<>cancel-", " add cuisine list in GD ==> " + classObjs.toString());
        oBookTimeSlots.clear();
        int length = classObjs.size();
        if (length > 0) {
            for (int i = 0; i < classObjs.size(); i++) {
                oBookTimeSlots.add(classObjs.get(i));
            }
            String strInfo = String.format("add time slots for booking info [SUCCESS]");
            Log.d(getClass().toString(), strInfo);
        }
        return;
    }

    public List<OBookTimeSlot> getTimeSlotsforBook() {
        return oBookTimeSlots;
    }

    public void addRestaurantsFullData(List<RestaurantDetailModel> classObjs) {
        restaurantsData.clear();
        int length = classObjs.size();
        if (length > 0) {
            for (int i = 0; i < classObjs.size(); i++) {
                restaurantsData.add(classObjs.get(i));
            }
            String strInfo = String.format("add whole details of restaurants info [SUCCESS]");
            Log.d(getClass().toString(), strInfo);
        }
        return;
    }

    public List<RestaurantDetailModel> getRestaurantsFullData() {
        return restaurantsData;
    }

    public void addAddressInList(List<OAddressList> classObjs) {
        addressList.clear();
        int length = classObjs.size();
        if (length > 0) {
            for (int i = 0; i < classObjs.size(); i++) {
                addressList.add(classObjs.get(i));
            }
            String strInfo = String.format("add whole details of address info [SUCCESS]");
            Log.d(getClass().toString(), strInfo);
        }
        return;
    }

    public List<OAddressList> getAllAddressList() {
        return addressList;
    }


    public void addBookingList(List<OBookings> classObjs) {
        oBookingsList.clear();
        int length = classObjs.size();
        if (length > 0) {
            for (int i = 0; i < classObjs.size(); i++) {
                oBookingsList.add(classObjs.get(i));
            }
            String strInfo = String.format("add  booking info [SUCCESS]");
            Log.d(getClass().toString(), strInfo);
        }
        return;
    }

    public List<OBookings> getBookingsList() {
        return oBookingsList;
    }

    public void addBookingPastList(List<OBookingsforPast> classObjs) {
        oBookingsforPasts.clear();
        int length = classObjs.size();
        if (length > 0) {
            for (int i = 0; i < classObjs.size(); i++) {
                oBookingsforPasts.add(classObjs.get(i));
            }
            String strInfo = String.format("add  booking past info [SUCCESS]");
            Log.d(getClass().toString(), strInfo);
        }
        return;
    }

    public List<OBookingsforPast> getBookingsForPastList() {
        return oBookingsforPasts;
    }

    public void addMealDeals(List<OMealDeals> classObjs) {
        mealDealsList.clear();
        int length = classObjs.size();
        if (length > 0) {
            for (int i = 0; i < classObjs.size(); i++) {
                mealDealsList.add(classObjs.get(i));
            }
            String strInfo = String.format("add  meal deals past info [SUCCESS]");
            Log.d(getClass().toString(), strInfo);
        }
        return;
    }

    public List<OMealDeals> getMealDealList() {
        return mealDealsList;
    }

    public void addCuisineList(List<String> classObjs) {
        Log.d("<>cancel-", " add cuisine list in GD ==> " + classObjs.toString());
        cuisineList.clear();
        int length = classObjs.size();
        if (length > 0) {
            for (int i = 0; i < classObjs.size(); i++) {
                cuisineList.add(classObjs.get(i));
            }
            String strInfo = String.format("add cuisine info [SUCCESS]");
            Log.d(getClass().toString(), strInfo);
        }
        return;
    }

    public ArrayList<String> getCuisineList() {
        return cuisineList;
    }

    public void addFacility(List<String> classObjs) {
        Log.d("<>resta-", " add cuisine list in GD ==> " + classObjs.toString());
        facilitiesList.clear();
        int length = classObjs.size();
        if (length > 0) {
            for (int i = 0; i < classObjs.size(); i++) {
                facilitiesList.add(classObjs.get(i));
            }
            String strInfo = String.format("add facility info [SUCCESS]");
            Log.d(getClass().toString(), strInfo);
        }
        return;
    }

    public ArrayList<String> getFacilitiesList() {
        return facilitiesList;
    }

    public void addDiets(List<String> classObjs) {
        dietsList.clear();
        int length = classObjs.size();
        if (length > 0) {
            for (int i = 0; i < classObjs.size(); i++) {
                dietsList.add(classObjs.get(i));
            }
            String strInfo = String.format("add diet info [SUCCESS]");
            Log.d(getClass().toString(), strInfo);
        }
        return;
    }

    public ArrayList<String> getDietsList() {
        return dietsList;
    }

    public void addCollectionDelivery(List<String> classObjs) {
        collectionList.clear();
        int length = classObjs.size();
        if (length > 0) {
            for (int i = 0; i < classObjs.size(); i++) {
                collectionList.add(classObjs.get(i));
            }
            String strInfo = String.format("add collection and delivery info [SUCCESS]");
            Log.d(getClass().toString(), strInfo);
        }
        return;
    }

    public ArrayList<String> getCollectionDeliveryList() {
        return collectionList;
    }

    public Integer getCuisineCount() {
        return cuisineCount;
    }

    public void setCuisineCount(Integer cuisineCount) {
        this.cuisineCount = cuisineCount;
    }

    public void addFacilitiesList(List<OFacilities> classObjs) {
        Log.d("<>resta-", " add cuisine list in GD ==> " + classObjs.toString());
        OFacilitiesList.clear();
        int length = classObjs.size();
        if (length > 0) {
            for (int i = 0; i < classObjs.size(); i++) {
                OFacilitiesList.add(classObjs.get(i));
            }
            String strInfo = String.format("add facility info [SUCCESS]");
            Log.d(getClass().toString(), strInfo);
        }
        return;
    }

    public ArrayList<OFacilities> getOFacilitiesList() {
        return OFacilitiesList;
    }

    public void addUserDataList(List<OUser> classObjs) {
        userData.clear();
        Log.d("<>like-", " in global data == > "+classObjs.toString());
        int length = classObjs.size();
        if (length > 0) {
            for (int i = 0; i < classObjs.size(); i++) {
                userData.add(classObjs.get(i));
            }
            String strInfo = String.format("add user info [SUCCESS]");
            Log.d(getClass().toString(), strInfo);
        }
        return;
    }

    public ArrayList<OUser> getUserData() {
        return userData;
    }

    public void addRestaurantDataList(ArrayList<ORestaurant> classObjs) {
        restaurantDetails.clear();
        int length = classObjs.size();
        if (length > 0) {
            for (int i = 0; i < classObjs.size(); i++) {
                restaurantDetails.add(classObjs.get(i));
            }
            String strInfo = String.format("add restaurant info [SUCCESS]");
            Log.d(getClass().toString(), strInfo);
        }
        return;
    }

    public ArrayList<ORestaurant> getRestaurantDetails() {
        return restaurantDetails;
    }

    public void addOCuisineList(List<OCuisines> classObjs) {
        Log.d("<>resta-", " add cuisine list in GD ==> " + classObjs.toString());
        oCuisinesList.clear();
        int length = classObjs.size();
        if (length > 0) {
            for (int i = 0; i < classObjs.size(); i++) {
                oCuisinesList.add(classObjs.get(i));
            }
            String strInfo = String.format("add cuisine info [SUCCESS]");
            Log.d(getClass().toString(), strInfo);
        }
        return;
    }

    public ArrayList<OCuisines> getOCuisinesList() {
        return oCuisinesList;
    }

    public Integer getSliderValue() {
        return sliderValue;
    }

    public void setSliderValue(Integer sliderValue) {
        this.sliderValue = sliderValue;
    }

    public Integer getAvgPrice() {
        return avgPrice;
    }

    public void setAvgPrice(Integer avgPrice) {
        this.avgPrice = avgPrice;
    }

    public void addOSpecialDiets(List<OSpecialDiet> classObjs) {
        Log.d("<>resta-", " add cuisine list in GD ==> " + classObjs.toString());
        specialDietsList.clear();
        int length = classObjs.size();
        if (length > 0) {
            for (int i = 0; i < classObjs.size(); i++) {
                specialDietsList.add(classObjs.get(i));
            }
            String strInfo = String.format("add special diet info [SUCCESS]");
            Log.d(getClass().toString(), strInfo);
        }
        return;
    }

    public ArrayList<OSpecialDiet> getSpecialDietsList() {
        return specialDietsList;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Boolean getVeg() {
        return isVeg;
    }

    public void setVeg(Boolean veg) {
        isVeg = veg;
    }

    public Boolean getOpen() {
        return isOpen;
    }

    public void setOpen(Boolean open) {
        isOpen = open;
    }

    public Boolean getDelivery() {
        return isDelivery;
    }

    public void setDelivery(Boolean delivery) {
        isDelivery = delivery;
    }

    public void addMenuDetailsDataList(List<MenuDetailsModel> classObjs) {
        menuDetailsModels.clear();
        int length = classObjs.size();
        if (length > 0) {
            for (int i = 0; i < classObjs.size(); i++) {
                menuDetailsModels.add(classObjs.get(i));
            }
            String strInfo = String.format("add menu details info [SUCCESS]");
            Log.d(getClass().toString(), strInfo);
        }
        return;
    }

    public ArrayList<MenuDetailsModel> getMenuDetailsDataList() {
        return menuDetailsModels;
    }

    public Boolean getClubMember() {
        return isClubMember;
    }

    public void setClubMember(Boolean clubMember) {
        isClubMember = clubMember;
    }

    public Boolean getMealsDeals() {
        return isMealsDeals;
    }

    public void setMealsDeals(Boolean mealsDeals) {
        isMealsDeals = mealsDeals;
    }

    public void addTableDetails(List<OTable> classObjs) {
        oTableList.clear();
        int length = classObjs.size();
        if (length > 0) {
            for (int i = 0; i < classObjs.size(); i++) {
                oTableList.add(classObjs.get(i));
            }
            String strInfo = String.format("add table list info [SUCCESS]");
            Log.d(getClass().toString(), strInfo);
        }
        return;
    }

    public ArrayList<OTable> getoTableList() {
        return oTableList;
    }

    public void addAllReviews(List<AllReviews> classObjs) {
        allReviews.clear();
        int length = classObjs.size();
        if (length > 0) {
            for (int i = 0; i < classObjs.size(); i++) {
                allReviews.add(classObjs.get(i));
            }
            String strInfo = String.format("add all review info [SUCCESS]");
            Log.d(getClass().toString(), strInfo);
        }
        return;
    }

    public ArrayList<AllReviews> getAllReviews() {
        return allReviews;
    }

    public void addTableGridData(List<OTableGrid> classObjs) {
        tableGridData.clear();
        int length = classObjs.size();
        if (length > 0) {
            for (int i = 0; i < classObjs.size(); i++) {
                tableGridData.add(classObjs.get(i));
            }
            String strInfo = String.format("add all table grid info [SUCCESS]");
            Log.d(getClass().toString(), strInfo);
        }
        return;
    }

    public ArrayList<OTableGrid> getAllTableGridData() {
        return tableGridData;
    }
}
