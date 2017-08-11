/*
 * Name: Bradley Dodd
 * Student ID: 201030851
 * Description: This program allows users to vote for one of a set of predefined candidates
 * 
 */
import java.rmi.*;
import java.io.*;
public class VoteClient {
  public static void main(String[] args) {
    try {
		CastVote serverImpl = (CastVote) Naming.lookup("rmi://localhost/VotingSystem"); 
		System.out.println("The candidates are:"+serverImpl.getCandidates());
		System.out.println("Please enter a number for your chosen candidate");  
		try
		{
			BufferedReader inputStream = new BufferedReader(new InputStreamReader(System.in));
			String inputString = inputStream.readLine();
	        int inputInteger = Integer.parseInt(inputString);
	  		switch(inputInteger)
			{
				case 1: 
					serverImpl.vote(1);
					break;
				case 2: 
					serverImpl.vote(2);
					break;
				case 3: 	
					serverImpl.vote(3);
					break;		
				default:
					System.out.println("Please enter a valid input");
			}
		}
		catch(Exception ex)
		{ 
			System.out.println("Please enter a valid input");
		}		
		System.out.println(serverImpl.getVotingResults());		
    }
    catch(Exception e) {
		System.out.println("Problem encountered accessing remote object "+e);
		}	
	}
}
