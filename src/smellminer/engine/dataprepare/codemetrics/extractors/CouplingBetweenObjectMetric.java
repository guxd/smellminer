package smellminer.engine.dataprepare.codemetrics.extractors;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import smellminer.engine.dataprepare.codemetrics.astparser.JavaASTExtractor;
import com.google.common.collect.Sets;
/***
 * CBO - Coupling between object classes.
 * The coupling between object classes (CBO) metric represents the number of classes 
 * coupled to a given class (efferent couplings and afferent couplings). 
 * This coupling can occur through method calls, field accesses, inheritance, arguments,
 *  return types, and exceptions.
 * @author Xiaodong
 *
 */
public class CouplingBetweenObjectMetric extends ASTVisitor implements IFileMetricRetriever
{

   
   public Set<ClassInstanceCreation> getClassInstanceCreationNodes(File file) throws IOException
   {
	 JavaASTExtractor astExtractor = new JavaASTExtractor(false);
	 CouplingBetweenObjectMetric m = new CouplingBetweenObjectMetric();
	 CompilationUnit cu = astExtractor.getAST(file);
	 cu.accept(m);
	 return m.clazzes;
   }
   
   public Set<MethodDeclaration> getMethodDeclarationNodes(File file) throws IOException
   {
	 JavaASTExtractor astExtractor = new JavaASTExtractor(false);
	 CouplingBetweenObjectMetric m = new CouplingBetweenObjectMetric();
	 CompilationUnit cu = astExtractor.getAST(file);
	 cu.accept(m);
	 return m.methods;
   }

   private final Set<ClassInstanceCreation> clazzes = Sets.newHashSet();
   private final Set<MethodDeclaration> methods=Sets.newHashSet();

   public boolean visit(ClassInstanceCreation node)
   {
	 clazzes.add(node);
	 return super.visit(node);
   }
   
   public boolean visit(MethodDeclaration node)
   {
	 methods.add(node);
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
	 Set<String> invokeClazzes=Sets.newHashSet();
	 
	 /***
	  * get invoked clazzes from instance creation
	  */
	 Set<ClassInstanceCreation> creations= this.getClassInstanceCreationNodes(paramFile);
	 for(ClassInstanceCreation creat:creations)
	 {
	    if(creat.getType().isSimpleType())
		  invokeClazzes.add(creat.getType().toString());
	 }
	 
	 /***
	  * get invoked clazzes from method parameters and return types
	  */
	 Set<MethodDeclaration> methods= this.getMethodDeclarationNodes(paramFile);
	 for(MethodDeclaration methodDecl:methods)
	 {
	    List<SingleVariableDeclaration> paras= methodDecl.parameters();
	    for(SingleVariableDeclaration para:paras)
	    {
		  if(para.getType().isSimpleType())invokeClazzes.add(para.getType().toString());
	    }
	    if(methodDecl.getReturnType2()!=null)
		  if(methodDecl.getReturnType2().isSimpleType())
		     invokeClazzes.add(methodDecl.getReturnType2().toString());
	 }
	 
	 /**Others such as inheritance, exceptions*/
	 
	 return invokeClazzes.size();
   }
}
