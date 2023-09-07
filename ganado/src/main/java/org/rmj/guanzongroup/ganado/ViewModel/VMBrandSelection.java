package org.rmj.guanzongroup.ganado.ViewModel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import org.rmj.g3appdriver.etc.AppConstants;
import org.rmj.g3appdriver.etc.ConnectionUtil;
import org.rmj.g3appdriver.lib.Ganado.Obj.Ganado;
import org.rmj.g3appdriver.lib.Ganado.Obj.ProductInquiry;
import org.rmj.g3appdriver.dev.Database.Entities.EMcModel;
import org.rmj.g3appdriver.dev.Database.Entities.EMcBrand;
import org.rmj.g3appdriver.utils.Task.OnDoBackgroundTaskListener;
import org.rmj.g3appdriver.utils.Task.TaskExecutor;

import java.util.List;

public class VMBrandSelection extends AndroidViewModel {

    private final ProductInquiry poSys;
    private final ConnectionUtil poConn;


    public VMBrandSelection(@NonNull Application application) {
        super(application);

        poSys = new ProductInquiry(application);
        poConn = new ConnectionUtil(application);
    }

    public LiveData<List<EMcBrand>> getBrandList(){
        return poSys.GetMotorcycleBrands();
    }
    public void importCriteria(){
        new VMBrandSelection.ImportCriteriaTask().execute();
    }


    private class ImportCriteriaTask{
        private String TAG = getClass().getSimpleName();
        public void execute(){
            TaskExecutor.Execute(null, new OnDoBackgroundTaskListener() {
                @Override
                public Object DoInBackground(Object args) {
                    if (!poConn.isDeviceConnected()){
                        return AppConstants.UNABLE_TO_REACH_SERVER;
                    }

                    return "";
                }

                @Override
                public void OnPostExecute(Object object) {

                    Log.e(TAG, object.toString());
                }
            });
        }
    }
}