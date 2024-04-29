package engine.process.management;

import org.apache.log4j.Logger;

import config.GameConfiguration;
import engine.dungeon.Position;
import engine.dungeon.Room;
import engine.entities.Entity;
import engine.entities.characters.Enemy;
import engine.entities.characters.Player;
import engine.entities.containers.Chest;
import engine.entities.containers.Garbage;
import engine.entities.environment.Environment;
import engine.entities.items.Key;
import engine.entities.items.consumables.*;
import engine.entities.items.equipment.*;
import engine.entities.npc.Vendor;
import engine.process.factories.*;
import engine.dungeon.Dungeon;
import log.Gamelog;

/**
 * Génie Logiciel - Projet RPG.
 * 
 * Cette classe gère la génération du jeu (salle et entités)
 * 
 * @author thibault.terrie@etu.cyu.fr
 * @author robin.de-angelis@etu.cyu.fr
 * @author hayder.ur-rehman@etu.cyu.fr
 * 
 */
public class GameBuilder {

    private static Logger logger = Gamelog.getLogger();
    private static EntityManager manager = EntityManager.getInstance();

    public static Dungeon buildDungeon() {
        logger.trace("New instance of Dungeon sent");
        return new Dungeon();
    }

    /**
     * Génère la salle et les ennemis à l'apparition de la salle en utilisant le constructeur de la classe {@link src.engine.characters.manager}
     * 
     * @param room la salle dans laquelle évolue le joueur
     * @return le système de gestion de la partie. Pour plus de détails, voir {@link src.engine.characters.manager}
     */
    public static void buildInitEntities (Dungeon dungeon) {
        manager.setDungeon(dungeon);
        logger.trace("New instance of manager");

        initializePlayer();
        logger.trace("Initialized player");

        initializeEntities();
        logger.trace("Initialized entities");
    }

    /**
     * Cette méthode permet d'initialiser le joueur au centre de la salle
     * 
     * @param manager le process de gestion des actions, auquel sera ajouté le joueur généré
     * 
     * @see engine.process.management.EntityManager pour les détails du processus de gestion des actions du joueur
     */
    private static void initializePlayer () {
        // On récupère l'instance de Player
        Player player = Player.getInstance();
        // On ajoute le joueur à la liste d'entités
        manager.getCurrentRoom().addEntity(player);
    }

    /**
     * Permet l'initialisation de toutes les entités de la salle actuelle, l'ordre de génération est important
     * @param manager
     */
    public static void initializeEntities() {

        Room currentRoom = manager.getCurrentRoom();

        initializeEnvironment();
        logger.trace("Initialized environment");

        if(currentRoom.isShop()) {
            // On initialise le vendeur
            initializeVendor();
            // On ouvre directement la porte
            manager.getCurrentRoom().open();
            logger.trace("Initialized vendor");
        }
        else if(currentRoom.isBoss()) {
            initializeBoss();
        }
        else {
            initializeEnemies();
            logger.trace("Initialized ennemies");
        }

    }

    /**
     * Cette méthode permet d'initialiser les ennemis à des endroits aléatoires de la salle.
     * 
     * @param manager le process de gestion des actions auquel seront ajoutés les ennemis générés.
     * 
     * @see engine.process.management.manager pour les détails du processus de gestion des ennemis
     */
    public static void initializeEnemies() {
        Player player = Player.getInstance();
        int stageNumber = player.getStageNumber();
        int enemyCount = player.getStageNumber() * player.getRoomNumber(); 
        for (int i = 0; i < enemyCount; i++) {
            int randomNumber = getRandomNumber(1, 2);
            String enemyType = "";
            if(stageNumber == 1) {
                if(randomNumber == 1) {
                    enemyType = "rat_fistule";
                }
                else {
                    enemyType = "rocky_blateboa";
                }
            }
            else if(stageNumber == 2) {
                if(randomNumber == 1) {
                    enemyType = "crackhead";
                }
                else {
                    enemyType = "chevre";
                }
            }
            else if(stageNumber == 3) {
                if(randomNumber == 1) {
                    enemyType = "secretaire";
                }
                else {
                    enemyType = "professor";
                }
            }
            Enemy enemy = EnemyFactory.createEnemy(enemyType, null); // On instancie des rats fistulés
            randomPlaceEntity(enemy); // On le place de manière aléatoire
            initializeEquipmentOfEnemy(enemy); // On l'équipe
        }
    }

