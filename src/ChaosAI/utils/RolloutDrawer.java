package ChaosAI.utils;

import core.vgdl.SpriteGroup;
import core.vgdl.VGDLSprite;
import core.game.ForwardModel;
import core.game.Game;
import core.game.StateObservation;
import ontology.Types;
import tools.Vector2d;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.geom.Line2D;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Map;

/* Usage:
 * Needs a static JFrame named 'frame' in Agent class
 * RolloutDrawer rd = new RolloutDrawer(startObservation, actionList);
 * rd.drawPath or rd.drawRollout
 * JOptionPane.showMessageDialog(Agent.frame, rd, "Title Message", JOptionPane.INFORMATION_MESSAGE);
 */
public class RolloutDrawer extends JComponent {
    private Dimension size;
    private ForwardModel model;
    private int blockSize;
    private double dBlockSize;
    private boolean rollout = false;
    private boolean drawPath = false;
    private ArrayList<Types.ACTIONS> actions;
    private ArrayList<Vector2d> avatarPositions = new ArrayList<>();
    private ArrayList<Vector2d> avatarOrientation = new ArrayList<>();
    private Map<Color, ArrayList<Position>> positionsToDraw;
    public SpriteGroup[] spriteGroups;

    public RolloutDrawer(StateObservation stateObs) {
        try {
            Field f = StateObservation.class.getDeclaredField("model");
            f.setAccessible(true);
            this.model = (ForwardModel) f.get(stateObs);
            this.blockSize = stateObs.getBlockSize();
            this.dBlockSize = (double) this.blockSize;
            this.size = stateObs.getWorldDimension();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void drawField() {
        this.paint();
    }

    /**
     * Does a rollout specified in the given parameters
     * and draws the taken path on the map.
     * @param rolloutActions List of actions that will be used for the rollout
     */
    public void drawRollout(ArrayList<Types.ACTIONS> rolloutActions) {
        this.rollout = true;
        this.actions = rolloutActions;
        this.avatarPositions.add(model.getAvatarPosition());
        this.avatarOrientation.add(model.getAvatarOrientation());
        for(int i=0; i<this.actions.size(); i++){
            model.advance(this.actions.get(i));
            this.avatarPositions.add(model.getAvatarPosition());
            this.avatarOrientation.add(model.getAvatarOrientation());
        }

        this.actions.add(Types.ACTIONS.ACTION_NIL);
        this.paint();
    }

    /**
     * Colors all given Path in the color given in the Map.
     * @param positionsToDraw Map containing the Paths as ArrayLists
     *                        and the color to draw them in
     */
    public void drawPath(Map<Color, ArrayList<Position>> positionsToDraw) {
        this.rollout = false;
        this.drawPath = true;
        this.positionsToDraw = positionsToDraw;

        this.paint();
    }

    public void paintComponent(Graphics gx) {
        Graphics2D g = (Graphics2D) gx;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g.setColor(Types.BLACK);
        g.fillRect(0, size.height, size.width, size.height);

        try {
            int[] gameSpriteOrder = model.getSpriteOrder();
            if (this.spriteGroups != null) for (Integer spriteTypeInt : gameSpriteOrder) {
                if (spriteGroups[spriteTypeInt] != null) {
                    ArrayList<VGDLSprite> spritesList = spriteGroups[spriteTypeInt].getSprites();
                    for (VGDLSprite sp : spritesList) {
                        if (sp != null) sp.draw(g, model);
                    }

                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }

        if(this.rollout) {
            // paint Avatar Path
            g.setColor(Types.BLACK);
            for (int i = 0; i < actions.size() - 1; i++) {
                // get coordinates of the upper left corner of the block the avatar is in
                int xBlock = (int) (avatarPositions.get(i).x / blockSize) * blockSize;
                int yBlock = (int) (avatarPositions.get(i).y / blockSize) * blockSize;

                if (!avatarPositions.get(i + 1).equals(avatarPositions.get(i))) {
                    // avatar will move in this step, draw rectangle at old position
                    g.setColor(Types.BLACK);
                    g.setStroke(new BasicStroke(2));
                    g.drawRect(xBlock, yBlock, blockSize, blockSize);
                    // draw an arrow indicating movement direction
                    double nextXMiddle = (avatarPositions.get(i + 1).x / dBlockSize) * dBlockSize + 0.5 * dBlockSize;
                    double nextYMiddle = (avatarPositions.get(i + 1).y / dBlockSize) * dBlockSize + 0.5 * dBlockSize;
                    g.setColor(new Color(0xFF5500));
                    g.setStroke(new BasicStroke(5));
                    g.draw(new Line2D.Double((double) xBlock + 0.5 * dBlockSize, (double) yBlock + 0.5 * dBlockSize, nextXMiddle, nextYMiddle));

                    if (actions.get(i) == Types.ACTIONS.ACTION_DOWN) {
                        g.draw(new Line2D.Double(nextXMiddle, nextYMiddle, nextXMiddle - 0.33 * dBlockSize, nextYMiddle - 0.33 * dBlockSize));
                        g.draw(new Line2D.Double(nextXMiddle, nextYMiddle, nextXMiddle + 0.33 * dBlockSize, nextYMiddle - 0.33 * dBlockSize));
                    } else if (actions.get(i) == Types.ACTIONS.ACTION_RIGHT) {
                        g.draw(new Line2D.Double(nextXMiddle, nextYMiddle, nextXMiddle - 0.33 * dBlockSize, nextYMiddle - 0.33 * dBlockSize));
                        g.draw(new Line2D.Double(nextXMiddle, nextYMiddle, nextXMiddle - 0.33 * dBlockSize, nextYMiddle + 0.33 * dBlockSize));
                    } else if (actions.get(i) == Types.ACTIONS.ACTION_LEFT) {
                        g.draw(new Line2D.Double(nextXMiddle, nextYMiddle, nextXMiddle + 0.33 * dBlockSize, nextYMiddle - 0.33 * dBlockSize));
                        g.draw(new Line2D.Double(nextXMiddle, nextYMiddle, nextXMiddle + 0.33 * dBlockSize, nextYMiddle + 0.33 * dBlockSize));
                    } else if (actions.get(i) == Types.ACTIONS.ACTION_UP) {
                        g.draw(new Line2D.Double(nextXMiddle, nextYMiddle, nextXMiddle - 0.33 * dBlockSize, nextYMiddle + 0.33 * dBlockSize));
                        g.draw(new Line2D.Double(nextXMiddle, nextYMiddle, nextXMiddle + 0.33 * dBlockSize, nextYMiddle + 0.33 * dBlockSize));
                    }
                } else {
                    // avatar will not move in this step
                    // check for orientation change
                    Vector2d o = avatarOrientation.get(i);
                    Vector2d o2 = avatarOrientation.get(i + 1);
                    if (o != null && o2 != null && !o.equals(o2)) {
                        // orientation changed, draw arc to indicate change
                        double startAngle = 0;
                        double arcAngle = 90;
                        // get start Angle
                        if (o.x == -1.0)
                            startAngle = 180;
                        else if (o.y == 1.0)
                            startAngle = 270;
                        else if (o.y == -1.0)
                            startAngle = 90;
                        // get movement angle
                        if (actions.get(i) == Types.ACTIONS.ACTION_DOWN && startAngle != 180)
                            arcAngle *= -1;
                        else if (actions.get(i) == Types.ACTIONS.ACTION_UP && startAngle == 180)
                            arcAngle *= -1;
                        else if (actions.get(i) == Types.ACTIONS.ACTION_LEFT && startAngle == 270)
                            arcAngle *= -1;
                        else if (actions.get(i) == Types.ACTIONS.ACTION_RIGHT && startAngle != 270)
                            arcAngle *= -1;

                        g.setColor(new Color(0xFF5500));
                        double indent = (0.2 * dBlockSize);
                        Arc2D.Double a = new Arc2D.Double(xBlock + indent, yBlock + indent, blockSize - 2 * indent, blockSize - 2 * indent, startAngle, arcAngle, Arc2D.PIE);
                        g.setStroke(new BasicStroke(5));
                        g.draw(a);
                    } else {
                        // position and orientation were not changed
                        // check for USE action
                        if(i < actions.size())
                        if (actions.get(i) == Types.ACTIONS.ACTION_USE && o != null) {
                            // draw something on the field to indicate a USE action
                            Rectangle rec = new Rectangle(xBlock + (int) o.x * blockSize, yBlock + (int) o.y * blockSize, blockSize, blockSize);

                            g.setColor(Types.RED);
                            g.setStroke(new BasicStroke(5));
                            g.draw(rec);
                            g.draw(new Line2D.Double(rec.getX(), rec.getY(), rec.getX() + dBlockSize, rec.getY() + dBlockSize));
                            g.draw(new Line2D.Double(rec.getX() + dBlockSize, rec.getY(), rec.getX(), rec.getY() + dBlockSize));
                        }
                    }
                }
            }
        } else if(this.drawPath){
            // Draw a path
            for(Map.Entry<Color, ArrayList<Position>> p: this.positionsToDraw.entrySet()) {
                g.setColor(p.getKey());
                for(Position v: p.getValue()) {
                    g.fillRect((int) (v.x * dBlockSize), (int) (v.y * dBlockSize), blockSize, blockSize);
                }
            }
        }

        g.setColor(Types.BLACK);
    }

    /**
     * Gets the spriteGroups from the ForwardModel
     */
    private void paint() {
        try {
            Field f = Game.class.getDeclaredField("spriteGroups");
            f.setAccessible(true);
            SpriteGroup[] spriteGroupsGame = (SpriteGroup[]) f.get(this.model);

            this.spriteGroups = new SpriteGroup[spriteGroupsGame.length];
            for(int i = 0; i < this.spriteGroups.length; ++i)
            {
                this.spriteGroups[i] = new SpriteGroup(spriteGroupsGame[i].getItype());
                this.spriteGroups[i].copyAllSprites(spriteGroupsGame[i].getSprites());
            }


            this.repaint();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public Dimension getPreferredSize() {
        return size;
    }
}
