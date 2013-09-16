import java.util.Vector;



public class Profiles {
	private String name;
	private int score;
	private Vector<String> levels;
	
	public Profiles(String sName,Vector<String> sLevels){
		name = sName;
		levels = sLevels;
	}
	
	public String getName(){
		return name;
	}
	public int getScore(){
		return score;
	}
	
	public Vector<String> getLevels(){
		return levels;
	}
	
	public void addLevel(String newLevel){
		levels.add(newLevel);
	}
}
