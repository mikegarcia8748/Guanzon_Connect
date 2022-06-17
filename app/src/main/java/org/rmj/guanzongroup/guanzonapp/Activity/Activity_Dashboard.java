package org.rmj.guanzongroup.guanzonapp.Activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.badge.BadgeUtils;
import com.google.android.material.navigation.NavigationView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import org.rmj.g3appdriver.lib.GCardCore.GCardSystem;
import org.rmj.g3appdriver.lib.GCardCore.iGCardSystem;
import org.rmj.g3appdriver.utils.Dialogs.Dialog_DoubleButton;
import org.rmj.g3appdriver.utils.Dialogs.Dialog_Loading;
import org.rmj.g3appdriver.utils.Dialogs.Dialog_SingleButton;
import org.rmj.guanzongroup.digitalgcard.Activity.Activity_QrCodeScanner;
import org.rmj.guanzongroup.guanzonapp.R;
import org.rmj.guanzongroup.guanzonapp.Service.OnLoginReceiver;
import org.rmj.guanzongroup.marketplace.Activity.Activity_ItemCart;
import org.rmj.guanzongroup.marketplace.ViewModel.VMHome;
import org.rmj.guanzongroup.guanzonapp.databinding.ActivityDashboardBinding;
import org.rmj.guanzongroup.marketplace.Activity.Activity_SearchItem;
import org.rmj.guanzongroup.notifications.Activity.Activity_NotificationList;
import org.rmj.guanzongroup.useraccount.Activity.Activity_Login;
import org.rmj.guanzongroup.useraccount.Activity.Activity_SignUp;

import java.util.Objects;

