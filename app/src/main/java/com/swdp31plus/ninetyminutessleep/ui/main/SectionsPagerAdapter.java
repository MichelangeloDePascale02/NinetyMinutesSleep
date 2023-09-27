package com.swdp31plus.ninetyminutessleep.ui.main;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.swdp31plus.ninetyminutessleep.R;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    private SoundFragment firstFragment;
    private AlarmFragment secondFragment;

    private NewAlarmFragment newAlarmFragment;

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.sounds, R.string.daily_alarm};
    private final Context mContext;

    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        if (position == 0) {
            firstFragment = SoundFragment.newInstance(position + 1);
            return firstFragment;
        } else if (position == 1) {
            return NewAlarmFragment.newInstance(position + 1);
        } else {
            return PlaceholderFragment.newInstance(position + 1);
        }
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        // Show 2 total pages.
        return 2;
    }

    public SoundFragment getFirstFragment() {
        return firstFragment;
    }
}