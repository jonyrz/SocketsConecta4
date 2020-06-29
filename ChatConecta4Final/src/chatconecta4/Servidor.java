package chatconecta4;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Servidor {
    private static ServerSocket server = null;
    private static int PUERTO = 59090;

    public static void main(String[] args) throws IOException {
        ChatS VentanaServidor = new ChatS();
        VentanaServidor.setVisible(true);
        server = new ServerSocket(PUERTO);
        Executor pool = Executors.newFixedThreadPool(20); //Maximo 40 usuarios
        while (true) {
            Juego partida = new Juego();
            pool.execute(partida.new IniciaJuego(server.accept(),'1')); //Peticion para jugador 1
            pool.execute(partida.new IniciaJuego(server.accept(),'2'));  //Peticion para jugador 2
        }

    }
}
