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
import javax.swing.JFrame;
import javax.swing.WindowConstants;

public class BackgroundApp implements Runnable {

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
        // Create the root of the branch graph
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
        background.setApplicationBounds(new BoundingSphere(new Point3d(),
                1000.0));
        background.setGeometry(createBackGraph());
        objRoot.addChild(background);

        AmbientLight ambientLight = new AmbientLight();
        ambientLight.setInfluencingBounds(new BoundingSphere());
        objRootBG.addChild(ambientLight);

        return objRootBG;
    } // end of CreateSceneGraph method

  /////////////////////////////////////////////////////////
    public BranchGroup createBackGraph() {

        // Create the root of the branch graph
        BranchGroup objRoot = new BranchGroup();

      
     
        
        int[] stripCount = {10};
        LineStripArray montana = new LineStripArray(10,
                LineStripArray.COORDINATES, stripCount);
        montana.setCoordinate(0, new Point3f(0.05f, 0.03f, 0.20f));
        montana.setCoordinate(1, new Point3f(0.1f, 0.0f, 0.20f));
        montana.setCoordinate(2, new Point3f(0.02f, 0.0f, 0.20f));
        montana.setCoordinate(3, new Point3f(0.05f, 0.03f, 0.20f));
        
        Appearance apmon = new Appearance();
        TextureLoader tex=new TextureLoader("mm.jpeg", null);
	apmon.setTexture(tex.getTexture());
        Shape3D mount = new Shape3D(montana,apmon);
        
               
        objRoot.addChild(mount);

        objRoot.compile();
        return objRoot;
    } // end of CreateBackGraph method

    /////////////////////BackgroundApp//////////////////////
    public BackgroundApp() {
        GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();

        Canvas3D canvas3D = new Canvas3D(config);
        canvas3D.setStereoEnable(false);

        // SimpleUniverse is a Convenience Utility class
        SimpleUniverse simpleU = new SimpleUniverse(canvas3D);

        BranchGroup scene = createSceneGraph(simpleU);

    // This will move the ViewPlatform back a bit so the
        // objects in the scene can be viewed.
        simpleU.getViewingPlatform().setNominalViewingTransform();

        simpleU.addBranchGraph(scene);
        new OtherView(simpleU.getLocale()); /* see note below */

        JFrame f = new JFrame("Planetario");
        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        f.add(canvas3D);
        f.pack();
        f.setVisible(true);
        f.setSize(300,300);
    } // end of BackgroundApp (constructor)

    /*
     * This class was created to make the boundingleaf work for this example
     * program. One OtherView object is created just a couple of lines above.
     * Inserting a second frame in the scene makes the BoundingLeaf object work
     * as desired.
     */
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
    } // end of OtherView class

  //  The following allows this to be run as an application
    //  as well as an applet
    public void run() {

    }

    public static void main(String[] args) {
        new BackgroundApp();
    } // end of main (method of BackgroundApp)

} // end of class BackgroundApp
