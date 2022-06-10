/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sm.gms.figuras;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import static java.awt.BasicStroke.CAP_ROUND;
import static java.awt.BasicStroke.JOIN_MITER;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import javafx.geometry.BoundingBox;

/**
 *
 * @author glori
 */
public class Figura {
    // Variable que almacena el color que se utiliza para pintar el fondo
    private Color fondo = Color.black;
    // Variable que almacena el color que se utiliza para pintar los bordes
    private Color borde = Color.red;
    // Variable que almacena el grosor que se utiliza para pintar
    private int grosor = 1;
    // Variable trazo construida con el grosor anterior que se le pasa a la variable g2d
    private Stroke trazo = new BasicStroke((float) grosor, CAP_ROUND, JOIN_MITER);
    //
    float patronDiscontinuidad[] = {10.0f, 7.0f};
    // Variable que almacena si la figura está rellena o no
    private boolean relleno = false;
    // Variable que almacena si la figura está transparente o no
    private boolean transparencia = false;
    // Variable que almacena si la figura está renderizada o no
    private boolean alisar = false; 
    // Variable que define la composición utilizada para la transparencia de la figura
    private Composite comp = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
    // Variable que define la renderización utilizada para la figura
    private RenderingHints render = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    //
    Shape figura;
    //
    Rectangle2D bordeFigura;
    //
    String nombre;

    /**
     * Método que devuelve la variable c de la clase
     *
     * @return c variable de la clase
     */
    public Color getFondo () {
        return fondo;
    }

    /**
     * Método que cambia el valor de la variable c de la clase
     *
     * @param c nuevo valor de c
     */
    public void setFondo (Color fondo) {
        this.fondo = fondo;
    }

    /**
     * Método que devuelve la variable c de la clase
     *
     * @return c variable de la clase
     */
    public Color getBorde () {
        return borde;
    }

    /**
     * Método que cambia el valor de la variable c de la clase
     *
     * @param c nuevo valor de c
     */
    public void setBorde (Color borde) {
        this.borde = borde;
    }

    /**
     * Método que devuelve la variable c de la clase
     *
     * @return c variable de la clase
     */
    public int getGrosor () {
        return grosor;
    }

    /**
     * Método que cambia el valor de la variable c de la clase
     *
     * @param c nuevo valor de c
     */
    public void setGrosor (int grosor) {
        this.grosor = grosor;
    }

    /**
     * Método que devuelve la variable c de la clase
     *
     * @return c variable de la clase
     */
    public Stroke getTrazo () {
        return trazo;
    }

    /**
     * Método que cambia el valor de la variable c de la clase
     *
     * @param c nuevo valor de c
     */
    public void setTrazo (Stroke trazo) {
        this.trazo = trazo;
    }

    /**
     * Método que devuelve la variable c de la clase
     *
     * @return c variable de la clase
     */
    public float[] getPatronDiscontinuidad () {
        return patronDiscontinuidad;
    }

    /**
     * Método que cambia el valor de la variable c de la clase
     *
     * @param c nuevo valor de c
     */
    public void setPatronDiscontinuidad (float[] patronDiscontinuidad) {
        this.patronDiscontinuidad = patronDiscontinuidad;
    }

    /**
     * Método que devuelve la variable c de la clase
     *
     * @return c variable de la clase
     */
    public boolean isRelleno () {
        return relleno;
    }

    /**
     * Método que cambia el valor de la variable c de la clase
     *
     * @param c nuevo valor de c
     */
    public void setRelleno (boolean relleno) {
        this.relleno = relleno;
    }

    /**
     * Método que devuelve la variable c de la clase
     *
     * @return c variable de la clase
     */
    public boolean isTransparencia () {
        return transparencia;
    }

    /**
     * Método que cambia el valor de la variable c de la clase
     *
     * @param c nuevo valor de c
     */
    public void setTransparencia (boolean transparencia) {
        this.transparencia = transparencia;
    }

    /**
     * Método que devuelve la variable c de la clase
     *
     * @return c variable de la clase
     */
    public boolean isAlisar () {
        return alisar;
    }

    /**
     * Método que cambia el valor de la variable c de la clase
     *
     * @param c nuevo valor de c
     */
    public void setAlisar (boolean alisar) {
        this.alisar = alisar;
    }

    public Composite getComp () {
        return comp;
    }

    public void setComp (Composite comp) {
        this.comp = comp;
    }

    public RenderingHints getRender () {
        return render;
    }

    public void setRender (RenderingHints render) {
        this.render = render;
    }

    public Shape getFigura () {
        return figura;
    }

    public void setFigura (Shape figura) {
        this.figura = figura;
    }

    public Rectangle2D getBordeFigura(){
        return bordeFigura;
    }

    public void setBordeFigura(Rectangle2D bordeFigura){
        this.bordeFigura = bordeFigura;
    }
    
    public void setNombre(String nombre){
        this.nombre = nombre;
    }

    public Figura (Color fondo, Color borde, int grosor, Stroke trazo, float patronDisc[], boolean relleno, boolean transparencia, boolean alisar){
        this.fondo = fondo;
        this.borde = borde;
        this.grosor = grosor;
        this.trazo = trazo;
        this.patronDiscontinuidad = patronDisc;
        this.relleno = relleno;
        this.transparencia = transparencia;
        this.alisar = alisar;
        this.bordeFigura = null;
    }

    public Figura (){
        this.fondo = Color.BLACK;
        this.borde = Color.BLACK;
        this.grosor = 1;
        this.trazo = new BasicStroke();
        this.relleno = false;
        this.transparencia = false;
        this.alisar = false;
        this.bordeFigura = null;
    }

    public void pintar (Graphics2D g2d){
        g2d.setStroke(trazo);
        if (transparencia) {
            g2d.setComposite(comp);
        }
        if (alisar) {
            g2d.setRenderingHints(render);
        }
        g2d.setPaint(fondo);
        if (relleno)
            g2d.fill(figura);
        g2d.setPaint(borde);
        g2d.draw(figura);
        if (bordeFigura != null){
            g2d.setPaint(Color.black);
            g2d.draw(bordeFigura);
        }
    }

    /**
     * Método que devuelve la figura que se encuenta en un punto p
     *
     * @param p Punto en el que hay que buscar la figura
     * @return s si existe una figura en el punto p devuelve dicha figura null
     * si no existe una figura en el punto p devuelve nulo
     */
    public boolean seleccionada (Point2D p) {
        if (figura.contains(p))
            return true;
        else
            return false;
    }

    public void setFigura (Point2D p1, Point2D p2){}

    public void setCurva (Point2D p){}

    public void moverFigura (Point2D p){}

    public void marcarFigura (){
        Rectangle2D bordes = new Rectangle2D.Float((float)this.figura.getBounds2D().getX() - 2.0f, (float)this.figura.getBounds2D().getY() - 2.0f,
                                            (float)this.figura.getBounds2D().getWidth() + 4.0f, (float)this.figura.getBounds2D().getHeight() + 4.0f);
        this.setBordeFigura(bordes);
    }
    
    @Override
    public String toString(){
        return nombre;
    }

}
