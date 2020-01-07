package problem;

import java.util.ArrayList;

public class VND {
	static double [][] distanceMatrix;
    private static Solution VND(Solution s) {
        
        // What would happen if Solution bestSolution = s;
        Solution bestSolution = cloneSolution(s);

        boolean terminationCondition = false;
        int localSearchIterator = 0;
        
        RelocationMove rm = new RelocationMove();
        SwapMove sm = new SwapMove();
        TwoOpt top = new TwoOpt();
        
        int k = 1;
        int kmax = 3;

        while (k <= kmax)
        {
            InitializeMoves(rm, sm, top);
            FindBestNeighbor(k, s, rm, sm, top);
            
            if (MoveIsImproving(k, rm, sm, top))
            {
                ApplyMove(k, s, rm, sm, top);
                k = 1;
            }
            else
            {
                k = k + 1;
            }
        }
        
        return bestSolution;
    }
    private static void InitializeMoves(RelocationMove rm, SwapMove sm, TwoOpt top) {
        //Initialize the relocation move rm
        InitializeTheRelocationMove(rm);
        //Initialize the swap move sm
        InitializeTheSwapMove(sm);
        //Initialize the 2 opt move
        InitializeTheTwoOptMove(top);
    }
    
    private static void InitializeTheRelocationMove(RelocationMove rm) {
        rm.positionOfRelocated = -1;
        rm.positionToBeInserted = -1;
        rm.moveCost = Double.MAX_VALUE;
    }

    private static void InitializeTheSwapMove(SwapMove sm) {
        sm.positionOfFirst = -1;
        sm.positionOfSecond = -1;
        sm.moveCost = Double.MAX_VALUE;
    }

    private static void InitializeTheTwoOptMove(TwoOpt top) 
    {
        top.positionOfFirst = -1;
        top.positionOfSecond = -1;
        top.moveCost = Double.MAX_VALUE;
    }

    private static void findBestTwoOptMove(TwoOpt top, Solution s) 
    {     
    	for (int x=0; x < s.routes.size(); x++) {
    	
	        for (int firstIndex = 0; firstIndex < s.routes.get(x).cust.size() - 1; firstIndex++)
	        {
	            Node A = s.routes.get(x).cust.get(firstIndex);
	            Node B = s.routes.get(x).cust.get(firstIndex + 1);
	            
	            for (int secondIndex = firstIndex + 2; secondIndex < s.routes.get(x).cust.size() - 1; secondIndex ++)
	            {
	                Node K = s.routes.get(x).cust.get(secondIndex);
	                Node L = s.routes.get(x).cust.get(secondIndex + 1);
	
	                if (firstIndex == 0 && secondIndex == s.routes.get(x).cust.size() - 2)
	                {
	                    continue;
	                }
	                
	                double costAdded = distanceMatrix[A.ID][K.ID] + distanceMatrix[B.ID][L.ID];
	                double costRemoved = distanceMatrix[A.ID][B.ID] + distanceMatrix[K.ID][L.ID];
	                
	                double moveCost = costAdded - costRemoved;
	                
	                if (moveCost < top.moveCost)
	                {
	                    top.moveCost = moveCost;
	                    top.positionOfFirst = firstIndex;
	                    top.positionOfSecond = secondIndex;
	                }
	            }
	        }
    	}
    }
    
