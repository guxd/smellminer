package smellminer.engine.dataprepare.codemetrics.extractors;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import smellminer.engine.dataprepare.codemetrics.astparser.JavaASTExtractor;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
/***
 * !!!!!!!! Have problem in calculating fields in a method, 
 * we use FieldAccess that can only count fields with "this.XXX" format.
 * 
 * 
 * Lack of Cohesion Of Methods (LCOM)
 *
 *  The single responsibility principle states that a class should not have more than one
 *  reason to change. Such a class is said to be cohesive. 
 *  A high LCOM value generally pinpoints a poorly cohesive class. 
 *  There are several LCOM metrics. The LCOM takes its values in the range [0-1]. 
 *  The LCOM HS (HS stands for Henderson-Sellers) takes its values in the range [0-2]. 
 *  A LCOM HS value highest than 1 should be considered alarming. 
 *  Here are algorithms used by JArchitect to compute LCOM metrics:
 *        LCOM = 1 ¨C (sum(MF)/M*F)
 *   	LCOM HS = (M ¨C sum(MF)/F)(M-1)
 *		Where:
 *			M is the number of methods in class (both static and instance methods are
 *					 counted, it includes also constructors, properties getters/setters,
 *					 events add/remove methods).
 *			F is the number of instance fields in the class.
 *			MF is the number of methods of the class accessing a particular instance field.
 *			Sum(MF) is the sum of MF over all instance fields of the class.
 *  The underlying idea behind these formulas can be stated as follow: 
 *  		a class is utterly cohesive if all its methods use all its instance fields,
 *  		which means that sum(MF)=M*F and then LCOM = 0 and LCOMHS = 0. 
 * @author Xiaodong
 *Atlassian
 */
public class LackOfCohesionMetric extends ASTVisitor implements IFileMetricRetriever
{
   private final Set<String> attributes = Sets.newHashSet();
   private final Map<MethodDeclaration,Set<String>> methods=Maps.newHashMap();
   //private final Map<String,List<MethodDeclaration>> methodsInvokFields=Maps.newHashMap();
   
   
   public Set<String> getAttributeNames(File file) throws IOException
   {
	 JavaASTExtractor astExtractor = new JavaASTExtractor(false);
	 LackOfCohesionMetric m = new LackOfCohesionMetric();//visitor
	 CompilationUnit cu = astExtractor.getAST(file);
	 cu.accept(m);//data, accept visiting of visitor
	 this.attributes.isEmpty();
	 return m.attributes;
   }
   
   public Map<MethodDeclaration,Set<String>> getMethodDeclarationNodes(File file) throws IOException
   {
	 JavaASTExtractor astExtractor = new JavaASTExtractor(false);
	 LackOfCohesionMetric m = new LackOfCohesionMetric();
	 CompilationUnit cu = astExtractor.getAST(file);
	 cu.accept(m);
	 return m.methods;
   }



   public boolean visit(FieldDeclaration node)
   {
	 for(int i=0;i<node.fragments().size();i++)
	 this.attributes.add(((VariableDeclarationFragment)node.fragments().get(i)).getName().getIdentifier());
	 return super.visit(node);
   }
   
   @Override
   public boolean visit(MethodDeclaration node)
   {
	 FieldsInMethodVisitor fieldVisitor=new FieldsInMethodVisitor(attributes);
	 node.accept(fieldVisitor);
	 this.methods.put(node,fieldVisitor.fieldsInMethod);
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
	 Set<String> fields=getAttributeNames(paramFile);
	// LCOM is the size of a set where are couples of method...
	// Why not just increment a local variable?
	double numberOfCouples = 0;
	// So we consider methods at the class level .
	final Map<MethodDeclaration,Set<String>> methodsOfClass =getMethodDeclarationNodes(paramFile);
	// For every method...
	for (final Iterator<MethodDeclaration> iterMethod1 = methodsOfClass.keySet().iterator(); iterMethod1
		.hasNext();) {
	   MethodDeclaration method1 = iterMethod1.next();
		// For a given method, we analyze other methods.
		// We can then make couples.
	    for (final Iterator<MethodDeclaration> iterMethod2 = methodsOfClass.keySet().iterator(); iterMethod2
	     	.hasNext();) {
			final MethodDeclaration method2 =  iterMethod2.next();
			// If the methods are different, we pass... (m1,m1)
			if (!method1.equals(method2)) {
				// If the intersection between AR(m1), AR(m2) and
				// allAttributes(class) is empty, the condition is satisfied.
			     boolean disjoint=true;
			     for(String fieldInMethod1:methodsOfClass.get(method1))
			     {
			        for(String fieldInMethod2:methodsOfClass.get(method2))
			        {
			     	 if(fieldInMethod1.equalsIgnoreCase(fieldInMethod2))
			     	    disjoint=false;
			        }
			     }
				if (disjoint) {
					// Incrementation of an index instead of
					// storing the couple in a set.
					numberOfCouples++;
				}
			}
	    }
	}
		return (double)numberOfCouples/2;
   }   
   

   
   public static void main(String[] args)
   {
	 File testFile=new File("D:/workspace/smellminer/src/smellminer/engine/dataprepare/codemetrics/extractors/LackOfCohesionMetric.java");
      try
	 {
	    double lcom=new LackOfCohesionMetric().getMetricForFile(testFile);
	    System.out.print(lcom);
	 } catch (IOException e)
	 {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	 }
   }
   

}

class FieldsInMethodVisitor extends ASTVisitor
{
   Set<String> attributes=null;
   public Set<String> fieldsInMethod=new HashSet<String>();
   public FieldsInMethodVisitor(Set<String> attributes)
   {
	 this.attributes=attributes;
   }
	 
	 @Override
	 public boolean visit(FieldAccess node)
	   {
	    if(this.attributes.contains(node.getName().getIdentifier()))
		 fieldsInMethod.add(node.getName().getIdentifier());
		 return super.visit(node);
	   }
}
