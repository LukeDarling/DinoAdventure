package model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

import javafx.beans.binding.Bindings;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;

public class Level {

    private ArrayList<Enemy> enemies = new ArrayList<Enemy>();
    private ArrayList<Block> blocks = new ArrayList<Block>();
    private ArrayList<Collectable> collectables = new ArrayList<Collectable>();
    private Player player;
    private int width = 0;
    private int height = 0;
    private String levelName;
    private LongProperty startTimeProperty = new SimpleLongProperty();
    private LongProperty runTimeProperty = new SimpleLongProperty();
    private LongProperty currentTimeProperty = new SimpleLongProperty();
    private LongProperty maxTimeProperty = new SimpleLongProperty();
    private LongProperty remainingTimeProperty = new SimpleLongProperty();
    private LongProperty idleTimeProperty = new SimpleLongProperty();
    private Point spawnPoint = new Point();

    public Level() {

        player = Game.instance().getPlayer();

        // Setup timer bindings

        runTimeProperty.bind(Bindings.createLongBinding(() -> {
            return currentTimeProperty.get() - (startTimeProperty.get() + idleTimeProperty.get());
        }, currentTimeProperty, startTimeProperty, idleTimeProperty));

        remainingTimeProperty.bind(Bindings.createLongBinding(() -> {
            return maxTimeProperty.get() - runTimeProperty.get();
        }, maxTimeProperty, runTimeProperty));

    }

    public Point getSpawnPoint() {
        return spawnPoint;
    }

    public void setSpawnPoint(Point spawnPoint) {
        this.spawnPoint.copyFrom(spawnPoint);
    }

    public long getMaxTime() {
        return maxTimeProperty.get() / 1000;
    }

    public void setMaxTime(long seconds) {
        maxTimeProperty.set(seconds * 1000);
    }

    public LongProperty maxTimeProperty() {
        return maxTimeProperty;
    }

    public LongProperty idleTimeProperty() {
        return idleTimeProperty;
    }

    public LongProperty remainingTimeProperty() {
        return remainingTimeProperty;
    }

    public void tick() {

        currentTimeProperty.set(System.currentTimeMillis());
        for (Entity e : enemies)
            e.tick();
        player.tick();

    }

    public LongProperty startTimeProperty() {
        return startTimeProperty;
    }

    public LongProperty runTimeProperty() {
        return runTimeProperty;
    }

    public void recordStartTime() {
        startTimeProperty.set(System.currentTimeMillis());
    }

    public void setStartTime(long time) {
        startTimeProperty.set(time);
    }

    public long getStartTime() {
        return startTimeProperty.get();
    }

    public long getRunTime() {
        return runTimeProperty.get();
    }

    /**
     * get the name of the level
     * 
     * @return - levelName
     */
    public String getLevelName() {
        return levelName;
    }

