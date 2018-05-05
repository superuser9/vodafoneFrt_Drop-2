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
import com.vodafone.frt.adapters.FRTRouteAdapterInProgressFRT;
import com.vodafone.frt.adapters.PTRRouteAdapterInprogress;
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
public class FRTInProgressTab extends Fragment implements FRTCallBackInitViews, FRTCallBackForIdFind, FRTCallBackSetListeners,
        FRTCallbackTaskList, FRTRouteAdapterInProgressFRT.SelectionViewAttendanceListener {
    private final FRTInProgressTab frtInProgressTab = this;
    private FRTCallBackForIdFind frtCallBackForIdFind;
    private FRTApp frtApp;
    private PTRMyTaskActivity frtMyTaskActivity;
    private FRTSharePrefUtil frtSharePrefUtil;
    private FRTTextviewTrebuchetMS norecord;
    private ListView listInProgress;
    private View rootView;
    private List<FRTResponseRouteCommonModelFRT> frtResponseRouteCommonModelList;
    private List<FRTResponseTaskDetailForFrtModel> frtMyTaskModelList;
    private int inProgressCount;
    private FRTUtility frtUtility;

    public FRTInProgressTab() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_frtin_progress_tab, container, false);
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
        FRTCallBackInitViews frtCallBackInitViews = frtInProgressTab;
        frtCallBackForIdFind = frtInProgressTab;
        FRTCallBackSetListeners frtCallBackSetListeners = frtInProgressTab;
        frtCallBackInitViews.initAllViews();
        frtCallBackSetListeners.commonListeners();
    }

    @Override
    public void initAllViews() {
        listInProgress = (ListView) frtCallBackForIdFind.view(R.id.listprogress);
        norecord = (FRTTextviewTrebuchetMS) frtCallBackForIdFind.view(R.id.norecordinprogress);
        frtMyTaskActivity = (PTRMyTaskActivity) getActivity();
        frtApp = (FRTApp) frtMyTaskActivity.getApplication();
       frtUtility = FRTUtility.getUtilityInstance();
        frtUtility.setContext(frtMyTaskActivity);
        frtUtility.settingStatusBarColor(frtMyTaskActivity, R.color.colorPrimary);
        frtSharePrefUtil = FRTSharePrefUtil.getInstance(frtMyTaskActivity);
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
        FRTRouteAdapterInProgressFRT frtRouteAdapter = new FRTRouteAdapterInProgressFRT(frtMyTaskActivity);
        frtRouteAdapter.setSelectionListener(frtInProgressTab);
        frtRouteAdapter.setDataSet(frtResponseRouteCommonModelList);
        frtRouteAdapter.notifyDataSetChanged();
        listInProgress.setAdapter(frtRouteAdapter);
    }

    private void addModelToList() {
        frtResponseRouteCommonModelList = new ArrayList<>();
        if (frtMyTaskModelList != null) {
            for (FRTResponseTaskDetailForFrtModel frtResponseMyTaskModel :
                    frtMyTaskModelList) {
                if (frtResponseMyTaskModel.getStatus().equals("InProgress") || frtResponseMyTaskModel.getStatus().equals("Paused") || frtResponseMyTaskModel.getStatus().equals("Resume")) {
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
                    frtResponseRouteCommonModel.setLatitude(frtResponseMyTaskModel.getLatitude());
                    frtResponseRouteCommonModel.setLongitude(frtResponseMyTaskModel.getLongitude());
                    frtResponseRouteCommonModel.setPatroller_remark(frtResponseMyTaskModel.getPatroller_remark());
                    frtResponseRouteCommonModel.setManager_remark(frtResponseMyTaskModel.getManager_remark());
                    frtResponseRouteCommonModelList.add(frtResponseRouteCommonModel);
                }
            }
        }
        if (frtResponseRouteCommonModelList.size() == 0) {
            listInProgress.setVisibility(View.GONE);
            norecord.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public void onClickItem(int position) {
       // if (inProgressCount == 0) {
        frtSharePrefUtil.setString("tasktrackingid", frtResponseRouteCommonModelList.get(position).getTask_tracking_id() + "");
        frtSharePrefUtil.setString("issueId", frtResponseRouteCommonModelList.get(position).getIssue_id() + "");
        frtSharePrefUtil.setString("userId", frtResponseRouteCommonModelList.get(position).getUser_id() + "");
        frtSharePrefUtil.setString("checkin_radius",frtResponseRouteCommonModelList.get(position).getCheckin_radius() +"");
        frtSharePrefUtil.setString("latitudeAssigned",frtResponseRouteCommonModelList.get(position).getLatitude() +"");
        frtSharePrefUtil.setString("longitudeAssigned",frtResponseRouteCommonModelList.get(position).getLongitude()+"");
        frtSharePrefUtil.setString("caseSharedP","null");

        Intent intent = new Intent(frtMyTaskActivity, FRTRouteControlerTaskActivityFRT.class);
        intent.putExtra("routetitle", frtResponseRouteCommonModelList.get(position).getIssue_type());
        intent.putExtra("keyparcel", frtResponseRouteCommonModelList.get(position));
        intent.putExtra("AssignedLat",frtResponseRouteCommonModelList.get(position).getLatitude());
        intent.putExtra("case", "Inprogress");
        frtUtility.logInfo(frtMyTaskActivity,"clicked on --> Inprogress tab (FRT) tasktrackingid: "+frtResponseRouteCommonModelList.get(position).getTask_tracking_id()+" issueId: "+
                frtResponseRouteCommonModelList.get(position).getIssue_id()+" userId: "+frtResponseRouteCommonModelList.get(position).getUser_id()+" checkin_radius :"+frtResponseRouteCommonModelList.get(position).getCheckin_radius() +" latitudeAssigned: "+
                frtResponseRouteCommonModelList.get(position).getLatitude()+" longitudeAssigned: "+frtResponseRouteCommonModelList.get(position).getLongitude(),new FRTConstants());
        startActivity(intent);
        frtMyTaskActivity.overridePendingTransition(R.anim.enter_anim, R.anim.exit_anim);
            frtApp.setTabPosition(1);
        /*} else {
            frtUtility.setSnackBar("Already a task is in progress!", listInProgress);
        }*/
    }

    @Override
    public void onTaskData(List<FRTResponseTaskDetailForFrtModel> frtResponseMyTaskModelList, int pendingCount) {
        frtMyTaskModelList = frtResponseMyTaskModelList;
        Log.d("listsss", frtMyTaskModelList + "");
        this.inProgressCount = inProgressCount;
    }
}
