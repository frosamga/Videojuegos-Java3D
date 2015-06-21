
import java.util.Enumeration;
import javax.media.j3d.*;
import javax.vecmath.Point3d;

public class DeteccionColisionesPersonaje extends javax.media.j3d.Behavior {

    Figura personaje;
    Shape3D personajeShape;
    WakeupOr oredCriteria = null;
    protected WakeupCriterion[] theCriteria;

    public DeteccionColisionesPersonaje(Figura _personaje) {
        personaje = _personaje;
        
        personajeShape = personaje.personaje;
        setSchedulingBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100));
    }

    public void initialize() {
        theCriteria = new WakeupCriterion[3];
        theCriteria[0] = new WakeupOnCollisionEntry(personajeShape);
        theCriteria[1] = new WakeupOnCollisionExit(personajeShape);
        theCriteria[2] = new WakeupOnCollisionMovement(personaje.personaje);
        oredCriteria = new WakeupOr(theCriteria);
        wakeupOn(oredCriteria);
    }

    public void processStimulus(Enumeration criteria) {
        try {
            WakeupCriterion theCriterion = (WakeupCriterion) criteria.nextElement();


            if (theCriterion instanceof WakeupOnCollisionEntry) {
                Node theLeaf = ((WakeupOnCollisionEntry) theCriterion)
                        .getTriggeringPath().getObject();

                if ((theLeaf.getUserData().toString().contains("pared")) || (theLeaf.getUserData().toString().contains("estela"))) {
                    personaje.muerto = true;
                    personaje.juego.ganador="Jugador "+(Math.abs(personaje.identificador-1)+1);
                    personaje.juego.terminado = true;
                }
            } else if (theCriterion instanceof WakeupOnCollisionExit) {
                Node theLeaf = ((WakeupOnCollisionExit) theCriterion)
                        .getTriggeringPath().getObject();


                if ((theLeaf.getUserData().toString().contains("pared")) || (theLeaf.getUserData().toString().contains("estela"))) {
                    personaje.muerto = true;
                    personaje.juego.ganador="Jugador "+(Math.abs(personaje.identificador-1)+1);
                    personaje.juego.terminado = true;
                   
                }
            } else {
                Node theLeaf = ((WakeupOnCollisionMovement) theCriterion)
                        .getTriggeringPath().getObject();
                
                if ((theLeaf.getUserData().toString().contains("pared")) || (theLeaf.getUserData().toString().contains("estela"))) {
                    personaje.muerto = true;
                    personaje.juego.ganador="Jugador "+(Math.abs(personaje.identificador-1)+1);
                    personaje.juego.terminado = true;
                }
            }

        } catch (Exception ex) {
            System.out.println("Excepcion controlada");
        }
        wakeupOn(oredCriteria);
    }
}