    /**
     * Cette méthode permet d'initialiser le boss de la salle
     */
    public static void initializeBoss() {
        Player player = Player.getInstance();
        int stageNumber = player.getStageNumber();
        if(stageNumber == 1) {
            Enemy boss = EnemyFactory.createEnemy("abomination_des_egouts", null);
            boss.getInventory().addItem(WeaponFactory.createWeapon("epee_legendaire_des_egouts", null));
            randomPlaceEntity(boss);
        }
        else if(stageNumber == 2) {
            Enemy boss = EnemyFactory.createEnemy("arc_mystique_de_gobelin", null);
            boss.getInventory().addItem(WeaponFactory.createWeapon("epee_legendaire_des_egouts", null));
            randomPlaceEntity(boss);
        }
        else if(stageNumber == 3) {
            Enemy boss = EnemyFactory.createEnemy("tampon_legendaire_de_derdoudiable", null);
            boss.getInventory().addItem(WeaponFactory.createWeapon("epee_legendaire_des_egouts", null));
            randomPlaceEntity(boss);
        }
    }
    
    private static void initializeEquipmentOfEnemy(Enemy enemy) {
        // On génère un nombre aléatoire de pièces
        int randomNumber = getRandomNumber(1, 25);

        // On ajoute à l'inventaire de l'ennemi les pièces
        Coin coins = (Coin)EntityFactory.createEntity(GameConfiguration.COIN_ENTITYTYPE, null);
        coins.setConsumableValue(randomNumber);
        enemy.getInventory().addItem(coins);

        // On donne ou pas à l'ennemi son arme favorite
        if(randomNumber < 12) {
            if(enemy.getEntityType().equals("rat_fistule")) {
                enemy.getEquipment().setWeapon(WeaponFactory.createWeapon("epee_de_rat", null));
            }
            else if(enemy.getEntityType().equals("rocky_blateboa")) {
                enemy.getEquipment().setWeapon(WeaponFactory.createWeapon("epee_de_chevalier", null));
            }
            else if(enemy.getEntityType().equals("crackhead")) {
                enemy.getEquipment().setWeapon(WeaponFactory.createWeapon("dagues_de_crackhead", null));
            }
            else if(enemy.getEntityType().equals("chevre")) {
                enemy.getEquipment().setWeapon(WeaponFactory.createWeapon("arc_de_chevre", null));
            }
            else if(enemy.getEntityType().equals("secretaire")) {
                enemy.getEquipment().setWeapon(WeaponFactory.createWeapon("dagues_de_secretaire", null));
            }
            else if(enemy.getEntityType().equals("professor")) {
                enemy.getEquipment().setWeapon(WeaponFactory.createWeapon("sceptre_de_professeur", null));
            }
        }

        // Peu de chances pour que l'ennemi ait un bout d'armure
        if(randomNumber == 1) {
            enemy.getEquipment().setHelmet((Helmet)EntityFactory.createEntity(GameConfiguration.HELMET_ENTITYTYPE, null));
        }
        else if(randomNumber == 2) {
            enemy.getEquipment().setChestplate((Chestplate)EntityFactory.createEntity(GameConfiguration.CHESTPLATE_ENTITYTYPE, null));
        }
        else if(randomNumber == 3) {
            enemy.getEquipment().setBoots((Boots)EntityFactory.createEntity(GameConfiguration.BOOTS_ENTITYTYPE, null));
        }
        else if(randomNumber == 4) {
            enemy.getEquipment().setGloves((Gloves)EntityFactory.createEntity(GameConfiguration.GLOVES_ENTITYTYPE, null));
        }
        else if(randomNumber == 5) {
            enemy.getEquipment().setPants((Pants)EntityFactory.createEntity(GameConfiguration.PANTS_ENTITYTYPE, null));
        }

        // On génère un grand nombre aléatoire
        randomNumber = getRandomNumber(1, 100);
        if(randomNumber%8 == 0) {

        }
    }

