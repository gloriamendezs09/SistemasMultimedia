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
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.QuadCurve2D;

/**
 *
 * @author glori
 */
public class TrazoLibre extends Figura{

    GeneralPath trazoLibre;

    public TrazoLibre(Point2D p){
        super();
        trazoLibre = new GeneralPath();        
        trazoLibre.moveTo(p.getX(), p.getY());
        super.setFigura(trazoLibre);
    }

    public TrazoLibre (Point2D p, Color fondo, Color borde, int grosor, Stroke trazo, float patronDisc[], boolean relleno, boolean transparencia, boolean alisar){
        super(fondo, borde, grosor, trazo, patronDisc, relleno, transparencia, alisar);
        trazoLibre = new GeneralPath();        
        trazoLibre.moveTo(p.getX(), p.getY());
        super.setFigura(trazoLibre);
    }

    /**
     * Devuelve una transformación aplicada a todos los puntos de un GeneralPath
     * con respecto a un punto p. Se utiliza para mover un trazo
     *
     * @param p punto al que hay que mover la variable trazoLibre
     */
    public void setLocationGeneralPath(Point2D p) {
        AffineTransform at;
        Point2D pActual = trazoLibre.getCurrentPoint();
        at = AffineTransform.getTranslateInstance(p.getX() - pActual.getX(), p.getY() - pActual.getY());
        trazoLibre.transform(at);
    }

    @Override
    public void setFigura(Point2D p1, Point2D p2){
        trazoLibre.lineTo(p1.getX(), p1.getY());
    }

    @Override
    public void moverFigura (Point2D p){
        setLocationGeneralPath(p);
    }

}
