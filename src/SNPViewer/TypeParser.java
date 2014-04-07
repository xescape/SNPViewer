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


/**
 * 
 * @author javi
 *The type parser is an implementation of the DataParser, which is just a skeleton of how data parsing
 *should work (basically only specifying the output mode and the fact that a legend is needed.
 *The reason such a base class is created is due to the emergence of the similarity parser, which parses
 *data in a fundamentally different way such that either we put it in a whole different class,
 *or we possibly repeat code. It just gets complicated, and similarity parse has little enough to do with
 *the rest of the coloring schemes (requiring a different sort of input file) that it's just not worth it.
 *So, a base class is needed for SimilarityParaser to extend, hence this setup. All methods really relevant
 *to parsing is contained here. 
 *
 *Type parser basically assigns a color to each 'block" in the genome according to its clustering characteristics.
 *This is of course dependent on mcl clustering. There clusters are generated in the python script
 *SNPSorter. We just assign the color here. 
 *
 *The goal is to color each block Logically. The problem here is that similarly looking cluster can have
 *very different implications, while different looking clusters can have similar implications. Hence, 
 *instead of simply coloring based on how they look, we want to color based on what they Mean. This gets
 *into the problem of Context, and hence one of the major problems this program faces: a particular cluster
 *can have different implications in different contexts. Its meaning is Entirely dependent on the question 
 *being asked. Therefore, it's not possible to generate "general overview" graphics - each graph must
 *answer a specific question. That is to say, a graph can tell you the similarity between two particular
 *genomes, but a graph attempting to "get an overview" could be very misleading. could, of course.
 *
 *The mechanics employed here is as follows:
 *The input file specified that all Types of clusters be specified at the end of the file preceded by "@@".
 *This program reads that and assigns a particular color to that pattern/signature based on the current mode.
 *It then iterates through all the data blocks, searches the dictionary for the cluster signature (in this
 *case just the string that describes the cluster), and assigns the color. This information is 
 *stored in a DataPoint object (just a container class) that includes the color, chromosomal position, and
 *the actual signature. DataPoint has the capability to be longer than one block, but we currently aren't
 *using that due to difficulties. Furthermore, DataPoint actually stores the cluster signature, but that feature
 *isn't being used either. More of a.. pre-emptive thing.
 *
 *The DataPoints get put in an array and assigned to an interger in a treemap. This integer represents the chromosome
 *number and can be converted back to the string through ChromosomeNameConverter. Treemap is used because
 *it can sort. Hence the chromosomes can be displayed in numerical order (something that is otherwise 
 *impossible because roman numerals don't sort nicely. Neither do stuff like IA and etc..)
 *
 *This program depends mostly on the persistentResults.txt.analysed filed from SNPSorter. 
 *The names defined here correspond to the strain names in the current experiment and will need to be modified
 *when the new data come in. There probably will be quite a few more variables in the types section once
 *that happens. 
 */
public class TypeParser extends DataParser {

