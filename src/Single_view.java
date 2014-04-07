/* ############ MatrixViewer ################

Copyright (C) 2003  John Parkinson

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.    */


import java.lang.Math;
import java.net.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.applet.Applet; 
import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.nio.*;

import javax.imageio.ImageIO;


public class Single_view extends Applet 
implements MouseListener,MouseMotionListener,
AdjustmentListener{ 

int Width, Height, counter, text_counter, flag, tmp, centerx, centery, pres_clus_id;
int  url_error=0;
double  oldx, oldy,origx, origy,zoom , cutoff, lcutoff,lc_pos,uc_pos;
URL userUrl;
int[] x= new int[80000];
int[] y= new int[80000];
int[] clusx= new int[1000];
int[] clusy= new int[1000];
int[] blst_score= new int[80000];
char[] b;
String file_line,file;;
String present_clus= new String("");
String[] cluster= new String[1000];
Color[] RectColor= new Color[80000];
Color BackColor = new Color(239,236,254);
private Scrollbar hScroll, vScroll, v1Scroll;

Panel main;

int[] triPointsx= new int[3];
int[] triPointsy= new int[3];

Color[] EColor = new Color[5];

private BufferedImage offScreenImage; 
private Dimension offScreenSize; 
private Graphics offScreenGraphics; 
String name1;
String name2;
String name3;
String filename;

//public void Main(String[] args){
//	init("/data/javi/Toxo/BaseData/tempz/matrix/persistentResult.txt.analyzed");
//}


public void init() 
{
EColor[0]= new Color(200,0,255); 
EColor[1]= new Color(0,0,255); 
EColor[2]= new Color(0,255,0); 
EColor[3]= new Color(230,230,0); 
EColor[4]= new Color(255,0,0); 

triPointsx[0]= 260;
triPointsx[1]= 410;
triPointsx[2]= 560;
triPointsy[0]= 290;
triPointsy[1]= 30;
triPointsy[2]= 290;

flag=1;
Width=1;
zoom=5;
cutoff=50;
lcutoff=10000;
lc_pos=181;
uc_pos=10;

counter=0;
text_counter=0;
origx=410;
origy=180;
oldx=410;
oldy=180;
centerx=410;
centery=180;
//file = this.getParameter("file");
file = "/data/javi/Toxo/BaseData/tempz/matrix/persistentResult.txt.analyzed";
try 
{
// Ditching the stream method in favor of just loading everything
//Because I've got so much memory anyways. 
// FileInputStream fin =  new FileInputStream(file);
// DataInputStream myInput = new DataInputStream(fin);
	
	
//	while ((file_line = myInput.readLine()) != null) 
//	{  readData(file_line);  }
// Finished with the file, so close it.
//	myInput.close() ;
	
	String data = readFile(file, Charset.defaultCharset());
	ArrayList<String> parsedData = parseData(data);
	Iterator<String> tItr = parsedData.iterator();
	while(tItr.hasNext()){
		readData((String)tItr.next());
	}
}

catch (Exception e) 
  {  System.err.println("Caught Exception "+ e.getMessage());  }


hScroll = new Scrollbar(Scrollbar.HORIZONTAL,1,10,0,100);
setLayout(new BorderLayout() );
add("South",hScroll);
hScroll.addAdjustmentListener(this);
addMouseListener(this);
addMouseMotionListener(this);
 
setBackground(Color.white);
repaint();
}

 public void paint(Graphics g) 
 {
  double xcoord, ycoord;
  double[] triPntsx= new double[3];
  double[] triPntsy= new double[3];
  int[] triPx= new int[3];
  int[] triPy= new int[3];
  int squaresize,xc,yc,xoffc,ssize,xsize,uc,lc,xint;
  g.setFont(new Font("Times", Font.PLAIN, 8));

   for (int i = 0; i < counter; i++) 
    {
	g.setColor(Color.white);
	xcoord=x[i]-origx;
    ycoord=y[i]-origy;
    xint=(int)x[i];
    xcoord*=zoom;
    ycoord*=zoom;
    xcoord+=centerx;
    ycoord+=centery;
    xc=(int)(xcoord/2);
    xoffc=(int)xcoord-60;
    yc=(int)ycoord;
    ssize=(int)(4.0*zoom);
    xsize=(int)(2.0*zoom);
    g.setColor(RectColor[i]);
    g.fillRect(xc,yc,xsize,ssize);
    g.drawRect(xc,yc,xsize,ssize);
     
    }

   	g.setColor(Color.black);
   

   for (int i = 0; i < text_counter; i++) 
    {
    xcoord=clusx[i]-origx;
    ycoord=clusy[i]-origy;
    xint=(int)x[i];
    xcoord*=zoom;
    ycoord*=zoom;
    xcoord+=centerx;
    ycoord+=centery;
    xc=(int)(xcoord/2);
    yc=(int)ycoord;
    present_clus=cluster[i];
    g.drawString(present_clus, xc-120, yc+12);
    }
   
/*    if(pres_clus_id >0 && blst_score[pres_clus_id] > cutoff && blst_score[pres_clus_id] < lcutoff)
    { 
    xcoord=x[pres_clus_id]-origx;
    ycoord=y[pres_clus_id]-origy;
    xcoord*=zoom;
    ycoord*=zoom;
    xcoord+=centerx;
    ycoord+=centery;
    g.setColor(RectColor[pres_clus_id]);
    g.fillRect((int)xcoord,(int)ycoord,(int)(4.0*zoom),(int)(4.0*zoom));
    g.setColor(Color.blue);
    g.drawRect((int)xcoord-1,(int)ycoord-1,(int)(4.0*zoom),(int)(4.0*zoom)); 
    }
*/
 } 

public void mouseClicked(MouseEvent e) 
 {
 }

public void mouseReleased(MouseEvent e) 
 {
 }
public void mouseEntered(MouseEvent e) 
 {
 }
public void mouseExited(MouseEvent e) 
 {
 }
public void mouseMoved(MouseEvent e) 
 {
 }

public void mouseDragged(MouseEvent e) 
  {
 int mousex,mousey;
 mousex=e.getX();
 mousey=e.getY();
 if(mousex < 182 && mousex >9 && mousey > 295 && mousey < 305)
  {
  lc_pos = mousex;
  if(lc_pos < 115)   { lcutoff = 50+((lc_pos-10)*50/35); }
  else if(lc_pos > 114 && lc_pos < 150)   { lcutoff = 200+((lc_pos-115)*100/35); }
  else if(lc_pos > 180) { lcutoff=10000; }
  else { lcutoff =  300+((lc_pos-150)*5); }
  repaint();  
  }
 if(mousex < 182 && mousex >9 && mousey > 280 && mousey < 290)
  {
  uc_pos = mousex;
  if(uc_pos < 115)   { cutoff = 50+((uc_pos-10)*50/35); }
  else if(uc_pos > 114 && uc_pos < 150)   { cutoff = 200+((uc_pos-115)*100/35); }
  else { cutoff =  300+((uc_pos-150)*5); }
  repaint();  
  }
 else if(mousex > 200)
  {
  origx=origx-((mousex-oldx)/zoom);
  origy=origy-((mousey-oldy)/zoom);
  repaint();
  oldx=mousex;
  oldy=mousey;  
  }
 }  




public void mousePressed(MouseEvent e) 
 {
 double mousex,mousey;
 String cluster_url;
 cluster_url="http://nema.cap.ed.ac.uk/nematodeESTs/";
  cluster_url+=present_clus;
 try 
  { userUrl = new URL(cluster_url);  }
 catch (Exception ex) 
  {  url_error=1;  }  
 mousex=(double)e.getX();
 mousey=(double)e.getY();
 oldx=mousex;
 oldy=mousey;
 if(e.isControlDown())
  {
  getAppletContext().showDocument(userUrl,"_blank");
  }
  
 else {
  mousex-=centerx;
  mousey-=centery;
  mousex/=zoom;
  mousey/=zoom;
  mousex+=origx;
  mousey+=origy;
  for( int i = 0; i < counter; i++) 
   {
   if((int)mousex >= x[i] && (int)mousey >= y[i] && (int)mousex < x[i]+4 &&
   (int)mousey < y[i]+4)
    { present_clus=cluster[i]; pres_clus_id=i; }    
   }
  repaint();
  }
 }



public void adjustmentValueChanged(AdjustmentEvent e)
 {
 double bar_value;
 //Display the entire AdjustmentEvent object
 if(e.getAdjustable()==hScroll){ 
 bar_value = hScroll.getValue(); 
 zoom=(bar_value/10)-1;
 zoom=Math.pow(1.5,zoom);
 repaint(); }
 }


private void readData(String string) 
 {
 int i, index, len = string.length(), value, colour, zoomv, offset, yoff, yval, xval;
 StringBuffer dest = new StringBuffer(len);
 String data_text= new String();
 String cluster_text= new String();
 char c;
 index=0;
 zoomv=4;
 offset=0;
 yval=0;
 for (i = 0; i < len; i++) 
  {
  c = string.charAt(i);
  if (Character.isLetterOrDigit(c)) { data_text+=c; }
  else if(Character.isWhitespace(c))
   {
   if(index==0)
    {
    cluster_text = data_text;
    data_text="";
    }
   if(index==1)
    {
    value=new Integer(data_text).intValue();
    value*=zoomv;
    y[counter] = value+160;
    yval=y[counter];
    data_text="";
    }
   if(index==2)
    {
    value=new Integer(data_text).intValue();
    xval=value/2;
    x[counter] = value*zoomv;
    if(value == 2)
     {
     clusx[text_counter]=value*zoomv;
     clusy[text_counter]=yval;
     cluster[text_counter]=cluster_text;
     text_counter++;
     }
    data_text="";
    }
   index++;
   }
  } 
 value=new Integer(data_text).intValue();
 blst_score[counter]=value;
 
 //Defines the color scheme used
 if(value == 0) { RectColor[counter] = Color.BLACK; }
 else if(value == 1) { RectColor[counter] = Color.RED; }
 else if(value == 2) { RectColor[counter] = Color.BLUE; }
 else if(value == 3) { RectColor[counter] = Color.GREEN; }
 else if(value == 4) { RectColor[counter] = Color.YELLOW; }
 else if(value == 5) { RectColor[counter] = Color.LIGHT_GRAY; }
 /* else if(value == 6) { RectColor[counter] = new Color(0,250,0); }
 else if(value == 7) { RectColor[counter] = new Color(200,250,0); }
 else if(value == 8) { RectColor[counter] = new Color(250,250,0); }
 else if(value == 9) { RectColor[counter] = new Color(150,170,0); }
 else if(value == 10) { RectColor[counter] = new Color(200,170,0); }
 else if(value == 11) { RectColor[counter] = new Color(250,170,0); }
 else if(value == 12) { RectColor[counter] = new Color(0,250,0); }
 else if(value == 13) { RectColor[counter] = new Color(150,250,0); }
 else if(value == 14) { RectColor[counter] = new Color(200,250,0); }
*/
 else { RectColor[counter] = new Color(0,0,255); }
 counter++;
 }

public final synchronized void update (Graphics g) 
 { 
 try {
 Dimension d = getSize(); 
 if((offScreenImage == null) || (d.width != offScreenSize.width) || (d.height != offScreenSize.height)) 
  { 
//  offScreenImage = createImage(d.width, d.height); 

	 offScreenImage = new BufferedImage(8000, 750, BufferedImage.TYPE_INT_ARGB);
	 offScreenSize = d; 
	 offScreenGraphics = offScreenImage.getGraphics(); 
	 
  
  }
 offScreenGraphics.setColor(Color.white);
 offScreenGraphics.fillRect(0, 0, 8000, 750); 
 paint(offScreenGraphics); 
 g.drawImage(offScreenImage, 0, 0, null);
 
 
 //save to file
 File outputfile = new File("saved.png");
 ImageIO.write(offScreenImage, "png", outputfile);
  }
 catch(Exception ex)
  { present_clus="Crash";   }
 }

private String readFile(String path, Charset encoding) 
		  throws IOException 
		{
		  byte[] encoded = Files.readAllBytes(Paths.get(path));
		  return encoding.decode(ByteBuffer.wrap(encoded)).toString();
		}

private ArrayList<String> parseData(String data){
	
	int chrCount = 0, cluCount = 0, type, typeCount = 1, count = 0;
	String chr, temp;
	
	//Making a table to convert cluster type to a value
	HashMap<String, Integer> clusterTypeMap = getColorIndex(data, "TYPEX");
	
	
	//split the data up into chunks containing one block each
	ArrayList<String> results = new ArrayList<String>();
	Pattern filePattern = Pattern.compile("@([^@]+?)\\n([^@]+?)(?=\\n@)", Pattern.DOTALL);
	Pattern linePattern = Pattern.compile("(\\d+?)\\s-\\s(\\d+?)\\s\\n(.+?)(?=\\n###)", Pattern.DOTALL);
	Matcher lineMatcher;
	Matcher fileMatcher;
	
	fileMatcher = filePattern.matcher(data);
	while(fileMatcher.find()){
		chr = fileMatcher.group(1);
//		System.out.println(fileMatcher.group(2));
		lineMatcher = linePattern.matcher(fileMatcher.group(2));
		while(lineMatcher.find()){			
			//clueterTypeMap really should contain keys for all types,
			//but this is just in case.
			if(clusterTypeMap.containsKey(lineMatcher.group(3))){
//				System.out.println(lineMatcher.group(3));
				type = clusterTypeMap.get(lineMatcher.group(3));
			}
			else type = 0;			
			
			for(int x = Integer.parseInt(lineMatcher.group(1)); x <= Integer.parseInt(lineMatcher.group(2)); x++){
				temp = String.format("%s %s %s %s", chr, chrCount, cluCount, type);
				results.add(temp);
//				System.out.println(temp);
				cluCount++;
			}
		}
		chrCount++;
		cluCount = 0;
	}

	//TODO: First parse the type info. The 5 most common types are listed between the @@[string]-- pattern at the end of the analysis file. Make a map out of it, assign int 1-5 to each type.
	//TODO: Then convert each block into a string identical to taxa2.out. Just follow its pattern and convert each block into multiple lines. 
			

	return results;
}


private HashMap<String, Integer> getColorIndex(String data, String arg){
	Pattern typePattern = Pattern.compile("@@(.*?)--", Pattern.DOTALL);
	Matcher typeMatcher = typePattern.matcher(data);
	
	switch(arg){
	case "TOPFIVE": return topFive(typeMatcher);
	case "TYPEX": return typeX(typeMatcher);
	default: return topFive(typeMatcher);
	}
	
}

//returns a multi-color scheme where areas with type X clustering (at least 3045 and 3142 form a lone cluster) 
//Type 1, 3, and me49 must not be in the cluster
//Shows the top four types as colors 1-5, all other type X as color 5, and all others as type 0
private HashMap<String, Integer> typeX(Matcher typeMatcher){
	int typeCount = 1, limit = 5;
	boolean positive = false, limitReached = false;
	Pattern posPattern = Pattern.compile("3045.*3142");
	Pattern negPattern = Pattern.compile("GT1|VEG|ME49");
	Pattern splitPattern = Pattern.compile("\\n");
	String[] temp;
	HashMap<String, Integer> clusterTypeMap = new HashMap<String, Integer>();
	
	while(typeMatcher.find()){
		temp = splitPattern.split(typeMatcher.group(1));
		for(String s : Arrays.asList(temp)){
			if(posPattern.matcher(s).find() && !negPattern.matcher(s).find()){
				positive = true;
			}
		}
		if(positive){
			clusterTypeMap.put(typeMatcher.group(1), typeCount);
			if(typeCount >= limit) limitReached = true;
			if(limitReached) typeCount = 0;
			else typeCount ++;
		}
		else clusterTypeMap.put(typeMatcher.group(1), 0);
		
		
	}
	System.out.print(clusterTypeMap.toString());
	return clusterTypeMap;
}

//returns a multi-color scheme where the five most common types are colored
//according to the defined scheme in readData
//Top 5 are assigned colors 1 - 5, all others are assigned color 0
private HashMap<String, Integer> topFive(Matcher typeMatcher){
	
	int typeCount = 1;
	int limit = 5;
	boolean limitReached = false;
	HashMap<String, Integer> clusterTypeMap = new HashMap<String, Integer>();
	
	while(typeMatcher.find()){
		clusterTypeMap.put(typeMatcher.group(1), typeCount);
		
		if(typeCount >= limit) limitReached = true;
		
		if(limitReached) typeCount = 0;
		else typeCount++;
		
	}
	System.out.print(clusterTypeMap.toString());
	return clusterTypeMap;
}

}
