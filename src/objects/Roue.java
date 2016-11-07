package objects;

public class Roue extends Robot {
	
	public Roue(Roue roue){
		super(roue);
	}

	public Roue(Case position, Carte carte, double vitesse) {
		super(position, carte, 10, 0, 5000, 20.0, "roue.png"); //10*60
		
		if (vitesse < 0) {
			System.out.println("Vitesse invalide -> vitesse par défault");
			super.vitesse = 80;
		} else {
			super.vitesse = vitesse;
		}
	}

	public double getVitesse(NatureTerrain NT) {
		if (NT == NatureTerrain.TERRAIN_LIBRE || NT == NatureTerrain.HABITAT) {
			return super.vitesse;
		}
		else {
			return -1;
		}
	}
	
	public Robot copierRobot(){
		Roue roue = new Roue(this);
		return roue;
	}

	@Override
	public String toString() {
		return ("Robot à roues " + super.toString() + " \n temps remplissage : " + tempsRemp + " \n volume dispo :"
				+ volEau + " \n sur " + volEauMax + "\n");
	}
}
