package smellminer.engine.dataprepare.codemetrics.extractors;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import smellminer.engine.dataprepare.codemetrics.astparser.JavaASTExtractor;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class ResponseForClassRetriever extends ASTVisitor implements IFileMetricRetriever
{
   
   public Set<MethodInvocation> getMethodInvokeNodes(File file) throws IOException
   {
	 JavaASTExtractor astExtractor = new JavaASTExtractor(false);
	 ResponseForClassRetriever m = new ResponseForClassRetriever();
	 CompilationUnit cu = astExtractor.getAST(file);
	 cu.accept(m);
	 return m.methods;
   }

   private final Set<MethodInvocation> methods = Sets.newHashSet();

   public boolean visit(MethodInvocation node)
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
	 Set<String> invokeNames=Sets.newHashSet();
	 Set<MethodInvocation> invocations= this.getMethodInvokeNodes(paramFile);
	 for(MethodInvocation invoke:invocations)
	 {
	    invokeNames.add(invoke.getName().getIdentifier());
	 }
	 return invokeNames.size();
   }


}
