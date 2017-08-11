/*
 * Name: Bradley Dodd
 * Student ID: 201030851 
 * Description: Program to count the freqency of words in a series of text files using multithreading
 */
import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

class WordCount {	
    public static void main(String[] args) {
	    final int MAX_THREADS = 20;
		final int MAX_TERMINATION_WAIT = 30;
		WordCountTable totalsTable = new WordCountTable();
		ExecutorService threadPool;
		threadPool = Executors.newFixedThreadPool(MAX_THREADS);
		File directory = new File("files/");
		File[] listOfFiles = directory.listFiles();
		//Loop through each file counting the words and storing them in the Hashtable
		long startTime = System.currentTimeMillis();
		for (File file : listOfFiles){
			WordCountThread worker = new WordCountThread(file,totalsTable);   
			threadPool.execute(worker);
		}
		//Shutdown the thread pool so no new threads can be created
		threadPool.shutdown(); 
		try {
			 // Wait a while for existing tasks to terminate
			 if (!threadPool.awaitTermination(MAX_TERMINATION_WAIT, TimeUnit.SECONDS)) {
				threadPool.shutdownNow(); // Cancel currently executing tasks
				// Wait a while for tasks to respond to being cancelled
				if (!threadPool.awaitTermination(MAX_TERMINATION_WAIT, TimeUnit.SECONDS))
				System.err.println("Pool did not terminate");
			}
		}catch (InterruptedException ie) {
			// (Re-)Cancel if current thread also interrupted
			threadPool.shutdownNow();
			// Preserve interrupt status
			Thread.currentThread().interrupt();
		}
		long endTime = System.currentTimeMillis();
		System.out.println("That took " + (endTime - startTime) + " milliseconds");
		//Print out the frequency of words
        for (Map.Entry<String, Integer> element : totalsTable.getTotals().entrySet()) {
			System.out.println(element.getKey() + " " + element.getValue());
		}
		
    }
}
//Object to store the Hashtable of words and frequencies
class WordCountTable{
	private Hashtable<String, Integer> grandTotals = new Hashtable<String, Integer>();
	synchronized void updateWordFreqency (String targetWord){
		Integer frequency = grandTotals.get(targetWord);
		grandTotals.put(targetWord, (frequency == null) ? 1 : frequency + 1);
	}
	synchronized Hashtable<String, Integer> getTotals (){
		return grandTotals;
	}
}
//Worker thread that counts the words 
class WordCountThread implements Runnable
{
	private WordCountTable totals;
	private File file;
	public WordCountThread(File fn, WordCountTable totals){
		file = fn;
		this.totals = totals;
	}
	public void run()   
	{ 
		try{ 
			Scanner wordScanner = new Scanner(file);
			while (wordScanner.hasNext()) {
				String word = wordScanner.next().toLowerCase();
				totals.updateWordFreqency(word);
			}
		}catch(FileNotFoundException fnf){
			System.err.println(fnf.getMessage());
		}
	}
	
}
