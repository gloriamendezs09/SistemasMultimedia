/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package sm.gms.iu;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import static java.awt.BasicStroke.CAP_BUTT;
import static java.awt.BasicStroke.CAP_ROUND;
import static java.awt.BasicStroke.JOIN_MITER;
import static java.awt.BasicStroke.JOIN_ROUND;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.QuadCurve2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.WritableRaster;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFileChooser;
import sm.gms.eventos.LienzoEvent;
import sm.gms.eventos.LienzoListener;
import sm.gms.figuras.Curva;
import sm.gms.figuras.Elipse;
import sm.gms.figuras.Figura;
import sm.gms.figuras.Linea;
import sm.gms.figuras.Rectangulo;
import sm.gms.figuras.TrazoLibre;

/**
 *
 * @author Gloria Méndez Sánchez
 */
public class Lienzo2D extends javax.swing.JPanel {
    
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
    float patronDiscClip[] = {10.0f, 7.0f};
    // Variable trazo construida con el grosor anterior que se le pasa a la variable g2d
    private final Stroke trazoClip = new BasicStroke((float) 3, CAP_BUTT, JOIN_ROUND, 1.0f, patronDiscClip, 0.0f);
    // Variable que almacena el modo del lienzo: dibujo o movimiento
    // Si mover = false -> modo dibujo
    // Si mover = true -> modo movimiento
    private boolean mover = false;
    // Variable que almacena el modo para pintar: con volcado o sin el
    // Si volcado = false -> Se pintan las figuras sin volcarlas en la imagen
    // Si volcado = true -> Se vuelcan las figuras pintadas y/o las que se estén pintando
    private boolean volcado = false;
    // Variable que almacena si el botón tintado está pulsado o no 
    private boolean tintado = false;
    // Variable que almacena si el botón filtroRojo está pulsado o no 
    private boolean filtroRojo = false;
    //
    private int posX = 0;
    //
    private int posY = 0;

    // Definición de un tipo enumerado para las distintas herramientas utilizadas
    private enum Figuras {
        TRAZO, LINEA, RECTANGULO, ELIPSE, CURVA;
    }
    // Variable que almacena el tipo de herramienta utilizada para pintar
    private Figuras figura = Figuras.TRAZO;
    // Variable que almacena las figuras pintadas
    private List<Figura> vShape = new ArrayList();
    // Variable que almacena el valor del punto en el que se ha presionado
    private Point2D pPressed = new Point2D.Float(0, 0);
    // Variable que almacena el valor del punto en el que se está arrastrando
    private Point2D pDragged = new Point2D.Float(0, 0);
    // Variable que almacena la figura que se está pintando
    private Figura figuraPintada;
    // Variable utilizada para saber que hay que pintar de la curva: si 
    // pulsacion = 1 se dibuja la linea sin curvatura, si pulsacion = 2 se
    // curva la linea
    private int pulsacion = 1;
    // Variable que almacena la figura seleccionada (para el modo movimiento)
    private Figura figuraSeleccionada = null;
    // 
    private BufferedImage imagen = new BufferedImage(100, 200, BufferedImage.TYPE_INT_RGB);
    //
    private Graphics2D g2dImagen;
    //
    private Rectangle2D clip;
    //
    ArrayList<LienzoListener> lienzoEventListeners = new ArrayList();

    public Color getFondo() {
        return fondo;
    }

    public void setFondo(Color fondo) {
        this.fondo = fondo;
    }

    public Color getBorde() {
        return borde;
    }

    public void setBorde(Color borde) {
        this.borde = borde;
    }

    public int getGrosor() {
        return grosor;
    }

    public void setGrosor(int grosor) {
        this.grosor = grosor;
    }

    public float[] getPatronDiscontinuidad() {
        return patronDiscontinuidad;
    }

    public void setPatronDiscontinuidad(float[] patronDiscontinuidad) {
        this.patronDiscontinuidad = patronDiscontinuidad;
    }

    public boolean isRelleno() {
        return relleno;
    }

    public void setRelleno(boolean relleno) {
        this.relleno = relleno;
    }

    public boolean isTransparencia() {
        return transparencia;
    }

    public void setTransparencia(boolean transparencia) {
        this.transparencia = transparencia;
    }

    public boolean isAlisar() {
        return alisar;
    }

    public void setAlisar(boolean alisar) {
        this.alisar = alisar;
    }

    public Composite getComp() {
        return comp;
    }

    public void setComp(Composite comp) {
        this.comp = comp;
    }

    public RenderingHints getRender() {
        return render;
    }

    public void setRender(RenderingHints render) {
        this.render = render;
    }

    /**
     * Método que devuelve la variable mover de la clase
     *
     * @return mover variable de la clase
     */
    public boolean isMover() {
        return mover;
    }

    /**
     * Método que cambia el valor de la variable mover de la clase
     *
     * @param mover nuevo valor de mover
     */
    public void setMover(boolean mover) {
        this.mover = mover;
    }

    /**
     * Método que devuelve la variable tintado de la clase
     *
     * @return tintado variable de la clase
     */
    public boolean isTintado() {
        return this.tintado;
    }

