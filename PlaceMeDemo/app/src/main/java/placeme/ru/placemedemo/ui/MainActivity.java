package placeme.ru.placemedemo.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.tomergoldst.tooltips.ToolTip;
import com.tomergoldst.tooltips.ToolTipsManager;

import gl.GL1Renderer;
import gl.GLFactory;
import placeme.ru.placemedemo.R;
import placeme.ru.placemedemo.core.Controller;
import placeme.ru.placemedemo.core.utils.ConverterToPlace;
import placeme.ru.placemedemo.ui.dialogs.AlertDialogCreator;
import system.ArActivity;
import system.MySetup;
import worldData.World;

import static com.google.android.gms.location.places.ui.PlaceAutocomplete.getPlace;
import static placeme.ru.placemedemo.ui.MainUtils.checkLogin;
import static placeme.ru.placemedemo.ui.MainUtils.getFieldValue;
import static placeme.ru.placemedemo.ui.MainUtils.loadProfileAvatar;
import static placeme.ru.placemedemo.ui.MainUtils.login;
import static placeme.ru.placemedemo.ui.MainUtils.plot;
import static placeme.ru.placemedemo.ui.MainUtils.saveRoute;
import static placeme.ru.placemedemo.ui.MainUtils.searchFriends;
import static placeme.ru.placemedemo.ui.MainUtils.showInfo;
import static placeme.ru.placemedemo.ui.dialogs.AlertDialogCreator.getPoints;
import static placeme.ru.placemedemo.ui.dialogs.DialogUtils.setUpDialog;

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

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,

    };

    private GoogleMap mGoogleMap;

    private Location mLastKnownLocation;
    private LatLng myPosition;

    private EditText mSearch;

    private ImageView mImageView;
    private Bitmap mBitmap;
    private Uri mUri;
    private int mLastAction = 0;
    private ToolTipsManager mToolTipsManager;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
        setContentView(R.layout.activity_main);
        checkLogin(this);
        showInfo(MainActivity.this);

        initializeGeolocation();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new MapLoader());

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        View hView = navigationView.getHeaderView(0);

        mToolTipsManager = new ToolTipsManager();

        loadProfileAvatar(MainActivity.this, hView);
        navigationView.setNavigationItemSelectedListener(this);

        initializeInputWindow();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
        } else {
            initializeCamera();
        }

        initGooglePlacesButton();

        FloatingActionButton actionButton = findViewById(R.id.fab);

        actionButton.setOnClickListener(v -> {
            Controller.getUserRoutesLength2(Controller.getLoggedInAsString(MainActivity.this), MainActivity.this);
            saveRoute(MainActivity.this, mGoogleMap).show();
        });

        ToolTip.Builder builder1 = new ToolTip.Builder(this, actionButton, findViewById(R.id.root_t), "Take a route snapshot!", ToolTip.POSITION_LEFT_TO);

        actionButton.setOnLongClickListener(v -> {
            mToolTipsManager.show(builder1.build());
            return true;
        });

        initializeSearchParameters();

        FloatingActionButton searchFriendsButton = findViewById(R.id.search_friends);

        ToolTip.Builder builder = new ToolTip.Builder(this, searchFriendsButton, findViewById(R.id.root_t), "Find friends\nhere!", ToolTip.POSITION_ABOVE);

        searchFriendsButton.setOnClickListener(v -> {
            searchFriends(this).show();
        });

        searchFriendsButton.setOnLongClickListener(v -> {
            mToolTipsManager.show(builder.build());
            return true;
        });

    }

    private void initGooglePlacesButton() {
        FloatingActionButton floatingActionButton = findViewById(R.id.google_places_button);
        ToolTip.Builder builder = new ToolTip.Builder(this, floatingActionButton, findViewById(R.id.root_t), "Import places\nfrom big base", ToolTip.POSITION_BELOW);

        floatingActionButton.setOnClickListener(v -> alertDialogAskGooglePlacesUsage(MainActivity.this).show());

        floatingActionButton.setOnLongClickListener(v -> {
            mToolTipsManager.show(builder.build());
            return true;
        });
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {}

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
            Intent profile = new Intent(this, ProfileActivity.class);
            startActivity(profile);
        } else if (id == R.id.nav_places) {
            Intent places = new Intent(this, FavouritePlacesActivity.class);
            startActivity(places);
        } else if (id == R.id.nav_routes) {
            Intent routes = new Intent(this, RoutesActivity.class);
            startActivity(routes);
        } else if (id == R.id.nav_settings) {
            Intent settings = new Intent(this, PlanActivity.class);
            startActivity(settings);
        } else if (id == R.id.nav_share) {
            shareApplication();
        } else if (id == R.id.nav_exit) {
            Controller.setLoggedOut(MainActivity.this);
            login(this);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void shareApplication() {
        //TODO:PICTURE
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Я пользуюсь приложением PlaceMe! Присоединяйся и ты: placeme.com :)");
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, "Share using..."));
    }

    private void initializeSearchParameters() {
        FloatingActionButton searchParamButton  = findViewById(R.id.button_search_parameters);
        ToolTip.Builder builder = new ToolTip.Builder(this, searchParamButton, findViewById(R.id.root_t), "Customize your search!", ToolTip.POSITION_LEFT_TO );
        searchParamButton.setOnClickListener(v -> {
            AlertDialog alert = AlertDialogCreator.createSearchParametersDialog(MainActivity.this);
            alert.show();
        });

        searchParamButton.setOnLongClickListener(v -> {
            mToolTipsManager.show(builder.build());
            return true;
        });

    }

    @Override
    public void onStop () {
        super.onStop();
    }

    @Override
    public void onDestroy () {
        super.onDestroy();
    }

    public class MarkerListener implements GoogleMap.OnMarkerClickListener {
        @Override
        public boolean onMarkerClick(Marker marker) {
            if (marker.getTitle() == null) {
                return false;
            }
            if (marker.getTitle().equals(MY_PLACE)) {
                AlertDialog alert = createAlertDialogNewPlace(marker.getPosition());
                alert.show();

                Controller.refreshMarkers(mGoogleMap);
            }
            else {
                Controller.runDescriptionDialog(MainActivity.this, marker, myPosition, mGoogleMap);
            }

            return false;
        }
    }

    public class MapListener implements GoogleMap.OnMapLongClickListener {
        @Override
        public void onMapLongClick(LatLng point) {
            mGoogleMap.addMarker(new MarkerOptions().position(point).title(MY_PLACE));
        }
    }

    public class MapLoader implements OnMapReadyCallback {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            mGoogleMap = googleMap;
            mGoogleMap.setOnMapLongClickListener(new MapListener());
            mGoogleMap.setOnMarkerClickListener(new MarkerListener());
            mGoogleMap.getUiSettings().setMapToolbarEnabled(false);
            mGoogleMap.getUiSettings().setCompassEnabled(false);
            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mGoogleMap.setMyLocationEnabled(true);
            Controller.addAllMarkers(mGoogleMap);
        }
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
            //TODO: ?
            Place place = getPlace(this, data);
            placeme.ru.placemedemo.elements.Place toAdd = ConverterToPlace.convertGooglePlaceToPlace(place);

            Controller.saveConvertedPlace(null, toAdd);
            String toastMsg = String.format("Place: %s", place.getName().toString());
            Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
        }
        if (requestCode == CAMERA_RESULT && data != null && data.getExtras() != null) {
            mBitmap = (Bitmap) data.getExtras().get("data");
            mLastAction = 2;

            mImageView.setImageBitmap(mBitmap);
        }
    }

    private void initializeCamera() {
        Button b = findViewById(R.id.button4);
        ToolTip.Builder builder = new ToolTip.Builder(this, b, findViewById(R.id.root_t), "Try AR route!", ToolTip.POSITION_ABOVE);
        b.setOnLongClickListener(v -> {
            mToolTipsManager.show(builder.build());
            return true;
        });
        b.setOnClickListener(v -> ArActivity.startWithSetup(MainActivity.this, new MySetup() {
            @Override
            public void addObjectsTo(GL1Renderer renderer, final World world, GLFactory objectFactory) {
                plot(getPoints(), world, objectFactory, mLastKnownLocation);
            }
        }));
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initializeGeolocation() {
        FusedLocationProviderClient mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            int permission2 = ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);

            if (permission2 != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
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
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void initializeInputWindow() {
        mSearch = findViewById(R.id.search);
        ToolTip.Builder builder2 = new ToolTip.Builder(this, mSearch, findViewById(R.id.root_t), "Search places\nhere!", ToolTip.POSITION_BELOW );
        mSearch.setOnLongClickListener(v -> {
            mToolTipsManager.show(builder2.build());
            return true;
        });
        mSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (mGoogleMap != null) {
                mGoogleMap.clear();
            }
            final String query = mSearch.getText().toString();
            Controller.addFoundedMarkers(mGoogleMap, query);
            AlertDialogCreator.createAlertDialogFounded(MainActivity.this, query, mGoogleMap, myPosition, this).show();

            return false;
        });
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
        LayoutInflater inflater = (LayoutInflater) MainActivity.this.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        final View layout = inflater.inflate(R.layout.dialog_new_place, null);

        mBitmap = null;
        mUri = null;
        mLastAction = 0;

        mImageView = layout.findViewById(R.id.new_place_image);
        mImageView.setOnClickListener(v -> {
            AlertDialog.Builder builderInner = new AlertDialog.Builder(MainActivity.this);
            builderInner.setMessage("Do you want to open gallery or take photo?");
            builderInner.setTitle("Adding photo");
            builderInner.setPositiveButton("Gallery", (dialog, which) -> {
                Intent intent = new Intent(Intent.ACTION_PICK);

                mLastAction = 1;
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY_INTENT);
            });
            builderInner.setNeutralButton("Camera", (dialog, which) -> {
                mLastAction = 2;
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                startActivityForResult(cameraIntent, CAMERA_RESULT);
            });
            builderInner.setNegativeButton(R.string.answer_cancel, (dialog, which) -> dialog.dismiss());
            builderInner.show();

        });

        builder.setPositiveButton(R.string.answer_finish, (dialog, id) -> {
            placeme.ru.placemedemo.elements.Place placeInfo = getPlaceInfo(layout);
            if (mLastAction == 1) {
                Controller.saveCreatedPlace(mUri, placeInfo, latLng);
            } else {
                Controller.saveCreatedPlace2(mBitmap, placeInfo, latLng);
            }
        }).setNegativeButton(R.string.answer_back, (dialog, arg1) -> dialog.dismiss());

        return setUpDialog(builder, layout);
    }

    private placeme.ru.placemedemo.elements.Place getPlaceInfo(final View layout) {
        EditText edName = layout.findViewById(R.id.place_name);
        EditText edDescription = layout.findViewById(R.id.place_description);
        EditText edTags = layout.findViewById(R.id.place_tags);

        return new placeme.ru.placemedemo.elements.Place(-1, getFieldValue(edName), getFieldValue(edDescription), getFieldValue(edTags), 0, 0);
    }
}