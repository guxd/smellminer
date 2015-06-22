package smellminer.engine.smellanalyze;

import java.util.List;
import org.apache.commons.math3.util.Pair;
import org.apache.commons.math3.exception.NoDataException;
import org.apache.commons.math3.stat.regression.SimpleRegression;

public class Regression
{
   public double regression(List<Pair<Integer,Double>> datapoints)
   {
	 SimpleRegression regr=new SimpleRegression();
	 for(Pair<Integer,Double> data:datapoints)
	    regr.addData(data.getKey(), data.getValue());
	 try{
	 regr.regress();
	 }catch(NoDataException e)
	 {
	    System.err.println(e.getMessage());
	    System.err.print("Only "+datapoints.size()+" data points:");
	    for(Pair<Integer,Double> data:datapoints)
		  System.err.print("<"+data.getKey()+","+data.getValue()+"> ");
	    System.err.println();
	    if(datapoints.size()==2)
	    {
		  double slop=(datapoints.get(1).getValue()-datapoints.get(0).getValue())
			   /(datapoints.get(1).getKey()-datapoints.get(0).getKey());
		  return slop;
	    }
	 }
	 double slop=regr.getSlope();
	 
	 
	 return slop;
   }   
}
