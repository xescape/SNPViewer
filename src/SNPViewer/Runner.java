package SNPViewer;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Runner {

	public static void main(String[] args) throws IllegalAccessException {
		// TODO Auto-generated method stub
		String name = "IntactClusters";
		Path filePath = Paths.get("/data/javi/Toxo/64Genomes/Counting/intact.sim");
//        Path densityPath = Paths.get("/data/javi/Toxo/64Genomes/Counting");
//		TypeParser tp = new TypeParser(filePath, "topfive");
		

		
		SimilarityParser tp = new SimilarityParser(filePath, "default");
//		TargetedTypeParser tp = new TargetedTypeParser(filePath, "similarity", "3045.");
		
//		TypeParser dp = new TypeParser(densityPath, "density");
		GenomeProcessor gp = new GenomeProcessor(tp.parseData(), name);
		
		//debug!
		System.out.println(tp.parseData());
		gp.setLegend(tp.getLegend());
//		gp.setDensity(dp.parseData(), 20);
		gp.generateImage(1);
//		tp.test();
	}

}
