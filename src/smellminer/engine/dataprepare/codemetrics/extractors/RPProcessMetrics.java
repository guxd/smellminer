package smellminer.engine.dataprepare.codemetrics.extractors;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import smellminer.engine.dataprepare.codemetrics.FileUtil;

/**
 * @author jcnam
 * 
 * This class extract more process metrics form the result text from HistoryMetricsCollector in HunkAnalyzer.
 */
public class RPProcessMetrics {
	
	String midDate = "2011-06-25";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String[] newArgs = {"sopra01_2011","data/hm_sopra01_2011.csv","2011-06-25"};
		args = newArgs;
		
		System.out.println("group,numCommitter,numCommits,avgCommits,rateCreatorChanges,rateOwnerChanges," + 
							"avgMyOwnership,avgHighestOwnership,maxCommitNumOfAStd,minCommitNumOfAStd,sumChangedLines,avgChangeLinesPerAFileInACommit,"+
							"maxChangedLOCOfAStd,minChangedLOCOfAStd,chageRateBtwFirstSecondHalf," +
							"changeRateBtwMultimediaAndSourceFiles,commitRateBtwFirstSecond," +
							"averageNumCommit,normalizedAverageNumCommit,stdNumCommit");
		
		int[] numMembers2011 = {5,5,6,5,5,5,5,5,5,5};
		int[] numMembers2012 = {5,4,5,4,5,2,5,5,5,4};
		
		for(int i=1;i<=10;i++){
			args[0] = "sopra" + String.format("%02d", i) + "_2011";
			args[1] = "data/hm_sopra" + String.format("%02d", i) + "_2011.csv";
			new RPProcessMetrics().run(args,numMembers2011[i-1]);
		}
		
