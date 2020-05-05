package com.abona_erp.driver.app.util;

import com.abona_erp.driver.app.logging.Log;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class ClientSSLSocketFactory {
  
  private static final String TAG = ClientSSLSocketFactory.class.getSimpleName();
  
  private static SSLSocketFactory socketFactory;
  private static X509TrustManager trustManager;
  
  public static SSLSocketFactory getSocketFactory() {
    if (socketFactory == null) {
      try {
        X509TrustManager trustManager = get509TrustManager();
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, new TrustManager[]{trustManager}, null);
        socketFactory = sslContext.getSocketFactory();
      } catch (NoSuchAlgorithmException | KeyManagementException e) {
        Log.e(TAG, "Unable to create the ssl socket factory.");
      }
    }
    return socketFactory;
  }
  
  static X509TrustManager get509TrustManager() {
    if (trustManager == null) {
      trustManager = new X509TrustManager() {
        
        @Override
        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
    
        }
  
        @Override
        public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
    
        }
  
        @Override
        public X509Certificate[] getAcceptedIssuers() {
          return new X509Certificate[0];
        }
      };
    }
    return trustManager;
  }
}
