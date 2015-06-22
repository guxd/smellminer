package smellminer.engine.dataprepare.codemetrics;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import smellminer.definition.Metrics;
import smellminer.engine.dataprepare.codemetrics.extractors.CouplingBetweenObjectMetric;
import smellminer.engine.dataprepare.codemetrics.extractors.CyclomaticCalculator;
import smellminer.engine.dataprepare.codemetrics.extractors.IFileMetricRetriever;
import smellminer.engine.dataprepare.codemetrics.extractors.LackOfCohesionMetric;
import smellminer.engine.dataprepare.codemetrics.extractors.LinesOfCodeMetric;
import smellminer.engine.dataprepare.codemetrics.extractors.MethodRetriever;
import smellminer.engine.dataprepare.codemetrics.extractors.NumberOfAttributesMetric;
import smellminer.engine.dataprepare.codemetrics.extractors.ResponseForClassRetriever;
import smellminer.engine.dataprepare.codemetrics.extractors.WeightedMethodsOfClassExtractor;

public class MetricExtractor implements IMetricExtract,Serializable
{
   public IFileMetricRetriever locRetriver = new LinesOfCodeMetric();
   public IFileMetricRetriever wmcRetriver = new WeightedMethodsOfClassExtractor();
   public IFileMetricRetriever rfcRetriver = new ResponseForClassRetriever();
   public IFileMetricRetriever cboRetriver = new CouplingBetweenObjectMetric();
   public IFileMetricRetriever lcomRetriver = new LackOfCohesionMetric();
   public IFileMetricRetriever noaRetriver = new NumberOfAttributesMetric();
   public IFileMetricRetriever nomRetriver = new MethodRetriever();

   @Override
   public Metrics extractMetrics(File file)
   {
	 int loc=0;
	 double wmc=0.0;
	 int rfc=0;
	 int cbo = 0;
	 double lcom=1.0;
	 int noa=0;
	 int nom=0;
	 try
	 {
	    loc = (int) locRetriver.getMetricForFile(file);
	    wmc = wmcRetriver.getMetricForFile(file);
	    rfc = (int) rfcRetriver.getMetricForFile(file);
	    cbo = (int) cboRetriver.getMetricForFile(file);
	    lcom = lcomRetriver.getMetricForFile(file);
	    noa = (int) noaRetriver.getMetricForFile(file);
	    nom = (int) nomRetriver.getMetricForFile(file);
	 } catch (IOException e)
	 {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	 }
	 Metrics metric = new Metrics(loc, wmc, rfc, cbo, lcom, noa, nom);
	 // TODO Auto-generated method stub
	 return metric;
   }

   public static void main(String[] args)
   {
	 // TODO Auto-generated method stub
   }
}
