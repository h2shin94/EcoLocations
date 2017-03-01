package uk.ac.cam.cl.foxtrot.ecolocations;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class EcoLocationManager {
    public static List<EcoLocation> getEcoLocationsList() {
        return ecoLocationsList;
    }
    private static List<EcoLocation> ecoLocationsList = new ArrayList<>();
    private static final String TAG = "EcoLocationManager";

    public static void loadLocationList(String rawJson) {
        try {
            JSONObject obj = new JSONObject(rawJson);
            loadLocationList(obj.getJSONArray("locations"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void loadLocationList(JSONArray input) {
        ecoLocationsList.clear();
        for (int i = 0; i < input.length(); i++) {
            try {
                JSONObject obj = input.getJSONObject(i);
                EcoLocation loc = new EcoLocation();
                loc.setId(obj.getInt("id"));
                loc.setWDPA_PID(obj.getString("WDPA_PID"));
                loc.setNAME(obj.getString("NAME"));
                loc.setLATITUDE(obj.getDouble("LATITUDE"));
                loc.setLONGITUDE(obj.getDouble("LONGITUDE"));
                loc.setLANDCOVER(obj.optString("LANDCOVER", "No data"));
                loc.setTYPE(obj.getString("TYPE"));
                loc.setDESIG_ENG(obj.getString("DESIG_ENG"));
                loc.setDESIG_TYPE(obj.getString("DESIG_TYPE"));
                loc.setMARINE(obj.getDouble("MARINE"));
                loc.setGIS_M_AREA(obj.getDouble("GIS_M_AREA"));
                loc.setGIS_AREA(obj.getDouble("GIS_AREA"));
                loc.setSTATUS(obj.getString("STATUS"));
                loc.setSTATUS_YR(obj.getInt("STATUS_YR"));
                loc.setGOV_TYPE(obj.getString("GOV_TYPE"));
                loc.setOWN_TYPE(obj.getString("OWN_TYPE"));
                loc.setMANG_AUTH(obj.getString("MANG_AUTH"));
                loc.setSUB_LOC(obj.getString("SUB_LOC"));
                loc.setISO3(obj.getString("ISO3"));
                loc.setPA_DEF(obj.getInt("PA_DEF"));
                loc.setORIG_NAME(obj.getString("ORIG_NAME"));
                loc.setIUCN_CAT(obj.getString("IUCN_CAT"));
                loc.setINT_CRIT(obj.getString("INT_CRIT"));
                loc.setREP_M_AREA(obj.getDouble("REP_M_AREA"));
                loc.setREP_AREA(obj.getDouble("REP_AREA"));
                loc.setNO_TAKE(obj.getString("NO_TAKE"));
                loc.setNO_TK_AREA(obj.getDouble("NO_TK_AREA"));
                loc.setMANG_PLAN(obj.getString("MANG_PLAN"));
                loc.setVERIF(obj.getString("VERIF"));
                loc.setMETADATAID(obj.getInt("METADATAID"));
                loc.setPARENT_ISO3(obj.getString("PARENT_ISO3"));
                loc.setDistance(obj.getDouble("distance"));
                loc.setPIXEL_NUMBER(obj.optInt("PIXEL_NUMBER", -1));
                ecoLocationsList.add(loc);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Log.i(TAG, "loadLocationList: added " + ecoLocationsList.size() + " locations");
    }

    public static int getLocationIndex(EcoLocation loc) {
        return ecoLocationsList.indexOf(loc);
    }

    public static EcoLocation getLocationById(String wdpa_id) {
        for (EcoLocation loc : ecoLocationsList) {
            if (loc.getWDPA_PID().equals(wdpa_id)) {
                return loc;
            }
        }
        return null;
    }

    public static JSONObject getGeoJson(String rawJson) throws JSONException {
        JSONObject obj = new JSONObject(rawJson);
        return obj.getJSONObject("protected_area").getJSONObject("geojson");
    }
}
