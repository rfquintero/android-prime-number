package com.asynchrony.pair;

import java.util.List;

import android.app.Activity;
import android.view.View;

public class PrimesView {

	public View view;
	
	public PrimesView(Activity activity) {
		view = new View(activity);
	}
	
	public View getView() {
		return view;
	}

	public void setPrimes(List<Integer> primes) {
		
	}
}
