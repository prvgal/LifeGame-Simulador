/**
 * Main.java
 *
 * @author Pablo Rivero Galvín [prvgal]
 * @version 1.0
 */

package lifesim;

import lifesim.vista.VentanaPrincipal;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new VentanaPrincipal().setVisible(true));
    }
}