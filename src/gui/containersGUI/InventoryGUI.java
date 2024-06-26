package gui.containersGUI;

import javax.swing.*;

import config.GameConfiguration;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import engine.entities.characters.Player;
import engine.entities.items.Item;
import engine.entities.items.Slot;
import engine.entities.items.consumables.Consumable;
import engine.entities.containers.Inventory;
import engine.entities.items.equipment.*;
import engine.entities.items.weapons.*;
import engine.process.management.ConsumableManager;

/**
 * Classe permettant de créer une interface graphique pour l'inventaire du joueur.
 * 
 * @see ContainerGUI
 */
public class InventoryGUI extends ContainerGUI {

    private Player player = Player.getInstance();
    private Inventory inventory = player.getInventory();
    private JPanel inventoryPanel = new JPanel();
    private JPanel itemsPanel = new JPanel();
    private JPanel playerViewPanel = new JPanel();
    private JPanel equipedItemsPanel = new JPanel();
    private JPanel playerStatisticsPanel = new JPanel();
    private JPanel imageAndCoinPanel = new JPanel();
    
    /**
     * Constructeur de la classe InventoryGUI.
     */
    public InventoryGUI() {
        super.manager.setInventoryRefreshListener(this);

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
        itemsPanel.setLayout(new GridLayout(1, GameConfiguration.INVENTORY_MAX));
        inventoryPanel.add(itemsPanel);
        initInventorySlotsPanels();
    }

