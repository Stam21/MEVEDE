package com.vehiclerouting.problem;



public class RoutingProblem {

	public static void main(String[] args) {
		
		//This is a knapsack object
		RSolver rs = new RSolver(100,1500,1200);
		
		rs.solve();
	}
}
