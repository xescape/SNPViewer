package SNPViewer;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.TreeMap;

public class Runner {

	public static void main(String[] args) throws IllegalAccessException {

		
		String name = "new_M7741";
//		Path filePath = Paths.get("F:\\Documents\\ProjectData\\64Genomes\\Counting\\pattern.txt");
//		Path filePath = Paths.get("/data/javi/Toxo/64Genomes/Filtered/matrix/composition.sim");
		Path filePath = Paths.get("/data/new/javi/toxo/newdata/nocutoff/simfiles/M7741.sim");
//      Path densityPath = Paths.get("/data/javi/Toxo/BaseData/tempz/matrix/persistentDensity.txt.analyzed");
//		TypeParser tp = new TypeParser(filePath, "topfive");
		

		
//		ClusterPatternParser tp = new ClusterPatternParser(filePath, "default");
		SimilarityParser tp = new SimilarityParser(filePath, "default");
//		TargetedTypeParser tp = new TargetedTypeParser(filePath, "similarity", "3045.");
//		TypeParser dp = new TypeParser(densityPath, "density");
		GenomeProcessor gp = new GenomeProcessor(tp.parseData(), name);
		
		//debug!
		TreeMap<Integer, ArrayList<DataPoint>> data = tp.parseData();
//		System.out.println(data.get(1).size());
//		gp.setLegend(tp.getLegend());
//		gp.setDensity(dp.parseData(), 0);
		gp.generateImage(1);
//		tp.test();
		

		
/*	
		//synteny
		String name = "CHRX";
		Path filePath = Paths.get("/data/new/javi/toxo/newdata/nocutoff/cytoscape/tabNetworkrc.tsv");
		SyntenyParser tp = new SyntenyParser(filePath, "default");
		tp.setChr("X");
		ChromosomeNameConverter cnc = new ChromosomeNameConverter();
		
		TreeMap<Integer, ArrayList<DataPoint>> data = tp.parseData();
		
		
		
		cnc.setAlternateScheme(tp.getChrMap());
		
		System.out.println(cnc.convert(10));
		System.out.println(data.get(10));
		
		GenomeProcessor gp = new GenomeProcessor(data, name, cnc);
		gp.generateImage(1);
*/
	}

}
