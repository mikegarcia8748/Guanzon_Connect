package org.rmj.guanzongroup.marketplace.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.rmj.g3appdriver.dev.Database.DataAccessObject.DOrderMaster;
import org.rmj.g3appdriver.etc.CashFormatter;
import org.rmj.guanzongroup.marketplace.R;

import java.util.List;

public class Adapter_OrderHistory extends RecyclerView.Adapter<Adapter_OrderHistory.ViewHolderItem> {

    private final List<DOrderMaster.OrderHistory> poMaster;
    private final OnOrderHistoryClickListener mListener;

    public interface OnOrderHistoryClickListener{
        void OnClick(String args, String args1);
    }

    public Adapter_OrderHistory(List<DOrderMaster.OrderHistory> poMaster, OnOrderHistoryClickListener listener) {
        this.poMaster = poMaster;
        this.mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolderItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View loView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_order_history, parent, false);
        return new ViewHolderItem(loView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderItem holder, int position) {
        DOrderMaster.OrderHistory loMaster = poMaster.get(position);
        holder.lblOrderNo.setText(loMaster.sTransNox);
        holder.lblOrderSt.setText(GetOrderStatus(loMaster));

        if(loMaster.nDiscount != 0.00) {
            holder.lblItmDisc.setText(loMaster.nDiscount + " %");
        } else {
            holder.lblItmDisc.setText("");
        }

        double lnTrantotl = loMaster.nTranTotl;
        double lnProcPaym = loMaster.nProcPaym;
        double lnDiscount = loMaster.nDiscount;
        double lnFreightx = loMaster.nFreightx;
        double lnSubTotal = lnTrantotl - (lnTrantotl * lnDiscount);

        double lnTotalxx = lnSubTotal + lnFreightx;
        holder.lblBrandNm.setText(loMaster.xModelNme);
        holder.lblOrderTl.setText(CashFormatter.parse(String.valueOf(lnTotalxx)));
        holder.lblItmPrce.setText(CashFormatter.parse(String.valueOf(loMaster.nUnitPrce)));
        holder.lblItmQtyx.setText("Quantity: "+ loMaster.nQuantity);
        holder.setImage(loMaster.sImagesxx);
        holder.itemView.setOnClickListener(v -> mListener.OnClick(loMaster.sTransNox, loMaster.cTranStat));
    }

    @Override
    public int getItemCount() {
        return poMaster.size();
    }

    public static class ViewHolderItem extends RecyclerView.ViewHolder{

        public TextView lblOrderNo,
                lblOrderSt,
                lblOrderTl,
                lblBrandNm,
                lblItmDisc,
                lblItmPrce,
                lblItmQtyx;
        private ImageView imageView;

        public ViewHolderItem(@NonNull View itemView) {
            super(itemView);
            lblOrderNo = itemView.findViewById(R.id.lbl_orderNo);
            lblOrderSt = itemView.findViewById(R.id.lbl_orderStatus);
            lblOrderTl = itemView.findViewById(R.id.lbl_orderTotal);
            lblBrandNm = itemView.findViewById(R.id.lbl_brandName);
            lblItmDisc = itemView.findViewById(R.id.lbl_itemDiscount);
            lblItmPrce = itemView.findViewById(R.id.lbl_itemPrice);
            lblItmQtyx = itemView.findViewById(R.id.lbl_itemQuantity);
            imageView = itemView.findViewById(R.id.img_item);
        }

        public void setImage(String image){
            try {
                JSONArray laJson = new JSONArray(image);
                String lsImgVal = laJson.getJSONObject(0).getString("sImageURL");
                Picasso.get().load(lsImgVal).placeholder(R.drawable.ic_no_image_available)
                        .error(R.drawable.ic_no_image_available).into(imageView);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private String GetOrderStatus(DOrderMaster.OrderHistory fsVal){
        switch (fsVal.cTranStat){
            case "0":
                if(fsVal.sTermCode.isEmpty()){
                    return "To Pay";
                }

                if(fsVal.sTermCode.equalsIgnoreCase("C0W2011")) {
                    if(fsVal.cPaymPstd.equalsIgnoreCase("0")) {
//                        if (fsVal.nTranTotl > fsVal.nProcPaym) {
//                            return "To Pay";
//                        }

                        return "Processing Payment";
                    } else if(fsVal.cPaymPstd.equalsIgnoreCase("1")){
                        if (fsVal.nTranTotl > fsVal.nProcPaym) {
                            return "To Pay";
//                            return "Processing Payment";
                        }
                    } else if(fsVal.cPaymPstd.equalsIgnoreCase("3")){
                        return "To Pay";
                    }
                }
//
//                if(fsVal.sTermCode.equalsIgnoreCase("C0W2011") &&
//                    fsVal.cPaymPstd.equalsIgnoreCase("1")){
//                    if(fsVal.nTranTotl > fsVal.nProcPaym) {
//                        return "To Pay";
//                    }
//                }
//                if(fsVal.sTermCode.equalsIgnoreCase("C0W2011") &&
//                        fsVal.cPaymPstd.equalsIgnoreCase("0")){
//                    return "Processing Payment";
//                }
                return "Processing";
            case "1":
                return "Verified";
            case "2":
                return "To Ship";
            case "3":
                return "Cancelled";
            default:
                return "Delivered";
        }
    }
}
