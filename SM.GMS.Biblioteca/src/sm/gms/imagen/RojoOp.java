/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sm.gms.imagen;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Gloria
 */
public class RojoOp extends BufferedImageOpAdapter {

    private int umbral;

    public RojoOp(int umbral) {
        this.umbral = umbral;
    }

    @Override
    public BufferedImage filter(BufferedImage src, BufferedImage dest) {
        if (src == null) {
            throw new NullPointerException("src image is null");
        }
        if (dest == null) {
            dest = createCompatibleDestImage(src, null);
        }   
        WritableRaster srcRaster = src.getRaster();
        WritableRaster destRaster = dest.getRaster();
        int[] pixelComp = new int[srcRaster.getNumBands()];
        int[] pixelCompDest = new int[srcRaster.getNumBands()];
        int sampleR, sampleG, sampleB, valor;

        for (int x = 0; x < src.getWidth(); x++) {
            for (int y = 0; y < src.getHeight(); y++) {
                srcRaster.getPixel(x, y, pixelComp);
                sampleR = pixelComp[0];
                sampleG = pixelComp[1];
                sampleB = pixelComp[2];
                if ((sampleR - sampleG - sampleB) < this.umbral){
                    valor = (sampleR + sampleG + sampleB) / 3;
                    for (int i = 0; i < srcRaster.getNumBands(); i++)
                        pixelCompDest[i] = valor;
                }
                else 
                    pixelCompDest = pixelComp;
                destRaster.setPixel(x, y, pixelCompDest);
            }
        }
        return dest;
    }
}
