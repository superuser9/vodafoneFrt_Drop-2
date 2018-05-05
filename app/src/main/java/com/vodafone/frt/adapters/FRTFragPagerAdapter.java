package com.vodafone.frt.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.vodafone.frt.fragments.FRTAssignedTab;
import com.vodafone.frt.fragments.FRTClosedTab;
import com.vodafone.frt.fragments.FRTInProgressTab;
import com.vodafone.frt.fragments.FRTPendingTab;
import com.vodafone.frt.models.FRTResponseTaskDetailForFrtModel;

import java.util.ArrayList;

/**
 * Created by ajay on 16/3/18
 */

public class FRTFragPagerAdapter extends FragmentStatePagerAdapter {
    private final int mNumOfTabs;
    private int inProgressCount;
    private ArrayList<FRTResponseTaskDetailForFrtModel> frtResponseMyTaskModelList;

    public FRTFragPagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = new FRTAssignedTab();
                ((FRTAssignedTab) fragment).onTaskData(frtResponseMyTaskModelList, inProgressCount);
                break;
            case 1:
                fragment = new FRTInProgressTab();
               ((FRTInProgressTab) fragment).onTaskData(frtResponseMyTaskModelList, inProgressCount);
                break;
          /*  case 2:
                fragment = new FRTPendingTab();
              ((FRTPendingTab) fragment).onTaskData(frtResponseMyTaskModelList, inProgressCount);
                break;*/
            case 2:
                fragment = new FRTClosedTab();
              ((FRTClosedTab) fragment).onTaskData(frtResponseMyTaskModelList, inProgressCount);
                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }

    public void setTaskList(ArrayList<FRTResponseTaskDetailForFrtModel> frtTaskModelList) {
        frtResponseMyTaskModelList = frtTaskModelList;
    }

    public void setInProgressCount(int inProgressCount) {
        this.inProgressCount = inProgressCount;
    }
}
