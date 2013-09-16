
public class PowerUpObject extends GridGameObject{
	public enum PowerUpType {ammo,shotgun,rocket};
	
	public PowerUpObject (float x, float y, int health,int iCooldown) {
        super (x, y,health,iCooldown);
	}
}
