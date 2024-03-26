package engine.dungeon;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import engine.Entity;
import log.Gamelog;

/**
 * Génie Logiciel - Projet RPG.
 * 
 * Cette classe contient toutes les données relatives à une salle. Cela comprend la liste des entités présentes à la génération de la salle
 * 
 * @author thibault.terrie@etu.cyu.fr
 * @author robin.de-angelis@etu.cyu.fr
 * @author hayder.ur-rehman@etu.cyu.fr
 * 
 */
public class Room {

    private static Logger logger = Gamelog.getLogger();
    
    // définition des attributs
    private Boolean cleaned = false; // booléen pour savoir si la salle a été nettoyée de toute entité hostile
    private Boolean exited = false; // booléen  pour savoir si le joueur a quitté

    private int number;

    private ArrayList<Entity> entities = new ArrayList<Entity>();
    private String fileName = "room";

    public Room (int number) {
        this.number = number;
        logger.trace("New instance of Room");
    }

    public void empty() {
        this.cleaned = false;
        this.exited = false;
        entities.clear();
        fileName = "room";
    }
  
    /**
     * Cette méthode ajoute une entité à la salle
     * 
     * @param entity l'entité à ajouter
     */
    public void addEntity (Entity entity) {
        entities.add(entity);
    }

    public ArrayList<Entity> getEntities() {
        return this.entities;
    }

    /**
     * Cette méthode retire une entité à la liste d'entités de la salle
     * 
     * @param entity l'entité à retirer de la liste
     */
    public void removeEntity(Entity entity) {
        entities.remove(entity);
        logger.trace(entity + " removed");
    }

    /**
     * Cette méthode permet de déclarer la salle comme etant nettoyée (de toute entité hostile)
     */
    public void clean () {
        this.cleaned = true;
        logger.info("room has been marked as cleaned");
        this.fileName = "room_open";
    }

    public Boolean getCleaned () {
        return this.cleaned;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void exit() {
        this.exited = true;
    }

    public Boolean getExited() {
        return exited;
    }

    public int getNumber() {
        return number;
    }
}
