package com.minbar.tafhimulquran.Prayer;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class LocationUtils {
    private static final String PREFS_NAME = "LocationPrefs";
    private static final String KEY_DISTRICT = "selectedDistrict";

    // Mapping of Bengali district names to English for API compatibility
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
        put("জয়পুরহাট", "Jaipurhat");
        put("জামালপুর", "Jamalpur");
        put("যশোর", "Jessore");
        put("ঝালকাঠি", "Jhalokati");
        put("ঝিনাইদহ", "Jhenaidah");
        put("জয়পুরহাট", "Joypurhat");
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
        put("নড়াইল", "Nawabganj");
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

    public static String[] loadLocation(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String district = prefs.getString(KEY_DISTRICT, "ঢাকা");
        return new String[]{district};
    }

    public static void saveLocation(Context context, String district, String subDistrict) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_DISTRICT, district);
        editor.apply();
    }

    public static String[] getDistricts() {
        return DISTRICT_MAPPING.keySet().toArray(new String[0]);
    }

    public static String getApiCityName(String district, String subDistrict) {
        return DISTRICT_MAPPING.getOrDefault(district, district); // Map to English or use district as fallback
    }

//    public static String getDistrictFromCoordinates(Context context, double latitude, double longitude) {
//        // Simplified logic: Return a default district based on approximate coordinates
//        // In a real app, use a geocoder or mapping API (e.g., Google Maps API) for accuracy
//        if (latitude > 23.7 && latitude < 23.9 && longitude > 90.3 && longitude < 90.5) {
//            return "ঢাকা";
//        } else if (latitude > 22.3 && latitude < 22.5 && longitude > 91.8 && longitude < 92.0) {
//            return "চট্টগ্রাম";
//        } else {
//            return "অজানা";
//        }
//    }

    public static String getDistrictFromCoordinates(Context context, double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(context, new Locale("bn", "BD"));
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                String subDistrict = address.getSubLocality() != null ? address.getSubLocality() : address.getLocality();
                return subDistrict != null ? subDistrict : "Unknown Area";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Unknown Area";
    }
}