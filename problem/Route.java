package com.vehiclerouting.problem;

import java.util.ArrayList;

public class Route {
	ArrayList<Node> customers;
	double cost;
	Truck truck;
	double duration;
	
	public Route() {
		customers = new ArrayList<>();
		cost=0;
		truck = new Truck();
		duration =0;
	}
	
}
