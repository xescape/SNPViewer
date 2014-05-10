package SNPViewer;

import java.awt.Color;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * This subclass is the result of a need to change the behavior of SimilarityParser. In this case,
 * we are no longer using a 3-strain comparison. Instead, we will be looking at how many types of 
 * patterns there are in a particular clustering. Each type will be represented individually. This
 * overcomes the 3-channel barrier, at the cost of the colors not really meaning anything anymore.
 * The only information that can be gleamed from this mode is whether regions are identical to each other.
 * This isn't even a measurement of How identical, because that turns out to be a difficult question. 
 * The behavior is as follows:
 * 
 * It expects one line, of the same pattern at SimilarityParser, with an integer value at the end as 
 * opposed to a float. This integer value will be used in the Color(int sRGB) constructor to create the
 * color for this section. The return type and everything else is the same as SimilarityParser. 
 * @author Javi
 *
 */
public class ClusterPatternParser extends SimilarityParser {

	
	public ClusterPatternParser(Path source, String mode) {
		super(source, mode);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public TreeMap<Integer, ArrayList<DataPoint>> parseData() {
		TreeMap<Integer, ArrayList<DataPoint>> results = super.parseData();
		
		Map<String, Color> colormap = makeColorMap(results);
		setLegend(colormap);
		return results;
	}
	
	@Override
	protected DataPoint parseMatrix(String matrix, int position ){
		Pattern linePattern = Pattern.compile("^(.+?) - (.+?)[:] ([0-9.]+?)(?=\\n|$)", Pattern.MULTILINE);
		Matcher lineMatcher = linePattern.matcher(matrix);
		int info;
		lineMatcher.find();
		info = Integer.parseInt(lineMatcher.group(3));
		System.out.println(new Color(info));
		return new DataPoint(position, position, new Color(info), lineMatcher.group(2));
	}
	

	protected void setLegend(Map<String, Color> colormap){
		legend = new HashMap<String, Color>();
		Iterator<Entry<String, Color>> itr = colormap.entrySet().iterator();
		while(itr.hasNext()){
			Entry<String, Color> next = itr.next();
			legend.put(next.getKey(), next.getValue());
		}
	}
	
	protected Map<String, Color> makeColorMap(TreeMap<Integer, ArrayList<DataPoint>> data){
		Map<String, Color> result = new HashMap<String, Color>();
		Set<Entry<Integer, ArrayList<DataPoint>>> entrySet = data.entrySet();
		Iterator<Entry<Integer, ArrayList<DataPoint>>> itr  = entrySet.iterator();
		while(itr.hasNext()){
			Entry<Integer, ArrayList<DataPoint>> next = itr.next();
			Iterator<DataPoint> dataItr = next.getValue().iterator();
			while(dataItr.hasNext()){
				DataPoint point = dataItr.next();
				String name = point.getData();
				Color color = point.getColor();
				if(!(result.containsKey(name)&&result.containsValue(color))){
					result.put(name, color);
				}
			}
		}
		
		return result;
	}

}
