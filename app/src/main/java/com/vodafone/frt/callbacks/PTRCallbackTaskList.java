package com.vodafone.frt.callbacks;

import com.vodafone.frt.models.PTRResponseMyTaskModel;

import java.util.List;

/**
 * Created by vishal on 19/12/17
 */

public interface PTRCallbackTaskList {
    void onTaskData(List<PTRResponseMyTaskModel> frtResponseMyTaskModelList, int pendingCount);
}
