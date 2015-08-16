package com.kiddos.rockpaperscissors;

import java.util.*;

import libsvm.*;

public class MachineLearning {
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
			vec[i].index = i;
			vec[i].value = v[i];
		}

		valueSet.add(c);
		vectorSet.add(vec);
	}
}
