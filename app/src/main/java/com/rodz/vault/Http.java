package com.rodz.vault;


import java.io.*;
import java.net.URL;
import java.net.URLEncoder;

import android.content.*;

import java.security.cert.CertificateException;
import java.util.*;
import android.app.*;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.security.cert.X509Certificate;

public class Http
{
    Context ctx;
    HttpParams params;
    public Http(HttpParams paramz){
        params = paramz;
        ctx = paramz.ctx;
        this.doJob();
    }

    public void doJob(){
        Thread thread = new Thread(new Runnable(){
            public void run(){
                try{
                    final String text = httpConn(params.url, "POST", params.params).trim();
                    Activity act = (Activity)ctx;
                    act.runOnUiThread(new Runnable(){
                        public void run(){
                            params.onResponse(text);
                        }
                    });
                }catch(Exception e){}
            }
        });
        thread.start();

    }

    public String httpConn(String urli, String method, Map<String, Object> params){
        URL url;
        try {
            TrustManager[] trustAllCerts = new TrustManager[] {new X509TrustManager() {
                @Override
                public void checkClientTrusted(java.security.cert.X509Certificate[] x509Certificates, String s) throws CertificateException {

                }

                @Override
                public void checkServerTrusted(java.security.cert.X509Certificate[] x509Certificates, String s) throws CertificateException {

                }

                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }
            };

            // Install the all-trusting trust manager
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            // Create all-trusting host name verifier
            HostnameVerifier allHostsValid = new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };

            // Install the all-trusting host verifier
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

            url = new URL(urli);
            //creating formdata from a linkedhashmap
            StringBuilder postData = new StringBuilder();

            int i = 0;
            for(Map.Entry<String, Object> param:params.entrySet()){
                try {
                    String key = param.getKey();
                    String value = String.valueOf(param.getValue());

                    //appending the form data
                    if (postData.length() != 0) postData.append('&');
                    postData.append(URLEncoder.encode(key, "UTF-8"));
                    postData.append('=');
                    postData.append(URLEncoder.encode(value, "UTF-8"));
                    i += 1;
                } catch (UnsupportedEncodingException ex) {
                    //Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            byte[] postDataBytes = postData.toString().getBytes("UTF-8");

            HttpsURLConnection conn = (HttpsURLConnection)url.openConnection();
            conn.setDefaultHostnameVerifier(allHostsValid);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
            conn.setConnectTimeout(5000);
            conn.setDoOutput(true);
            conn.getOutputStream().write(postDataBytes);

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

            //get string output
            String line = null;
            String response = "";
            while ((line = reader.readLine()) != null) {
                if (response.equals("")){
                    response = line;
                }
                else{
                    response += "\n"+line;
                }
                //System.out.println(line);
            }
            //String response = sb.toString();
            return response;
        } catch (UnsupportedEncodingException ex) {
            return ex.toString();
        } catch (IOException exx) {
            return exx.toString();
        }
        catch(Exception last){
            return last.toString();
        }
    }
}
