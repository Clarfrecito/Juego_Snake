package Paquete;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import javax.sound.sampled.*;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.io.File;
import java.io.IOException;

public class PanelSnake extends JPanel {

    Color colorSnakeHead = new Color(71, 190, 194);
    Color colorComida = new Color(128, 0, 32);
    Color colorSnake1 = new Color(90, 25, 158);
    Color colorSnake2 = new Color(199, 68, 210);
    int tammax, tam, cant, res;
    List<int[]> snake = new ArrayList<>();
    int[] comida = new int[2];
    String direccion = "de"; // Inicializar dirección a "de"
    String direccionProxima = "de"; // Inicializar dirección próxima a "de"
    boolean puedeMoverse = false; // Variable para controlar el movimiento
    Thread hilo;
    Caminante camino;

    public static int score;
    public static int highScore;

    public PanelSnake(int tammax, int cant) {
        this.tammax = tammax;
        this.cant = cant;
        this.tam = tammax / cant;
        this.res = tammax % cant;
        initializeSnake();
        generarComida();
        this.score = 0;
        this.highScore = 0;
        camino = new Caminante(this);
        hilo = new Thread(camino);
        hilo.start();
    }

    @Override
    public void paint(Graphics pintor) {
        super.paint(pintor);

        // Pintar el cuerpo de la serpiente con bordes
        for (int i = 0; i < snake.size(); i++) {
            int[] par = snake.get(i);
            Color colorActual = (i % 2 == 0) ? colorSnake1 : colorSnake2;
            pintor.setColor(colorActual);
            pintor.fillRect(res / 2 + par[0] * tam, res / 2 + par[1] * tam, tam, tam); // Dibujar el cuerpo

            // Dibujar el borde del color de la serpiente
            pintor.setColor(colorActual);
            pintor.drawRect(res / 2 + par[0] * tam, res / 2 + par[1] * tam, tam, tam); // Dibujar el borde
        }

        // Pintar la cabeza de la serpiente
        pintor.setColor(colorSnakeHead);
        int[] cabeza = snake.get(snake.size() - 1);
        pintor.fillRect(res / 2 + cabeza[0] * tam, res / 2 + cabeza[1] * tam, tam, tam); // Pintar cabeza

        // Dibujar borde de la cabeza
        pintor.setColor(colorSnakeHead);
        pintor.drawRect(res / 2 + cabeza[0] * tam, res / 2 + cabeza[1] * tam, tam, tam); // Borde de la cabeza

        // Pintar la comida
        pintor.setColor(colorComida);
        pintor.fillRect(res / 2 + comida[0] * tam, res / 2 + comida[1] * tam, tam, tam);
    }

    public void avanzar() {
        if (!puedeMoverse) {
            return; // No mover la serpiente si no se ha iniciado el juego
        }
        igualarDir();
        int[] ultimo = snake.get(snake.size() - 1);
        int agregarx = 0;
        int agregary = 0;
        switch (direccion) {
            case "de":
                agregarx = 1;
                break;
            case "iz":
                agregarx = -1;
                break;
            case "ar":
                agregary = -1;
                break;
            case "ab":
                agregary = 1;
                break;
        }

        int nuevoX = ultimo[0] + agregarx;
        int nuevoY = ultimo[1] + agregary;

        if (nuevoX < 0 || nuevoX >= cant || nuevoY < 0 || nuevoY >= cant) {
            reproducirSonido("E:\\Snake prog\\SnakeGameProgramacion\\src\\Sonidos\\perdiste.wav"); // Ruta del sonido de perder
            JOptionPane.showMessageDialog(this, "Has perdido");
            resetGame();
            return;
        }

        int[] nuevo = {Math.floorMod(nuevoX, cant), Math.floorMod(nuevoY, cant)};
        boolean existe = false;
        for (int i = 0; i < snake.size(); i++) {
            if (nuevo[0] == snake.get(i)[0] && nuevo[1] == snake.get(i)[1]) {
                existe = true;
                break;
            }
        }
        if (existe) {
            reproducirSonido("E:\\Snake prog\\SnakeGameProgramacion\\src\\Sonidos\\perdiste.wav"); // Ruta del sonido de perder
            JOptionPane.showMessageDialog(this, "¡Has Perdido!");
            resetGame();
        } else {
            if (nuevo[0] == comida[0] && nuevo[1] == comida[1]) {
                snake.add(nuevo);
                score++;
                Vista.cambiarScore(score);
                reproducirSonido("E:\\Snake prog\\SnakeGameProgramacion\\src\\Sonidos\\sonido_comida.wav"); // Cambia 'sonido.wav' por el nombre de tu archivo
                if (score > highScore) {
                    highScore = score;
                    Vista.cambiarHigh(highScore);
                }
                generarComida();
            } else {
                snake.add(nuevo);
                snake.remove(0);
            }
        }
    }

    public void generarComida() {
        boolean existe;
        int a, b;
        do {
            a = (int) (Math.random() * cant);
            b = (int) (Math.random() * cant);
            existe = false;
            for (int[] par : snake) {
                if (par[0] == a && par[1] == b) {
                    existe = true;
                    break;
                }
            }
        } while (existe);
        this.comida[0] = a;
        this.comida[1] = b;
    }

    public void cambiarDireccion(String dir) {
        if (!puedeMoverse) {
            puedeMoverse = true; // Permitir el movimiento después de la primera tecla
        }

        if ((this.direccion.equals("de") || this.direccion.equals("iz")) && (dir.equals("ar") || dir.equals("ab"))) {
            this.direccionProxima = dir;
        }
        if ((this.direccion.equals("ar") || this.direccion.equals("ab")) && (dir.equals("iz") || dir.equals("de"))) {
            this.direccionProxima = dir;
        }
    }

    public void igualarDir() {
        this.direccion = this.direccionProxima;
    }

    private void resetGame() {
        score = 0;
        Vista.cambiarScore(score);
        snake.clear();
        puedeMoverse = false; // No permitir movimiento al reiniciar
        initializeSnake();
        generarComida();
    }

    private void initializeSnake() {
        direccion = "de";
        direccionProxima = "de";
        snake.clear();
        int inicialX = cant / 2; // Posición central
        int inicialY = cant / 2; // Posición central

        snake.add(new int[]{inicialX, inicialY});          // Cabeza
        snake.add(new int[]{inicialX + 1, inicialY});      // Segundo segmento
        snake.add(new int[]{inicialX + 2, inicialY});      // Tercer segmento
    }

    // Método para reproducir el sonido
    public void reproducirSonido(String rutaArchivo) {
        try {
            File archivoSonido = new File(rutaArchivo);
            if (!archivoSonido.exists()) {
                System.out.println("El archivo no existe: " + rutaArchivo);
                return; // Salir del método si el archivo no se encuentra
            }
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(archivoSonido);
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
        } catch (UnsupportedAudioFileException e) {
            System.out.println("Formato de archivo no soportado: " + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }
}

