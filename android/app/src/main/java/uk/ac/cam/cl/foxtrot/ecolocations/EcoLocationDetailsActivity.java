package uk.ac.cam.cl.foxtrot.ecolocations;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.util.AsyncListUtil;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class EcoLocationDetailsActivity extends AppCompatActivity {
    private static final String TAG = "EcoLocationDetailsActiv";
    private static final int PHOTO_SIZE = 270;

    private EcoLocation mEcoLocation;

    private TextView detailsDesignation;
    private TextView detailsCategory;
    private TextView detailsArea;
    private TextView detailsStatus;
    private TextView detailsStatusYr;
    private TextView detailsDesignationType;
    private TextView detailsMangAuth;
    private TextView detailsLandCover;
    private TextView detailsGovernance;
    private LinearLayout mLayout;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private PhotoAdapter mAdapter;

    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2017-02-26 17:10:39 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        detailsDesignation = (TextView) findViewById(R.id.details_designation);
        detailsCategory = (TextView) findViewById(R.id.details_category);
        detailsArea = (TextView) findViewById(R.id.details_area);
        detailsStatus = (TextView) findViewById(R.id.details_status);
        detailsStatusYr = (TextView) findViewById(R.id.details_status_yr);
        detailsDesignationType = (TextView) findViewById(R.id.details_designation_type);
        detailsMangAuth = (TextView) findViewById(R.id.details_mang_auth);
        detailsLandCover = (TextView) findViewById(R.id.details_landcover);
        detailsGovernance = (TextView) findViewById(R.id.details_governance);
        mLayout = (LinearLayout) findViewById(R.id.details_layout);
        mRecyclerView = (RecyclerView) findViewById(R.id.details_flickr_grid);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eco_location_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        mEcoLocation = (EcoLocation) getIntent().getExtras().get("EcoLocation");
        setupViews();

        final int gridMargin = getResources().getDimensionPixelOffset(R.dimen.grid_margin);
        int spanCount = getResources().getDisplayMetrics().widthPixels / (PHOTO_SIZE + (2 * gridMargin));
        mLayoutManager = new GridLayoutManager(this, spanCount);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.set(gridMargin, gridMargin, gridMargin, gridMargin);
            }
        });
        int heightCount = getResources().getDisplayMetrics().heightPixels / PHOTO_SIZE;
        mRecyclerView.getRecycledViewPool().setMaxRecycledViews(0, spanCount * heightCount * 2);
        mRecyclerView.setItemViewCacheSize(0);
        mAdapter = new PhotoAdapter();
        mRecyclerView.setAdapter(mAdapter);

        fetchImages();
    }

    private void setupViews() {
        findViews();
        setTitle(mEcoLocation.getNAME());
        /*
        Sample response:
            {
      "id": 4,
      "WDPA_PID": "193928",
      "PA_DEF": 1,
      "NAME": "Paradise",
      "LATITUDE": "52.1948130000000000",
      "LONGITUDE": "0.1130090000000000",
      "LANDCOVER": null,
      "ORIG_NAME": "Paradise",
      "TYPE": "Polygon",
      "DESIG": "Local Nature Reserve",
      "DESIG_ENG": "Local Nature Reserve",
      "DESIG_TYPE": "National",
      "IUCN_CAT": "IV",
      "INT_CRIT": "Not Applicable",
      "MARINE": 0,
      "REP_M_AREA": 0,
      "GIS_M_AREA": 0,
      "REP_AREA": 0.02,
      "GIS_AREA": 0.021900661347772,
      "NO_TAKE": "Not Applicable",
      "NO_TK_AREA": 0,
      "STATUS": "Designated",
      "STATUS_YR": 1996,
      "GOV_TYPE": "Federal or national ministry or agency",
      "OWN_TYPE": "Not Reported",
      "MANG_AUTH": "Natural England, Scottish Natural Heritage, Natural Resources Wales and Department of Environment Northern Ireland, Local Authorities",
      "MANG_PLAN": "Not Reported",
      "VERIF": "State Verified",
      "METADATAID": 1839,
      "SUB_LOC": "UKH",
      "PARENT_ISO3": "GBR",
      "ISO3": "GBR",
      "distance": 2899.7712637269
        },
         */
        detailsArea.setText(
                String.format(Locale.getDefault(), "%f",
                        mEcoLocation.getREP_M_AREA() + mEcoLocation.getREP_AREA()));
        detailsLandCover.setText(mEcoLocation.getLANDCOVER());
        detailsStatus.setText(mEcoLocation.getSTATUS());
        detailsStatusYr.setText(String.valueOf(mEcoLocation.getSTATUS_YR()));
        detailsDesignation.setText(mEcoLocation.getDESIG_ENG());
        detailsDesignationType.setText(mEcoLocation.getDESIG_TYPE());
        detailsGovernance.setText(mEcoLocation.getGOV_TYPE());
        //TODO: get named category
        detailsCategory.setText(mEcoLocation.getIUCN_CAT());
        detailsMangAuth.setText(mEcoLocation.getMANG_AUTH());

        Picasso.with(this)
                .load(String.format("https://protectedplanet.net/assets/tiles/%s?type=protected_area", mEcoLocation.getWDPA_PID()))
                .placeholder(R.color.cardview_dark_background)
                .into((ImageView) findViewById(R.id.toolbar_image));
    }

    private void fetchImages() {
        RequestQueue requestQueue = Volley.newRequestQueue(EcoLocationDetailsActivity.this);
        requestQueue.start();
        requestQueue.add(new StringRequest(Request.Method.GET, FlickrUtils.getURL(mEcoLocation), response -> {
            Log.d("HttpRequest", response);
            List<String> imageUrlList = FlickrUtils.XMLParse(response);
            mAdapter.setPhotos(imageUrlList);
//            for (String imageUrl : imageUrlList) {
//                Picasso.with(this)
//                        .load(imageUrl)
//                        .into(new Target() {
//                            @Override
//                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
//                                ImageView newImage = new ImageView(EcoLocationDetailsActivity.this);
//                                newImage.setImageBitmap(bitmap);
//                                mLayout.addView(newImage);
//                            }
//
//                            @Override
//                            public void onBitmapFailed(Drawable errorDrawable) {
//                                // ignore failures, we don't add images in this case
//                            }
//
//                            @Override
//                            public void onPrepareLoad(Drawable placeHolderDrawable) {
//                                // ignore placeholders, we don't add until we have an image
//                            }
//                        });
//            }
        }, error -> {
            Log.e("HttpRequest", error.toString());
        }));
    }

    private class PhotoAdapter extends RecyclerView.Adapter<PhotoViewHolder>  {
        private List<String> photos = Collections.emptyList();

        @Override
        public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_image, parent, false);
            ViewGroup.LayoutParams params = view.getLayoutParams();
            params.width = PHOTO_SIZE;
            params.height = PHOTO_SIZE;
            return new PhotoViewHolder(view);
        }

        public void setPhotos(List<String> photos) {
            this.photos = photos;
            notifyDataSetChanged();
        }

        @Override
        public int getItemViewType(int position) {
            return 0;
        }

        @Override
        public void onBindViewHolder(PhotoViewHolder holder, int position) {
            final String current = photos.get(position);

            Picasso.with(holder.imageView.getContext())
                    .load(current)
                    .into(holder.imageView);

            holder.imageView.setOnClickListener(view -> {
                Intent intent = FullscreenImageActivity.getIntent(holder.imageView.getContext(), current);
                startActivity(intent);
            });
        }

        @Override
        public long getItemId(int i) {
            return RecyclerView.NO_ID;
        }

        @Override
        public int getItemCount() {
            return photos.size();
        }
    }

    private static class PhotoViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView;

        public PhotoViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView;
        }
    }
}
