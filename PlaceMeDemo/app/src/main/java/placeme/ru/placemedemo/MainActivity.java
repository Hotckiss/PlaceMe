package placeme.ru.placemedemo;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import geo.GeoObj;
import gl.GL1Renderer;
import gl.GLFactory;
import placeme.ru.placemedemo.core.map.MapManager;
import placeme.ru.placemedemo.core.utils.AuthorizationUtils;
import placeme.ru.placemedemo.elements.Place;
import system.ArActivity;
import system.MySetup;
import worldData.World;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, GoogleMap.OnMapClickListener,
        GoogleMap.OnMapLongClickListener, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMarkerClickListener {

    private GoogleMap googleMap;
    private static final int MAPS_ACTIVITY_CODE = 7;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private FirebaseDatabase mBase;
    private DatabaseReference mDatabaseReference;
    private ChildEventListener childEventListener;

    //location
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location mLastKnownLocation;
    private LatLng myPosition;
    //***

    private Integer iid;
    private EditText edName;
    private EditText edDescription;
    private EditText edTags;
    private Marker myPlace;

    private EditText edSearch;

    //img
    private StorageReference mStorageRef;
    private static final int GALLERY_INTENT = 2;
    private static ImageView iv;
    private Uri uri;

    //ar
    private static ArrayList<LatLng> points = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("seaddddddddrch", "find!");
        if (AuthorizationUtils.getLoggedIn(this) == -1) {
            login();
            return;
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        //img
        mStorageRef = FirebaseStorage.getInstance().getReference();


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        edSearch = (EditText) findViewById(R.id.search);
        edSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                //Toast.makeText(getApplicationContext(), "tapped, point=0", Toast.LENGTH_LONG).show();
                if(googleMap != null) {
                    googleMap.clear();
                }

                Log.d("search", "find!");
                final String toFind = edSearch.getText().toString();
                //Toast.makeText(getApplicationContext(), toFind, Toast.LENGTH_LONG).show();
                MapManager.addFoundedMarkers(googleMap, toFind);
                AlertDialogCreator.createAlertDialogFinded(MainActivity.this, toFind, googleMap, myPosition).show();
                return false;
            }
        });

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
            locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    if (task.isSuccessful()) {
                        // Set the map's camera position to the current location of the device.
                        mLastKnownLocation = task.getResult();
                        myPosition = new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
                        //Log.d("coords", ((Double)mLastKnownLocation.getLatitude()).toString());

                        //Log.d("coords", ((Double)mLastKnownLocation.getLongitude()).toString());

                        //Log.d("coords", ((Double)mLastKnownLocation.getAltitude()).toString());
                        //59.9473787
                        //30.2621547
                    } else {
                        Log.d("FATAL ERROR", "Current location is null. Using defaults.");
                    }
                }});
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }


        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            Log.d("aaaaaaaaaaaaaaaaa", "rtfgnsftjnrtgggn");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},1);//выводит диалог, где пользователю предоставляется выбор
        }else{
            Log.d("en", "rtfgnsftjnrtgggn");
            testCam();
        }

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
        googleMap.addMarker(new MarkerOptions().position(point).title("My Place"));
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        googleMap.setOnMyLocationButtonClickListener(this);
        googleMap.setOnMapClickListener(this);
        googleMap.setOnMapLongClickListener(this);
        googleMap.setOnMarkerClickListener(this);

        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        MapManager.addAllMarkers(googleMap);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if(requestCode == 1) {
            Log.d("eeeeeeeeeeeeeeeeee", "rtfgnsftjnrtgggn");
            testCam();
        }
        try {
            googleMap.setMyLocationEnabled(true);
        } catch (SecurityException se) {
            se.printStackTrace();
        }
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        //Log.d("ddd", marker.getTitle());
        if(marker.getTitle() == null) {
            return false;
        }
        if(marker.getTitle().equals("My Place")) {
            AlertDialog alert = createAlertDialogNewPlace(marker.getPosition());
            alert.show();
            // TODO: i do not like this!
            MapManager.refreshMarkers(googleMap);
        }
        else {
            showDescriptionDialog(MainActivity.this, marker);
        }

        // Return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).
        return false;
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
            //Toast.makeText(getApplicationContext(), "Nothing to share :(",Toast.LENGTH_SHORT).show();
            //TODO:STRING/PICTURE
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "Я пользуюсь приложением PlaceMe! Присоединяйся и ты: placeme.com :)");
            sendIntent.setType("text/plain");
            startActivity(Intent.createChooser(sendIntent, "Share using..."));
            /*mStorageRef = FirebaseStorage.getInstance().getReference();
            Intent intent = new Intent(Intent.ACTION_PICK);

            intent.setType("image/*");
            startActivityForResult(intent, GALLERY_INTENT);
            */

        } else if (id == R.id.nav_exit) {
            AuthorizationUtils.setLoggedOut(MainActivity.this);
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
        //Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    public AlertDialog createAlertDialogNewPlace(final LatLng latLng) {
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

    public AlertDialog createNewPlace(final LatLng latLng) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        //LayoutInflater inflater = MainActivity.this.getLayoutInflater();
        LayoutInflater inflater = (LayoutInflater) MainActivity.this.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        final View layout = inflater.inflate(R.layout.dialog_new_place, null);

        iv = (ImageView) layout.findViewById(R.id.new_place_image);
        iv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //Toast.makeText(context, "ttt", Toast.LENGTH_LONG).show();
                mStorageRef = FirebaseStorage.getInstance().getReference();
                Intent intent = new Intent(Intent.ACTION_PICK);

                intent.setType("image/*");
                //intent.se
                //Log.d("sdvsvd", intent.getType());
                startActivityForResult(intent, GALLERY_INTENT);

                //context.
                //context.startActivity(intent);
                //context.getActivity().startActivityForResult(context, intent, GALLERY_INTENT);
            }
        });

        builder.setPositiveButton("Finish", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                edName = (EditText) layout.findViewById(R.id.place_name);
                edDescription = (EditText) layout.findViewById(R.id.place_description);
                edTags = (EditText) layout.findViewById(R.id.place_tags);

                if(uri == null)
                    Log.d("uri", "NULL");
                else
                    Log.d("uri", uri.toString());

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

                        StorageReference child = mStorageRef.child("photos").child(iid.toString() + "place_photo");

                        child.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                //Toast.makeText(getApplicationContext(), "Ok upload",
                                // Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                //Toast.makeText(getApplicationContext(), "Fail upload",
                                //   Toast.LENGTH_SHORT).show();
                            }
                        });
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


    public void showDescriptionDialog(final Context context, final Marker marker) {
        mBase = FirebaseDatabase.getInstance();
        mDatabaseReference = mBase.getReference().child("places");
        ChildEventListener childEventListener1 = new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Place place = (Place) dataSnapshot.getValue(Place.class);
                if ((place.getLatitude() == marker.getPosition().latitude) && (place.getLongitude() == marker.getPosition().longitude)) {
                    //Toast.makeText(MainActivity.this, place.getDescription(), Toast.LENGTH_SHORT).show();
                    AlertDialog alert = createAlertDescriptionDialog(place);
                    alert.show();
                }
            }

            @Override public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
            @Override public void onChildRemoved(DataSnapshot dataSnapshot) {}
            @Override public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            @Override public void onCancelled(DatabaseError databaseError) {}
        };
        mDatabaseReference.addChildEventListener(childEventListener1);
    }

    public AlertDialog createAlertDescriptionDialog(final Place place) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater inflater = (LayoutInflater) MainActivity.this.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        final View layout = inflater.inflate(R.layout.dialog_description, null);

        builder.setTitle(place.getName());
        //builder.setMessage(place.getDescription());
        TextView descriptionText = (TextView) layout.findViewById(R.id.descriptionText);
        descriptionText.setText(place.getDescription());

        //StorageReference gsReference = storage.getReferenceFromUrl("gs://bucket/images/stars.jpg");
        StorageReference child = mStorageRef.child("photos").child(((Integer)place.getId()).toString() + "place_photo");
        final ImageView imgView = (ImageView) layout.findViewById(R.id.description_picture);
        child.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.with(MainActivity.this).load(uri)
                        .placeholder(android.R.drawable.btn_star_big_on)
                        .error(android.R.drawable.btn_star_big_on)
                        .into(imgView);

            }
        });
        RatingBar rb = (RatingBar) layout.findViewById(R.id.total_rating);
        rb.setRating(place.getMark());
        builder.setPositiveButton("Go here!", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                //TODO: build root
                Toast.makeText(MainActivity.this, "TODO: build root", Toast.LENGTH_LONG).show();
            }
        }).setNeutralButton("Rate place!",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        AlertDialog alert = AlertDialogCreator.createAlertRateDialog(place,MainActivity.this);
                        alert.show();

                    }
                }).setNegativeButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                dialog.cancel();
            }
        });
        builder.setCancelable(true);
        builder.setView(layout);
        return builder.create();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_INTENT && resultCode == RESULT_OK) {
            uri = data.getData();
            iv.setImageURI(uri);
            /*for (int i = 0; i < 30; i++) {
                StorageReference child = mStorageRef.child("avatars").child(((Integer)i).toString() + "avatar");

            child.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //Toast.makeText(getApplicationContext(), "Ok upload",
                    // Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    //Toast.makeText(getApplicationContext(), "Fail upload",
                    //   Toast.LENGTH_SHORT).show();
                }
            });
            }*/
        }

        /*if(requestCode == 3 && resultCode == RESULT_OK) {
            StorageReference child = mStorageRef.child("photos").child("1place_photo");
            child.putFile(data.getData());
        }*/
    }


    private void putLineToPoint(GL1Renderer renderer, final World world, GLFactory objectFactory, LatLng start, LatLng finish) {
        double maxx = Math.max(start.latitude, finish.latitude);
        double maxy = Math.max(start.longitude, finish.longitude);
        double minx = Math.min(start.latitude, finish.latitude);
        double miny = Math.min(start.longitude, finish.longitude);
        double dx = maxx - minx;
        double dy = maxy - miny;
        int iter = (int)(100 * Math.max(dx, dy)) + 3;
        if(dx * dx + dy * dy < 1e-14) {
            iter = 1;
        }
        for (int i = 0; i <= iter; i++) {
            Location l = new Location("");
            l.setLatitude(start.latitude + (finish.latitude - start.latitude) * i / iter);
            l.setLongitude(start.longitude + (finish.longitude - start.longitude) * i / iter);
            GeoObj next = new GeoObj(l);
            next.setComp(objectFactory.newArrow());

            world.add(next);
        }
    }

    private void testCam() {
        Log.d("fffgggggggggggff", "asfdeabbe");
        Button b = findViewById(R.id.button4);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("ffffffffffffffff", "asfdeabbe");
                //ArActivity.startWithSetup(MainActivity.this, new CollectItemsSetup());
                //DefaultARSetup
                ArActivity.startWithSetup(MainActivity.this, new MySetup() {
                    @Override
                    public void addObjectsTo(GL1Renderer renderer, final World world, GLFactory objectFactory) {
                        //Obj grid = new Obj();
                        //MeshComponent gridMesh = objectFactory.newGrid(Color.blue(), 1, 100);
                        //grid.setComp(objectFactory.newGrid(Color.blue(), 1, 20));
                        //world.add(grid);

                        points = AlertDialogCreator.getPoints();
                        if(points == null) {
                            Log.d("kek lol", "arbidol");
                        }
                        else {
                            if (points.size() == 0) {
                                Log.d("mdaaaaa", "vot neudacha");
                            } else {
                                putLineToPoint(renderer, world, objectFactory, new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()), points.get(0));

                                for (int i = 0; i < points.size() - 1; i++) {
                                    putLineToPoint(renderer, world, objectFactory, points.get(i), points.get(i + 1));
                                }
                                Location ls = new Location("");
                                LatLng end = points.get(points.size() - 1);
                                ls.setLatitude(end.latitude);
                                ls.setLongitude(end.longitude);
                                GeoObj endObj = new GeoObj(ls);
                                endObj.setComp(objectFactory.newArrow());
                                world.add(endObj);
                            }
                        }
                        //world.add(tm);


                        //Location l = new Location("");
                        //l.setLatitude(59.9473787);
                        //l.setLongitude(30.2621547);
                        //world.update();
                    }
                });
            }
        });
    }

}
