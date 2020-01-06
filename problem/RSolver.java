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
	private static double durationLimit = 3.5*60;
	private int cap15;
	private int cap12;
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
		CreateAllNodesAndCustomerLists(this.numberOfCustomers);
		CalculateDurationMatrix();
	}
	
	
	public void solve() {
			
	      //Create an object s with the current solution (initially empty)
	      s = new Solution();
	      s.selectionMatrix = new boolean[allNodes.size()];
	      s.routes.clear();

	      //Create an object s with the best solution (initially empty)
	      bestSolution = new Solution();
	      bestSolution.selectionMatrix = new boolean[allNodes.size()];
	      
	      SolutionConstructor(s);
	       
	}
	
	public void CalculateDurationMatrix() { 
	    durationMatrix = new double[allNodes.size()][allNodes.size()];
        for (int i = 0; i < allNodes.size(); i++) {
            for (int j = 0; j < allNodes.size(); j++) {
                durationMatrix[i][j] = (dist[i][j]/35)*60;
            }
        }
	}
	
	public void SolutionConstructor(Solution s) {
		modelIsFeasible = true;
		Route route = new Route();
		Node bestInsertion;
		for (int insertions = 0; insertions < customers.size();) {
			if (modelIsFeasible == false) {
				route = new Route();
			}
			if (route.customers == null) { 
				route.customers.add(depot);
			}
			bestInsertion = IdentifyBestInsertion(route);
			
			
		}
	}
	
	public Node IdentifyBestInsertion(Route route) {
		
		Node best;
		for (int i=0; i <customers.size(); i++) {
			Node candidate = customers.get(i);
			if (candidate.delivered==false) {
				if ((route.truck.load + candidate.demand<=cap12) && numberofTrucks12>0){
					Node from = route.customers.get(route.customers.size()-1);
					double duration = durationMatrix[from.ID][candidate.ID];
					if (duration<durationLimit) {
						route.truck.tr12 = true;
						numberofTrucks12 -= 1;
					}
				}else if((route.truck.load + candidate.demand<=cap15) && numberofTrucks15>0) {
						route.truck.tr15 = true;
						numberofTrucks15 -= 1;
				} 
			}
		}
		return best;
	}
	
	public double objectiveFunction(Solution s) {
		double sum = 0;
		int FNode;
		int TNode;
		for(int i=0; i< s.routes.size(); i++ ) {
			for (int j=0; j< s.routes.get(i).customers.size(); j+=2) {
				FNode = s.routes.get(i).customers.get(j).ID;
				TNode = s.routes.get(i).customers.get(j+1).ID;
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
			for (int j=0; j<allNodes.size(); j++) {
				Node nd2 = allNodes.get(j);
				dist[i][j] = Math.sqrt(Math.pow((nd.x - nd2.x),2) + Math.pow((nd.y - nd2.y),2));
			}
		}
		
	}

}