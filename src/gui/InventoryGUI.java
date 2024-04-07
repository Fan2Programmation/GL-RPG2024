package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import engine.entities.characters.Player;
import engine.entities.items.Inventory;
import engine.entities.items.Item;
import engine.entities.items.Slot;
import engine.entities.items.weapons.Weapon;

public class InventoryGUI extends JFrame {

    private Player player = Player.getInstance();
    private Inventory inventory = player.getInventory();
    private JPanel inventoryPanel = new JPanel();
    private JPanel playerViewPanel = new JPanel();
    private JPanel equipedItems = new JPanel();
    private JPanel playerStatisticsPanel = new JPanel();
    
    public InventoryGUI() {
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

        pack();
        setLocationRelativeTo(null);
        setTitle("Inventory");
		setVisible(true);
    }

    public void initInventoryPanel() {
        // La vue de l'inventaire sera comme une ligne avec une colonne par item
        inventoryPanel.setLayout(new GridLayout(1, 7));
        for(Slot slot : inventory.getSlots()) {
            Item item = slot.getItem();
            initItemSlot(inventoryPanel, item, (item == null) ? "" : item.getEntityType(), true);
        }
    }

    public void initPlayerViewPanel() {
        playerViewPanel.setLayout(new GridLayout(1, 2));

        playerViewPanel.add(equipedItems);
        initEquipedItems();

        String playerFilePath = "src/ressources/mainCharacter.png";
        ImageIcon playerIcon = new ImageIcon(playerFilePath);
        JLabel iconLabel = new JLabel(playerIcon, JLabel.CENTER);
        playerViewPanel.add(iconLabel);
    }

    public void initEquipedItems() {
        // Une ligne pour le JLabel puis des lignes pour les items équipés
        equipedItems.setLayout(new GridLayout(7, 1));
        JLabel equipedItemsLabel = new JLabel("Items équipés", JLabel.CENTER);
        equipedItems.add(equipedItemsLabel);

        Weapon weapon = (Weapon)player.getWeaponSlot().getItem();
        initItemSlot(equipedItems, weapon, "Arme", false);
        initItemSlot(equipedItems, null, "Casque", false);
        initItemSlot(equipedItems, null, "Plastron", false);
        initItemSlot(equipedItems, null, "Gants", false);
        initItemSlot(equipedItems, null, "Jambières", false);
        initItemSlot(equipedItems, null, "Bottes", false);
    }

    public void initItemSlot(JPanel panel, Item item, String slotName, boolean button) {
        JPanel itemPanel = new JPanel();
        itemPanel.setLayout(new BorderLayout());
        itemPanel.setBackground(Color.WHITE);
        itemPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        JLabel nameLabel = new JLabel(slotName, JLabel.CENTER);
        itemPanel.add(nameLabel, BorderLayout.PAGE_START);

        if(item != null) {
            String itemFilePath = "src/ressources/" +  item.getEntityType() + ".png";
            ImageIcon itemIcon = new ImageIcon(itemFilePath);
            JLabel itemIconLabel = new JLabel(itemIcon, JLabel.CENTER);

            itemPanel.add(itemIconLabel, BorderLayout.CENTER);
        }

        if(button) {
            JButton useButton = new JButton("Use");
            itemPanel.add(useButton, BorderLayout.PAGE_END);
        }

        panel.add(itemPanel);
    }

    public void initPlayerStatisticsPanel() {
        // Une colonne et une ligne par statistique
        playerStatisticsPanel.setLayout(new GridLayout(4, 1));
        JTextField healthTextField = new JTextField();
        JTextField armorTextField = new JTextField();
        JTextField attackSpeedTextField = new JTextField();
        JTextField moveSpeedTextField = new JTextField();

        healthTextField.setEditable(false);
        armorTextField.setEditable(false);
        attackSpeedTextField.setEditable(false);
        moveSpeedTextField.setEditable(false);

        healthTextField.setText("Nombre de points de vie actuels : " + player.getHealth());
        armorTextField.setText("Nombre de points d'armure : " + player.getArmor());
        attackSpeedTextField.setText("Vitesse d'attaque : " + player.getAttackSpeed());
        moveSpeedTextField.setText("Vitesse de déplacement : " + player.getMoveSpeed());

        playerStatisticsPanel.add(healthTextField);
        playerStatisticsPanel.add(armorTextField);
        playerStatisticsPanel.add(attackSpeedTextField);
        playerStatisticsPanel.add(moveSpeedTextField);
    }

    class Return implements ActionListener {
		public void actionPerformed(ActionEvent e){
            InventoryGUI.this.setVisible(false);
		}
	}
}
