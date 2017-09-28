package com.xedrux.cclouds.utils;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.*;
import com.loopj.android.http.Base64;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.loopj.android.http.*;

import java.io.*;
import java.net.*;
import java.util.List;

public class WebServiceHelper {

    static String error;

    public static String getError() {
        return error;
    }

    public static void clearError() {
        error = "";
    }

    public static JSONObject getServerDataObject(String parametros, String url) {
        System.out.println("params: " + parametros);
        JSONObject response = null;
        InputStream is = sendRequest(parametros, url);
        if (is != null) {
            String resultado = getResponse(is);
            try {
                response = new JSONObject(resultado);
            } catch (JSONException e) {
                e.printStackTrace();
                Log.w("Error Parsing JSon", e.toString() + " ---- " + resultado);
                error = "Formato de respuesta incorrecto";
            }
        }
        return response;
    }

    public static JSONArray getServerDataArray(String parametros, String url) {
        JSONArray array = null;
        InputStream is = sendRequest(parametros, url);
        if (is != null) {
            String resultado = getResponse(is);
            System.out.println("Resultado: " + resultado);
            try {
                array = new JSONArray(resultado);
            } catch (JSONException e) {
                Log.e("Error Parsing JSon", e.toString() + " ---- " + resultado);
                error = "Formato de respuesta incorrecto";
            }
        }
        return array;
    }

    private static String fixParameters(String params) {
        String spaceReplacement = "%20";
        StringBuilder aux = new StringBuilder(params);
        int posStart = params.indexOf(" ");
        while (true) {
            if (posStart == -1)
            {
                break;
            }
            else
            {
                if(posStart>=0 && posStart<params.length())
                {
                    aux.insert(posStart, spaceReplacement);
                    aux.delete(posStart + 3, posStart + 4);
                    posStart = aux.indexOf(" ");
                }
            }
        }
        return aux.toString();
    }

    public static InputStream sendRequest(String parametros, String url) {
        error = "";
        parametros = fixParameters(parametros);
        System.out.println("Sending request to this url " + url);
        InputStream is = null;
        try {

            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost(url);
            List<NameValuePair> pairs = URLEncodedUtils.parse(
                    URI.create(parametros), HTTP.UTF_8);
            post.setEntity(new UrlEncodedFormEntity(pairs));
            HttpResponse response = client.execute(post);
            HttpEntity entity = response.getEntity();
            is = entity.getContent();
        } catch (HttpHostConnectException e) {
            e.printStackTrace();
            error = "Conexion rechazada";
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Error Http sendRequest", e.toString());
        } finally {
            return is;
        }

    }

    static String getResponse(InputStream is) {
        String result = "";
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "utf-8"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            result = sb.toString();
//            System.out.println("Result1: "+result);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Error en getResponse", e.toString());
        }
        return result;
    }


    /**
     * This is the explanation
     *
     * @param file
     * @param url
     * @throws IOException
     */

    public static void uploadFile(File file, String url, String user, String password, Context context) throws IOException {

        URL server = new URL(url);
        RequestParams params = new RequestParams();
        params.put("db_file", file);
        AsyncHttpClient client = new AsyncHttpClient();
//        AsyncHttpClient client = new AsyncHttpClient();
        client.clearCredentialsProvider();
        client.setBasicAuth(user, password, new AuthScope(server.getHost(), 80, AuthScope.ANY_REALM));
        RequestHandle r = doUpload(client, url, params, context);
        while ((!r.isFinished()) && (!r.isCancelled())) ;

    }


    private static RequestHandle doUpload(AsyncHttpClient client, String url, RequestParams params, final Context context) {
        SingletonPattern.uploading = true;
        System.out.println("voy a llamar clien.post");
        client.setLoggingEnabled(true);
//        client.cancelRequests(context,true);
//        client.setMaxConnections(1);
//        client.sett
        return client.post(context, url, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("uploader", "Status code: " + statusCode);
                Log.d("uploader", "Response: " + response.toString());
                if (statusCode != 200)
                    notifyFailure();
                else {
                    try {
                        if (response.getBoolean("success"))
                            notifySuccess();
                        else notifyFailure();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        notifyFailure();
                    }
                }
            }


            private void notifySuccess() {
                SingletonPattern.uploading = false;
                SingletonPattern.setUpdate_needed(context, false);
                Intent intent = new Intent();
                intent.setAction("sync_terminada");
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

            }

            private void notifyFailure() {
                SingletonPattern.uploading = false;
                SingletonPattern.setUpdate_needed(context, true);
                Intent intent = new Intent();
                intent.setAction("sync_terminada");
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

            }


            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.e("cclouds/uploader", "Hubo un fallo mientras se subía el archivo: " + statusCode);
                notifyFailure();
            }

            @Override
            public void onFinish() {
                SingletonPattern.uploading = false;
                super.onFinish();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e("cclouds/uploader", "Fallo!!! Status: " + statusCode + " Response string " + responseString);
                notifyFailure();
            }


            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e("cclouds/uploader", "Hubo un fallo mientras se subía el archivo: " + statusCode);
                notifyFailure();
            }
        });


    }
