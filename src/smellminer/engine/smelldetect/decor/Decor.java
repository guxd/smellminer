package smellminer.engine.smelldetect.decor;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import padl.creator.javafile.eclipse.CompleteJavaFileCreator;
import padl.creator.javafile.eclipse.LightJavaFileCreator;
import padl.generator.helper.ModelGenerator;
import padl.kernel.IClass;
import padl.kernel.ICodeLevelModel;
import padl.kernel.IFirstClassEntity;
import padl.kernel.IGhost;
import padl.kernel.IIdiomLevelModel;
import padl.kernel.exception.CreationException;
import padl.kernel.exception.ModelDeclarationException;
import padl.kernel.impl.Factory;
import pom.metrics.IMetric;
import pom.metrics.MetricsRepository;
import pom.metrics.repository.CBO;
import pom.metrics.repository.LCOM1;
import pom.metrics.repository.LOC;
import pom.metrics.repository.NOA;
import pom.metrics.repository.NOM;
import pom.metrics.repository.RFC;
import pom.metrics.repository.WMC;
import sad.codesmell.detection.CodeSmellDetectionsRepository;
import sad.codesmell.detection.ICodeSmellDetection;
import sad.codesmell.detection.repository.Blob.ControllerClassDetection;
import sad.kernel.ICodeSmell;
import sad.util.OperatorsCodeSmells;
import smellminer.definition.FileSnapshot;
import smellminer.definition.Metrics;
import smellminer.engine.dataprepare.codemetrics.IMetricExtract;
import smellminer.engine.smelldetect.ISmellDetect;
import util.io.ProxyConsole;
import util.repository.IRepository;

public class Decor implements ISmellDetect, IMetricExtract
{
   IIdiomLevelModel padlModelFromSrcFiles = null;

   public Decor(String sourcepath) {
	 /*
	  * padlModelFromSrcFiles
	  * =Factory.getInstance().createCodeLevelModel("My name"); try {
	  * padlModelFromSrcFiles.create(new
	  * CompleteJavaFileCreator(sourcepath,""));
	  * padlModelFromSrcFiles.create(new LightJavaFileCreator(sourcepath,""));
	  * }catch (final CreationException e) {
	  * e.printStackTrace(ProxyConsole.getInstance().errorOutput()); }
	  */
	 
	    padlModelFromSrcFiles = ModelGenerator
			.generateModelFromJavaFilesDirectoriesUsingEclipse(sourcepath);
	 
   }
   public Decor(String projType,String sourcepath) {
	 
	   if(projType.equalsIgnoreCase("java"))
	    padlModelFromSrcFiles = ModelGenerator
			.generateModelFromJavaFilesDirectoriesUsingEclipse(sourcepath);
	   else if(projType.matches("csharp|c#"))
	   {		   
		   String[] someCSharpFiles=new String[]{};
		   padlModelFromSrcFiles=ModelGenerator.generateModelFromCSharpFilesV1(
				   "", someCSharpFiles, null);
	   }
	   else if(projType.matches("c++|cpp|cplusplus"))
	   {
		   padlModelFromSrcFiles=ModelGenerator.generateModelFromCppFilesUsingEclipse
				   ("", sourcepath);
	   }
	 
   }

   public static void main(String[] args)
   {
	 // String
	 // sourcepath="D:\\workspace\\reviewmining\\src\\mining\\featureclustering";
	 String sourcepath = "src/smellminer/engine/dataprepare/bugtracks/dbbugtracker";
	 Decor decor = new Decor(sourcepath);
	 Set<String> smells = decor.detectSmellTypes();
	 for (String smell : smells)
	    System.out.println(smell);
	 Metrics metrics=decor.extractMetrics(new File(""));
	 System.out.println(metrics);
   }

   @Override
   public void defineSmellRules()
   {
	 // TODO Auto-generated method stub
   }

