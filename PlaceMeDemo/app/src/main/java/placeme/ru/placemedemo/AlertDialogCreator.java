package placeme.ru.placemedemo;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Leg;
import com.akexorcist.googledirection.model.Route;
import com.akexorcist.googledirection.model.Step;
import com.akexorcist.googledirection.request.DirectionDestinationRequest;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by Андрей on 21.11.2017.
 */

public class AlertDialogCreator {
    private static FirebaseDatabase mBase;
    private static DatabaseReference mDatabaseReference;
    private static ChildEventListener childEventListener;

    private static Integer iid;
    private static EditText edName;
    private static EditText edDescription;
    private static EditText edTags;

    public static AlertDialog createAlertDialogFinded (final Context context, final String toFind, final GoogleMap googleMap, final LatLng myPosition) {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        final View layout = inflater.inflate(R.layout.list, null);
        builderSingle.setView(layout);
        builderSingle.setIcon(R.drawable.icon);
        builderSingle.setTitle("Results of query");
        final ListView lv = (ListView) layout.findViewById(R.id.lv);
        lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, android.R.layout.select_dialog_multichoice);
        final ArrayList<Place> placeArrayList = new ArrayList<>();

        FirebaseDatabase mBase;
        DatabaseReference mDatabaseReference;
        ChildEventListener childEventListener;
        mBase = FirebaseDatabase.getInstance();
        mDatabaseReference = mBase.getReference().child("places");
        childEventListener = new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Place place = (Place) dataSnapshot.getValue(Place.class);
                if (place.getName().indexOf(toFind) != -1) {
                    arrayAdapter.add(place.getName());
                    placeArrayList.add(place);

                } else {
                    for (String tag : place.getTags().split(",")) {
                        if (toFind.equals(tag)) {
                            arrayAdapter.add(place.getName());
                            placeArrayList.add(place);
                            break;
                        }
                    }
                }
            }

            @Override public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
            @Override public void onChildRemoved(DataSnapshot dataSnapshot) {}
            @Override public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            @Override public void onCancelled(DatabaseError databaseError) {}

        };
        mDatabaseReference.addChildEventListener(childEventListener);

        builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        lv.setAdapter(arrayAdapter);

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String strName = arrayAdapter.getItem(position);
                AlertDialog.Builder builderInner = new AlertDialog.Builder(context);
                builderInner.setMessage(placeArrayList.get(position).getDescription());
                builderInner.setTitle(placeArrayList.get(position).getName());
                builderInner.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,int which) {
                        dialog.dismiss();
                    }
                });
                builderInner.show();
                return false;
            }
        });

        builderSingle.setPositiveButton("Make route", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SparseBooleanArray sp=lv.getCheckedItemPositions();

                final LatLng origin = myPosition;
                LatLng destination = myPosition;
                DirectionDestinationRequest gd = GoogleDirection.withServerKey("AIzaSyD_WcUAMqVEVW0H84GsXLKBr0HokiO-v_4").from(origin);
                int lastPoint = -1;
                for(int i=0;i<placeArrayList.size();i++) {
                    if(sp.get(i)) {
                        lastPoint = i;
                    }
                    //Log.d(((Integer)i).toString(), ((Boolean)sp.get(i)).toString());
                }

                if(lastPoint != -1) {
                    destination = new LatLng(placeArrayList.get(lastPoint).getLatitude(), placeArrayList.get(lastPoint).getLongitude());
                }

                for(int i=0;i<placeArrayList.size();i++) {
                    if(sp.get(i)) {
                        if(i != lastPoint) {
                            gd.and(new LatLng(placeArrayList.get(i).getLatitude(), placeArrayList.get(i).getLongitude()));
                        }
                    }
                    Log.d(((Integer)i).toString(), ((Boolean)sp.get(i)).toString());
                }

                gd.to(destination)
                        .transportMode(TransportMode.DRIVING)
                        .execute(new DirectionCallback() {
                            @Override
                            public void onDirectionSuccess(Direction direction, String rawBody) {
                                if(direction.isOK()) {

                                    Route route = direction.getRouteList().get(0);
                                    int legCount = route.getLegList().size();
                                    for (int index = 0; index < legCount; index++) {
                                        Leg leg = route.getLegList().get(index);
                                        googleMap.addMarker(new MarkerOptions().position(leg.getStartLocation().getCoordination()));
                                        if (index == legCount - 1) {
                                            googleMap.addMarker(new MarkerOptions().position(leg.getEndLocation().getCoordination()));
                                        }
                                        List<Step> stepList = leg.getStepList();
                                        ArrayList<PolylineOptions> polylineOptionList = DirectionConverter.createTransitPolyline(context, stepList, 5, Color.RED, 3, Color.BLUE);
                                        for (PolylineOptions polylineOption : polylineOptionList) {
                                            googleMap.addPolyline(polylineOption);
                                        }
                                    }
                                    // Do something
                                } else {
                                    // Do something
                                }
                            }

                            @Override
                            public void onDirectionFailure(Throwable t) {
                                // Do something
                            }
                        });

                String str="";
                for(int i=0;i<placeArrayList.size();i++)
                {
                    Log.d(((Integer)i).toString(), ((Boolean)sp.get(i)).toString());
                }
                Toast.makeText(context, ""+str, Toast.LENGTH_SHORT).show();
            }
        });

        return builderSingle.create();
    }

    private static void setCameraWithCoordinationBounds(Route route, GoogleMap googleMap) {
        LatLng southwest = route.getBound().getSouthwestCoordination().getCoordination();
        LatLng northeast = route.getBound().getNortheastCoordination().getCoordination();
        LatLngBounds bounds = new LatLngBounds(southwest, northeast);
        googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
    }

    public static void showDescriptionDialog(final Context context, final Marker marker) {
        mBase = FirebaseDatabase.getInstance();
        mDatabaseReference = mBase.getReference().child("places");
        ChildEventListener childEventListener1 = new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Place place = (Place) dataSnapshot.getValue(Place.class);
                if ((place.getLatitude() == marker.getPosition().latitude) && (place.getLongitude() == marker.getPosition().longitude)) {
                    AlertDialog alert = AlertDialogCreator.createAlertDescriptionDialog(place, context);
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

    public static AlertDialog createAlertDescriptionDialog(final Place place, final Context context) {
        AlertDialog.Builder ad;
        ad = new AlertDialog.Builder(context);
        ad.setTitle(place.getName());  // заголовок
        ad.setMessage(place.getDescription()); // сообщение
        ad.setPositiveButton("Go here!", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                //TODO: build root
                Toast.makeText(context, "TODO: build root", Toast.LENGTH_LONG).show();
            }
        });
        ad.setNeutralButton("Add to favourite",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //dialog.cancel();

                        mBase = FirebaseDatabase.getInstance();
                        mDatabaseReference = mBase.getReference().child("users").child(((Integer)LoginUtility.getLoggedIn(context)).toString()).child("favouritePlaces");
                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Log.d("PLACES", dataSnapshot.getValue().toString());
                                String[] places = dataSnapshot.getValue().toString().split(",");
                                int flag = 0;
                                for(String str : places) {
                                    if(str.equals(((Integer)place.getId()).toString())) {
                                        flag = 1;
                                    }
                                }

                                if(flag == 0) {
                                    String newFavourite = dataSnapshot.getValue().toString() + "," + ((Integer)place.getId()).toString();
                                    DatabaseReference mDatabaseReference1 = mBase.getReference().child("users").child(((Integer)LoginUtility.getLoggedIn(context)).toString()).child("favouritePlaces");
                                    mDatabaseReference1.setValue(newFavourite);

                                }
                            }

                            @Override
                            public void onCancelled( DatabaseError firebaseError) {

                                Log.d("User", "-1" );
                            }
                        });

                    }
                });
        ad.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                dialog.cancel();
            }
        });
        ad.setCancelable(true);
        return ad.create();
    }

    public static AlertDialog createAlertDialog(final LatLng latLng, final Context context) {
        AlertDialog.Builder ad;
        ad = new AlertDialog.Builder(context);
        ad.setTitle("Creating place");  // заголовок
        ad.setMessage("Create new place here?"); // сообщение
        ad.setPositiveButton("Yes!", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                //Toast.makeText(MainActivity.this, "TODO: creating place", Toast.LENGTH_LONG).show();
                AlertDialog alertNewPlace = createNewPlace(latLng, context);
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

    public static AlertDialog createNewPlace(final LatLng latLng, final Context context) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        //LayoutInflater inflater = MainActivity.this.getLayoutInflater();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
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
}
