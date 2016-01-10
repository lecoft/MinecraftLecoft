package minecraft;

import com.sun.j3d.utils.applet.*;
import com.sun.j3d.utils.behaviors.keyboard.*;
import com.sun.j3d.utils.geometry.*;
import com.sun.j3d.utils.image.TextureLoader;
import com.sun.j3d.utils.universe.*;
import java.applet.Applet;
import java.awt.*;
import java.awt.AWTEvent;
import static java.awt.Frame.MAXIMIZED_BOTH;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.imageio.ImageIO;
import javax.media.j3d.*;
import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.event.ChangeEvent;
import javax.vecmath.*;
import javax.swing.event.ChangeListener;

public class Minecraft implements ActionListener, ChangeListener {

    private JProgressBar vida;
    private String textura = "Images\\textura (1).png";
    private String NombreJugador = "Jugador1";
    private JLabel jugador;
    private JButton menu, close, dibuja;
    JButton[] bloquecitos;
    private JPanel infoJugador, PanelMenu, game, PanelDibujo;
    boolean coordenadasOcupadas[][][] = new boolean[15][15][20];
    JButton[][] botonesArreglo = new JButton[15][15];
    JButton[] texturas = new JButton[20];
    BranchGroup objRoot;
    BranchGroup Primero;
    boolean primera = true;
    JSlider js;
    JPanel pintaGrid;
    ArrayList<String> CoorUsadas = new ArrayList<>();


    Shape3D createLand() {
        LineArray landGeom = new LineArray(44, GeometryArray.COORDINATES | GeometryArray.COLOR_3);
        float l = -50.0f;
        for (int c = 0; c < 44; c += 4) {

            landGeom.setCoordinate(c + 0, new Point3f(-50.0f, -0.3f, l));
            landGeom.setCoordinate(c + 1, new Point3f(50.0f, -0.3f, l));
            landGeom.setCoordinate(c + 2, new Point3f(l, -0.3f, -50.0f));
            landGeom.setCoordinate(c + 3, new Point3f(l, -0.3f, 50.0f));
            l += 10.0f;
        }

        Color3f c = new Color3f(0.1f, 0.8f, 0.9f);
        for (int i = 0; i < 44; i++) {
            landGeom.setColor(i, c);
        }

        return new Shape3D(landGeom);
    }

    public BranchGroup createSceneGraph(SimpleUniverse su) {

        BranchGroup objRootBG = new BranchGroup();

        Vector3f translate = new Vector3f();
        Transform3D T3D = new Transform3D();

        translate.set(0.0f, -0.3f, 0.0f);
        T3D.setTranslation(translate);
        TransformGroup objRoot = new TransformGroup(T3D);
        objRootBG.addChild(objRoot);

        //objRoot.addChild(createLand());
        BoundingLeaf boundingLeaf = new BoundingLeaf(new BoundingSphere());

        PlatformGeometry platformGeom = new PlatformGeometry();
        platformGeom.addChild(boundingLeaf);
        platformGeom.compile();
        su.getViewingPlatform().setPlatformGeometry(platformGeom);

        KeyNavigatorBehavior keyNavBeh = new KeyNavigatorBehavior(su.getViewingPlatform().getViewPlatformTransform());
        keyNavBeh.setSchedulingBoundingLeaf(boundingLeaf);
        objRootBG.addChild(keyNavBeh);

        Background background = new Background();
        background.setApplicationBounds(new BoundingSphere(new Point3d(), 1000.0));
        background.setGeometry(createBackGraph());
        objRoot.addChild(background);

        AmbientLight ambientLight = new AmbientLight();
        ambientLight.setInfluencingBounds(new BoundingSphere());
        objRootBG.addChild(ambientLight);
  
        return objRootBG;
    }

    public BranchGroup createBackGraph() {

        // Create the root of the branch graph
        BranchGroup objRoot = new BranchGroup();
        objRoot.compile();
        return objRoot;
    }

