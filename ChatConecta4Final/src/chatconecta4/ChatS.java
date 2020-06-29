package chatconecta4;

import java.io.*;
import java.net.*;
import java.util.*;

public class ChatS extends javax.swing.JFrame{
   ArrayList clienteOutputStreams;
   ArrayList<String> usuarios;

   public class ThreadCliente implements Runnable{
       BufferedReader reader;
       Socket socket;
       PrintWriter cliente;

       public ThreadCliente(Socket clienteSocket, PrintWriter usuario){
            cliente = usuario;
            try{
                socket = clienteSocket;
                InputStreamReader isReader = new InputStreamReader(socket.getInputStream());
                reader = new BufferedReader(isReader);
            }
            catch (Exception ex){
                ta_chat.append("Error inesperado \n");
            }
       }

       @Override
       public void run(){
            String msj, conectar = "Conectado", disconnect = "Desconectado", chat = "Chat" ;
            String[] dato;
            
            try{
                while ((msj = reader.readLine()) != null){
                    ta_chat.append("Recibido: " + msj + "\n");
                    dato = msj.split(":");
                    for (String token:dato){
                        ta_chat.append(token + "\n");
                    }
                    if (dato[2].equals(conectar)){
                        EnviarTodos((dato[0] + ":" + dato[1] + ":" + chat));
                        AgregarUsuario(dato[0]);
                    } 
                    else if (dato[2].equals(disconnect)){
                        EnviarTodos((dato[0] + ":se ha desconectado." + ":" + chat));
                        EliminarUsuario(dato[0]);
                    } 
                    else if (dato[2].equals(chat)){
                        EnviarTodos(msj);
                    } 
                    else{
                        ta_chat.append("");
                    }
                } 
             } 
             catch (Exception ex){
                ta_chat.append("Se perdio la conexión. \n");
                ex.printStackTrace();
                clienteOutputStreams.remove(cliente);
             } 
	} 
    }

    public ChatS(){
        initComponents();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        ta_chat = new javax.swing.JTextArea();
        b_start = new javax.swing.JButton();
        b_end = new javax.swing.JButton();
        b_users = new javax.swing.JButton();
        b_clear = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Chat - Servidor");
        setName("server"); // NOI18N
        setResizable(false);

        ta_chat.setColumns(20);
        ta_chat.setRows(5);
        jScrollPane1.setViewportView(ta_chat);

        b_start.setText("INICIAR");
        b_start.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                b_startActionPerformed(evt);
            }
        });

        b_end.setText("DETENER");
        b_end.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                b_endActionPerformed(evt);
            }
        });

        b_users.setText("Usuarios conectados");
        b_users.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                b_usersActionPerformed(evt);
            }
        });

        b_clear.setText("Limpiar");
        b_clear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                b_clearActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(b_start, javax.swing.GroupLayout.DEFAULT_SIZE, 149, Short.MAX_VALUE)
                            .addComponent(b_end, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 236, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(b_users, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(b_clear, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 340, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(b_start)
                    .addComponent(b_users))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(b_clear)
                    .addComponent(b_end))
                .addGap(22, 22, 22))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void b_endActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_b_endActionPerformed
        try{
            Thread.sleep(5000);                 //5000 milliseconds is five second.
        } 
        catch(InterruptedException ex) {Thread.currentThread().interrupt();}
        EnviarTodos("Se detuvo el servidor. Los usuarios se desconectaran.\n:Chat");
        ta_chat.append("Servidor detenido \n");
        ta_chat.setText("");
    }//GEN-LAST:event_b_endActionPerformed

    private void b_startActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_b_startActionPerformed
        Thread iniciar = new Thread(new IniciarServidor());
        iniciar.start();
        ta_chat.append("Servidor iniciado\n");
    }//GEN-LAST:event_b_startActionPerformed

    private void b_usersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_b_usersActionPerformed
        ta_chat.append("\n Usuarios conectados : \n");
        for (String usuarioActual : usuarios){
            ta_chat.append(usuarioActual);
            ta_chat.append("\n");
        }    
        
    }//GEN-LAST:event_b_usersActionPerformed

    private void b_clearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_b_clearActionPerformed
        ta_chat.setText("");
    }//GEN-LAST:event_b_clearActionPerformed

    public static void main(String args[]){
        java.awt.EventQueue.invokeLater(new Runnable(){
            @Override
            public void run() {
                new ChatS().setVisible(true);
            }
        });
    }
    
    public class IniciarServidor implements Runnable{
        @Override
        public void run(){
            clienteOutputStreams = new ArrayList();
            usuarios = new ArrayList();
            try{
                ServerSocket serverSock = new ServerSocket(5000);
                while (true) {
                    Socket clienteSocket = serverSock.accept();
                    PrintWriter writer = new PrintWriter(clienteSocket.getOutputStream());
                    clienteOutputStreams.add(writer);
                    Thread listener = new Thread(new ThreadCliente(clienteSocket, writer));
                    listener.start();
                    ta_chat.append("Estableciendo una conexion... \n");
                }
            }
            catch (Exception ex){
                ta_chat.append("Error estableciendo conexión. \n");
            }
        }
    }
    
    public void AgregarUsuario (String dato){
        String msj, agregar = ": :Conectado", ok = "Servidor: :OK", nombre = dato;
        ta_chat.append("Antes " + nombre + " agregado. \n");
        usuarios.add(nombre);
        ta_chat.append("Despues " + nombre + " agregado. \n");
        String[] lista = new String[(usuarios.size())];
        usuarios.toArray(lista);
        for (String token:lista){
            msj = (token + agregar);
            EnviarTodos(msj);
        }
        EnviarTodos(ok);
    }
    
    public void EliminarUsuario (String dato){
        String msj, agregar = ": :Conectado", ok = "Servidor: :OK", nombre = dato;
        usuarios.remove(nombre);
        String[] lista = new String[(usuarios.size())];
        usuarios.toArray(lista);
        for (String token:lista){
            msj = (token + agregar);
            EnviarTodos(msj);
        }
        EnviarTodos(ok);
    }
    
    public void EnviarTodos(String msj){
	Iterator it = clienteOutputStreams.iterator();
        while (it.hasNext()){
            try{
                PrintWriter writer = (PrintWriter) it.next();
		writer.println(msj);
		ta_chat.append("Enviando: " + msj + "\n");
                writer.flush();
                ta_chat.setCaretPosition(ta_chat.getDocument().getLength());
            } 
            catch (Exception ex){
		ta_chat.append("Error al enviar a todos. \n");
            }
        } 
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton b_clear;
    private javax.swing.JButton b_end;
    private javax.swing.JButton b_start;
    private javax.swing.JButton b_users;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea ta_chat;
    // End of variables declaration//GEN-END:variables
}
