
public class EnemyStructure extends EnemyObject{
	public enum StructureType {ATTACK,PASSIVE};	
	private StructureType movementType;
	
	public EnemyStructure(float x, float y,int ihealth,int iCooldown,StructureType t) {
		super(x,y,ihealth,iCooldown);		
		
		movementType = t;
	}
	
	public StructureType getStructure(){
		return movementType;
	}	
	
}
