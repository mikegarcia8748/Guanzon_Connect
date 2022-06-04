package org.rmj.guanzongroup.notifications.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import org.json.JSONObject;
import org.rmj.g3appdriver.dev.Database.DataAccessObject.DNotifications;
import org.rmj.g3appdriver.etc.FragmentAdapter;
import org.rmj.g3appdriver.etc.NonSwipeableViewPager;
import org.rmj.guanzongroup.marketplace.Activity.Activity_Purchases;
import org.rmj.guanzongroup.marketplace.Fragment.Fragment_OrderStatus;
import org.rmj.guanzongroup.notifications.Fragment.Fragment_ActionNotification;
import org.rmj.guanzongroup.notifications.Fragment.Fragment_EventsPromos;
import org.rmj.guanzongroup.notifications.Fragment.Fragment_ProductInquiry;
import org.rmj.guanzongroup.notifications.Fragment.Fragment_ViewMessage;
import org.rmj.guanzongroup.notifications.R;
import org.rmj.guanzongroup.notifications.ViewModel.VMViewNotification;

import java.util.Objects;

public class Activity_ViewNotification extends AppCompatActivity {

    private Toolbar toolbar;
    private NonSwipeableViewPager viewPager;
    private VMViewNotification mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_notification);
        mViewModel = new ViewModelProvider(Activity_ViewNotification.this).get(VMViewNotification.class);
        toolbar = findViewById(R.id.toolbar_notification);
        viewPager = findViewById(R.id.viewpager_notification);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        if(getIntent().hasExtra("sMsgIDxxx")){
            mViewModel.SendReadResponse(getIntent().getStringExtra("sMsgIDxxx"));
            mViewModel.GetNotificationInfo(getIntent().getStringExtra("sMsgIDxxx")).observe(Activity_ViewNotification.this, clientNotificationInfo -> {
                try{
                    Bundle loBundle;
                    switch (clientNotificationInfo.MsgTypex) {
                        case "00000":   //Regular Message
                            viewPager.setAdapter(new FragmentAdapter(getSupportFragmentManager(), new Fragment[]{new Fragment_ViewMessage()}));
                            break;
                        case "00002":   //GCard Registered on Different
                            viewPager.setAdapter(new FragmentAdapter(getSupportFragmentManager(), new Fragment[]{new Fragment_ActionNotification()}));
                            break;
                        case "00004":   //Registration OR/CR Process Status
                            viewPager.setAdapter(new FragmentAdapter(getSupportFragmentManager(), new Fragment[]{new Fragment_ViewMessage()}));
                            break;
                        case "00005":   //GCard Pre-Order
                            viewPager.setAdapter(new FragmentAdapter(getSupportFragmentManager(), new Fragment[]{new Fragment_ViewMessage()}));
                            break;
                        case "00006":   //MarketPlace Order Status
                            JSONObject loJson = new JSONObject(clientNotificationInfo.DataInfo);
                            String lsOrderIDx = loJson.getJSONObject("data").getString("sOrderIDx");
                            Intent loIntent = new Intent(Activity_ViewNotification.this, Activity_Purchases.class);
                            loIntent.putExtra("sOrderIDx", lsOrderIDx);
                            startActivity(loIntent);
                            finish();
                            break;
                        case "00007":   //Guanzon Promotions
                            Fragment_EventsPromos loPromos = new Fragment_EventsPromos();
                            loBundle = new Bundle();
                            loPromos.setArguments(loBundle);
                            viewPager.setAdapter(new FragmentAdapter(getSupportFragmentManager(), new Fragment[]{loPromos}));
                            break;
                        case "00008":   //Guanzon Events
                            Fragment_EventsPromos loEvent = new Fragment_EventsPromos();
                            loBundle = new Bundle();
                            loEvent.setArguments(loBundle);
                            viewPager.setAdapter(new FragmentAdapter(getSupportFragmentManager(), new Fragment[]{new Fragment_EventsPromos()}));
                            break;
                        case "00009":   //MarketPlace Q&A
                            viewPager.setAdapter(new FragmentAdapter(getSupportFragmentManager(), new Fragment[]{new Fragment_ViewMessage()}));
                            break;
                        case "00011":   //GuanzonApp Product Inquiry
                            viewPager.setAdapter(new FragmentAdapter(getSupportFragmentManager(), new Fragment[]{new Fragment_ProductInquiry()}));
                            break;
                        default:
                            viewPager.setAdapter(new FragmentAdapter(getSupportFragmentManager(), new Fragment[]{new Fragment_ViewMessage()}));
                            break;
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}