package com.ray.api.handler;

import java.util.LinkedList;
import java.util.List;

import android.app.Activity;

public class ActivityManager {

	private List<Activity> activityList = new LinkedList<Activity>();

	private static ActivityManager instance = null;

	private ActivityManager() {
	}

	public static synchronized ActivityManager getInstance() {
		if (instance == null) {
			instance = new ActivityManager();
		}
		return instance;
	}

	public void addActivity(Activity activity) {
		activityList.add(activity);
	}

	public void removeActivity(Activity activity) {
		activityList.remove(activity);
	}

	public void finishActivity(Activity activity) {
		for (Activity act : activityList) {
			if (!act.equals(activity)) {
				act.finish();
			}
		}
	}

	public void exit() {
		for (Activity activity : activityList) {
			activity.finish();
		}
	}
}
