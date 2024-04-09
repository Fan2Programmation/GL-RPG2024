package gui.containersGUI;

import javax.swing.*;

import config.GameConfiguration;

import java.awt.*;
import java.util.ArrayList;

import engine.entities.characters.Player;
import engine.entities.items.Slot;
import engine.entities.items.containers.Inventory;
import engine.entities.items.equipment.*;
import engine.entities.items.weapons.*;
import engine.process.EntityManager;

public class InventoryGUI extends ContainerGUI {

    private Player player = Player.getInstance();
    private Inventory inventory = player.getInventory();
    private JPanel inventoryPanel = new JPanel();
    private JPanel playerViewPanel = new JPanel();
    private JPanel equipedItemsPanel = new JPanel();
    private JPanel playerStatisticsPanel = new JPanel();
    
    public InventoryGUI(EntityManager manager) {
        super(manager);

        initOverallView();

        pack();
        setLocationRelativeTo(null);
        setTitle("Inventory");
		setVisible(true);
    }

    public void initOverallView() {
        // On veut séparer notre première fenêtre en deux parties, gauche et droite
        this.getContentPane().setLayout(new GridLayout(1, 2));
        // À gauche on ajoute la vue du joueur
        this.add(playerViewPanel);
        initPlayerViewPanel();
        // À droite on ajoute un autre Panel
        JPanel panelDroite = new JPanel();
        this.add(panelDroite);
        // Que l'on va aussi découper en 2 parties, haute et basse
        panelDroite.setLayout(new GridLayout(2, 1));
        // En haut on met la partie inventaire
        panelDroite.add(inventoryPanel);
        initInventoryPanel();
        // En bas on met les statistiques du joueur
        panelDroite.add(playerStatisticsPanel);
        initPlayerStatisticsPanel();
    }

    public void initInventoryPanel() {
        inventoryPanel.setLayout(new GridLayout(2, 1));
        JLabel inventaireLabel = new JLabel("Inventaire", JLabel.CENTER);
        inventoryPanel.add(inventaireLabel);
        // La vue des items de l'inventaire sera comme une ligne avec une colonne par Slot (donc le nombre max d'objets dans l'inventaire)
        JPanel itemsPanel = new JPanel();
        itemsPanel.setLayout(new GridLayout(1, GameConfiguration.INVENTORY_MAX));
        inventoryPanel.add(itemsPanel);
        ArrayList<Slot> slots = inventory.getSlots();
    
        initSlotsPanels(itemsPanel, slots);
    }

    public void initPlayerViewPanel() {
        playerViewPanel.setLayout(new GridLayout(1, 2));

        playerViewPanel.add(equipedItemsPanel);
        initEquipedItemsPanel();

        String playerFilePath = "src/ressources/mainCharacter.png";
        ImageIcon playerIcon = new ImageIcon(playerFilePath);
        JLabel iconLabel = new JLabel(playerIcon, JLabel.CENTER);
        playerViewPanel.add(iconLabel);
    }

    public void initEquipedItemsPanel() {
        // Une ligne pour le JLabel puis des lignes pour les items équipés
        equipedItemsPanel.setLayout(new GridLayout(7, 1));
        JLabel equipedItemsPanelLabel = new JLabel("Items équipés", JLabel.CENTER);
        equipedItemsPanel.add(equipedItemsPanelLabel);

        Weapon weapon = player.getEquipment().getWeapon();
        Helmet helmet = player.getEquipment().getHelmet();
        Gloves gloves = player.getEquipment().getGloves();
        Chestplate chestplate = player.getEquipment().getChestplate();
        Pants pants = player.getEquipment().getPants();
        Boots boots = player.getEquipment().getBoots();
        JPanel weaponPanel = new JPanel();
        JPanel helmetPanel = new JPanel();
        JPanel chestplatePanel = new JPanel();
        JPanel glovesPanel = new JPanel();
        JPanel pantsPanel = new JPanel();
        JPanel bootsPanel = new JPanel();
        initItemSlot(equipedItemsPanel, weaponPanel, weapon, "Arme");
        initItemSlot(equipedItemsPanel, helmetPanel, helmet, "Casque");
        initItemSlot(equipedItemsPanel, chestplatePanel, gloves, "Plastron");
        initItemSlot(equipedItemsPanel, glovesPanel, chestplate, "Gants");
        initItemSlot(equipedItemsPanel, pantsPanel, pants, "Jambières");
        initItemSlot(equipedItemsPanel, bootsPanel, boots, "Bottes");
    }

    public void initPlayerStatisticsPanel() {
        // Une colonne et une ligne par statistique, la dernière utilisée pour l'affichage de l'EXP
        playerStatisticsPanel.setLayout(new GridLayout(7, 1));
        JTextField healthTextField = new JTextField();
        JTextField armorTextField = new JTextField();
        JTextField attackSpeedTextField = new JTextField();
        JTextField moveSpeedTextField = new JTextField();
        JTextField abilityCooldownTextField = new JTextField();
        JTextField stunCooldownTextField = new JTextField();

        healthTextField.setEditable(false);
        armorTextField.setEditable(false);
        attackSpeedTextField.setEditable(false);
        moveSpeedTextField.setEditable(false);
        abilityCooldownTextField.setEditable(false);
        stunCooldownTextField.setEditable(false);

        healthTextField.setText("Nombre de points de vie actuels : " + player.getHealth());
        armorTextField.setText("Nombre de points d'armure : " + player.getArmor());
        attackSpeedTextField.setText("Vitesse d'attaque : " + player.getAttackSpeed());
        moveSpeedTextField.setText("Vitesse de déplacement : " + player.getMoveSpeed());
        abilityCooldownTextField.setText("Délai de récupération des abilités : " + player.getAbilityCooldown() + "ms");
        stunCooldownTextField.setText("Durée des immobilisations : " + player.getStunCooldown() + "ms");

        playerStatisticsPanel.add(healthTextField);
        playerStatisticsPanel.add(armorTextField);
        playerStatisticsPanel.add(attackSpeedTextField);
        playerStatisticsPanel.add(moveSpeedTextField);
        playerStatisticsPanel.add(abilityCooldownTextField);
        playerStatisticsPanel.add(stunCooldownTextField);

        // Partie expérience

        JProgressBar expProgressBar = new JProgressBar();

        expProgressBar.setMinimum(0);
        expProgressBar.setMaximum(1000);

        expProgressBar.setValue(player.getExp());

        expProgressBar.setStringPainted(true);
        expProgressBar.setString("EXP: " + player.getExp() + " / " + expProgressBar.getMaximum());

        playerStatisticsPanel.add(expProgressBar);
    }

    /**
     * Méthode permettant de rafraîchir l'affichage des 3 panels principaux de la fenêtre
     */
    @Override
    public void refreshContainer() {
        inventoryPanel.removeAll(); // Supprime tous les composants du panel
        initInventoryPanel(); // Reconstruit le panel
        inventoryPanel.revalidate(); // Indique que le layout manager doit recalculer le layout
        inventoryPanel.repaint(); // Demande la redessin du panel

        equipedItemsPanel.removeAll();
        initEquipedItemsPanel();
        equipedItemsPanel.revalidate();
        equipedItemsPanel.repaint();

        playerStatisticsPanel.removeAll();
        initPlayerStatisticsPanel();
        playerStatisticsPanel.revalidate();
        playerStatisticsPanel.repaint();
    }
}
