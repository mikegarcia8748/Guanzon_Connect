package org.rmj.guanzongroup.marketplace.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import org.rmj.g3appdriver.etc.CashFormatter;
import org.rmj.guanzongroup.marketplace.R;
import org.rmj.guanzongroup.marketplace.ViewModel.VMProductOverview;

import java.util.Objects;

public class Activity_ProductOverview extends AppCompatActivity {
    private VMProductOverview mViewModel;
    private Toolbar toolbar;
    private TextView txtProdNm, txtUntPrc, txtSoldQt, txtBrandx, txtCatgry, txtColorx, txtStocks,
            txtBriefx;

    private String psItemIdx = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_overview);
        mViewModel = new ViewModelProvider(Activity_ProductOverview.this)
                .get(VMProductOverview.class);
        getExtras();
        initViews();
        setUpToolbar();
        displayData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.order_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void getExtras() {
        psItemIdx = getIntent().getStringExtra("sListingId");
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        txtProdNm = findViewById(R.id.txt_product_name);
        txtUntPrc = findViewById(R.id.txt_product_price);
        txtSoldQt = findViewById(R.id.txt_product_sold_count);
        txtBrandx = findViewById(R.id.txt_brand_name);
        txtCatgry = findViewById(R.id.txt_category);
        txtColorx = findViewById(R.id.txt_variant);
        txtStocks = findViewById(R.id.txt_stocks);
        txtBriefx = findViewById(R.id.txt_brief_desc);
    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
    }

    private void displayData() {
        mViewModel.getProductInfo(psItemIdx).observe(Activity_ProductOverview.this, product -> {
            try {
                txtProdNm.setText(Objects.requireNonNull(product.getModelNme()));
                txtUntPrc.setText(CashFormatter.parse(Objects.requireNonNull(product.getUnitPrce())));
                txtSoldQt.setText(Objects.requireNonNull(product.getSoldQtyx()) + " Sold");
                txtBrandx.setText(Objects.requireNonNull(product.getBrandNme()));
                txtCatgry.setText(Objects.requireNonNull(product.getCategrNm()));
                txtColorx.setText(Objects.requireNonNull(product.getColorNme()));
                txtStocks.setText(Objects.requireNonNull(product.getQtyOnHnd()));
                txtBriefx.setText(Objects.requireNonNull(product.getBriefDsc()));
            } catch (NullPointerException e) {
                e.printStackTrace();
                finish();
            }
        });
    }

}