package org.rmj.g3appdriver.dev.Database.DataAccessObject;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import org.rmj.g3appdriver.dev.Database.Entities.EBarangayInfo;
import org.rmj.g3appdriver.dev.Database.Entities.ECountryInfo;
import org.rmj.g3appdriver.dev.Database.Entities.EProvinceInfo;
import org.rmj.g3appdriver.dev.Database.Entities.ETownInfo;

import java.util.List;

@Dao
public interface DAddress {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void SaveBarangay(List<EBarangayInfo> foValue);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void SaveTown(List<ETownInfo> foValue);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void SaveProvince(List<EProvinceInfo> foValue);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void SaveCountry(List<ECountryInfo> foValue);

    @Query("SELECT * FROM Barangay_Info WHERE sTownIDxx =:TownID")
    LiveData<List<EBarangayInfo>> GetBarangayList(String TownID);

    @Query("SELECT * FROM Country_Info")
    LiveData<List<ECountryInfo>> GetCountryList();

    @Query("SELECT " +
            "a.sTownIDxx AS sTownID, " +
            "a.sTownName AS sTownNm, " +
            "b.sProvName AS sProvNm " +
            "FROM Town_Info a " +
            "LEFT JOIN Province_Info b " +
            "ON a.sProvIDxx = b.sProvIDxx")
    LiveData<List<oTownObj>> GetTownList();

    class oTownObj{
        public String sTownID;
        public String sTownNm;
        public String sProvNm;
    }
}