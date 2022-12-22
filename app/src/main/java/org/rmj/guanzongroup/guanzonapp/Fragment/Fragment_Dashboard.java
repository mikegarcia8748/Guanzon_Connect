package org.rmj.guanzongroup.guanzonapp.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.rmj.g3appdriver.etc.FragmentAdapter;
import org.rmj.guanzongroup.digitalgcard.Fragment.Fragment_MyGcard;
import org.rmj.guanzongroup.guanzonapp.R;
import org.rmj.guanzongroup.marketplace.Fragment.Fragment_MPItemCart;
import org.rmj.guanzongroup.notifications.Fragment.Fragment_Notifications;
import org.rmj.guanzongroup.notifications.Fragment.Fragment_Promotion;
import org.rmj.guanzongroup.panalo.Fragment.Fragment_Panalo;
import org.rmj.guanzongroup.panalo.ViewModel.VMPanaloDashboard;

public class Fragment_Dashboard extends Fragment {
    private static final String TAG = Fragment_Dashboard.class.getSimpleName();

    private View view;
    private BottomNavigationView botNav;
    private ViewPager viewPager;

    public static Fragment_Dashboard newInstance() {
        return new Fragment_Dashboard();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        initViews();

        setupPages();
        botNav.setBackground(null);
        botNav.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_home) {
                viewPager.setCurrentItem(0);
                Log.d(TAG, "Home Selected.");
            } else if(item.getItemId() == R.id.nav_MpItemCart){
                viewPager.setCurrentItem(3);
                Log.d(TAG, "Promotions Selected.");
            } else if(item.getItemId() == R.id.nav_promos){
                viewPager.setCurrentItem(1);
                Log.d(TAG, "Guanzon Panalo Selected.");
            } else if(item.getItemId() == R.id.nav_MyGcard) {
                viewPager.setCurrentItem(4);
                Log.d(TAG, "My G-Card Selected.");
            } else if(item.getItemId() == R.id.nav_Panalo) {
                viewPager.setCurrentItem(2);
                Log.d(TAG, "Guanzon Panalo Selected.");
            }
            return true;
        });

        return view;
    }


    private void initViews() {
        viewPager = view.findViewById(R.id.viewpager);
        botNav = view.findViewById(R.id.bottom_navigation);
    }

    private void setupPages(){
        Fragment[] loFragments = new Fragment[]{
                new Fragment_Home(),
                new Fragment_Promotion(),
                new Fragment_Panalo(),
                new Fragment_MPItemCart(),
                new Fragment_MyGcard()};
        FragmentAdapter loAdapter = new FragmentAdapter(getChildFragmentManager(), loFragments);
        viewPager.setAdapter(loAdapter);
    }
}