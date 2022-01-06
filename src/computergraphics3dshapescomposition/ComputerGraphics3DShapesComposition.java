/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package computergraphics3dshapescomposition;

import com.sun.j3d.utils.behaviors.keyboard.KeyNavigatorBehavior;
import com.sun.j3d.utils.behaviors.vp.OrbitBehavior;
import com.sun.j3d.utils.behaviors.vp.ViewPlatformAWTBehavior;
import com.sun.j3d.utils.geometry.Cylinder;
import com.sun.j3d.utils.geometry.Primitive;
import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.universe.PlatformGeometry;
import com.sun.j3d.utils.universe.SimpleUniverse;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GraphicsConfiguration;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Enumeration;
import javax.media.j3d.Appearance;
import javax.media.j3d.Behavior;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.Light;
import javax.media.j3d.Material;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.WakeupOnCollisionEntry;
import javax.media.j3d.WakeupOnCollisionExit;
import javax.swing.JFrame;
import javax.vecmath.AxisAngle4f;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

/**
 *
 * @author Plamenna Petrova
 */
public class ComputerGraphics3DShapesComposition extends JFrame implements KeyListener {

    private SimpleUniverse universe = null;
    private TransformGroup tg = null;
    private TransformGroup tg_tink = null;
    private Transform3D t3d_tink = null;
    private Transform3D t3dstep = new Transform3D();
    private BoundingSphere bounds;

    public ComputerGraphics3DShapesComposition() {
        setLayout(new BorderLayout());
        GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
        Canvas3D canvas = new Canvas3D(config);
        add("Center", canvas);
        universe = new SimpleUniverse(canvas);

        BranchGroup scene = createSceneGraph();

        universe.getViewingPlatform().setNominalViewingTransform();

        canvas.addKeyListener(this);

        universe.addBranchGraph(scene);

        //  canvas.addKeyListener(this);        
        OrbitBehavior orbitBehavior = new OrbitBehavior(canvas, ViewPlatformAWTBehavior.KEY_LISTENER | OrbitBehavior.REVERSE_ROTATE | OrbitBehavior.REVERSE_TRANSLATE | OrbitBehavior.PROPORTIONAL_ZOOM);
        orbitBehavior.setSchedulingBounds(new BoundingSphere(new Point3d(0, 0, 0), 100));
        universe.getViewingPlatform().setViewPlatformBehavior(orbitBehavior);//движи изгледа около точка с натискането на мишката
        // извърщва ротация транслация и мащабиране
    }

    private BranchGroup createSceneGraph() {
        BranchGroup objRoot = new BranchGroup();

        TransformGroup viewtrans = new TransformGroup();

        // BoundingSphere bounds = new BoundingSphere(new Point3d(), 10000.0);
        bounds = new BoundingSphere(new Point3d(), 10000.0);

        viewtrans = universe.getViewingPlatform().getViewPlatformTransform();

        KeyNavigatorBehavior keyNavBeh = new KeyNavigatorBehavior(viewtrans);
        keyNavBeh.setSchedulingBounds(bounds);
        PlatformGeometry platformGeom = new PlatformGeometry();
        platformGeom.addChild(keyNavBeh);
        universe.getViewingPlatform().setPlatformGeometry(platformGeom);

        objRoot.addChild(createBall());
        objRoot.addChild(createColorCube());

        return objRoot;
    }

    private BranchGroup createBall() {

        BranchGroup objRoot = new BranchGroup();
        TransformGroup tg = new TransformGroup();

        tg_tink = new TransformGroup();
        t3d_tink = new Transform3D();

        tg_tink.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

        t3d_tink.setTranslation(new Vector3d(-1.2, -1.4, -2.1));
        t3d_tink.setRotation(new AxisAngle4f(0.0f, 1.0f, 0.0f, 1.25f));
        t3d_tink.setScale(0.9);

        tg_tink.setTransform(t3d_tink);

        Appearance ap = new Appearance();
        Sphere shape
                = new Sphere(0.4f, Primitive.GENERATE_NORMALS | Primitive.GENERATE_TEXTURE_COORDS, 50, ap);

        tg_tink.addChild(shape);

        CollisionDetectorGroup cdGroup = new CollisionDetectorGroup(tg_tink);
        cdGroup.setSchedulingBounds(bounds);

        tg.addChild(tg_tink);
        // objRoot.addChild(tg_tink);
        tg.addChild(cdGroup);

        objRoot.addChild(tg);
        objRoot.addChild(createLight());

        objRoot.compile();

        return objRoot;

    }

    private BranchGroup createColorCube() {

        BranchGroup objRoot = new BranchGroup();
        TransformGroup tg = new TransformGroup();
        Transform3D t3d = new Transform3D();

        t3d.setTranslation(new Vector3d(1.2, -1.3, -2.1));
        t3d.setRotation(new AxisAngle4f(0.0f, 1.0f, 0.0f, 2.24f));
        t3d.setScale(0.6);

        tg.setTransform(t3d);

        Appearance appearance = new Appearance();

        Material material = new Material();

        Color3f lightColor = new Color3f(Color.BLUE);

        Color3f darkColor = new Color3f(Color.GREEN);

        material.setEmissiveColor(lightColor);

        material.setDiffuseColor(darkColor);

        material.setSpecularColor(new Color3f(Color.RED));

        appearance.setMaterial(material);

        Cylinder cylinder = new Cylinder(5, 10, appearance);

        tg.addChild(cylinder);

        objRoot.addChild(tg);
        objRoot.addChild(createLight());

        objRoot.compile();

        return objRoot;

    }

    private Light createLight() {
        DirectionalLight light = new DirectionalLight(true, new Color3f(1.0f, 1.0f, 1.0f),
                new Vector3f(-0.3f, 0.2f, -1.0f));

        light.setInfluencingBounds(new BoundingSphere(new Point3d(), 10000.0));

        return light;
    }

    public void keyTyped(KeyEvent e) {
        char key = e.getKeyChar();

        if (key == 'q') {
            t3dstep.set(new Vector3d(0.0, 0.0, 0.1));
            tg_tink.getTransform(t3d_tink);
            t3d_tink.mul(t3dstep);
            tg_tink.setTransform(t3d_tink);
        }

    }

    public void keyReleased(KeyEvent e) {
    }

    public void keyPressed(KeyEvent e) {
    }

    class CollisionDetectorGroup extends Behavior {

        private boolean inCollision = false;
        private TransformGroup group;

        private WakeupOnCollisionEntry wEnter;
        private WakeupOnCollisionExit wExit;

        public CollisionDetectorGroup(TransformGroup gp) { // Corrected: gp
            group = gp; // Corrected: gp
            inCollision = false;

        }

        public void initialize() {
            wEnter = new WakeupOnCollisionEntry(group);
            wExit = new WakeupOnCollisionExit(group);
            wakeupOn(wEnter);
        }

        public void processStimulus(Enumeration criteria) {

            inCollision = !inCollision;
            if (inCollision) {

                t3dstep.set(new Vector3d(0.0, 0.0, -6.0));
                tg_tink.getTransform(t3d_tink);
                t3d_tink.mul(t3dstep);
                tg_tink.setTransform(t3d_tink);

                wakeupOn(wExit);
            } else {
                wakeupOn(wEnter);
            }
        }
    }

    public static void main(String[] args) {
        ComputerGraphics3DShapesComposition opr = new ComputerGraphics3DShapesComposition();
        opr.setSize(500, 500);
        opr.setVisible(true);
    }
}
