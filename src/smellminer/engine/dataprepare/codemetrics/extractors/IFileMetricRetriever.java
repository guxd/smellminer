package smellminer.engine.dataprepare.codemetrics.extractors;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import org.eclipse.jdt.core.dom.ASTNode;

public abstract interface IFileMetricRetriever extends Serializable
{
  public abstract double getMetricForASTNode(ASTNode paramASTNode);
  
  public abstract double getMetricForFile(File paramFile) throws IOException;
}


/* Location:           C:\Users\Xiaodong\tsg\
 * Qualified Name:     codemining.java.codedata.metrics.IFileMetricRetriever
 * JD-Core Version:    0.7.1
 */