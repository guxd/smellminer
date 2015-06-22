 package smellminer.engine.dataprepare.codemetrics.extractors;
 
 import java.io.File;
 import java.io.IOException;
 import org.apache.commons.io.FileUtils;
 import org.eclipse.jdt.core.dom.ASTNode;
 
 public class LinesOfCodeMetric implements IFileMetricRetriever
 {
   public double getMetricForASTNode(ASTNode node)
   {
     return node.toString().split("\n").length;
   }
   
   public double getMetricForFile(File file) throws IOException
   {
     String fileContents = FileUtils.readFileToString(file);
     return fileContents.split("\n").length;
   }
 }


/* Location:           C:\Users\Xiaodong\tsg\
 * Qualified Name:     codemining.java.codedata.metrics.LinesOfCodeMetric
 * JD-Core Version:    0.7.1
 */