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
import java.awt.geom.QuadCurve2D;

/**
 *
 * @author Gloria
 */
public class Linea extends Figura{

    Line2D linea;

    public Linea(Point2D p1, Point2D p2){
        super();
        linea = new Line2D.Float(p1, p2);
        super.setFigura(linea);
        super.setNombre("Linea");
    }

    public Linea(Point2D p1, Point2D p2, Color fondo, Color borde, int grosor, Stroke trazo, float patronDisc[], boolean relleno, boolean transparencia, boolean alisar){
        super(fondo, borde, grosor, trazo, patronDisc, relleno, transparencia, alisar);
        linea = new Line2D.Float(p1, p2);
        super.setFigura(linea);
        super.setNombre("Linea");
    }

    /**
     * Devuelve si existe una linea a una distancia menor de 2.0 con respecto a
     * un punto p
     *
     * @param p Punto en el que hay que buscar la figura
     * @return true si existe una linea a menos de 2.0 de distancia de p false
     * si no existe una linea a menos de 2.0 de distancia de p
     */
    public boolean containsLine(Point2D p) {
        return this.linea.ptLineDist(p) <= 4.0;
    }

    @Override
    public void pintar(Graphics2D g2d){
        g2d.setStroke(super.getTrazo());
        if (super.isTransparencia()) {
            g2d.setComposite(super.getComp());
        }
        if (super.isAlisar()) {
            g2d.setRenderingHints(super.getRender());
        }
        g2d.setPaint(super.getBorde());
        g2d.draw(this.linea);
    }

    @Override
    public boolean seleccionada(Point2D p){
        if (containsLine(p))
            return true;
        else
            return false;
    }

    @Override
    public void setFigura(Point2D p1, Point2D p2){
        linea.setLine(p1, p2);
    }

    @Override
    public void moverFigura (Point2D p){
        double dx = p.getX() - this.linea.getX1();
        double dy = p.getY() - this.linea.getY1();
        Point2D newp2 = new Point2D.Double(this.linea.getX2() + dx, this.linea.getY2() + dy);
        this.linea.setLine(p, newp2);
    }

}
