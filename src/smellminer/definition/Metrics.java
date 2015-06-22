package smellminer.definition;

import java.io.Serializable;

public class Metrics implements Serializable
{
    public int loc;//the number of lines of code excluding white spaces and comments
    public double wmc;//the complexity of a class as the sum of the McCabes cyclomatic complexity of its methods
    public int rfc;//the number of distinct methods and constructors invoked by a class
    public int cbo;//the number of classes to which a class is coupled
    public double lcom;//the higher the pairs of methods in a class sharing at least a field, the higher its cohesion
    public int noa;//number of attributes
    public int nom;//number of methods
    
    
   public Metrics(int loc, double wmc, int rfc, int cbo, double lcom, int noa, int nom) {
	 super();
	 this.loc = loc;
	 this.wmc = wmc;
	 this.rfc = rfc;
	 this.cbo = cbo;
	 this.lcom = lcom;
	 this.noa = noa;
	 this.nom = nom;
   }
   
   public double getMetricValue(String metricname)
   {
	 if(metricname.equalsIgnoreCase("loc"))return (double)loc;
	 else if(metricname.equalsIgnoreCase("wmc"))return wmc;
	 else if(metricname.equalsIgnoreCase("rfc"))return (double)rfc;
	 else if(metricname.equalsIgnoreCase("cbo"))return (double)cbo;
	 else if(metricname.equalsIgnoreCase("lcom"))return lcom;
	 else if(metricname.equalsIgnoreCase("noa"))return (double)noa;
	 else if(metricname.equalsIgnoreCase("nom"))return (double)nom;
	 else return -1;
   }
    
   public String toString()
   {
	 return "LOC:"+loc+" WMC:"+wmc+" RFC:"+rfc+" CBO:"+cbo+" LCOM:"+lcom+" NOA:"+noa+" NOM:"+nom;
   }
}
