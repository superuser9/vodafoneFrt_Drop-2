package com.vodafone.frt.network;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Message;


public class AsyncThread extends AsyncTask<ReqRespBean, Integer, ReqRespBean> {

    private ProgressDialog mProgressDialog;
    private Context ctx;

    @Override
    protected void onPreExecute() {

        try {
            if (mProgressDialog != null)
                // new BlurAsyncTask().execute();
                mProgressDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onPreExecute();
    }

    @Override
    protected ReqRespBean doInBackground(ReqRespBean... params) {
        ReqRespBean mReqRespBean = params[0];
        AppNetwork.getInstance().init(mReqRespBean.getCtx());
        if (mReqRespBean.getJson() != null) {
            mReqRespBean.setJson(mReqRespBean.getJson());
        }
        if (!isCancelled()) {
            mReqRespBean = AppNetwork.getInstance().sendHttpRequest(mReqRespBean);
        }
        return mReqRespBean;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onCancelled(ReqRespBean reqRespBean) {
        super.onCancelled(reqRespBean);
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    @Override
    protected void onPostExecute(ReqRespBean result) {
        try {
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
                mProgressDialog = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (result != null && result.getmHandler() != null) {
            Message message = new Message();
            message.obj = result;
            result.getmHandler().sendMessage(message);
        } else {
            //TODO: Set data globally or insert into db.
        }
        super.onPostExecute(result);
    }

    public void initProgressDialog(Context context, DialogInterface.OnCancelListener mOnCancelListener) {
        ctx = context;
        mProgressDialog = new ProgressDialog(context);
        // mProgressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
        // mProgressDialog = new CustomProgressDialog(context, R.style.progress_dialog_theme);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setOnCancelListener(mOnCancelListener);
        //Bitmap map = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.progress_bg);
        //Bitmap fast = BlurBuilder.fastblur(map, 0.2f,90);
        //final Drawable draw = new BitmapDrawable(ctx.getResources(), map);
        // Window window = mProgressDialog.getWindow();
        //window.setBackgroundDrawable(draw);
        // window.setBackgroundDrawableResource(R.drawable.progress_bg);
        //window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        // window.setGravity(Gravity.CENTER);

        //mProgressDialog.setMessage("Please wait...");
        //ProgressBar spinner = new android.widget.ProgressBar(context,null,android.R.attr.progressBarStyle);
        //spinner.getIndeterminateDrawable().setColorFilter(0xFFFF0000, android.graphics.PorterDuff.Mode.MULTIPLY);


    }

}