    /**
     * Méthode permettant d'initialiser les panels des slots de l'inventaire.
     */
    public void initInventorySlotsPanels() {
        // On récupère la liste des slots du sac
        ArrayList<Slot> slots = inventory.getSlots();
        for(int i = 0 ; i < GameConfiguration.INVENTORY_MAX ; i++) {
            // On garde le compte
            final int slotNumber = i;
            // On récupère l'Item du Slot
            Item item = slots.get(i).getItem();
            // On créé le Panel qui recevra l'Item
            JPanel itemPanel = new JPanel();

            initItemSlot(itemsPanel, itemPanel, item, (item == null) ? "" : item.getEntityName());

            itemPanel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    JPopupMenu popupMenu = new JPopupMenu();
                    JMenuItem deleteItem = new JMenuItem("Supprimer");

                    if(item instanceof Clothe || item instanceof Weapon) {
                        JMenuItem equipItem = new JMenuItem("Équiper");
                        popupMenu.add(equipItem);
                        equipItem.addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent e) {
                                manager.equipInventoryItem(slotNumber);
                            }
                        });
                    }

                    if(item instanceof Consumable) {
                        JMenuItem consumeItem = new JMenuItem("Consommer");
                        popupMenu.add(consumeItem);
                        consumeItem.addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent e) {
                                ConsumableManager.consumeInventoryItem(slotNumber);
                            }
                        });
                    }
                    deleteItem.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            manager.deleteInventoryItem(slotNumber);
                        }
                    });

                    popupMenu.add(deleteItem);
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            });
        }
    }

    /**
     * Méthode permettant d'initialiser le panel contenant l'image du joueur, son compteur de pièces et ses items équipés
     */
    public void initPlayerViewPanel() {
        playerViewPanel.setLayout(new GridLayout(1, 2));

        playerViewPanel.add(equipedItemsPanel);
        initEquipedItemsPanel();

        playerViewPanel.add(imageAndCoinPanel);
        initImageAndCoinsPanel();
    }
    
    /*
     * Méthode permettant d'initialiser le panel des items équipés
     */
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
        initItemSlot(equipedItemsPanel, glovesPanel, gloves, "Gants");
        initItemSlot(equipedItemsPanel, chestplatePanel, chestplate, "Plastron");
        initItemSlot(equipedItemsPanel, pantsPanel, pants, "Jambières");
        initItemSlot(equipedItemsPanel, bootsPanel, boots, "Bottes");
        initEquipmentSlotListener(weaponPanel, "weapon");
        initEquipmentSlotListener(helmetPanel, "helmet");
        initEquipmentSlotListener(glovesPanel, "gloves");
        initEquipmentSlotListener(chestplatePanel, "chestplate");
        initEquipmentSlotListener(pantsPanel, "pants");
        initEquipmentSlotListener(bootsPanel, "boots");
    }

    /*
     * Méthode permettant d'initialiser le panel de l'image et du compteur de pièces
     */
    public void initImageAndCoinsPanel() {
        imageAndCoinPanel.setLayout(new BorderLayout());

        String playerFilePath = "";
        switch(player.getPlayerClass()){
            case "heavy":
                playerFilePath = "src/ressources/assets/entity/costaud.png";
                break;
            case "sorcerer":
                playerFilePath = "src/ressources/assets/entity/sorcier.png";
                break;
            case "fast":
                playerFilePath = "src/ressources/assets/entity/rapide.png";
                break;
        }

        ImageIcon playerIcon = new ImageIcon(playerFilePath);
        JLabel iconLabel = new JLabel(playerIcon, JLabel.CENTER);
        imageAndCoinPanel.add(iconLabel, BorderLayout.CENTER);

        JTextField coinCounterLabel = new JTextField("Coins: " + player.getCoinCount());
        coinCounterLabel.setEditable(false);
        coinCounterLabel.setHorizontalAlignment(JTextField.CENTER);
        imageAndCoinPanel.add(coinCounterLabel, BorderLayout.SOUTH);
    }

    /*
     * Méthode initialisant un écouteur de clic pour les slots d'équipement
     */
    public void initEquipmentSlotListener(JPanel panel, String entityType) {
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JPopupMenu popupMenu = new JPopupMenu();
                JMenuItem desequipItem = new JMenuItem("Déséquiper");
                popupMenu.add(desequipItem);
                desequipItem.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        manager.desequipInventoryItem(entityType);
                    }
                });
                popupMenu.show(e.getComponent(), e.getX(), e.getY());
            }
        });
    }

    /**
     * Méthode permettant d'initialiser le panel des statistiques du joueur
     */
    public void initPlayerStatisticsPanel() {
        // Une colonne et une ligne par statistique, la dernière utilisée pour l'affichage de l'EXP
        playerStatisticsPanel.setLayout(new GridLayout(7, 1));
        JTextField healthTextField = new JTextField();
        JTextField armorTextField = new JTextField();
        JTextField attackDamageTextField = new JTextField();
        JTextField attackSpeedTextField = new JTextField();
        JTextField attackRangeTextField = new JTextField();
        JTextField moveSpeedTextField = new JTextField();
        JTextField abilityCooldownTextField = new JTextField();
        JTextField stunCooldownTextField = new JTextField();

        healthTextField.setEditable(false);
        armorTextField.setEditable(false);
        attackDamageTextField.setEditable(false);
        attackSpeedTextField.setEditable(false);
        attackRangeTextField.setEditable(false);
        moveSpeedTextField.setEditable(false);
        abilityCooldownTextField.setEditable(false);
        stunCooldownTextField.setEditable(false);

        healthTextField.setText("Points de vie actuels : " + player.getHealth() + " pv");
        armorTextField.setText("Armure : " + player.getArmor() + "%");
        attackDamageTextField.setText("Dégâts d'attaque : " + player.getAttackDamage());
        attackSpeedTextField.setText("Vitesse d'attaque : " + player.getAttackSpeed()/100 + " s");
        attackRangeTextField.setText("Portée d'attaque : " + player.getAttackRange());
        moveSpeedTextField.setText("Vitesse de déplacement : " + player.getMoveSpeed() + " pixels par tick");
        abilityCooldownTextField.setText("Délai de récupération des abilités : " + player.getAbilityCooldown()/100 + " s");
        stunCooldownTextField.setText("Durée des immobilisations : " + player.getStunCooldown()/100 + " s");

        playerStatisticsPanel.add(healthTextField);
        playerStatisticsPanel.add(armorTextField);
        playerStatisticsPanel.add(attackDamageTextField);
        playerStatisticsPanel.add(attackSpeedTextField);
        playerStatisticsPanel.add(attackRangeTextField);
        playerStatisticsPanel.add(moveSpeedTextField);
        playerStatisticsPanel.add(abilityCooldownTextField);
        playerStatisticsPanel.add(stunCooldownTextField);

        // Partie expérience

        JProgressBar expProgressBar = new JProgressBar();

        expProgressBar.setMinimum(0);
        expProgressBar.setMaximum(GameConfiguration.PLAYER_EXP_MAX);

        expProgressBar.setValue(player.getExp());

        expProgressBar.setStringPainted(true);
        expProgressBar.setString("Level : " + player.getLevel() + " | EXP: " + player.getExp() + " / " + expProgressBar.getMaximum());

        playerStatisticsPanel.add(expProgressBar);
    }

    /**
     * Méthode permettant de rafraîchir l'affichage des 3 panels principaux de la fenêtre
     */
    @Override
    public void refreshContainer() {
        inventoryPanel.removeAll(); // Supprime tous les composants du panel
        itemsPanel.removeAll();
        initInventoryPanel(); // Reconstruit le panel
        inventoryPanel.revalidate(); // Indique que le layout manager doit recalculer le layout
        inventoryPanel.repaint(); // Demande la redessin du panel
        itemsPanel.revalidate();
        itemsPanel.repaint();

        equipedItemsPanel.removeAll();
        initEquipedItemsPanel();
        equipedItemsPanel.revalidate();
        equipedItemsPanel.repaint();

        imageAndCoinPanel.removeAll();
        initImageAndCoinsPanel();
        imageAndCoinPanel.revalidate();
        imageAndCoinPanel.repaint();

        playerStatisticsPanel.removeAll();
        initPlayerStatisticsPanel();
        playerStatisticsPanel.revalidate();
        playerStatisticsPanel.repaint();
    }
}
