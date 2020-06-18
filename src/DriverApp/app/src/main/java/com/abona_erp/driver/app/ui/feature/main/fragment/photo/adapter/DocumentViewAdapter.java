package com.abona_erp.driver.app.ui.feature.main.fragment.photo.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.abona_erp.driver.app.App;
import com.abona_erp.driver.app.R;
import com.abona_erp.driver.app.data.model.AppFileInterchangeItem;
import com.abona_erp.driver.app.logging.Log;
import com.abona_erp.driver.app.ui.widget.AsapTextView;
import com.abona_erp.driver.core.base.ContextUtils;
import com.google.gson.Gson;
import com.pnikosis.materialishprogress.ProgressWheel;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DocumentViewAdapter extends RecyclerView.Adapter<DocumentViewAdapter.ViewHolder> {
  
  private static final String TAG = DocumentViewAdapter.class.getSimpleName();
  
  private static final String APP_FOLDER = "/DriverApp/";
  private static final String PATH_SLASH = "/";
  
  private Context mContext;
  
  private String mMandantID;
  private String mOrderNo;
  
  private AppFileInterchangeItem[] appFileInterchangeItems;
  private ArrayList<AppFileInterchangeItem> _items = new ArrayList<>();
  private FileDownloadTask fileDownloadTask;
  
  public DocumentViewAdapter() {
  }
  
  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext())
      .inflate(R.layout.item_document, parent, false);
    mContext = parent.getContext();
    return new ViewHolder(view);
  }
  
  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    Log.d(TAG, "onBindViewHolder");
    
    holder.setIsRecyclable(false);
    if (_items.get(position).getFileName() != null) {
      holder.tv_document_link.setText(_items.get(position).getFileName());
    }
    if (_items.get(position).getAddedUser() != null) {
      holder.tv_document_user.setText(_items.get(position).getAddedUser());
    }
    if (_items.get(position).getAddedDate() != null) {
      synchronized (DocumentViewAdapter.this) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        holder.tv_document_created.setText(sdf.format(_items.get(position).getAddedDate()));
      }
    }
  
    if (isFileExists(mMandantID, mOrderNo, _items.get(position).getFileName())) {
      holder.btn_document_download.setText(mContext.getResources().getString(R.string.action_open));
    } else {
      holder.btn_document_download.setText(mContext.getResources().getString(R.string.action_download));
    }
    holder.btn_document_download.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        
        if (isFileExists(mMandantID, mOrderNo, _items.get(position).getFileName())) {
  
          File file = new File(Environment
            .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            .getAbsolutePath() + APP_FOLDER + mMandantID + PATH_SLASH + mOrderNo,
            _items.get(position).getFileName());
          
          MimeTypeMap mime = MimeTypeMap.getSingleton();
          String ext = file.getName().substring(file.getName().lastIndexOf(".") + 1);
          String type = mime.getMimeTypeFromExtension(ext);
          
          try {
            Uri path = null;
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
  
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
              path = FileProvider.getUriForFile(mContext, "com.abona_erp.driver.app.provider", file);
    
              intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
              intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } else {
              path = Uri.fromFile(file);
              
              intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            }
  
            intent.setDataAndType(path, type);
            App.getInstance().getApplicationContext().startActivity(intent);
            
          } catch (Exception e) {
            Log.e(TAG, "exception: " + e.getMessage());
          }
          
        } else {
          holder.btn_document_download.setVisibility(View.INVISIBLE);
          holder.progressWheel.setVisibility(View.VISIBLE);
  
          try {
            //URL url = new URL(_items.get(position).getLinkToFile());
            Log.i(TAG, "URL: " + _items.get(position).getLinkToFile());
            fileDownload(_items.get(position).getLinkToFile(), _items.get(position).getFileName(), position);
          } catch (Exception e) {
            Log.e(TAG, e.getMessage());
          }
        }
      }
    });
  }
  
  public void setDocumentItems(List<String> items, int mandantID, int orderNo, int taskId) {
    
    if (items.size() > 0) {
      Log.i(TAG, items.get(0));
      Gson gson = new Gson();
      appFileInterchangeItems = gson.fromJson(items.get(0), AppFileInterchangeItem[].class);
      
      _items.clear();
      for (int i = 0; i < appFileInterchangeItems.length; i++) {
        if (appFileInterchangeItems[i].getTaskId() == 0 || appFileInterchangeItems[i].getTaskId() == taskId) {
          _items.add(appFileInterchangeItems[i]);
        }
      }
      
      mMandantID = String.valueOf(mandantID);
      mOrderNo = String.valueOf(orderNo);
      
      notifyDataSetChanged();
    } else {
      Log.i(TAG, "No items...");
    }
  }
  
  // getItemCount() is called many times, and when it is first called,
  // mList has not been updated (means initially, it's null,
  // and we can't return null).
  @Override
  public int getItemCount() {
    if (_items != null) {
      return _items.size();
    } else {
      return 0;
    }
  }
  
  class ViewHolder extends RecyclerView.ViewHolder {
    
    private final AsapTextView    tv_document_link;
    private final AsapTextView    tv_document_user;
    private final AsapTextView    tv_document_created;
    private final AppCompatButton btn_document_download;
    private final ProgressWheel   progressWheel;
  
    ViewHolder(View itemView) {
      super(itemView);
      
      tv_document_link = itemView.findViewById(R.id.tv_link);
      tv_document_user = itemView.findViewById(R.id.tv_user);
      tv_document_created = itemView.findViewById(R.id.tv_created);
      btn_document_download = itemView.findViewById(R.id.btn_download);
      progressWheel = itemView.findViewById(R.id.progressWheel);
    }
  }
  
  private void fileDownload(String id, String filename, int position) {
  
    Request req = new Request.Builder().url(id).build();
    App.getInstance().apiManager.provideApiClient().newCall(req).enqueue(new okhttp3.Callback() {
      @Override
      public void onFailure(@NotNull okhttp3.Call call, @NotNull IOException e) {
        Log.d(TAG, "error");
      }
  
      @Override
      public void onResponse(@NotNull okhttp3.Call call, @NotNull okhttp3.Response response) throws IOException {
        if (response.isSuccessful()) {
//          Toast.makeText(ContextUtils.getApplicationContext(), "Downloading...", Toast.LENGTH_SHORT).show();
    
          fileDownloadTask = new FileDownloadTask(filename, position);
          fileDownloadTask.execute(response.body());
        } else {
          Log.d(TAG, "Connection failed! " + response.message());
        }
      }
    });
  }
  
  private class FileDownloadTask extends AsyncTask<ResponseBody, Pair<Integer, Long>, String> {
    
    private final String filename;
    private final int    position;
    
    public FileDownloadTask(String filename, int position) {
      this.filename = filename;
      this.position = position;
    }
    
    @Override
    protected void onPreExecute() {
      super.onPreExecute();
    }
    
    @Override
    protected String doInBackground(ResponseBody... urls) {
      // Copy you logic to calculate progress and call.
      saveToDisk(urls[0], this.filename);
      return null;
    }
    
    @Override
    protected void onPostExecute(String result) {
    }
    
    @RequiresApi(api = Build.VERSION_CODES.N)
    protected void onProgressUpdate(Pair<Integer, Long>... progress) {
      
      if (progress[0].first == 100) {
        Toast.makeText(mContext, "File downloaded successfully", Toast.LENGTH_SHORT).show();
      }
      if (progress[0].second > 0) {
        //int currentProgress = (int)((double)progress[0].first / (double)progress[0].second * 100);
        //mCurrentProgress = (int)((double)progress[0].first / (double)progress[0].second * 100);
        //mProcessed.replace(position, mCurrentProgress);
      }
      if (progress[0].first == -1) {
        Toast.makeText(mContext, "Download failed!", Toast.LENGTH_SHORT).show();
      }
  
      notifyDataSetChanged();
    }
    
    public void doProgress(Pair<Integer, Long> progressDetails) {
      publishProgress(progressDetails);
    }
  }
  
  private void saveToDisk(ResponseBody body, String filename) {
    if (!createDirectory(mMandantID, mOrderNo)) {
      Toast.makeText(mContext, "Fehler beim Erstellen der Download Ordner!",
        Toast.LENGTH_SHORT).show();
      return;
    }
    
    try {
      File destinationFile = new File(Environment
        .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        + APP_FOLDER + mMandantID + PATH_SLASH + mOrderNo, filename);
  
      InputStream inputStream = null;
      OutputStream outputStream = null;
      
      try {
        inputStream = body.byteStream();
        outputStream = new FileOutputStream(destinationFile);
        byte data[] = new byte[4096];
        int count;
        int progress = 0;
        long fileSize = body.contentLength();
        Log.d(TAG, "File Size=" + fileSize);
        
        while ((count = inputStream.read(data)) != -1) {
          outputStream.write(data, 0, count);
          progress += count;
          Pair<Integer, Long> pairs = new Pair<>(progress, fileSize);
          fileDownloadTask.doProgress(pairs);
          Log.d(TAG, "Progress: " + progress + "/" + fileSize + " >>>> " + (float)progress / fileSize);
        }
        
        outputStream.flush();
        
        Log.d(TAG, destinationFile.getParent());
        Pair<Integer, Long> pairs = new Pair<>(100, 100L);
        fileDownloadTask.doProgress(pairs);
        return;
        
      } catch (IOException e) {
        e.printStackTrace();
        Pair<Integer, Long> pairs = new Pair<>(-1, Long.valueOf(-1));
        fileDownloadTask.doProgress(pairs);
        Log.d(TAG, "Failed to save the file!");
        return;
      } finally {
        if (inputStream != null) inputStream.close();
        if (outputStream != null) outputStream.close();
      }
    } catch (IOException e) {
      e.printStackTrace();
      Log.d(TAG, "Failed to save the file!");
      return;
    }
  }
  
  /**
   * Function to check is file exists and is not directory.
   */
  private boolean isFileExists(String mandantID, String orderNo, String filename) {
    if (TextUtils.isEmpty(mandantID) || mandantID.length() == 0)
      return false;
    if (TextUtils.isEmpty(orderNo) || orderNo.length() == 0)
      return false;
    
    try {
      File file = new File(Environment
        .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        + APP_FOLDER + mandantID + PATH_SLASH + orderNo, filename);
      if (file.exists() && !file.isDirectory()) {
        return true;
      }
    } catch (Exception e) {
      Log.w(TAG, e.toString());
    }
    
    return false;
  }
  
  private boolean createDirectory(String mandantID, String orderNo) {
    if (TextUtils.isEmpty(mandantID) || mandantID.length() == 0)
      return false;
    if (TextUtils.isEmpty(orderNo) || orderNo.length() == 0)
      return false;
    
    try {
      File dir = new File(Environment
        .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        + APP_FOLDER + mandantID + PATH_SLASH + orderNo);
      if (!dir.exists()) {
        dir.mkdirs();
      }
      return true;
    } catch (Exception e) {
      Log.w(TAG, e.toString());
    }
    
    return false;
  }
}
