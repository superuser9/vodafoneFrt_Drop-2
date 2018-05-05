package com.vodafone.frt.callbacks;

import android.view.View;

/**
 * Created by vishal
 */

public interface FRTCallBackForIdFind {
    /**
     * This method is responsible for finding View ids at a time
     *
     * @param id view id
     * @return View instance
     */
    View view(int id);
}
