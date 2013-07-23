package com.asynchrony.pair;

import android.os.Bundle;
import android.app.Activity;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		PrimesView view = new PrimesView(this);
		PrimesModel model = new PrimesModel();
		
		view.setPrimes(model.primesInRange(1, 100));
		
		setContentView(view.getView());
	}
}
