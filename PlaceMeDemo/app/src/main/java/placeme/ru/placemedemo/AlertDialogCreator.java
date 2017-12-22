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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;

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
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import placeme.ru.placemedemo.core.database.DatabaseManager;
import placeme.ru.placemedemo.core.utils.AuthorizationUtils;

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

    //image
    private static StorageReference mStorageRef;
    private static final int GALLERY_INTENT = 2;
    private static ImageView iv;

    private static ArrayList<LatLng> points = new ArrayList<>();

    public static ArrayList<LatLng> getPoints() {
        return points;
    }

    public static AlertDialog createAlertDialogFinded (final Context context, final String toFind, final GoogleMap googleMap, final LatLng myPosition) {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        final View layout = inflater.inflate(R.layout.list, null);
        builderSingle.setView(layout);
        builderSingle.setIcon(R.drawable.icon);
        builderSingle.setTitle("Results of query");
        final ListView lv = (ListView) layout.findViewById(R.id.lv);
        lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, android.R.layout.select_dialog_multichoice);
        final ArrayList<Place> placeArrayList = new ArrayList<>();
        
        DatabaseManager.findPlacesByString(arrayAdapter, placeArrayList, toFind);

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
                        .transportMode(TransportMode.WALKING)
                        .execute(new DirectionCallback() {
                            @Override
                            public void onDirectionSuccess(Direction direction, String rawBody) {
                                if(direction.isOK()) {

                                    Route route = direction.getRouteList().get(0);
                                    int legCount = route.getLegList().size();
                                    Log.d ("length", ((Integer)legCount).toString());
                                    for (int index = 0; index < legCount; index++) {
                                        Leg leg = route.getLegList().get(index);
                                        googleMap.addMarker(new MarkerOptions().position(leg.getStartLocation().getCoordination()));
                                        if (index == legCount - 1) {
                                            googleMap.addMarker(new MarkerOptions().position(leg.getEndLocation().getCoordination()));
                                        }
                                        List<Step> stepList = leg.getStepList();
                                        ArrayList<PolylineOptions> polylineOptionList = DirectionConverter.createTransitPolyline(context, stepList, 5, Color.RED, 3, Color.BLUE);
                                        //Log.d ("length", ((Integer)polylineOptionList.size()).toString());
                                        Integer cnt = 0;
                                        for (PolylineOptions polylineOption : polylineOptionList) {
                                            cnt += polylineOption.getPoints().size();
                                            points.addAll(polylineOption.getPoints());
                                            googleMap.addPolyline(polylineOption);
                                        }
                                        Log.d ("lengthfff", ((Integer)points.size()).toString());
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

    public static AlertDialog createAlertRateDialog(final Place place, final Context context) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        final View layout = inflater.inflate(R.layout.dialog_rate_place, null);

        builder.setPositiveButton("Rate it!", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                //TODO: build root
                mBase = FirebaseDatabase.getInstance();
                mDatabaseReference = mBase.getReference().child("places").child(((Integer)place.getId()).toString());
                mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        HashMap<String, Object> currentPlace = (HashMap<String, Object>) dataSnapshot.getValue();

                        RatingBar rb = (RatingBar) layout.findViewById(R.id.rate_place);
                        Float mark = rb.getRating();
                        Log.d("RATE", mark.toString());
                        //Log.d("RATE", currentPlace.get("sumOfMarks").toString());
                        Float curSum = Float.parseFloat(currentPlace.get("sumOfMarks").toString());
                        Long curNumOfMarks = Long.parseLong(currentPlace.get("numberOfRatings").toString());
                        curSum += mark;
                        curNumOfMarks++;

                        DatabaseReference mDatabaseReference1 = mBase.getReference().child("places").child(((Integer)place.getId()).toString()).child("sumOfMarks");
                        mDatabaseReference1.setValue(curSum);

                        mDatabaseReference1 = mBase.getReference().child("places").child(((Integer)place.getId()).toString()).child("numberOfRatings");
                        mDatabaseReference1.setValue(curNumOfMarks);
                    }

                    @Override
                    public void onCancelled( DatabaseError firebaseError) {

                        Log.d("User", "-1" );
                    }
                });
                //Toast.makeText(context, "TODO: rate it", Toast.LENGTH_LONG).show();
            }
        });
                builder.setNeutralButton("Add to favourite",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //dialog.cancel();

                        mBase = FirebaseDatabase.getInstance();
                        mDatabaseReference = mBase.getReference().child("users").child(AuthorizationUtils.getLoggedInAsString(context)).child("favouritePlaces");
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
                                    DatabaseReference mDatabaseReference1 = mBase.getReference().child("users").child(AuthorizationUtils.getLoggedInAsString(context)).child("favouritePlaces");
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
                        builder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                dialog.cancel();
            }
        });
        builder.setCancelable(true);
        builder.setView(layout);
        return builder.create();
    }
}
