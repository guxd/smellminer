package smellminer.engine.smellanalyze;

import java.util.List;
import org.apache.commons.math3.util.Pair;
import org.apache.commons.math3.stat.inference.MannWhitneyUTest;

public class RankSumTest
{
   public double test(double[] data1,double[] data2)
   {
	 //https://commons.apache.org/proper/commons-math/apidocs/org/apache/commons/math3/stat/inference/MannWhitneyUTest.html
	 
	 MannWhitneyUTest mwutest=new MannWhitneyUTest();
	 //double mean= mwutest.mannWhitneyU(data1,data2);
	 //Computes the Mann-Whitney U statistic comparing mean for two independent samples 
	 //possibly of different length.
	 
	 
	 double p=mwutest.mannWhitneyUTest(data1, data2);
	 //Returns the asymptotic observed significance level, or p-value, associated with 
	 //a Mann-Whitney U statistic comparing mean for two independent samples.
	 return p;
   }
}
