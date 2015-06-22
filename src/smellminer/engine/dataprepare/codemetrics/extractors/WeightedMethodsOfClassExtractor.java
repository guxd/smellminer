package smellminer.engine.dataprepare.codemetrics.extractors;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.ConditionalExpression;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SwitchCase;
import org.eclipse.jdt.core.dom.WhileStatement;
import smellminer.engine.dataprepare.codemetrics.astparser.JavaASTExtractor;

/***
 * Weighted methods per class
 * A class's weighted methods per class WMC metric is simply the sum of the 
 * complexities of its methods. 
 * As a measure of complexity we can use the cyclomatic complexity,
 * or we can abritrarily assign a complexity value of 1 to each method. 
 * The ckjm program assigns a complexity value of 1 to each method, 
 * and therefore the value of the WMC is equal to the number of methods in the class.
 * @author Xiaodong
 *
 */
public class WeightedMethodsOfClassExtractor implements IFileMetricRetriever
{
   private static final Logger LOGGER = Logger.getLogger(WeightedMethodsOfClassExtractor.class.getName());

   public int getComplexity(File file) throws IOException
   {
	 JavaASTExtractor ast = new JavaASTExtractor(false);
	 JunctionVisitor visitor = new JunctionVisitor();
	 ast.getAST(file).accept(visitor);
	 return visitor.complexity;
   }

   public double getMetricForASTNode(ASTNode node)
   {
	 JunctionVisitor visitor = new JunctionVisitor();
	 node.accept(visitor);
	 return visitor.complexity;
   }

   public double getMetricForFile(File file) throws IOException
   {
	 return getWeightedMethods(file);
   }
   
   /***
    * WMC
    * @param file
    * @return
    */
   public double getWeightedMethods(File file)
   {
	 double wmc=0.0;
	 Map<String, MethodDeclaration> methods=null;
	 try
	 {
	    methods = MethodRetriever.getMethodNodes(file);
	 } catch (IOException e)
	 {
	    e.printStackTrace();
	 }
	 Iterator<MethodDeclaration> localIterator2 = methods.values().iterator();
	 
      while (localIterator2.hasNext()) {
         MethodDeclaration entry = localIterator2.next();
         wmc+=getMetricForASTNode((ASTNode)entry);
      }
      return wmc;
   }

   private static class JunctionVisitor extends ASTVisitor
   {
	 int complexity = 0;

	 public boolean visit(CatchClause arg0)
	 {
	    this.complexity += 1;
	    return super.visit(arg0);
	 }

	 public boolean visit(ConditionalExpression arg0)
	 {
	    this.complexity += 1;
	    return super.visit(arg0);
	 }

	 public boolean visit(DoStatement arg0)
	 {
	    this.complexity += 1;
	    return super.visit(arg0);
	 }

	 public boolean visit(EnhancedForStatement arg0)
	 {
	    this.complexity += 1;
	    return super.visit(arg0);
	 }

	 public boolean visit(ForStatement arg0)
	 {
	    this.complexity += 1;
	    return super.visit(arg0);
	 }

	 public boolean visit(IfStatement arg0)
	 {
	    this.complexity += 1;
	    return super.visit(arg0);
	 }

	 public boolean visit(MethodDeclaration arg0)
	 {
	    this.complexity += 1;
	    return super.visit(arg0);
	 }

	 public boolean visit(SwitchCase arg0)
	 {
	    this.complexity += 1;
	    return super.visit(arg0);
	 }

	 public boolean visit(WhileStatement arg0)
	 {
	    this.complexity += 1;
	    return super.visit(arg0);
	 }
   }
}
/*
 * Location: D:\research\topics\Software Engineering\Bug Gene\Tools\tsg.jar
 * Qualified Name: codemining.java.codedata.metrics.CyclomaticCalculator JD-Core
 * Version: 0.6.0
 */