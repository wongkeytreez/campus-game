import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
public class fileHandler {
    public static class SaveStruct implements Serializable {
        public SaveStruct(
        NPCManager.NPC[] npcs,
        RoomManager.Room[] rooms,
        LessonManager.Lesson[] lessons,
        GameMap.TileData[][] map){
this.npcs=npcs;
this.rooms=rooms;
this.lessons=lessons;
this.map=map;
        }
        NPCManager.NPC[] npcs;
        RoomManager.Room[] rooms;
        LessonManager.Lesson[] lessons;
        GameMap.TileData[][] map;
    }
    public static void saveFile(SaveStruct save, String path) {
        try{
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(path+".sav"))) {
            out.writeObject(save);
        }
    } catch(Exception e){System.out.println(e);}
    }

    public static SaveStruct loadFile(String path) {
        try {
            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(path+".sav"))) {
            return (SaveStruct) in.readObject();
        }
 } catch(Exception e){System.out.println(e);}
        return null;
        
    }
    public static BufferedImage[] loadPngImagesFromFolder(String folderPath) {
    File folder = new File(folderPath);
    File[] files = folder.listFiles();
    if (files == null) return new BufferedImage[0];

    return java.util.Arrays.stream(files)
        .filter(file -> file.isFile())
        .filter(file -> {
            String name = file.getName().toLowerCase();
            return name.endsWith(".png") || name.endsWith(".png");
        })
        .map(file -> {
            try {
                return ImageIO.read(file);
            } catch (Exception e) {
                System.err.println("Failed to load: " + file.getName());
                return null;
            }
        })
        .filter(img -> img != null)
        .toArray(BufferedImage[]::new);
}
}
