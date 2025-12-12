import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
//got image code from linked GitHub
public class SwingControlDemo implements ActionListener {
    private JFrame mainFrame;
    private JLabel statusLabel;
    private JLabel imageLabel;
    private JPanel imagePanel;
    private JPanel dataPanel;
    private JPanel searchPanel;
    private JPanel controlPanel;
    private JMenuBar mb;
    private JMenu file, edit, help;
    private JMenuItem cut, copy, paste, selectAll;
    private JTextArea ta; //typing area
    private int WIDTH = 800;
    private int HEIGHT = 700;
    private boolean issearch = false;
    private JPanel doublepanel;
    private JPanel infopanel;
    //api starts on one, got error if it wasn't on one
    int pokemon = 1;
    String pokemonstring = "";
    String findpokemon = "";
    private String currentImageUrl = "";

    public SwingControlDemo() throws ParseException, IOException {
        prepareGUI();
        pull();
        addImage();
    }


    public void pull() throws ParseException {

        String output = "abc";
        String totlaJson = "";
        pokemonstring = ta.getText().substring(9);
        try {
            URL url = new URL("https://pokeapi.co/api/v2/pokemon/");
            //this checks if it is a search, or if it is from clicking the ok button. this is used to proc either the pull in pokemonstring or pokemon.
            if (!issearch) {
                url = new URL("https://pokeapi.co/api/v2/pokemon/" + pokemon);
                System.out.println("used pokemon: " + pokemon);
                System.out.println(pokemonstring = "pokemonstring");
            }
            else {
                url = new URL("https://pokeapi.co/api/v2/pokemon/" + pokemonstring);
                System.out.println("used pokemonstring: " + pokemonstring);
            }
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            //if you get the error 403, uncomment line of code below
            //conn.setRequestProperty("User-Agent", "Mozilla/5.0"); // Add User-Agent


            if (conn.getResponseCode() != 200) {

                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));


            System.out.println("Output from Server .... \n");
            while ((output = br.readLine()) != null) {
                //System.out.println(output);
                totlaJson += output;
            }

            conn.disconnect();

        } catch (MalformedURLException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONParser parser = new JSONParser();
        //System.out.println(str);
        org.json.simple.JSONObject jsonObject = (org.json.simple.JSONObject) parser.parse(totlaJson);
        //System.out.println(jsonObject);

        try {
            infopanel.removeAll();
            String name = (String) jsonObject.get("name");
            JLabel pokename = new JLabel(name.toUpperCase());
            pokename.setFont(new Font("Ariel", Font.BOLD, 24));
            //defining longs for use with api collection for every pull
            long basexp;
            long height;
            long ident;
            long weight;
            //uses the things like height and turns them into data from the json object jsonObject
            height = (long) jsonObject.get("height");
            weight = (long) jsonObject.get("weight");
            basexp = (long) jsonObject.get("base_experience");
            ident = (long) jsonObject.get("id");
            System.out.println("BASEXP: " + basexp);

            infopanel.setAlignmentX(Component.CENTER_ALIGNMENT);
            //I added some spacing to make it clear when things were new
            infopanel.add(Box.createVerticalStrut(20));
            infopanel.add(pokename);
            infopanel.add(Box.createVerticalStrut(15));
            JLabel ability = new JLabel("Abilities");
            infopanel.add(ability);
            ability.setFont(new Font("Ariel", Font.BOLD, 15));
            infopanel.add(Box.createVerticalStrut(15));
            //because things are order linearly, I had to write the other half of the code after the for loop. took a while to figure out
            org.json.simple.JSONArray abilities = (org.json.simple.JSONArray) jsonObject.get("abilities");
            int n2 = abilities.size();
            org.json.simple.JSONObject sprites = (org.json.simple.JSONObject) jsonObject.get("sprites");
            currentImageUrl = (String) sprites.get("front_default");
            System.out.println("Image URL: " + currentImageUrl);

//            }
            for (int i = 0; i < n2; i++) {
                org.json.simple.JSONObject abilityContainer = (org.json.simple.JSONObject) abilities.get(i);
                //only find ability
                org.json.simple.JSONObject abilityDetails = (org.json.simple.JSONObject) abilityContainer.get("ability");

                // 4. Get the name from the ability object in teh array
                String abilityName = (String) abilityDetails.get("name");
                System.out.println("ability " + (i + 1) + ": " + abilityName);
                infopanel.add(Box.createVerticalStrut(15));
                infopanel.add(new JLabel("Ability " + (i + 1) + "- " + abilityName));
                //comment later
            }

            infopanel.add(Box.createVerticalStrut(20));
            JLabel stats = new JLabel("Stats");
            infopanel.add(stats);
            stats.setFont(new Font("Ariel", Font.BOLD, 15));
            infopanel.add(Box.createVerticalStrut(15));
            JLabel xp = new JLabel("Base Experience: " + basexp);
            infopanel.add(xp);
            infopanel.add(Box.createVerticalStrut(15));
            JLabel hight = new JLabel("Height: " + height);
            infopanel.add(hight);
            infopanel.add(Box.createVerticalStrut(15));
            JLabel id = new JLabel("Id: " + ident);
            infopanel.add(id);
            infopanel.add(Box.createVerticalStrut(15));
            JLabel mass = new JLabel("Weight: " + weight);
            infopanel.add(mass);
            //redoes the painting everytime to make sure that the residual images and text from the previous pokemon arent there
            infopanel.revalidate();
            infopanel.repaint();
            //System.out.println(name);
            //System.out.println(hair_color);
            //System.out.println(starship);
        } catch (Exception e) {
            e.printStackTrace();
            //removes stuf if there is an error, needd this for try catch.
            infopanel.removeAll();
            infopanel.add(new JLabel("ERROR: Fix your code"));
            infopanel.revalidate();
            infopanel.repaint();
        }


    }

