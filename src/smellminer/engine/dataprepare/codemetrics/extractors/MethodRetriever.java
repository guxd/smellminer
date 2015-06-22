package smellminer.engine.dataprepare.codemetrics.extractors;

import com.google.common.collect.Maps;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SimpleName;
import smellminer.engine.dataprepare.codemetrics.astparser.JavaASTExtractor;

public final class MethodRetriever extends ASTVisitor implements IFileMetricRetriever
{
   public static Map<String, MethodDeclaration> getMethodNodes(File file) throws IOException
   {
	 JavaASTExtractor astExtractor = new JavaASTExtractor(false);
	 MethodRetriever m = new MethodRetriever();
	 CompilationUnit cu = astExtractor.getAST(file);
	 cu.accept(m);
	 return m.methods;
   }

   private final Map<String, MethodDeclaration> methods = Maps.newTreeMap();

   public boolean visit(MethodDeclaration node)
   {
	 methods.put(node.getName().toString(), node);
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
	 return getMethodNodes(paramFile).values().size();
   }
}
/*
 * Location: C:\Users\Xiaodong\tsg\
 * 
 * Qualified Name: codemining.java.codedata.MethodRetriever
 * 
 * JD-Core Version: 0.7.1
 */