    /**
     * Método que cambia el valor de la variable tintado de la clase
     *
     * @param tintado nuevo valor de tintado
     */
    public void setTintado(boolean tintado) {
        this.tintado = tintado;
    }

    /**
     * Método que devuelve la variable filtroRojo de la clase
     *
     * @return filtroRojo variable de la clase
     */
    public boolean isFiltroRojo() {
        return this.filtroRojo;
    }

    /**
     * Método que cambia el valor de la variable filtroRojo de la clase
     *
     * @param filtroRojo nuevo valor de filtroRojo
     */
    public void setFiltroRojo(boolean filtroRojo) {
        this.filtroRojo = filtroRojo;
    }

    /**
     * Método que devuelve la variable volcado de la clase
     *
     * @return volcado variable de la clase
     */
    public boolean isVolcado() {
        return volcado;
    }

    /**
     * Método que cambia el valor de la variable volcado de la clase
     *
     * @param volcado nuevo valor de volcado
     */
    public void setVolcado(boolean volcado) {
        this.volcado = volcado;
        if (volcado && this.imagen != null) {
            this.volcarImagen();
        }
    }

    /**
     * Método que devuelve la variable posX de la clase
     *
     * @return posX variable de la clase
     */
    public int getPosX() {
        return this.posX;
    }

    /**
     * Método que cambia el valor de la variable posX de la clase
     *
     * @param posX nuevo valor de posX
     */
    public void setPosX(int posX) {
        this.posX = posX;
    }

    public List<Figura> getVShape(){
        return this.vShape;
    }

    /**
     * Método que devuelve la variable posY de la clase
     *
     * @return posY variable de la clase
     */
    public int getPosY() {
        return this.posY;
    }

    /**
     * Método que cambia el valor de la variable posY de la clase
     *
     * @param posY nuevo valor de posY
     */
    public void setPosY(int posY) {
        this.posY = posY;
    }

    /**
     * Método que devuelve la variable figura de la clase
     *
     * @return figura variable de la clase
     */
    public Figuras getFigura() {
        return figura;
    }

    /**
     * Método que cambia el valor de la variable figura de la clase
     *
     * @param figura nuevo valor de figura
     */
    public void setFigura(Figuras figura) {
        this.figura = figura;
    }

    /**
     * Método que cambia el valor de la variable figura por "TRAZO"
     */
    public void setFiguraTrazo() {
        this.figura = Figuras.TRAZO;
    }

    /**
     * Método que cambia el valor de la variable figura por "LINEA"
     */
    public void setFiguraLinea() {
        this.figura = Figuras.LINEA;
    }

    /**
     * Método que cambia el valor de la variable figura por "RECTANGULO"
     */
    public void setFiguraRectangulo() {
        this.figura = Figuras.RECTANGULO;
    }

    /**
     * Método que cambia el valor de la variable figura por "ELIPSE"
     */
    public void setFiguraElipse() {
        this.figura = Figuras.ELIPSE;
    }

    /**
     * Método que cambia el valor de la variable figura por "CURVA"
     */
    public void setFiguraCurva() {
        this.figura = Figuras.CURVA;
    }

    public BufferedImage getImagenGuardar() {
        BufferedImage imagenSalida = new BufferedImage(imagen.getWidth(), imagen.getHeight(), imagen.getType());
        /*Si guardo una imagen previamente guardada da fallo: Unknown image type 0*/
        this.paint(imagenSalida.createGraphics());
        this.vShape.clear();
        return imagenSalida;
    }

    public BufferedImage getImagen() {
        return this.imagen;
    }

    public void setImagen(BufferedImage imagen) {
        this.imagen = imagen;
        if (imagen != null) {
            setPreferredSize(new Dimension(imagen.getWidth(), imagen.getHeight()));
            this.clip = new Rectangle2D.Float(0, 0, 0, 0);
        }
    }

    /**
     * Método que devuelve la figura que se encuenta en un punto p
     *
     * @param p Punto en el que hay que buscar la figura
     * @return s si existe una figura en el punto p devuelve dicha figura null
     * si no existe una figura en el punto p devuelve nulo
     */
    private Figura getSelectedShape(Point2D p) {
        if (!vShape.isEmpty()) {
            int i = vShape.size() - 1;
            while (i >= 0) {
                Figura s = vShape.get(i);
                if (s.seleccionada(p))
                    return s;
                i--;
            }
        }
        return null;
    }

    public void marcarFigura (Shape s){
        if (mover == true)
            for (Figura f: vShape)
                if (f.getFigura().equals(s)){
                    f.marcarFigura();
                }
    }

    public void addLienzoListener(LienzoListener listener) {
        if (listener != null) {
            this.lienzoEventListeners.add(listener);
        }
    }

    private void notifyShapeAddedEvent(LienzoEvent evt) {
        if (!lienzoEventListeners.isEmpty()) {
            for (LienzoListener listener : lienzoEventListeners) {
                listener.shapeAdded(evt);
            }
        }
    }

