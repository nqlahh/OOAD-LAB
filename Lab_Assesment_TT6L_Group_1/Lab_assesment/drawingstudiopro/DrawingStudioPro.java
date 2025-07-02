package drawingstudiopro;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import drawingstudiopro.UI.ButtonTabComponent;
import drawingstudiopro.canvas.*;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

public class DrawingStudioPro extends JFrame {
    private JTabbedPane compositionTabbedPane;
    private DrawingCanvas drawingCanvas;
    private List<CompositionCanvas> compositionCanvases;
    private int canvasCounter = 0;
    private JPanel galleryPanel;
    private JScrollPane galleryScrollPane;

    public DrawingStudioPro() {
        super("Drawing Studio Pro");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);

        compositionCanvases = new ArrayList<>();

        // Create gallery panel
        createGalleryPanel();

        // Toolbar setup
        JToolBar toolBar = new JToolBar("Tools");
        toolBar.setOrientation(JToolBar.VERTICAL);
        toolBar.setFloatable(false);

        // Add toolbar buttons

        // Add Item Button
        JButton addItemButton = new JButton("Add Item");
        addItemButton.setToolTipText("Add Animal or Flower from Gallery");
        toolBar.add(addItemButton);

        JPopupMenu addItemMenu = new JPopupMenu();
        JMenuItem addAnimalMenuItem = new JMenuItem("Add Animal");
        JMenuItem addFlowerMenuItem = new JMenuItem("Add Flower");
        JMenuItem addCustomImageMenuItem = new JMenuItem("Add Custom Image");

        addAnimalMenuItem.addActionListener(e -> showImageGallery("animal"));
        addFlowerMenuItem.addActionListener(e -> showImageGallery("flower"));
        addCustomImageMenuItem.addActionListener(e -> showAddCustomImageDialog());

        addItemMenu.add(addAnimalMenuItem);
        addItemMenu.add(addFlowerMenuItem);
        addItemMenu.add(addCustomImageMenuItem);

        addItemButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                addItemMenu.show(e.getComponent(), e.getX(), e.getY());
            }
        });
        

        toolBar.addSeparator();

        // Rotate Buttons
        JButton rotateItemButton = new JButton("Rotate Selected Item");
        rotateItemButton.addActionListener(e -> {
            Component selected = compositionTabbedPane.getSelectedComponent();
            if (selected instanceof CompositionCanvas) {
                ((CompositionCanvas) selected).rotateSelectedItem(Math.toRadians(15));
            }
        });
        toolBar.add(rotateItemButton);
        

        // Resize Selected Item
        JButton resizeItemButton = new JButton("Resize Selected Item");
        resizeItemButton.addActionListener(e -> {
            Component selected = compositionTabbedPane.getSelectedComponent();
            if (selected instanceof CompositionCanvas) {
                DrawingItem item = ((CompositionCanvas) selected).getSelectedItem();
                if (item != null) {
                    String input = JOptionPane.showInputDialog(this, "Enter new width,height (e.g. 120,120):", "Resize", JOptionPane.PLAIN_MESSAGE);
                    if (input != null && input.contains(",")) {
                        try {
                            String[] parts = input.split(",");
                            int w = Integer.parseInt(parts[0].trim());
                            int h = Integer.parseInt(parts[1].trim());
                            item.setSize(w, h);
                            ((CompositionCanvas) selected).repaint();
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(this, "Invalid input.");
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "No item selected.");
                }
            }
        });
        toolBar.add(resizeItemButton);

        // Mirror Selected Item
        JButton mirrorItemButton = new JButton("Mirror Selected Item");
        mirrorItemButton.addActionListener(e -> {
            Component selected = compositionTabbedPane.getSelectedComponent();
            if (selected instanceof CompositionCanvas) {
                DrawingItem item = ((CompositionCanvas) selected).getSelectedItem();
                if (item != null) {
                    item.mirror();
                    ((CompositionCanvas) selected).repaint();
                } else {
                    JOptionPane.showMessageDialog(this, "No item selected.");
                }
            }
        });
        toolBar.add(mirrorItemButton);

        JButton deleteItemButton = new JButton("Delete Selected Item");
        deleteItemButton.addActionListener(e -> {
            Component selected = compositionTabbedPane.getSelectedComponent();
            if (selected instanceof CompositionCanvas) {
                CompositionCanvas canvas = (CompositionCanvas) selected;
                DrawingItem item = canvas.getSelectedItem();
                if (item != null) {
                    canvas.removeDrawingItem(item);
                } else {
                    JOptionPane.showMessageDialog(this, "No item selected.");
                }
            }
        });
        toolBar.add(deleteItemButton);
        toolBar.addSeparator();


        JButton newCompCanvasButton = new JButton("New Comp. Canvas");
        newCompCanvasButton.addActionListener(e -> createNewCompositionCanvas());
        toolBar.add(newCompCanvasButton);

        JButton clearCompCanvasButton = new JButton("Clear Comp. Canvas");
        clearCompCanvasButton.addActionListener(e -> {
            Component selected = compositionTabbedPane.getSelectedComponent();
            if (selected instanceof CompositionCanvas) {
                ((CompositionCanvas) selected).clearCanvas();
            }
        });
        toolBar.add(clearCompCanvasButton);

        JLabel rotateCanvasLabel = new JLabel("Rotate Canvas:");
        toolBar.add(rotateCanvasLabel);

        JSlider rotateCanvasSlider = new JSlider(JSlider.HORIZONTAL, -180, 180, 0);
        rotateCanvasSlider.setMajorTickSpacing(90);
        rotateCanvasSlider.setMinorTickSpacing(15);
        rotateCanvasSlider.setPaintTicks(true);
        rotateCanvasSlider.setPaintLabels(true);
        rotateCanvasSlider.setPreferredSize(new Dimension(120, 40));
        toolBar.add(rotateCanvasSlider);

        rotateCanvasSlider.addChangeListener(e -> {
            Component selected = compositionTabbedPane.getSelectedComponent();
            if (selected instanceof CompositionCanvas) {
                double angle = Math.toRadians(rotateCanvasSlider.getValue());
                ((CompositionCanvas) selected).setCanvasRotationAngle(angle);
            }
        });
        toolBar.addSeparator();

        // Drawing Canvas Controls
        
        JButton chooseColorButton = new JButton("Choose Pen Color");
        chooseColorButton.addActionListener(e -> {
            Color selectedColor = JColorChooser.showDialog(this, "Select Pen Color", drawingCanvas.getCurrentColor());
            if (selectedColor != null) {
                drawingCanvas.setCurrentColor(selectedColor);
            }
        });
        toolBar.add(chooseColorButton);
        toolBar.addSeparator();

        JPanel strokePanel = new JPanel();
        strokePanel.setLayout(new BoxLayout(strokePanel, BoxLayout.Y_AXIS));
        strokePanel.add(new JLabel("Pen Size:"));
        JSlider strokeSizeSlider = new JSlider(JSlider.HORIZONTAL, 1, 10, 2);
        strokeSizeSlider.setMajorTickSpacing(1);
        strokeSizeSlider.setPaintTicks(true);
        strokeSizeSlider.setPaintLabels(true);
        strokeSizeSlider.addChangeListener(e -> drawingCanvas.setStrokeSize(strokeSizeSlider.getValue()));
        strokePanel.add(strokeSizeSlider);
        strokePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        toolBar.add(strokePanel);
        toolBar.addSeparator();

        JButton mergeCanvasButton = new JButton("Merge to New Canvas");
        mergeCanvasButton.addActionListener(e -> mergeCurrentCompositionCanvas());
        toolBar.add(mergeCanvasButton);

        JButton undoButton = new JButton("Undo Drawing Canvas");
        undoButton.setToolTipText("Undo last drawing step");
        undoButton.addActionListener(e -> drawingCanvas.undo());
        toolBar.add(undoButton);

        JButton clearDrawingCanvasButton = new JButton("Clear Drawing Canvas");
        clearDrawingCanvasButton.addActionListener(e -> drawingCanvas.clearCanvas());
        toolBar.add(clearDrawingCanvasButton);

        JButton saveButton = new JButton("Save Drawing Canvas");
        saveButton.addActionListener(e -> saveDrawingCanvasImage(drawingCanvas));
        toolBar.add(saveButton);

        // UI Layout
        compositionTabbedPane = new JTabbedPane();
        createNewCompositionCanvas();

        drawingCanvas = new DrawingCanvas();

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, compositionTabbedPane, drawingCanvas);
        splitPane.setDividerLocation(getWidth() / 2);
        splitPane.setResizeWeight(0.5);

        add(toolBar, BorderLayout.WEST);
        add(splitPane, BorderLayout.CENTER);

        setVisible(true);
    }

    private void createGalleryPanel() {
        galleryPanel = new JPanel();
        galleryPanel.setLayout(new GridLayout(0, 3, 10, 10)); // 3 columns, flexible rows
        galleryPanel.setBorder(BorderFactory.createTitledBorder("Image Gallery"));

        galleryScrollPane = new JScrollPane(galleryPanel);
        galleryScrollPane.setPreferredSize(new Dimension(400, 300));
        galleryScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        galleryScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    }

    private void showImageGallery(String imageType) {
        // Clear previous gallery content
        galleryPanel.removeAll();

        // Define the directory path based on image type
        String directoryPath = "C:/Users/user/Downloads/Lab_assesment(1)/Lab_assesment/drawingstudiopro/canvas/";

        // Look for images in the directory
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            JOptionPane.showMessageDialog(this, "Image directory not found: " + directoryPath);
            // Fallback to sample images if the directory doesn't exist
            addSampleImages(imageType);
            showGalleryDialog(imageType);
            return;
        }

        File[] imageFiles = directory.listFiles((dir, name) -> {
            String lowerName = name.toLowerCase();
            return (lowerName.contains(imageType.toLowerCase()) &&
                   (lowerName.endsWith(".jpg") || lowerName.endsWith(".jpeg") ||
                    lowerName.endsWith(".png") || lowerName.endsWith(".gif")));
        });

        if (imageFiles == null || imageFiles.length == 0) {
            // If no specific files found, show some sample images
            addSampleImages(imageType);
        } else {
            // Add found images to gallery
            for (File imageFile : imageFiles) {
                addImageToGallery(imageFile, imageType);
            }
        }
        showGalleryDialog(imageType);
    }

    private void showGalleryDialog(String imageType) {
        // Show the gallery in a dialog
        JDialog galleryDialog = new JDialog(this, imageType.substring(0, 1).toUpperCase() +
                                             imageType.substring(1) + " Gallery", true);
        galleryDialog.setLayout(new BorderLayout());
        galleryDialog.add(galleryScrollPane, BorderLayout.CENTER);

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> galleryDialog.dispose());
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(closeButton);
        galleryDialog.add(buttonPanel, BorderLayout.SOUTH);

        galleryDialog.setSize(450, 400);
        galleryDialog.setLocationRelativeTo(this);
        galleryDialog.setVisible(true);
    }

    private void addSampleImages(String imageType) {
        // Create sample image placeholders if no actual images are found
        for (int i = 1; i <= 6; i++) {
            JPanel imagePanel = new JPanel();
            imagePanel.setPreferredSize(new Dimension(120, 120));
            imagePanel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
            imagePanel.setBackground(Color.LIGHT_GRAY);
            imagePanel.setLayout(new BorderLayout());

            JLabel label = new JLabel(imageType.substring(0, 1).toUpperCase() +
                                     imageType.substring(1) + " " + i, SwingConstants.CENTER);
            imagePanel.add(label, BorderLayout.CENTER);

            // Make it clickable
            imagePanel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    addItemToCanvas(imageType, null); // Pass null for sample images
                    // Close the gallery dialog
                    SwingUtilities.getWindowAncestor(imagePanel).dispose();
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    imagePanel.setBorder(BorderFactory.createLineBorder(Color.BLUE, 3));
                    imagePanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    imagePanel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
                    imagePanel.setCursor(Cursor.getDefaultCursor());
                }
            });

            galleryPanel.add(imagePanel);
        }

        galleryPanel.revalidate();
        galleryPanel.repaint();
    }

    private void addImageToGallery(File imageFile, String imageType) {
        try {
            BufferedImage originalImage = ImageIO.read(imageFile);
            if (originalImage != null) {
                // Scale image for gallery display
                Image scaledImage = originalImage.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                ImageIcon icon = new ImageIcon(scaledImage);

                JLabel imageLabel = new JLabel(icon);
                imageLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
                imageLabel.setToolTipText(imageFile.getName());

                // Make it clickable
                imageLabel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        addItemToCanvas(imageType, originalImage);
                        // Close the gallery dialog
                        SwingUtilities.getWindowAncestor(imageLabel).dispose();
                    }

                    @Override
                    public void mouseEntered(MouseEvent e) {
                        imageLabel.setBorder(BorderFactory.createLineBorder(Color.BLUE, 3));
                        imageLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        imageLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
                        imageLabel.setCursor(Cursor.getDefaultCursor());
                    }
                });

                galleryPanel.add(imageLabel);
            }
        } catch (IOException e) {
            System.err.println("Could not load image: " + imageFile.getName());
        }

        galleryPanel.revalidate();
        galleryPanel.repaint();
    }

    private void addItemToCanvas(String imageType, BufferedImage selectedImage) {
        Component selected = compositionTabbedPane.getSelectedComponent();
        if (selected instanceof CompositionCanvas) {
            if (imageType.equals("flower")) {
                if (selectedImage != null) {
                    // Add custom image flower item using the enhanced FlowerItem constructor
                    ((CompositionCanvas) selected).addDrawingItem(new FlowerItem(
                            50 + (int)(Math.random() * 200),
                            50 + (int)(Math.random() * 200),
                            80, 80, selectedImage));
                } else {
                    // Add default flower item (if no specific image was selected)
                    ((CompositionCanvas) selected).addDrawingItem(new FlowerItem(
                            50 + (int)(Math.random() * 200),
                            50 + (int)(Math.random() * 200),
                            80, 80));
                }
            } else if (imageType.equals("animal")) {
                if (selectedImage != null) {
                    // Add custom image animal item using the enhanced AnimalItem constructor
                    ((CompositionCanvas) selected).addDrawingItem(new AnimalItem(
                            50 + (int)(Math.random() * 200),
                            50 + (int)(Math.random() * 200),
                            100, 100, selectedImage));
                } else {
                    // Add default animal item (if no specific image was selected)
                    ((CompositionCanvas) selected).addDrawingItem(new AnimalItem(
                            50 + (int)(Math.random() * 200),
                            50 + (int)(Math.random() * 200),
                            100, 100));
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please create or select a Composition Canvas first.");
        }
    }

    private void showAddCustomImageDialog() {
        String[] options = {"From Device", "From Internet"};
        int choice = JOptionPane.showOptionDialog(
            this,
            "Add image from:",
            "Add Custom Image",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]
        );

        BufferedImage customImage = null;

        if (choice == 0) { // From Device
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Select Image File");
            fileChooser.setAcceptAllFileFilterUsed(false);
            fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Image Files", "jpg", "jpeg", "png", "gif"));
            if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                try {
                    customImage = ImageIO.read(file);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Failed to load image: " + ex.getMessage());
                }
            }
        } else if (choice == 1) { // From Internet
            String url = JOptionPane.showInputDialog(this, "Enter image URL:");
            if (url != null && !url.trim().isEmpty()) {
                try {
                    java.net.URL imageUrl = new java.net.URL(url.trim());
                    customImage = ImageIO.read(imageUrl);
                    if (customImage == null) {
                        JOptionPane.showMessageDialog(this, "The URL does not point to a valid image.");
                    }
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Failed to load image from URL: " + ex.getMessage());
                }
            }
        }

        if (customImage != null) {
            addCustomImageToCompositionCanvas(customImage);
        }
    }

    private void addCustomImageToCompositionCanvas(BufferedImage image) {
        Component selected = compositionTabbedPane.getSelectedComponent();
        if (selected instanceof CompositionCanvas) {
            ((CompositionCanvas) selected).addDrawingItem(
                new ImageDrawingItem(image, 50, 50, 120, 120)
            );
        } else {
            JOptionPane.showMessageDialog(this, "Please select a Composition Canvas first.");
        }
    }

    private void createNewCompositionCanvas() {
        canvasCounter++;
        CompositionCanvas newCanvas = new CompositionCanvas();
        compositionCanvases.add(newCanvas);

        String title = "Composition " + canvasCounter;
        compositionTabbedPane.addTab(title, newCanvas);
        int index = compositionTabbedPane.indexOfComponent(newCanvas);

        compositionTabbedPane.setTabComponentAt(index, new ButtonTabComponent(compositionTabbedPane));

        compositionTabbedPane.setSelectedIndex(index);
    }

   private void mergeCurrentCompositionCanvas() {
    Component selected = compositionTabbedPane.getSelectedComponent();
        if (selected instanceof CompositionCanvas) {
            CompositionCanvas originalCanvas = (CompositionCanvas) selected;
            BufferedImage mergedImage = originalCanvas.mergeToNewCanvas();

            // Use the actual size of the original canvas
            Dimension originalSize = originalCanvas.getSize();
            double originalRotation = originalCanvas.getCanvasRotationAngle();

            // Create new canvas with same preferred size and rotation
            CompositionCanvas newMergedCanvas = new CompositionCanvas() {
                @Override
                public Dimension getPreferredSize() {
                    return originalSize;
                }
            };
            newMergedCanvas.setPreferredSize(originalSize);
            newMergedCanvas.setCanvasRotationAngle(originalRotation);

            // Add the merged image as a drawing item, stretched to fill the canvas
            newMergedCanvas.addDrawingItem(new ImageDrawingItem(
                mergedImage, 0, 0, originalSize.width, originalSize.height
            ));

            compositionCanvases.add(newMergedCanvas);

            canvasCounter++;
            String title = "Merged " + canvasCounter;
            compositionTabbedPane.addTab(title, newMergedCanvas);
            int index = compositionTabbedPane.indexOfComponent(newMergedCanvas);

            compositionTabbedPane.setTabComponentAt(index, new ButtonTabComponent(compositionTabbedPane));
            compositionTabbedPane.setSelectedIndex(index);
        } else {
            JOptionPane.showMessageDialog(this, "No Composition Canvas selected to merge.");
        }
    }


    private void saveDrawingCanvasImage(DrawingCanvas canvas) {
        BufferedImage imageToSave = canvas.getCanvasImage();
        if (imageToSave == null) {
            JOptionPane.showMessageDialog(this, "The drawing canvas is empty.",
                "Nothing to Save", JOptionPane.WARNING_MESSAGE);
            return;
        }
        saveImageWithFormatChoice(imageToSave, "Drawing");
    }

    private void saveImageWithFormatChoice(BufferedImage image, String imageType) {
        if (image == null || image.getWidth() <= 1) {
            JOptionPane.showMessageDialog(this, "No image content to save.",
                "Save Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Create file chooser with format options
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save " + imageType + " Image");

        // Remove default "All files" option
        fileChooser.setAcceptAllFileFilterUsed(false);

        // Add supported format filters
        FileNameExtensionFilter pngFilter = new FileNameExtensionFilter("PNG Image (*.png)", "png");
        FileNameExtensionFilter jpgFilter = new FileNameExtensionFilter("JPEG Image (*.jpg, *.jpeg)", "jpg", "jpeg");
        fileChooser.addChoosableFileFilter(pngFilter);
        fileChooser.addChoosableFileFilter(jpgFilter);

        // Set PNG as default
        fileChooser.setFileFilter(pngFilter);

        // Suggest a default filename
        fileChooser.setSelectedFile(new File(imageType.toLowerCase() + ".png"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            FileNameExtensionFilter selectedFilter = (FileNameExtensionFilter) fileChooser.getFileFilter();
            String format = selectedFilter.getExtensions()[0];

            // Ensure file has correct extension
            String filePath = file.getAbsolutePath();
            if (!filePath.toLowerCase().endsWith("." + format)) {
                file = new File(filePath + "." + format);
            }

            try {
                // Handle JPEG specific requirements (must be TYPE_INT_RGB)
                if (format.equalsIgnoreCase("jpg") || format.equalsIgnoreCase("jpeg")) {
                    BufferedImage jpgImage = new BufferedImage(
                        image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
                    Graphics2D g2d = jpgImage.createGraphics();
                    g2d.drawImage(image, 0, 0, null);
                    g2d.dispose();
                    image = jpgImage;
                }

                if (ImageIO.write(image, format, file)) {
                    JOptionPane.showMessageDialog(this,
                        imageType + " saved successfully to:\n" + file.getAbsolutePath());
                } else {
                    throw new IOException("No appropriate writer found for format: " + format);
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this,
                    "Failed to save image: " + ex.getMessage(),
                    "Save Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DrawingStudioPro());
    }
}