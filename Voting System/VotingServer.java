/*
 * Name: Bradley Dodd
 * Student ID: 201030851
 * Description: Server for voting system
 * 
 */
import java.rmi.Naming;
public class VotingServer {
	public static void main(String[] args) {
		try {
		CastVoteImpl remote = new CastVoteImpl();
		Naming.rebind ("VotingSystem", remote);
	}
	catch(Exception e) {
		System.out.println("Error occurred at server  "+e.getMessage());
		}
	}
}

