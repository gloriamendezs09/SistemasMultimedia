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
public class Curva extends Figura{

    public QuadCurve2D curva;

    public Curva (Point2D p){
        super();
        curva = new QuadCurve2D.Float((float) p.getX(), (float) p.getY(),
                                (float) p.getX(), (float) p.getY(),
                                (float) p.getX(), (float) p.getY());
        super.setFigura(curva);
        super.setNombre("Curva");
    }

    public Curva (Point2D p, Color fondo, Color borde, int grosor, Stroke trazo, float patronDisc[], boolean relleno, boolean transparencia, boolean alisar){
        super(fondo, borde, grosor, trazo, patronDisc, relleno, transparencia, alisar);
        curva = new QuadCurve2D.Float((float) p.getX(), (float) p.getY(),
                                (float) p.getX(), (float) p.getY(),
                                (float) p.getX(), (float) p.getY());
        super.setFigura(curva);
        super.setNombre("Curva");
    }

    @Override
    public void setFigura (Point2D p1, Point2D p2){
        curva.setCurve((float) p1.getX(), (float) p1.getY(),
                        (float) p1.getX(), (float) p1.getY(),
                        (float) p2.getX(), (float) p2.getY());
    }

    @Override
    public void setCurva (Point2D p){
        curva.setCurve((float) curva.getX1(), (float) curva.getY1(),
                        (float) p.getX(), (float) p.getY(),
                        (float) curva.getX2(), (float) curva.getY2());
    }

    @Override
    public void moverFigura (Point2D p){
        Point2D p2 = new Point2D.Double(curva.getX2() + p.getX() - curva.getX1(),
                                        curva.getY2() + p.getY() - curva.getY1());
        curva.setCurve(p, curva.getCtrlPt(), p2);
    }

    public void marcarFigura(){
        super.setBordeFigura(curva.getBounds2D());
    }

}
