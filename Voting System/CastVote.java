/*
 * Name: Bradley Dodd
 * Student ID: 201030851
 * Description: 
 * 
 */
import java.rmi.*;
public interface CastVote extends java.rmi.Remote {
	public int register() throws java.rmi.RemoteException;
	public void vote (int choice) throws java.rmi.RemoteException;
	public String getCandidates() throws java.rmi.RemoteException;
	public String getVotingResults() throws java.rmi.RemoteException;
}