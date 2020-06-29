package chatconecta4;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Juego {

    player Jugador1, Jugador2;

    public Juego() {
        Jugador1 = new player();
        Jugador2 = new player();
    }

    public class IniciaJuego implements Runnable {
        public IniciaJuego(Socket socket, char marca) throws IOException {
            if (Jugador1.marca == '?') {
                Jugador1.setPlayer(socket, marca);
            } else if (Jugador2.marca == '?') {
                Jugador2.setPlayer(socket, marca);
            }
        }

        public void run() {
            boolean turnoJ1 = true;
            boolean FinJuego = false;

            if (Jugador1.marca != '?' && Jugador2.marca != '?') {
                while (true) {
                    if (turnoJ1) {  //Turno J1
                        Jugador1.out.println("j1");
                        Jugador2.out.println("j2");
                        int index = -1;				//Detiene el movimieto hasta que J1 mueva
                        if (Jugador1.in.hasNext() == true) {
                            index = Jugador1.in.nextInt();	//Obtiene el movimiento del J1
                        }
                        if (Jugador2.socket.isClosed() == true) //Cerrar conexión si J2 se desconecta
                        {
                            Jugador1.cerrarConexion();
                            Jugador2.cerrarConexion();
                            FinJuego = true;
                            break;
                        }

                        Jugador2.out.println(index);

                        if (index == -1){               //Si no se logra obtener el movimiento del otro jugador se cierra la conexión
                            Jugador1.cerrarConexion();
                            Jugador2.cerrarConexion();
                            FinJuego = true;
                            break;
                        }
                        if (Jugador1.in.hasNext() == true) {    //J1 termina de mover, comienza el tuerno del J2
                            FinJuego = Jugador1.in.nextBoolean();
                        }
                        turnoJ1 = false;    
                        if (FinJuego == true) {     //Termina el juego si cambia el estado a verdadero
                            break;
                        }

                    } 
                    else{
                        Jugador2.out.println("j1");
                        Jugador1.out.println("j2");
                        if (Jugador1.socket.isClosed() == true) {   //Cerrar conexión si J2 se desconecta
                            Jugador1.cerrarConexion();
                            Jugador2.cerrarConexion();
                            FinJuego = true;    //Termina el juego si cambia el estado a verdadero
                            break;
                        }
                        int index = -1;     //Detiene el movimieto hasta que J2 mueva
                        if (Jugador2.in.hasNext() == true) {    
                            index = Jugador2.in.nextInt();
                        }

                        Jugador1.out.println(index);    //Verifica que haya movido el J2

                        if (index == -1) {  //Verifica que el jugador anterior haya movido
                            Jugador1.cerrarConexion();
                            Jugador2.cerrarConexion();
                            FinJuego = true;    //Termina el juego si cambia el estado a verdadero
                            break;
                        }
                        if (Jugador2.in.hasNext() == true) {    //J1 termina de mover, comienza el tuerno del J2 
                            FinJuego = Jugador2.in.nextBoolean();
                        }
                        if (FinJuego == true) {     //Termina el juego si se cumple la condición
                            break;
                        }
                        turnoJ1 = true;     //Al no cumplir ninguna condicion, es turno de J1
                    }
                }
                //Cierra conexiones para cada jugador
                Jugador1.cerrarConexion();
                Jugador2.cerrarConexion();

            }
        }
    }

    public class player {
        char marca = '?';
        Socket socket = null;
        Scanner in = null;      //Entrada de datos cliente
        PrintWriter out = null; //Salida de datos cliente

        public void setPlayer(Socket socket, char marca) throws IOException {
            this.socket = socket;
            this.marca = marca;
            in = new Scanner(socket.getInputStream());  //Se crea una instancia para los datos recibidos del cliente
            out = new PrintWriter(socket.getOutputStream(), true);  //Se crea una instancia para los devolver una respuesta del cliente
            out.println("Bienvenido Jugador " + marca);
        }

        //Método para cerrar conexiones
        public void cerrarConexion(){   
            try {
                socket.close();
                in.close();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
