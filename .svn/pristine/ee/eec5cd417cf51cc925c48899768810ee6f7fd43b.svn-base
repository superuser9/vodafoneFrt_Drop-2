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
import com.vodafone.frt.adapters.PTRRouteAdapterClosed;
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
public class PTRClosedTab extends Fragment implements FRTCallBackInitViews,
        FRTCallBackForIdFind, FRTCallBackSetListeners, PTRCallbackTaskList, PTRRouteAdapterClosed.SelectionViewAttendanceListener {
    private final PTRClosedTab frtClosedTab = this;
    private FRTCallBackForIdFind frtCallBackForIdFind;
    private FRTApp frtApp;
    private PTRMyTaskActivity frtMyTaskActivity;
    private FRTTextviewTrebuchetMS norecord;
    private ListView listClosed;
    private View rootView;
    private List<PTRResponseRouteCommonModel> frtResponseRouteCommonModelList;
    private List<PTRResponseMyTaskModel> frtMyTaskModelList;
    private FRTSharePrefUtil frtSharePrefUtil;
    FRTUtility frtUtility;
    public PTRClosedTab() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_frtclosed_tab, container, false);
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
        FRTCallBackInitViews frtCallBackInitViews = frtClosedTab;
        frtCallBackForIdFind = frtClosedTab;
        FRTCallBackSetListeners frtCallBackSetListeners = frtClosedTab;
        frtCallBackInitViews.initAllViews();
        frtCallBackSetListeners.commonListeners();
    }



    @Override
    public void initAllViews() {
        listClosed = (ListView) frtCallBackForIdFind.view(R.id.listclosed);
        norecord = (FRTTextviewTrebuchetMS) frtCallBackForIdFind.view(R.id.norecordclosed);
        frtMyTaskActivity = (PTRMyTaskActivity) getActivity();
        frtUtility = FRTUtility.getUtilityInstance();
        frtUtility.setContext(frtMyTaskActivity);
        frtUtility.settingStatusBarColor(frtMyTaskActivity, R.color.colorPrimary);
        frtSharePrefUtil = FRTSharePrefUtil.getInstance(frtMyTaskActivity);
        frtApp = (FRTApp) frtMyTaskActivity.getApplication();
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
        PTRRouteAdapterClosed frtRouteAdapter = new PTRRouteAdapterClosed(frtMyTaskActivity);
        frtRouteAdapter.setSelectionListener(frtClosedTab);
        frtRouteAdapter.setDataSet(frtResponseRouteCommonModelList);
        frtRouteAdapter.notifyDataSetChanged();
        listClosed.setAdapter(frtRouteAdapter);
    }

    private void addModelToList() {
        frtResponseRouteCommonModelList = new ArrayList<>();
        if (frtMyTaskModelList != null) {
            for (PTRResponseMyTaskModel frtResponseMyTaskModel :
                    frtMyTaskModelList) {
                if (frtResponseMyTaskModel.getStatus().equals("Completed")) {
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
            listClosed.setVisibility(View.GONE);
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
        frtSharePrefUtil.setString("self_check_in",frtResponseRouteCommonModelList.get(position).getSelf_check_in());
        Intent intent = new Intent(frtMyTaskActivity, PTRRouteControllerTaskActivity.class);
        intent.putExtra("routetitle", frtResponseRouteCommonModelList.get(position).getRoute());
        intent.putExtra("keyparcel", frtResponseRouteCommonModelList.get(position));
        intent.putExtra("case", "Completed");
        frtUtility.logInfo(frtMyTaskActivity,"clicked on --> Completed tab (PTR) routeassigmentid: "+frtResponseRouteCommonModelList.get(position).getRouteAssignmentId()+" routeId: "+
                frtResponseRouteCommonModelList.get(position).getRouteId()+" routeRefId: "+frtResponseRouteCommonModelList.get(position).getRouteRefId()+" self_check_in: "+
                frtResponseRouteCommonModelList.get(position).getSelf_check_in()+" routetitle: "+frtResponseRouteCommonModelList.get(position).getRoute(),new FRTConstants());
        startActivity(intent);
        frtApp.setActualStartTime(frtResponseRouteCommonModelList.get(position).getActual_start_time());
        frtApp.setActualEndTime(frtResponseRouteCommonModelList.get(position).getActual_end_time());
        frtMyTaskActivity.overridePendingTransition(R.anim.enter_anim, R.anim.exit_anim);
        frtApp.setTabPosition(3);
    }
}
