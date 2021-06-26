package org.udg.pds.todoandroid.fragment;

import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.SellApp;
import org.udg.pds.todoandroid.entity.User;
import org.udg.pds.todoandroid.rest.TodoApi;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileMapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileMapFragment extends SupportMapFragment implements OnMapReadyCallback {

    TodoApi mTodoService;
    LatLng latLng;
    String loc;

    public ProfileMapFragment(String location){
        loc = location;
    }

    private LatLng getCoordinates() {

        final LatLng[] latLng = new LatLng[1];
        latLng[0] = new LatLng(41.9831085, 2.82493);

        if(loc.contentEquals("Girona"))  latLng[0] = new LatLng(41.9831085, 2.82493);
        else if (loc.contentEquals("Lleida")) latLng[0] = new LatLng(41.6167412, 0.62218);
        else if(loc.contentEquals("Barcelona")) latLng[0] = new LatLng(41.3887901, 2.1589899);
        else if(loc.contentEquals("Tarragona")) latLng[0] = new LatLng(41.1166687, 1.25);
        else if (loc == null)
            Toast.makeText(ProfileMapFragment.this.getContext(), "Error Getting User Data", Toast.LENGTH_SHORT).show();

        return latLng[0];
    }

    @Override
    public void onStart() {
        super.onStart();
        mTodoService = ((SellApp) this.getActivity().getApplication()).getAPI();
        latLng = getCoordinates();
        getMapAsync(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);

        return rootView;
    }

    @Override
    public void onMapReady(GoogleMap map) {

        float zoom = 10;
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        map.addMarker(new MarkerOptions().position(latLng));

        // Other configurations
        UiSettings settings = map.getUiSettings();

        settings.setMapToolbarEnabled(true);
        settings.setZoomControlsEnabled(true);
        settings.setAllGesturesEnabled(true);
    }
}
