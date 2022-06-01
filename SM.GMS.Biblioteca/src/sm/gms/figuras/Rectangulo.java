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
import java.awt.geom.Point2D;
import java.awt.geom.QuadCurve2D;
import java.awt.geom.Rectangle2D;

/**
 *
 * @author glori
 */
public class Rectangulo extends Figura{

    Rectangle2D rectangulo;

    public Rectangulo(Point2D p){
        super();
        rectangulo = new Rectangle2D.Float((float)p.getX(), (float)p.getY(), 0.0f, 0.0f);
        super.setFigura(rectangulo);
    }

    public Rectangulo(Point2D p, Color fondo, Color borde, int grosor, Stroke trazo, float patronDisc[], boolean relleno, boolean transparencia, boolean alisar){
        super(fondo, borde, grosor, trazo, patronDisc, relleno, transparencia, alisar);
        rectangulo = new Rectangle2D.Float((float)p.getX(), (float)p.getY(), 0.0f, 0.0f);
        super.setFigura(rectangulo);
    }

    @Override
    public void setFigura (Point2D p1, Point2D p2){
        rectangulo.setFrameFromDiagonal(p1, p2);
    }

    @Override
    public void moverFigura (Point2D p){
        rectangulo.setFrame(p.getX(), p.getY(), rectangulo.getWidth(), rectangulo.getHeight());
    }

}
