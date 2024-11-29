package Juego_Snake.SnakeGame.src.Paquete;

import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JPanel;

public class PanelFondo extends JPanel {

    Color colorVerdeClaro = new Color(131, 221, 30); // Verde claro
    Color colorVerdeOscuro = new Color(98, 158, 29); // Verde oscuro
    int tammax, tam, cant, res;

    public PanelFondo(int tammax, int cant) {
        this.tammax = tammax; // Tamaño máximo del panel
        this.cant = cant;     // Cantidad de celdas en una fila/columna
        this.tam = tammax / cant; // Tamaño de cada celda
        this.res = tammax % cant;  // Residuo para centrar la cuadrícula
    }

    @Override
    public void paint(Graphics pintor) {
        super.paint(pintor);

        // Pintar el fondo en forma de tablero de ajedrez
        for (int i = 0; i < cant; i++) {
            for (int j = 0; j < cant; j++) {
                // Alternar entre verde claro y oscuro
                if ((i + j) % 2 == 0) {
                    pintor.setColor(colorVerdeClaro);
                } else {
                    pintor.setColor(colorVerdeOscuro);
                }
                // Cambiar tam - 1 a tam para evitar líneas
                pintor.fillRect(res / 2 + i * tam, res / 2 + j * tam, tam, tam);
            }
        }
    }
}