		for(int i=1;i<=10;i++){
			args[0] = "sopra" + String.format("%02d", i) + "_2012";
			args[1] = "data/hm_sopra" + String.format("%02d", i) + "_2012.csv";
			args[2] = "2012-06-12";
			new RPProcessMetrics().run(args,numMembers2012[i-1]);
		}
	}

	void run(String[] args, int numMembers){
		String groupName = args[0];
		String dataFile = args[1];
		midDate = args[2];
		
		ArrayList<String> lines = FileUtil.getLines(dataFile);
		ArrayList<RawMetrics> rawMetrics = getRawMetrics(lines);
		String result;
		try {
			result = ComputeMetricsWithFileExtension(groupName,"cs",rawMetrics,true,numMembers);
			System.out.println(result);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	String ComputeMetricsWithFileExtension(String groupName, 
			String ext,ArrayList<RawMetrics> records,
			boolean considerOnlyTrunk,int numMembers) throws ParseException{
		
		int numCommits = 0;
		int allRecords = 0;
		int numCreatorChanges = 0;
		int numOwnerChanges = 0;
		double sumOfMyOwnership = 0.0;
		double sumOfHighestOwnership = 0.0;
		int sumOfChangedLines = 0;
		
		TimeZone.setDefault(TimeZone.getTimeZone("Europe/Berlin"));
		
		int changeCountInFirstHalf = 0;
		int changeCountInSecondHalf = 0;
		
		int commitCountInFirstHalf = 0;
		int commitCountInSecondHalf = 0;
		
		int numChangeOfMultimediaFiles = 0;
		int numChangeOfSourceCodeFiles = 0;
		
		HashMap<String,Integer> commitCounterByCommitter = new HashMap<String,Integer>();
		HashMap<String,Integer> changedLinesByCommitter = new HashMap<String,Integer>();
		
		int currrentRevision = -1;
		for(RawMetrics record:records){		
		
			
			if(considerOnlyTrunk && !record.path.startsWith("trunk/"))
				continue;
			
			if(record.ext.toLowerCase().equals("cs")){
			/*if(record.ext.toLowerCase().equals("cs")
					|| record.ext.toLowerCase().equals("xml")
					|| record.ext.toLowerCase().equals("jpg")
					|| record.ext.toLowerCase().equals("png")
					|| record.ext.toLowerCase().equals("bmp")
					|| record.ext.toLowerCase().equals("wav")
					|| record.ext.toLowerCase().equals("fbx")
					){*/
				
				
				
				Date commitDate = new SimpleDateFormat("yyyy-MM-dd").parse(record.date);
				Date halfDate = new SimpleDateFormat("yyyy-MM-dd").parse(midDate);
				
				
				// new revision
				if(currrentRevision!=record.revision){
					currrentRevision = record.revision;
					numCommits++;
					
					if(commitDate.compareTo(halfDate)<0){
						// first half
						commitCountInFirstHalf++;
						
					}
					else{
						//second half
						commitCountInSecondHalf++;
					}
					
					if(commitCounterByCommitter.get(record.committer)==null){
						commitCounterByCommitter.put(record.committer, 1);
						// init here anyway~
						changedLinesByCommitter.put(record.committer, 0);
					}
					else
						commitCounterByCommitter.put(record.committer, commitCounterByCommitter.get(record.committer)+1);
				}

				allRecords++;
				numCreatorChanges= numCreatorChanges + record.isCreator;
				numOwnerChanges = numOwnerChanges + record.isOwner;
				sumOfMyOwnership = sumOfMyOwnership + record.ownership;
				sumOfHighestOwnership = sumOfHighestOwnership + record.ownershipHighest;
				sumOfChangedLines = sumOfChangedLines + Math.abs(record.changedLOC);
				
				changedLinesByCommitter.put(record.committer, changedLinesByCommitter.get(record.committer)+Math.abs(record.changedLOC));
				
				

				if(commitDate.compareTo(halfDate)<0){
					// first half
					changeCountInFirstHalf++;
					
				}
				else{
					//second half
					changeCountInSecondHalf++;
				}
					
				
				
				if(record.ext.toLowerCase().equals("jpg")
						|| record.ext.toLowerCase().equals("png")
						|| record.ext.toLowerCase().equals("bmp")
						|| record.ext.toLowerCase().equals("wav")
						|| record.ext.toLowerCase().equals("fbx")
						)
					numChangeOfMultimediaFiles++;
				else
					numChangeOfSourceCodeFiles++;	
					
			}
		}
		
		commitCounterByCommitter = cutRecordByCount(commitCounterByCommitter,numMembers);
		changedLinesByCommitter = cutRecordByCount(changedLinesByCommitter,numMembers);
		
		return groupName + "," + commitCounterByCommitter.size() +"," + 
				numCommits + "," +
				(double) numCommits/commitCounterByCommitter.size() + "," + 
				(double) numCreatorChanges/allRecords + "," +
				(double) numOwnerChanges/allRecords +"," +
				sumOfMyOwnership/allRecords + "," +
				sumOfHighestOwnership/allRecords + "," +
				getMaxValueFromHashMap(commitCounterByCommitter) + "," +
				getMinValueFromHashMap(commitCounterByCommitter) + "," +
				sumOfChangedLines + "," + 
				(double) sumOfChangedLines/allRecords + "," +
				getMaxValueFromHashMap(changedLinesByCommitter) + "," +
				getMinValueFromHashMap(changedLinesByCommitter) + "," +
				(double)changeCountInFirstHalf/changeCountInSecondHalf + "," +
				(double)numChangeOfMultimediaFiles/numChangeOfSourceCodeFiles + "," +
				(double)commitCountInFirstHalf/commitCountInSecondHalf + "," +
				getAverageFromHashMap(commitCounterByCommitter) + "," +
				getAverageFromHashMap(commitCounterByCommitter)/numCommits + "," +
				getSTD(commitCounterByCommitter);
	}
	
	HashMap<String,Integer> cutRecordByCount(HashMap<String,Integer> data,int cutCounter){
		HashMap<String,Integer> newData = new HashMap<String,Integer>(data);
		while(newData.size() > cutCounter){
			int min =-1;
			String minKey = "";
			for(String key:newData.keySet()){
				if(min==-1){
					min = newData.get(key);
					minKey = key;
				}
				
				if(min > newData.get(key)){
					min = newData.get(key);
					minKey = key;
				}
			}
			newData.remove(minKey);
		}
		
		return newData;
	}
	
	double getSTD(HashMap<String,Integer> data){
		
		StandardDeviation std = new StandardDeviation();
		
		return std.evaluate(getDoublesFromHashMap(data));
	}
	
	double[] getDoublesFromHashMap(HashMap<String,Integer> data){
		
		double[] values = new double[data.size()];
		
		int i=0;
		for(String key:data.keySet()){
			values[i] = (double) data.get(key);
			i++;
		}
		
		return values;
	}
	
	double getAverageFromHashMap(HashMap<String,Integer> hashmapData){
		
		int sum = 0;
		
		for(String key:hashmapData.keySet()){
			Integer value = hashmapData.get(key);
			sum = sum + value;
		}
		
		return (double) sum/hashmapData.size();
		
	}
	
	int getMaxValueFromHashMap(HashMap<String,Integer> commitCounterByCommitter){
		int max = 0;
		for(String committer:commitCounterByCommitter.keySet()){
			if(max < commitCounterByCommitter.get(committer))
				max = commitCounterByCommitter.get(committer);
		}
		
		return max;
	}
	
	int getMinValueFromHashMap(HashMap<String,Integer> commitCounterByCommitter){
		int min = -1;
		for(String committer:commitCounterByCommitter.keySet()){
			
			if(min==-1)
				min = commitCounterByCommitter.get(committer);
			
			if(min > commitCounterByCommitter.get(committer))
				min = commitCounterByCommitter.get(committer);
		}
		
		return min;
	}
	
	ArrayList<RawMetrics> getRawMetrics(ArrayList<String> lines){
		
		ArrayList<RawMetrics> rawMetrics = new ArrayList<RawMetrics>();
		
		for(String line:lines)
			rawMetrics.add(new RawMetrics(line));
		
		return rawMetrics;
	}
	
}

class RawMetrics{
	String path="";
	String ext;
	String fullDate;
	String date;
	int revision;
	String committer;
	int changedLOC;
	int isCreator;
	double ownership;
	double ownershipHighest;
	int isOwner;
	
	RawMetrics(String line){
		String[] record = line.split(",");
		
		// in case file path includes commas, need to compute the path and others correctly
		int interval = record.length -15;
		for(int i=0;i<interval+1;i++){
			path = path + record[i] +",";
		}
		ext = record[1+interval];
		fullDate = record[2+interval];
		date = record[3+interval];
		revision = Integer.parseInt(record[4+interval]);
		committer = record[5+interval];
		changedLOC = Integer.parseInt(record[7+interval]);
		isCreator = Integer.parseInt(record[11+interval]);
		ownership = Double.parseDouble(record[12+interval]);
		ownershipHighest = Double.parseDouble(record[13+interval]);
		isOwner = Integer.parseInt(record[14+interval]);
	}
}
