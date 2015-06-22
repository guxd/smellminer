package smellminer.engine.dataprepare.codemetrics.extractors;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import smellminer.engine.dataprepare.codemetrics.astparser.JavaASTExtractor;

public final class JavaMethodClassCounter
{
   public static class MethodClassCountVisitor extends ASTVisitor
   {
	 public int noMethods = 0;
	 public int noClasses = 0;
      
	 public void postVisit(ASTNode node)
	 {
	    if (node.getNodeType() == ASTNode.METHOD_DECLARATION)
	    {
		  this.noMethods += 1;
	    }
	    if ((node.getNodeType() == ASTNode.TYPE_DECLARATION) || (node.getNodeType() == ASTNode.ENUM_DECLARATION))
	    {
		  this.noClasses += 1;
	    }
	 }
   }

   public static void main(String[] args) throws IOException
   {
	 if (args.length != 1)
	 {
	    System.err.println("Usage <inputDirectory>");
	    System.exit(-1);
	 }
	 File directory = new File(args[0]);
	 countMethodsClasses(directory);
   }

   public static void countMethodsClasses(File projectDir) throws IOException
   {
	 System.out.println("\n===== Project " + projectDir);
	 MethodClassCountVisitor mccv = new MethodClassCountVisitor();
	 JavaASTExtractor astExtractor = new JavaASTExtractor(false);
	 List<File> files = (List) FileUtils.listFiles(projectDir, new String[] { "java" }, true);
	 int count = 0;
	 for (File file : files)
	 {
	    CompilationUnit cu = astExtractor.getAST(file);
	    cu.accept(mccv);
	    if (count % 1000 == 0)
	    {
		  System.out.println("At file " + count + " of " + files.size());
	    }
	    count++;
	 }
	 System.out.println("Project " + projectDir);
	 System.out.println("No. *.java files " + files.size());
	 System.out.println("No. Methods: " + mccv.noMethods);
	 System.out.println("No. Classes: " + mccv.noClasses);
   }
}
/*
 * Location: C:\Users\Xiaodong\tsg\
 * 
 * Qualified Name: codemining.java.codedata.metrics.JavaMethodClassCounter
 * 
 * JD-Core Version: 0.7.1
 */