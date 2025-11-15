import java.awt.image.BufferedImage;
import java.io.Serializable;

public class BlockManager implements Serializable{
    static BufferedImage[] Floors= fileHandler.loadPngImagesFromFolder("images/blocks/chairs");
    static BufferedImage[] Chairs= fileHandler.loadPngImagesFromFolder("images/blocks/chairs");
    static BufferedImage[] Tables= fileHandler.loadPngImagesFromFolder("images/blocks/tables");
    static BufferedImage[] Walls= fileHandler.loadPngImagesFromFolder("images/blocks/walls");
    static BufferedImage[] Extras= fileHandler.loadPngImagesFromFolder("images/blocks/extras");
    
    static BufferedImage Rooms= fileHandler.loadPngImagesFromFolder("images/blocks")[0];
    class FloorClass implements Serializable{byte type;
    }
    class ChairClass implements Serializable{byte type;
        Boolean seated=false;
    }
    class TableClass implements Serializable{byte type;
    }
    class WallClass implements Serializable{byte type;
    }
    class ExtrasClass implements Serializable{byte type;
    }
    public void draw(GameMap.TileData tile,int x,int y){
        if(tile.room!=null)panel.addImage(Rooms, x*Main.tileSize, y*Main.tileSize, Main.tileSize/100f, 0);
       

        if(tile.descriptor==null)return;
        if(tile.type==0)
        panel.addImage(Floors[(((FloorClass)(tile.descriptor)).type)*4 + tile.dir], x*Main.tileSize, y*Main.tileSize, Main.tileSize/200f, 0);
        else if(tile.type==1)
        panel.addImage(Chairs[(((ChairClass)(tile.descriptor)).type)*4 + tile.dir], x*Main.tileSize, y*Main.tileSize, Main.tileSize/200f, 0);
        else if(tile.type==2)
        panel.addImage(Tables[(((TableClass)(tile.descriptor)).type)*4 + tile.dir], x*Main.tileSize, y*Main.tileSize, Main.tileSize/200f, 0);
        else if(tile.type==3)
        panel.addImage(Walls[(((WallClass)(tile.descriptor)).type)*4 + tile.dir], x*Main.tileSize, y*Main.tileSize, Main.tileSize/200f, 0);
        else if(tile.type==4)
        panel.addImage(Extras[(((ExtrasClass)(tile.descriptor)).type)*4 + tile.dir], x*Main.tileSize, y*Main.tileSize, Main.tileSize/200f, 0);
       
       
    }
    transient CallbackPanel panel;
    GameMap map;
    public BlockManager(GameMap map,CallbackPanel panel){
    this.map=map;
    this.panel=panel;
    }


    public void AddBlock(int type,int typeType,int dir,int x,int y){
    try{
     if(map.map[x][y].type!=0||map.map[x][y].descriptor!=null)return;
     if(type==0)
     {map.addTile((byte)0,(byte) dir,new FloorClass(), x, y);
      ((FloorClass) map.map[x][y].descriptor).type=(byte)typeType;
     }else if(type==1)
     {map.addTile((byte)1,(byte) dir,new ChairClass(), x, y);
      ((ChairClass) map.map[x][y].descriptor).type=(byte)typeType;
     }else if(type==2)
     {map.addTile((byte)2,(byte) dir,new TableClass(), x, y);
      ((TableClass) map.map[x][y].descriptor).type=(byte)typeType;
     }else if(type==3)
     {map.addTile((byte)3,(byte) dir,new WallClass(), x, y);
      ((WallClass) map.map[x][y].descriptor).type=(byte)typeType;
     }else if(type==4)
     {map.addTile((byte)4,(byte) dir,new ExtrasClass(), x, y);
      ((ExtrasClass) map.map[x][y].descriptor).type=(byte)typeType;
     }
    }catch(Error e){System.out.println(e);}
    }

}