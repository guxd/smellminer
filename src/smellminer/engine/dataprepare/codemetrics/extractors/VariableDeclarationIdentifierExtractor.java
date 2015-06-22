package smellminer.engine.dataprepare.codemetrics.extractors;

import org.eclipse.jdt.core.dom.IExtendedModifier;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.SimpleName;
import java.io.IOException;
import java.util.Iterator;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;
import java.io.File;
import com.google.common.collect.Sets;
import java.util.Set;
import java.util.logging.Logger;
import org.eclipse.jdt.core.dom.ASTVisitor;
import smellminer.engine.dataprepare.codemetrics.astparser.JavaASTExtractor;

public class VariableDeclarationIdentifierExtractor extends ASTVisitor
{
   private static final Logger LOGGER = Logger.getLogger(VariableDeclarationIdentifierExtractor.class
		  .getName());
   private Set<String> vars;

   public VariableDeclarationIdentifierExtractor() {
	 super();
	 this.vars = Sets.newTreeSet();
   }

   public static void main(final String[] args) throws IOException
   {
	 for (final File f : FileUtils.listFiles(new File(args[0]),
		  new RegexFileFilter(".*\\.java$"), DirectoryFileFilter.DIRECTORY))
	 {
	    VariableDeclarationIdentifierExtractor.LOGGER.info("Processing "
			+ f.getAbsolutePath());
	    try
	    {
		  final JavaASTExtractor ex = new JavaASTExtractor(false);
		  final VariableDeclarationIdentifierExtractor cpl = new VariableDeclarationIdentifierExtractor();
		  ex.getAST(f).accept(cpl);
	    } catch (Exception e)
	    {
		  e.printStackTrace();
	    }
	 }
   }

   @Override
   public boolean visit(final SimpleName node)
   {
	 this.vars.add(node.getIdentifier());
	 return super.visit(node);
   }

   @Override
   public boolean visit(final VariableDeclarationStatement node)
   {
	 Iterator<VariableDeclarationFragment> it=node.fragments().iterator();
	 while(it.hasNext())
	 {
	    final VariableDeclarationFragment fr=it.next();
	    System.out.println(node.getParent().getParent().getClass().getSimpleName());
	    Iterator<IExtendedModifier> itt=node.modifiers().iterator();
	    while( itt.hasNext())
	    {
		  final IExtendedModifier mod=itt.next();
		  System.out.print(mod + " ");
	    }
	    System.out.println();
	    System.out.println(node.getType());
	    for (final String name : this.vars)
	    {
		  System.out.print(String.valueOf(name) + " ");
	    }
	    System.out.println();
	    System.out.println(fr.getName());
	 }
	 return super.visit(node);
   }
}