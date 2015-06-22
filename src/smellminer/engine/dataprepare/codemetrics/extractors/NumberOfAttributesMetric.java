package smellminer.engine.dataprepare.codemetrics.extractors;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import smellminer.definition.Metrics;
import smellminer.engine.dataprepare.codemetrics.IMetricExtract;
import smellminer.engine.dataprepare.codemetrics.astparser.JavaASTExtractor;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class NumberOfAttributesMetric extends ASTVisitor implements IFileMetricRetriever
{
   public Set<FieldDeclaration> getAttributeNodes(File file) throws IOException
   {
	 JavaASTExtractor astExtractor = new JavaASTExtractor(false);
	 NumberOfAttributesMetric m = new NumberOfAttributesMetric();
	 CompilationUnit cu = astExtractor.getAST(file);
	 cu.accept(m);
	 return m.attributes;
   }

   private final Set<FieldDeclaration> attributes = Sets.newHashSet();

   public boolean visit(FieldDeclaration node)
   {
	 attributes.add(node);
	 return super.visit(node);
   }

   @Override
   public double getMetricForASTNode(ASTNode paramASTNode)
   {
	 // TODO Auto-generated method stub
	 return 0;
   }

   @Override
   public double getMetricForFile(File paramFile) throws IOException
   {
	 // TODO Auto-generated method stub
	 return getAttributeNodes(paramFile).size();
   }
 

}
