
// Shooter3D.java
// Andrew Davison, April 2005, ad@fivedots.coe.psu.ac.th

/* Illustrates a shooting device that fires a laser beam at
   a point clicked on the checkboard. Includes sounds, an
   animated laser beam, an animated explosion.
*/

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


public class Shooter3D extends JFrame
{
  public Shooter3D() 
  {
    super("Shooter3D");

    Container c = getContentPane();
    c.setLayout( new BorderLayout() );
    WrapShooter3D w3d = new WrapShooter3D();
    c.add(w3d, BorderLayout.CENTER);

    setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    pack();
    setResizable(false);    // fixed size display
    setVisible(true);
  } // end of Shooter3D()


// -----------------------------------------

  public static void main(String[] args)
  { new Shooter3D(); }

} // end of Shooter3D class
