package com.plivo.plivoaddressbook.adapters;

import com.plivo.plivoaddressbook.screens.dial.tabs.TabFragment;

import java.util.Arrays;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class TabsPagerAdapter extends FragmentStatePagerAdapter {

    private List<TabFragment> pagerFragments;

    public TabsPagerAdapter(FragmentManager fragmentManager, TabFragment... fragments) {
        super(fragmentManager);
        if (fragments != null && fragments.length > 0) {
            pagerFragments = Arrays.asList(fragments);
        }
    }

    @Override
    public Fragment getItem(int position) {
        return pagerFragments != null && position < pagerFragments.size() ?
                pagerFragments.get(position) :
                null;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return pagerFragments != null && position < pagerFragments.size() ?
                pagerFragments.get(position).getTitle() :
                "Unknown";
    }
}