    public static void main(String[] args) throws IOException, ParseException {
        SwingControlDemo swingControlDemo = new SwingControlDemo();
        swingControlDemo.addImage();
    }

    private void prepareGUI() {
        mainFrame = new JFrame("Pokidex");
        mainFrame.setSize(WIDTH, HEIGHT);
        mainFrame.setLayout(new BorderLayout());

        //menu at top
        cut = new JMenuItem("cut");
        copy = new JMenuItem("copy");
        paste = new JMenuItem("paste");
        selectAll = new JMenuItem("selectAll");
        cut.addActionListener(this);
        copy.addActionListener(this);
        paste.addActionListener(this);
        selectAll.addActionListener(this);

        mb = new JMenuBar();
        file = new JMenu("File");
        edit = new JMenu("Edit");
        help = new JMenu("Help");
        edit.add(cut);
        edit.add(copy);
        edit.add(paste);
        edit.add(selectAll);
        mb.add(file);
        mb.add(edit);
        mb.add(help);
        //end menu at top

        ta = new JTextArea("Pokemon: ");
        ta.setBounds(50, 5, WIDTH - 100, HEIGHT - 50);
        mainFrame.add(mb);  //add menu bar

        mainFrame.setJMenuBar(mb); //set menu bar

        statusLabel = new JLabel("pokemon:", JLabel.CENTER);
        statusLabel.setSize(350, 100);

        mainFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowEvent) {
                System.exit(0);
            }
        });
        imagePanel = new JPanel();
        searchPanel = new JPanel();
        searchPanel.setLayout(new BorderLayout());
        controlPanel = new JPanel();
        doublepanel = new JPanel();
        doublepanel.setLayout(new GridLayout(1,2));
        infopanel = new JPanel();
        infopanel.setLayout(new BoxLayout(infopanel, BoxLayout.Y_AXIS));
        controlPanel.setLayout(new FlowLayout()); //set the layout of the pannel


        JButton okButton = new JButton("OK");
        okButton.setActionCommand("OK");
        okButton.addActionListener(new ButtonClickListener());
        searchPanel.add(okButton, BorderLayout.EAST);

        searchPanel.add(ta, BorderLayout.CENTER);//add typing area
        JButton nextButton = new JButton("Next");
        JButton backButton = new JButton("Back");
        JButton quitButton = new JButton("Quit");

        nextButton.setActionCommand("Next");
        backButton.setActionCommand("Back");
        quitButton.setActionCommand("Quit");

        nextButton.addActionListener(new ButtonClickListener());
        backButton.addActionListener(new ButtonClickListener());
        quitButton.addActionListener(new ButtonClickListener());
        //the double panel is a double panel with the image label and the info panel. I made it a grid so that users could easily see both things
        controlPanel.add(nextButton, BorderLayout.CENTER);
        controlPanel.add(backButton, BorderLayout.WEST);
        controlPanel.add(quitButton, BorderLayout.EAST);
        mainFrame.add(searchPanel, BorderLayout.NORTH);
        mainFrame.add(controlPanel, BorderLayout.SOUTH);
        mainFrame.add(doublepanel, BorderLayout.CENTER);
        doublepanel.add(imagePanel, BorderLayout.WEST);
        doublepanel.add(infopanel, BorderLayout.EAST);
        mainFrame.setVisible(true);
    }

    private void addImage() throws IOException {
        try {
            String path = "";
            if (!ta.getText().contains("http")) {
                path = currentImageUrl;
            } else {
                path = ta.getText();
                if (path.contains("url")) {
                    path = path.substring(path.indexOf("http"));
                }
            }


            URL url = new URL(path);
            BufferedImage ErrorImage = ImageIO.read(new File("Error.png"));
            BufferedImage inputImageBuff = ImageIO.read(url.openStream());


            ImageIcon inputImage;
            if (inputImageBuff != null) {
                //image scaled down to fit by 2/3 from the og
                inputImage = new ImageIcon(inputImageBuff.getScaledInstance(533, 467, Image.SCALE_SMOOTH));
                //sets the image to the image
                if (inputImage != null) {
                    imageLabel = new JLabel(inputImage);
                } else {
                    imageLabel = new JLabel(new ImageIcon(ErrorImage.getScaledInstance(800, 589, Image.SCALE_SMOOTH)));

                }
                imagePanel.removeAll();
                imagePanel.repaint();

                imagePanel.add(imageLabel);

            } else {
                imageLabel = new JLabel(new ImageIcon(ErrorImage.getScaledInstance(800, 589, Image.SCALE_SMOOTH)));

            }

        } catch (IOException e) {
            System.out.println(e);
            System.out.println("sadness");
            BufferedImage ErrorImage = ImageIO.read(new File("Error.png"));
            JLabel imageLabel = new JLabel(new ImageIcon(ErrorImage.getScaledInstance(800, 589, Image.SCALE_SMOOTH)));

            imagePanel.removeAll();
            imagePanel.repaint();
            imagePanel.add(imageLabel);



        }


        mainFrame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == cut)
            ta.cut();
        if (e.getSource() == paste)
            ta.paste();
        if (e.getSource() == copy)
            ta.copy();
        if (e.getSource() == selectAll)
            ta.selectAll();
    }



    private class ButtonClickListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();

            if (command.equals("OK")) {
                try {
                    //sends stuff to pull, half the logic for deciding what image to do is here, and the other half is in the pull
                    System.out.println("anothertestprintok");
                    System.out.println("where is teh poke" + pokemonstring);
                    issearch = true;
                    pull();
                    addImage();
                } catch (IOException | ParseException ex) {
                    ex.printStackTrace();
                }
            }
                if (command.equals("Next")) {
                    try {
                        System.out.println("testprint");
                        //edge case of forward from 1026, the highest id
                        if(pokemon == 1026) {
                            pokemon = 1;
                        }
                        pokemon++;
                        issearch = false;
                        pull();
                        addImage();
                        System.out.println(pokemon);
                    } catch (ParseException | IOException ex) {
                        ex.printStackTrace();
                    }
                }

            if (command.equals("Back")) {
                try {
                    System.out.println("testprint");
                    //oppsite edge case
                    if(pokemon == 1) {
                        pokemon = 1026;
                    }
                    pokemon--;
                    pull();
                    addImage();
                    System.out.println(pokemon);


                } catch (ParseException ex) {
                    ex.printStackTrace();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
            //quit project
            if (command.equals("Quit")) {
                System.exit(0);
            }
                // statusLabel.setText("Ok Button clicked.")
        }
    }
}
