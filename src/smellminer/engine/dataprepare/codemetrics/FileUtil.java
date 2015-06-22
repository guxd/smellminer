package smellminer.engine.dataprepare.codemetrics;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class FileUtil {
	
	static public ArrayList<String> getLines(String file){
		ArrayList<String> lines = new ArrayList<String>();
		String thisLine="";
		//Open the file for reading
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			while ((thisLine = br.readLine()) != null) { // while loop begins here
				lines.add(thisLine);
			} // end while 
			br.close();
		} // end try
		catch (IOException e) {
			System.err.println("Error: " + e);
		}
		
		return lines;
	}
	
	static ArrayList<String> loadListOfFiles(String path){
		ArrayList<String> listOfFiles = new ArrayList<String>();
		
		File dir = new File(path);
		
		FileFilter fileFilter = new FileFilter() {
			@Override
			public boolean accept(File file) {
				return file.isFile();
			}
		};
		
		File[] files = dir.listFiles(fileFilter);
		
		for(File file:files){
			String fileName = file.getName();
			listOfFiles.add(fileName);
		}
		
		return listOfFiles;
	}
	
	static String getFirstLine(String file){
		String thisLine="";
		//Open the file for reading
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			thisLine = br.readLine();
			br.close();
		} // end try
		catch (IOException e) {
			System.err.println("Error: " + e);
		}
		
		return thisLine;
	}
	
	public static void writeAFile(ArrayList<String> lines, String targetFileName){
		try {
			File file= new File(targetFileName);
			FileOutputStream fos = new FileOutputStream(file);
			DataOutputStream dos=new DataOutputStream(fos);
			
			for(String line:lines){
				dos.write((line+"\n").getBytes());
			}
			//dos.writeBytes();
			dos.close();
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	static public void print(ArrayList<String> lines,int targetColumn){
		for(String line:lines){
			if(targetColumn > 0 )
				System.out.println(line.split(",")[targetColumn]);
			else
				System.out.println(line);
		}
	}

}
