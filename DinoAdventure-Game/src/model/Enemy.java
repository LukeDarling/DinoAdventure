//-------------------------------------------------------------
//File:   Enemy.java
//Desc:   Enemy class, extends Entity, and holds the enemies state and type.
//        Holds methods for the enemy to move
//-------------------------------------------------------------

package model;

import java.io.*;
import javafx.beans.property.*;

/**
 * Class that holds a single instance of an enemy
 */
public class Enemy extends Entity implements Living {

    // Holds the state of the enemy
    protected EnemyState state;

    // Holds the type of the enemy
    protected EnemyState type;

    // ticks per second. Controlls how often the physics are updated per second
    protected int tps = 1;

    // Minimum distance from enemy to player to apply physics update
    private double drawDistance = 2202.91;

    // Health property
    protected IntegerProperty healthProperty = new SimpleIntegerProperty();

    // Create an enemy with 1 hp
    public Enemy() {
        setHealth(1);
    }

    /**
     * Create a new enemy at the given location as the given type
     * 
     * @param x    double
     * @param y    double
     * @param type EnemyState
     */
    public Enemy(double x, double y, EnemyState type) {
        centerPoint.xProperty().set(x);
        centerPoint.yProperty().set(y);
        this.type = type;
        if (type == EnemyState.SCHAUB) {
            setWidth(76);
            setHeight(100);
            setHealth(3);
        } else {
            setWidth(59);
            setHeight(50);
            setHealth(1);
        }
        setDirection(EntityDirection.LEFT);
    }

    // Getters/Setters

    public int getHealth() {
        return healthProperty.get();
    }

    public void setHealth(int health) {
        healthProperty.set(health);
    }

    public IntegerProperty healthProperty() {
        return healthProperty;
    }

    public EnemyState getState() {
        return state;
    }

    public void setState(EnemyState state) {
        this.state = state;
    }

    public EnemyState getType() {
        return type;
    }

    public void setType(EnemyState type) {
        this.type = type;
    }

    /**
     * Apply Physics update, appropreit for the type of enemy
     */
    @Override
    public void tick() {

        // Only update the enemy if it's within a certian distance of the player
        if (Game.instance().getPlayer().centerPoint().distanceFrom(this.centerPoint) > drawDistance) {
            return;
        }

        // Apply correct behavior based on type
        switch (type) {

        case WANDERING:
            if (tps / Game.FPS == 1) {
                // Apply specific WanderingEnemy physics/behavior
                if (Game.random.nextInt(10) == 0) {
                    // Randomly every ~X seconds, toggle whether or not the enemy is walking
                    state = (state == EnemyState.STANDING ? EnemyState.WANDERING : EnemyState.STANDING);
                }

                if (Game.random.nextInt(15) == 0) {
                    // Randomly every ~X seconds, toggle which direction the enemy is facing
                    direction = (direction == EntityDirection.LEFT ? EntityDirection.RIGHT : EntityDirection.LEFT);
                }
            }

            if (state == EnemyState.WANDERING && onSurface) {
                if (direction == EntityDirection.LEFT) {
                    xVelocity = Math.max(-maxSpeed, xVelocity - (8 / Game.FPS));
                } else {
                    xVelocity = Math.min(maxSpeed, xVelocity + (8 / Game.FPS));
                }
            }
            break;

        case FOLLOWING:
            if (centerPoint.distanceFrom(Game.instance().getPlayer().centerPoint()) <= 1000) {
                state = EnemyState.FOLLOWING;
                if (centerPoint.getX() - 15 > Game.instance().getPlayer().centerPoint().getX() && onSurface) {
                    xVelocity = Math.max(-maxSpeed, xVelocity - (10 / Game.FPS));
                    direction = EntityDirection.LEFT;
                } else if (centerPoint.getX() + 15 < Game.instance().getPlayer().centerPoint().getX() && onSurface) {
                    xVelocity = Math.min(maxSpeed, xVelocity + (10 / Game.FPS));
                    direction = EntityDirection.RIGHT;
                }
            } else {
                state = EnemyState.STANDING;
            }
            break;

        case FLEEING:
            if (centerPoint.distanceFrom(Game.instance().getPlayer().centerPoint()) <= 500) {
                state = EnemyState.FOLLOWING;
                if (centerPoint.getX() > Game.instance().getPlayer().centerPoint().getX() && onSurface) {
                    xVelocity = Math.min(maxSpeed, xVelocity + (10 / Game.FPS));
                    direction = EntityDirection.RIGHT;
                } else if (centerPoint.getX() <= Game.instance().getPlayer().centerPoint().getX() && onSurface) {
                    xVelocity = Math.max(-maxSpeed, xVelocity - (10 / Game.FPS));
                    direction = EntityDirection.LEFT;
                }
            } else {
                state = EnemyState.STANDING;
            }
            break;

        case JUMPING:
            if (centerPoint.distanceFrom(Game.instance().getPlayer().centerPoint()) <= 1000) {
                state = EnemyState.JUMPING;
                if (centerPoint.getX() - 15 > Game.instance().getPlayer().centerPoint().getX()) {
                    xVelocity = Math.max(-maxSpeed, xVelocity - (5 / Game.FPS));
                    direction = EntityDirection.LEFT;
                } else if (centerPoint.getX() + 15 < Game.instance().getPlayer().centerPoint().getX()) {
                    xVelocity = Math.min(maxSpeed, xVelocity + (5 / Game.FPS));
                    direction = EntityDirection.RIGHT;
                }
                if (onSurface) {
                    yVelocity = -5;
                }
            } else {
                state = EnemyState.STANDING;
            }
            break;

        case SCHAUB:
            if (centerPoint.distanceFrom(Game.instance().getPlayer().centerPoint()) <= 2000) {
                state = EnemyState.SCHAUB;
                if (centerPoint.getX() - 15 > Game.instance().getPlayer().centerPoint().getX()) {
                    xVelocity = Math.max(-maxSpeed, xVelocity - (20 / Game.FPS));
                    direction = EntityDirection.LEFT;
                } else if (centerPoint.getX() + 15 < Game.instance().getPlayer().centerPoint().getX()) {
                    xVelocity = Math.min(maxSpeed, xVelocity + (20 / Game.FPS));
                    direction = EntityDirection.RIGHT;
                }
                if (onSurface) {
                    if (tps / Game.FPS == 1 && Game.random.nextInt(4) > 0) {
                        yVelocity = -15;
                    }
                }
            } else {
                state = EnemyState.STANDING;
            }
            break;

        default:
            break;

        }

        // Apply generic entity physics updates
        super.tick();

        tps++;
        if (tps > Game.FPS) {
            tps = 1;
        }

    }

