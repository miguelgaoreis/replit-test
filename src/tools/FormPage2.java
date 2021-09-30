package tools;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;

public class FormPage2 extends JFrame implements ActionListener {

    // Components of the Form
    private Container c;
    private JLabel title;
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
    public FormPage2(HashMap<String, ArrayList<String>> maps_used, int _playerN)
    {
        playerN = _playerN;
        this.maps_used = maps_used;
        ranking_temp = new HashMap<>();
        String game = maps_used.keySet().iterator().next();
        current_game = game;
        setTitle("Ranking for " + game );
        setBounds(300, 90, 900, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        c = getContentPane();
        c.setLayout(null);

        title = new JLabel("Please rank the levels from the game " + game + " from 1 to "+ maps_used.get(game).size() + " (1 being the easiest/ " + maps_used.get(game).size() + " the hardest)");
        title.setFont(new Font("Arial", Font.PLAIN, 20));
        title.setSize(900, 60);
        title.setLocation(20, 10);
        c.add(title);


        int offset_w = 0;
        int offset_h = 0;
        int count = 1;
        for(String level : maps_used.get(game)){
            try {
                BufferedImage myPicture = ImageIO.read(new File("examples/levels_img/" + level + ".png"));
                double multiplier = 250.0/myPicture.getWidth();
                int w = (int) Math.floor(myPicture.getWidth() * multiplier);
                int h = (int) Math.floor(myPicture.getHeight() * multiplier);
                Image tmp = myPicture.getScaledInstance(w, h, Image.SCALE_SMOOTH);
                BufferedImage dimg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

                Graphics2D g2d = dimg.createGraphics();
                g2d.drawImage(tmp, 0, 0, null);
                g2d.dispose();

                JLabel picLabel = new JLabel(new ImageIcon(dimg));
                picLabel.setSize(w, h);
                picLabel.setLocation(50+offset_w, 120 + offset_h);
                c.add(picLabel);

                JLabel img_title = new JLabel("Level " + count);
                img_title.setFont(new Font("Arial", Font.PLAIN, 15));
                img_title.setSize(50, 20);
                img_title.setLocation(145 + offset_w , 95 + offset_h);
                c.add(img_title);

                trank = new JTextField();
                trank.setFont(new Font("Arial", Font.PLAIN, 15));
                trank.setSize(30, 20);
                trank.setLocation(147 + offset_w, 260 + offset_h);
                c.add(trank);
                ranking_temp.put(level, trank);

                if(count%3 == 0){
                    offset_h += 230;
                    offset_w = 0;
                }else{
                    offset_w += w + 30;
                }

                count++;
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        sub = new JButton("Submit");
        sub.setFont(new Font("Arial", Font.PLAIN, 15));
        sub.setSize(100, 20);
        sub.setLocation(750, 520);
        sub.addActionListener(this);
        c.add(sub);


        res = new JLabel("");
        res.setFont(new Font("Arial", Font.PLAIN, 20));
        res.setSize(500, 25);
        res.setLocation(300, 520);
        c.add(res);


        setVisible(true);
    }

    // method actionPerformed()
    // to get the action performed
    // by the user and act accordingly
    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() == sub) {
            boolean dup = false;
            Collection<JTextField> temp = ranking_temp.values();
            Set<String> s = new HashSet<String>();
            for(JTextField name : temp) {
                if(isInteger(name.getText(),10)){
                    if(s.add(name.getText()) == false)
                        dup = true;
                    int val = Integer.valueOf(name.getText());
                    System.out.println(val);
                    int max_size = maps_used.values().iterator().next().size();
                    System.out.println(max_size);
                    if(val <1 || val>max_size){
                        dup = true;
                    }
                }
                else{
                    dup = true;
                }
            }
            if (dup){
                res.setText("Please insert valid ranking values");
            }
            else{
                HashMap<String, Integer> final_ranking = new HashMap<>();
                for (String map : ranking_temp.keySet()){
                    int val = Integer.valueOf(ranking_temp.get(map).getText());
                    final_ranking.put(map, val);
                    System.out.println(map + ": " + val);
                }
                ObjectIO objectIO = new ObjectIO();
                objectIO.ResultWriteObjectToFile(final_ranking,"player_"+playerN+"/"+current_game+"Form");
                maps_used.remove(current_game);
                if(maps_used.size()>0){
                    this.setVisible(false);
                    FormPage2 newformpage2 = new FormPage2(maps_used, playerN);
                    newformpage2.setVisible(true);
                }
                else{
                    System.exit(0);
                }
            }
        }
    }

    public static boolean isInteger(String s, int radix) {
        if(s.isEmpty()) return false;
        for(int i = 0; i < s.length(); i++) {
            if(i == 0 && s.charAt(i) == '-') {
                if(s.length() == 1) return false;
                else continue;
            }
            if(Character.digit(s.charAt(i),radix) < 0) return false;
        }
        return true;
    }
}
