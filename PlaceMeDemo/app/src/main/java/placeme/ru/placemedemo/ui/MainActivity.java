package placeme.ru.placemedemo.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.tomergoldst.tooltips.ToolTip;
import com.tomergoldst.tooltips.ToolTipsManager;

import gl.GL1Renderer;
import gl.GLFactory;
import placeme.ru.placemedemo.R;
import placeme.ru.placemedemo.core.ArSetup;
import placeme.ru.placemedemo.core.Controller;
import placeme.ru.placemedemo.core.utils.HelpUtils;
import placeme.ru.placemedemo.elements.Place;
import system.ArActivity;
import worldData.World;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_CALENDAR;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_CALENDAR;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static placeme.ru.placemedemo.ui.dialogs.AlertDialogCreator.createAlertDialogFounded;
import static placeme.ru.placemedemo.ui.dialogs.AlertDialogCreator.createSearchParametersDialog;
import static placeme.ru.placemedemo.ui.dialogs.AlertDialogCreator.getPoints;
import static placeme.ru.placemedemo.ui.dialogs.DialogUtils.setUpDialog;
import static placeme.ru.placemedemo.ui.utils.MainUtils.addGooglePlace;
import static placeme.ru.placemedemo.ui.utils.MainUtils.checkLogin;
import static placeme.ru.placemedemo.ui.utils.MainUtils.getFieldValue;
import static placeme.ru.placemedemo.ui.utils.MainUtils.getPlaceInfo;
import static placeme.ru.placemedemo.ui.utils.MainUtils.loadProfileAvatar;
import static placeme.ru.placemedemo.ui.utils.MainUtils.login;
import static placeme.ru.placemedemo.ui.utils.MainUtils.plot;
import static placeme.ru.placemedemo.ui.utils.MainUtils.saveRoute;
import static placeme.ru.placemedemo.ui.utils.MainUtils.searchFriends;
import static placeme.ru.placemedemo.ui.utils.MainUtils.shareApplication;
import static placeme.ru.placemedemo.ui.utils.MainUtils.showInfo;

