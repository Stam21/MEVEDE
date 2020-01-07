package com.vehiclerouting.problem;

import java.util.ArrayList;
import java.util.Random;
import java.lang.Math;

public class RSolver {
	
	private static ArrayList<Node> customers;
	private static ArrayList<Node> allNodes;
	private static double[][] dist;
	private static double[][] durationMatrix;
	private static Node depot;
	private static double durationLimit;
	private int cap15;
	private int cap12;
	private boolean inserted12;
	private boolean inserted15;
	private int numberOfCustomers;
	private int numberofTrucks15=15;
	private int numberofTrucks12=15;
	private boolean modelIsFeasible;
	Solution s;
	Solution bestSolution;
	
	public RSolver(int numberOfCustomers, int capacity15 , int capacity12) {
		this.numberOfCustomers = numberOfCustomers;
		this.cap15 = capacity15;
		this.cap12 = capacity12;
		
	}
	
	
	public void solve() {
		CreateAllNodesAndCustomerLists(numberOfCustomers);
		CalculateDurationMatrix();
	    //Create an object s with the current solution (initially empty)
	    s = new Solution();
	    s.routes.clear();

	    //Create an object s with the best solution (initially empty)
	    bestSolution = new Solution();
	      
	    SolutionConstructor(s);
	       
	}
	
	public void CalculateDurationMatrix() { 
	    durationMatrix = new double[allNodes.size()][allNodes.size()];
        for (int i = 0; i < allNodes.size(); i++) {
            for (int j = 0; j < allNodes.size(); j++) {
                durationMatrix[i][j] = Math.round((dist[i][j]/35)*60);
            }
        }
	}
	
	public void SolutionConstructor(Solution s) {
		inserted12 = true;
		inserted15 = true;
		modelIsFeasible = true;
		Node lastCandidate=depot;
		Route route = new Route();
		route.cust.add(depot);
		durationLimit = 210;
		for (int insertions = 0; insertions < customers.size();) {
			
			CustomerInsertion bestInsertion = new CustomerInsertion();
			bestInsertion.cost = Double.MAX_VALUE;
			if (route !=null) {
				IdentifyBestInsertion(route,bestInsertion,lastCandidate);
			}
			if (bestInsertion.cost<Double.MAX_VALUE) {
				ApplyInsertion(bestInsertion,s,route);
				insertions++;
				lastCandidate = bestInsertion.customer;
			}else {
					modelIsFeasible = false;
			}
			if (modelIsFeasible == false) {
				s.routes.add(route);
				route = new Route();
				route.cust.add(depot);
				lastCandidate = depot;
				modelIsFeasible = true;
				inserted12 = true;
				inserted15= true;
				if (bestInsertion.cap == cap12) {
					numberofTrucks12 -= 1;
				}else if (bestInsertion.cap == cap15) {
					numberofTrucks15 -=1;
				}
			}
			
		}
		
		double total_cost = objectiveFunction(s);
		System.out.println(total_cost);
		for (int i = 0; i<s.routes.size(); i++) {
			System.out.println(s.routes.get(i).load);
		}
	}
	
	
	
	public void IdentifyBestInsertion(Route route, CustomerInsertion bestInsertion,Node lastcand) {
		
		
		for (int i=0; i <customers.size(); i++) {
			Node candidate = customers.get(i);
			if (candidate.delivered==false) {
				if ((route.load + candidate.demand<=cap12) && numberofTrucks12>0 && inserted12==true){
					double duration = durationMatrix[lastcand.ID][candidate.ID];
					if (route.duration+duration<durationLimit) {
						double trialCost = dist[lastcand.ID][candidate.ID];
						if (trialCost < bestInsertion.cost) {
							bestInsertion.customer = candidate;
		                 	bestInsertion.cost = trialCost;
		                 	bestInsertion.duration = duration;
		                 	bestInsertion.cap = cap12;
		                 	bestInsertion.load = candidate.demand;
						}
					} 
				}else if((route.load + candidate.demand<=cap15) && numberofTrucks15>0 && inserted15==true) {
					double duration = durationMatrix[lastcand.ID][candidate.ID];
					if (route.duration+duration<durationLimit) {
						double trialCost = dist[lastcand.ID][candidate.ID];
						if (trialCost < bestInsertion.cost) {
							bestInsertion.customer = candidate;
		                 	bestInsertion.cost = trialCost;
		                 	bestInsertion.duration = duration;
		                 	bestInsertion.cap = cap15;
		                 	bestInsertion.load = candidate.demand;
						}
					} 
				}
			}
		}
	}
	
	public void ApplyInsertion(CustomerInsertion in, Solution s,Route route) {
		Node insertedCustomer = in.customer;
		route.cust.add(insertedCustomer);
		route.cost += in.cost;
		route.load += in.load;
		route.duration += in.duration;
		insertedCustomer.delivered = true;
		if (in.cap==cap12) { 
			inserted15 = false;
		}else if (in.cap==cap15) {
			inserted12 = false;
		}
	}
	public double objectiveFunction(Solution s) {
		double sum = 0;
		int FNode;
		int TNode;
		for(int i=0; i< s.routes.size(); i++ ) {
			for (int j=1; j< s.routes.get(i).cust.size(); j++) {
				FNode = s.routes.get(i).cust.get(j-1).ID;
				TNode = s.routes.get(i).cust.get(j).ID;
				sum += dist[FNode][TNode];
			}
		}
		return sum;
	}
	
	public void CreateAllNodesAndCustomerLists(int numberOfCustomers) {
		//Create the list with the customers
		customers = new ArrayList<>();
		int birthday = 28111998;
		Random ran = new Random(birthday);
		for (int i = 0 ; i < 100; i++) {
			Node cust = new Node();
			cust.x = ran.nextInt(100);
			cust.y = ran.nextInt(100);
			cust.demand = 100*(1 + ran.nextInt(5));
			cust.serviceTime = 15;
			cust.delivered = false;
			customers.add(cust);
		}
		//Build the allNodes array and the corresponding distance matrix
		allNodes = new ArrayList<>();
		depot = new Node();
		depot.x = 50;
		depot.y = 50;
		depot.demand = 0;
		allNodes.add(depot);
		for (int i = 0 ; i < customers.size(); i++) {
			Node cust = customers.get(i);
			allNodes.add(cust);
		}
		dist = new double[numberOfCustomers+1][numberOfCustomers+1];
		for (int i = 0 ; i < allNodes.size(); i++) {
			Node nd = allNodes.get(i);	
			nd.ID = i;
			for (int j=0; j<allNodes.size(); j++) {
				Node nd2 = allNodes.get(j);
				dist[i][j] = Math.round(Math.sqrt(Math.pow((nd.x - nd2.x),2) + Math.pow((nd.y - nd2.y),2)));
			}
		}
		
	}

}