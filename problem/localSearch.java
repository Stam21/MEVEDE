package problem;
import java.util.ArrayList;
import java.util.Random;







public class localSearch {
	
	static double [][] distanceMatrix;
    static ArrayList <Node> allNodes;
    static ArrayList <Node> customers;
    static Random ran;
	
	public static void main (String[] args) {
		CreateAllNodesAndCustomerLists(100);
		CalculateDistanceMatrix();
		
		for (double[] x : distanceMatrix)
		{
		   for (double y : x)
		   {
		        System.out.print(y + " ");
		        System.out.println();
		        
		   }
		   System.out.println();
		}
	}
	
    private static Solution LocalSearch(Solution s) {
        
       
        Solution bestSolution = cloneSolution(s);

        boolean terminationCondition = false;
        int localSearchIterator = 0;
        
        RelocationMove rm = new RelocationMove();
     

        while (terminationCondition == false)
        {
           
            InitializeTheRelocationMove(rm);

            

            findBestRelocationMove(rm, s);
            if (rm.positionOfRelocated != -1)
            {

                if (rm.moveCost < 0)
                {
                    applyRelocationMove(rm, s);
                }
                else
                {
                    terminationCondition = true;
                }
            }
//      
                     
            if (s.cost < bestSolution.cost)
            {
                bestSolution = cloneSolution(s);
            }
            
            localSearchIterator = localSearchIterator + 1;
        }
        
        return bestSolution;
    }
    
    
    private static void InitializeTheRelocationMove(RelocationMove rm) {
        rm.positionOfRelocated = -1;
        rm.positionToBeInserted = -1;
        rm.moveCost = Double.MAX_VALUE;
    }
    
    private static class RelocationMove 
    {
        int positionOfRelocated;
        int positionToBeInserted;
        double moveCost;
        
        public RelocationMove() 
        {
        }
    }
    
    
    private static void findBestRelocationMove(RelocationMove rm, Solution s) 
    {
        double bestMoveCost = Double.MAX_VALUE;
        for (int x=1; x < s.routes.size(); x ++) {
            for (int relIndex = 1; relIndex < s.routes.get(x).cust.size() - 1; relIndex++)
            {
                Node A = s.routes.get(x).cust.get(relIndex - 1);
                Node B = s.routes.get(x).cust.get(relIndex);
                Node C = s.routes.get(x).cust.get(relIndex + 1);
                
                for (int afterInd = 0; afterInd < s.routes.get(x).cust.size() -1; afterInd ++)
                {
                    
                    if (afterInd != relIndex && afterInd != relIndex - 1)
                    {
                        Node F = s.routes.get(x).cust.get(afterInd);
                        Node G = s.routes.get(x).cust.get(afterInd + 1);
                        
                        double costRemoved1 = distanceMatrix[A.ID][B.ID] + distanceMatrix[B.ID][C.ID];
                        double costRemoved2 = distanceMatrix[F.ID][G.ID];
                        double costRemoved = costRemoved1 + costRemoved2;
                        
                        double costAdded1 = distanceMatrix[A.ID][C.ID];
                        double costAdded2 = distanceMatrix[F.ID][B.ID] + distanceMatrix[B.ID][G.ID];
                        double costAdded = costAdded1 + costAdded2;
                        
                        double moveCost = costAdded - costRemoved;
                        
                        if (moveCost < bestMoveCost)
                        {
                            bestMoveCost = moveCost;
                            
                            rm.positionOfRelocated = relIndex;
                            rm.positionToBeInserted = afterInd;
                            rm.moveCost = moveCost;
                        }
                    }
                }
            }
        }

    }

    private static void applyRelocationMove(RelocationMove rm, Solution s) 
    {
    	for (int x=1; x < s.routes.size(); x ++) {
	        Node relocatedNode = s.routes.get(x).cust.get(rm.positionOfRelocated);
	        
	        s.routes.get(x).cust.remove(rm.positionOfRelocated);
	 
	        if (rm.positionToBeInserted < rm.positionOfRelocated)
	        {
	            s.routes.get(x).cust.add(rm.positionToBeInserted + 1, relocatedNode);
	        }
	        
	        else
	        {
	            s.routes.get(x).cust.add(rm.positionToBeInserted, relocatedNode);
	        }
	        
	        
	        double newSolutionCost = 0;
	        for (int y = 0; y < s.routes.get(x).cust.size();)
	        for (int i = 0 ; i < s.routes.get(x).cust.size() - 1; i++)
	        {
	            Node A = s.routes.get(x).cust.get(i);
	            Node B = s.routes.get(x).cust.get(i + 1);
	            newSolutionCost = newSolutionCost + distanceMatrix[A.ID][B.ID];
	        }
	        
	        if (s.cost + rm.moveCost != newSolutionCost)
	        {
	            System.out.println("Something Went wrong with the cost calculations !!!!");
	        }
	        
	        
	        s.cost = s.cost + rm.moveCost;
	        s.routes.get(x).cost = s.routes.get(x).cost + rm.moveCost;
    	}
    }
	
	public static void CreateAllNodesAndCustomerLists(int numberOfCustomers) {
		 //Create the list with the customers
		 customers = new ArrayList();
		 int birthday = 9021998; // if your bday is on 9 feb 1998
		 Random ran = new Random(birthday);
		 for (int i = 0 ; i < 100; i++)
		 {
		 Node cust = new Node();
		 cust.x = ran.nextInt(100);
		 cust.y = ran.nextInt(100);
		 cust.demand = 100*(1 + ran.nextInt(5));
		 cust.serviceTime = 0.25;
		 customers.add(cust);
		 }

		 
		//Build the allNodes array and the corresponding distance matrix
		 allNodes = new ArrayList();
		 Node depot = new Node();
		 depot.x = 50;
		 depot.y = 50;
		 depot.demand = 0;
		 allNodes.add(depot);
		 for (int i = 0 ; i < customers.size(); i++)
		 {
		 Node cust = customers.get(i);
		 allNodes.add(cust);
		 }

		 for (int i = 0 ; i < allNodes.size(); i++)
		 {
		 Node nd = allNodes.get(i);
		 nd.ID = i;
		 }
	} 
	
    private static Solution cloneSolution(Solution sol) 
    {
        Solution out = new Solution();
        
        out.cost = sol.cost;
        for (int x=1; x < sol.routes.size(); x ++) {
        
        	out.routes.get(x).cost =sol.routes.get(x).cost;
        
        	//Need to clone: arraylists are objects
        	for (int i = 0 ; i < sol.routes.get(x).cust.size(); i++)
        	{
            Node n = sol.routes.get(x).cust.get(i);
            out.routes.get(x).cust.add(n);
        	}
        }
        
        return out;
        
    }
	
    private static void CalculateDistanceMatrix() {
        
        distanceMatrix = new double [allNodes.size()][allNodes.size()];
        for (int i = 0 ; i < allNodes.size(); i++)
        {
            Node from = allNodes.get(i);
            
            for (int j = 0 ; j < allNodes.size(); j++)
            {
                Node to = allNodes.get(j);
                
                double Delta_x = (from.x - to.x);
                double Delta_y = (from.y - to.y);
                double distance = Math.sqrt((Delta_x * Delta_x) + (Delta_y * Delta_y));
                
                distance = Math.round(distance);
                
                distanceMatrix[i][j] = distance;
            }
        }
    }
}
