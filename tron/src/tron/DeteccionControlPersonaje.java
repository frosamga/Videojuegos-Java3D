
import java.awt.AWTEvent;
import java.awt.event.KeyEvent;
import java.util.Enumeration;
import javax.media.j3d.*;

public class DeteccionControlPersonaje extends javax.media.j3d.Behavior {

    Figura personaje;
    int jugador;
    TransformGroup TG_personaje;
    WakeupOnAWTEvent presionada = new WakeupOnAWTEvent(KeyEvent.KEY_PRESSED);
    WakeupOnAWTEvent liberada = new WakeupOnAWTEvent(KeyEvent.KEY_RELEASED);
    WakeupCondition keepUpCondition = null;
    WakeupCriterion[] continueArray = new WakeupCriterion[2];

    public DeteccionControlPersonaje(Figura _personaje, int jugador_) {
        TG_personaje = _personaje.desplazamientoFigura;
        personaje = _personaje;
        continueArray[0] = liberada;
        continueArray[1] = presionada;
        keepUpCondition = new WakeupOr(continueArray);
        jugador = jugador_;

    }

    public void initialize() {
        wakeupOn(keepUpCondition);
    }

    public void processStimulus(Enumeration criteria) {
        while (criteria.hasMoreElements()) {
            WakeupCriterion ster = (WakeupCriterion) criteria.nextElement();
            if (ster instanceof WakeupOnAWTEvent) {
                AWTEvent[] events = ((WakeupOnAWTEvent) ster).getAWTEvent();
                for (int n = 0; n < events.length; n++) {
                    if (events[n] instanceof KeyEvent) {
                        KeyEvent ek = (KeyEvent) events[n];
                        if (ek.getID() == KeyEvent.KEY_PRESSED) {
                            String direccion = personaje.getDirection();

                            if (jugador == 1) {
                                if (ek.getKeyChar() == 'w') {
                                    if (!direccion.equals("S") && !direccion.equals("N")) {

                                        personaje.distanciaRecorrida=-2.5f;
                                        personaje.adelante = true;

                                        personaje.izquierda = false;
                                        personaje.derecha = false;
                                        personaje.atras = false;
                                    }
                                } else if (ek.getKeyChar() == 'a') {
                                    if (!direccion.equals("E") && !direccion.equals("W")) {
                                        personaje.distanciaRecorrida=-2.5f;
                                        personaje.izquierda = true;
                                        personaje.adelante = false;

                                        personaje.derecha = false;
                                        personaje.atras = false;
                                    }
                                } else if (ek.getKeyChar() == 'd') {
                                    if (!direccion.equals("W")&& !direccion.equals("E")) {
                                        personaje.distanciaRecorrida=-2.5f;
                                        personaje.derecha = true;
                                        personaje.adelante = false;
                                        personaje.izquierda = false;

                                        personaje.atras = false;
                                    }
                                } else if (ek.getKeyChar() == 's') {
                                    if (!direccion.equals("N") && !direccion.equals("S")) {
                                        personaje.distanciaRecorrida=-2.5f;
                                        personaje.atras = true;
                                        personaje.adelante = false;
                                        personaje.izquierda = false;
                                        personaje.derecha = false;

                                    }
                                }

                            } else if (jugador == 2) {

                                if (ek.getKeyCode() == KeyEvent.VK_UP) {

                                    if (!direccion.equals("S") && !direccion.equals("N")) {
                                        personaje.distanciaRecorrida=-2.5f;
                                        personaje.adelante = true;

                                        personaje.izquierda = false;
                                        personaje.derecha = false;
                                        personaje.atras = false;
                                    }
                                } else if (ek.getKeyCode() == KeyEvent.VK_LEFT) {
                       
                                    if (!direccion.equals("E") && !direccion.equals("W")) {
                                        personaje.distanciaRecorrida=-2.5f;
                                        personaje.izquierda = true;
                                        personaje.adelante = false;

                                        personaje.derecha = false;
                                        personaje.atras = false;
                                    }
                                } else if (ek.getKeyCode() == KeyEvent.VK_RIGHT) {
 
                                    if (!direccion.equals("W") && !direccion.equals("E")) {
                                        personaje.distanciaRecorrida=-2.5f;
                                        personaje.derecha = true;
                                        personaje.adelante = false;
                                        personaje.izquierda = false;

                                        personaje.atras = false;
                                    }
                                } else if (ek.getKeyCode() == KeyEvent.VK_DOWN) {
                                    if (!direccion.equals("N") && !direccion.equals("S")) {
                                        personaje.distanciaRecorrida=-2.5f;
                                        personaje.atras = true;
                                        personaje.adelante = false;
                                        personaje.izquierda = false;
                                        personaje.derecha = false;

                                    }
                                }
                            }
                        }
                    }

                }
            }
        }
    


        wakeupOn(keepUpCondition);
    }
    
}
