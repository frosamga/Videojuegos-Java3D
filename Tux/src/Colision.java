/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import com.sun.j3d.utils.geometry.ColorCube;
import java.util.Enumeration;
import javax.media.j3d.Behavior;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.Node;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.WakeupCriterion;
import javax.media.j3d.WakeupOnCollisionEntry;
import javax.media.j3d.WakeupOnCollisionExit;
import javax.media.j3d.WakeupOnCollisionMovement;
import javax.media.j3d.WakeupOr;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3f;
/**
 *
 * @author lucita
 */
public class Colision extends Behavior {

    protected WakeupCriterion[] Criteria;
    protected WakeupOr oredCriteria;
    protected Node collidingNode;

    public Colision(TransformGroup tr) {
        this.collidingNode = tr;
        
    }

    public void initialize() {

// inicializar colisiones
        Criteria = new WakeupCriterion[3];
        Criteria[0] = new WakeupOnCollisionEntry(collidingNode, WakeupOnCollisionEntry.USE_GEOMETRY);
        Criteria[1] = new WakeupOnCollisionExit(collidingNode, WakeupOnCollisionEntry.USE_GEOMETRY);
        Criteria[2] = new WakeupOnCollisionMovement(collidingNode, WakeupOnCollisionEntry.USE_GEOMETRY);
        oredCriteria = new WakeupOr(Criteria);
        wakeupOn(oredCriteria);
        
    }

    public void processStimulus(Enumeration criteria) {
        while (criteria.hasMoreElements()) {
            WakeupCriterion wakeup = (WakeupCriterion) criteria.nextElement();
//ENTRA COLISION
            if (wakeup instanceof WakeupOnCollisionEntry) {
                Node theLeaf = ((WakeupOnCollisionEntry) wakeup).getTriggeringPath().getObject().getParent();
                System.out.println("inicio a colisionar con:"+theLeaf.getUserData());
//DEJA COLISION
            } else if (wakeup instanceof WakeupOnCollisionExit) {
                Node theLeaf = ((WakeupOnCollisionExit) wakeup).getTriggeringPath().getObject().getParent();
                System.out.println("dejo de colisionar con:"+theLeaf.getUserData());
//COLISION
            } else if (wakeup instanceof WakeupOnCollisionMovement) {
                Node theLeaf = ((WakeupOnCollisionMovement) wakeup).getTriggeringPath().getObject().getParent();
                System.out.println(theLeaf.getUserData());
            }
        }
        wakeupOn(oredCriteria);
    }
//AGREGAR CUBOS    {se necesita un transformgroup}
    public TransformGroup AgregarCubitos(Vector3f vec,String name){
        TransformGroup tr=new TransformGroup();
        Transform3D t3d=new Transform3D();
        ColorCube cubito=new ColorCube(.4);
        t3d.set(vec);
        tr.setTransform(t3d);
//AGREGAR TEXTO AL TRANSFORMGROUP
        tr.setUserData("Cubito: "+ name);
        tr.addChild(cubito);
        return tr;
    }
}
