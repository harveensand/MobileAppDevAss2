package com.example.mobileappdevass2;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ShareLocationView extends ViewModel {
    //hold data for setting and posting updates
    private final MutableLiveData<Location> locationLiveData = new MutableLiveData<>();

    //update values
    public void setLocationData(Location newValue) {
        locationLiveData.setValue(newValue);
    }

    //get data
    public LiveData<Location> getLocationData() {
        return locationLiveData;
    }
}
