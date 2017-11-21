package placeme.ru.placemedemo;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import static android.Manifest.permission.READ_CONTACTS;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, GoogleMap.OnMapClickListener,
        GoogleMap.OnMapLongClickListener, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMarkerClickListener {

    private GoogleMap googleMap;
    private static final int MAPS_ACTIVITY_CODE = 7;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private FirebaseDatabase mBase;
    private DatabaseReference mDatabaseReference;
    private ChildEventListener childEventListener;

    private Integer iid;
    private EditText edName;
    private EditText edDescription;
    private EditText edTags;
    private Marker myPlace;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (LoginUtility.getLoggedIn(this) == -1) {
            login();
            return;
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void login() {
        Intent login = new Intent(this, LoginActivity.class);
        login.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(login);
        finish();
    }

    @Override
    public void onMapClick(LatLng point) {
        Log.d("onMapClick", "pressed" + point);
        Toast.makeText(getApplicationContext(), "tapped, point=" + point, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMapLongClick(LatLng point) {
        Log.d("onMapLongClick", "pressed" + point);
        //googleMap.clear();
        googleMap.addMarker(new MarkerOptions().position(point).title("My Place"));
        //googleMap.moveCamera(CameraUpdateFactory.newLatLng(point));
        //Toast.makeText(getApplicationContext(), "tapped, point=" + point, Toast.LENGTH_SHORT).show();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        googleMap.setOnMyLocationButtonClickListener(this);
        googleMap.setOnMapClickListener(this);
        googleMap.setOnMapLongClickListener(this);
        googleMap.setOnMarkerClickListener(this);
        Log.d("MAP", "enabled");
        //enableLocation();
        //if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);

        mBase = FirebaseDatabase.getInstance();
        mDatabaseReference = mBase.getReference().child("places");
        if( childEventListener == null ) {
            childEventListener = new ChildEventListener() {

                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Place place = (Place) dataSnapshot.getValue(Place.class);
                    googleMap.addMarker(new MarkerOptions()
                            .position(new LatLng(place.getLatitude(), place.getLongitude()))
                            .title(place.getName()));

                }

                @Override public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
                @Override public void onChildRemoved(DataSnapshot dataSnapshot) {}
                @Override public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
                @Override public void onCancelled(DatabaseError databaseError) {}
            };
        }
        mDatabaseReference.addChildEventListener(childEventListener);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        try {
            googleMap.setMyLocationEnabled(true);
        } catch (SecurityException se) {
            se.printStackTrace();
        }
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        //Log.d("MRK", "CLICK");
        //Toast.makeText(this, "Ask to create new place?", Toast.LENGTH_LONG).show();
        if(marker.getTitle().equals("My Place")) {
            AlertDialog alert = createAlertDialog(marker.getPosition());
            alert.show();
            // TODO: i do not like this!
            refreshMarkers();
        }
        else {
           //Log.d("MRK", "CLICK");
            mBase = FirebaseDatabase.getInstance();
            mDatabaseReference = mBase.getReference().child("places");
            ChildEventListener childEventListener1 = new ChildEventListener() {

                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Place place = (Place) dataSnapshot.getValue(Place.class);
                    //Log.d("MRK", "CLICK11111");
                    if ((place.getLatitude() == marker.getPosition().latitude) && (place.getLongitude() == marker.getPosition().longitude)) {
                        AlertDialog alert = createAlertDescriptionDialog(place);
                        alert.show();
                       // Toast.makeText(MainActivity.this, "Show Descr", Toast.LENGTH_LONG).show();
                    }

                }

                @Override public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
                @Override public void onChildRemoved(DataSnapshot dataSnapshot) {}
                @Override public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
                @Override public void onCancelled(DatabaseError databaseError) {}
            };
            mDatabaseReference.addChildEventListener(childEventListener1);
        }

        // Return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).
        return false;
    }

    private void refreshMarkers() {
        googleMap.clear();
        ChildEventListener childEventListener1 = new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Place place = (Place) dataSnapshot.getValue(Place.class);
                googleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(place.getLatitude(), place.getLongitude()))
                        .title(place.getName()));

            }

            @Override public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
            @Override public void onChildRemoved(DataSnapshot dataSnapshot) {}
            @Override public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            @Override public void onCancelled(DatabaseError databaseError) {}
        };
        mDatabaseReference.addChildEventListener(childEventListener1);
    }
    private AlertDialog createAlertDialog(final LatLng latLng) {
        AlertDialog.Builder ad;
        ad = new AlertDialog.Builder(MainActivity.this);
        ad.setTitle("Creating place");  // заголовок
        ad.setMessage("Create new place here?"); // сообщение
        ad.setPositiveButton("Yes!", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                //Toast.makeText(MainActivity.this, "TODO: creating place", Toast.LENGTH_LONG).show();
                AlertDialog alertNewPlace = createNewPlace(latLng);
                alertNewPlace.show();
            }
        });
        ad.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {

            }
        });
        ad.setCancelable(true);
        return ad.create();
    }

    private AlertDialog createAlertDescriptionDialog(final Place place) {
        AlertDialog.Builder ad;
        ad = new AlertDialog.Builder(MainActivity.this);
        ad.setTitle(place.getName());  // заголовок
        ad.setMessage(place.getDescription()); // сообщение
        ad.setPositiveButton("Go here!", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                //TODO: build root
                Toast.makeText(MainActivity.this, "TODO: build root", Toast.LENGTH_LONG).show();
            }
        });
        ad.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {

            }
        });
        ad.setCancelable(true);
        return ad.create();
    }

    private AlertDialog createNewPlace(final LatLng latLng) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater inflater = MainActivity.this.getLayoutInflater();
        final View layout = inflater.inflate(R.layout.dialog_new_place, null);

        builder.setPositiveButton("Finish", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                edName = (EditText) layout.findViewById(R.id.place_name);
                edDescription = (EditText) layout.findViewById(R.id.place_description);
                edTags = (EditText) layout.findViewById(R.id.place_tags);
                mBase = FirebaseDatabase.getInstance();
                mDatabaseReference = mBase.getReference().child("maxidplaces");
                mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Integer id = (Integer) dataSnapshot.getValue(Integer.class);
                        iid = id;
                        Place newPlace = new Place(iid, edName.getText().toString(), edDescription.getText().toString(), edTags.getText().toString(), latLng.latitude, latLng.longitude);
                        DatabaseReference mDatabaseReference1 = mBase.getReference().child("places");
                        mDatabaseReference1.child(id.toString()).setValue(newPlace);
                        mDatabaseReference.setValue(iid + 1);
                    }

                    @Override
                    public void onCancelled( DatabaseError firebaseError) {

                        Log.d("User", "-1" );
                    }
                });


            }
        }).setNegativeButton("Back", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {}
        });
        builder.setCancelable(true);
        builder.setView(layout);
        return builder.create();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            Log.d("back", "pressed");
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            Intent profile = new Intent(this, ProfileActivity.class);
            //profile.putExtra("jjlkn", "kjhgkjbjhbj,kh");
            startActivity(profile);
        } else if (id == R.id.nav_places) {
            Intent places = new Intent(this, FavouritePlacesActivity.class);
            startActivity(places);

        } else if (id == R.id.nav_routes) {
            Intent routes = new Intent(this, RoutesActivity.class);
            startActivity(routes);

        } else if (id == R.id.nav_settings) {
            Intent settings = new Intent(this, SettingsActivity.class);
            startActivity(settings);

        } else if (id == R.id.nav_share) {
            Toast.makeText(getApplicationContext(), "Nothing to share :(",
                    Toast.LENGTH_SHORT).show();

        } else if (id == R.id.nav_exit) {
            LoginUtility.setLoggedOut(MainActivity.this);
            login();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onStop () {
        super.onStop();
    }

    @Override
    public void onDestroy () {
        super.onDestroy();
        if (childEventListener != null) {
            mDatabaseReference.removeEventListener(childEventListener);
            childEventListener = null;
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }


}
