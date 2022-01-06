/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package computergraphics3dshapescomposition;

import com.sun.j3d.utils.behaviors.keyboard.KeyNavigatorBehavior;
import com.sun.j3d.utils.behaviors.vp.OrbitBehavior;
import com.sun.j3d.utils.behaviors.vp.ViewPlatformAWTBehavior;
import com.sun.j3d.utils.geometry.Box;
import com.sun.j3d.utils.geometry.Cone;
import com.sun.j3d.utils.geometry.Cylinder;
import com.sun.j3d.utils.geometry.Primitive;
import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.image.TextureLoader;
import com.sun.j3d.utils.universe.PlatformGeometry;
import com.sun.j3d.utils.universe.SimpleUniverse;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GraphicsConfiguration;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Vector;
import javax.imageio.ImageIO;
import javax.media.j3d.Alpha;
import javax.media.j3d.Appearance;
import javax.media.j3d.Background;
import javax.media.j3d.Behavior;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.Light;
import javax.media.j3d.Material;
import javax.media.j3d.RotationInterpolator;
import javax.media.j3d.Texture;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.WakeupOnCollisionEntry;
import javax.media.j3d.WakeupOnCollisionExit;
import javax.swing.JFrame;
import javax.vecmath.AxisAngle4f;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

/**
 *
 * @author Plamenna Petrova
 */
public class ComputerGraphics3DShapesComposition extends JFrame  {

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

        universe.addBranchGraph(scene);