    /**
     * set the name of the level
     * 
     * @param levelName
     */
    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }

    /**
     * find the Entity with the given id
     * 
     * @param id
     * @return Entity
     */
    public Enemy findEntity(int id) {
        for (Enemy entity : enemies) {
            if (entity.getId() == id) {
                return entity;
            }
        }
        return null;
    }

    /**
     * remove the Entity with the id from entities
     * 
     * @param id
     */
    public void removeEntity(Enemy entity) {
        enemies.remove(entity);
    }

    /**
     * find the surface with the given id
     * 
     * @param id
     * @return Block
     */
    public Block findBlock(int id) {
        for (Block block : blocks) {
            if (block.getId() == id) {
                return block;
            }
        }
        return null;
    }

    // /**
    // * find the surface with the given id
    // *
    // * @param id
    // * @return Block
    // */
    // public Collectable findCollectable(int id) {
    // for (Collectable item : collectables) {
    // if (item.getId() == id) {
    // return item;
    // }
    // }
    // return null;
    // }

    /**
     * remove the surface with the given surface id from surfaces
     * 
     * @param id
     */
    public void removeBlock(Block block) {
        blocks.remove(block);
    }

    /**
     * remove the given item from collectables
     * 
     * @param id
     */
    public void removeCollectable(Collectable item) {
        collectables.remove(item);
    }

    /**
     * adds entity to entities
     * 
     * @param object
     */
    public void addEntity(Enemy entity) {
        enemies.add(entity);
    }

    /**
     * adds surface to surfaces
     * 
     * @param object
     */
    public void addBlock(Block block) {
        blocks.add(block);
    }

    /**
     * adds item to collectables
     * 
     * @param object
     */
    public void addCollectable(Collectable item) {
        collectables.add(item);
    }

    /**
     * Gets the surfaces in the level
     * 
     * @return surfaces
     */
    public ArrayList<Block> getBlocks() {
        return blocks;
    }

    /**
     * Gets the Entities in the level
     * 
     * @return surfaces
     */
    public ArrayList<Enemy> getEntites() {
        return enemies;
    }

    /**
     * Gets the items in the level
     * 
     * @return surfaces
     */
    public ArrayList<Collectable> getCollectables() {
        return collectables;
    }

    /**
     * get the width of the world
     * 
     * @return width
     */
    public int getWidth() {
        return width;
    }

    /**
     * set teh width of the world
     * 
     * @param width
     */
    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * get the hight of the world
     * 
     * @return height
     */
    public int getHeight() {
        return height;
    }

    /**
     * Set the hight of the world
     * 
     * @param height
     */
    public void setHeight(int height) {
        this.height = height;
    }

    /**
     * Save the file
     */
    public void save(String filename) throws IOException {
        try (DataOutputStream writer = new DataOutputStream(new FileOutputStream(filename, false))) {
            // write the size of the level
            writer.writeInt(width);
            writer.writeInt(height);
            // write the spawn point
            writer.writeInt(spawnPoint.getIntX());
            writer.writeInt(spawnPoint.getIntY());
            // write how many entities their are
            writer.writeInt(enemies.size());
            // Iterate through the entities saving each's data
            for (int i = 0; i < enemies.size(); ++i) {
                writer.writeInt(enemies.get(i).getId());
                writer.writeUTF(enemies.get(i).getType().toString());
                writer.writeInt(enemies.get(i).centerPoint().getIntX());
                writer.writeInt(enemies.get(i).centerPoint().getIntY());
                // writer.writeInt(entities.get(i).getHeight());
                // writer.writeInt(entities.get(i).getWidth());
            }
            // Write how many blocks there are
            writer.writeInt(blocks.size());
            // Iterate through the blocks saving each's data
            for (int i = 0; i < blocks.size(); ++i) {
                writer.writeInt(blocks.get(i).getId());
                writer.writeUTF(blocks.get(i).getTexture());
                writer.writeInt(blocks.get(i).centerPoint().getIntX());
                writer.writeInt(blocks.get(i).centerPoint().getIntY());
                writer.writeInt(blocks.get(i).getWidth());
                writer.writeInt(blocks.get(i).getHeight());
            }
            // writer.writeInt(collectables.size());
            // // Iterate through the collectables saving each's data
            // for (int i = 0; i < collectables.size(); ++i) {
            // // writer.writeInt(collectables.get(i).getId());
            // writer.writeUTF(collectables.get(i).getType());
            // writer.writeInt(collectables.get(i).centerPoint().getIntX());
            // writer.writeInt(collectables.get(i).centerPoint().getIntY());
            // }
        }
    }

    /**
     * 
     * Load the level
     */
    public void load(String fileName) throws IOException {
        // Set the name of the level
        setLevelName(fileName);
        // clear the level of old items/enemies/blocks
        enemies.clear();
        blocks.clear();
        collectables.clear();
        // Load Playermanager instance from filename.dat binary file
        var reader = new DataInputStream(new FileInputStream(fileName)); // Create loader
        // read the size of the level
        int width = reader.readInt();
        int height = reader.readInt();
        // read and update the spawn point

        int spawnX = reader.readInt();
        int spawnY = reader.readInt();
        
        // read the number of entities
        int sizeOfEntities = reader.readInt();
        // get how many players there are
        for (int i = 0; i < sizeOfEntities; ++i) {
            // iterate over each playing gathering their values
            Enemy entity = new Enemy();
            entity.setId(reader.readInt());
            entity.setType(reader.readUTF());
            entity.centerPoint().setXY(reader.readInt(), reader.readInt());
            entity.setWidth(59);
            entity.setHeight(50);
            enemies.add(entity);
        }

        // get blocks
        int sizeOfBlocks = reader.readInt();
        for (int i = 0; i < sizeOfBlocks; ++i) {
            // iterate over each playing gathering their values
            Block box = new Block();
            box.setId(reader.readInt());
            box.setTexture(reader.readUTF());
            box.centerPoint().setXY(reader.readInt(), reader.readInt());
            box.setWidth(reader.readInt());
            box.setHeight(reader.readInt());
            blocks.add(box);
        }
        //set the spawn point
        setSpawnPoint(new Point(spawnX, spawnY));

        //set the size of the level
        setWidth(width);
        setHeight(height);

        reader.close();
    }

    
    // this was made for serialization the Game model. This method does not save the
    // play in the Player.
    // THe writer passed by the parameter is the one used in the save method in the
    // game class
    public void serialize(DataOutputStream writer) throws IOException {
        writer.writeInt(width);
        writer.writeInt(height);
        int size = enemies.size();
        writer.writeInt(size);
        for (int i = 0; i < size; i++) {
            if (enemies.get(i) instanceof Enemy) {
                Enemy enemy = (Enemy) enemies.get(i);
                writer.writeInt(enemies.get(i).getId());
                enemy.serialize(writer); // This method needs more to be added when there are going to be multiple types
                                         // of enemies in the game.
            }
        }

        //
        writer.writeInt(blocks.size());
        for (int i = 0; i < blocks.size(); ++i) {
            writer.writeInt(blocks.get(i).getId());
            // writer.writeUTF(surfaces.get(i).getType());
            writer.writeInt(blocks.get(i).centerPoint().getIntX());
            writer.writeInt(blocks.get(i).centerPoint().getIntY());
            writer.writeInt(blocks.get(i).getWidth());
            writer.writeInt(blocks.get(i).getHeight());
        }
        if (levelName != null) {
            writer.writeUTF(levelName);
        } else {
            writer.writeUTF("None");
        }
    }

    // this was made for serialization the Game model. This method loads everything
    // that was saved in the serialize method.
    // The reader passed by the parameter is the one used in the load method in the
    // game class.
    public void deserialize(DataInputStream reader) throws IOException {
        width = reader.readInt();
        height = reader.readInt();
        int entitiesSize = reader.readInt();
        for (int i = 0; i < entitiesSize; i++) {
            int id = reader.readInt();
            if (this.findEntity(id) != null) {
                Enemy enemy = (Enemy) this.findEntity(id);
                enemy.deserialize(reader);
            } else {
                Enemy enemy = new Enemy();
                this.addEntity(enemy);
                enemy.deserialize(reader);
                enemies.add(enemy);
            }
        }

        int surfaceSize = reader.readInt();

        for (int i = 0; i < surfaceSize; i++) {
            int id = reader.readInt();
            if (this.findBlock(id) != null) {
                Block block = this.findBlock(id);
                // String Type = reader.readUTF();
                block.centerPoint().setXY(reader.readInt(), reader.readInt());
                block.setWidth(reader.readInt());
                block.setHeight(reader.readInt());
            } else {
                Block block = new Block();
                block.setId(id);
                // String type = reader.readUTF(); //I do really know what or where this would
                // be set for
                block.centerPoint().setXY(reader.readInt(), reader.readInt());
                block.setWidth(reader.readInt());
                block.setHeight(reader.readInt());
                blocks.add(block);
            }
        }
        levelName = reader.readUTF();
    }
}
