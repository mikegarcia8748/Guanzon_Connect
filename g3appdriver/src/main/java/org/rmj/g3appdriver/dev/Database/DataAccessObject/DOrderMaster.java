package org.rmj.g3appdriver.dev.Database.DataAccessObject;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import org.rmj.g3appdriver.dev.Database.Entities.EOrderMaster;

import java.util.List;

@Dao
public interface DOrderMaster {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void SaveOrderMaster(EOrderMaster foVal);

    @Query("SELECT * FROM MarketPlace_Order_Master WHERE sTransNox=:fsTransNo")
    EOrderMaster CheckOrderMasterIfExist(String fsTransNo);

    @Query("SELECT COUNT(sTransNox) FROM MarketPlace_Order_Master")
    int CheckIfMasterHasRecord();

    @Query("SELECT dTimeStmp FROM MarketPlace_Order_Detail ORDER BY dTimeStmp DESC LIMIT 1")
    String getMasterLatestTimeStmp();

    @Query("SELECT COUNT(*) FROM MarketPlace_Order_Master")
    LiveData<Integer> GetToPayOrders();

    @Query("SELECT * FROM MarketPlace_Order_Master WHERE sTransNox =:fsTransNo")
    LiveData<EOrderMaster> GetMasterInfo(String fsTransNo);

    @Query("SELECT * FROM MarketPlace_Order_Master WHERE cTranStat =:fcStatus")
    LiveData<List<EOrderMaster>> GetMasterOrderHistory(String fcStatus);

    @Query("SELECT * FROM MarketPlace_Order_Master")
    LiveData<List<EOrderMaster>> GetMasterOrderHistory();

    @Query("SELECT a.sTransNox, " +
            "a.cTranStat, " +
            "b.nQuantity * b.nUnitPrce AS nTranTotl, " +
            "b.nEntryNox, " +
            "b.nQuantity, " +
            "b.nUnitPrce, " +
            "b.nDiscount, " +
            "c.sBriefDsc, " +
            "c.xBarCodex, " +
            "c.xBrandNme, " +
            "c.xModelNme, " +
            "c.xColorNme, " +
            "c.xCategrNm " +
            "FROM MarketPlace_Order_Master a " +
            "LEFT JOIN MarketPlace_Order_Detail b " +
            "ON a.sTransNox = b.sTransNox " +
            "LEFT JOIN Product_Inventory c " +
            "ON b.sReferNox = c.sListngID " +
            "WHERE a.sClientID = (SELECT sClientID FROM Client_Info_Master) " +
            "ORDER BY a.dTransact DESC")
    LiveData<List<OrderHistory>> GetOrderHistoryList();

    @Query("SELECT COUNT(*) FROM MarketPlace_Order_Master " +
            "WHERE cTranStat = '0' " +
            "AND sClientID = (" +
            "SELECT sClientID FROM Client_Info_Master)")
    LiveData<Integer> GetToPayOrdersCount();

    @Query("SELECT COUNT(*) FROM MarketPlace_Order_Master " +
            "WHERE cTranStat = '1' " +
            "AND sClientID = (" +
            "SELECT sClientID FROM Client_Info_Master)")
    LiveData<Integer> GetProcessingOrdersCount();

    @Query("SELECT COUNT(*) FROM MarketPlace_Order_Master " +
            "WHERE cTranStat = '2' " +
            "AND sClientID = (" +
            "SELECT sClientID FROM Client_Info_Master)")
    LiveData<Integer> GetToShipOrdersCount();

    @Query("SELECT COUNT(*) FROM MarketPlace_Order_Master " +
            "WHERE cTranStat = '3' " +
            "AND sClientID = (" +
            "SELECT sClientID FROM Client_Info_Master)")
    LiveData<Integer> GetDeliveredOrdersCount();

    @Query("SELECT COUNT(*) FROM MarketPlace_Order_Master " +
            "WHERE cTranStat = '4' " +
            "AND sClientID = (" +
            "SELECT sClientID FROM Client_Info_Master)")
    LiveData<Integer> GetCancelledOrdersCount();

    @Query("SELECT a.sTransNox, " +
            "a.cTranStat, " +
            "b.nQuantity * b.nUnitPrce AS nTranTotl, " +
            "b.nEntryNox, " +
            "b.nQuantity, " +
            "b.nUnitPrce, " +
            "b.nDiscount, " +
            "c.sBriefDsc, " +
            "c.xBarCodex, " +
            "c.xBrandNme, " +
            "c.xModelNme, " +
            "c.xColorNme, " +
            "c.xCategrNm " +
            "FROM MarketPlace_Order_Master a " +
            "LEFT JOIN MarketPlace_Order_Detail b " +
            "ON a.sTransNox = b.sTransNox " +
            "LEFT JOIN Product_Inventory c " +
            "ON b.sReferNox = c.sListngID " +
            "WHERE a.sClientID = (SELECT sClientID FROM Client_Info_Master) " +
            "AND a.cTranStat=:fsVal " +
            "ORDER BY a.dTransact DESC")
    LiveData<List<OrderHistory>> GetOrderHistoryList(String fsVal);

    @Query("SELECT a.sTransNox," +
            " a.dTransact," +
            " IFNULL(a.dExpected, '')," +
            " a.sReferNox," +
            " a.nTranTotl," +
            " a.sTermCode," +
            " a.cTranStat," +
            " b.sFrstName || ' ' || b.sMiddName || ' ' || b.sLastName || ' ' || IFNULL(b.sSuffixNm, '') AS sUserName," +
            " IFNULL(b.sHouseNox, '') || ' ' || IFNULL(b.sAddressx, '') || ' ' || c.sBrgyName || ' ' ||  d.sTownName || ' ' || e.sProvName AS sAddressx," +
            " b.sMobileNo" +
            " FROM MarketPlace_Order_Master a " +
            " LEFT JOIN Client_Info_Master b " +
            " ON a.sClientID = b.sClientID " +
            " LEFT JOIN Barangay_Info c " +
            " ON b.sBrgyIDxx = c.sBrgyIDxx" +
            " LEFT JOIN Town_Info d" +
            " ON c.sTownIDxx = d.sTownIDxx" +
            " LEFT JOIN Province_Info e" +
            " ON d.sProvIDxx = e.sProvIDxx " +
            " WHERE a.sTransNox =:fsVal")
    LiveData<DetailedOrderHistory> GetDetailOrderHistory(String fsVal);

    //POJO use for list
    class OrderHistory{
        public String sTransNox;
        public String cTranStat;
        public String nTranTotl;
        public String nEntryNox;
        public String nQuantity;
        public String nUnitPrce;
        public String nDiscount;
        public String sBriefDsc;
        public String xBarCodex;
        public String xBrandNme;
        public String xModelNme;
        public String xColorNme;
        public String xCategrNm;
    }

    class DetailedOrderHistory{
        public String sTransNox;
        public String dTransact;
        public String dExpected;
        public String sReferNox;
        public String nTranTotl;
        public String sTermCode;
        public String cTranStat;
        public String sUserName;
        public String sAddressx;
        public String sMobileNo;
    }
}