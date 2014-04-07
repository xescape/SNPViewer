package SNPViewer;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.awt.FontMetrics;
import java.awt.Font;


public abstract class DataParser {

protected String rawData;
protected String mode;
protected HashMap<String, Color> legend;
protected HashMap<String, Color> colorScheme;
	
	public DataParser(Path source, String mode){
		try {
			rawData = new String(Files.readAllBytes(source));
			this.mode = mode;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public HashMap<String, Color> getLegend(){
		return legend;
	}
	
	public abstract TreeMap<Integer, ArrayList<DataPoint>> parseData();
	
}
