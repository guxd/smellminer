package smellminer.engine.dataprepare.codemetrics.astparser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.FileUtils;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;

public class JavaASTExtractor
{
   private final boolean useBindings;
   private final boolean useJavadocs;

   public JavaASTExtractor(boolean useBindings) {
	 this.useBindings = useBindings;
	 this.useJavadocs = false;
   }

   public JavaASTExtractor(boolean useBindings, boolean useJavadocs) {
	 this.useBindings = useBindings;
	 this.useJavadocs = useJavadocs;
   }

   public final CompilationUnit getAST(File file) throws IOException
   {
	 return getAST(file, new HashSet<String>());
   }

   public final CompilationUnit getAST(File file, Set<String> srcPaths) throws IOException
   {
	 String sourceFile = FileUtils.readFileToString(file);
	 ASTParser parser = ASTParser.newParser(AST.JLS8);
	 parser.setKind(ASTParser.K_COMPILATION_UNIT);
	 Map<String, String> options = new Hashtable<String, String>();
	 options.put("org.eclipse.jdt.core.compiler.codegen.targetPlatform", "1.8");
	 options.put("org.eclipse.jdt.core.compiler.source", "1.8");
	 if (this.useJavadocs)
	 {
	    options.put("org.eclipse.jdt.core.compiler.doc.comment.support", "enabled");
	 }
	 parser.setCompilerOptions(options);
	 parser.setSource(sourceFile.toCharArray());
	 parser.setResolveBindings(this.useBindings);
	 parser.setBindingsRecovery(this.useBindings);
	 parser.setStatementsRecovery(true);
	 parser.setUnitName(file.getAbsolutePath());
	 String srcFilePath;
	 if (file.getAbsolutePath().contains("/src"))
	    srcFilePath = file.getAbsolutePath().substring(0,
			file.getAbsolutePath().indexOf("src", 0) + 3);
	 else
	 {
	    srcFilePath = "";
	 }
	 srcPaths.add(srcFilePath);
	 String[] sourcePathEntries = (String[]) srcPaths.toArray(new String[srcPaths.size()]);
	 String[] classPathEntries = new String[0];
	 parser.setEnvironment(classPathEntries, sourcePathEntries, null, true);
	 CompilationUnit compilationUnit = (CompilationUnit) parser.createAST(null);
	 return compilationUnit;
   }

   public final ASTNode getAST(String fileContent, int parseType)
   {
	 return getASTNode(fileContent, parseType);
   }

   public final ASTNode getASTNode(char[] content, int parseType)
   {
	 ASTParser parser = ASTParser.newParser(AST.JLS8);
	 parser.setKind(parseType);
	 Map<String, String> options = new Hashtable<String, String>();
	 options.put("org.eclipse.jdt.core.compiler.codegen.targetPlatform", "1.8");
	 options.put("org.eclipse.jdt.core.compiler.source", "1.8");
	 if (this.useJavadocs)
	 {
	    options.put("org.eclipse.jdt.core.compiler.doc.comment.support", "enabled");
	 }
	 parser.setCompilerOptions(options);
	 parser.setSource(content);
	 parser.setResolveBindings(this.useBindings);
	 parser.setBindingsRecovery(this.useBindings);
	 parser.setStatementsRecovery(true);
	 return parser.createAST(null);
   }

   public final ASTNode getASTNode(String fileContent, int parseType)
   {
	 return getASTNode(fileContent.toCharArray(), parseType);
   }



   private final MethodDeclaration getFirstMethodDeclaration(ASTNode node)
   {
	 TopMethodRetriever visitor = new TopMethodRetriever();
	 node.accept(visitor);
	 return visitor.topDcl;
   }



   private static final class TopMethodRetriever extends ASTVisitor
   {
	 public MethodDeclaration topDcl;

	 public boolean visit(MethodDeclaration node)
	 {
	    this.topDcl = node;
	    return false;
	 }
   }

   
}
