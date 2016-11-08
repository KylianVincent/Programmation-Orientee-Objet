package animation;

import java.awt.Color;
import java.util.*;

import gui.*;
import objects.*;
import strategie.*;

import exception.*;

public class Simulateur implements Simulable {
	/** L'interface graphique associée */
	private GUISimulator gui;
	private DonneesSimulation donnees;
	private int lenCaseHeight;
	private int lenCaseWidth;
	private DonneesSimulation donneesBase;

	private long dateSimulation;
	private LinkedList<Evenement> evenements;
	private LinkedList<Evenement> evenementsAAjouter;
	private ChefRobot chef;
	

	public Simulateur(GUISimulator gui, DonneesSimulation donnees) {
		this.gui = gui;
		gui.setSimulable(this); // association a la gui!
		this.donnees = donnees;
		this.lenCaseHeight = (gui.getPanelHeight()-50)/donnees.getCarte().getNbLignes(); //On adapte la tille des cases à la fenêtre
		this.lenCaseWidth = (gui.getPanelWidth()-50)/donnees.getCarte().getNbColonnes();
		this.donneesBase = donnees.copierDonnees();

		this.dateSimulation = 0;
		this.evenements = new LinkedList<Evenement>();
		this.evenementsAAjouter = new LinkedList<Evenement>();	
		this.chef = new ChefRobotSimple(this.donnees, this);

		draw();
	}
	public long getDateSimu(){
		return this.dateSimulation;
	}

	public void ajouteEvenement(Evenement e) {
		ListIterator<Evenement> le = evenementsAAjouter.listIterator();
		while (le.hasNext()) {
			if (le.next().getDate() > e.getDate()) {
				le.previous();
				break;
			}
		}
		le.add(e);
	}

	private void updateEvents() {
		ListIterator<Evenement> le = evenements.listIterator();
		ListIterator<Evenement> leAdd = evenementsAAjouter.listIterator();
		Evenement e = le.hasNext() ? le.next() : null;
		while (leAdd.hasNext()) {
			Evenement eAdd = leAdd.next();
			while (e != null && e.getDate()<eAdd.getDate() && le.hasNext()) {
				e = le.next();
			}
			if (e != null) {
				le.previous();
			}
			le.add(eAdd);
			leAdd.remove();
		}
	}

	public void incrementeDate() {
		dateSimulation++;
	}

	public boolean simulationTerminee() {
		// La complexité de size est en O(1) car attribut.
		if (evenements.size() == 0) {
			return true;
		}
		return false;
	}

	@Override
	public void restart() {
		donnees = donneesBase.copierDonnees();
		evenements.clear();
		evenementsAAjouter.clear();
		dateSimulation = 0;
		// Rappeler la fonction qui calcule tous les déplacements initiaux, ie le chef robot
		
		draw();
	}

	@Override
	public void next() {
		try {
			this.chef.executeStrategie();
			updateEvents();
			if (simulationTerminee()) {
				return;
			}
			ListIterator<Evenement> le = evenements.listIterator();
			Evenement e = le.hasNext() ? le.next() : null;
			while (e != null && e.getDate() <= dateSimulation) {
				if (dateSimulation >= e.getRobot().getDateOccupe()) {
					e.execute();
					System.out.println("execution ok");
				}
				le.remove();
				e = le.hasNext() ? le.next() : null;
			}
			verifIncendies();
			incrementeDate();
			draw();
			
			
		} catch (ExecutionEvenementException e) {
			System.out.println(e);
			evenements.remove();
		}
	}

	private void draw() {
		gui.reset();
		Carte carte = this.donnees.getCarte();
		for (int i = 0; i < carte.getNbLignes(); i++) {
			for (int j = 0; j < carte.getNbColonnes(); j++) {
				NatureTerrain nature = carte.getCase(i, j).getNature();
				Color couleur = Color.BLACK;
				String image="images/";
				if (nature == NatureTerrain.EAU) {
					image += "eau.jpg";
				} else if (nature == NatureTerrain.ROCHE) {
					image += "roche.png";
				} else if (nature == NatureTerrain.FORET) {
					image += "foret.png";
				} else if (nature == NatureTerrain.TERRAIN_LIBRE) {
					image += "herbe.png";
				} else if (nature == NatureTerrain.HABITAT) {
					image += "maison.png";
				}
				gui.addGraphicalElement(new ImageElement((int) ((j+0.5)*lenCaseWidth),
						(int) ((i+0.5)*lenCaseHeight),image,lenCaseWidth, lenCaseHeight ,null));
			}
		}
		drawIncendies();
		drawRobots();
	}

	@Override
	public String toString() {
		String out = "Affichage de la liste triée : ";
		ListIterator<Evenement> le = evenements.listIterator();
		while (le.hasNext()) {
			out += le.next().getDate() + " ";
		}
		return out;
	}

	public void drawRobots() {

		List<objects.Robot> robots = donnees.getRobots();
		Color couleur = Color.decode("#8f8f8f");
		Case position;
		//ImageObserver io = null;

		for (int i = 0; i < robots.size(); i++) {
			position = robots.get(i).getPosition();		
			gui.addGraphicalElement(new ImageElement((int) ((position.getColonne()+0.5) * lenCaseWidth), 
					(int) (( position.getLigne()+0.5) * lenCaseHeight),robots.get(i).getImage(), lenCaseWidth, 
					lenCaseHeight, null));
		}
	}

	public void drawIncendies() {

		List<Incendie> incendies = donnees.getIncendies();
		Color couleur = Color.decode("#DFAF00");
		Case position;

		for (int i = 0; i < incendies.size(); i++) {

			// L'incendie ne doit pas etre dessinne si son intensite est nulle mais
			// la fonction verifIncendies est appellée avant draw(), les incendies nuls sont donc
			// supprimés de la liste
			position = incendies.get(i).getPosition();
			gui.addGraphicalElement(new ImageElement((int) ((position.getColonne() + 0.5) * lenCaseWidth),
					(int) ((position.getLigne() + 0.5) * lenCaseHeight), "images/Fire.gif", lenCaseWidth,
					lenCaseHeight, null));
		}
	}
	
	private void verifIncendies() {
		List<Incendie> incendies = donnees.getIncendies();
		for (int i = 0; i < incendies.size(); i++) {
			if (incendies.get(i).getLitresEau() == 0.0) {
				incendies.remove(i);
			}
		}
	}
}
