package SNPViewer;
import java.awt.Color;


public class DataPoint {

private final int start;
private final int end;
private final Color color;
private final String data;
public DataPoint(int start, int end, Color color, String data){
	this.start = start;
	this.end = end;
	this.color = color;
	this.data = data;
}

public String getData(){
	return data;
}

public int getStart(){
	return start;
}

public int getEnd(){
	return end;
}

public Color getColor(){
	return color;
}

public int getLength(){
	return end - start + 1;
}

public String toString(){
	return Integer.toString(start) + Integer.toString(end) + color.toString();
}
}
