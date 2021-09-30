package tools;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class IntroPage extends JFrame implements ActionListener {

    // Components of the Form
    private Container c;
    private JLabel title;
    private JLabel title2;
    private JTextField trank;
    private JButton sub;
    private HashMap<String,JTextField> ranking_temp;
    private JTextArea tout;
    private JLabel res;
    private JTextArea resadd;
    private HashMap<String, ArrayList<String>> maps_used;
    private String current_game;
    private int playerN;

    // constructor, to initialize the components
    // with default values.
    public IntroPage(String gameName)
    {

        String game = gameName;
        current_game = game;
        setTitle("Intro for " + game );
        setBounds(300, 90, 1000, 450);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        c = getContentPane();
        c.setLayout(null);

        title = new JLabel("Welcome to this user study!");
        title.setFont(new Font("Arial", Font.PLAIN, 20));
        title.setSize(900, 30);
        title.setLocation(370, 10);
        c.add(title);

        title2 = new JLabel("You will be playing a few levels from the game " + game + " and then you will be giving your opinion on the relative difficulty of each one.");
        title2.setFont(new Font("Arial", Font.PLAIN, 17));
        title2.setSize(960, 30);
        title2.setLocation(15, 30);
        c.add(title2);
        String level = gameName + "_lvl0";
        try {
            BufferedImage myPicture = ImageIO.read(new File("examples/levels_img/" + level + ".png"));
            double multiplier = 550.0/myPicture.getWidth();
            int w = (int) Math.floor(myPicture.getWidth() * multiplier);
            int h = (int) Math.floor(myPicture.getHeight() * multiplier);
            Image tmp = myPicture.getScaledInstance(w, h, Image.SCALE_SMOOTH);
            BufferedImage dimg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

            Graphics2D g2d = dimg.createGraphics();
            g2d.drawImage(tmp, 0, 0, null);
            g2d.dispose();

            JLabel picLabel = new JLabel(new ImageIcon(dimg));
            picLabel.setSize(w, h);
            picLabel.setLocation(30, 90 );
            c.add(picLabel);

        }catch (Exception e){
            e.printStackTrace();
        }

        title = new JLabel("<html>In this game you control a man(seen at the bottom<br> of the image) and your objective is to get to the flag <br>at the top of the screen while avoiding the cars in<br> traffic.<br><br> The red cars move slightly faster than the yellow cars<br><br> Controls:  Arrow Keys</html>");
        title.setFont(new Font("Arial", Font.PLAIN, 17));
        title.setSize(900, 200);
        title.setLocation(589, 85);
        c.add(title);


        title = new JLabel("This study takes around 5 minutes to complete");
        title.setFont(new Font("Arial", Font.PLAIN, 17));
        title.setSize(900, 200);
        title.setLocation(589, 205);
        c.add(title);

        sub = new JButton("Continue");
        sub.setFont(new Font("Arial", Font.PLAIN, 15));
        sub.setSize(150, 20);
        sub.setLocation(700, 340);
        sub.addActionListener(this);
        c.add(sub);


        setVisible(true);
    }

    // method actionPerformed()
    // to get the action performed
    // by the user and act accordingly
    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() == sub) {
            File myObj = new File("src/tracks/singlePlayer/Files/wait.txt");
            try {
                myObj.createNewFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            setVisible(false); //you can't see me!
            dispose();
        }
    }

}

class Reg {

    public static void main(String[] args) throws Exception
    {
        IntroPage f = new IntroPage("freeway");
    }
}