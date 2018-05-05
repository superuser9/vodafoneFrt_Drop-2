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
import com.vodafone.frt.adapters.PTRRouteAdapterAssigned;
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
public class PTRAssignedTab extends Fragment implements FRTCallBackInitViews, FRTCallBackForIdFind, FRTCallBackSetListeners,
        PTRCallbackTaskList, PTRRouteAdapterAssigned.SelectionViewAttendanceListener {
    private final PTRAssignedTab frtAssignedTab = this;
    private FRTCallBackForIdFind frtCallBackForIdFind;
    private FRTUtility frtUtility;
    private PTRMyTaskActivity frtMyTaskActivity;
    private FRTApp frtApp;
    private FRTTextviewTrebuchetMS norecord;
    private FRTSharePrefUtil frtSharePrefUtil;
    private int inProgressCount;
    private ListView listAssigned;
    private View rootView;
    private List<PTRResponseRouteCommonModel> frtResponseRouteCommonModelList;
    private List<PTRResponseMyTaskModel> frtMyTaskModelList;

    public PTRAssignedTab() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_frtassigned_tab, container, false);
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
        FRTCallBackInitViews frtCallBackInitViews = frtAssignedTab;
        frtCallBackForIdFind = frtAssignedTab;
        FRTCallBackSetListeners frtCallBackSetListeners = frtAssignedTab;
        frtCallBackInitViews.initAllViews();
        frtCallBackSetListeners.commonListeners();
    }

    @Override
    public void initAllViews() {
        listAssigned = (ListView) frtCallBackForIdFind.view(R.id.listassigned);
        norecord = (FRTTextviewTrebuchetMS) frtCallBackForIdFind.view(R.id.norecordassigned);
        frtMyTaskActivity = (PTRMyTaskActivity) getActivity();
        frtUtility = FRTUtility.getUtilityInstance();
        frtUtility.setContext(frtMyTaskActivity);
        frtUtility.settingStatusBarColor(frtMyTaskActivity, R.color.colorPrimary);
        frtApp = (FRTApp) frtMyTaskActivity.getApplication();
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
        PTRRouteAdapterAssigned frtRouteAdapter = new PTRRouteAdapterAssigned(frtMyTaskActivity);
        frtRouteAdapter.setSelectionListener(frtAssignedTab);
        frtRouteAdapter.setDataSet(frtResponseRouteCommonModelList);
        frtRouteAdapter.notifyDataSetChanged();
        listAssigned.setAdapter(frtRouteAdapter);
    }

    private void addModelToList() {
        frtResponseRouteCommonModelList = new ArrayList<>();
        if (frtMyTaskModelList != null) {
            for (PTRResponseMyTaskModel frtResponseMyTaskModel :
                    frtMyTaskModelList) {
                if (frtResponseMyTaskModel.getStatus().equals("Assigned")) {
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
            listAssigned.setVisibility(View.GONE);
            norecord.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onTaskData(List<PTRResponseMyTaskModel> frtResponseMyTaskModelList, int inProgressCount) {
        frtMyTaskModelList = frtResponseMyTaskModelList;
        Log.d("listsss", frtMyTaskModelList + "");
        this.inProgressCount = inProgressCount;
    }

    @Override
    public void onClickItem(int position) {
        if (inProgressCount == 0) {
            frtSharePrefUtil.setString("routeassigmentid", frtResponseRouteCommonModelList.get(position).getRouteAssignmentId() + "");
            frtSharePrefUtil.setString("routeId", frtResponseRouteCommonModelList.get(position).getRouteId() + "");
            frtSharePrefUtil.setString("routeRefId", frtResponseRouteCommonModelList.get(position).getRouteRefId() + "");
            frtSharePrefUtil.setString("self_check_in",frtResponseRouteCommonModelList.get(position).getSelf_check_in());
            Intent intent = new Intent(frtMyTaskActivity, PTRRouteControllerTaskActivity.class);
            intent.putExtra("routetitle", frtResponseRouteCommonModelList.get(position).getRoute());
            intent.putExtra("keyparcel", frtResponseRouteCommonModelList.get(position));
            intent.putExtra("case", "Assigned");
            frtUtility.logInfo(frtMyTaskActivity,"clicked on --> Assigned tab (PTR) routeassigmentid: "+frtResponseRouteCommonModelList.get(position).getRouteAssignmentId()+" routeId: "+
                    frtResponseRouteCommonModelList.get(position).getRouteId()+" routeRefId: "+frtResponseRouteCommonModelList.get(position).getRouteRefId()+" self_check_in: "+
                    frtResponseRouteCommonModelList.get(position).getSelf_check_in()+" routetitle: "+frtResponseRouteCommonModelList.get(position).getRoute(),new FRTConstants());
            startActivity(intent);
            frtMyTaskActivity.overridePendingTransition(R.anim.enter_anim, R.anim.exit_anim);
            frtApp.setTabPosition(0);
        } else {
            frtUtility.setSnackBar("Already a task is in progress!", listAssigned);
        }
    }
}
