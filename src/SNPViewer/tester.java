package SNPViewer;

import java.awt.Color;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class tester {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		

//		Pattern headerPattern = Pattern.compile("Chromosome\\tPosition\\t(.+?)$");
//		Pattern chrPattern = Pattern.compile("@[\\w]+?_CHR([\\w]+?)\\t");
//		Pattern posPattern = Pattern.compile("\\t([\\d]+?)\\t");
//		Pattern colorPattern = Pattern.compile("\\t(#[\\w]{6}.+?)$");
//		
//		Path filePath = Paths.get("/home/javi/workspace/SNPViewer/bin/SNPViewer/3045..persistentMatrix.txt.sim");
//		
//		String s1 = "Chromosome\tPosition\tSAMP1\tSAMP2\tSAMP3\tSAMP4";
//		String s2 = "@TGME49_CHRIV\t9000\t#67GG87\t#90II89\t#AA99AA\t#EE99EE";
////		System.out.println(s1.equals(s2));
//		
//		Matcher head = headerPattern.matcher(s1);
//		head.find();
//		System.out.println(head.group(1));
//		
//		Matcher col = colorPattern.matcher(s2);
//		col.find();
//		System.out.println(col.group(1));
	
		TreeMap<Integer, ArrayList<String>> a = new TreeMap<Integer, ArrayList<String>>();
		a.put(1, new ArrayList<String>());
		a.put(0, new ArrayList<String>());
		a.get(1).add("hello");
		a.get(0).add("world");
		System.out.println(a);
		
	}

}
