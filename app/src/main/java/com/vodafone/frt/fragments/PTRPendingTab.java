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
import com.vodafone.frt.adapters.PTRRouteAdapterPending;
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
public class PTRPendingTab extends Fragment implements FRTCallBackInitViews, FRTCallBackForIdFind, FRTCallBackSetListeners, PTRCallbackTaskList, PTRRouteAdapterPending.SelectionViewAttendanceListener {
    private final PTRPendingTab frtPendingTab = this;
    private FRTCallBackForIdFind frtCallBackForIdFind;
    private FRTApp frtApp;
    private PTRMyTaskActivity frtMyTaskActivity;
    private FRTTextviewTrebuchetMS norecord;
    private ListView listPending;
    private View rootView;
    private List<PTRResponseRouteCommonModel> frtResponseRouteCommonModelList;
    private List<PTRResponseMyTaskModel> frtMyTaskModelList;
    private FRTSharePrefUtil frtSharePrefUtil;
    FRTUtility frtUtility;
    public PTRPendingTab() {
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
        PTRRouteAdapterPending.SelectionViewAttendanceListener selectionViewAttendanceListener = frtPendingTab;
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
        PTRRouteAdapterPending frtRouteAdapter = new PTRRouteAdapterPending(frtMyTaskActivity);
        frtRouteAdapter.setSelectionListener(frtPendingTab);
        frtRouteAdapter.setDataSet(frtResponseRouteCommonModelList);
        frtRouteAdapter.notifyDataSetChanged();
        listPending.setAdapter(frtRouteAdapter);
    }

    private void addModelToList() {
        frtResponseRouteCommonModelList = new ArrayList<>();
        if (frtMyTaskModelList != null) {
            for (PTRResponseMyTaskModel frtResponseMyTaskModel :
                    frtMyTaskModelList) {
                if (frtResponseMyTaskModel.getStatus().equals("Pending")) {
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
        intent.putExtra("case", "Pending");
        frtUtility.logInfo(frtMyTaskActivity,"clicked on --> Pending tab (PTR) routeassigmentid: "+frtResponseRouteCommonModelList.get(position).getRouteAssignmentId()+" routeId: "+
                frtResponseRouteCommonModelList.get(position).getRouteId()+" routeRefId: "+frtResponseRouteCommonModelList.get(position).getRouteRefId()+" self_check_in: "+
                frtResponseRouteCommonModelList.get(position).getSelf_check_in()+" routetitle: "+frtResponseRouteCommonModelList.get(position).getRoute(),new FRTConstants());
        startActivity(intent);
        frtApp.setActualStartTime(frtResponseRouteCommonModelList.get(position).getActual_start_time());
        frtApp.setActualEndTime(frtResponseRouteCommonModelList.get(position).getActual_end_time());
        frtMyTaskActivity.overridePendingTransition(R.anim.enter_anim, R.anim.exit_anim);
        frtApp.setTabPosition(2);
    }
}