    private static void applyTwoOptMove(TwoOpt top, Solution s) 
    {
    	for (int x=0; x < s.routes.size(); x++) {
	        ArrayList modifiedRt = new ArrayList();
	        
	        for (int i = 0; i <= top.positionOfFirst; i++)
	        {
	            modifiedRt.add(s.routes.get(x).cust.get(i));
	        }
	        for (int i = top.positionOfSecond; i > top.positionOfFirst; i--)
	        {
	            modifiedRt.add(s.routes.get(x).cust.get(i));
	        }
	        for (int i = top.positionOfSecond + 1; i < s.routes.get(x).cust.size(); i++)
	        {
	            modifiedRt.add(s.routes.get(x).cust.get(i));
	        }
	        
	        s.routes.get(x).cust = modifiedRt;
	        
	        
	        double newSolutionCost = 0;
	        for (int i = 0 ; i < s.routes.get(x).cust.size() - 1; i++)
	        {
	            Node A = s.routes.get(x).cust.get(i);
	            Node B = s.routes.get(x).cust.get(i + 1);
	            newSolutionCost = newSolutionCost + distanceMatrix[A.ID][B.ID];
	        }
	        if (s.cost + top.moveCost != newSolutionCost)
	        {
	            System.out.println("Something Went wrong with the cost calculations !!!!");
	        }
	
	        s.routes.get(x).cost += top.moveCost;
	        s.cost += top.moveCost;
        
    	}
        
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
    
    private static class SwapMove 
    {
        int positionOfFirst;
        int positionOfSecond;
        
        double moveCost;
        
        public SwapMove() 
        {
            
        }
    }
    
    private static class TwoOpt {
    
        int positionOfFirst;
        int positionOfSecond;
        double moveCost;
    }
    

	private static Solution cloneSolution(Solution sol) 
	{
	    Solution out = new Solution();
	    for (int x=0; x < sol.routes.size(); x++) {
	    	
	    
		    out.cost = sol.cost;
		    
		    //No need to clone -- Basic Types
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
	
    private static void FindBestNeighbor(int k, Solution s, RelocationMove rm, SwapMove sm, TwoOpt top) 
    {
        if (k == 1)
        {
            findBestRelocationMove(rm, s);
        }
        else if (k == 2)
        {
            findBestSwapMove(sm, s);
        }
        else if (k == 3)
        {
            findBestTwoOptMove(top, s);
        }
    }

    private static boolean MoveIsImproving(int k, RelocationMove rm, SwapMove sm, TwoOpt top) 
    {
        if (k == 1)
        {
            if (rm.moveCost < 0)
            {
                return true;
            }
        }
        else if (k == 2)
        {
            if (sm.moveCost < 0)
            {
                return true;
            }
        }
        else if (k == 3)
        {
            if (top.moveCost < 0)
            {
                return true;
            }
        }
        
        return false;
    }

    private static void ApplyMove(int k, Solution s, RelocationMove rm, SwapMove sm, TwoOpt top) {
        
        if (k == 1)
        {
            applyRelocationMove(rm, s);
        }
        else if (k == 2)
        {
            applySwapMove(sm, s);
        }
        if (k == 3)
        {
            applyTwoOptMove(top, s);
        }
    
    }
    
    private static void applySwapMove(SwapMove sm, Solution s) 
    {
    	for (int x=0; x < s.routes.size(); x++) {
    	
	        Node swapped1 = s.routes.get(x).cust.get(sm.positionOfFirst);
	        Node swapped2 = s.routes.get(x).cust.get(sm.positionOfSecond);
	        
	        
	        s.routes.get(x).cust.set(sm.positionOfFirst, swapped2);
	        s.routes.get(x).cust.set(sm.positionOfSecond, swapped1);
	        
	 
	        double newSolutionCost = 0;
	        for (int i = 0 ; i < s.routes.get(x).cust.size() - 1; i++)
	        {
	            Node A = s.routes.get(x).cust.get(i);
	            Node B = s.routes.get(x).cust.get(i + 1);
	            newSolutionCost = newSolutionCost + distanceMatrix[A.ID][B.ID];
	        }
	        
	        if (s.cost + sm.moveCost != newSolutionCost)
	        {
	            System.out.println("Something Went wrong with the cost calculations !!!!");
	        }
	        
	  
	        s.cost = s.cost + sm.moveCost;
	        s.routes.get(x).cost = s.routes.get(x).cost + sm.moveCost;
    	}
    }
    
    private static void applyRelocationMove(RelocationMove rm, Solution s) 
    {
    	for (int x=0; x < s.routes.size(); x++) {
	        Node relocatedNode = s.routes.get(x).cust.get(rm.positionOfRelocated);
	        
	        //Take out the relocated node
	        s.routes.get(x).cust.remove(rm.positionOfRelocated);
	        
	        //Reinsert the relocated node into the appropriarte position
	        //Where??? -> after the node that WAS (!!!!) located in the rm.positionToBeInserted of the route
	        
	        //Watch out!!! 
	        //If the relocated customer is reinserted backwards we have to re-insert it in (rm.positionToBeInserted + 1)
	        if (rm.positionToBeInserted < rm.positionOfRelocated)
	        {
	        	s.routes.get(x).cust.add(rm.positionToBeInserted + 1, relocatedNode);
	        }
	        ////else (if it is reinserted forward) we have to re-insert it in (rm.positionToBeInserted)
	        else
	        {
	        	s.routes.get(x).cust.add(rm.positionToBeInserted, relocatedNode);
	        }
	        
	        //just for debugging purposes
	        // to test if everything is OK
	        double newSolutionCost = 0;
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
    
    private static void findBestRelocationMove(RelocationMove rm, Solution s) 
    {
        double bestMoveCost = Double.MAX_VALUE;
        for (int x=0; x < s.routes.size(); x++) {
        
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
    
    private static void findBestSwapMove(SwapMove sm, Solution s) 
    {
        double bestMoveCost = Double.MAX_VALUE;
        for (int x=0; x < s.routes.size(); x++) {
	        for (int firstIndex = 1; firstIndex < s.routes.get(x).cust.size() - 1; firstIndex++)
	        {
	            Node A = s.routes.get(x).cust.get(firstIndex - 1);
	            Node B = s.routes.get(x).cust.get(firstIndex);
	            Node C = s.routes.get(x).cust.get(firstIndex + 1);
	            
	           
	            for (int secondInd = firstIndex + 1; secondInd < s.routes.get(x).cust.size() -1; secondInd ++)
	            {
	                Node D = s.routes.get(x).cust.get(secondInd - 1);
	                Node E = s.routes.get(x).cust.get(secondInd);
	                Node F = s.routes.get(x).cust.get(secondInd + 1);
	                
	                
	                double costRemoved = 0; 
	                double costAdded = 0;
	                
	                if (secondInd == firstIndex + 1)
	                {
	                    costRemoved =  distanceMatrix[A.ID][B.ID] + distanceMatrix[B.ID][C.ID] +  distanceMatrix[C.ID][F.ID];
	                    costAdded = distanceMatrix[A.ID][C.ID] + distanceMatrix[C.ID][B.ID] +  distanceMatrix[B.ID][F.ID] ;
	                }
	                else
	                {
	                    double costRemoved1 =  distanceMatrix[A.ID][B.ID] + distanceMatrix[B.ID][C.ID] ;
	                    double costRemoved2 =  distanceMatrix[D.ID][E.ID] + distanceMatrix[E.ID][F.ID] ;
	                    costRemoved = costRemoved1 + costRemoved2;
	                    
	                    double costAdded1 =  distanceMatrix[A.ID][E.ID] + distanceMatrix[E.ID][C.ID] ;
	                    double costAdded2 =  distanceMatrix[D.ID][B.ID] + distanceMatrix[B.ID][F.ID] ;
	                    costAdded = costAdded1 + costAdded2 ;
	                }
	                
	                double moveCost = costAdded - costRemoved;
	                    
	                if (moveCost < bestMoveCost)
	                {
	                    bestMoveCost = moveCost;
	
	                    sm.positionOfFirst = firstIndex;
	                    sm.positionOfSecond = secondInd;
	                    sm.moveCost = moveCost;
	                }
	            }
	        }
        }
    }
}
