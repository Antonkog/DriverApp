package com.abona_erp.driver.app.work;

import android.os.AsyncTask;

import com.abona_erp.driver.app.data.model.ResultOfAction;
import com.abona_erp.driver.app.logging.Log;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

public class ConfirmationAsyncTask extends AsyncTask<Call, Void, ResultOfAction> {
  
  @Override
  protected ResultOfAction doInBackground(Call... params) {
    try {
      Call<ResultOfAction> call = params[0];
      Response<ResultOfAction> response = call.execute();
      return response.body();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }
  
  @Override
  protected void onPostExecute(ResultOfAction result) {
    Log.i("ConfirmationAsyncTask", "********************************************");
  }
}
