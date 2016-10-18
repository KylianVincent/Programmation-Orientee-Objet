package objects;

public class Patte extends Robot {

	public Patte(Case position) {

		super(position);
		this.vitesse = 30;
		this.vitDever = 10.0;
	}

	public double getVitesse(NatureTerrain NT) {
		if (NT == NatureTerrain.ROCHE) {
			return super.vitesse - 10;
		}
		return super.vitesse;
	}
	
	@Override
	public String toString() {
		return ("Robot à pattes " + super.toString() + "\n");
	}
}