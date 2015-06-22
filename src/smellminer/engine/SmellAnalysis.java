package smellminer.engine;

import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.util.Pair;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.BoxAndWhiskerToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BoxAndWhiskerRenderer;
import org.jfree.data.statistics.BoxAndWhiskerCategoryDataset;
import org.jfree.data.statistics.DefaultBoxAndWhiskerCategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import com.esotericsoftware.kryo.KryoException;
import smellminer.definition.CommitTag;
import smellminer.definition.FileSnapshot;
import smellminer.engine.dataprepare.DataPrepare;
import smellminer.engine.dataprepare.IDataPrepare;
import smellminer.engine.smellanalyze.CliffDeltaTest;
import smellminer.engine.smellanalyze.RankSumTest;
import smellminer.engine.smellanalyze.Regression;
import smellminer.utils.serialization.ISerializationStrategy;
import smellminer.utils.serialization.KryoSerialization;
import smellminer.utils.serialization.ISerializationStrategy.SerializationException;

public class SmellAnalysis
{
   IDataPrepare datahouse;
   BoxPlot boxplot;
   
   /***
    * Record which metric is related to a specific smell.
    * Results from RQ1
    */
   private boolean[][] smellRelatedMetrics=new boolean[5][7];
   
   /***
    * Record the average number of commits required by smell_k to affect code instances.
    */
   private double [] avgCommitsBeforeSmellAffect=new double[5];
   
