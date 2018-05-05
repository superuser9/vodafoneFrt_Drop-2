package com.vodafone.frt.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class AppNetwork {

	private static final String TAG = AppNetwork.class.getSimpleName();
	private static int CONNECTION_TIMEOUT = 8000;
	public static AppNetwork instance = null;
	private boolean init = false;
	private Context ctx = null;
	public static Handler uiHandlr = null;
	public Handler netHandlr = null;
	int serverResponseCode = 0;
	private AppNetwork() {
	}

	public static AppNetwork getInstance() {
		if (instance == null)
			instance = new AppNetwork();
		return instance;
	}


	/**
	 * To initialize network message loop to handle network calls.
	 *
	 * @param ctx
	 * @return {@link if initialization is success then return <code>true</code>
	 *         else <code>false</code>}
	 */
	public boolean init(Context ctx) {
		if (init)
			return true;
		if (ctx == null)
			return init;
		this.ctx = ctx;
		init = true;
		return init;
	}

	/** Checks whether the device currently has a network connection 
	 * @param context TODO*/
	public boolean isDeviceOnline(Context context) {
		ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connMgr != null) {
			NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
			if (networkInfo != null && networkInfo.isConnected()) {
				return true;
			}
		}
		return true;
	}// End of isDeviceOnline().


	public ReqRespBean sendHttpRequest(ReqRespBean mReqRespBean){

		//TODO: SSL Validation disable

		if(mReqRespBean == null ){
			Log.e(TAG, "Request is null");
			Log.i(TAG,"sendHttpRequest(): Request params are null");
			return null;
		}
		String method = mReqRespBean.getRequestmethod();
		String url = mReqRespBean.getUrl();
		String requestJson = mReqRespBean.getJson();
		HashMap<String, String> header = mReqRespBean.getHeader();
		String mimeType = mReqRespBean.getMimeType();

		Log.i(TAG,"sendHttpRequest():method:"+method);
		Log.i(TAG,"sendHttpRequest(): Url:"+url);
		Log.i(TAG,"sendHttpRequest():Request jason:"+requestJson);
		Log.i(TAG,"sendHttpRequest():mimeType:"+mimeType);
		int read;
		if((url == null || url.length() == 0)){
			System.out.println( TAG + "httpPostRequest: IllegalArgument: Url is null");
			Log.e(TAG,"sendHttpRequest(): Request URl is null");
			Log.e(TAG, "URL is null");
			return null;
		}/*else if(! mReqRespBean.getRequestmethod().equalsIgnoreCase("get")
				|| ! mReqRespBean.getRequestmethod().equalsIgnoreCase("post")){
			System.out.println( TAG + "httpPostRequest: IllegalArgument:invalid method type:"+mReqRespBean.getRequestmethod());
			Log.e(TAG, "invalid method type:" + mReqRespBean.getRequestmethod());
			return  null;
		}*/
		Log.i(TAG, "Request data:"+requestJson);
		HttpURLConnection conn = null;
		try{
			URL outUrl = new URL(url);
			URI uri = new URI(outUrl.getProtocol(), outUrl.getUserInfo(), outUrl.getHost(), outUrl.getPort(), outUrl.getPath(), outUrl.getQuery(), outUrl.getRef());
			outUrl = uri.toURL();
			Log.i(TAG, "Url:"+outUrl);
			conn = (HttpURLConnection)outUrl.openConnection();
			if(conn != null) {
				conn.setConnectTimeout(CONNECTION_TIMEOUT);
				HttpURLConnection.setFollowRedirects(true);
				conn.setDoOutput(method.equalsIgnoreCase("GET"));
				conn.setRequestMethod(method.toUpperCase());
				conn.setRequestProperty("Content-Type", mimeType);
				conn.setRequestProperty("Connection", "Close");
				System.setProperty("http.keepAlive", "false");
				if(header != null){
					//conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
					Iterator it = header.entrySet().iterator();
					while (it.hasNext()) {
						Map.Entry pair = (Map.Entry)it.next();
						//System.out.println(pair.getKey() + " = " + pair.getValue());
						Log.i(TAG,"Header:"+pair.getKey().toString()+":"+pair.getValue().toString());
						conn.setRequestProperty(pair.getKey().toString(), pair.getValue().toString());
						it.remove();
					}
				}
				if(requestJson != null) {
					/*OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
					wr.write(requestJson);
					wr.flush();
					wr.close();*/
					OutputStream outputStream = new BufferedOutputStream(conn.getOutputStream());
					BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "utf-8"));
					writer.write(requestJson);
					writer.flush();
					writer.close();
					outputStream.close();
				}
			}
			conn.connect();
			mReqRespBean.setHttpResp(conn.getResponseCode());
			InputStream iStrm = null;
			//AppLogUtil.getInstance().writeLog(TAG,"sendHttpRequest():Http Response Code:"+conn.getResponseCode());
			Log.i(TAG,"sendHttpRequest(): HttpResponse:"+conn.getResponseCode());
			if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
				Log.i(TAG,"sendHttpRequest(): HttpResponse");
				iStrm = conn.getInputStream();

			}else{
				int respCode = conn.getResponseCode();
				iStrm = conn.getErrorStream();
			}
			mReqRespBean.setHttpResponseCode(conn.getResponseCode());
			if(iStrm == null){
				System.out.println(TAG + "httpPostRequest: No inputStream is created.");
				return null;
			}
			else{
				/*StringBuffer sb = new StringBuffer();
				while((read = iStrm.read() ) != -1){
					sb.append((char) read);
				}
				responseJson = sb.toString();*/
				BufferedReader br = new BufferedReader(new InputStreamReader(iStrm));
				StringBuffer sb  = new StringBuffer();
				String line = "";
				while(( line = br.readLine())  != null){
					sb.append(line);
				}
				mReqRespBean.setJson(sb.toString());
				Log.i(TAG, "Response json:" + sb.toString());
				Log.i(TAG,"sendHttpRequest():Response data:"+sb.toString());
				br.close();
			}
		}
		catch(Exception e){
			mReqRespBean.setJson(e.getMessage());
			System.out.println( TAG + "httpPostRequest:Caught an exception when sending request:" + e);
			Log.i(TAG, "sendHttpRequest():Exception:" + e);
		}finally {
			if (conn != null) {
				conn.disconnect();
			}
		}
		return mReqRespBean;
	}// End of httpPostRequest().

}// End of class.
