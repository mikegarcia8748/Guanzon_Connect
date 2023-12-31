package org.guanzongroup.com.creditapp.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.guanzongroup.com.creditapp.R;
import org.json.JSONArray;
import org.rmj.g3appdriver.dev.Database.DataAccessObject.DProduct;
import org.rmj.g3appdriver.etc.CashFormatter;

import java.util.List;

public class Adapter_LoanProductList extends RecyclerView.Adapter<Adapter_LoanProductList.ViewHolderItem> {

    private final List<DProduct.oProduct> poProdcts;
    private List<DProduct.oProduct> poFilter;
    private final OnItemClick poCallBck;

    public Adapter_LoanProductList(List<DProduct.oProduct> foProdcts, OnItemClick foCallBck){
        this.poProdcts = foProdcts;
        this.poFilter = poProdcts;
        this.poCallBck = foCallBck;
    }

    @NonNull
    @Override
    public ViewHolderItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View viewItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_loan_product_list, parent, false);
        return new ViewHolderItem(viewItem, poCallBck);
    }

    @Override
    public void onBindViewHolder(ViewHolderItem holder, int position) {
        try {
            DProduct.oProduct loProduct = poProdcts.get(position);
            holder.sListIdxx = loProduct.sProdctID;
            holder.BrandNme = loProduct.xBrandNme;
            holder.ModelIDx = loProduct.sModelIDx;
            holder.txtProdNm.setText(loProduct.sProdctNm);
            holder.setImage(loProduct.sImagesxx);
            holder.txtPricex.setText(CashFormatter.parse(loProduct.sPricexxx));
            // TODO: Set product image url ~> Picasso.get().load(stringUrl).into(holder.imgProdct);
            // TODO: Display promo banner if there is any (8:1 aspect ratio)
//            boolean isThereAPromoForThisItem = true;
//            if(isThereAPromoForThisItem) {
//                holder.imgPromox.setVisibility(View.VISIBLE);
//                Picasso.get().load(stringUrl).into(holder.imgPromox);
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return poProdcts.size();
    }

    public static class ViewHolderItem extends RecyclerView.ViewHolder{

        public String sListIdxx = "", BrandNme = "", ModelIDx = "";
        public ImageView imgProdct, imgPromox;
        public TextView txtProdNm, txtPricex, btnAppLoan;

        public ViewHolderItem(@NonNull View itemView, OnItemClick foCallBck) {
            super(itemView);
            imgProdct = itemView.findViewById(R.id.img_product);
            imgPromox = itemView.findViewById(R.id.imgPromox);
            txtProdNm = itemView.findViewById(R.id.txt_product_name);
            txtPricex = itemView.findViewById(R.id.txt_product_price);
            btnAppLoan = itemView.findViewById(R.id.btnAppLoan);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if(position != RecyclerView.NO_POSITION) {
                    foCallBck.onClick(sListIdxx);
                }
            });

            btnAppLoan.setOnClickListener(view -> {
                foCallBck.onApplyLoanClick(sListIdxx, BrandNme, ModelIDx);
            });
        }

        public void setImage(String image){
            try {
                JSONArray laJson = new JSONArray(image);
                String lsImgVal = laJson.getJSONObject(0).getString("sImageURL");
                Picasso.get().load(lsImgVal).placeholder(R.drawable.ic_no_image_available)
                        .error(R.drawable.ic_no_image_available).into(imgProdct);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public interface OnItemClick {
        void onClick(String fsListIdx);
        void onApplyLoanClick(String fsListIdx, String BrandNme, String StockID);
    }
}