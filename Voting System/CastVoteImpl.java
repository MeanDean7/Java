/*
 * Name: Bradley Dodd
 * Student ID: 201030851
 * Description: Implementation of voting methods
 * 
 */
import java.util.Random;
import java.rmi.*;
import java.rmi.server.UnicastRemoteObject; 
public class CastVoteImpl extends UnicastRemoteObject implements CastVote
{
	String[][] votingResults = new String[][]{
	{"Candidate","Votes"},
	{"Obama","0"},
	{"Trump","0"},
	{"Clinton","0"}
	};
    public CastVoteImpl() throws RemoteException{};
    public VotingSystem getVotingSystem() throws RemoteException{
		return new VotingSystem();
    };
	public int register() throws java.rmi.RemoteException{
		Random rand = new Random();
		int userID = rand.nextInt(50) + 1;
		return userID;
	}
	public void vote (int choice) throws java.rmi.RemoteException{
		String voteCountString = votingResults[choice][1];
		int voteCount = Integer.parseInt(voteCountString);
		voteCount++;
		votingResults[choice][1] = Integer.toString(voteCount);
	};
	public String getCandidates() throws java.rmi.RemoteException{
		String output = "\n1: Obama\n2: Trump\n3: Clinton\n";
		return output; 
	};
	public String getVotingResults() throws java.rmi.RemoteException{
		String output = "\nVoting Results:\n" + votingResults[0][0] + " " + votingResults[0][1] + "\n";
		for (int i = 1; i <=3; i++){
			output = output + votingResults[i][0]; 
			if(votingResults[i][0] == "Clinton"){
				output = output + ":  ";
			}else{
				output = output + ":    ";
			}
			output = output + votingResults[i][1] + "\n"; 
		}
		return output; 
	};
}
