package SNPViewer;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.imageio.ImageIO;

/*
 * This generator, along with dataParser, aims to replace the Single_View applet. It is responsible for converting 
 * datapoints into rectangles with appropriate color and location. Each new data point is received via the 
 * drawNext(Color c) method, specifying the color. The "print head", represented by
 * xCoord and yCoord, will automatically move to the next available location and
 * draw a 20x100 rectangle. This this, as well as the size of the image, can be altered by the
 * magnification variable set at construction time. 
 * New chromomes are specified by the newChromosome(String name) method, where the print head goes back to
 * (0, next line), prints the name, and standsby to receive the first data point for that chr. 
 * Images are saved as a PNG file.
 * 
 * v2:
 * Some changes in the design. The image generator now Must be extended to a processing class. This allows the
 * generator to be reused for different pre-processing requirements (such as smoothing, etc.) 
 * The methods defined here will be used by the children classes that have the appropriate processing methods.
 * Most things are intact, but the image parameters such as height etc are now to be defined in the child class. 
 */
public abstract class GenomeImageGenerator {

//Some attributes of the image. Image itself constructed in constructor.
protected int width;
protected int rectWidth;
protected int height;
protected int rectHeight;
protected int lineHeight;
protected int textSpaceWidth;
protected int mag;
protected String name;
protected BufferedImage image;
protected Graphics graphics;
protected HashMap<String, Color> legend;

protected final int BASE_RECT_HEIGHT = 100;
protected final int BASE_RECT_WIDTH = 20;
protected final int TEXT_Y_OFFSET = 50;

//These two numbers act as the "print head", determining where then next element is to be drawn.
protected int xCoord;
protected int yCoord;



protected void init(){
	try{
		rectWidth =  BASE_RECT_WIDTH * mag;
		rectHeight = BASE_RECT_HEIGHT * mag;
		lineHeight = rectHeight * 2;
		
		preprocess();
		
		image = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
		graphics = image.getGraphics();
		graphics.setColor(Color.WHITE);
		graphics.fillRect(0, 0, width, height);
		xCoord = 100 * mag;
		yCoord = 100 * mag;
	}catch(Exception e){
		System.out.println("initialization error");
		e.printStackTrace();
	}
	}
	
	protected void drawNext(Color c){
		//TODO draws a box of 20x100 of a specified color representing the next gene block.
		graphics.setColor(c);
		graphics.fillRect(xCoord, yCoord, rectWidth, rectHeight);
		xCoord += rectWidth;
	}
	
	protected void drawNext(Color c, int i){
		for(int x = 0; x < i; x++){
			drawNext(c);
		}
	}
	
	protected void newChromosome(String chrName){
		//TODO starts a new line representing a new chromsome, and writes the name of the new chr.
		xCoord = 100 * mag;
		yCoord += lineHeight;
		graphics.setColor(Color.BLACK);
		graphics.drawString(chrName, xCoord, yCoord + TEXT_Y_OFFSET);
		xCoord += textSpaceWidth;
	}
	
	protected void finishLine(){
		//TODO clean-up method to complete the current line. 
		//so far the only planned action is to draw a border around the finished chromosome.
		graphics.setColor(Color.BLACK);
		int xStart = 100 * mag + textSpaceWidth;
		graphics.drawRect(xStart, yCoord, xCoord - xStart, rectHeight);
	}
	
	protected void saveImage(){
		try {
			System.out.println("saving image...");
			graphics.drawImage(image, 0, 0, null);
			ImageIO.write(image, "png", new File(name + ".png"));
			System.out.println("done");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	protected void drawLegend(){
		newChromosome("legend");
		FontMetrics fm = graphics.getFontMetrics();
		for(Entry<String, Color> entry : legend.entrySet()){
			graphics.setColor(Color.BLACK);
			xCoord += 10;
			graphics.drawString(entry.getKey(), xCoord, yCoord + TEXT_Y_OFFSET/3);
			xCoord += fm.stringWidth(entry.getKey()) + 20;
			drawNext(entry.getValue());
			xCoord += 50;
		}
	}
	
	public void setLegend(HashMap<String, Color> legend){
		this.legend = legend;
	}
	
	public void generateImage(int magnification){
		
	}
	
	public void preprocess(){
		
	}
	
}