/**
 * Main activity of the app
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final int GALLERY_INTENT = 2;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final int PLACE_PICKER_REQUEST = 1;
    private static final int CAMERA_RESULT = 0;
    private static final String MY_PLACE = "My place";
    private static final String DATA_KEY = "data";
    private static final String IMAGE_TYPE = "image/*";
    //private static final String TIP_AR = .getString(R.string.tip_ar);
    //private static final String TIP_SEARCH_PLACES = .getString(R.string.tip_places);
    //private static final String TIP_SEARCH_FRIENDS = .getString(R.string.tip_friends);
    //private static final String TIP_GOOGLE_PLACES = .getString(R.string.tip_gp);
    //private static final String TIP_SEARCH_PARAMS = .getString(R.string.tip_params);
    //private static final String TIP_ROUTE_SNAPSHOT = .getString(R.string.tip_snapshot);
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final int SETTINGS_CODE = 7;
    private static String[] PERMISSIONS_STORAGE = {
            ACCESS_FINE_LOCATION,
            ACCESS_COARSE_LOCATION,
            READ_EXTERNAL_STORAGE,
            WRITE_EXTERNAL_STORAGE,
            READ_CALENDAR,
            WRITE_CALENDAR,
            CAMERA
    };

    private GoogleMap mGoogleMap;
    private Location mLastKnownLocation;
    private LatLng myPosition;
    private ImageView mImageView;
    private Bitmap mBitmap;
    private Uri mUri;
    private int mLastAction;
    private ToolTipsManager mToolTipsManager = new ToolTipsManager();

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
        setContentView(R.layout.activity_main);
        checkLogin(this);
        if (HelpUtils.getHelp(this)) {
            showInfo(MainActivity.this);
        }
        initGeolocation();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new MapLoader());
        initHeader();
        initInputWindow();
        initGooglePlacesButton();
        initSearchParameters();
        initRouteSnapshotButton();
        initFriendsSearchButton();

        if (ContextCompat.checkSelfPermission(this, CAMERA) != PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{CAMERA}, 1);
        } else {
            initializeCamera();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            loadChosenActivity(ProfileActivity.class);
        } else if (id == R.id.nav_places) {
            loadChosenActivity(FavouritePlacesActivity.class);
        } else if (id == R.id.nav_routes) {
            loadChosenActivity(RoutesActivity.class);
        } else if (id == R.id.nav_settings) {
            loadChosenActivity(PlanActivity.class);
        } else if (id == R.id.nav_share) {
            shareApplication(this);
        } else if (id == R.id.nav_exit) {
            Controller.setLoggedOut(MainActivity.this);
            login(this);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK) {
            mUri = data.getData();
            mImageView.setImageURI(mUri);
            mLastAction = 1;
        }
        if (requestCode == PLACE_PICKER_REQUEST && resultCode == RESULT_OK) {
            addGooglePlace(this, data);
        }
        if (requestCode == CAMERA_RESULT && data != null && data.getExtras() != null) {
            mBitmap = (Bitmap) data.getExtras().get(DATA_KEY);
            mLastAction = 2;
            mImageView.setImageBitmap(mBitmap);
        }
        if (requestCode == SETTINGS_CODE) {
            this.recreate();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivityForResult(new Intent(this, SettingsActivity.class), SETTINGS_CODE);
            return true;
        }

        if (id == R.id.action_about) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.about);
            builder.setMessage(R.string.authors);
            builder.setPositiveButton(R.string.answer_ok, (dialog, which) -> dialog.dismiss());
            builder.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Method that generates tooltip for specific view
     * @param view view to add tooltip
     * @param tip tip text
     * @param position tip position relative to view
     * @return always true because showing tip is always not an action and button should not be pressed
     */
    private boolean generateTip(final View view, final String tip, int position) {
        ToolTip.Builder builder = new ToolTip.Builder(this, view, findViewById(R.id.root_t), tip, position);
        mToolTipsManager.show(builder.build());

        return true;
    }

    private void initializeCamera() {
        Button b = findViewById(R.id.button4);
        b.setOnLongClickListener(v -> generateTip(b, getString(R.string.tip_ar), ToolTip.POSITION_ABOVE));
        b.setOnClickListener(v -> ArActivity.startWithSetup(MainActivity.this, new ArSetup() {
            @Override
            public void addObjectsTo(GL1Renderer renderer, final World world, GLFactory objectFactory) {
                plot(getPoints(), world, objectFactory, mLastKnownLocation);
            }
        }));
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initGeolocation() {
        FusedLocationProviderClient mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            int permission2 = ActivityCompat.checkSelfPermission(MainActivity.this, ACCESS_FINE_LOCATION);

            if (permission2 != PERMISSION_GRANTED) {
                requestPermissions(new String[]{ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            } else {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        mLastKnownLocation = task.getResult();
                        if (mLastKnownLocation != null) {
                            myPosition = new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
                        }
                    }
                });
            }
        } catch (SecurityException e)  {
            e.printStackTrace();
        }
    }

    private void initInputWindow() {
        EditText search = findViewById(R.id.search);
        search.setOnLongClickListener(v -> generateTip(search, getString(R.string.tip_places), ToolTip.POSITION_BELOW));
        search.setOnEditorActionListener((v, actionId, event) -> {
            final String query = getFieldValue(search);
            if (mGoogleMap != null) {
                mGoogleMap.clear();
            }
            Controller.addFoundedMarkers(mGoogleMap, query);
            createAlertDialogFounded(MainActivity.this, query, mGoogleMap, myPosition, this).show();

            return false;
        });
    }

    private void loadChosenActivity(Class<?> toLoad) {
        startActivity(new Intent(this, toLoad));
    }

    private void initGooglePlacesButton() {
        FloatingActionButton placesButton = findViewById(R.id.google_places_button);
        placesButton.setOnClickListener(v -> alertDialogAskGooglePlacesUsage(MainActivity.this).show());
        placesButton.setOnLongClickListener(v -> generateTip(placesButton, getString(R.string.tip_gp_p) + "\n" + getString(R.string.tip_gp_s), ToolTip.POSITION_BELOW));
    }

    private void initSearchParameters() {
        FloatingActionButton searchParamButton  = findViewById(R.id.button_search_parameters);
        searchParamButton.setOnClickListener(v -> createSearchParametersDialog(MainActivity.this).show());
        searchParamButton.setOnLongClickListener(v -> generateTip(searchParamButton, getString(R.string.tip_params), ToolTip.POSITION_LEFT_TO));
    }

    private void initHeader() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //toolbar.setBackground(new ColorDrawable(ColorUtils.getColor(this)));
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        View hView = navigationView.getHeaderView(0);

        loadProfileAvatar(MainActivity.this, hView);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void initRouteSnapshotButton() {
        FloatingActionButton actionButton = findViewById(R.id.fab);

        actionButton.setOnLongClickListener(v -> generateTip(actionButton, getString(R.string.tip_snapshot), ToolTip.POSITION_LEFT_TO));
        actionButton.setOnClickListener(v -> {
            Controller.getUserRoutesLength2(Controller.getLoggedInAsString(MainActivity.this), MainActivity.this);
            saveRoute(MainActivity.this, mGoogleMap).show();
        });
    }

    private void initFriendsSearchButton() {
        FloatingActionButton searchFriendsButton = findViewById(R.id.search_friends);

        searchFriendsButton.setOnLongClickListener(v -> generateTip(searchFriendsButton, getString(R.string.tip_friends_p) + "\n" + getString(R.string.tip_friends_s), ToolTip.POSITION_ABOVE));
        searchFriendsButton.setOnClickListener(v -> searchFriends(this).show());
    }

    private AlertDialog createAlertDialogNewPlace(final LatLng latLng) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        builder.setTitle(R.string.places_creating);
        builder.setMessage(R.string.places_creating_question);
        builder.setPositiveButton(R.string.answer_yes, (dialog, arg1) -> createNewPlace(latLng).show());
        builder.setNegativeButton(R.string.answer_no, (dialog, arg1) -> dialog.dismiss());
        builder.setCancelable(true);

        return builder.create();
    }

    private AlertDialog createNewPlace(final LatLng latLng) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater inflater = (LayoutInflater) MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View layout = inflater.inflate(R.layout.dialog_new_place, null);

        mBitmap = null;
        mUri = null;
        mLastAction = 0;
        mImageView = layout.findViewById(R.id.new_place_image);
        mImageView.setOnClickListener(v -> askPictureDialog().show());

        builder.setPositiveButton(R.string.answer_finish, (dialog, id) -> {
            Place placeInfo = getPlaceInfo(layout);
            if (mLastAction == 1) {
                Controller.saveCreatedPlace(mUri, placeInfo, latLng);
            } else {
                Controller.saveCreatedPlace2(mBitmap, placeInfo, latLng);
            }
        });
        builder.setNegativeButton(R.string.answer_back, (dialog, arg1) -> dialog.dismiss());

        return setUpDialog(builder, layout);
    }

    private AlertDialog alertDialogAskGooglePlacesUsage(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle(R.string.places_question_gp);
        builder.setMessage(R.string.places_question_gp_message);
        builder.setPositiveButton(R.string.answer_yes, (dialog, which) -> {

            PlacePicker.IntentBuilder builder1 = new PlacePicker.IntentBuilder();

            try {
                startActivityForResult(builder1.build(MainActivity.this), PLACE_PICKER_REQUEST);
            } catch (GooglePlayServicesRepairableException e) {
                e.printStackTrace();
            } catch (GooglePlayServicesNotAvailableException e) {
                e.printStackTrace();
            }
        });
        builder.setNegativeButton(R.string.answer_no, (dialog, which) -> dialog.cancel());
        builder.setCancelable(true);

        return builder.create();
    }

    private AlertDialog askPictureDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage(R.string.ask_photo);
        builder.setTitle(R.string.photo_add);
        builder.setPositiveButton(R.string.answer_gallery, (dialog, which) -> {
            Intent intent = new Intent(Intent.ACTION_PICK);

            mLastAction = 1;
            intent.setType(IMAGE_TYPE);
            startActivityForResult(intent, GALLERY_INTENT);
        });
        builder.setNeutralButton(R.string.answer_photo, (dialog, which) -> {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            mLastAction = 2;
            startActivityForResult(cameraIntent, CAMERA_RESULT);
        });
        builder.setNegativeButton(R.string.answer_cancel, (dialog, which) -> dialog.dismiss());

        return builder.create();
    }

    /**
     * Class that listens for clicks on the markers on the map and do all
     * important things connected with such clicks
     */
    public class MarkerListener implements GoogleMap.OnMarkerClickListener {
        @Override
        public boolean onMarkerClick(Marker marker) {
            if (marker.getTitle() == null) {
                return false;
            }
            if (marker.getTitle().equals(MY_PLACE)) {
                createAlertDialogNewPlace(marker.getPosition()).show();
                Controller.refreshMarkers(mGoogleMap);
            } else {
                Controller.runDescriptionDialog(MainActivity.this, marker, myPosition, mGoogleMap);
            }

            return false;
        }
    }

    /**
     * Class that reacts on map clicks
     */
    public class MapListener implements GoogleMap.OnMapLongClickListener {
        @Override
        public void onMapLongClick(LatLng point) {
            mGoogleMap.addMarker(new MarkerOptions().position(point).title(MY_PLACE).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker7)));
        }
    }

    /**
     * Class that initialize map in the application
     * using generated google map
     */
    public class MapLoader implements OnMapReadyCallback {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            mGoogleMap = googleMap;
            mGoogleMap.setOnMapLongClickListener(new MapListener());
            mGoogleMap.setOnMarkerClickListener(new MarkerListener());
            mGoogleMap.getUiSettings().setMapToolbarEnabled(false);
            mGoogleMap.getUiSettings().setCompassEnabled(false);
            if (ActivityCompat.checkSelfPermission(MainActivity.this, ACCESS_FINE_LOCATION) != PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(MainActivity.this, ACCESS_COARSE_LOCATION) != PERMISSION_GRANTED) {
                return;
            }
            mGoogleMap.setMyLocationEnabled(true);
            Controller.addAllMarkers(mGoogleMap);
        }
    }
}