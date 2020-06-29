package chatconecta4;

import java.awt.Color;
import java.awt.Font;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

public class Cliente {
    private static int PUERTO = 59090;
    private static Socket socket;
    private static Scanner in;
    private static PrintWriter out;
    static int[][] tbl;
    private static final LayoutManager GridLayout = null;
    private static int PanelW = 595, PanelH = 685;
    private static int casilla = 85;
    private static JButton grids[][];
    private static JButton ctrl[];
    private static JFrame juegoFrame;
    private static JButton btnChat;
    private static Color j1Color = Color.blue;
    private static Color j2Color = Color.red;
    private static boolean FinJuego = false;
    private static int ganador = -1;
    private static String flag = "";    //Recibe los datos desde cualquier jugador desde el servidor

    public static void main(String[] args) throws IOException {
        juegoFrame = new JFrame("Conecta 4 - Rendon Zamora Jonathan Omar");

        //Diseño del tablero
        tbl = new int[6][7];
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 7; j++) {
                tbl[i][j] = 0;
            }
        }
        //Llamada a metodos
        IniciarServicio();
        Interfaz();
        IniciarJuego();
    }

    public static void IniciarServicio() throws IOException {
        InetAddress host = InetAddress.getLocalHost();  //Devuelve la direccion IP del cliente
        socket = new Socket(host.getHostName(), PUERTO);    //Crea el objeto obteniendo el IP y el Puerto
        in = new Scanner(socket.getInputStream());  //Crea el objeto para leer los datos 
        out = new PrintWriter(socket.getOutputStream(), true);
        String connected = in.nextLine();   //Secuencia de datos entrantes
        System.out.println(connected);  //Imprime el orden de las peticiones para jugar
    }

    public static void Interfaz() {
        //Elaboracion de la interfaz 
        juegoFrame.setVisible(true);
        juegoFrame.setSize(PanelW, PanelH);
        juegoFrame.setResizable(false);
        juegoFrame.setLayout(GridLayout);
        juegoFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Color color2 = new Color(66, 21, 179); 	//Crea un objeto para definir el color del tablero
        
        //Boton del chat
        grids = new JButton[6][7];
        JButton btnChat = new JButton("Chat");
        btnChat.setBounds(252, 611, 90, 30);
        juegoFrame.add(btnChat);
        btnChat.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ChatC VentanaCliente = new ChatC();
                VentanaCliente.setVisible(true);
            }
        });

        //Diseño de las casillas
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 7; j++) {
                grids[i][j] = new JButton("");
                grids[i][j].setFont(new Font("Arial", Font.PLAIN, 65)); 	
                grids[i][j].setBounds(j * casilla, i * casilla + casilla, casilla, casilla);
                grids[i][j].setContentAreaFilled(false);
                grids[i][j].setFocusable(false);
                juegoFrame.add(grids[i][j]);
            }
        }
        
        //Evento al cerrar la ventana
        juegoFrame.addWindowListener(new java.awt.event.WindowAdapter() { 
            public void cerrarVentana(java.awt.event.WindowEvent windowEvent) {
                if (JOptionPane.showConfirmDialog(juegoFrame,
                        "¿Seguro que quieres cerrar esta ventana?", "¿Cerrar ventana?",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                    if (flag.equals("j1")) {    //Manda al flag -1 para avisar que el jugador se ha desconectado
                        out.println(-1);
                    }
                    try {
                        socket.close();     //Se desconecta del socket
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    System.exit(0);     //Sale del sistema
                }
            }
        });
        
        //Diseño de las casillas de encabezado
        ctrl = new JButton[7];
        for (int i = 0; i < 7; i++) {
            ctrl[i] = new JButton("");
            ctrl[i].setBounds(i * casilla, 0, casilla, casilla);
            ctrl[i].setText("▼");
            ctrl[i].setFont(new Font("Arial", Font.PLAIN, 35));
            ctrl[i].setForeground(color2);
            juegoFrame.add(ctrl[i]);
            int[] index = new int[1];
            index[0] = i;   //Se crea un indez que se ira llenando al paso de los turnos, 
                            //el limite sera 7 que es cuando se llena el tablero

            ctrl[i].addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    //Rellena las filas con la figura del jugador O
                    for (int fila=5; fila>=0; fila-=1) {
                        if (tbl[fila][index[0]] == 0) {
                            grids[fila][index[0]].setText("O");
                            grids[fila][index[0]].setForeground(j1Color);
                            tbl[fila][index[0]] = 1;
                            //El limite es 7, no se puede exceder por la dimension del tablero
                            for (int i=0; i<7; i++) {     
                                ctrl[i].setEnabled(false);
                            }
                            out.println(index[0]);  //Imprime el movimiento en el arreglo index
                            VerificarGanador();     //Se invoca al metodo para verificar si hay un ganador
                            out.println(FinJuego);  
                            break;
                        }
                    }
                    //Excede el numero de movimientos
                    if (tbl[0][index[0]] != 0) {
                        ctrl[index[0]].setText("▬");
                        ctrl[index[0]].setEnabled(false);
                    }
                }
            });
        }
        juegoFrame.repaint();   //Dibuja el tablero
    }

    public static void VerificarGanador() {
        //Verificacion horizontal, recorre todas las casillas
        for (int i=0; i<6; i+=1){
            for (int j=0; j<4; j+=1) {
                int cont=0;
                for (int k=0; k<4; k+=1) {
                    if (tbl[i][j] == tbl[i][j + k]) {
                        cont += 1;
                    }
                }
                //Si el contador llega a 4 hay un ganador
                if (cont==4) {
                    if (tbl[i][j] == 1) {   //Si la matriz es 1, gana el jugador 1
                        FinJuego = true;
                        ganador = 1;
                    } else if (tbl[i][j] == 2) {    //Si la matriz es 2, gana el jugador 2
                        FinJuego = true;
                        ganador = 2;
                    }
                }
            }
        }
        //Verificacion vertical, recorre todas las casillas
        for (int i=0; i<7; i+=1) {
            for (int j=0; j<3; j+=1) {
                int cont=0;
                for (int k=0; k<4; k+=1) {
                    if (tbl[j][i]==tbl[j+k][i]) {
                        cont+=1;
                    }
                }
                //Si el contador llega a 4 hay un ganador
                if (cont==4) {
                    if (tbl[j][i] == 1) {   //Si la matriz es 1, gana el jugador 1
                        FinJuego = true;
                        ganador = 1;
                    } else if (tbl[j][i] == 2) {    //Si la matriz es 2, gana el jugador 2
                        FinJuego = true;
                        ganador = 2;
                    }
                }
            }
        }
        
        //Verifiacion diagonal
        for (int i=0; i<3; i+=1) {
            for (int j=0; j<4; j+=1){
                int cont=0;
                for (int k=0; k<4; k+=1) {
                    if (tbl[i][j] == tbl[i+k][j+k]) {
                        cont+=1;
                    }
                }
                //Si el contador llega a 4 hay un ganador
                if (cont==4) {
                    if (tbl[i][j] == 1) {   //Si la matriz es 1, gana el jugador 1
                        FinJuego = true;
                        ganador = 1;
                    } else if (tbl[i][j] == 2) {    //Si la matriz es 2, gana el jugador 2
                        FinJuego = true;
                        ganador = 2;
                    }
                }
            }
        }

        //Verificacion diagonal inversa
        for (int i=3; i<6; i+=1) {
            for (int j=0; j<4; j+=1) {
                int cont=0;
                for (int k=0; k<4; k+=1) {
                    if (tbl[i][j] == tbl[i-k][j+k]) {
                        cont+=1;
                    }
                }
                //Si el contador llega a 4 hay un ganador
                if (cont==4) {
                    if (tbl[i][j]==1) {     //Si la matriz es 1, gana el jugador 1
                        FinJuego = true;
                        ganador = 1;
                    } else if (tbl[i][j] == 2) {    //Si la matriz es 2, gana el jugador 2
                        FinJuego = true;
                        ganador = 2;
                    }
                }
            }
        }

	//Verifica que se rellenen las casillas
        boolean dibujar = true;
        for (int i = 0; i < 6; i += 1) {
            for (int j = 0; j < 7; j += 1) {
                if (tbl[i][j] == 0) {
                    dibujar = false;
                    break;
                }
            }
        }
        //Verifica que se hayan llenado las casillas
        if (dibujar == true) {
            FinJuego = true;
            ganador = 0;    //Si llega al limite de casillas llenas, no habra un ganador
        }
    }
    
    //Método para mostrar resultado
    public static void MostrarResultado() {
        JFrame ResultadoFrame = new JFrame("Resultado");
        //ResultadoFrame.setLayout(GridLayout);
        ResultadoFrame.setVisible(true);
        ResultadoFrame.setSize(550, 250);
        ResultadoFrame.setResizable(false);
        ResultadoFrame.setLayout(GridLayout);
        ResultadoFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel ResultadoText = new JLabel("");

        if (ganador == 1) {
            ResultadoText.setText("¡Haz ganado!");
            ResultadoText.setForeground(Color.blue);
        } else if (ganador == 2) {
            ResultadoText.setText("¡Haz perdido!");
            ResultadoText.setForeground(Color.red);
        } else if (ganador == 0) {
            ResultadoText.setText("Empate");
            ResultadoText.setForeground(Color.darkGray);
        } else {
            ResultadoText.setText("Jugador 2 se ha desconectado");
            ResultadoText.setForeground(Color.blue);
        }
        ResultadoText.setBounds(30, 10, 500, 180);
        ResultadoText.setFont(new Font("Arial", Font.BOLD, 30));
        ResultadoFrame.add(ResultadoText);
    }

    public static void IniciarJuego() throws IOException {
        for (int i=0; i<7; i++) {
            ctrl[i].setEnabled(false);
        }
        while (true) {
            if (FinJuego == true) {
                break;
            }
            if (in.hasNext() == true) {
                flag = in.nextLine();
                if (flag.equals("j1")) {
                    //Jugador 1 puede iniciar
                    for (int i = 0; i < 7; i++) {
                        if (ctrl[i].getText().equals("▼")) {
                            ctrl[i].setEnabled(true);
                        }
                    }
                } 
                else if (flag.equals("j2")) {
                    //Jugador 2 puede inciar
                    int index = 0;
                    if (in.hasNext() == true) {
                        index = in.nextInt();
                    }
                    if (index == -1) {
                        System.out.println("Jugador 2 desconectado");
                        FinJuego = true;
                        break;
                    }
                    for (int fila=5; fila>=0; fila-=1) {
                        if (tbl[fila][index] == 0) {
                            tbl[fila][index] = 2;
                            grids[fila][index].setText("O");
                            grids[fila][index].setForeground(j2Color);
                            break;
                        }
                    }
                    if (tbl[0][index] != 0) {
                        ctrl[index].setText("Empate");
                        ctrl[index].setEnabled(false);
                    }
                    VerificarGanador();
                    //Si hay un ganador finaliza el juego
                    if (FinJuego == true) {
                        break;
                    }
                }
            }
        }
        MostrarResultado();
        in.close();
        out.close();
        socket.close();
    }
}
