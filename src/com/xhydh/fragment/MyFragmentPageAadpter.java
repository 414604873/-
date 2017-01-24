package com.xhydh.fragment;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class MyFragmentPageAadpter extends FragmentPagerAdapter {
	private ArrayList<Fragment> fragmentsList;

	public MyFragmentPageAadpter(FragmentManager fm) {
		super(fm);
		// TODO Auto-generated constructor stub
	}

	public MyFragmentPageAadpter(FragmentManager fm,
			ArrayList<Fragment> fragments) {
		super(fm);
		// TODO Auto-generated constructor stub
		this.fragmentsList = fragments;
	}

	@Override
	public Fragment getItem(int arg0) {
		// TODO Auto-generated method stub
		return fragmentsList.get(arg0);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return fragmentsList.size();
	}

}
