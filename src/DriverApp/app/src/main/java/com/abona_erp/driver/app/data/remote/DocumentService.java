package com.abona_erp.driver.app.data.remote;
  
  import com.abona_erp.driver.app.data.model.AppFileInterchangeItem;
  
  import java.util.ArrayList;
  
  import retrofit2.Call;
  import retrofit2.http.GET;
  import retrofit2.http.Headers;
  import retrofit2.http.Query;

public interface DocumentService {
  
  @Headers("Content-Type:application/json")
  @GET("documents")
  Call<ArrayList<AppFileInterchangeItem>> getDocuments(@Query("mandantId") int mandantId, @Query("orderNo") int orderNo, @Query("deviceId") String deviceId);
}
