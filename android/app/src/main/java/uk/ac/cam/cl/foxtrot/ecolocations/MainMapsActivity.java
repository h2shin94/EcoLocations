package uk.ac.cam.cl.foxtrot.ecolocations;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.text.TextUtilsCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.data.Feature;
import com.google.maps.android.data.Layer;
import com.google.maps.android.data.geojson.GeoJsonFeature;
import com.google.maps.android.data.geojson.GeoJsonLayer;
import com.google.maps.android.data.geojson.GeoJsonLineStringStyle;
import com.google.maps.android.data.geojson.GeoJsonPointStyle;
import com.google.maps.android.data.geojson.GeoJsonPolygonStyle;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.json.JSONException;

import java.util.Collection;
import java.util.HashMap;

public class MainMapsActivity extends FragmentActivity
        implements OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnInfoWindowClickListener,
        GoogleMap.OnMyLocationChangeListener {

    public static final String ECOLOCATION_URL = "http://ecolocation.ukwest.cloudapp.azure.com/getlocations.php?latitude=%f&longitude=%f";
    public static final String WDPA_ID = "wdpa_id";
    private GoogleMap mMap;
    private CoordinatorLayout mMapLayout;
    private SlidingUpPanelLayout mLayout;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private TextView mRefreshText;
    private ProgressBar mProgressBar;
    private FloatingActionButton mFab;
    private static final String REQUEST_SERVER_TAG = "RequestServerTag";
    private static final String REQUEST_PROTECTED_TAG = "RequestProtectedTag";
    private RequestQueue mRequestQueue;
    private Marker mActiveMarker;
    private LatLng mCurrentLocation;
    private HashMap<GeoJsonFeature, EcoLocation> mFeatureMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mMapLayout = (CoordinatorLayout) findViewById(R.id.map_layout);
        mRefreshText = (TextView) findViewById(R.id.refresh_text);
        mProgressBar = (ProgressBar) findViewById(R.id.spinner);
        mLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        mLayout.setTouchEnabled(false);
        mMapLayout = (CoordinatorLayout) findViewById(R.id.map_layout);

        mFeatureMap = new HashMap<>();


        mRecyclerView = (RecyclerView) findViewById(R.id.location_list);
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(mRecyclerView);
        mAdapter = new EcoLocationsAdapter();
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (mActiveMarker != null) {
                        mActiveMarker.hideInfoWindow();
                    }
                    int item = mLayoutManager.findFirstCompletelyVisibleItemPosition();
                    mActiveMarker = EcoLocationManager.getEcoLocationsList().get(item).getMarker();
                    mActiveMarker.showInfoWindow();
                    moveToMarker(mActiveMarker);
                }
            }
        });

        mRequestQueue = Volley.newRequestQueue(MainMapsActivity.this);

        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setOnClickListener(view -> startRefresh());
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMarkerClickListener(this);
        mMap.setOnInfoWindowClickListener(this);
        mMap.setOnMyLocationChangeListener(this);
        mMap.getUiSettings().setAllGesturesEnabled(false);

        if (!hasLocationPermissions()) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
            return;
        }
        setUpMapWithPermissions();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (!hasLocationPermissions()) {
            return;
        }
        setUpMapWithPermissions();
    }

    private void setUpMapWithPermissions() {
        if (!hasLocationPermissions()) {
            return;
        }
        //noinspection MissingPermission
        mMap.setMyLocationEnabled(true);
        LatLng update = getLastKnownLocation();
        if (update != null) {
            if (mCurrentLocation == null) {
                mCurrentLocation = update;
                startRefresh();
            }
//            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.fromLatLngZoom(update, 11.0f)));
            moveToPosition(update);
        } else {

        }
    }

    private boolean hasLocationPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    @Nullable
    private LatLng getLastKnownLocation() {
        LocationManager lm = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_LOW);
        String provider = lm.getBestProvider(criteria, true);
        if (provider == null) {
            return null;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!hasLocationPermissions()) {
                return null;
            }
        }
        @SuppressWarnings("MissingPermission") Location loc = lm.getLastKnownLocation(provider);
        if (loc != null) {
            return new LatLng(loc.getLatitude(), loc.getLongitude());
        }
        return null;
    }

    @NonNull
    private LatLng getMapCenter() {
//        return mMap.getCameraPosition().target;
//        return mMap.getProjection().getVisibleRegion().latLngBounds.getCenter();
        //TODO: remember to reset this, we're using map center to debug
        LatLng coord = null;//getLastKnownLocation();
        if (coord == null) {
            //return map center if we don't have
            return mMap.getProjection().getVisibleRegion().latLngBounds.getCenter();
        } else {
            return coord;
        }
    }

    private void createMarkersAndAnimateMap() {
        mMap.clear();
        mFeatureMap.clear();
        mActiveMarker = null;

        LatLngBounds.Builder boundBuilder = new LatLngBounds.Builder();
        boolean first = true;

        for (EcoLocation loc : EcoLocationManager.getEcoLocationsList()) {
            Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(loc.getCoordinates())
                    .title(loc.getNAME()));
            marker.setTag(loc);
            loc.setMarker(marker);

            if (first) {
                marker.showInfoWindow();
                mActiveMarker = marker;
                first = false;
            }

            boundBuilder.include(loc.getCoordinates());
            fetchGeoJson(marker);
        }

        LatLngBounds bounds = boundBuilder.build();
        int padding = (int) getResources().getDimension(R.dimen.marker_padding);
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        mMap.animateCamera(cu);
    }

    private void fetchGeoJson(Marker marker) {
        //https://api.protectedplanet.net/v3/protected_areas/555561770?with_geometry=true&token=c271eb62dd72df12f7e4db8ecdaefcc0
        EcoLocation loc = (EcoLocation) marker.getTag();
        String url =
                String.format(
                        "https://api.protectedplanet.net/v3/protected_areas/%s?with_geometry=true&token=c271eb62dd72df12f7e4db8ecdaefcc0",
                        loc.getWDPA_PID());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, response -> {
            Log.d("HttpRequest", response);
            try {
                GeoJsonLayer layer = new GeoJsonLayer(mMap, EcoLocationManager.getGeoJson(response));

                GeoJsonPolygonStyle polygonStyle = layer.getDefaultPolygonStyle();
                GeoJsonLineStringStyle lineStyle = layer.getDefaultLineStringStyle();
                GeoJsonPointStyle pointStyle = layer.getDefaultPointStyle();
                polygonStyle.setFillColor(Color.parseColor("#4d12af1d"));
                polygonStyle.setStrokeWidth(2);

                for (GeoJsonFeature feature : layer.getFeatures()) {
                    feature.setPolygonStyle(polygonStyle);
                    mFeatureMap.put(feature, loc);
                    feature.setProperty(WDPA_ID, String.valueOf(loc.getWDPA_PID()));
                    switch (feature.getGeometry().getGeometryType()) {
                        case "MultiPoint":
                        case "Point": {
                            //remove points, we've already added markers in the past
                            layer.removeFeature(feature);
                            break;
                        }
                    }
                }

                // Set<>
                if (layer.getFeatures() instanceof Collection<?>) {
                    // don't add layer if we don't have any features
                    if (((Collection<?>)layer.getFeatures()).size() == 0) {
                        return;
                    }
                }
//                layer.setOnFeatureClickListener(feature -> {
//                    String wdpa_id = feature.getProperty(WDPA_ID);
//                    Log.d("blah", "onFeatureClick: wpda_id property: " + wdpa_id);
//                    if (wdpa_id == null)
//                        return;
//                    EcoLocation loc1 =  EcoLocationManager.getLocationById(wdpa_id);
//                    Log.d("blah", "onFeatureClick: wpda_id: " + loc1.getWDPA_PID());
//                    if (loc1 == null)
//                        return;
//                    int index = EcoLocationManager.getLocationIndex(loc1);
//                    if (index != -1)
//                        mRecyclerView.smoothScrollToPosition(index);
//                });
//                // setOnFeatureClickListener overrides our previous onMarkerClickListener
//                mMap.setOnMarkerClickListener(this);
                layer.addLayerToMap();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Log.e("HttpRequest", error.toString());
        });
        stringRequest.setTag(REQUEST_PROTECTED_TAG);
        mRequestQueue.add(stringRequest);
    }

    private void fetchLocations() {
        mRequestQueue.cancelAll(REQUEST_SERVER_TAG);
        mRequestQueue.cancelAll(REQUEST_PROTECTED_TAG);
        LatLng pos = getMapCenter();
        String url = String.format(ECOLOCATION_URL, pos.latitude, pos.longitude);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, response -> {
            Log.d("HttpRequest", response);
            EcoLocationManager.loadLocationList(response);
            mAdapter.notifyDataSetChanged();
            createMarkersAndAnimateMap();
            transitionOutOfLoad(true);
        }, error -> {
            Log.e("HttpRequest", error.toString());
            Toast.makeText(this, "Failed to fetch data from server", Toast.LENGTH_SHORT).show();
            transitionOutOfLoad(false);
        });
        stringRequest.setTag(REQUEST_SERVER_TAG);
        mRequestQueue.add(stringRequest);
    }

    private void transitionIntoLoad() {
        mFab.setEnabled(false);
        mProgressBar.setVisibility(View.VISIBLE);
        mRefreshText.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.GONE);
        mLayout.setPanelHeight((int) getResources().getDimension(R.dimen.slide_size));
        mMapLayout.setTranslationY(0);
        mLayout.invalidate();
        mMapLayout.invalidate();
    }

    private void transitionOutOfLoad(boolean showList) {
        mFab.setEnabled(true);
        mProgressBar.setVisibility(View.GONE);
        if (showList) {
            mRecyclerView.setVisibility(View.VISIBLE);
            mRefreshText.setVisibility(View.GONE);
            // workaround, recyclerview takes a while to draw, which means getHeight returns 0
            // so we post a runnable in the message queue to set the panel height, after the height
            // has been properly calculated
            mRecyclerView.post(() -> mLayout.setPanelHeight(mRecyclerView.getHeight()));
            mMap.getUiSettings().setAllGesturesEnabled(true);
        } else {
            mRefreshText.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
            mLayout.setPanelHeight((int) getResources().getDimension(R.dimen.slide_size));
        }
        mMapLayout.setTranslationY(0);
        mLayout.invalidate();
        mMapLayout.invalidate();
    }

    private void moveToMarker(Marker marker) {
        moveToPosition(marker.getPosition());
    }

    private void moveToPosition(LatLng pos) {
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(pos)
                .zoom(mMap.getCameraPosition().zoom)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        EcoLocation loc = (EcoLocation) marker.getTag();
        int index = EcoLocationManager.getLocationIndex(loc);
        if (index != -1)
            mRecyclerView.smoothScrollToPosition(index);
        return false;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        openLocationDetailActivity((EcoLocation) marker.getTag());
    }

    private void openLocationDetailActivity(EcoLocation loc) {
        Intent intent = new Intent(this, EcoLocationDetailsActivity.class);
        intent.putExtra("EcoLocation", loc);
        startActivity(intent);
    }

    @Override
    public void onMyLocationChange(Location location) {
        LatLng locationLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        mCurrentLocation = locationLatLng;
        // only update camera when we don't have markers on screen
        if (EcoLocationManager.getEcoLocationsList().size() != 0) {
            return;
        }
        moveToPosition(locationLatLng);
    }

    private void startRefresh() {
        Log.d("blah", "onCreate: " + mRecyclerView.getHeight());
        transitionIntoLoad();
        fetchLocations();
    }
}
