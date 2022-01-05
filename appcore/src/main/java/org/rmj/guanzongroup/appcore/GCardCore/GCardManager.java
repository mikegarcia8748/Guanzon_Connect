package org.rmj.guanzongroup.appcore.GCardCore;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;

import androidx.lifecycle.LiveData;

import org.json.JSONArray;
import org.json.JSONObject;
import org.rmj.guanzongroup.appcore.Database.Entities.EGCardTransactionLedger;
import org.rmj.guanzongroup.appcore.Database.Entities.EGcardApp;
import org.rmj.guanzongroup.appcore.Database.Entities.ERedeemablesInfo;
import org.rmj.guanzongroup.appcore.Database.Entities.EServiceInfo;
import org.rmj.guanzongroup.appcore.Database.Repositories.RGcardApp;
import org.rmj.guanzongroup.appcore.Database.Repositories.RServiceInfo;
import org.rmj.guanzongroup.appcore.Etc.AppConstants;
import org.rmj.guanzongroup.appcore.Etc.ConnectionUtil;
import org.rmj.guanzongroup.appcore.Etc.SessionManager;
import org.rmj.guanzongroup.appcore.Etc.Telephony;
import org.rmj.guanzongroup.appcore.GCardCore.Obj.CartItem;
import org.rmj.guanzongroup.appcore.GCardCore.Obj.GcardCartItems;
import org.rmj.guanzongroup.appcore.GCardCore.Obj.GcardCredentials;
import org.rmj.guanzongroup.appcore.ServerRequest.GCardAPI;
import org.rmj.guanzongroup.appcore.ServerRequest.HttpHeaders;
import org.rmj.guanzongroup.appcore.ServerRequest.WebClient;

import java.util.List;

public class GCardManager implements iGCardSystem{
    private static final String TAG = GCardManager.class.getSimpleName();

    private final Context mContext;

    private final HttpHeaders poHeaders;
    private final CodeGenerator poCode;
    private final RGcardApp poGCard;
    private final SessionManager poSession;
    private final Telephony poDevicex;
    private final RServiceInfo poService;

    public GCardManager(Context context) {
        this.mContext = context;
        this.poHeaders = new HttpHeaders(mContext);
        this.poCode = new CodeGenerator();
        this.poGCard = new RGcardApp(mContext);
        this.poSession = new SessionManager(mContext);
        this.poDevicex = new Telephony(mContext);
        this.poService = new RServiceInfo(mContext);
    }

    @Override
    public void AddGCard(GcardCredentials gcardInfo, GCardSystem.GCardSystemCallback callback) throws Exception{
        if(!gcardInfo.isDataValid()){
            callback.OnFailed(gcardInfo.getMessage());
        } else {
            String lsResponse = WebClient.httpsPostJSon(GCardAPI.URL_ADD_NEW_GCARD, gcardInfo.getJSONParameters(), poHeaders.getHeaders());
            if(lsResponse == null){
                callback.OnFailed("No server response.");
            } else {
                JSONObject loResponse = new JSONObject(lsResponse);
                String lsResult = loResponse.getString("result");
                if(lsResult.equalsIgnoreCase("success")){
                    callback.OnSuccess("New gcard has been added successfully.");
                } else {
                    JSONObject loError = loResponse.getJSONObject("error");
                    String lsMessage = loError.getString("message");
                    callback.OnFailed(lsMessage);
                }
            }
        }
    }

    @Override
    public LiveData<List<EGcardApp>> GetGCardList() {
        return poGCard.getAllGCardInfo();
    }

    @Override
    public void AddGCardQrCode(String GCardNumber, GCardSystem.GCardSystemCallback callback) throws Exception {
        JSONObject params = new JSONObject();
        params.put("secureno", poCode.generateSecureNo(GCardNumber));
        String lsResponse = WebClient.httpsPostJSon(GCardAPI.URL_ADD_NEW_GCARD, params.toString(), poHeaders.getHeaders());
        if(lsResponse == null){
            callback.OnFailed("No server response.");
        } else {
            JSONObject loResponse = new JSONObject(lsResponse);
            String lsResult = loResponse.getString("result");
            if(lsResult.equalsIgnoreCase("success")){
                callback.OnSuccess("New gcard has been added successfully.");
            } else {
                JSONObject loError = loResponse.getJSONObject("error");
                String lsMessage = loError.getString("message");
                callback.OnFailed(lsMessage);
            }
        }
    }

    @Override
    public void DownloadGcardNumbers(GCardSystem.GCardSystemCallback callback) throws Exception {
        JSONObject param = new JSONObject();
        param.put("user_id", poSession.getUserID());
        String lsResponse = WebClient.httpsPostJSon(GCardAPI.URL_IMPORT_GCARD, param.toString(), poHeaders.getHeaders());
        if(lsResponse == null){
            callback.OnFailed("Server no response.");
        } else {
            JSONObject loResponse = new JSONObject(lsResponse);
            String lsResult = loResponse.getString("result");
            if(lsResult.equalsIgnoreCase("success")){
                callback.OnSuccess(loResponse.toString());
            } else {
                JSONObject loError = loResponse.getJSONObject("error");
                String lsMessage = loError.getString("message");
                callback.OnFailed(lsMessage);
            }
        }
    }

    @Override
    public void SaveGCardInfo(JSONObject detail) throws Exception {
        EGcardApp loGCard = new EGcardApp();
        loGCard.setGCardNox(detail.getString("sGCardNox"));
        loGCard.setCardNmbr(detail.getString("sCardNmbr"));
        loGCard.setUserIDxx(poSession.getUserID());
        loGCard.setNmOnCard(detail.getString("sNmOnCard"));
        loGCard.setMemberxx(detail.getString("dMemberxx"));
        loGCard.setCardType(detail.getString("cCardType"));
        loGCard.setAvlPoint(detail.getString("nAvlPoint"));
        loGCard.setTotPoint(detail.getString("nTotPoint"));
        loGCard.setTranStat(detail.getString("cCardStat"));
        loGCard.setActvStat("1");
        loGCard.setNotified("1");
        poGCard.insert(loGCard);
    }

