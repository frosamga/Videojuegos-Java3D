package juego;

import java.awt.AWTEvent;
import java.awt.event.KeyEvent;
import java.util.Enumeration;
import javax.media.j3d.*;

public class DeteccionControlPersonaje extends javax.media.j3d.Behavior {
    Figura personaje;
    TransformGroup TG_personaje;
    WakeupOnAWTEvent    presionada = new
    WakeupOnAWTEvent(KeyEvent.KEY_PRESSED);
    WakeupOnAWTEvent    liberada = new WakeupOnAWTEvent(KeyEvent.KEY_RELEASED);
    WakeupCondition     keepUpCondition = null;
    WakeupCriterion[]   continueArray = new WakeupCriterion[2];

 public DeteccionControlPersonaje(Figura _personaje ) {
        TG_personaje = _personaje.desplazamientoFigura;
        personaje = _personaje;
        continueArray[0]=liberada;
        continueArray[1]=presionada;
        keepUpCondition = new WakeupOr(continueArray);
    }

 public void initialize()    {
            wakeupOn(keepUpCondition);
    }

 public void processStimulus(Enumeration criteria) {
      while (criteria.hasMoreElements()){
      WakeupCriterion ster=(WakeupCriterion) criteria.nextElement();
       if (ster instanceof WakeupOnAWTEvent)   {
           AWTEvent[] events = ( (WakeupOnAWTEvent) ster).getAWTEvent();
           for (int n=0;n<events.length;n++){
              if (events[n]  instanceof KeyEvent){
                KeyEvent ek = (KeyEvent) events[n] ;
                if (ek.getID() == KeyEvent.KEY_PRESSED) {
                    //keyEvent.vk_left si es para teclas diferentes
                    if (ek.getKeyChar() == 'w') personaje.adelante= true;
                    else if (ek.getKeyChar() == 'a') personaje.izquierda=true;
                    else if (ek.getKeyChar() == 'd') personaje.derecha=true;
                    else if (ek.getKeyChar() == 's') personaje.atras=true;               
                    else if (personaje.cambioPersona&&ek.getKeyChar() == 'p') personaje.cambioPersona=false;
                    else if (!personaje.cambioPersona&&ek.getKeyChar() == 'p') personaje.cambioPersona=true;
                    else if (ek.getKeyChar() == 'j') personaje.ataque=true;
                    else if (personaje.cam1&&ek.getKeyChar() == 'o')personaje.cam1=false;
                    else if (!personaje.cam1&&ek.getKeyChar() == 'o')personaje.cam1=true;
            
              
             
                    
                }
                else if (ek.getID() == KeyEvent.KEY_RELEASED)   {
                    personaje.moviendo=false;
                    if (ek.getKeyChar()== 'w') personaje.adelante=false;
                    else if (ek.getKeyChar() == 'a') personaje.izquierda=false;
                    else if (ek.getKeyChar() == 'd') personaje.derecha=false;
                    else if (ek.getKeyChar() == 's')personaje.atras=false;
                    else if (ek.getKeyChar() == 'j') personaje.ataque=false;
          
                    
                }
          }
        }
}}
 wakeupOn(keepUpCondition);
  }
}
