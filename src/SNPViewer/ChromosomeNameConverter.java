package SNPViewer;
import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
public class ChromosomeNameConverter {
	private HashMap<String, Integer> chrMap;
	private HashMap<Integer, String> invMap;
	
	public ChromosomeNameConverter(){
		
		chrMap = new HashMap<String, Integer>();
		invMap = new HashMap<Integer, String>();
		
		
		chrMap.put("TGME49_CHRIA", 0);
		chrMap.put("TGME49_CHRIB", 1);
		chrMap.put("TGME49_CHRII", 2);
		chrMap.put("TGME49_CHRIII", 3);
		chrMap.put("TGME49_CHRIV", 4);
		chrMap.put("TGME49_CHRV", 5);
		chrMap.put("TGME49_CHRVI", 6);
		chrMap.put("TGME49_CHRVIIA", 7);
		chrMap.put("TGME49_CHRVIIB", 8);
		chrMap.put("TGME49_CHRVIII", 9);
		chrMap.put("TGME49_CHRIX", 10);
		chrMap.put("TGME49_CHRX", 11);
		chrMap.put("TGME49_CHRXI", 12);
		chrMap.put("TGME49_CHRXII", 13);
		
		for(Map.Entry<String, Integer> entry : chrMap.entrySet()){
			invMap.put(entry.getValue(), entry.getKey());
		}
	}
	
	public int convert(String chrName){
		return chrMap.get(chrName.toUpperCase());
	}
	
	public String convert(int chrNum){
		return invMap.get(chrNum);
	}
	
	public int getChrNumber(){
		return chrMap.size();
	}
	
	public void setAlternateScheme(HashMap<String, Integer> scheme){
		this.chrMap = scheme;
		this.invMap = new HashMap<Integer, String>();
		for(Map.Entry<String, Integer> entry : this.chrMap.entrySet()){
			this.invMap.put(entry.getValue(), entry.getKey());
		}
	}
}
