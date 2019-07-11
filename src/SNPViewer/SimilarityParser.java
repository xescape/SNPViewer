package SNPViewer;

import java.awt.Color;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SimilarityParser extends DataParser {

	
	
	public SimilarityParser(Path source, String mode) {
		super(source, mode);
		// TODO Auto-generated constructor stub
	}

	@Override
	public TreeMap<Integer, ArrayList<DataPoint>> parseData() {
		// TODO Auto-generated method stub
		String header;
		String[] types;
		String[] lineInfo;
		String body;
		String matrix;
		float num;
		String chr;
		TreeMap<Integer, ArrayList<DataPoint>> results = new TreeMap<Integer, ArrayList<DataPoint>>();
		ChromosomeNameConverter cnc = new ChromosomeNameConverter();
		
		
		Pattern headerPattern = Pattern.compile("[$]\\n(.*?)\\n[$]", Pattern.DOTALL);
		Pattern chrPattern = Pattern.compile("@([^@]+?)\\n(.+?)(?=@|$)", Pattern.DOTALL);
		Pattern matrixPattern = Pattern.compile("[#]([0-9]+?)\\n(.+?)(?=[#]|$)", Pattern.DOTALL);
		Matcher headerMatcher = headerPattern.matcher(rawData);
		headerMatcher.find();
		header = headerMatcher.group(1);		
		types = Pattern.compile("\\n").split(header);	
		System.out.println(Arrays.toString(types));
		Matcher bodyMatcher = chrPattern.matcher(rawData);
		while(bodyMatcher.find()){
			chr = bodyMatcher.group(1);
			body = bodyMatcher.group(2);
			ArrayList<DataPoint> dps = new ArrayList<DataPoint>();
			Matcher matrixMatcher = matrixPattern.matcher(body);
			while(matrixMatcher.find()){
				dps.add(parseMatrix(matrixMatcher.group(2), Integer.parseInt(matrixMatcher.group(1))));				
			}
			results.put(cnc.convert(chr), dps);
		}
		
		setLegend(types);
		return results;
	}
	
	
	protected void setLegend(String[] types){
		Color[] colors = {new Color(255,0,0), new Color(0,0,255), new Color(0,255,0)};
		legend = new HashMap<String, Color>();
		
		if(types.length > 3){ throw new IndexOutOfBoundsException("There are too many type for the legend");}
		for(int x = 0; x > types.length; x++){
			legend.put(types[x], colors[x]);
		}
	}
	
	protected DataPoint parseMatrix(String matrix, int position ){
		Pattern linePattern = Pattern.compile("^(.+?) - (.+?)[:] ([0-9.]+?)(?=\\n|$)", Pattern.MULTILINE);
		Matcher lineMatcher = linePattern.matcher(matrix);
		Float[] info = new Float[3];
		for(int x=0; x<3; x++){
			lineMatcher.find();
			info[x] = Float.parseFloat(lineMatcher.group(3));
			
		}
		System.out.println(new Color(info[0], info[2], info[1]));
		// THIS LINE DETERMINES THE COLOR OF EACH ANCESTOR
		return new DataPoint(position, position, new Color(info[0], info[2], info[1]), matrix);
	}
	

}
