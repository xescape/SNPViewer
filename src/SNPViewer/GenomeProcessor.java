package SNPViewer;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
public class GenomeProcessor extends GenomeImageGenerator {

ChromosomeNameConverter cnc = new ChromosomeNameConverter();

	private TreeMap<Integer, ArrayList<DataPoint>> data;
	private TreeMap<Integer, ArrayList<DataPoint>> density;
	private boolean hasDensity = false;
	private int minDensity = 0;
	
	public GenomeProcessor(){
		super();
	}
	
	public GenomeProcessor(TreeMap<Integer, ArrayList<DataPoint>> data, String name){
		this.data = (TreeMap<Integer, ArrayList<DataPoint>>) data.clone();
		this.name = name;
		height = 0;
		textSpaceWidth = 0;
		width = 0;	
	}
	
	public GenomeProcessor(TreeMap<Integer, ArrayList<DataPoint>> data, String name, ChromosomeNameConverter cnc){
		this.data = (TreeMap<Integer, ArrayList<DataPoint>>) data.clone();
		this.name = name;
		height = 0;
		textSpaceWidth = 0;
		width = 0;
		this.cnc = cnc;
	}
	
	public void setDensity(TreeMap<Integer, ArrayList<DataPoint>> data, int min){
		//This allows us to plot the snp density on top of the chromosomes. First we set the data, then we add to the other method. 
		density = data;
		hasDensity = true;
		minDensity = min;
	}
	
	public void filterDensity(int min){

		for(Entry<Integer, ArrayList<DataPoint>> entry: data.entrySet()){
			ArrayList<DataPoint> chr = entry.getValue();
			ArrayList<DataPoint> chrDensity = density.get(entry.getKey());
			for(int x = 0; x<chr.size(); x++){
				DataPoint currPoint = chr.get(x);
				if(Integer.parseInt(chrDensity.get(x).getData()) < min) chr.set(x, new DataPoint(currPoint.getStart(), currPoint.getEnd(), Color.WHITE, currPoint.getData()));
			}
		}
	}
	
	@Override
	public void preprocess(){
		int tmpWidth = 0;
		int tmpHeight = 0;
		Image tmpImage = new BufferedImage(1, 1, 1);
		Graphics tmpGraphics = tmpImage.getGraphics();
		tmpGraphics.setFont(new Font("TimesNew", Font.PLAIN, 100));
		FontMetrics tfm = tmpGraphics.getFontMetrics();
		for(Entry<Integer, ArrayList<DataPoint>> entry : data.entrySet()){
			ArrayList<DataPoint> chr = entry.getValue();
			int tmpSize = chr.get(chr.size() -1).getEnd();
			if(tmpSize > tmpWidth) tmpWidth = tmpSize;
			
			int tmpTxtSize = tfm.stringWidth(cnc.convert(entry.getKey()));
			if(tmpTxtSize > textSpaceWidth) textSpaceWidth = tmpTxtSize;
			
			tmpHeight += lineHeight;
		}
		width = tmpWidth * rectWidth + textSpaceWidth + 200;
		height = tmpHeight + lineHeight + 200;
		
		if(legend != null){
			tmpWidth = 0;
			tmpHeight =  lineHeight + 200;
			for(Entry<String, Color> entry : legend.entrySet()){
				tmpWidth += tfm.stringWidth(entry.getKey());
				tmpWidth += rectWidth + 100;
			}
			if(tmpWidth > width) width = tmpWidth;
			height += tmpHeight; 
		}
		
		if(hasDensity) filterDensity(minDensity);
	}
	
	@Override
	public void generateImage(int magnification){
		mag = magnification;
		init();
		paint();
		
	}
	
	protected void paint(){
		for(Entry<Integer, ArrayList<DataPoint>> entry : data.entrySet()){
			ArrayList<DataPoint> chr = entry.getValue();
			newChromosome(cnc.convert(entry.getKey()));
			for(DataPoint dp : chr){
				drawNext(dp.getColor(), dp.getLength());				
			}
			finishLine();
			
			if(hasDensity){
				ArrayList<DataPoint> chrDensity = density.get(entry.getKey());
				densityLine(chrDensity);	
			}	
		}

//		drawLegend();
		saveImage();
	}
	
	protected void densityLine(ArrayList<DataPoint> chrDensity){
		xCoord = (100 + textSpaceWidth) * mag;
		int oldy = yCoord;
		yCoord += rectHeight + 10;
		
		for(DataPoint dp : chrDensity){
			drawDensity(dp.getColor(), dp.getLength());	
		}
		
		yCoord = oldy;
	}
	
	protected void drawDensity(Color c){
		//TODO draws a box of 20x100 of a specified color representing the next gene block.
		graphics.setColor(c);
		graphics.fillRect(xCoord, yCoord, rectWidth, rectHeight/3);
		xCoord += rectWidth;
	}
	
	protected void drawDensity(Color c, int i){
		for(int x = 0; x < i; x++){
			drawDensity(c);
		}
	}
	
}