/*
    public static void uploadFile(File file, String url, Context context) throws IOException {
        RequestParams params = new RequestParams();
        params.put("db_file", file);
        SyncHttpClient client = new SyncHttpClient();
        doUpload(client,url,params,context);

    }*/


    public static boolean uploadFile1(File file, String url, String user, String password, Context context) throws IOException {
        URL server = new URL(url);

        FileInputStream fis = new FileInputStream(file);
        System.out.println("File size: " + file.length());
        System.out.println("File name: " + file.getName());
        BufferedOutputStream bufferedOutputStream = null;

        try {
            String boundary = "OK---------------thisismyboundary";
            String payloadHeader = "--" + boundary + "\n" +
                    "Content-Disposition: form-data; name=\"db_file\"; filename=\"filename.db\"\n" +
                    "Content-Type: application/octet-stream\n" +
                    "Content-Transfer-Encoding: binary\n\n";
            String ending = "\n--" + boundary + "--\n";
            HttpURLConnection connection = (HttpURLConnection) server.openConnection();
            String b64 = Base64.encodeToString((user +
                    ":" + password).getBytes("ISO-8859-1"), Base64.DEFAULT);
            System.out.println("b64: " + (b64.charAt(b64.length() - 1) == '\n'));
            if (b64.endsWith("\n")) {
                b64 = b64.substring(0, b64.length() - 1);
            }
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            connection.setRequestProperty("Authorization", "Basic " + b64);
            connection.setFixedLengthStreamingMode((int) (payloadHeader.getBytes("ISO-8859-1").length +
                    ending.getBytes("ISO-8859-1").length + file.length()));

            connection.setDoOutput(true);
            connection.setDoInput(true);
            bufferedOutputStream = new BufferedOutputStream(connection.getOutputStream());
            bufferedOutputStream.write(payloadHeader.getBytes("ISO-8859-1"));
            long written_amount = writeStream(fis, bufferedOutputStream);
            System.out.println("Writtent amount:  " + written_amount);
            bufferedOutputStream.write(ending.getBytes("ISO-8859-1"));
            bufferedOutputStream.flush();
            bufferedOutputStream.close();
            System.out.println("Response code: " + connection.getResponseCode());
            System.out.println("Response message: " + connection.getResponseMessage());
            if (connection.getResponseCode() == 200) {
                long contentLength = connection.getContentLength();
                System.out.println("Content length " + contentLength);
                BufferedInputStream bis = new BufferedInputStream(connection.getInputStream());
                StringBuilder stringBuilder = new StringBuilder();
                String response = getResponse(bis);
                connection.disconnect();

                JSONObject jsonObject = new JSONObject(response);

                if (jsonObject.getBoolean("success"))
                    return true;
                else return false;
            }
        } catch (IOException e) {
            e.printStackTrace();

        } catch (JSONException e) {
            e.printStackTrace();
        }
        fis.close();

        return false;


    }


    /***
     * @param outputFile Archivo de salida
     * @param url        Dirección de descarga
     * @throws FileNotFoundException
     * @throws MalformedURLException
     */
    public static boolean downloadFile(File outputFile, String url, final String user, final String pass) throws FileNotFoundException, MalformedURLException {
        FileOutputStream fos = new FileOutputStream(outputFile);
        URL server = new URL(url);
        try {

            Authenticator.setDefault(new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(user, pass.toCharArray());
                }
            });
            HttpURLConnection connection = (HttpURLConnection) server.openConnection();
            connection.setDoInput(true);
            //connection.setDoOutput(true);
            System.out.println("Response code: " + connection.getResponseCode());
            if (connection.getResponseCode() == 200) {
                long contentLength = connection.getContentLength();
                BufferedInputStream bis = new BufferedInputStream(connection.getInputStream());
                long downloaded_bytes = writeStream(bis, fos);
                fos.close();
                bis.close();
                if (contentLength == downloaded_bytes) {
                    return true;
                }
            }
            fos.close();
//            bis.close();
            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();

        }
        return false;
    }

    /**
     * Copia los bytes del Intputstream is al Outputstream os
     *
     * @param is El stream de entrada
     * @param os El stream de salida
     * @throws IOException En caso de que ocurra algún problema de Entrada/Salida
     */
    static long writeStream(InputStream is, OutputStream os) throws IOException {
        byte[] buffer = new byte[1024];
        int length;
        long written_amount = 0;
        while ((length = is.read(buffer)) > 0) {
            os.write(buffer, 0, length);
            written_amount += length;
        }
        os.flush();
        return written_amount;
    }


}
