package SNPViewer;

import java.awt.Color;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.TreeMap;

/*
 * This class reads tabnetwork.tsv and displays each sample on a line.
 * You should be able to define a particular chromosome and even regions in
 * some fashion. Legends won't be available by default, and would have to be
 * added on in illustrator.
 */
public class SyntenyParser extends DataParser {

	private String[] names;
	private int start;
	private int end;
	private String chrSelect;
	private HashMap<String, Integer> chrMap;
	
	public SyntenyParser(Path source, String mode){
		super(source, mode);
		this.start = 0;
		this.end = Integer.MAX_VALUE;
		this.chrSelect = "I";
		this.chrMap = new HashMap<String, Integer>();
	}
	
	
	public void setRange(int start, int end){
		this.start = start;
		this.end = end;
	}
	
	public void setChr(String chr){
		this.chrSelect = chr;
	}
	
	public HashMap<String, Integer> getChrMap(){
		return this.chrMap;
	}
	
	@Override
	public TreeMap<Integer, ArrayList<DataPoint>> parseData() {
		
		Pattern headerPattern = Pattern.compile("Chromosome\\tPosition\\t(.+?)\\n");
		Pattern chrPattern = Pattern.compile("@[\\w]+?_CHR([\\w]+?)\\t");
		Pattern posPattern = Pattern.compile("\\t([\\d]+?)\\t");
		Pattern colorPattern = Pattern.compile("\\t(#[\\w]{6}.+?)$");
		
		TreeMap<Integer, ArrayList<DataPoint>> results = new TreeMap<Integer, ArrayList<DataPoint>>();
		
		Matcher headerMatcher = headerPattern.matcher(rawData);
		headerMatcher.find();
		String header = headerMatcher.group(1);
		List<String> names = Arrays.asList(header.split("\t"));
		
		Integer ind = 0;
		for(String name: names){
			results.put(ind, new ArrayList<DataPoint>());
			this.chrMap.put(name, ind);
			ind += 1;
		}
		
		List<String> lines = Arrays.asList(rawData.split("\\n"));
		for(String line : lines.subList(1, lines.size() - 1)){
			Matcher chrMatcher = chrPattern.matcher(line);
			chrMatcher.find();
			Matcher posMatcher = posPattern.matcher(line);
			posMatcher.find();
			Matcher colorMatcher = colorPattern.matcher(line);
			colorMatcher.find();
			
			String chr = chrMatcher.group(1);
			int pos = Integer.parseInt(posMatcher.group(1)) / 10000;
			String data = colorMatcher.group(1);
			
			String[] colors = data.split("\\t");
			ind = 0;
			if(chr.equals(this.chrSelect)){
				for(String s: colors){
				results.get(ind).add(hexToDp(pos, s));
				ind++;
				}
			}
			
		}
		return results;
	}
	
	private DataPoint hexToDp(int position, String hex){
	//turns a hex number in the format #AA00AA into a datapoint	
		
		return new DataPoint(position, position, Color.decode(hex), hex);
	}

}
