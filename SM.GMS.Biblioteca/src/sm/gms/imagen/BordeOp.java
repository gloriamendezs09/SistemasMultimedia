/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sm.gms.imagen;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

/**
 *
 * @author Gloria
 */
public class BordeOp extends BufferedImageOpAdapter{
   private int umbral;
   private Color color;

    public BordeOp (int umbral, Color color) {
        this.umbral = umbral;
        this.color = color;
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
        if (srcRaster.getNumBands() != 3){
            System.err.println("Se necesitan 3 bandas para realizar la operación");
            return null;
        }
        WritableRaster destRaster = dest.getRaster();
        int bandas = srcRaster.getNumBands();
        int[] pixelCompDest = new int[bandas];        
        int[] pixelComp = new int[bandas];
        int valor;

        for (int x = 1; x < src.getWidth(); x++) {
            for (int y = 1; y < src.getHeight(); y++) {
                srcRaster.getPixel(x, y, pixelComp);
                if (calcularAlfa(srcRaster, x, y, pixelComp) >= this.umbral){
                    pixelCompDest[0] = this.color.getRed();
                    pixelCompDest[1] = this.color.getGreen();
                    pixelCompDest[2] = this.color.getBlue();
                    destRaster.setPixel(x, y, pixelCompDest);
                } else {
                    valor = calcularGrisMedio(pixelComp[0], pixelComp[1], pixelComp[2]);
                    for (int i = 0; i < bandas; i++)
                        destRaster.setSample(x, y, i, valor);
                }
            }
        }
        return dest;
    }
    
    private int calcularAlfa (WritableRaster srcRaster, int x, int y, int[] pixelComp){
        srcRaster.getPixel(x, y, pixelComp);
        int valorCentral = this.calcularGrisMedio(pixelComp[0], pixelComp[1], pixelComp[2]);
        srcRaster.getPixel(x-1, y, pixelComp);
        int valorSuperior = this.calcularGrisMedio(pixelComp[0], pixelComp[1], pixelComp[2]);
        int maximo = valorCentral - valorSuperior;
        srcRaster.getPixel(x, y-1, pixelComp);
        int valorIzquierdo = this.calcularGrisMedio(pixelComp[0], pixelComp[1], pixelComp[2]);
        if (maximo < (valorCentral - valorIzquierdo))
            maximo = valorCentral - valorIzquierdo;
        return maximo;
    }
    
    private int calcularGrisMedio (int rojo, int verde, int azul){
        return (rojo + verde + azul) / 3;
    }
}
