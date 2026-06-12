package com.minbar.tafhimulquran.Prayer;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class LocationUtils {
    private static final String PREFS_NAME = "LocationPrefs";
    private static final String KEY_DISTRICT = "selectedDistrict";
    private static final String KEY_LAT = "latitude";
    private static final String KEY_LON = "longitude";
    private static final String KEY_USE_COORDINATES = "use_coordinates";

    public static final String[] BANGLADESH_DISTRICTS = {
            "বাগেরহাট", "বান্দরবান", "বরগুনা", "বরিশাল", "ভোলা", "বগুড়া", "ব্রাহ্মণবাড়িয়া", "চাঁদপুর",
            "চট্টগ্রাম", "চুয়াডাঙ্গা", "কুমিল্লা", "কক্স বাজার", "ঢাকা", "দিনাজপুর", "ফরিদপুর", "ফেনী",
            "গাইবান্ধা", "গাজীপুর", "গোপালগঞ্জ", "হবিগঞ্জ", "জয়পুরহাট", "জামালপুর", "যশোর", "ঝালকাঠি",
            "ঝিনাইদহ", "খাগড়াছড়ি", "খুলনা", "কিশোরগঞ্জ", "কুড়িগ্রাম", "কুষ্টিয়া", "লক্ষ্মীপুর",
            "লালমনিরহাট", "মাদারীপুর", "মাগুরা", "মানিকগঞ্জ", "মেহেরপুর", "মৌলভীবাজার", "মুন্সিগঞ্জ",
            "ময়মনসিংহ", "নওগাঁ", "নড়াইল", "নারায়ণগঞ্জ", "নরসিংদি", "নাটোর", "চাঁপাইনবাবগঞ্জ",
            "নেত্রকোণা", "নীলফামারী", "নোয়াখালী", "পাবনা", "পঞ্চগড়", "পটুয়াখালী", "পিরোজপুর",
            "রাজবাড়ী", "রাজশাহী", "রাঙ্গামাটি", "রংপুর", "সাতক্ষীরা", "শরীয়তপুর", "শেরপুর",
            "সিরাজগঞ্জ", "সুনামগঞ্জ", "সিলেট", "টাঙ্গাইল", "ঠাকুরগাঁও"
    };

    private static final String[] COMMON_COUNTRIES = {
            "Saudi Arabia", "UAE", "Kuwait", "Qatar", "Bahrain", "Oman",
            "Pakistan", "India", "Turkey", "Egypt", "UK", "USA", "Canada",
            "Australia", "Germany", "France", "Malaysia", "Indonesia"
    };

    private static final Map<String, String> DISTRICT_MAPPING = new HashMap<String, String>() {{
        put("বাগেরহাট", "Bagerhat");
        put("বান্দরবান", "Bandarban");
        put("বরগুনা", "Barguna");
        put("বরিশাল", "Barisal");
        put("ভোলা", "Bhola");
        put("বগুড়া", "Bogra");
        put("ব্রাহ্মণবাড়িয়া", "Brahmanbaria");
        put("চাঁদপুর", "Chandpur");
        put("চট্টগ্রাম", "Chittagong");
        put("চুয়াডাঙ্গা", "Chuadanga");
        put("কুমিল্লা", "Comilla");
        put("কক্স বাজার", "Cox's Bazar");
        put("ঢাকা", "Dhaka");
        put("দিনাজপুর", "Dinajpur");
        put("ফরিদপুর", "Faridpur");
        put("ফেনী", "Feni");
        put("গাইবান্ধা", "Gaibandha");
        put("গাজীপুর", "Gazipur");
        put("গোপালগঞ্জ", "Gopalganj");
        put("হবিগঞ্জ", "Habiganj");
        put("জয়পুরহাট", "Joypurhat");
        put("জামালপুর", "Jamalpur");
        put("যশোর", "Jessore");
        put("ঝালকাঠি", "Jhalokati");
        put("ঝিনাইদহ", "Jhenaidah");
        put("খাগড়াছড়ি", "Khagrachhari");
        put("খুলনা", "Khulna");
        put("কিশোরগঞ্জ", "Kishoreganj");
        put("কুড়িগ্রাম", "Kurigram");
        put("কুষ্টিয়া", "Kushtia");
        put("লক্ষ্মীপুর", "Lakshmipur");
        put("লালমনিরহাট", "Lalmonirhat");
        put("মাদারীপুর", "Madaripur");
        put("মাগুরা", "Magura");
        put("মানিকগঞ্জ", "Manikganj");
        put("মেহেরপুর", "Meherpur");
        put("মৌলভীবাজার", "Moulvibazar");
        put("মুন্সিগঞ্জ", "Munshiganj");
        put("ময়মনসিংহ", "Mymensingh");
        put("নওগাঁ", "Naogaon");
        put("নড়াইল", "Narail");
        put("নারায়ণগঞ্জ", "Narayanganj");
        put("নরসিংদি", "Narsingdi");
        put("নাটোর", "Natore");
        put("চাঁপাইনবাবগঞ্জ", "Nawabganj");
        put("নেত্রকোণা", "Netrakona");
        put("নীলফামারী", "Nilphamari");
        put("নোয়াখালী", "Noakhali");
        put("পাবনা", "Pabna");
        put("পঞ্চগড়", "Panchagarh");
        put("পটুয়াখালী", "Patuakhali");
        put("পিরোজপুর", "Pirojpur");
        put("রাজবাড়ী", "Rajbari");
        put("রাজশাহী", "Rajshahi");
        put("রাঙ্গামাটি", "Rangamati");
        put("রংপুর", "Rangpur");
        put("সাতক্ষীরা", "Satkhira");
        put("শরীয়তপুর", "Shariatpur");
        put("শেরপুর", "Sherpur");
        put("সিরাজগঞ্জ", "Sirajganj");
        put("সুনামগঞ্জ", "Sunamganj");
        put("সিলেট", "Sylhet");
        put("টাঙ্গাইল", "Tangail");
        put("ঠাকুরগাঁও", "Thakurgaon");
    }};

    static {
        Arrays.sort(BANGLADESH_DISTRICTS);
    }

    public static String[] loadLocation(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String district = prefs.getString(KEY_DISTRICT, "ঢাকা");
        String country = prefs.getString("country", "Bangladesh");
        return new String[]{district, country};
    }

    public static String[] getAllCountries() {
        return COMMON_COUNTRIES;
    }

    public static void saveLocation(Context context, String district, String subDistrict) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_DISTRICT, district);
        editor.putString("country", "Bangladesh"); // Default to Bangladesh
        editor.putBoolean(KEY_USE_COORDINATES, false);
        editor.apply();
    }

    public static void saveInternationalLocation(Context context, String district, String country) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_DISTRICT, district);
        editor.putString("country", country);
        editor.putBoolean(KEY_USE_COORDINATES, false);
        editor.apply();
    }

    public static void saveCoordinates(Context context, double lat, double lon, String districtName) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putFloat(KEY_LAT, (float) lat);
        editor.putFloat(KEY_LON, (float) lon);
        editor.putString(KEY_DISTRICT, districtName);
        editor.putBoolean(KEY_USE_COORDINATES, true);
        editor.apply();
    }

    public static boolean shouldUseCoordinates(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .getBoolean(KEY_USE_COORDINATES, false);
    }

    public static boolean isUsingCoordinates(Context context) {
        return shouldUseCoordinates(context);
    }

    public static double[] getSavedCoordinates(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return new double[]{
                prefs.getFloat(KEY_LAT, 0.0f),
                prefs.getFloat(KEY_LON, 0.0f)
        };
    }

    public static double getSavedLatitude(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .getFloat(KEY_LAT, 0.0f);
    }

    public static double getSavedLongitude(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .getFloat(KEY_LON, 0.0f);
    }

    public static String getSavedDistrict(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .getString(KEY_DISTRICT, "ঢাকা");
    }

    public static String getApiCityName(String district, String subDistrict) {
        return DISTRICT_MAPPING.getOrDefault(district, district);
    }

    public static boolean isInternationalLocation(Context context) {
        String[] location = loadLocation(context);
        String country = location.length > 1 ? location[1] : "Bangladesh";
        return !"Bangladesh".equals(country);
    }

    public static String getDistrictFromCoordinates(Context context, double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(context, new Locale("bn", "BD"));
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                String district = address.getSubAdminArea();
                if (district == null) district = address.getLocality();
                
                if (district != null) {
                    for (String key : BANGLADESH_DISTRICTS) {
                        if (district.contains(key) || key.contains(district)) {
                            return key;
                        }
                    }
                }
                return district != null ? district : "ঢাকা";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "ঢাকা";
    }
}