public class Activity_Dashboard extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityDashboardBinding binding;
    private NavigationView navigationView;
    private VMHome mViewModel;
    private LayoutInflater loInflate;

    private Toolbar toolbar;
    private BadgeDrawable loBadge;
    private TextView lblBadge;
    private static final int SCAN_GCARD = 1;

    private final OnLoginReceiver poLogRcv = new OnLoginReceiver();

    private final ActivityResultLauncher<Intent> poArl = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if(result.getResultCode() == SCAN_GCARD) {
                    Intent loIntent = result.getData();
                    if (loIntent != null) {
                        Toast.makeText(Activity_Dashboard.this, loIntent.getStringExtra("result"), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(Activity_Dashboard.this, "No data result receive.", Toast.LENGTH_LONG).show();
                    }
                }
            }
    );

    @SuppressLint("UnsafeOptInUsageError")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashboardBinding.inflate(getLayoutInflater());
        mViewModel = new ViewModelProvider(Activity_Dashboard.this).get(VMHome.class);
        toolbar = findViewById(R.id.toolbar);
        setContentView(binding.getRoot());
        setSupportActionBar(binding.appBarActivityDashboard.toolbar);

        DrawerLayout drawer = binding.drawerLayout;
        navigationView = binding.navView;

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home,
                R.id.nav_promos,
                R.id.nav_events,
                R.id.nav_purchases,
                R.id.nav_wishlist,
                R.id.nav_item_cart,
                R.id.nav_my_gcard,
                R.id.nav_scan_qrcode,
                R.id.nav_redeemables,
                R.id.nav_gcard_orders,
                R.id.nav_gcard_transactions,
                R.id.nav_pre_termination,
                R.id.nav_account_settings,
                R.id.nav_find_us,
                R.id.nav_customer_service,
                R.id.nav_logout)
                .setOpenableLayout(drawer)
                .build();

        mViewModel.GetActiveGCard().observe(Activity_Dashboard.this, eGcardApp -> {
            try {
                navigationView = (NavigationView) findViewById(R.id.nav_view);
                Menu nav_Menu = navigationView.getMenu();

                //Disable Pre-Termination page untill project is develop...
                nav_Menu.findItem(R.id.nav_wishlist).setVisible(false);
                nav_Menu.findItem(R.id.nav_pre_termination).setVisible(false);
                nav_Menu.findItem(R.id.nav_customer_service).setVisible(false);
                if (eGcardApp == null) {
                    nav_Menu.findItem(R.id.nav_redeemables).setVisible(false);
                    nav_Menu.findItem(R.id.nav_gcard_orders).setVisible(false);
                    nav_Menu.findItem(R.id.nav_gcard_transactions).setVisible(false);
                    nav_Menu.findItem(R.id.nav_pre_termination).setVisible(false);
                } else {
                    nav_Menu.findItem(R.id.nav_redeemables).setVisible(true);
                    nav_Menu.findItem(R.id.nav_gcard_orders).setVisible(true);
                    nav_Menu.findItem(R.id.nav_gcard_transactions).setVisible(true);
                    nav_Menu.findItem(R.id.nav_pre_termination).setVisible(true);
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        });

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_activity_dashboard);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        loInflate = LayoutInflater.from(Activity_Dashboard.this);

        mViewModel.GetUnreadMessagesCount().observe(Activity_Dashboard.this, count -> {
//            try{
//                loBadge = BadgeDrawable.create(Activity_Dashboard.this);
//                if(count > 0) {
//                    loBadge.setNumber(count);
//                    BadgeUtils.attachBadgeDrawable(loBadge, toolbar, R.id.item_notifications);
//                } else {
//                    BadgeUtils.detachBadgeDrawable(loBadge, toolbar, R.id.item_notifications);
//                }
//            } catch (Exception e){
//                e.printStackTrace();
//            }
        });

        mViewModel.GetCartItemCount().observe(Activity_Dashboard.this, count -> {
//            try {
//                loBadge = BadgeDrawable.create(Activity_Dashboard.this);
//                if(count > 0) {
//                    loBadge.setNumber(count);
//                    BadgeUtils.attachBadgeDrawable(loBadge, toolbar, R.id.item_cart);
//                } else {
//                    BadgeUtils.detachBadgeDrawable(loBadge, toolbar, R.id.item_cart);
//                }
//            } catch (Exception e){
//                e.printStackTrace();
//            }
        });

        mViewModel.GetToPayOrders().observe(Activity_Dashboard.this, count -> {
            try{
                lblBadge = (TextView) loInflate.inflate(R.layout.nav_action_badge, null, false);
                navigationView.getMenu().findItem(R.id.nav_purchases).setActionView(lblBadge);
                lblBadge.setText(GetBadgeValue(count));
            } catch (Exception e){
                e.printStackTrace();
            }
        });

        navigationView.getMenu().findItem(R.id.nav_logout).setOnMenuItemClickListener(menuItem -> {
            Dialog_DoubleButton loDialog = new Dialog_DoubleButton(Activity_Dashboard.this);
            loDialog.setButtonText("YES", "NO");
            loDialog.initDialog("Guanzon App", "Log out user session?", new Dialog_DoubleButton.OnDialogConfirmation() {
                @Override
                public void onConfirm(AlertDialog dialog) {
                    mViewModel.LogoutUserSession();
                    dialog.dismiss();
                }

                @Override
                public void onCancel(AlertDialog dialog) {
                    dialog.dismiss();
                }
            });
            loDialog.show();
            return false;
        });

        navigationView.getMenu().findItem(R.id.nav_scan_qrcode).setOnMenuItemClickListener(menuItem -> {
            Intent loIntent = new Intent(Activity_Dashboard.this, Activity_QrCodeScanner.class);
            poArl.launch(loIntent);
            return false;
        });

        navigationView.getMenu().findItem(R.id.nav_item_cart).setOnMenuItemClickListener(menuItem -> {
            Intent intent = new Intent(Activity_Dashboard.this, Activity_ItemCart.class);
            intent.putExtra("args", "1");
            startActivity(intent);
            return false;
        });

//        navigationView.getMenu().findItem(R.id.nav_purchases).setOnMenuItemClickListener(menuItem -> {
//            Intent intent = new Intent(Activity_Dashboard.this, Activity_Purchases.class);
//            startActivity(intent);
//            return false;
//        });
        setUpHeader(navigationView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_mrktplc, menu);
        // Get the SearchView and set the searchable configuration
        mViewModel.getClientInfo().observe(Activity_Dashboard.this, eClientinfo -> {
            try {
                if(eClientinfo != null){
                    menu.findItem(R.id.item_notifications).setVisible(true);
                    menu.findItem(R.id.item_cart).setVisible(true);
                } else {
                    menu.findItem(R.id.item_notifications).setVisible(false);
                    menu.findItem(R.id.item_cart).setVisible(false);
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        });
        return true;
    }

    @Override
    public void onBackPressed() {
        Dialog_DoubleButton loDialog = new Dialog_DoubleButton(Activity_Dashboard.this);
        loDialog.setButtonText("YES", "NO");
        loDialog.initDialog("Guanzon App", "Exit Guanzon App?", new Dialog_DoubleButton.OnDialogConfirmation() {
            @Override
            public void onConfirm(AlertDialog dialog) {
                dialog.dismiss();
                finish();
            }

            @Override
            public void onCancel(AlertDialog dialog) {
                dialog.dismiss();
            }
        });
        loDialog.show();
    }

    @Override
    protected void onStart() {
        IntentFilter intentFilter = new IntentFilter("android.intent.action.SUCCESS_LOGIN");
        registerReceiver(poLogRcv, intentFilter);
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(poLogRcv);
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent loIntent;
        if(item.getItemId() == android.R.id.home){

        } else if (item.getItemId() == R.id.item_search) {
            loIntent = new Intent(Activity_Dashboard.this, Activity_SearchItem.class);
            startActivity(loIntent);
        } else if (item.getItemId() == R.id.item_cart) {
            Intent intent = new Intent(Activity_Dashboard.this, Activity_ItemCart.class);
            intent.putExtra("args", "1");
            startActivity(intent);
        } else {
            startActivity(new Intent(Activity_Dashboard.this, Activity_NotificationList.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_activity_dashboard);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void setUpHeader(NavigationView foNavigxx) {
        View headerLayout = foNavigxx.getHeaderView(0);
        LinearLayout lnAuthxxx = headerLayout.findViewById(R.id.ln_authenticate);
        TextView txtSignUp = headerLayout.findViewById(R.id.lbl_Signup);
        TextView txtLoginx = headerLayout.findViewById(R.id.lbl_Login);
        TextView txtFullNm = headerLayout.findViewById(R.id.lbl_UserFullName);

        mViewModel.getClientInfo().observe(Activity_Dashboard.this, eClientinfo -> {
            try {
                Menu nav_Menu = navigationView.getMenu();
                if(eClientinfo != null) {
                    String lsFullNme;
                    if(eClientinfo.getClientID() != null) {
                        lsFullNme = eClientinfo.getFrstName() + " " + eClientinfo.getLastName();
                    } else {
                        lsFullNme = eClientinfo.getUserName();
                    }
                    lnAuthxxx.setVisibility(View.GONE);
                    txtFullNm.setVisibility(View.VISIBLE);
                    txtFullNm.setText(Objects.requireNonNull(lsFullNme));
                    nav_Menu.findItem(R.id.nav_purchases).setVisible(true);
                    nav_Menu.findItem(R.id.nav_wishlist).setVisible(true);
                    nav_Menu.findItem(R.id.nav_item_cart).setVisible(true);
                    nav_Menu.findItem(R.id.nav_logout).setVisible(true);
                } else {
                    lnAuthxxx.setVisibility(View.VISIBLE);
                    txtFullNm.setVisibility(View.GONE);
                    txtSignUp.setOnClickListener(v -> {
                        Intent loIntent = new Intent(Activity_Dashboard.this, Activity_SignUp.class);
                        startActivity(loIntent);
                    });

                    txtLoginx.setOnClickListener(v -> {
                        Intent loIntent = new Intent(Activity_Dashboard.this, Activity_Login.class);
                        startActivity(loIntent);
                    });
                    nav_Menu.findItem(R.id.nav_purchases).setVisible(false);
                    nav_Menu.findItem(R.id.nav_wishlist).setVisible(false);
                    nav_Menu.findItem(R.id.nav_item_cart).setVisible(false);
                    nav_Menu.findItem(R.id.nav_logout).setVisible(false);
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == SCAN_GCARD){
            iGCardSystem loGcard = new GCardSystem(Activity_Dashboard.this).getInstance(GCardSystem.CoreFunctions.GCARD);
            try {
                String lsVal = Objects.requireNonNull(data).getStringExtra("data");
                loGcard.ParseQrCode(lsVal, new GCardSystem.ParseQrCodeCallback() {
                    @Override
                    public void ApplicationResult(String args) {
//                        Toast.makeText(Activity_Dashboard.this, "ApplicationResult " + args, Toast.LENGTH_LONG).show();
                        //TODO : Add call GCardSystem>AddGCardQrCode()
                        //Log.e("ApplicationResult :",args);
                        //showDialog("Transaction ", args);
                        AddGCard(args);

                    }

                    @Override
                    public void TransactionResult(String args) {
                        Log.e("TransactionResult :",args);
//                        Toast.makeText(Activity_Dashboard.this, "TransactionResult" + args, Toast.LENGTH_LONG).show();
                        //TODO: Create dialog that will display the PIN. After closing the dialog, call GCardSystem>DownloadTransactions()
                        // Display message that transaction won't affect immediately on GCard Ledger.
                        showDialog("Transaction ", args);
                    }

                    @Override
                    public void OnFailed(String message) {
                        //TODO: Display error message dialog
                        Log.e("OnFailed :",message);
                        //Toast.makeText(Activity_Dashboard.this, "OnFailed " + message, Toast.LENGTH_LONG).show();
                        showDialog("Failed", message);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                showDialog("Exception", e.getMessage());
                Toast.makeText(Activity_Dashboard.this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }
    void showDialog(String title, String args){

        Dialog_SingleButton msgDialog = new Dialog_SingleButton(Activity_Dashboard.this);
        msgDialog.setButtonText("Okay");
        msgDialog.initDialog(title, args, dialog -> dialog.dismiss());
        msgDialog.show();
    }

    private String GetBadgeValue(int val){
        if(val > 0){
            lblBadge.setVisibility(View.VISIBLE);
            return String.valueOf(val);
        } else {
            lblBadge.setVisibility(View.GONE);
            return "0";
        }
    }

    public void AddGCard(String fsVal){
        Dialog_Loading loLoad = new Dialog_Loading(Activity_Dashboard.this);
        Dialog_SingleButton loMessage = new Dialog_SingleButton(Activity_Dashboard.this);
        mViewModel.AddNewGCard(fsVal, new VMHome.OnActionCallback() {
            @Override
            public void OnLoad() {
                loLoad.initDialog("Digital GCard", "Adding GCard. Please wait...");
                loLoad.show();
            }

            @Override
            public void OnSuccess(String args) {
                loLoad.dismiss();
                loMessage.setButtonText("Okay");
                loMessage.initDialog("Digital GCard", args, dialog -> dialog.dismiss());
                loMessage.show();
            }

            @Override
            public void OnFailed(String args) {
                loLoad.dismiss();
                loMessage.setButtonText("Okay");
                loMessage.initDialog("Digital GCard", args, dialog -> dialog.dismiss());
                loMessage.show();
            }
        });
    }
}