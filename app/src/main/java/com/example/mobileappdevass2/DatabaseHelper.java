package com.example.mobileappdevass2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Address;
import android.location.Geocoder;

import androidx.annotation.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "custom_location.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME = "custom_locations";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_ADDRESS = "address";
    private static final String COLUMN_LATITUDE = "latitude";
    private static final String COLUMN_LONGITUDE = "longitude";

    private Context context;

    //constructor
    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    //create database
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableStatement = "CREATE TABLE " + TABLE_NAME + " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_ADDRESS + " TEXT, " + COLUMN_LATITUDE + " TEXT, " + COLUMN_LONGITUDE + " TEXT)";
        db.execSQL(createTableStatement);
        //add mockadata
        addMockData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Handle database upgrades if needed
    }

    //add locations
    public boolean addLocation(Location location) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = getLocationContentValues(location);

        long insert = db.insert(TABLE_NAME, null, cv);
        db.close();
        return insert != -1;
    }

    //update locations
    public boolean updateLocation(Location location) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = getLocationContentValues(location);

        int update = db.update(TABLE_NAME, cv, COLUMN_ID + "=?", new String[]{String.valueOf(location.getId())});
        db.close();
        return update != -1;
    }

    //delete locations
    public boolean deleteLocation(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int delete = db.delete(TABLE_NAME, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
        return delete != -1;
    }

    //get all locations
    public List<Location> getAllLocations() {
        List<Location> returnList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                Location newLocation = getLocationFromCursor(cursor);
                returnList.add(newLocation);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return returnList;
    }

    //get content from locations
    private ContentValues getLocationContentValues(Location location) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_ADDRESS, location.getAddress());
        cv.put(COLUMN_LATITUDE, location.getLatitude());
        cv.put(COLUMN_LONGITUDE, location.getLongitude());
        return cv;
    }

    //get location from cursor
    //although there are errors, based on my experince with lab3, this will cause no issues since values should never be -1
    private Location getLocationFromCursor(Cursor cursor) {
        int locationID = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
        String locationAddress = cursor.getString(cursor.getColumnIndex(COLUMN_ADDRESS));
        String locationLatitude = cursor.getString(cursor.getColumnIndex(COLUMN_LATITUDE));
        String locationLongitude = cursor.getString(cursor.getColumnIndex(COLUMN_LONGITUDE));

        return new Location(locationID, locationAddress, locationLatitude, locationLongitude);
    }

    //add mockdata to database
    private void addMockData(SQLiteDatabase db) {
        List<Location> mockLocations = getMockDataFromTextFile();
        for (Location location : mockLocations) {
            ContentValues cv = getLocationContentValues(location);
            db.insert(TABLE_NAME, null, cv);
        }
    }

    //read mockdata from textfile
    private List<Location> getMockDataFromTextFile() {
        List<Location> locationList = new ArrayList<>();

        try (InputStream is = context.getAssets().open("locations.txt");
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {

            String line;
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split("\\s+"); // Split by space or tab

                if (tokens.length >= 2) {
                    String latitude = tokens[0];
                    String longitude = tokens[1];
                    String address = getAddress(latitude, longitude);

                    Location newLocation = new Location();
                    newLocation.setLatitude(latitude);
                    newLocation.setLongitude(longitude);
                    newLocation.setAddress(address);

                    locationList.add(newLocation);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return locationList;
    }

    //get an address from latitude and longitude using Geocoder
    private String getAddress(String latitude, String longitude) {
        String address = "error";

        if (Geocoder.isPresent()) {
            Geocoder geocoder = new Geocoder(this.context, Locale.getDefault());

            try {
                List<Address> addresses = geocoder.getFromLocation(Double.parseDouble(latitude), Double.parseDouble(longitude), 1);
                if (!addresses.isEmpty()) {
                    address = addresses.get(0).getAddressLine(0);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return address;
    }
}
