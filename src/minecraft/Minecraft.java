package minecraft;

import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
import com.sun.j3d.utils.applet.*;
import com.sun.j3d.utils.universe.*;
import com.sun.j3d.utils.geometry.*;
import javax.media.j3d.*;
import javax.vecmath.*;
import java.awt.AWTEvent;
import java.util.Enumeration;
import com.sun.j3d.utils.behaviors.keyboard.*;
import com.sun.j3d.utils.image.TextureLoader;
import static java.awt.Frame.MAXIMIZED_BOTH;
import javax.swing.*;

public class Minecraft implements Runnable, ActionListener {
    
    private JProgressBar vida;
    private JLabel jugador;
    private JButton menu,close;
    private JPanel infoJugador,PanelMenu;
       
    Shape3D createLand() {
        LineArray landGeom = new LineArray(44, GeometryArray.COORDINATES | GeometryArray.COLOR_3);
        float l = -50.0f;
        for (int c = 0; c < 44; c += 4) {

            landGeom.setCoordinate(c + 0, new Point3f(-50.0f, 0.0f, l));
            landGeom.setCoordinate(c + 1, new Point3f(50.0f, 0.0f, l));
            landGeom.setCoordinate(c + 2, new Point3f(l, 0.0f, -50.0f));
            landGeom.setCoordinate(c + 3, new Point3f(l, 0.0f, 50.0f));
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

        objRoot.addChild(createLand());

        BoundingLeaf boundingLeaf = new BoundingLeaf(new BoundingSphere());

        PlatformGeometry platformGeom = new PlatformGeometry();
        platformGeom.addChild(boundingLeaf);
        platformGeom.compile();
        su.getViewingPlatform().setPlatformGeometry(platformGeom);

        KeyNavigatorBehavior keyNavBeh = new KeyNavigatorBehavior(su.getViewingPlatform().getViewPlatformTransform());
        keyNavBeh.setSchedulingBoundingLeaf(boundingLeaf);
        objRootBG.addChild(keyNavBeh);

        Background background = new Background();
        background.setApplicationBounds(new BoundingSphere(new Point3d(),1000.0));
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
       
        int[] stripCount = {10};
        LineStripArray montana = new LineStripArray(10,LineStripArray.COORDINATES, stripCount);
        montana.setCoordinate(0, new Point3f(0.05f, 0.03f, 0.20f));
        montana.setCoordinate(1, new Point3f(0.1f, 0.0f, 0.20f));
        montana.setCoordinate(2, new Point3f(0.02f, 0.0f, 0.20f));
        montana.setCoordinate(3, new Point3f(0.05f, 0.03f, 0.20f));
        
        /*Appearance apmon = new Appearance();
        TextureLoader tex=new TextureLoader("mm.jpeg", null);
	apmon.setTexture(tex.getTexture());
        Shape3D mount = new Shape3D(montana,apmon);
        
               
        objRoot.addChild(mount);*/

        objRoot.compile();
        return objRoot;
    } 
    
    public Minecraft() {
        vida = new JProgressBar();
        vida.setValue(100);
        jugador = new JLabel("Nombre_Jugador");
        menu = new JButton("<<");
        close = new JButton("X");
        menu.addActionListener(this);
        close.addActionListener(this);
        // p1.setPreferredSize(new Dimension(250,150));
        infoJugador = new JPanel(new BorderLayout());
        infoJugador.setOpaque(false);
        JPanel estadisticas = new JPanel(new FlowLayout());
        JPanel botones = new JPanel(new FlowLayout());
        estadisticas.add(jugador);
        estadisticas.add(vida);
        infoJugador.add(estadisticas,BorderLayout.LINE_START);
        botones.add(menu);
        botones.add(close);       
        infoJugador.add(botones,BorderLayout.LINE_END);
      
        PanelMenu = new JPanel(new FlowLayout());
        PanelMenu.setVisible(true);

        GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();

        Canvas3D canvas3D = new Canvas3D(config);
        canvas3D.setStereoEnable(false);
        
        JPanel game=new JPanel();
        game.setVisible(true);
        game.setLayout(new GridBagLayout());
        GridBagConstraints c=new GridBagConstraints();

        SimpleUniverse simpleU = new SimpleUniverse(canvas3D);

        BranchGroup scene = createSceneGraph(simpleU);

        simpleU.getViewingPlatform().setNominalViewingTransform();

        simpleU.addBranchGraph(scene);
        new OtherView(simpleU.getLocale()); /* see note below */
        
        c.fill=GridBagConstraints.HORIZONTAL;
        c.weightx=1;
        c.weighty=0;
        c.gridx=0;
        c.gridy=0;
        game.add(infoJugador,c);
        
        c.fill=GridBagConstraints.BOTH;
        c.weighty=1;
        c.gridx=0;
        c.gridy=1;
        game.add(canvas3D,c);

        JFrame f = new JFrame("Minecraft");
        
        JLayeredPane lp=f.getLayeredPane();
        
        f.setUndecorated(true);
        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        f.setExtendedState(MAXIMIZED_BOTH); 
        f.pack();
        f.setVisible(true);
        
        game.setSize(f.getSize());
        lp.add(game,0);
        lp.add(PanelMenu, 1);
        PanelMenu.setSize(200, 700);
        PanelMenu.setLocation(500, 20);
        PanelMenu.setBackground(Color.yellow);
        
    }

   
    public void actionPerformed(ActionEvent e) {
      JButton b = (JButton) e.getSource();
      if(b == close){
           System.exit(0); 
      }
      else if(b == menu){
          if(PanelMenu.isVisible())
            PanelMenu.setVisible(false);
          else
            PanelMenu.setVisible(true);
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

    public void run() {

    }
    public static void main(String[] args) {
        new Minecraft();
    } 

} 