   public SmellAnalysis()
   {
	
   }
   
   
   public void preareData(String localRepoPath, String projName, String projType, String uri, String startRev,
	    String endRev)
   {
	 
	 String serFileName = localRepoPath + "/" + projName + "-" + startRev + "-" + endRev + ".smd";
	 File serFile = new File(serFileName);
	 if (serFile.exists())
	 {
	    ISerializationStrategy serializer = new KryoSerialization();
	    try
	    {
		  this.datahouse = (DataPrepare) serializer.deserializeFrom(localRepoPath + "/"
			   + projName + "-" + startRev + "-" + endRev + ".smd");
	    }catch(KryoException e)
	    {
		  e.printStackTrace();
		  datahouse = new DataPrepare(localRepoPath,uri, startRev, endRev,projType);
		  datahouse.collectData(uri);
	    }
	    catch (SerializationException e)
	    {
		  e.printStackTrace();
		  datahouse = new DataPrepare(localRepoPath,uri, startRev, endRev,projType);
		  datahouse.collectData(uri);
	    }
	 }
	 else{
	 datahouse = new DataPrepare(localRepoPath,uri, startRev, endRev,projType);
	 datahouse.collectData(uri);
	 }
   }
   
   
   /***
    * Fig2
    * @return
    */
   public DefaultBoxAndWhiskerCategoryDataset numOfCommitsSmellManifestAnalyze()
   {
	 List<List<Integer>> dataforbox=new ArrayList<List<Integer>>();
	 DefaultBoxAndWhiskerCategoryDataset dataset = new DefaultBoxAndWhiskerCategoryDataset();
		
	 
	 Map<String,Map<Integer,FileSnapshot>> FCtable=datahouse.getRawData();
	 Set<String> files=FCtable.keySet();
	 String[] smells=new String[]{"Blob","CDSBP","CC","FD","SC"};
	 for(int ind_smell=0;ind_smell<smells.length;ind_smell++)
	 {
	    String smelltype=smells[ind_smell];
	    List<Integer> boxdataForOneSmell=new ArrayList<Integer>();
	    for(String filepath:files)
	    {
		  int num= datahouse.getNumCommitBeforeSmell(filepath, smelltype);
		  if(num>-1)
		  {
			boxdataForOneSmell.add(num);
		  }
	    }
	    this.avgCommitsBeforeSmellAffect[ind_smell]=(double)boxdataForOneSmell.stream().reduce(0, (a,b)->a+b)/boxdataForOneSmell.size();
	    dataforbox.add(boxdataForOneSmell);
	    dataset.add(boxdataForOneSmell, smelltype, "aa");
	 }
	 
	 boxplot=new BoxPlot("Number of Commits by a smell to manifest itself",dataset);
	 
	 //return dataforbox;
      return dataset;
   }
   

   
   /**
    * Table IV and V
    * @return
    * a 2-D table with rows as smelltype and column as metric.
    * stat per smell per metric
    */
   public List<List<CompStat>> regressionSlopCompare()
   {
	 List<List<CompStat>> stat=new ArrayList<List<CompStat>>();
	 
	 Map<String,Map<Integer,FileSnapshot>> FCtable=datahouse.getRawData();
	 Set<String> files=FCtable.keySet();
	 
	 String[] smells=new String[]{"Blob","CDSBP","CC","FD","SC"};
	 String[] metrics=new String[]{"LOC","LCOM","WMC","RFC","CBO","NOM","NOA"};
	 
	 for(int ind_smell=0;ind_smell<smells.length;ind_smell++)
	 {
	    String smelltype=smells[ind_smell];
	    List<CompStat> stat_per_smell=new ArrayList<CompStat>();
	    for(int ind_metric=0;ind_metric<metrics.length;ind_metric++)
	    {
		  String metric=metrics[ind_metric];
		  DescriptiveStatistics yesSlops = new DescriptiveStatistics();
		  DescriptiveStatistics noSlops = new DescriptiveStatistics();
		  //List<Double> yesData=new ArrayList<Double>();
		  //List<Double> noData=new ArrayList<Double>();
		  for(String filepath:files)
		  {
			int numCommitsBeforeSmell=datahouse.getNumCommitBeforeSmell(filepath, smelltype);
			if(numCommitsBeforeSmell>-1)//file is affected by the smell
			{//then record regression data for yes set
			   List<Pair<Integer,Double>> regr_data_yes=datahouse.getRegressionData(filepath, metric, smelltype,numCommitsBeforeSmell);
			   if(regr_data_yes.size()>1){
			   double slop_yes=new Regression().regression(regr_data_yes);
		         yesSlops.addValue(slop_yes);
		         }
			}
			else{//else record regression data for no set
		         List<Pair<Integer,Double>> regr_data_no=datahouse.getRegressionData(filepath, metric, smelltype,(int)this.avgCommitsBeforeSmellAffect[ind_smell]+1);
		        if(regr_data_no.size()>1){
		           double slop_no=new Regression().regression(regr_data_no);
		           noSlops.addValue(slop_no);}
		         }
		     }
		     CompStat stat_per_smell_per_metric=null;
		     if(yesSlops.getValues().length<1&&noSlops.getValues().length<1)//data is too few
			   stat_per_smell_per_metric=new CompStat(-1,-1,-1,-1,-1,-1);
		     else if(noSlops.getValues().length<1)
			   stat_per_smell_per_metric=new CompStat(yesSlops.getMean(),-1,yesSlops.getPercentile(50),-1,-1,-1);
		     else if(yesSlops.getValues().length<1)
		        stat_per_smell_per_metric=new CompStat(-1,noSlops.getMean(),-1,noSlops.getPercentile(50),-1,-1);
		     else{
		     	double mean_yes=yesSlops.getMean();
		          double mean_no=noSlops.getMean();
		          double med_yes=yesSlops.getPercentile(50);
		          double med_no=noSlops.getPercentile(50);
		          double p=new RankSumTest().test(yesSlops.getValues(), noSlops.getValues());
		          double d=new CliffDeltaTest().test(yesSlops.getValues(), noSlops.getValues());
		          stat_per_smell_per_metric=new CompStat(mean_yes,mean_no,med_yes,med_no,p,d);
		     
//record whether the metric is related to smell-affect. need to check the condition.
		          if(p<0.05&&p>-1&&d>=0.33)this.smellRelatedMetrics[ind_smell][ind_metric]=true;
		     }
		     stat_per_smell.add(stat_per_smell_per_metric);
	    }
	   stat.add(stat_per_smell);
	 }
	 return stat;
   }
   private class CompStat{
	 double mean_yes;
	 double mean_no;
	 double med_yes;
	 double med_no;
	 double p;
	 double d;
	 public CompStat(double mean_yes, double mean_no, double med_yes, double med_no, double p,
		  double d) {
	    super();
	    this.mean_yes = mean_yes;
	    this.mean_no = mean_no;
	    this.med_yes = med_yes;
	    this.med_no = med_no;
	    this.p = p;
	    this.d = d;
	 }
	 public String toString()
	 {
	    return "no_mean:"+mean_no
			+" no_med:"+med_no
			+" yes_mean:"+mean_yes
			+" yes_med:"+med_yes
			+" p:"+p
			+" d:"+d;
	 }
   }
   
   
   /***
    * For table VI-IX
    * @return
    */
   public Map<String, Set<String>> findSmellIntroCommits()
   {
	 Map<String, Set<String>> smellIntroCommits = new HashMap<String, Set<String>>();
	 String[] smells = new String[] { "Blob", "CDSBP", "CC", "FD", "SC" };
	 String[] metrics = new String[] { "LOC", "LCOM", "WMC", "RFC", "CBO", "NOM", "NOA" };
	 Set<String> files = datahouse.getRawData().keySet();
	 for (String filepath : files)
	 {
	    for (int i = 0; i < smells.length; i++)
	    {
		  for (int j = 0; j < metrics.length; j++)
		  {
			List<String> smellIntroCommitsOneFile = datahouse.findSmellIntroCommit(filepath,
				 smells[i], metrics[j], smellRelatedMetrics[i][j]);
			for (String smellIntroCommit : smellIntroCommitsOneFile)
			{
			   Set<String> smellfilelist = smellIntroCommits.get(smellIntroCommit);
			   if (smellfilelist == null) smellfilelist = new HashSet<String>();
			   smellfilelist.add(filepath);
			   smellIntroCommits.put(smellIntroCommit, smellfilelist);
			}
		  }
	    }
	 }
	 return smellIntroCommits;
   }
   
   

