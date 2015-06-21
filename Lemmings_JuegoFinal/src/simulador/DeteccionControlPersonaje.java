package simulador;

import java.awt.AWTEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.Enumeration;
import javax.media.j3d.*;

public class DeteccionControlPersonaje extends javax.media.j3d.Behavior {
    Figura personaje;
    Juego juego;
    WakeupOnAWTEvent    presionada = new WakeupOnAWTEvent(KeyEvent.KEY_PRESSED);
    WakeupOnAWTEvent    liberada = new WakeupOnAWTEvent(KeyEvent.KEY_RELEASED);
    WakeupOnAWTEvent    ratonPresionado= new WakeupOnAWTEvent(MouseEvent.MOUSE_PRESSED);
    WakeupOnAWTEvent    ratonSuelto= new WakeupOnAWTEvent(MouseEvent.MOUSE_RELEASED);
    WakeupOnAWTEvent    ratonMovimiento= new WakeupOnAWTEvent(MouseEvent.MOUSE_DRAGGED);
    WakeupCondition     keepUpCondition = null,wakeupConditionRaton=null;
    WakeupCriterion[]   continueArray = new WakeupCriterion[5];
    long                lastJumpExecuted = 0;
  
 
    
 public DeteccionControlPersonaje(Figura _personaje ) {
        personaje = _personaje;
        continueArray[0]=liberada;
        continueArray[1]=presionada;
        continueArray[2]=ratonPresionado;
        continueArray[3]=ratonSuelto;
        continueArray[4]=ratonMovimiento;
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
                    if (ek.getKeyChar() == 'w')      personaje.adelante=true ;            
                    else if (ek.getKeyChar() == 'a') personaje.izquierda=true;
                    else if (ek.getKeyChar() == 'd') personaje.derecha=true;
                    else if (ek.getKeyChar() == 's') personaje.atras=true;
                    else if (ek.getKeyChar() == KeyEvent.VK_SPACE) {
                        if (System.currentTimeMillis() - lastJumpExecuted > 1500) {
                            personaje.salto=true;
                            lastJumpExecuted = System.currentTimeMillis();
                        }
                    } else if (ek.getKeyChar() == 'j') personaje.accion=true;                            
                }
                else if (ek.getID() == KeyEvent.KEY_RELEASED)   {
                    if (ek.getKeyChar()== 'w') personaje.adelante=false;
                    else if (ek.getKeyChar() == 'a') personaje.izquierda=false;
                    else if (ek.getKeyChar() == 'd') personaje.derecha=false;
                    else if (ek.getKeyChar() == 's')personaje.atras=false;
                    else if (ek.getKeyChar() ==  KeyEvent.VK_SPACE) {
                        personaje.salto=false;
                        //lastJumpExecuted = 0;
                    }else if (ek.getKeyChar() == 'j') personaje.accion=true;
                }
              }            
              if(events[n] instanceof MouseEvent){
                  MouseEvent me = (MouseEvent) events[n];
                  if(me.getID()== MouseEvent.MOUSE_PRESSED){
                     //si queremos asignarle algo, pues aqui esta, solo usaremos el dragged para el puzzle              
                 }else if(me.getID()== MouseEvent.MOUSE_RELEASED){
                     //lo mismo, ahora no lo usaremos.             
                 }else if(me.getID()== MouseEvent.MOUSE_DRAGGED){
                      //System.out.println(me.getX()+","+me.getY());  
                      personaje.ratonX=me.getX();
                      personaje.ratonY=me.getY();
              }
           }
        }
}}
 wakeupOn(keepUpCondition);

  }
}




