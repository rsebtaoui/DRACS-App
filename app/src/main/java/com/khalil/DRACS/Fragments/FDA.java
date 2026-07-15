package com.khalil.DRACS.Fragments;

import com.khalil.DRACS.R;

public class FDA extends BaseContentFragment {

    @Override
    protected String getPageId() {
        return "fda";
    }

    @Override
    protected int getHomeActionId() {
        return R.id.action_FDA_to_home;
    }
}