   /***
    * Table VI to IX
    */
   public void reasonAnalyze(Map<String,Set<String>> smellIntroCommits)
   {
	 List<CommitTag> committags=datahouse.tagCommits(smellIntroCommits);
	 for(int i=0;i<committags.size();i++)
	    System.out.println(committags.get(i));
   }

   
   
   public static void main(String[] args)
   {
	 String localRepoPath="D:/workspace/smellminer/resources/localrepo";
	 //String projName="Apache"; String svnURI = "http://svn.apache.org/repos/asf/";
	 String projName="Rhino"; String svnURI = "https://github.com/mozilla/rhino";
	 String projType="java";
	 //String projType="csharp";
	 String startRevision = "df654e871e2547e30f10321d86a3956c5d0023e1";
	 String lastRevision = "HEAD";
	 
	   
	 SmellAnalysis smellAnalyzer=new SmellAnalysis();
	 
	 smellAnalyzer.preareData(localRepoPath, projName, projType, svnURI, startRevision, lastRevision);
      
	 try
	 {
	    smellAnalyzer.serializeData(localRepoPath, projName, startRevision, lastRevision);
	 } catch (IOException e)
	 {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	 }
	 
	 
	 /***
	  * Fig 2
	  */
	 DefaultBoxAndWhiskerCategoryDataset dataset=smellAnalyzer.numOfCommitsSmellManifestAnalyze();
	 //DefaultBoxAndWhiskerCategoryDataset dataset=new JFreeChartDemo("a").createSampleDataset();
	 BoxPlot bp=new BoxPlot("number of commits for smell manifest",dataset);
      bp.pack();
      RefineryUtilities.centerFrameOnScreen(bp);
      bp.setVisible(true);
      
     
	 
	 /***
	  * Table IV and V
	  */
      List<List<CompStat>> compresults=smellAnalyzer.regressionSlopCompare();
      String[] smells=new String[]{"Blob","CDSBP","CC","FD","SC"};
	 String[] metrics=new String[]{"LOC","LCOM","WMC","RFC","CBO","NOM","NOA"};
	 System.out.println("Smell | Metric |  No Mean|No Med  |  Yes Mean|Yes Med  |  p  |  d  ");
      for(int i=0;i<compresults.size();i++)
      {
         String smell=smells[i];
         for(int j=0;j<compresults.get(i).size();j++)
         {
     	  String metric=metrics[j];
     	  System.out.println(smell+" | "+metric+" | "+compresults.get(i).get(j));
         }
      }
      
      
      
      /***
       * Find smell-introducing commits
       */
      Map<String,Set<String>> smellIntroCommits= smellAnalyzer.findSmellIntroCommits();
      List<String> commits=smellIntroCommits.keySet().stream().sorted().collect(Collectors.toList());
      for(String commit:commits)
      {
         System.out.println("Smell-Intro Commit:"+commit+"\n Files:");
         Set<String> smellFiles=smellIntroCommits.get(commit);
         for(String file:smellFiles)
         {
 //    	  System.out.println("-----"+file);
         }
      }
      
      
      
      
      /***
       * table VI to IX: find reasons for smell introducing
       */
      smellAnalyzer.reasonAnalyze(smellIntroCommits);
      
      
      
      
      
      

      
   }
   
   
   public void serializeData(String localRepoPath, String projName, String startRev, String endRev) throws IOException
   {
	 String serFileName = localRepoPath + "/" + projName + "-" + startRev + "-" + endRev + ".smd";
	 File serFile = new File(serFileName);
	 if (!serFile.exists())
	 {
	    ISerializationStrategy serializer = new KryoSerialization();
	    try
	    {
		  serializer.serialize(this.datahouse, localRepoPath + "/" + projName + "-" + startRev
			   + "-" + endRev + ".smd");
	    } catch (SerializationException e)
	    {
		  e.printStackTrace();
	    }
	 }
   }

}



class BoxPlot extends ApplicationFrame {
   public BoxPlot(final String title,BoxAndWhiskerCategoryDataset dataset) {
       super(title);
       final CategoryAxis xAxis = new CategoryAxis("Type");
       final NumberAxis yAxis = new NumberAxis("Value");
       yAxis.setAutoRangeIncludesZero(false);
       final BoxAndWhiskerRenderer renderer = new BoxAndWhiskerRenderer();
       renderer.setFillBox(false);
       renderer.setToolTipGenerator(new BoxAndWhiskerToolTipGenerator());
       final CategoryPlot plot = new CategoryPlot(dataset, xAxis, yAxis, renderer);

       final JFreeChart chart = new JFreeChart("Box-and-Whisker Demo",
           new Font("SansSerif", Font.BOLD, 14), plot,true
       );
       final ChartPanel chartPanel = new ChartPanel(chart);
       chartPanel.setPreferredSize(new java.awt.Dimension(450, 270));
       setContentPane(chartPanel);
   }
}
