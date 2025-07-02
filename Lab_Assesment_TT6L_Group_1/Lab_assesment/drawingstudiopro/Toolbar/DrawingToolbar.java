package drawingstudiopro.Toolbar;

import javax.swing.*;


import drawingstudiopro.canvas.DrawingCanvas; 
import drawingstudiopro.Library.ImageLibrary;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;



public class DrawingToolbar extends JPanel {
    private List<JButton> buttons;
    private JPanel toolbarPanel;
    private DrawingCanvas rightCanvas;
    private String selectedAnimal;
    private String selectedFlower;
    private ImageLibrary imageLibrary;

    public DrawingToolbar(DrawingCanvas rightCanvas) {
        this.rightCanvas = rightCanvas;
        this.buttons = new ArrayList<>();
        this.toolbarPanel = new  JPanel(new FlowLayout());
        this.imageLibrary = new ImageLibrary();

        refreshRightCanvasButton();
        colorPickerButton();
        strokeSizeButton();
        selectAnimalButton();
        selectFlowerButton();
        insertAnimalButton();
        insertFlowerButton();
        saveCustomImageButton();
    }

    public JPanel getToolbarPanel() {
        return toolbarPanel;
    }

    public void addButton(Icon icon, ActionListener action) {
        JButton button = new JButton(icon);
        button.addActionListener(action);
        buttons.add(button);
        toolbarPanel.add(button);
    }

    public void createLeftCanvasButton() {
        JButton btn = new JButton("Create Left Canvas");
        btn.addActionListener(e -> {
            System.out.println("Left Canvas Created or Reset");
        });
        buttons.add(btn);
        toolbarPanel.add(btn);
    }

    public void refreshRightCanvasButton() {
        JButton btn = new JButton("Clear Right Canvas");
        btn.addActionListener(e -> {
            rightCanvas.clearCanvas();
        });
        buttons.add(btn);
        toolbarPanel.add(btn);
    }

    public void colorPickerButton() {
        JButton btn = new JButton("Pick Color");
        btn.addActionListener(e -> {
            Color chosenColor = JColorChooser.showDialog(null, "Choose Drawing Color", Color.BLACK);
            if (chosenColor != null) {
                rightCanvas.setColor(chosenColor);
            }
        });
        buttons.add(btn);
        toolbarPanel.add(btn);
    }

    public void strokeSizeButton() {
        JButton btn = new JButton("Set Stroke Size");
        btn.addActionListener(e -> {
            String input = JOptionPane.showInputDialog("Enter stroke size (1-20):");
            try {
                int size = Integer.parseInt(input);
                if (size >= 1 && size <= 20) {
                    rightCanvas.setStrokeSize(size);
                } else {
                    JOptionPane.showMessageDialog(null, "Please enter a value between 1 and 20.");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Invalid number.");
            }
        });
        buttons.add(btn);
        toolbarPanel.add(btn);
    }

    public void selectAnimalButton() {
        JButton btn = new JButton("Select Animal");
        btn.addActionListener(e -> {
            String[] animalNames = imageLibrary.getAllAnimals().keySet().toArray(new String[0]);
            String choice = (String) JOptionPane.showInputDialog(
                null,
                "Select an Animal:",
                "Animal Library",
                JOptionPane.PLAIN_MESSAGE,
                null,
                animalNames,
                animalNames.length > 0 ? animalNames[0] : null
            );
            if (choice != null) {
                selectedAnimal = choice;
                JOptionPane.showMessageDialog(null, choice + "selected.");
            }
        });
        buttons.add(btn);
        toolbarPanel.add(btn);
    }

    public void selectFlowerButton() {
        JButton btn = new JButton("Select Flower");
        btn.addActionListener(e -> {
            String[] flowerNames = imageLibrary.getAllFlowers().keySet().toArray(new String[0]);
            String choice = (String) JOptionPane.showInputDialog(
                null,
                "Select a flower:",
                "Flower Library",
                JOptionPane.PLAIN_MESSAGE,
                null,
                flowerNames,
                flowerNames.length > 0 ? flowerNames[0] : null
            );
            if (choice != null) {
                selectedFlower = choice;
                JOptionPane.showMessageDialog(null, choice + "selected.");
            }
        });
        buttons.add(btn);
        toolbarPanel.add(btn);
    }

    public void insertImage(ImageIcon image) {
        Graphics g = this.getGraphics();
        g.drawImage(image.getImage(), 50, 50, this);
        repaint();
    }

    public void insertAnimalButton() {
        JButton btn = new JButton("Insert Animal");
        btn.addActionListener(e -> {
            if (selectedAnimal != null) {
                ImageIcon animalImage = imageLibrary.getAnimalImage(selectedAnimal);
                if (animalImage != null) {
                    rightCanvas.insertImage(animalImage);
                } else {
                    JOptionPane.showMessageDialog(null,"Animal image not found.");
                }
            } else {
                JOptionPane.showMessageDialog(null, "Please select an animal first.");
            }
        });
        buttons.add(btn);
        toolbarPanel.add(btn);
    }

    public void insertFlowerButton() {
        JButton btn = new JButton("Insert Flower");
        btn.addActionListener(e -> {
            if (selectedFlower != null) {
                ImageIcon flowerImage = imageLibrary.getFlowerImage(selectedFlower);
                if ( flowerImage != null) {
                    rightCanvas.insertImage(flowerImage);
                } else {
                    JOptionPane.showMessageDialog(null, "Flower image not found.");
                }
            } else {
                JOptionPane.showMessageDialog(null, "Please select a flower first.");
            }
        });
        buttons.add(btn);
        toolbarPanel.add(btn);
    }

    public void saveCustomImageButton() {
        JButton btn = new JButton("Save as Custom");
        btn.addActionListener(e -> {
            String name = JOptionPane.showInputDialog("Enter a name for this custom drawing:");
            if (name != null && !name.trim().isEmpty()) {
                ImageIcon snapshot = rightCanvas.captureSnapshot();
                imageLibrary.addCustomImage(name, snapshot);
                JOptionPane.showMessageDialog(null, "Custom drawing saved as: " + name);
            } else {
                JOptionPane.showMessageDialog(null, "Invalid name. Custom drawing not saved.");
            }
        });
        buttons.add(btn);
        toolbarPanel.add(btn);
    }

    public ImageIcon captureSnapshot() {
        BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        paintAll(g2);
        g2.dispose();
        return new ImageIcon(image);
    }

    public void addToCompositionButton() {
     
    }    
}