   @Override
   public Set<String> detectSmellTypes()
   {
	 Set<String> smelltypes = new HashSet<String>();
	 /* Blob */
	 Set<ICodeSmell> blobsmells = new HashSet<ICodeSmell>();
	 CodeSmellDetectionsRepository repo = CodeSmellDetectionsRepository.getInstance();
	 ICodeSmellDetection blobDetector1 = repo.getCodeSmellDetection("ControllerClassDetection");
	 blobDetector1.detect(padlModelFromSrcFiles);
	 blobsmells.addAll(blobDetector1.getCodeSmells());
	 ICodeSmellDetection blobDetector2 = repo.getCodeSmellDetection("DataClass");
	 blobDetector2.detect(padlModelFromSrcFiles);
	 blobsmells.addAll(blobDetector2.getCodeSmells());
	 ICodeSmellDetection blobDetector3 = repo.getCodeSmellDetection("LargeClassDetection");
	 blobDetector3.detect(padlModelFromSrcFiles);
	 blobsmells.addAll(blobDetector3.getCodeSmells());
	 ICodeSmellDetection blobDetector4 = repo.getCodeSmellDetection("LowCohesionDetection");
	 blobDetector4.detect(padlModelFromSrcFiles);
	 blobsmells.addAll(blobDetector4.getCodeSmells());
	 if (!blobsmells.isEmpty()) smelltypes.add("Blob");
	 /* ClassDataShouldBePrivate */
	 Set<ICodeSmell> cdsbsmells = new HashSet<ICodeSmell>();
	 ICodeSmellDetection cdsbDetector1 = repo.getCodeSmellDetection("FieldPublic");
	 cdsbDetector1.detect(padlModelFromSrcFiles);
	 cdsbsmells.addAll(cdsbDetector1.getCodeSmells());
	 if (!cdsbsmells.isEmpty()) smelltypes.add("CDSBP");
	 /* ComplexClass */
	 Set<ICodeSmell> ccsmells = new HashSet<ICodeSmell>();
	 ICodeSmellDetection ccDetector1 = repo.getCodeSmellDetection("ComplexClassOnlyDetection");
	 ccDetector1.detect(padlModelFromSrcFiles);
	 ccsmells.addAll(ccDetector1.getCodeSmells());
	 ICodeSmellDetection ccDetector2 = repo.getCodeSmellDetection("LargeClassOnlyDetection");
	 ccDetector2.detect(padlModelFromSrcFiles);
	 ccsmells.addAll(ccDetector2.getCodeSmells());
	 if (!ccsmells.isEmpty()) smelltypes.add("CC");
	 /* FunctionalDecomposition */
	 Set<ICodeSmell> fdsmells = new HashSet<ICodeSmell>();
	 ICodeSmellDetection fdDetector1 = repo.getCodeSmellDetection("ClassOneMethod");
	 fdDetector1.detect(padlModelFromSrcFiles);
	 fdsmells.addAll(fdDetector1.getCodeSmells());
	 ICodeSmellDetection fdDetector2 = repo.getCodeSmellDetection("FieldPrivate");
	 fdDetector2.detect(padlModelFromSrcFiles);
	 fdsmells.addAll(fdDetector2.getCodeSmells());
	 ICodeSmellDetection fdDetector3 = repo.getCodeSmellDetection("FunctionClassDetection");
	 fdDetector3.detect(padlModelFromSrcFiles);
	 fdsmells.addAll(fdDetector3.getCodeSmells());
	 ICodeSmellDetection fdDetector4 = repo.getCodeSmellDetection("NoInheritanceDetection");
	 fdDetector4.detect(padlModelFromSrcFiles);
	 fdsmells.addAll(fdDetector4.getCodeSmells());
	 ICodeSmellDetection fdDetector5 = repo.getCodeSmellDetection("NoPolymorphism");
	 fdDetector5.detect(padlModelFromSrcFiles);
	 fdsmells.addAll(fdDetector5.getCodeSmells());
	 if (!fdsmells.isEmpty()) smelltypes.add("FD");
	 /* SpaghettiCode */
	 Set<ICodeSmell> scsmells = new HashSet<ICodeSmell>();
	 ICodeSmellDetection scDetector1 = repo.getCodeSmellDetection("ClassGlobalVariable");
	 scDetector1.detect(padlModelFromSrcFiles);
	 fdsmells.addAll(scDetector1.getCodeSmells());
	 ICodeSmellDetection scDetector2 = repo.getCodeSmellDetection("LongMethodDetection");
	 scDetector2.detect(padlModelFromSrcFiles);
	 fdsmells.addAll(scDetector2.getCodeSmells());
	 ICodeSmellDetection scDetector3 = repo.getCodeSmellDetection("MethodNoParameterDetection");
	 scDetector3.detect(padlModelFromSrcFiles);
	 fdsmells.addAll(scDetector3.getCodeSmells());
	 ICodeSmellDetection scDetector4 = repo.getCodeSmellDetection("NoInheritanceDetection");
	 scDetector4.detect(padlModelFromSrcFiles);
	 fdsmells.addAll(scDetector4.getCodeSmells());
	 if (!scsmells.isEmpty()) smelltypes.add("SC");
	 return smelltypes;
   }

   @Override
   public String findSmellIntroRevision(Map<String, FileSnapshot> filestat, double regr_slop)
   {
	 // TODO Auto-generated method stub
	 return null;
   }

	@Override
	public Metrics extractMetrics(File file) {
		MetricsRepository repo = MetricsRepository.getInstance();
		Iterator<IFirstClassEntity> firstEntitys = padlModelFromSrcFiles
				.getIteratorOnTopLevelEntities();
		IFirstClassEntity firstEntity = null;
		while (firstEntitys.hasNext()) {
			IFirstClassEntity entity = firstEntitys.next();
			if(entity instanceof IGhost)continue;
			else 
			{ 
				firstEntity=entity;
				break;//only consider the first class
			}
		
		}
		if (firstEntity == null) {
			System.err.print("failed to get first level entity! metrics extract fail!");
			return new Metrics(-1, -1, -1, -1, -1, -1, -1);
		}
		IMetric locmetric = repo.getMetric("LOC");
		double loc = ((LOC)locmetric).compute(padlModelFromSrcFiles,
					firstEntity);
		IMetric wmcmetric = repo.getMetric("WMC");
		double wmc = ((WMC) wmcmetric).compute(padlModelFromSrcFiles,
					firstEntity);
		IMetric rfcmetric = repo.getMetric("RFC");
		double rfc = ((RFC) rfcmetric).compute(padlModelFromSrcFiles,
					firstEntity);
		IMetric cbometric = repo.getMetric("CBO");
		double cbo = ((CBO) cbometric).compute(padlModelFromSrcFiles,
					firstEntity);
		IMetric lcommetric = repo.getMetric("LCOM1");
		double lcom = ((LCOM1) lcommetric).compute(padlModelFromSrcFiles,
					firstEntity);
		IMetric noametric = repo.getMetric("NOA");
		double noa = ((NOA) noametric).compute(padlModelFromSrcFiles,
					firstEntity);
		IMetric nommetric = repo.getMetric("NOM");
		double nom = ((NOM) nommetric).compute(padlModelFromSrcFiles,
					firstEntity);
		return new Metrics((int) loc, wmc, (int) rfc, (int) cbo, lcom,
					(int) noa, (int) nom);
		}
	
}
