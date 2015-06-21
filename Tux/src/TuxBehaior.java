/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



import com.sun.j3d.utils.geometry.ColorCube;
import com.sun.j3d.utils.universe.SimpleUniverse;
import java.awt.AWTEvent;
import java.awt.event.KeyEvent;
import java.util.Enumeration;
import javax.media.j3d.*;
import javax.vecmath.*;

public class TuxBehaior extends Behavior {
//camara
    TransformGroup Camara;
// personaje
    TransformGroup Node;
//Llevan la posicion anterior del personaje es usado por la camara
    Transform3D tr1Camara = new Transform3D();
    Transform3D tr2Personaje = new Transform3D();
    private WakeupOnAWTEvent trigger;
// que tan lejos estara la camara
    Integer Zoom = 3;

    private void TuxPosicion() {
//posicion clave
        Node.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        tr1Camara.set(new Vector3d(0f, 0.6f, +Zoom));
        Camara.setTransform(tr1Camara);
        tr2Personaje.set(new Vector3f(0f, 0.45f, 0f));
        Node.setTransform(tr2Personaje);
    }

    TuxBehaior(SimpleUniverse su, TransformGroup Node) {
//Inicializacion y posicionamiento
        this.Node = Node;
        this.Camara = su.getViewingPlatform().getViewPlatformTransform();
        trigger = new WakeupOnAWTEvent(KeyEvent.KEY_PRESSED);
        TuxPosicion();
        setSchedulingBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0));
    }

    public void initialize() {
        this.wakeupOn(trigger);
    }

    public void processStimulus(Enumeration criteria) {
        while (criteria.hasMoreElements()) {
            WakeupCriterion wakeup = (WakeupCriterion) criteria.nextElement();
            if (wakeup instanceof WakeupOnAWTEvent) {

                AWTEvent[] arr = ((WakeupOnAWTEvent) (wakeup)).getAWTEvent();
                KeyEvent ke = (KeyEvent) arr[0];
                switch (ke.getKeyCode()) {
                    case KeyEvent.VK_UP:
                        TuxArriba();
                        break;
                    case KeyEvent.VK_DOWN:
                        TuxAbajo();
                        break;
                    case KeyEvent.VK_LEFT:
                        TuxIzq();
                        break;
                    case KeyEvent.VK_RIGHT:
                        TuxDer();
                        break;
                    case KeyEvent.VK_A:
                        TuxVolar();
                        break;
                    case KeyEvent.VK_S:
                        TuxCae();
                        break;
                }

            }
        }
        wakeupOn(trigger);
    }

    private void TuxArriba() {
// nueva posicion
        Transform3D tr1=new Transform3D();
        Vector3f vec=new Vector3f();
        vec.z=-0.1f;
        tr1.set(vec);
// se le agrega a la camara
        this.tr1Camara.mul(tr1);
        this.Camara.setTransform(this.tr1Camara);
//Se le agrega al personaje
        this.tr2Personaje.mul(tr1);
        this.Node.setTransform(this.tr2Personaje);
    }

    private void TuxAbajo() {
// nueva posicion
        Transform3D tr1=new Transform3D();
        Vector3f vec=new Vector3f();
        vec.z=+0.1f;
        tr1.set(vec);
// se le agrega a la camara
        this.tr1Camara.mul(tr1);
        this.Camara.setTransform(this.tr1Camara);
//Se le agrega al personaje
        this.tr2Personaje.mul(tr1);
        this.Node.setTransform(this.tr2Personaje);
    }

    private void TuxIzq() {
//Personaje
             Transform3D transY=new Transform3D();
            transY.rotY(+Math.PI/20);
            this.tr2Personaje.mul(transY);
            Node.setTransform(tr2Personaje);
             Vector3f vec=new Vector3f(0f,0f,-Zoom);
//Camara
            transY.set(vec);
            tr1Camara.mul(transY);
            transY.rotY(+Math.PI/20);
            tr1Camara.mul(transY);
            vec=new Vector3f(0f,0f,this.Zoom);
            transY.set(vec);
            tr1Camara.mul(transY);
            Camara.setTransform(tr1Camara);

    }

    private void TuxDer() {
//Personaje
             Transform3D transY=new Transform3D();
            transY.rotY(-Math.PI/20);
            this.tr2Personaje.mul(transY);
            Node.setTransform(tr2Personaje);
             Vector3f vec=new Vector3f(0f,0f,-Zoom);
//Camara
            transY.set(vec);
            tr1Camara.mul(transY);
            transY.rotY(-Math.PI/20);
            tr1Camara.mul(transY);
            vec=new Vector3f(0f,0f,this.Zoom);
            transY.set(vec);
            tr1Camara.mul(transY);
            Camara.setTransform(tr1Camara);
    }
    private void TuxVolar(){
// nueva posicion
        Transform3D tr1=new Transform3D();
        Vector3f vec=new Vector3f();
        vec.y=+0.2f;
        tr1.set(vec);
// se le agrega a la camara
        this.tr1Camara.mul(tr1);
        this.Camara.setTransform(this.tr1Camara);
//Se le agrega al personaje
        this.tr2Personaje.mul(tr1);
        this.Node.setTransform(this.tr2Personaje);
    }
    private void TuxCae(){
// nueva posicion
        Transform3D tr1=new Transform3D();
        Vector3f vec=new Vector3f();
        vec.y=-0.2f;
        tr1.set(vec);
// se le agrega a la camara
        this.tr1Camara.mul(tr1);
        this.Camara.setTransform(this.tr1Camara);
//Se le agrega al personaje
        this.tr2Personaje.mul(tr1);
        this.Node.setTransform(this.tr2Personaje);
    }
}