    @Override
    public Bitmap GenerateGCardQrCode() {
        String lsSource = "CARD";
        String lsDevcID = poDevicex.getDeviceID();
        String lsCardNo = poGCard.getCardNo();
        String lsUserID = poSession.getUserID();
        String lsMobNox = poDevicex.getMobilNumbers();
        String lsDateTm = new AppConstants().GCARD_DATE_TIME;
        double lsCardPt = poGCard.getRemainingActiveCardPoints();
        String lsModelx = Build.MODEL;
        String lsTransN = "";
        return poCode.generateGCardCodex(lsSource,
                lsDevcID,
                lsCardNo,
                lsUserID,
                lsMobNox,
                lsDateTm,
                lsCardPt,
                lsModelx,
                lsTransN);
    }

    @Override
    public void DownloadRedeemables(GCardSystem.GCardSystemCallback callback) throws Exception {
        throw new NullPointerException();
    }

    @Override
    public LiveData<List<ERedeemablesInfo>> GetRedeemablesList() {
        return null;
    }

    @Override
    public void AddToCart(CartItem item, GCardSystem.GCardSystemCallback callback) throws Exception {
        throw new NullPointerException();
    }

    @Override
    public void UpdateCartItem(CartItem item, GCardSystem.GCardSystemCallback callback) throws Exception {
        throw new NullPointerException();
    }

    @Override
    public void PlaceOrder(GcardCartItems items, GCardSystem.GCardSystemCallback callback) throws Exception {
        throw new NullPointerException();
    }

    @Override
    public Bitmap GenerateGCardOrderQrCode() throws Exception {
        return null;
    }

    @Override
    public void DownloadTransactions(GCardSystem.GCardSystemCallback callback) throws Exception {
        throw new NullPointerException();
    }

    @Override
    public LiveData<List<EGCardTransactionLedger>> GetGcardTransactions() {
        return null;
    }

    @Override
    public void DownloadMCServiceInfo(String GCard, GCardSystem.GCardSystemCallback callback) throws Exception {
        JSONObject params = new JSONObject();
        String lsSecureNo = new CodeGenerator().generateSecureNo(GCard);
        params.put("secureno", lsSecureNo);
        String lsResponse = WebClient.httpsPostJSon(GCardAPI.URL_IMPORT_SERVICE, params.toString(), poHeaders.getHeaders());
        if (lsResponse == null) {
            callback.OnFailed("Server no response");
        } else {
            JSONObject loResponse = new JSONObject(lsResponse);
            String lsResult = loResponse.getString("result");
            if (lsResponse.equalsIgnoreCase("success")) {
                callback.OnSuccess(loResponse.toString());
            } else {
                JSONObject loError = loResponse.getJSONObject("error");
                String lsMessage = loError.getString("message");
                callback.OnFailed(lsMessage);
            }
        }
    }

    @Override
    public void DownloadRegistrationInfo(String GCard, GCardSystem.GCardSystemCallback callback) throws Exception {
        JSONObject params = new JSONObject();
        String lsSecureNo = new CodeGenerator().generateSecureNo(GCard);
        params.put("secureno", lsSecureNo);
        String lsResponse = WebClient.httpsPostJSon(GCardAPI.URL_IMPORT_MC_REGISTRATION, params.toString(), poHeaders.getHeaders());
        if (lsResponse == null) {
            callback.OnFailed("Server no response");
        } else {
            JSONObject loResponse = new JSONObject(lsResponse);
            String lsResult = loResponse.getString("result");
            if (lsResponse.equalsIgnoreCase("success")) {
                callback.OnSuccess(loResponse.toString());
            } else {
                JSONObject loError = loResponse.getJSONObject("error");
                String lsMessage = loError.getString("message");
                callback.OnFailed(lsMessage);
            }
        }
    }

    @Override
    public void SaveMcServiceInfo(JSONObject detail) throws Exception {
        JSONArray laJson = detail.getJSONArray("detail");
        if(laJson.length() > 0) {
            for (int x = 0; x < laJson.length(); x++) {
                JSONObject loJson = laJson.getJSONObject(x);
                EServiceInfo loService = new EServiceInfo();
                loService.setSerialID(loJson.getString("sSerialID"));
                loService.setEngineNo(loJson.getString("sEngineNo"));
                loService.setFrameNox(loJson.getString("sFrameNox"));
                loService.setBrandNme(loJson.getString("sBrandNme"));
                loService.setModelNme(loJson.getString("sModelNme"));
                loService.setFSEPStat(loJson.getString("cFSEPStat"));
                loService.setLastSrvc(loJson.getString("dLastSrvc"));
                loService.setYellowxx(loJson.getInt("nYellowxx"));
                loService.setWhitexxx(loJson.getInt("nWhitexxx"));
                loService.setYlwCtrxx(loJson.getInt("nYlwCtrxx"));
                loService.setWhtCtrxx(loJson.getInt("nWhtCtrxx"));
                loService.setMIlAgexx(loJson.getInt("nMilagexx"));
                loService.setNxtRmnds(loJson.getString("dNxtRmndS"));
                poService.insert(loService);
            }
        }
    }

    @Override
    public void SaveRegistrationInfo(JSONObject detail) throws Exception {

    }

    @Override
    public void ScheduleNextServiceDate(String date, GCardSystem.GCardSystemCallback callback) {

    }

    @Override
    public void ActivateGcard(String GcardNo) throws Exception {

    }
}