        OrbitBehavior orbitBehavior = new OrbitBehavior(canvas, ViewPlatformAWTBehavior.KEY_LISTENER | OrbitBehavior.REVERSE_ROTATE | OrbitBehavior.REVERSE_TRANSLATE | OrbitBehavior.PROPORTIONAL_ZOOM);
        orbitBehavior.setSchedulingBounds(new BoundingSphere(new Point3d(0, 0, 0), 100));
        universe.getViewingPlatform().setViewPlatformBehavior(orbitBehavior);
    }

    private BranchGroup createSceneGraph() {
        BranchGroup objRoot = new BranchGroup();

        TransformGroup viewtrans = new TransformGroup();

        // BoundingSphere bounds = new BoundingSphere(new Point3d(), 10000.0);
        bounds = new BoundingSphere(new Point3d(), 15000.0);

        viewtrans = universe.getViewingPlatform().getViewPlatformTransform();

        KeyNavigatorBehavior keyNavBeh = new KeyNavigatorBehavior(viewtrans);
        keyNavBeh.setSchedulingBounds(bounds);
        PlatformGeometry platformGeom = new PlatformGeometry();
        platformGeom.addChild(keyNavBeh);
        universe.getViewingPlatform().setPlatformGeometry(platformGeom);

        objRoot.addChild(createConeWithNestedSphere());
        objRoot.addChild(createCylinder());

        Background sceneBackground = new Background(0.5f, 1.0f, 1.0f);
        URL imageBackground = getClass().getClassLoader().getResource("space.jpg");
        sceneBackground.setImage(new TextureLoader(imageBackground, this).getImage());
        sceneBackground.setImageScaleMode(Background.SCALE_FIT_ALL);
        sceneBackground.setApplicationBounds(bounds);

        objRoot.addChild(sceneBackground);

        return objRoot;
    }

    public void initiateRotation(int rotationSpeed, TransformGroup targetTransformGroup) {
        Alpha e = new Alpha(-1, rotationSpeed);
        RotationInterpolator selfSpin = new RotationInterpolator(e, targetTransformGroup);
        BoundingSphere boundingSphere = new BoundingSphere();
        Vector behaviours = new Vector();
        behaviours.add(selfSpin);
        selfSpin.setSchedulingBounds(boundingSphere);
        selfSpin.setTarget(targetTransformGroup);
        targetTransformGroup.addChild(selfSpin);
    }

     private BranchGroup createConeWithNestedSphere() {

        BufferedImage coneBrickImage;
        BufferedImage sphereLavaImage;
        Texture coneTexture;
        Texture sphereTexture;

        BranchGroup objRoot = new BranchGroup();
        TransformGroup tg = new TransformGroup();

        tg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

        tg_tink = new TransformGroup();
        t3d_tink = new Transform3D();

        tg_tink.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

        t3d_tink.setTranslation(new Vector3d(-1.2, -1.4, -2.1));
        t3d_tink.setRotation(new AxisAngle4f(0.0f, 1.0f, 0.0f, 1.25f));
        t3d_tink.setScale(0.9);

        tg_tink.setTransform(t3d_tink);

        Appearance cylinderAppearance = new Appearance();

        try {
            coneBrickImage = ImageIO.read(getClass().getResourceAsStream("/brick.jpg"));
            coneTexture = new TextureLoader(coneBrickImage, this).getTexture();
            cylinderAppearance.setTexture(coneTexture);
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        Cone cone = new Cone(2.8f, 5.5f, Primitive.GENERATE_NORMALS_INWARD | Primitive.GENERATE_TEXTURE_COORDS, cylinderAppearance);

        TransformGroup sphereTransformGroup = new TransformGroup();

        sphereTransformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

        Appearance shpereAppearance = new Appearance();

        try {
            sphereLavaImage = ImageIO.read(getClass().getResourceAsStream("/lava.jpg"));
            sphereTexture = new TextureLoader(sphereLavaImage, this).getTexture();
            shpereAppearance.setTexture(sphereTexture);
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        Sphere sphere = new Sphere(0.6f, Primitive.GENERATE_NORMALS | Primitive.GENERATE_TEXTURE_COORDS, 50, shpereAppearance);

        sphereTransformGroup.addChild(sphere);

        initiateRotation(5000, sphereTransformGroup);

        tg_tink.addChild(cone);

        tg_tink.addChild(sphereTransformGroup);
        
        CollisionDetectorGroup cdGroup = new CollisionDetectorGroup(tg_tink);
        cdGroup.setSchedulingBounds(bounds);

        tg.addChild(tg_tink);

        tg.addChild(cdGroup);
        
        initiateRotation(15000, tg);

        objRoot.addChild(tg);
        objRoot.addChild(createLight());

        objRoot.compile();

        return objRoot;

    }

    private BranchGroup createCylinder() {

        BranchGroup objRoot = new BranchGroup();
        TransformGroup tg = new TransformGroup();

        tg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

        TransformGroup cylinderTransformGroup = new TransformGroup();

        Transform3D t3d = new Transform3D();

        t3d.setTranslation(new Vector3d(0.8, -0.9, -1.7));
        t3d.setRotation(new AxisAngle4f(0.0f, 1.0f, 0.0f, 2.24f));
        t3d.setScale(0.6);

        cylinderTransformGroup.setTransform(t3d);

        Appearance cylinderAppearance = new Appearance();

        Material material = new Material();

        Color3f lightColor = new Color3f(0.0f, 0.0f, 1.0f);
        Color3f darkColor = new Color3f(Color.GREEN);

        material.setEmissiveColor(lightColor);
        material.setDiffuseColor(darkColor);
        material.setSpecularColor(new Color3f(Color.RED));

        cylinderAppearance.setMaterial(material);

        Cylinder cylinder = new Cylinder(5, 10, cylinderAppearance);

        cylinderTransformGroup.addChild(cylinder);

        tg.addChild(cylinderTransformGroup);

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

        private void setCollisionDistance(Transform3D step, Transform3D tink3D, TransformGroup transformGroupTink, Vector3d vector) {
            step.set(vector);
            transformGroupTink.getTransform(tink3D);
            tink3D.mul(step);
            transformGroupTink.setTransform(tink3D);
        }

        public void processStimulus(Enumeration criteria) {

            inCollision = !inCollision;
            if (inCollision) {
                double vectorXAxis = 0.0;
                double vectorYAxis = 0.0;
                double vectorZAxis = -6.0;
                if (vectorZAxis < -6.0) {
                    setCollisionDistance(t3dstep, t3d_tink, tg_tink, new Vector3d(vectorXAxis, vectorYAxis, vectorZAxis));
                } else {
                    setCollisionDistance(t3dstep, t3d_tink, tg_tink, new Vector3d(vectorXAxis, vectorYAxis, -6.0));
                }
                wakeupOn(wExit);
            } else {
                wakeupOn(wEnter);
            }
        }
    }

    public static void main(String[] args) {
        ComputerGraphics3DShapesComposition shapesComposition = new ComputerGraphics3DShapesComposition();
        shapesComposition.setSize(800, 800);
        shapesComposition.setVisible(true);
    }
}