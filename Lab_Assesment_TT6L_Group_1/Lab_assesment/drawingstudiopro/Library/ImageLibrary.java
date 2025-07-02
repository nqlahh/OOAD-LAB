package drawingstudiopro.Library;

import java.util.HashMap;
import java.util.Map;
import javax.swing.*;

public class ImageLibrary {
    private Map<String, ImageIcon> animalImages;
    private Map<String, ImageIcon> flowerImages;
    private Map<String, ImageIcon> customImages;

    public ImageLibrary() {
        animalImages = new HashMap<>();
        flowerImages = new HashMap<>();
        customImages = new HashMap<>();

        loadPredefinedImages();
    }

    private void loadPredefinedImages() {
        animalImages.put("Cat", new ImageIcon("assets/animal/cat.png"));
        flowerImages.put("Rose", new ImageIcon("assets/flower/rose.png"));
        // add more
    }

    public void addCustomImage(String name, ImageIcon image) {
        customImages.put(name, image);
    }

    public ImageIcon getAnimalImage(String name) {
        return animalImages.get(name);
    }

    public ImageIcon getFlowerImage(String name) {
        return flowerImages.get(name);
    }

    public ImageIcon getCustomImage(String name) {
        return customImages.get(name);
    }

    public Map<String, ImageIcon> getAllAnimals() {
        return animalImages;
    }

    public Map<String, ImageIcon> getAllFlowers() {
        return flowerImages;
    }

    public Map<String, ImageIcon> getAllCustomImages() {
        return customImages;
    }
}