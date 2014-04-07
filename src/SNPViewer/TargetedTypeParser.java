package SNPViewer;

import java.awt.Color;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TargetedTypeParser extends TypeParser {

	
String target;

//	Target must match exactly as it is represented. The 5 char thing. 
	public TargetedTypeParser(Path source, String mode, String target) {
		super(source, mode);
		// TODO Auto-generated constructor stub
		this.target = target;
	}

	@Override
	protected void setColorScheme(String schemeName, String info){
		
		Matcher typeMatcher = typePattern.matcher(info);
		
		System.out.println("setting color scheme");
		switch(schemeName.toUpperCase()){
		case "SIMILARITY": colorScheme = targetSimilarityScheme(typeMatcher);
			break;
		}
		
		
	}
	
	private HashMap<String, Color> targetSimilarityScheme(Matcher typeMatcher){
		/**
		 * TODO: This applies the channel method to the cluster analysis. Firstly,
		 * this allows us to directly compare the two methods and note the errors
		 * in the clustering method (just how good is it?)
		 * once validated, the clustering method would be preferred.
		 * Basic methodology:
		 * have three values. You test for similarity against three catagories. 
		 * for each test, set one of the values to 0 of 1, according to the result
		 * assign color using those three values, just like the channel method. Except
		 * there won't be any intermediate shades. 
		 */
		System.out.println("Targeted Similarity Scheme");
		
		ArrayList<String[][]> orderList = new ArrayList<String[][]>();
		orderList.add(new String[][]{typeI});
		orderList.add(new String[][]{typeIII});
		orderList.add(new String[][]{typeII});
		
		//other stuff = white
		//match all = white
		//no match = black
		//insufficient data = white
		HashMap<String, Color> clusterTypeMap = new HashMap<String, Color>();
		
		List<Float> colorList;
		String temp;
		while(typeMatcher.find()){
			temp = typeMatcher.group(1);
			colorList =  new ArrayList<Float>();
			for(String[][] ct : orderList){
				if(checkType(ct, temp, false)){
					colorList.add(1f);
				}
				else{
					colorList.add(0f);
				}
			}
			
			clusterTypeMap.put(temp, new Color(colorList.get(0),colorList.get(1),colorList.get(2)));
		}
	
		legend = new HashMap<String, Color>();
//		legend.put("type I", Color.RED);
//		legend.put("type III", Color.BLUE);
		legend.put("TypeII", Color.BLUE);
		legend.put("TypeI", Color.RED);
		legend.put("TypeIII", Color.GREEN);
		legend.put("None", Color.BLACK);
		legend.put("All or N/A", Color.WHITE);
	
		return clusterTypeMap;
	}
	
	
	protected boolean checkType(String[][] types, String clusterType, boolean unique){
		List<String[]> typesList = Arrays.asList(types);
		Pattern splitPattern = Pattern.compile("\n");
		for(String s:Arrays.asList(splitPattern.split(clusterType))){
			if(checkLineType(typesList, s, unique)) return true;
		}
		return false;
	}
	
	@Override
	protected boolean checkLineType(List<String[]> type, String clusterType, boolean unique){
		//	clusterType represents just one line
				Pattern splitPattern = Pattern.compile("\\t");
				List<String> eList = Arrays.asList(splitPattern.split(clusterType));
				
//				if(clusterType.contains(reference[0]) && !type.contains(reference)) return false;
//we don't care much about the reference in this case.
//				
//				if(unique){
//					ArrayList<String> allOtherTypes = new ArrayList<String>();
//					for (String[] sa : Arrays.asList(typeArray)){
//						if(!type.contains(sa)) allOtherTypes.addAll(Arrays.asList(sa));
//					}
//					if(containsAny(eList, allOtherTypes)) return false;
//				}
//				
				List<List<String>> allowedElements = new ArrayList<List<String>>();
				for(String[] sa : type){
					allowedElements.add(Arrays.asList(sa));
				}
		
				if(targetMatch(allowedElements, eList)) return true;
				
				return false;
			}
	
	protected boolean targetMatch(List<List<String>> candidates, List<String> elements){
		if(!elements.contains(target)){

			return false;
		}
		else{

			for(List<String> catagory:candidates){
				for(String t: catagory){
					if(elements.contains(t)) return true;
				}
			}
		}
		return false;
	}
	
	public void test(){
		String y = "3045.\tGT1.S\tME49\nARI.V\nP89.S\tVEG.S\n3142.\tTGSKN";
		System.out.println(checkType(new String[][]{typeI}, y, false));
		System.out.println(checkType(new String[][]{typeII}, y, false));
		System.out.println(checkType(new String[][]{typeIII}, y, false));
	}
}
