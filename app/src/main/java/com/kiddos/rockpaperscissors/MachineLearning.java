package com.kiddos.rockpaperscissors;

import java.io.Serializable;
import java.util.*;

import android.util.Log;
import libsvm.*;

public class MachineLearning implements Serializable {
	private svm_model model;
	private svm_problem problem;
	private ArrayList<svm_node[]> vectorSet;
	private ArrayList<Double> valueSet;
	private svm_parameter parameter;

	public MachineLearning() {
		vectorSet = new ArrayList<>();
		valueSet = new ArrayList<>();
		problem = new svm_problem();

		// parameter
		parameter = new svm_parameter();
		parameter.svm_type = svm_parameter.NU_SVC;
		parameter.kernel_type = svm_parameter.RBF;
		parameter.nu = 0.5;
		parameter.degree = 3;
		parameter.coef0 = 1;
		parameter.gamma = 1;
		parameter.cache_size = 100;

		parameter.C = 1;
		parameter.eps = 1e-3;
		parameter.p = 0.1;
		parameter.shrinking = 1;
		parameter.probability = 0;
		parameter.nr_weight = 0;
		parameter.weight_label = new int[0];
		parameter.weight = new double[0];
	}

	public void addVector(int[] v, double c) {
		svm_node[] vec = new svm_node[v.length];
		for (int i = 0 ; i < v.length ; i++) {
			vec[i] = new svm_node();
			vec[i].index = i;
			vec[i].value = v[i];
		}

		vectorSet.add(vec);
		valueSet.add(c);
	}

	public void train() {
		if (vectorSet.size() != 0) {
			if (problem == null)
				problem = new svm_problem();

			int l = vectorSet.size();
			int nodeL = vectorSet.get(0).length;
			problem.l = l;
			problem.x = new svm_node[l][nodeL];
			problem.y = new double[l];
			for (int i = 0 ; i < l ; i ++) {
				problem.x[i] = vectorSet.get(i);
				problem.y[i] = valueSet.get(i);
			}

			model = svm.svm_train(problem, parameter);
		} else {
			Log.d("ML", "no data to train");
		}
	}

	public void clearData() {
		vectorSet = new ArrayList<>();
		valueSet = new ArrayList<>();
		problem = new svm_problem();
		model = null;
	}

	public int predict(int[] series) {
		svm_node[] s = new svm_node[series.length];
		for (int i = 0 ; i < series.length ; i ++) {
			s[i] = new svm_node();
			s[i].index = i;
			s[i].value = series[i];
		}
		if (model != null) {
			double result = svm.svm_predict(model, s);
			return (int)result;
		}
		return -1;
	}
}
