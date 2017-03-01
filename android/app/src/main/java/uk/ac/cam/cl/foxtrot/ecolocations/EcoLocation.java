package uk.ac.cam.cl.foxtrot.ecolocations;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.data.geojson.GeoJsonLayer;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class EcoLocation implements Serializable {
    private int id;
    private String WDPA_PID;
    private String NAME;
    private double LATITUDE;
    private double LONGITUDE;
    private String LANDCOVER;
    private String TYPE;
    private String DESIG_ENG;
    private String DESIG_TYPE;
    private double MARINE;
    private double GIS_M_AREA;
    private double GIS_AREA;
    private String STATUS;
    private int STATUS_YR;
    private String GOV_TYPE;
    private String OWN_TYPE;
    private String MANG_AUTH;
    private String SUB_LOC;
    private String ISO3;

    private int PA_DEF;
    private String ORIG_NAME;
    private String IUCN_CAT;
    private String INT_CRIT;
    private double REP_M_AREA;
    private double REP_AREA;
    private String NO_TAKE;
    private double NO_TK_AREA;
    private int PIXEL_NUMBER;

    public int getPA_DEF() {
        return PA_DEF;
    }

    public void setPA_DEF(int PA_DEF) {
        this.PA_DEF = PA_DEF;
    }

    public String getORIG_NAME() {
        return ORIG_NAME;
    }

    public void setORIG_NAME(String ORIG_NAME) {
        this.ORIG_NAME = ORIG_NAME;
    }

    public String getIUCN_CAT() {
        return IUCN_CAT;
    }

    public void setIUCN_CAT(String IUCN_CAT) {
        this.IUCN_CAT = IUCN_CAT;
    }

    public String getINT_CRIT() {
        return INT_CRIT;
    }

    public void setINT_CRIT(String INT_CRIT) {
        this.INT_CRIT = INT_CRIT;
    }

    public double getREP_M_AREA() {
        return REP_M_AREA;
    }

    public void setREP_M_AREA(double REP_M_AREA) {
        this.REP_M_AREA = REP_M_AREA;
    }

    public double getREP_AREA() {
        return REP_AREA;
    }

    public void setREP_AREA(double REP_AREA) {
        this.REP_AREA = REP_AREA;
    }

    public String getNO_TAKE() {
        return NO_TAKE;
    }

    public void setNO_TAKE(String NO_TAKE) {
        this.NO_TAKE = NO_TAKE;
    }

    public double getNO_TK_AREA() {
        return NO_TK_AREA;
    }

    public void setNO_TK_AREA(double NO_TK_AREA) {
        this.NO_TK_AREA = NO_TK_AREA;
    }

    public String getMANG_PLAN() {
        return MANG_PLAN;
    }

    public void setMANG_PLAN(String MANG_PLAN) {
        this.MANG_PLAN = MANG_PLAN;
    }

    public String getVERIF() {
        return VERIF;
    }

    public void setVERIF(String VERIF) {
        this.VERIF = VERIF;
    }

    public int getMETADATAID() {
        return METADATAID;
    }

    public void setMETADATAID(int METADATAID) {
        this.METADATAID = METADATAID;
    }

    public String getPARENT_ISO3() {
        return PARENT_ISO3;
    }

    public void setPARENT_ISO3(String PARENT_ISO3) {
        this.PARENT_ISO3 = PARENT_ISO3;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    private String MANG_PLAN;
    private String VERIF;
    private int METADATAID;
    private String PARENT_ISO3;
    private double distance;

    private transient Marker marker;

    public LatLng getCoordinates() {
        return new LatLng(LATITUDE, LONGITUDE);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWDPA_PID() {
        return WDPA_PID;
    }

    public void setWDPA_PID(String WDPA_PID) {
        this.WDPA_PID = WDPA_PID;
    }

    public String getNAME() {
        return NAME;
    }

    public void setNAME(String NAME) {
        this.NAME = NAME;
    }

    public double getLATITUDE() {
        return LATITUDE;
    }

    public void setLATITUDE(double LATITUDE) {
        this.LATITUDE = LATITUDE;
    }

    public double getLONGITUDE() {
        return LONGITUDE;
    }

    public void setLONGITUDE(double LONGITUDE) {
        this.LONGITUDE = LONGITUDE;
    }

    public String getLANDCOVER() {
        return LANDCOVER == null ? "100% Urban" : LANDCOVER;
    }

    public void setLANDCOVER(String LANDCOVER) {
        this.LANDCOVER = LANDCOVER;
    }

    public String getTYPE() {
        return TYPE;
    }

    public void setTYPE(String TYPE) {
        this.TYPE = TYPE;
    }

    public String getDESIG_ENG() {
        return DESIG_ENG;
    }

    public void setDESIG_ENG(String DESIG_ENG) {
        this.DESIG_ENG = DESIG_ENG;
    }

    public String getDESIG_TYPE() {
        return DESIG_TYPE;
    }

    public void setDESIG_TYPE(String DESIG_TYPE) {
        this.DESIG_TYPE = DESIG_TYPE;
    }

    public double getMARINE() {
        return MARINE;
    }

    public void setMARINE(double MARINE) {
        this.MARINE = MARINE;
    }

    public double getGIS_M_AREA() {
        return GIS_M_AREA;
    }

    public void setGIS_M_AREA(double GIS_M_AREA) {
        this.GIS_M_AREA = GIS_M_AREA;
    }

    public double getGIS_AREA() {
        return GIS_AREA;
    }

    public void setGIS_AREA(double GIS_AREA) {
        this.GIS_AREA = GIS_AREA;
    }

    public String getSTATUS() {
        return STATUS;
    }

    public void setSTATUS(String STATUS) {
        this.STATUS = STATUS;
    }

    public int getSTATUS_YR() {
        return STATUS_YR;
    }

    public void setSTATUS_YR(int STATUS_YR) {
        this.STATUS_YR = STATUS_YR;
    }

    public String getGOV_TYPE() {
        return GOV_TYPE;
    }

    public void setGOV_TYPE(String GOV_TYPE) {
        this.GOV_TYPE = GOV_TYPE;
    }

    public String getOWN_TYPE() {
        return OWN_TYPE;
    }

    public void setOWN_TYPE(String OWN_TYPE) {
        this.OWN_TYPE = OWN_TYPE;
    }

    public String getMANG_AUTH() {
        return MANG_AUTH;
    }

    public void setMANG_AUTH(String MANG_AUTH) {
        this.MANG_AUTH = MANG_AUTH;
    }

    public String getSUB_LOC() {
        return SUB_LOC;
    }

    public void setSUB_LOC(String SUB_LOC) {
        this.SUB_LOC = SUB_LOC;
    }

    public String getISO3() {
        return ISO3;
    }

    public void setISO3(String ISO3) {
        this.ISO3 = ISO3;
    }

    public Marker getMarker() {
        return marker;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }

    public int getPIXEL_NUMBER() {
        return PIXEL_NUMBER;
    }

    public void setPIXEL_NUMBER(int PIXEL_NUMBER) {
        this.PIXEL_NUMBER = PIXEL_NUMBER;
    }
}