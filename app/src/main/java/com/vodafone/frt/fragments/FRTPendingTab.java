package com.vodafone.frt.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.vodafone.frt.R;
import com.vodafone.frt.activities.FRTRouteControlerTaskActivityFRT;
import com.vodafone.frt.activities.PTRMyTaskActivity;
import com.vodafone.frt.activities.PTRRouteControllerTaskActivity;
import com.vodafone.frt.adapters.FRTRouteAdapterPendingFRT;
import com.vodafone.frt.adapters.PTRRouteAdapterPending;
import com.vodafone.frt.app.FRTApp;
import com.vodafone.frt.callbacks.FRTCallBackForIdFind;
import com.vodafone.frt.callbacks.FRTCallBackInitViews;
import com.vodafone.frt.callbacks.FRTCallBackSetListeners;
import com.vodafone.frt.callbacks.FRTCallbackTaskList;
import com.vodafone.frt.callbacks.PTRCallbackTaskList;
import com.vodafone.frt.constants.FRTConstants;
import com.vodafone.frt.fonts.FRTTextviewTrebuchetMS;
import com.vodafone.frt.models.FRTResponseRouteCommonModelFRT;
import com.vodafone.frt.models.FRTResponseTaskDetailForFrtModel;
import com.vodafone.frt.models.PTRResponseMyTaskModel;
import com.vodafone.frt.models.PTRResponseRouteCommonModel;
import com.vodafone.frt.preferences.FRTSharePrefUtil;
import com.vodafone.frt.utility.FRTUtility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class FRTPendingTab extends Fragment implements FRTCallBackInitViews, FRTCallBackForIdFind, FRTCallBackSetListeners,
        FRTCallbackTaskList, FRTRouteAdapterPendingFRT.SelectionViewAttendanceListener {
    private final FRTPendingTab frtPendingTab = this;
    private FRTCallBackForIdFind frtCallBackForIdFind;
    private FRTApp frtApp;
    private PTRMyTaskActivity frtMyTaskActivity;
    private FRTTextviewTrebuchetMS norecord;
    private ListView listPending;
    private View rootView;
    private List<FRTResponseRouteCommonModelFRT> frtResponseRouteCommonModelList;
    private List<FRTResponseTaskDetailForFrtModel> frtMyTaskModelList;
    private FRTSharePrefUtil frtSharePrefUtil;
    private FRTUtility frtUtility;

    public FRTPendingTab() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_frtpending_tab, container, false);
        callBackSetUp();
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        createAdapter();
    }

    /**
     * This method initialize the callback and objects
     */
    private void callBackSetUp() {
        FRTCallBackInitViews frtCallBackInitViews = frtPendingTab;
        frtCallBackForIdFind = frtPendingTab;
        FRTCallBackSetListeners frtCallBackSetListeners = frtPendingTab;
        frtCallBackInitViews.initAllViews();
        frtCallBackSetListeners.commonListeners();
    }

    @Override
    public void initAllViews() {
        listPending = (ListView) frtCallBackForIdFind.view(R.id.listpending);
        norecord = (FRTTextviewTrebuchetMS) frtCallBackForIdFind.view(R.id.norecordpending);
        frtMyTaskActivity = (PTRMyTaskActivity) getActivity();
        frtApp = (FRTApp) frtMyTaskActivity.getApplication();
        frtUtility = FRTUtility.getUtilityInstance();
        frtUtility.setContext(frtMyTaskActivity);
        frtUtility.settingStatusBarColor(frtMyTaskActivity, R.color.colorPrimary);
        frtSharePrefUtil = FRTSharePrefUtil.getInstance(frtMyTaskActivity);
        //PTRRouteAdapterPending.SelectionViewAttendanceListener selectionViewAttendanceListener = frtPendingTab;
    }

    @Override
    public View view(int id) {
        return rootView.findViewById(id);
    }

    @Override
    public void commonListeners() {
    }

    private void createAdapter() {
        addModelToList();
        Collections.sort(frtResponseRouteCommonModelList);
        FRTRouteAdapterPendingFRT frtRouteAdapter = new FRTRouteAdapterPendingFRT(frtMyTaskActivity);
        frtRouteAdapter.setSelectionListener(frtPendingTab);
        frtRouteAdapter.setDataSet(frtResponseRouteCommonModelList);
        frtRouteAdapter.notifyDataSetChanged();
        listPending.setAdapter(frtRouteAdapter);
    }

    private void addModelToList() {
        frtResponseRouteCommonModelList = new ArrayList<>();
        if (frtMyTaskModelList != null) {
            for (FRTResponseTaskDetailForFrtModel frtResponseMyTaskModel :
                    frtMyTaskModelList) {
                if (frtResponseMyTaskModel.getStatus().equals("Pending")) {
                    FRTResponseRouteCommonModelFRT frtResponseRouteCommonModel = new FRTResponseRouteCommonModelFRT();
                    frtResponseRouteCommonModel.setCheckin_radius(frtResponseMyTaskModel.getCheckin_radius());
                    frtResponseRouteCommonModel.setIssue_id(frtResponseMyTaskModel.getIssue_id());
                    frtResponseRouteCommonModel.setUser_id(frtResponseMyTaskModel.getUser_id());
                    frtResponseRouteCommonModel.setIssue_type(frtResponseMyTaskModel.getIssue_type());
                    frtResponseRouteCommonModel.setTask_tracking_id(frtResponseMyTaskModel.getTask_tracking_id());
                    frtResponseRouteCommonModel.setCheckin_time(frtResponseMyTaskModel.getCheckin_time());
                    frtResponseRouteCommonModel.setCheckout_time(frtResponseMyTaskModel.getCheckout_time());
                    frtResponseRouteCommonModel.setCheckin_remarks(frtResponseMyTaskModel.getCheckin_remarks());
                    frtResponseRouteCommonModel.setCheckout_remarks(frtResponseMyTaskModel.getCheckout_remarks());
                    frtResponseRouteCommonModel.setAssigned_date(frtResponseMyTaskModel.getAssigned_date());
                    frtResponseRouteCommonModel.setManager_remark(frtResponseMyTaskModel.getManager_remark());
                    frtResponseRouteCommonModel.setPatroller_remark(frtResponseMyTaskModel.getPatroller_remark());
                    frtResponseRouteCommonModel.setLatitude(frtResponseMyTaskModel.getLatitude());
                    frtResponseRouteCommonModel.setLongitude(frtResponseMyTaskModel.getLongitude());
                    frtResponseRouteCommonModelList.add(frtResponseRouteCommonModel);
                }
            }
            if (frtResponseRouteCommonModelList.size() == 0) {
                listPending.setVisibility(View.GONE);
                norecord.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }


  /*  @Override
    public void onTaskData(List<PTRResponseMyTaskModel> frtResponseMyTaskModelList, int pendingCount) {
        frtMyTaskModelList = frtResponseMyTaskModelList;
        Log.d("listsss", frtMyTaskModelList + "");
    }
*/
    @Override
    public void onClickItem(int position) {

        frtSharePrefUtil.setString("tasktrackingid", frtResponseRouteCommonModelList.get(position).getTask_tracking_id() + "");
        frtSharePrefUtil.setString("issueId", frtResponseRouteCommonModelList.get(position).getIssue_id() + "");
        frtSharePrefUtil.setString("userId", frtResponseRouteCommonModelList.get(position).getUser_id() + "");
        frtSharePrefUtil.setString("checkin_radius",frtResponseRouteCommonModelList.get(position).getCheckin_radius() +"");
        frtSharePrefUtil.setString("latitudeAssigned",frtResponseRouteCommonModelList.get(position).getLatitude() +"");
        frtSharePrefUtil.setString("longitudeAssigned",frtResponseRouteCommonModelList.get(position).getLongitude()+"");
        Intent intent = new Intent(frtMyTaskActivity, FRTRouteControlerTaskActivityFRT.class);
        intent.putExtra("routetitle", frtResponseRouteCommonModelList.get(position).getIssue_type());
        intent.putExtra("keyparcel", frtResponseRouteCommonModelList.get(position));
        intent.putExtra("case", "Pending");
        frtUtility.logInfo(frtMyTaskActivity,"clicked on --> Pending tab (FRT) tasktrackingid: "+frtResponseRouteCommonModelList.get(position).getTask_tracking_id()+" issueId: "+
                frtResponseRouteCommonModelList.get(position).getIssue_id()+" userId: "+frtResponseRouteCommonModelList.get(position).getUser_id()+" checkin_radius :"+frtResponseRouteCommonModelList.get(position).getCheckin_radius()+
                " latitudeAssigned: "+ frtResponseRouteCommonModelList.get(position).getLatitude()+" longitudeAssigned: "+frtResponseRouteCommonModelList.get(position).getLongitude(),new FRTConstants());
        startActivity(intent);
        frtMyTaskActivity.overridePendingTransition(R.anim.enter_anim, R.anim.exit_anim);
        frtApp.setTabPosition(2);
    }

    @Override
    public void onTaskData(List<FRTResponseTaskDetailForFrtModel> frtResponseMyTaskModelList, int pendingCount) {
        frtMyTaskModelList = frtResponseMyTaskModelList;
        Log.d("listsss", frtMyTaskModelList + "");
    }
}
