package placeme.ru.placemedemo;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

    public static AlertDialog createAlertDialogFinded (final Context context, final String toFind) {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(context);
        builderSingle.setIcon(R.drawable.icon);
        builderSingle.setTitle("Results of query");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, android.R.layout.select_dialog_singlechoice);
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

                } else {
                    for (String tag : place.getTags().split(",")) {
                        if (toFind.equals(tag)) {
                            arrayAdapter.add(place.getName());
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

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String strName = arrayAdapter.getItem(which);
                AlertDialog.Builder builderInner = new AlertDialog.Builder(context);
                builderInner.setMessage(strName);
                builderInner.setTitle("Do you want to go there?");
                builderInner.setPositiveButton("Yes!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,int which) {
                        //TODO: build root
                        Toast.makeText(context, "TODO: build root", Toast.LENGTH_LONG).show();
                    }
                });
                builderInner.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builderInner.show();
            }
        });
        builderSingle.show();
        return builderSingle.create();
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
        ad.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {

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
