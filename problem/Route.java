package com.vehiclerouting.problem;

import java.util.ArrayList;

public class Route {
	ArrayList<Node> cust;
	double cost;
	double load;
	double duration;
	
	public Route() {
		cust = new ArrayList<>();
		cost=0;
		load =0;
		duration =0;
	}
	
}
