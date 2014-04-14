package SNPViewer;

import java.awt.Color;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class tester {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		
		Pattern headerPattern = Pattern.compile("[$]\\n(.*?)\\n[$]", Pattern.DOTALL);
		Pattern chrPattern = Pattern.compile("@([^@]+?)\\n(.+?)(?=@|$)", Pattern.DOTALL);
		Pattern matrixPattern = Pattern.compile("[#][0-9]+?\\n(.+?)(?=[#]|$)", Pattern.DOTALL);
		Pattern linePattern = Pattern.compile("^(.+?) - (.+?)[:] ([0-9.]+?)(?=\\n|$)", Pattern.MULTILINE);
		
		Path filePath = Paths.get("/home/javi/workspace/SNPViewer/bin/SNPViewer/3045..persistentMatrix.txt.sim");
		
//		String s1 = "3045.\tME49\tP89.S\tARI.V\tVEG.S\tGT1.S\n3142.\tTGSKN";
//		String s2 = "3045.\t3142.\tTGSKN\tARI.V\nME49\tVEG.S\nP89.S\tGT1.S";
//		System.out.println(s1.equals(s2));
		
		System.out.println(new Color(16777215));
	
	}

}