	//defines the "type" of each strain
	//not all strains must have a type. But if they don't, 
	//they are excluded from the color scheme. 
	protected String[] typeX = {"3045.", "3142."};
	protected String[] typeI = {"GT1.S"};
	protected String[] typeII = {"ME49"};
	protected String[] typeIII = {"VEG.S"};
	protected String[][] typeArray = {typeI, typeII, typeIII, typeX};
	protected String[] reference = typeII;
	protected Pattern typePattern = Pattern.compile("@@(.*?)--", Pattern.DOTALL);
	protected Pattern filePattern = Pattern.compile("(?<=^|\\n)@([^@]+?)\\n([^@]+?)(?=\\n@)", Pattern.DOTALL);
	protected Pattern linePattern = Pattern.compile("(\\d+?) - (\\d+?) \\n(.+?)(?=\\n###)", Pattern.DOTALL);
	//Inherited from DataParser. Just reads the file into a giant string.
	//Maybe this isn't the most efficient way but right now I have tons of ram so
	//oh well. 
	public TypeParser(Path source, String mode) {
		super(source, mode);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 * General notes on how to create a new view:
	 * So, views in Typeparser (not necessarily in other subclasses) are based on a HashMap called colorScheme. 
	 * This map relates each cluster signature to a color. and it's the responsibility of the view
	 * to match each and every signature to a color. Thanksfully, all the cluster signatures from
	 * analyzed SNPSorter results are collected at the bottom and tagged with "@@". 
	 * 
	 * So the current idea is to iterate through all the listed cluster signatures and assign them a color
	 * in the ---Scheme methods, and then iterate through every data block in the main parseData() method
	 * to populate the dataTree. which is then returned. 
	 * 
	 * As to how to assign color to cluster signatures, that's the whole problem. There are currently
	 * helper methods to help with the logic. such as checkType(). You'll probably need to write
	 * new methods. 
	 */
	//defines a coloring scheme where type X related blocks are colored and others are not.
	//Uniquely typeX(i.e. checkType(typeX, string, true)) is black, whereas not unique type X
	//(where !checkType(typex, string, true) and checkType(typex, string, false)) is blue.
	protected HashMap<String, Color> typeXScheme(Matcher typeMatcher){
			//If unique type X, black. If non-unique type X, blue. Others, white		
			String temp;
			//debug
			int ccount = 0;
			HashMap<String, Color> clusterTypeMap = new HashMap<String, Color>();
			while(typeMatcher.find()){
				ccount++;
				temp = typeMatcher.group(1);
				if(checkType(new String[][]{typeX}, temp, true)){
					clusterTypeMap.put(temp, Color.BLACK);
				}else if(checkType(new String[][]{typeX}, temp, false)){
					clusterTypeMap.put(temp, Color.BLUE);
				}else {
					clusterTypeMap.put(temp, Color.WHITE);
				}
			}
			
			legend = new HashMap<String, Color>();
			legend.put("type X", Color.BLACK);
			legend.put("non-unique typeX", Color.BLUE);
			legend.put("Others", Color.WHITE);
			
			return clusterTypeMap;
		}

	protected HashMap<String, Color> topFiveScheme(Matcher typeMatcher){
			//This Scheme shows the top 5 most common cluster patterns in the
			//order according to pie chart order of libreoffice, for consistence.. first one
			//is the color of OTHER. 
			Color[] colorList = {Color.WHITE, Color.BLUE, Color.RED, Color.YELLOW, Color.GREEN, new Color(110,0,0), new Color(150,180,240), new Color(0,80,15), new Color(70,190,0), Color.BLACK};
			
			int typeCount = 1;
			int limit = 8;
			boolean limitReached = false;
			HashMap<String, Color> clusterTypeMap = new HashMap<String, Color>();
			legend = new HashMap<String, Color>();
			
			while(typeMatcher.find()){
				Color tmpClr = colorList[typeCount];
				clusterTypeMap.put(typeMatcher.group(1), tmpClr);
				if(!tmpClr.equals(Color.WHITE)) legend.put(typeMatcher.group(1), tmpClr);
				
				if(typeCount >= limit) limitReached = true;
				
				if(limitReached) typeCount = 0;
				else typeCount++;
				
			}
	
			legend.put("Others", Color.WHITE);
			
			return clusterTypeMap;
		}

	protected HashMap<String, Color> defaultScheme(Matcher typeMatcher){
		//TODO: the default scheme shows a few things with preset colors:
		//Type 1 - RED
		//Type 2 - BLUE
		//Type 3 - Yellow
		//Type X - BLACK
		//Everything else - WHITE
		//Preference hierarchy goes I-III-X-II-Others
		HashMap<String[], Color> colorMap = new HashMap<String[], Color>();
		colorMap.put(typeI, Color.RED);
		colorMap.put(typeIII, Color.YELLOW);
		colorMap.put(typeX, Color.BLACK);
		colorMap.put(typeII, Color.BLUE);
		List<String[]> orderList = new ArrayList<String[]>();
		orderList.add(typeI);
		orderList.add(typeIII);
		orderList.add(typeX);
		orderList.add(typeII);
	
		
		//other stuff = white
		
		HashMap<String, Color> clusterTypeMap = new HashMap<String, Color>();
		String temp;
		while(typeMatcher.find()){
			temp = typeMatcher.group(1);
			outer:
			for(String[] ct : orderList){
				if(checkType(new String[][]{ct}, temp, false)){
					clusterTypeMap.put(temp, colorMap.get(ct));
					break outer;
				}
			}
			if(!clusterTypeMap.containsKey(temp)) clusterTypeMap.put(temp, Color.WHITE);
		}
	
		legend = new HashMap<String, Color>();
		legend.put("type I", Color.RED);
		legend.put("type III", Color.YELLOW);
		legend.put("type X", Color.BLACK);
		legend.put("Others", Color.WHITE);
		
		return clusterTypeMap;
	}

	protected HashMap<String, Color> crossScheme(Matcher typeMatcher){
		/**
		 * TODO: This scheme is designed to highlight areas were type I, III, and X 
		 * cluster together. This way, we can have some rudimentary view of whether it's
		 * possible to relate their linages and find any crossover points. Colors:
		 * Type I Unique: RED
		 * Type III Unique: BLUE
		 * Type X Unique GREEN
		 * Type I-X cross: YELLOW
		 * Type III-X cross: PURPLE
		 * Others: WHITE
		 */
		ArrayList<String[][]> orderList = new ArrayList<String[][]>();
		
		orderList.add(new String[][]{typeI, typeIII, typeX});
		orderList.add(new String[][]{typeIII, typeX});
		orderList.add(new String[][]{typeI, typeX});
		orderList.add(new String[][]{typeX});
		orderList.add(new String[][]{typeIII});
		orderList.add(new String[][]{typeI});
		
		HashMap<String[][], Color> colorMap = new HashMap<String[][], Color>();
		colorMap.put(orderList.get(5), Color.RED);
		colorMap.put(orderList.get(4), Color.BLUE);
		colorMap.put(orderList.get(3), Color.GREEN);
		colorMap.put(orderList.get(2), Color.YELLOW);
		colorMap.put(orderList.get(1), Color.CYAN);
		colorMap.put(orderList.get(0), Color.BLACK);
		
	
	
		//other stuff = white
	
		HashMap<String, Color> clusterTypeMap = new HashMap<String, Color>();
		String temp;
		while(typeMatcher.find()){
			temp = typeMatcher.group(1);
			outer:
				for(String[][] ct : orderList){
					if(checkType(ct, temp, true)){
						clusterTypeMap.put(temp, colorMap.get(ct));
						break outer;
					}
				}
			if(!clusterTypeMap.containsKey(temp)) clusterTypeMap.put(temp, Color.WHITE);
		}
	
		legend = new HashMap<String, Color>();
		legend.put("type I", Color.RED);
		legend.put("type III", Color.BLUE);
		legend.put("type X", Color.GREEN);
		legend.put("I-X", Color.YELLOW);
		legend.put("III-X", Color.CYAN);
		legend.put("I-III-X", Color.BLACK);
		legend.put("Others", Color.WHITE);
	
		return clusterTypeMap;
	}

	protected HashMap<String, Color> overlapScheme(Matcher typeMatcher){
		/**
		 * TODO: This scheme is designed to highlight areas were type I, III, and X 
		 * cluster together. This way, we can have some rudimentary view of whether it's
		 * possible to relate their linages and find any crossover points. Colors:
		 * Type I Unique: RED
		 * Type III Unique: BLUE
		 * Type X Unique GREEN
		 * Type I-X cross: YELLOW
		 * Type III-X cross: PURPLE
		 * Others: WHITE
		 */
		ArrayList<String[][]> orderList = new ArrayList<String[][]>();
		
		orderList.add(new String[][]{typeI, typeIII, typeX});
		orderList.add(new String[][]{typeIII, typeX});
		orderList.add(new String[][]{typeI, typeX});
		orderList.add(new String[][]{typeX});
		orderList.add(new String[][]{typeIII});
		orderList.add(new String[][]{typeI});
		
		HashMap<String[][], Color> colorMap = new HashMap<String[][], Color>();
		colorMap.put(orderList.get(5), Color.RED);
		colorMap.put(orderList.get(4), Color.BLUE);
		colorMap.put(orderList.get(3), Color.GREEN);
		colorMap.put(orderList.get(2), Color.YELLOW);
		colorMap.put(orderList.get(1), Color.CYAN);
		colorMap.put(orderList.get(0), Color.BLACK);
	
	
	
		//other stuff = white
	
		HashMap<String, Color> clusterTypeMap = new HashMap<String, Color>();
		String temp;
		while(typeMatcher.find()){
			temp = typeMatcher.group(1);
			outer:
				for(String[][] ct : orderList){
					if(checkAllTypes(ct, temp, true)){
						clusterTypeMap.put(temp, colorMap.get(ct));
						break outer;
					}
				}
			if(!clusterTypeMap.containsKey(temp)) clusterTypeMap.put(temp, Color.WHITE);
		}
	
		legend = new HashMap<String, Color>();
		legend.put("type I", Color.RED);
		legend.put("type III", Color.BLUE);
		legend.put("type X", Color.GREEN);
		legend.put("I-X", Color.YELLOW);
		legend.put("III-X", Color.CYAN);
		legend.put("I-III-X", Color.BLACK);
		legend.put("Others", Color.WHITE);
	
		return clusterTypeMap;
	}
	
	protected HashMap<String, Color> relationScheme(Matcher typeMatcher){
		/**
		 * TODO: This scheme checks for when are two types related. Maybe I ought to filter
		 * for "biallelic" things first. 
		 * I-X: BLUE
		 * III-X: RED
		 * II-X: YELLOW
		 * Others: WHITE
		 */
		ArrayList<String[][]> orderList = new ArrayList<String[][]>();
		
		orderList.add(new String[][]{typeI, typeIII, typeX});
		orderList.add(new String[][]{typeIII, typeX});
		orderList.add(new String[][]{typeI, typeX});
		orderList.add(new String[][]{typeII, typeX});
//		orderList.add(new String[][]{typeIII});
//		orderList.add(new String[][]{typeI});
		
		HashMap<String[][], Color> colorMap = new HashMap<String[][], Color>();
//		colorMap.put(orderList.get(5), Color.RED);
//		colorMap.put(orderList.get(4), Color.BLUE);
		colorMap.put(orderList.get(3), Color.BLUE);
		colorMap.put(orderList.get(2), Color.RED);
		colorMap.put(orderList.get(1), Color.GREEN);
		colorMap.put(orderList.get(0), Color.BLACK);
	
	
	
		//other stuff = white
	
		HashMap<String, Color> clusterTypeMap = new HashMap<String, Color>();
		String temp;
		while(typeMatcher.find()){
			temp = typeMatcher.group(1);
			outer:
				for(String[][] ct : orderList){
					if(checkType(ct, temp, false)){
						clusterTypeMap.put(temp, colorMap.get(ct));
						break outer;
					}
				}
			if(!clusterTypeMap.containsKey(temp)) clusterTypeMap.put(temp, Color.WHITE);
		}
	
		legend = new HashMap<String, Color>();
//		legend.put("type I", Color.RED);
//		legend.put("type III", Color.BLUE);
		legend.put("II-X", Color.BLUE);
		legend.put("I-X", Color.RED);
		legend.put("III-X", Color.GREEN);
		legend.put("I-III-X", Color.BLACK);
		legend.put("Others", Color.WHITE);
	
		return clusterTypeMap;
	}
	
	protected HashMap<String, Color> densityScheme(Matcher typeMatcher){
		/**
		 * TODO: This view shows the SNP density. Any region that is equal or greater than the median of the whole
		 * genome is black, which stands for complete coverage. Anything below is colored on a linear grayscale
		 * from black to white (0 snps)
		 */
	
		HashMap<String, Color> clusterTypeMap = new HashMap<String, Color>();
		int temp;
		int median = 0;
		int max = 0;
		float ZERO = (float) 0;
		while(typeMatcher.find()){
			temp = Integer.parseInt(typeMatcher.group(1));
			if(temp > max) max = temp;
			if(median == 0) median = temp;
		}
		
		for(int x = 0; x<=max; x++){
			if(x>=median){
				clusterTypeMap.put(Integer.toString(x), new Color(ZERO, ZERO, ZERO));
			}
			else{
				float cn = (1 - ((float)x/median));
				clusterTypeMap.put(Integer.toString(x), new Color(cn, cn, cn));
			}
			
		}
		return clusterTypeMap;
	}

	
	/**
	 * 
	 * @param types
	 * @param clusterType
	 * @param unique
	 * @return
	 * 
	 * selecting True for this method causes it to check that All the things are uniquely clustered. 
	 */
	protected boolean checkAllTypes(String[][] types, String clusterType, boolean unique){
		//use this method to check types in parallel. Useful for the overlap view. 
		
		for(String[] t : Arrays.asList(types)){
			if(!checkType(new String[][]{t}, clusterType, unique)) return false;
		}
		return true;
	}

	//This is the user accessible method. Task is then delegated to checklinetype.
	protected boolean checkType(String[][] types, String clusterType, boolean unique){
		List<String[]> typesList = Arrays.asList(types);
		Pattern splitPattern = Pattern.compile("\n");
		for(String s:Arrays.asList(splitPattern.split(clusterType))){
			if(checkLineType(typesList, s, unique)) return true;
		}
		return false;
	}

	//	protected boolean isType(List<String[]> types, String clusterType, boolean unique){
	//		
	//		if(types.size() > 1){				
	//			if(isType(types.subList(1, types.size() -1), clusterType, unique) && isType(types.get(0), clusterType, unique)) return true;
	//			else return false;
	//		}
	//		else return isType(types.get(0), clusterType, unique);
	//		
	//	}
		
		protected boolean checkLineType(List<String[]> type, String clusterType, boolean unique){
	//	clusterType represents just one line
			Pattern splitPattern = Pattern.compile("\\t");
			List<String> eList = Arrays.asList(splitPattern.split(clusterType));
			
			if(clusterType.contains(reference[0]) && !type.contains(reference)) return false;
			
			if(unique){
				ArrayList<String> allOtherTypes = new ArrayList<String>();
				for (String[] sa : Arrays.asList(typeArray)){
					if(!type.contains(sa)) allOtherTypes.addAll(Arrays.asList(sa));
				}
				if(containsAny(eList, allOtherTypes)) return false;
			}
			
			List<List<String>> allowedElements = new ArrayList<List<String>>();
			for(String[] sa : type){
				allowedElements.add(Arrays.asList(sa));
			}
	
			if(containsAll(allowedElements, eList)) return true;
			
			return false;
		}


		protected void setColorScheme(String schemeName, String info){
			
			Matcher typeMatcher = typePattern.matcher(info);
			
			System.out.println("setting color scheme to" + schemeName);
			switch(schemeName.toUpperCase()){
			case "TYPEX": colorScheme = typeXScheme(typeMatcher);
				break;
			case "TOPFIVE": colorScheme = topFiveScheme(typeMatcher);
				break;
			case "CROSS": colorScheme = crossScheme(typeMatcher);
				break;
			case "OVERLAP": colorScheme = overlapScheme(typeMatcher);
				break;
			case "DENSITY": colorScheme = densityScheme(typeMatcher);
				break;
			case "RELATION": colorScheme = relationScheme(typeMatcher);
				break;
			case "DEFAULT": colorScheme =  defaultScheme(typeMatcher);
				break;
			}	
		}

		public void test(){
				//A method used for testing purposes because I'm too lazy to write unit tests
				System.out.println("Testing Phase..");
				String z = "ME49\tGT1\t3045\nVEG";
				String x = "3142\t3045\tGT1";
				String y = "3142.\tME49\tARI.V\nP89.S\tVEG.S\tGT1.S\n3045.\tTGSKN";
				
				String s = "3045.\t3142.\tARI.V\nME49\tTGSKN\tGT1.S\nP89.S\tVEG.S";
				
				List a = new ArrayList<String>();
				
		//		colorScheme.put(s, Color.YELLOW);
				System.out.println(checkType(new String[][]{typeII, typeI}, z, false));
				System.out.println(checkType(new String[][]{typeII, typeI}, z, true));
				System.out.println(checkType(new String[][]{typeX, typeI}, x, false));
				System.out.println(checkType(new String[][]{typeX, typeI}, x, true));
				System.out.println(checkType(new String[][]{typeX, typeI}, y, false));
				System.out.println(checkType(new String[][]{typeX, typeI}, y, true));
				System.out.println(colorScheme.get(y));
				System.out.println("done.");
			}

		public TreeMap<Integer, ArrayList<DataPoint>> parseData(){
				
				int chrNum, chrCount = 0, cluCount = 0, type, typeCount = 1, count = 0;
				String chr, temp, cluType;
				
				//Making a table to convert cluster type to a value
				setColorScheme(mode, rawData);
				
				
				//split the data up into chunks containing one block each
				ArrayList<String> results = new ArrayList<String>();
				
				//For filePattern and fileMatcher:
				//group(1) = chromosome name
				//group(2) = the information pertaining to that chromosome
				//each block represents one chromosome
				
				//For linePattern and lineMatcher
				//group(1) represents the starting position
				//group(2) represents the ending position
				//group(3) represents the cluster pattern
				ChromosomeNameConverter cnc = new ChromosomeNameConverter();
				Matcher lineMatcher;
				Matcher fileMatcher;
				TreeMap<Integer, ArrayList<DataPoint>> dataTree = new TreeMap<Integer, ArrayList<DataPoint>>();
				ArrayList<DataPoint> chrBranch;
		//		GenomeImageGenerator gi;
				
				
				
				fileMatcher = filePattern.matcher(rawData);
				while(fileMatcher.find()){
					chr = fileMatcher.group(1);
					chrBranch = new ArrayList<DataPoint>();
					dataTree.put(cnc.convert(chr), chrBranch);
					lineMatcher = linePattern.matcher(fileMatcher.group(2));
					while(lineMatcher.find()){	
						//colorScheme really should contain keys for all types,
						//but this is just in case.	
						//debug
						String zz = lineMatcher.group(3);
						if(colorScheme.containsKey(zz) == false){
							System.out.println("missing:" + zz);
						}
						int start = Integer.parseInt(lineMatcher.group(1));
						int end = Integer.parseInt(lineMatcher.group(2));
						Color c = colorScheme.get(lineMatcher.group(3));
						String info = lineMatcher.group(3);
						for(int x = start; x <= end; x++){
							chrBranch.add(new DataPoint(x, x, c, info));
						}
					}
				}
				return dataTree;
			}

		protected Pattern getLineParsePattern(){
			return Pattern.compile("linepattern");
		}

		protected Pattern getFileParsePattern(){
			return Pattern.compile("filepattern");
		}

		protected boolean containsAll(List<List<String>> a, List<String> b){
			//specialized method! checks if all lists in a has at least one correspondend in list b
			//same with containsAny, has provisions one being the short form of the other
			//i.e. a from one list and aa from the other will count as contains!
			if(a.size() > 1){
				return containsAll(a.subList(0, 1), b) && containsAll(a.subList(1, a.size()), b);
			}
			else{
				return containsAny(a.get(0), b);
			}
			
		}

		protected boolean containsAny(List<String> a, List<String> b){
			//checks if List A contains all elements of list b
			if(b.size() > 1){
				return containsAny(a, b.subList(0, 1)) || containsAny(a, b.subList(1, b.size()));
			}
			else{
				for(String s : a){
					if(s.contains(b.get(0))) return true;
					if(b.get(0).contains(s)) return true;
				}
				return false;
			}
			
		}

}
