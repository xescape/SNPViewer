package SNPViewer;

import java.awt.Color;
import java.nio.file.Path;
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
	protected DataPoint parseMatrix(String matrix, int position ){
		Pattern linePattern = Pattern.compile("^(.+?) - (.+?)[:] ([0-9.]+?)(?=\\n|$)", Pattern.MULTILINE);
		Matcher lineMatcher = linePattern.matcher(matrix);
		int info;
		lineMatcher.find();
		info = Integer.parseInt(lineMatcher.group(3));
		System.out.println(new Color(info));
		return new DataPoint(position, position, new Color(info), matrix);
	}

}
