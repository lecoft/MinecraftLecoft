/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package minecraft;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import javax.swing.JOptionPane;

/**
 *
 * @author Sairi
 */
public class Cliente implements Runnable{
    private Socket cliente;
    private ObjectInputStream oisNet;
    private ObjectOutputStream oosNet;
    private int puerto=5000;
    Minecraft m;
    
    public Cliente(String host, Minecraft mine){
       int i=0;
       while(i==0){	
            System.out.println("Esperando por el servidor . . ."); i=1;
            try {
                    cliente=new Socket(host, puerto);
            } catch ( IOException e) {
                    System.out.println("Fallo creacion Socket"); i=0;
            }
       }
       System.out.println("Connectado al servidor.");
       m=mine;
       try {
                oisNet = new ObjectInputStream(cliente.getInputStream());
                oosNet = new ObjectOutputStream(cliente.getOutputStream()); 
       } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Error al crear los flujos de objeto"+e);
       }
       (new Thread(this)).start();
    }
    public void escribeRed(int ...a) {     
        try {
               oosNet.writeObject(a);        
               oosNet.flush();
        } catch (IOException ex) { ex.printStackTrace(); }
    }

    @Override
    public void run() {
        Object obj=null;
	int j;
        System.out.println("run");
   	for(;;){
    		j=0;
    		try {
                        obj=oisNet.readObject();	
    		} catch (IOException e) {
			System.out.println("IO ex"+e);
         		j=1;
                } catch (ClassNotFoundException ex) {
                     	System.out.println("Class no found"+ex);
			j=1;
		} 
    		if (j==0 && obj!=null) {
			//lr.leeRed(obj);
                    int[] cord=(int[])obj;
                    m.Update(cord[0],cord[1],cord[2],cord[3]);
                }//if
        }//for    
    }
}
