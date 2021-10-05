package org.rmj.g3appdriver.ImportData;

import android.app.Application;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.rmj.g3appdriver.Database.Entities.EGCardTransactionLedger;
import org.rmj.g3appdriver.Database.Repositories.RGCardTransactionLedger;
import org.rmj.g3appdriver.Database.Repositories.RGcardApp;
import org.rmj.g3appdriver.Http.HttpHeaders;
import org.rmj.g3appdriver.etc.AppConfigPreference;
import org.rmj.g3appdriver.etc.AppConstants;
import org.rmj.g3appdriver.utils.CodeGenerator;
import org.rmj.g3appdriver.utils.ConnectionUtil;
import org.rmj.g3appdriver.utils.Http.WebClient;
import org.rmj.g3appdriver.utils.WebApi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Import_Transactions extends CodeGenerator implements ImportInstance {
    private static final String TAG = Import_Transactions.class.getSimpleName();
    private final Application instance;
    private final AppConfigPreference poConfig;
    private final RGcardApp poGcardx;
/*
    Repository
    private final RBranch repository;
*/
    private String gCardNo = "";
    public Import_Transactions(Application application){
        this.instance = application;
        this.poConfig = AppConfigPreference.getInstance(instance);
        this.poGcardx = new RGcardApp(instance);


//        this.repository = new RBranch(instance);
    }

    @Override
    public void ImportData(ImportDataCallback callback) {
        try {
            JSONObject loJson = new JSONObject();
            loJson.put("secureno", generateSecureNo(poGcardx.getCardNo()));
            new ImportTransactionsTask(callback, instance).execute(loJson);

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private static class ImportTransactionsTask extends AsyncTask<JSONObject, Void, String> {
        private final ImportDataCallback callback;
        private final HttpHeaders headers;
        private final ConnectionUtil conn;
        private final RGCardTransactionLedger repository;
        private WebApi webApi;

        private String[] Import_Type = {
            "ONLINE",
            "OFFLINE",
            "PREORDER",
            "REDEMPTION"};


        public ImportTransactionsTask(ImportDataCallback callback, Application instance) {
            this.callback = callback;
            this.headers = HttpHeaders.getInstance(instance);
            this.conn = new ConnectionUtil(instance);
            this.repository = new RGCardTransactionLedger(instance);
            this.webApi = new WebApi(instance);

        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected String doInBackground(JSONObject... jsonObjects) {
            String response = "";
            try {
                String[] Import_Urls = new String[]{
                        webApi.URL_IMPORT_TRANSACTIONS_ONLINE,
                        webApi.URL_IMPORT_TRANSACTIONS_OFFLINE,
                        webApi.URL_IMPORT_TRANSACTIONS_PREORDER,
                        webApi.URL_IMPORT_TRANSACTIONS_REDEMPTION
                };
                if(conn.isDeviceConnected()) {
                    for(int index = 0; index < Import_Urls.length; index++){
                        response = WebClient.httpsPostJSon(Import_Urls[index], jsonObjects[0].toString(), headers.getHeaders());
                        JSONObject loJson = new JSONObject(Objects.requireNonNull(response));
                        Log.e(TAG, loJson.getString("result"));
                        String lsResult = loJson.getString("result");
                        if(lsResult.equalsIgnoreCase("success")){
                            JSONArray laJson = loJson.getJSONArray("detail");
                            saveDataToLocal(laJson);
    //                        if(!repository.insertBranchInfos(laJson)){
    //                            response = AppConstants.ERROR_SAVING_TO_LOCAL();
    //                        }
                        } else {
                            JSONObject loError = loJson.getJSONObject("error");
                            String message = loError.getString("message");
                            callback.OnFailedImportData(message);
                        }
                    }
                } else {
                    response = AppConstants.NO_INTERNET();
                }
            } catch (Exception e) {
                Log.e(TAG, Arrays.toString(e.getStackTrace()));
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject loJson = new JSONObject(s);
                Log.e(TAG, loJson.getString("result"));
                String lsResult = loJson.getString("result");
                if(lsResult.equalsIgnoreCase("success")){
                    callback.OnSuccessImportData();
                } else {
                    JSONObject loError = loJson.getJSONObject("error");
                    String message = loError.getString("message");
                    callback.OnFailedImportData(message);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                callback.OnFailedImportData(e.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
                callback.OnFailedImportData(e.getMessage());
            }
        }
        void saveDataToLocal(JSONArray laJson) throws Exception{
            List<EGCardTransactionLedger> brnList = new ArrayList<>();
            for(int x = 0; x < laJson.length(); x++){
                JSONObject loJson = laJson.getJSONObject(x);
                EGCardTransactionLedger info = new EGCardTransactionLedger();
                info.setGCardNox(loJson.getString("sGCardNox"));
                info.setTransact(loJson.getString("dTransact"));
                info.setSourceDs( Import_Type[x]);
                info.setReferNox(loJson.getString("sReferNox"));
                info.setTranType(loJson.getString("sTranType"));
                info.setSourceNo(loJson.getString("sSourceNo"));
                info.setPointsxx(Double.parseDouble(loJson.getString("nPointsxx")));
                brnList.add(info);
            }
            repository.insertBulkData(brnList);
        }
    }
}
