package com.vodafone.frt.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.vodafone.frt.fragments.PTRAssignedTab;
import com.vodafone.frt.fragments.PTRClosedTab;
import com.vodafone.frt.fragments.PTRInProgressTab;
import com.vodafone.frt.fragments.PTRPendingTab;
import com.vodafone.frt.models.PTRResponseMyTaskModel;

import java.util.ArrayList;

/**
 * Created by ajay on 3/30/18
 */

public class PTRFragPagerAdapter extends FragmentStatePagerAdapter {
    private final int mNumOfTabs;
    private int inProgressCount;
    private ArrayList<PTRResponseMyTaskModel> frtResponseMyTaskModelList;

    public PTRFragPagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        mNumOfTabs = NumOfTabs;
    }

    // comment
    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = new PTRAssignedTab();
                ((PTRAssignedTab) fragment).onTaskData(frtResponseMyTaskModelList, inProgressCount);
                break;
            case 1:
                fragment = new PTRInProgressTab();
                ((PTRInProgressTab) fragment).onTaskData(frtResponseMyTaskModelList, inProgressCount);
                break;
            case 2:
                fragment = new PTRPendingTab();
                ((PTRPendingTab) fragment).onTaskData(frtResponseMyTaskModelList, inProgressCount);
                break;
            case 3:
                fragment = new PTRClosedTab();
                ((PTRClosedTab) fragment).onTaskData(frtResponseMyTaskModelList, inProgressCount);
                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }

    public void setTaskList(ArrayList<PTRResponseMyTaskModel> frtTaskModelList) {
        frtResponseMyTaskModelList = frtTaskModelList;
    }

    public void setInProgressCount(int inProgressCount) {
        this.inProgressCount = inProgressCount;
    }
}
