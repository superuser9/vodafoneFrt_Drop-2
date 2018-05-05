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
import com.vodafone.frt.activities.PTRMyTaskActivity;
import com.vodafone.frt.activities.PTRRouteControllerTaskActivity;
import com.vodafone.frt.adapters.PTRRouteAdapterInprogress;
import com.vodafone.frt.app.FRTApp;
import com.vodafone.frt.callbacks.FRTCallBackForIdFind;
import com.vodafone.frt.callbacks.FRTCallBackInitViews;
import com.vodafone.frt.callbacks.FRTCallBackSetListeners;
import com.vodafone.frt.callbacks.PTRCallbackTaskList;
import com.vodafone.frt.constants.FRTConstants;
import com.vodafone.frt.fonts.FRTTextviewTrebuchetMS;
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
public class PTRInProgressTab extends Fragment implements FRTCallBackInitViews, FRTCallBackForIdFind, FRTCallBackSetListeners,
        PTRCallbackTaskList, PTRRouteAdapterInprogress.SelectionViewAttendanceListener {
    private final PTRInProgressTab frtInProgressTab = this;
    private FRTCallBackForIdFind frtCallBackForIdFind;
    private FRTApp frtApp;
    private PTRMyTaskActivity frtMyTaskActivity;
    private FRTSharePrefUtil frtSharePrefUtil;
    private FRTTextviewTrebuchetMS norecord;
    private ListView listInProgress;
    private View rootView;
    private List<PTRResponseRouteCommonModel> frtResponseRouteCommonModelList;
    private List<PTRResponseMyTaskModel> frtMyTaskModelList;
    FRTUtility frtUtility;

    public PTRInProgressTab() {
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
        PTRRouteAdapterInprogress.SelectionViewAttendanceListener selectionViewAttendanceListener = frtInProgressTab;
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
        PTRRouteAdapterInprogress frtRouteAdapter = new PTRRouteAdapterInprogress(frtMyTaskActivity);
        frtRouteAdapter.setSelectionListener(frtInProgressTab);
        frtRouteAdapter.setDataSet(frtResponseRouteCommonModelList);
        frtRouteAdapter.notifyDataSetChanged();
        listInProgress.setAdapter(frtRouteAdapter);
    }

    private void addModelToList() {
        frtResponseRouteCommonModelList = new ArrayList<>();
        if (frtMyTaskModelList != null) {
            for (PTRResponseMyTaskModel frtResponseMyTaskModel :
                    frtMyTaskModelList) {
                if (frtResponseMyTaskModel.getStatus().equals("Inprogress") || frtResponseMyTaskModel.getStatus().equals("Paused") || frtResponseMyTaskModel.getStatus().equals("Resume")) {
                    PTRResponseRouteCommonModel frtResponseRouteCommonModel = new PTRResponseRouteCommonModel();
                    frtResponseRouteCommonModel.setRoute(frtResponseMyTaskModel.getRoute_name());
                    frtResponseRouteCommonModel.setRouteId(frtResponseMyTaskModel.getRoute_id());
                    frtResponseRouteCommonModel.setRouteRefId(frtResponseMyTaskModel.getRoute_ref_id());
                    frtResponseRouteCommonModel.setRouteAssignmentId(frtResponseMyTaskModel.getRoute_assigned_id());
                    frtResponseRouteCommonModel.setPlanned_start_time(frtResponseMyTaskModel.getPlanned_start_time());
                    frtResponseRouteCommonModel.setPlanned_end_time(frtResponseMyTaskModel.getPlanned_end_time());
                    frtResponseRouteCommonModel.setActual_start_time(frtResponseMyTaskModel.getActual_start_time());
                    frtResponseRouteCommonModel.setActual_end_time(frtResponseMyTaskModel.getActual_end_time());
                    frtResponseRouteCommonModel.setSelf_check_in(frtResponseMyTaskModel.getSelf_check_in());
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
    public void onTaskData(List<PTRResponseMyTaskModel> frtResponseMyTaskModelList, int pendingCount) {
        frtMyTaskModelList = frtResponseMyTaskModelList;
        Log.d("listsss", frtMyTaskModelList + "");
    }

    @Override
    public void onClickItem(int position) {
        frtSharePrefUtil.setString("routeassigmentid", frtResponseRouteCommonModelList.get(position).getRouteAssignmentId() + "");
        frtSharePrefUtil.setString("routeId", frtResponseRouteCommonModelList.get(position).getRouteId() + "");
        frtSharePrefUtil.setString("routeRefId", frtResponseRouteCommonModelList.get(position).getRouteRefId() + "");
        frtSharePrefUtil.setString("self_check_in",frtResponseRouteCommonModelList.get(position).getSelf_check_in());
        Intent intent = new Intent(frtMyTaskActivity, PTRRouteControllerTaskActivity.class);
        intent.putExtra("routetitle", frtResponseRouteCommonModelList.get(position).getRoute());
        intent.putExtra("keyparcel", frtResponseRouteCommonModelList.get(position));
        intent.putExtra("case", "Inprogress");
        frtUtility.logInfo(frtMyTaskActivity,"clicked on --> Inprogress tab (PTR) routeassigmentid: "+frtResponseRouteCommonModelList.get(position).getRouteAssignmentId()+" routeId: "+
                frtResponseRouteCommonModelList.get(position).getRouteId()+" routeRefId: "+frtResponseRouteCommonModelList.get(position).getRouteRefId()+" self_check_in: "+
                frtResponseRouteCommonModelList.get(position).getSelf_check_in()+" routetitle: "+frtResponseRouteCommonModelList.get(position).getRoute(),new FRTConstants());
        startActivity(intent);
        frtApp.setActualStartTime(frtResponseRouteCommonModelList.get(position).getActual_start_time());
        frtMyTaskActivity.overridePendingTransition(R.anim.enter_anim, R.anim.exit_anim);
        frtApp.setTabPosition(1);

    }
}