    /**
     * On initialise l'environnement dans un ordre particulier : les murs avant le reste pour que tout soit contenu à l'intérieur des murs
     * @param manager
     */
    public static void initializeEnvironment() {
        Room currentRoom = manager.getCurrentRoom();

        initializeWalls();
        if(!currentRoom.isBoss()) {
            initializeObstacles();
            initializeChest();
            initializeGarbage();
        }
    }

    public static void initializeObstacles() {
        // on veut entre 7 et 11 arbres
        int obstacleNumber = getRandomNumber(7, 11);
        int stageNumber = Player.getInstance().getStageNumber();
        String entityType = "";
        if(stageNumber == 1) {
            entityType = GameConfiguration.PIPE_ASSET_ENTITYTYPE;
        }
        else if(stageNumber == 2) {
            entityType = GameConfiguration.TREE_ASSET_ENTITYTYPE;
        }
        else if(stageNumber == 3) {
            entityType = GameConfiguration.TABLE_ASSET_ENTITYTYPE;
        }
        for(int i = 0 ; i < obstacleNumber ; i++) {
            Environment environment = EnvironmentFactory.createEnvironment(entityType, null);
            randomPlaceEntity(environment);
        }
    }

    /**
     * Cette méthode permet d'initialiser un coffre à un endroit aléatoire de la salle.
     * Ce coffre contiendra soit une potion de vie, soit un magôt d'une grande valeur.
     */
    public static void initializeChest() {
        Chest chest = (Chest)EntityFactory.createEntity(GameConfiguration.CHEST_ENTITYTYPE, null);
        int randomNumber = getRandomNumber(1, 2);
        if(randomNumber == 1) {
            chest.addItem((HealthFlask)EntityFactory.createEntity(GameConfiguration.HEALTHFLASK_ENTITYTYPE, null));
        }
        else {
            Coin coin = (Coin)EntityFactory.createEntity(GameConfiguration.COIN_ENTITYTYPE, null);
            coin.setConsumableValue(getRandomNumber(100, 300));
            chest.addItem(coin);
        }
        randomPlaceEntity(chest);
    }

    public static void initializeVendor() {
        Vendor vendor = (Vendor)EntityFactory.createEntity(GameConfiguration.VENDOR_ENTITYTYPE, null);
        vendor.addSellingItem((Chestplate)EntityFactory.createEntity(GameConfiguration.CHESTPLATE_ENTITYTYPE, null), 300);
        vendor.addSellingItem((HealthFlask)EntityFactory.createEntity(GameConfiguration.HEALTHFLASK_ENTITYTYPE, null), 50);
        vendor.addSellingItem((Boots)EntityFactory.createEntity(GameConfiguration.BOOTS_ENTITYTYPE, null), 250);
        randomPlaceEntity(vendor);
    }

    public static void initializeGarbage() {
        // On met entre 6 et 16 piles de déchêts par salle
        int garbageNumber = getRandomNumber(6, 16);
        for(int i = 0 ; i < garbageNumber ; i++) {
            Garbage garbage = (Garbage)EntityFactory.createEntity(GameConfiguration.GARBAGE_ENTITYTYPE, null);
            // On veut qu'il y ai une chance sur 10 qu'une clé se trouve à l'intérieur du tas de détritus
            int keyOdds = getRandomNumber(1, 10);
            if(keyOdds == 10) {
                garbage.addItem((Key)EntityFactory.createEntity(GameConfiguration.KEY_ENTITYTYPE, null));
            }
            randomPlaceEntity(garbage);
        }
    }

