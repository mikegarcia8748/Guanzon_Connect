package org.rmj.g3appdriver.lib.CreditApp.model;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.rmj.g3appdriver.etc.AppConstants;

public class MpCreditApp {
    private static final String TAG = MpCreditApp.class.getSimpleName();

    private JSONObject params = new JSONObject(); //parent

    private final PersonalInfo poClient = new PersonalInfo();
    private final MeansInfo poMeans = new MeansInfo();
    private final DisbursementInfo poOther = new DisbursementInfo();

    private String message;

    public MpCreditApp() {

    }

    public void setUnitType(String fsVal) throws JSONException {
        params.put("cUnitType", fsVal);
    }

    public String getUnitType() throws Exception{
        if(params.has("cUnitType")){
            return params.getString("cUnitType");
        }
        return "";
    }

    public void setModel(String fsVal) throws JSONException {
        params.put("sModelIDx", fsVal);
    }

    public void setDiscount(String fsVal) throws JSONException {
        params.put("nDiscount", fsVal);
    }

    public void setDownpayment(String fsVal) throws JSONException {
        params.put("nDownPaym", fsVal);
    }

    public String getDownpayment() throws Exception{
        if(params.has("nDownPaym")){
            return params.getString("nDownPaym");
        }
        return "";
    }

    public void setAmortization(String fsVal) throws JSONException {
        params.put("nAmortztn", fsVal);
    }

    public void setInstallmentTerm(String fsVal) throws JSONException {
        params.put("sInstlTrm", fsVal);
    }

    public void setMiscellaneousExpense(String fsVal) throws JSONException {
        params.put("sMiscExpn", fsVal);
    }

    public void setDateApplied(String fsVal) throws Exception{
        params.put("dAppliedx", new AppConstants().CURRENT_DATE);
    }

    public void setDateCreated(String fsVal) throws Exception{
        params.put("dCreatedx", new AppConstants().DATE_MODIFIED);
    }

    public void setUnitApplied(String fsVal) throws Exception{
        params.put("cUnitAppl", "1"); //Default 1 for mobile phone loan app
    }

    public PersonalInfo clientInfo() {
        return poClient;
    }

    public MeansInfo meansInfo(){
        return poMeans;
    }

    public DisbursementInfo disbursementInfo(){
        return poOther;
    }

    public boolean isDataValid() throws JSONException{
        if(params.has("nDownPaym") &&
                params.getString("nDownPaym").trim().isEmpty()){
            message = "Unset downpayment detected.";
            return false;
        } else if(params.has("nAmortztn") &&
                params.getString("nAmortztn").trim().isEmpty()){
            message = "Unset amortization detected.";
            return false;
        } else if(params.has("nUnitPrce") &&
                params.getString("nUnitPrce").trim().isEmpty()){
            message = "Unset unit price detected.";
            return false;
        } else if(params.has("sMiscExpn") &&
                params.getString("sMiscExpn").trim().isEmpty()){
            message = "Unset miscellaneous expense detected.";
            return false;
        } else if(params.has("cUnitType") &&
                params.getString("cUnitType").trim().isEmpty()){
            message = "Please select unit type.";
            return false;
        } else if(params.has("sModelIDx") &&
                params.getString("sModelIDx").trim().isEmpty()){
            message = "Please select model.";
            return false;
        } else if(params.has("sInstlTrm") &&
                params.getString("sInstlTrm").trim().isEmpty()){
            message = "Please select installment term.";
            return false;
        } else if(!poClient.isDataValid()) {
            message = poClient.getMessage();
            return false;
        } else if(!meansInfo().isDataValid()){
            message = meansInfo().getMessage();
            return false;
        } else if(!disbursementInfo().isDataValid()){
            message = disbursementInfo().getMessage();
            return false;
        } else {
            return true;
        }
    }

    public void setData(String fsVal) throws JSONException{
        params = new JSONObject(fsVal);
        poClient.setData(fsVal);
        poMeans.setData(fsVal);
        poOther.setData(fsVal);
        Log.d(TAG, params.toString());
    }

    public String getData() throws JSONException {
        params.put("personal_info", poClient.getData());
        params.put("means_info", poMeans.getData());
        params.put("disbursement_info", poOther.getData());
        Log.d(TAG, params.toString());
        return params.toString();
    }
}
