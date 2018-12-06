package duyliem.photoviewer.Collection;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import duyliem.photoviewer.Object.Photo;
import duyliem.photoviewer.R;

/**
 * Created by Duy Liem on 19/10/2018.
 */

public class FragmentCollection extends Fragment {

    ListView lv;
    ArrayList<Collection> collections;
    ListCollectionAdapter listCollectionAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_collection, container, false);

        lv = view.findViewById(R.id.lvAlbum);

        collections = new ArrayList<>();
        listCollectionAdapter = new ListCollectionAdapter(collections);
        lv.setAdapter(listCollectionAdapter);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Collection");

        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Collection collection = dataSnapshot.getValue(Collection.class);
                collections.add(new Collection(collection.name, collection.links));

                listCollectionAdapter.notifyDataSetChanged();

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getContext(), ShowCollection.class);
                String nameAlbum = collections.get(position).name;
                intent.putExtra("nameAlbum", nameAlbum);
                startActivity(intent);
            }
        });
        return view;
    }
}