    /**
     * gets the type of the enemy and returns a string value of it's type
     * 
     * @return String value of enemy type
     */
    public String getTypeString() {
        if (type == EnemyState.WANDERING) {
            return "WANDERING";
        } else if (type == EnemyState.FOLLOWING) {
            return "FOLLOWING";
        } else if (type == EnemyState.STANDING) {
            return "STANDING";
        } else if (type == EnemyState.JUMPING) {
            return "JUMPING";
        } else if (type == EnemyState.FLEEING) {
            return "FLEEING";
        }
        return "";
    }

    // writes the each property to the DataOutputStream passed in the parameters of
    // the enemy to the file to be saved.
    public void serialize(DataOutputStream writer) throws IOException {
        writer.writeInt(centerPoint().getIntX());
        writer.writeInt(centerPoint().getIntY());
        if (state != null) {
            writer.writeBoolean(true);
            writer.writeInt(state.ordinal());
        } else {
            writer.writeBoolean(false);
        }
        if (direction != null) {
            writer.writeBoolean(true);
            writer.writeInt(direction.ordinal());
        } else {
            writer.writeBoolean(false);
        }
        writer.writeInt(healthProperty.intValue());
        if (type != null) {
            writer.writeBoolean(true);
            writer.writeInt(type.ordinal());
        } else {
            writer.writeBoolean(false);
        }
        writer.writeInt(widthProperty.get());
        writer.writeInt(heightProperty.get());
        writer.writeDouble(xVelocity);
        writer.writeDouble(yVelocity);
    }

    // reads the DataOutputStream passed in the parameters and sets the Game model
    // accordingly.
    public void deserialize(DataInputStream reader) throws IOException {
        centerPoint().setXY(reader.readInt(), reader.readInt());
        boolean b = reader.readBoolean();
        if (b == true) {
            state = EnemyState.values()[reader.readInt()];
        }
        b = reader.readBoolean();
        if (b == true) {
            direction = EntityDirection.values()[reader.readInt()];
        }
        healthProperty.setValue(reader.readInt());
        boolean b2 = reader.readBoolean();
        if (b2 == true) {
            type = EnemyState.values()[reader.readInt()];
        }
        widthProperty.set(reader.readInt());
        heightProperty.set(reader.readInt());
        this.setXVelocity(reader.readDouble());
        this.setYVelocity(reader.readDouble());
    }

    public void setType(String type) {
        this.type = EnemyState.valueOf(type);

    }

}
