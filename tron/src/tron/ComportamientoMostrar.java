import java.util.Enumeration;
import javax.media.j3d.Behavior;
import javax.media.j3d.WakeupOnElapsedFrames;

public class ComportamientoMostrar extends Behavior {
   WakeupOnElapsedFrames framewake = new WakeupOnElapsedFrames(0, true);
   Navegador_Tema_3 juego;

public ComportamientoMostrar(Navegador_Tema_3 juego_ ) {
     juego = juego_;
}

public void initialize() { 
    wakeupOn( framewake );
}

public void processStimulus(Enumeration criteria) {
    juego.mostrar();
    wakeupOn( framewake ); }
}