    public static void initializeWalls() {
        Room currentRoom = manager.getCurrentRoom();
        Environment sideWall = EnvironmentFactory.createEnvironment(GameConfiguration.WALL_ASSET_ENTITYTYPE, null);
        sideWall.getHitbox().drawHitbox(new Position(0, 0), new Position(GameConfiguration.ROOM_LEFT_LIMITATION, GameConfiguration.WINDOW_HEIGHT));
        currentRoom.addEntity(sideWall);

        Environment upperWall = EnvironmentFactory.createEnvironment(GameConfiguration.WALL_ASSET_ENTITYTYPE, null);
        upperWall.getHitbox().drawHitbox(new Position(0, 0), new Position(GameConfiguration.WINDOW_WIDTH, GameConfiguration.ROOM_UPPER_LIMITATION));
        currentRoom.addEntity(upperWall);

        Environment lowerWall = EnvironmentFactory.createEnvironment(GameConfiguration.WALL_ASSET_ENTITYTYPE, null);
        lowerWall.getHitbox().drawHitbox(new Position(0, GameConfiguration.ROOM_LOWER_LIMITATION), new Position(GameConfiguration.WINDOW_WIDTH, GameConfiguration.WINDOW_HEIGHT));
        currentRoom.addEntity(lowerWall);

        Environment upperGateWall = EnvironmentFactory.createEnvironment(GameConfiguration.WALL_ASSET_ENTITYTYPE, null);
        upperGateWall.getHitbox().drawHitbox(new Position(GameConfiguration.ROOM_RIGHT_LIMITATION, 0), GameConfiguration.GATE_UPPERRIGHT);
        currentRoom.addEntity(upperGateWall);

        Environment lowerGateWall = EnvironmentFactory.createEnvironment(GameConfiguration.WALL_ASSET_ENTITYTYPE, null);
        lowerGateWall.getHitbox().drawHitbox(GameConfiguration.GATE_BOTTOMLEFT, new Position(GameConfiguration.WINDOW_WIDTH, GameConfiguration.WINDOW_HEIGHT));
        currentRoom.addEntity(lowerGateWall);

        Environment gate = EnvironmentFactory.createEnvironment(GameConfiguration.GATE_ASSET_ENTITYTYPE, null);
        gate.getHitbox().drawHitbox(GameConfiguration.GATE_UPPERLEFT, GameConfiguration.GATE_BOTTOMRIGHT);
        currentRoom.setGate(gate);
        currentRoom.addEntity(gate);
    }

    public static void randomPlaceEntity(Entity entity) {
        boolean cannotBePlaced = true;
        while(cannotBePlaced) {
            int randomX = getRandomNumber(0, GameConfiguration.WINDOW_WIDTH);
            int randomY = getRandomNumber(0, GameConfiguration.WINDOW_HEIGHT);
            Position randomPosition = new Position(randomX, randomY);
            entity.setPosition(randomPosition);
            // Si la hitbox de l'ennemi n'est en collision avec aucune autre dans la Room
            if(manager.verifHitboxes(entity, entity.getHitbox())) {
                manager.getCurrentRoom().addEntity(entity); // On ajoute l'entité à la liste d'entités de la Room
                break; // On sort de la boucle
            }
        }
    }
    
    /**
     * Cette méthode génère un nombre aléatoire compris entre un minimum et un maximum
     * 
     * @param min le nombre minimum
     * @param max le nombre maximum
     * @return le nombre aléatoire généré, formatté en entier
     */
    public static int getRandomNumber(int min, int max) {
		return (int) (Math.random() * (max + 1 - min)) + min;
    }
}
