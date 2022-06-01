/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sm.gms.imagen;

import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;

/**
 *
 * @author Gloria
 */
class BufferedImageOpAdapter implements BufferedImageOp {

    public BufferedImage filter(BufferedImage src, BufferedImage dest){
        return null;
    }

    @Override
    public Rectangle2D getBounds2D(BufferedImage src) {
        return src.getRaster().getBounds();
    }

    @Override
    public BufferedImage createCompatibleDestImage(BufferedImage src, ColorModel destCM) {
        if (destCM == null) 
            destCM = src.getColorModel();
        WritableRaster wr = destCM.createCompatibleWritableRaster(src.getWidth(), src.getHeight());
        return new BufferedImage(destCM, wr, destCM.isAlphaPremultiplied(), null);
    }

    @Override
    public Point2D getPoint2D(Point2D srcPt, Point2D dstPt) {
        if (dstPt == null) {
            dstPt = (Point2D) srcPt.clone();
        } else {
            dstPt.setLocation(srcPt);
        }
        return dstPt;
    }

    @Override
    public RenderingHints getRenderingHints() {
        return null;
    }
}