    private void notifyPropertyChangeEvent(LienzoEvent evt) {
        if (!lienzoEventListeners.isEmpty()) {
            for (LienzoListener listener : lienzoEventListeners) {
                listener.propertyChange(evt);
            }
        }
    }

    private void pintarClip(Graphics2D g2d) {
        if (this.clip != null) {
            int width = imagen.getWidth(), height = imagen.getHeight();
            if (this.getWidth() < imagen.getWidth()) {
                width = this.getWidth();
                g2d.setColor(Color.red);
            }
            if (this.getHeight() < imagen.getHeight()) {
                height = this.getHeight();
                g2d.setColor(Color.red);
            }
            this.clip.setFrame(0, 0, width, height);
            g2d.setStroke(trazoClip);
            g2d.setClip(this.clip);
            g2d.draw(clip);
        }
    }

    private void volcarImagen() {
        BufferedImage img = this.getImagenGuardar();
        this.setImagen(img);
        this.vShape.clear();
    }

    /**
     * Método utilizado para dibujar las figuras geométricas
     *
     * @param g Instancia de la clase Graphics
     */
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;
        if (imagen != null) {
            g2d.drawImage(imagen, this.posX, this.posY, this);
            pintarClip(g2d);
        }
        if (!(vShape.isEmpty())) {
            for (Figura s : vShape) {
                s.pintar(g2d);
            }
        }
    }

    /**
     * Creates new form Lienzo2D
     */
    public Lienzo2D() {
        initComponents();
        this.setPreferredSize(new Dimension(400, 400));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                formMouseDragged(evt);
            }
        });
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                formMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                formMouseReleased(evt);
            }
        });
        setLayout(new java.awt.BorderLayout());
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Método para cuando se presiona el ratón Guarda la posición en la que se
     * está presionando el ratón y actúa en función de la variable mover: - Si
     * mover = false: se crea una nueva figura del tipo especificado y se añade
     * a la lista de figuras vShape - Si mover = true: se llama al método
     * getSelectedShape para obtener la figura seleccionada (en caso de que se
     * haya seleccionado alguna)
     *
     * @param evt
     */
    private void formMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMousePressed
        pPressed = evt.getPoint();
        if (!mover) {
            switch (figura) {
                case TRAZO:
                    figuraPintada = new TrazoLibre(pPressed, fondo, borde, grosor, trazo, patronDiscontinuidad, relleno, transparencia, alisar);
                    break;
                case LINEA:
                    figuraPintada = new Linea(pPressed, pPressed, fondo, borde, grosor, trazo, patronDiscontinuidad, relleno, transparencia, alisar);
                    break;
                case RECTANGULO:
                    figuraPintada = new Rectangulo(pPressed, fondo, borde, grosor, trazo, patronDiscontinuidad, relleno, transparencia, alisar);
                    break;
                case ELIPSE:
                    figuraPintada = new Elipse(pPressed, fondo, borde, grosor, trazo, patronDiscontinuidad, relleno, transparencia, alisar);
                    break;
                case CURVA:
                    if (pulsacion == 1)
                        figuraPintada = new Curva(pPressed, fondo, borde, grosor, trazo, patronDiscontinuidad, relleno, transparencia, alisar);
                    break;
            }
            vShape.add(figuraPintada);
        } else if (mover)
            figuraSeleccionada = this.getSelectedShape(pPressed);
    }//GEN-LAST:event_formMousePressed

    /**
     * Método para cuando se arrastra el ratón Guarda la posición en la que está
     * arrastrándose el ratón y actúa en función de la variable mover: - Si
     * mover = false: se obtiene la última figura añadida al vector de formas y
     * se sigue pintando - Si mover = true: se cambia la posición de la figura
     * seleccionada
     *
     * @param evt
     */
    private void formMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseDragged
        pDragged = evt.getPoint();
        if (!mover && !vShape.isEmpty()) {
            if (figuraPintada.getFigura() instanceof GeneralPath)
                figuraPintada.setFigura(pDragged, null);
            else if (figuraPintada.getFigura() instanceof QuadCurve2D && pulsacion == 2)
                figuraPintada.setCurva(pDragged);
            else
                figuraPintada.setFigura(pPressed, pDragged);
        } else if (mover && figuraSeleccionada != null)
            figuraSeleccionada.moverFigura(pDragged);
        this.repaint();
    }//GEN-LAST:event_formMouseDragged

    /**
     * Método para cuando se suelta el ratón Hace una llamada al método
     * formMouseDragged de la clase
     *
     * @param evt
     */
    private void formMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseReleased
        if (pulsacion == 1 && this.figura == Figuras.CURVA) {
            pulsacion = 2;
        } else if (pulsacion == 2 && this.figura == Figuras.CURVA) {
            pulsacion = 1;
        }        
        if (volcado && (pulsacion == 1 && this.figura == Figuras.CURVA))
            this.volcarImagen();
        if (!volcado){
            LienzoEvent evtLienzo = new LienzoEvent(this, this.vShape.get(vShape.size() - 1).getFigura(), null);
            notifyShapeAddedEvent(evtLienzo);
        }
    }//GEN-LAST:event_formMouseReleased


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