    public Minecraft() {
        NombreJugador = setJugador();
        vida = new JProgressBar();
        vida.setValue(100);
        jugador = new JLabel(NombreJugador);
        menu = new JButton("<<");
        dibuja = new JButton("Dibuja");
        close = new JButton("X");
        dibuja.addActionListener(this);
        menu.addActionListener(this);
        close.addActionListener(this);
        // p1.setPreferredSize(new Dimension(250,150));
        infoJugador = new JPanel(new BorderLayout());
        infoJugador.setOpaque(false);
        JPanel estadisticas = new JPanel(new FlowLayout());
        JPanel botones = new JPanel(new FlowLayout());
        estadisticas.add(dibuja);
        estadisticas.add(jugador);
        infoJugador.add(estadisticas, BorderLayout.LINE_START);
        botones.add(menu);
        botones.add(close);
        infoJugador.add(botones, BorderLayout.LINE_END);

        PanelMenu = new JPanel(new GridBagLayout());
        PanelMenu.setVisible(false);

        PanelDibujo = new JPanel(new GridBagLayout());
        PanelDibujo.setVisible(false);

        JPanel blocksGrid = new JPanel(new GridLayout(5, 2));
        pintaGrid = new JPanel(new GridLayout(15, 15));

        GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();

        Canvas3D canvas3D = new Canvas3D(config);
        canvas3D.setStereoEnable(false);

        game = new JPanel();
        game.setVisible(true);
        game.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        SimpleUniverse simpleU = new SimpleUniverse(canvas3D);

        BranchGroup scene = createSceneGraph(simpleU);

        simpleU.getViewingPlatform().setNominalViewingTransform();

        simpleU.addBranchGraph(scene);

        objRoot = new BranchGroup();
        Primero = new BranchGroup();
        Primero.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
        Primero.setCapability(TransformGroup.ALLOW_CHILDREN_WRITE);
        Primero.setCapability(BranchGroup.ALLOW_DETACH);
        objRoot.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
        objRoot.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
        simpleU.addBranchGraph(objRoot);

        new OtherView(simpleU.getLocale()); /* see note below */

        for (int i = 0; i < 20; i++) {
            texturas[i] = new JButton();
            texturas[i].setIcon(new ImageIcon("Images\\textura (" + (i + 1) + ").png"));
            texturas[i].addActionListener(this);
            blocksGrid.add(texturas[i]);
        }

        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.weighty = 0;
        c.gridx = 0;
        c.gridy = 0;
        game.add(infoJugador, c);

        c.fill = GridBagConstraints.BOTH;
        c.weighty = 1;
        c.gridx = 0;
        c.gridy = 1;
        game.add(canvas3D, c);
        JScrollPane blocks = new JScrollPane(blocksGrid);

        JFrame f = new JFrame("Minecraft");

        JLayeredPane lp = f.getLayeredPane();

        f.setUndecorated(true);
        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        f.setExtendedState(MAXIMIZED_BOTH);
        f.pack();
        f.setVisible(true);

        game.setSize(f.getSize());
        PanelMenu.setSize((int) f.getSize().getWidth() / 5, (int) ((f.getSize().getHeight() * 5) / 6));
        PanelMenu.setLocation((int) (f.getSize().getWidth() / 5) * 4, (int) ((f.getSize().getHeight() / 15)));
        PanelDibujo.setLayout(new BorderLayout());
        PanelDibujo.setSize((int) f.getSize().getWidth() * 2 / 5, (int) ((f.getSize().getHeight() * 5) / 6));
        PanelDibujo.setLocation(0, (int) ((f.getSize().getHeight() / 15)));
        blocks.setPreferredSize(new Dimension((int) (PanelMenu.getSize().getWidth() - 20), (int) (PanelMenu.getSize().getHeight() - 30)));
        PanelMenu.add(blocks);
        pintaGrid.setPreferredSize(new Dimension((int) (PanelDibujo.getSize().getWidth() - 20), (int) (PanelDibujo.getSize().getHeight() - 50)));
        PanelDibujo.add(pintaGrid, BorderLayout.NORTH);

        js = new JSlider(-10, 10, 0);
        js.addChangeListener(this);
        js.setMajorTickSpacing(1);
        js.setPaintLabels(true);

        Hashtable tablaHash = new Hashtable();
        tablaHash.put(new Integer(-10), new JLabel("-10"));
        tablaHash.put(new Integer(0), new JLabel("0"));
        tablaHash.put(new Integer(10), new JLabel("10"));
        js.setLabelTable(tablaHash);
        js.setPaintLabels(true);

        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                botonesArreglo[i][j] = new JButton();
                botonesArreglo[i][j].addActionListener(this);
                botonesArreglo[i][j].setBackground(Color.lightGray);
                if (coordenadasOcupadas[i][j][js.getValue() + 10] == true) {
                    botonesArreglo[i][j].setBackground(Color.RED);
                }
                pintaGrid.add(botonesArreglo[i][j]);
            }
        }
        PanelDibujo.add(js, BorderLayout.SOUTH);

        lp.add(game, new Integer(1));
        lp.add(PanelMenu, new Integer(2));
        lp.add(PanelDibujo, new Integer(2));
    }

    public String setJugador() {
        String jugador;
        String opciones[] = {"Aceptar"};
        boolean continuar = false;
        ImageIcon icon = new ImageIcon("Images\\icono.png");
        do {
            jugador = (String) JOptionPane.showInputDialog(null, "Ingresa tu nickname", "Nickname", JOptionPane.INFORMATION_MESSAGE, icon, null, null);
            if (jugador.length() > 0) {
                continuar = true;
            } else {
                JOptionPane.showMessageDialog(null, "Debes de escribir un nickname", "Error", JOptionPane.ERROR_MESSAGE);
                continuar = false;
            }
        } while (!continuar);
        return jugador;
    }

    public void actionPerformed(ActionEvent e) {
        JButton b = (JButton) e.getSource();
        if (b == close) {
            System.exit(0);
        } else if (b == menu) {
            if (PanelMenu.isVisible()) {
                PanelMenu.setVisible(false);
            } else {
                PanelMenu.setVisible(true);
            }
        } else if (b == dibuja) {
            if (PanelDibujo.isVisible()) {
                PanelDibujo.setVisible(false);
            } else {
                PanelDibujo.setVisible(true);
            }
        } else {
            for (int i = 0; i < 15; i++) {
                for (int j = 0; j < 15; j++) {
                    if (b == botonesArreglo[i][j]) {
                        if (botonesArreglo[i][j].getBackground() == Color.RED) {
                            borra(j - 8, i - 8,js.getValue());
                            botonesArreglo[i][j].setBackground(Color.lightGray);
                            coordenadasOcupadas[i][j][js.getValue() + 10] = false;
                        } else {
                            coordenadasOcupadas[i][j][js.getValue() + 10] = true;
                            if (coordenadasOcupadas[i][j][js.getValue() + 10] == true) {
                                botonesArreglo[i][j].setBackground(Color.RED);
                            }
                            insertar(j - 8, i - 8);
                        }
                    }
                }
            }
            for (int i = 0; i < 20; i++) {
                if (b == texturas[i]) {
                    textura = texturas[i].getIcon().toString();
                }
            }
        }

    }

    public void stateChanged(ChangeEvent e) {
        JSlider source = (JSlider) e.getSource();
        if (!source.getValueIsAdjusting()) {
            int z = (int) source.getValue();
            for (int i = 0; i < 15; i++) {
                for (int j = 0; j < 15; j++) {
                    if (coordenadasOcupadas[i][j][z + 10] == true) {
                        botonesArreglo[i][j].setBackground(Color.RED);
                    } else {
                        botonesArreglo[i][j].setBackground(Color.lightGray);
                    }
                    pintaGrid.add(botonesArreglo[i][j]);
                }
            }
        }
    }

    public class OtherView extends Object {

        public TransformGroup vpTrans;

        public OtherView(Locale locale) {
            GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();

            Canvas3D canvas3D = new Canvas3D(config);
            canvas3D.setStereoEnable(false);
            PhysicalBody body = new PhysicalBody();
            PhysicalEnvironment environment = new PhysicalEnvironment();
            View view = new View();
            view.setPhysicalBody(body);
            view.setPhysicalEnvironment(environment);
            BranchGroup vpRoot = new BranchGroup();
            Transform3D viewT3D = new Transform3D();
            viewT3D.set(new Vector3f(0.0f, 0.0f, 2.0f));
            ViewPlatform vp = new ViewPlatform();
            vpTrans = new TransformGroup(viewT3D);
            vpTrans.addChild(vp);
            vpRoot.addChild(vpTrans);
            view.attachViewPlatform(vp);
            locale.addBranchGraph(vpRoot);
        }
    }

    public void insertar(int x, int y) {
        TransformGroup tg = new TransformGroup();
        BranchGroup bg = new BranchGroup();
        Node obj = null;
        //activamos las capacidades necesarias
        tg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        tg.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        tg.setCapability(TransformGroup.ENABLE_PICK_REPORTING);

        //permitimos que el padre lo destruya
        bg.setCapability(BranchGroup.ALLOW_DETACH);

        Appearance ap = new Appearance();
        ap.setCapability(Appearance.ALLOW_TEXTURE_WRITE);
        TextureLoader tex = new TextureLoader(textura, null);
        ap.setTexture(tex.getTexture());

        obj = (Node) new com.sun.j3d.utils.geometry.Box(1f, 1f, 1f, Primitive.GENERATE_TEXTURE_COORDS, ap);

        obj.setCapability(Node.ALLOW_AUTO_COMPUTE_BOUNDS_READ);
        obj.setCapability(Node.ALLOW_BOUNDS_READ);
        obj.setBoundsAutoCompute(true);

        Transform3D t = new Transform3D();
        Vector3f vec = new Vector3f(x * 2f, js.getValue() * 2f, y * 2f);
        t.setTranslation(vec);

        tg.setTransform(t);

        tg.addChild(obj);
        bg.addChild(tg);
        if (!primera) {
            objRoot.removeAllChildren();
            Primero.addChild(bg);
            objRoot.addChild(Primero);
        } else {
            Primero.addChild(bg);
            objRoot.addChild(Primero);
            primera = false;
        }
        CoorUsadas.add(x+","+y+","+js.getValue()); 
    }

    public void borra(int x, int y, int z) {
        for(int i = 0;i<CoorUsadas.size();i++){
            if(CoorUsadas.get(i).equals(x+","+y+","+z)){
                Primero.removeChild(i);
                CoorUsadas.remove(i);  
            }
        }       
    }

    public static void main(String[] args) {
        try {
            for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // If Nimbus is not available, you can set the GUI to another look and feel.
        }
        new Minecraft();
    }

}
