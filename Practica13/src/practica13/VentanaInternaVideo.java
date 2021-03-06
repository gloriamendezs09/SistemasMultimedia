/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package practica13;

import java.awt.Dimension;
import java.io.File;
import java.util.ArrayList;
import sm.gms.eventos.VentanaInternaListener;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.MediaPlayerEventListener;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

/**
 *
 * @author Gloria
 */
public class VentanaInternaVideo extends javax.swing.JInternalFrame {

    private EmbeddedMediaPlayer vlcplayer = null;
    private File ficheroVideo;
    
    /**
     * Creates new form VentanaInternaVideo
     */
    private VentanaInternaVideo(File ficheroVideo) {
        initComponents();
        this.setSize(new Dimension(300, 400));
        this.ficheroVideo = ficheroVideo;
        EmbeddedMediaPlayerComponent aVisual = new EmbeddedMediaPlayerComponent();
        getContentPane().add(aVisual, java.awt.BorderLayout.CENTER);
        vlcplayer = aVisual.getMediaPlayer();
    }
    
    public static VentanaInternaVideo getInstance(File ficheroVideo){
        VentanaInternaVideo ventana = new VentanaInternaVideo(ficheroVideo);
        if (ventana != null)
            return ventana;
        else
            return null;
    }
    
    public void addMediaPlayerListener (MediaPlayerEventListener ml){
        if (vlcplayer != null)
            vlcplayer.addMediaPlayerEventListener(ml);
    }

    public void play(){
        if (vlcplayer != null)
            if (vlcplayer.isPlayable()) // Para el caso en el que se haya pausado el video
                vlcplayer.play();
            else
                vlcplayer.playMedia(ficheroVideo.getAbsolutePath());
    }
    
    public void stop(){
        if (vlcplayer != null) {
            if (vlcplayer.isPlaying())
                vlcplayer.pause();
            else
                vlcplayer.stop();
        }
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameClosed(evt);
            }
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeactivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeiconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameIconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameOpened(javax.swing.event.InternalFrameEvent evt) {
            }
        });

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formInternalFrameClosed(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosed
        vlcplayer.stop();
        vlcplayer = null;
    }//GEN-LAST:event_formInternalFrameClosed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
