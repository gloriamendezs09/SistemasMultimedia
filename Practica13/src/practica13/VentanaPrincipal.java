/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package practica13;

import java.awt.Color;
import java.awt.Shape;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BandCombineOp;
import java.awt.image.BufferedImage;
import java.awt.image.ByteLookupTable;
import java.awt.image.ColorConvertOp;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.ConvolveOp;
import java.awt.image.DataBuffer;
import java.awt.image.Kernel;
import java.awt.image.LookupOp;
import java.awt.image.LookupTable;
import java.awt.image.RescaleOp;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.swing.Icon;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import sm.gms.eventos.LienzoEvent;
import sm.gms.eventos.LienzoListener;
import sm.gms.eventos.VentanaInternaAdapter;
import sm.gms.eventos.VentanaInternaEvent;
import sm.gms.figuras.Figura;
import sm.gms.imagen.BordeOp;
import sm.gms.imagen.NegativoOp;
import sm.gms.imagen.PosterizarOp;
import sm.gms.imagen.RojoOp;
import sm.image.EqualizationOp;
import sm.image.KernelProducer;
import sm.image.LookupTableProducer;
import sm.image.SepiaOp;
import sm.image.TintOp;
import sm.image.color.YCbCrColorSpace;
import sm.sound.SMClipPlayer;
import sm.sound.SMSoundRecorder;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;

/**
 *
 * @author Gloria Méndez Sánchez
 */
public class VentanaPrincipal extends javax.swing.JFrame {

    // Variable que almacena la ventana interna activa en cada momento
    private JInternalFrame activa = null;
    // Variable utilizada para abrir y guardar los lienzos
    private JFileChooser dlg = new JFileChooser();
    //
    private BufferedImage copiaImagen;
    //
    private int a = 0;
    //
    private int b = 0;
    //
    private SMClipPlayer player = null;
    //
    private SMSoundRecorder recorder = null;
    // Variable que almacena 1 si el botón inicioYPausa está en función de
    // reproducción por primera vez, 2 si está en función de reproducción
    // el resto de veces y 3 si está en función de pausa
    private int inicioOPausa = 1;
    //
    private Icon iconoInicio = new javax.swing.ImageIcon(getClass().getResource("/iconos/play24x24.png"));
    //
    private Icon iconoPausa = new javax.swing.ImageIcon(getClass().getResource("/iconos/pausa24x24.png"));
    // 
    private long tiempoPausa = 0;
    // 
    private File audioGrabado;

    /**
     * Método que almacena la ventana seleccionada del escritorio en la variable
     * activa de la clase
     */
    private boolean ventanaActiva() {
        activa = escritorio.getSelectedFrame();
        return (activa != null);
    }

    /*
     * Manejador de la clase ventanaInterna que actualiza los botones de la
     * Ventana Principal en función de la Ventana Interna que se seleccione
     */
    private class miManejadorVentanaInterna extends VentanaInternaAdapter {

        @Override
        public void ventanaInternaChange(VentanaInternaEvent evt) {
            if (ventanaActiva() && activa instanceof VentanaInternaImagen){
                VentanaInternaImagen ventana = (VentanaInternaImagen)activa;
                volcado.setSelected(ventana.getLienzo().isVolcado());
                relleno.setSelected(ventana.getLienzo().isRelleno());
                transparencia.setSelected(ventana.getLienzo().isTransparencia());
                alisar.setSelected(ventana.getLienzo().isAlisar());
                spinnerGrosor.setValue((Object) ventana.getLienzo().getGrosor());
                volcado.setSelected(ventana.getLienzo().isVolcado());
                spinnerValorA.setEnabled(funcionTrapezoidal.isSelected());
                spinnerValorA.setEnabled(funcionTrapezoidal.isSelected());
                tintado.setSelected(ventana.getLienzo().isTintado());
                sliderTintado.setEnabled(tintado.isSelected());
                filtroRojo.setSelected(ventana.getLienzo().isFiltroRojo());
                sliderFiltroRojo.setEnabled(filtroRojo.isSelected());
                listaFiguras.removeAllItems();
                for (Figura s: ventana.getLienzo().getVShape())
                    listaFiguras.addItem(s);
            }
        }

    }

    private class miManejadorRaton implements MouseMotionListener {

        Point2D raton;

        @Override
        public void mouseDragged(MouseEvent e){}

        @Override
        public void mouseMoved(MouseEvent e){
            Point2D raton = new Point2D.Double(e.getX(), e.getY());
            setRaton(raton);
            setColor(raton);
        }

        public void setRaton(Point2D raton){
            coordenadasPixel.setText("Posición: (" + raton.getX() + ", " + raton.getY() + ")");
        }

        public void setColor (Point2D raton){
            if (ventanaActiva() && activa instanceof VentanaInternaImagen){
                VentanaInternaImagen ventana = (VentanaInternaImagen)activa;
                if (raton.getX() < ventana.getLienzo().getImagen().getWidth() && raton.getY() < ventana.getLienzo().getImagen().getHeight())
                    valorPixel.setText("Valor: " + ventana.getLienzo().getImagen().getRGB((int)raton.getX(), (int)raton.getY()));
            }
        }
    }


    /*
     * Manejador de la clase ventanaInterna que actualiza los botones de la
     * Ventana Principal en función de la Ventana Interna que se seleccione
     */
    private class miManejadorAudio implements LineListener {

        @Override
        public void update(LineEvent event) {
            if (event.getType() == LineEvent.Type.START) {
                inicioYPausa.setIcon(iconoPausa);
                inicioOPausa = 2;
            }
            if (event.getType() == LineEvent.Type.STOP) {
                inicioYPausa.setIcon(iconoInicio);
                inicioOPausa = 1;
            }
            if (event.getType() == LineEvent.Type.CLOSE) {
                inicioOPausa = 1;
            }
            escritorio.repaint();
        }
    }

    private class miManejadorLienzo implements LienzoListener{
        @Override
        public void shapeAdded(LienzoEvent evt){
            listaFiguras.addItem(evt.getForma());
            listaFiguras.setSelectedItem(evt.getForma());
        }

        @Override
        public void propertyChange (LienzoEvent evt){
        }
    }

        /*
     * Manejador de la clase ventanaInterna que actualiza los botones de la
     * Ventana Principal en función de la Ventana Interna que se seleccione
     */
    private class miManejadorVideo extends MediaPlayerEventAdapter {

        @Override
        public void playing(MediaPlayer mediaPlayer){
            inicioVideo.setEnabled(false);
            finVideo.setEnabled(true);
        }
        
        @Override
        public void paused(MediaPlayer mediaPlayer){
            inicioVideo.setEnabled(true);
            finVideo.setEnabled(false);
        }
        
        public void finished(MediaPlayer mediaPlayer){
            paused(mediaPlayer);
        }
    }
    
    /**
     * Creates new form ventanaPrincipal
     */
    public VentanaPrincipal() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jMenuItem1 = new javax.swing.JMenuItem();
        panelHerramientas = new javax.swing.JPanel();
        nuevo = new javax.swing.JButton();
        abrir = new javax.swing.JButton();
        guardar = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JSeparator();
        volcado = new javax.swing.JToggleButton();
        duplicar = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        trazoLibre = new javax.swing.JButton();
        linea = new javax.swing.JButton();
        rectangulo = new javax.swing.JButton();
        elipse = new javax.swing.JButton();
        curva = new javax.swing.JButton();
        mover = new javax.swing.JToggleButton();
        jSeparator3 = new javax.swing.JSeparator();
        colorFondo = new sm.gms.colores.VentanaPrincipal();
        selectorFondo = new javax.swing.JButton();
        colorBorde = new sm.gms.colores.VentanaPrincipal();
        selectorBorde = new javax.swing.JButton();
        jSeparator4 = new javax.swing.JSeparator();
        spinnerGrosor = new javax.swing.JSpinner();
        relleno = new javax.swing.JToggleButton();
        transparencia = new javax.swing.JToggleButton();
        alisar = new javax.swing.JToggleButton();
        listaFiguras = new javax.swing.JComboBox<>();
        jSeparator5 = new javax.swing.JSeparator();
        inicioYPausa = new javax.swing.JButton();
        parar = new javax.swing.JButton();
        duracionAudio = new javax.swing.JLabel();
        listaAudios = new javax.swing.JComboBox<>();
        grabar = new javax.swing.JToggleButton();
        jSeparator6 = new javax.swing.JSeparator();
        inicioVideo = new javax.swing.JButton();
        finVideo = new javax.swing.JButton();
        camaraWeb = new javax.swing.JButton();
        capturar = new javax.swing.JButton();
        ventanaMedio = new javax.swing.JPanel();
        escritorio = new javax.swing.JDesktopPane();
        jPanel1 = new javax.swing.JPanel();
        panelBarraEstado = new javax.swing.JPanel();
        barraEstado = new javax.swing.JLabel();
        coordenadasPixel = new javax.swing.JLabel();
        valorPixel = new javax.swing.JLabel();
        panelProcesamiento = new javax.swing.JPanel();
        panelProcesamientoBrilloyContraste = new javax.swing.JPanel();
        sliderBrillo = new javax.swing.JSlider();
        sliderContraste = new javax.swing.JSlider();
        panelProcesamientoFiltros = new javax.swing.JPanel();
        comboBoxFiltros = new javax.swing.JComboBox<>();
        sliderFiltro = new javax.swing.JSlider();
        panelProcesamientoTransformaciones = new javax.swing.JPanel();
        contraste = new javax.swing.JButton();
        iluminar = new javax.swing.JButton();
        oscurecer = new javax.swing.JButton();
        panelProcesamientoFiltros6 = new javax.swing.JPanel();
        funcionCuadratica = new javax.swing.JToggleButton();
        sliderFuncionCuadratica = new javax.swing.JSlider();
        funcionTrapezoidal = new javax.swing.JToggleButton();
        spinnerValorA = new javax.swing.JSpinner();
        spinnerValorB = new javax.swing.JSpinner();
        panelProcesamientoRotacionYEscalado = new javax.swing.JPanel();
        sliderRotacion = new javax.swing.JSlider();
        rotacion90 = new javax.swing.JButton();
        rotacion180 = new javax.swing.JButton();
        rotacion270 = new javax.swing.JButton();
        aumentar = new javax.swing.JButton();
        disminuir = new javax.swing.JButton();
        panelProcesamientoFiltros1 = new javax.swing.JPanel();
        bandas = new javax.swing.JButton();
        cambioEspacio = new javax.swing.JComboBox<>();
        panelProcesamientoFiltros2 = new javax.swing.JPanel();
        panelProcesamientoFiltros3 = new javax.swing.JPanel();
        tintado = new javax.swing.JToggleButton();
        sliderTintado = new javax.swing.JSlider();
        panelProcesamientoFiltros4 = new javax.swing.JPanel();
        filtroRojo = new javax.swing.JToggleButton();
        sliderFiltroRojo = new javax.swing.JSlider();
        panelProcesamientoFiltros5 = new javax.swing.JPanel();
        pintarBordes = new javax.swing.JToggleButton();
        sliderBordes = new javax.swing.JSlider();
        sliderPosterizado = new javax.swing.JSlider();
        combinar = new javax.swing.JButton();
        sepia = new javax.swing.JButton();
        ecualizacion = new javax.swing.JButton();
        negativo = new javax.swing.JButton();
        menuOpciones = new javax.swing.JMenuBar();
        archivo = new javax.swing.JMenu();
        archivo_nuevo = new javax.swing.JMenuItem();
        archivo_abrir = new javax.swing.JMenuItem();
        archivo_guardar = new javax.swing.JMenuItem();
        edicion = new javax.swing.JMenu();
        activarBarraEstado = new javax.swing.JCheckBoxMenuItem();
        ayuda = new javax.swing.JMenu();
        acercaDe = new javax.swing.JMenuItem();

        jMenuItem1.setText("jMenuItem1");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        panelHerramientas.setBackground(new java.awt.Color(204, 204, 204));
        panelHerramientas.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        panelHerramientas.setLayout(new javax.swing.BoxLayout(panelHerramientas, javax.swing.BoxLayout.LINE_AXIS));

        nuevo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/nuevo.png"))); // NOI18N
        nuevo.setToolTipText("Nuevo");
        nuevo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nuevoActionPerformed(evt);
            }
        });
        panelHerramientas.add(nuevo);

        abrir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/abrir.png"))); // NOI18N
        abrir.setToolTipText("Abrir");
        abrir.setMaximumSize(new java.awt.Dimension(30, 30));
        abrir.setMinimumSize(new java.awt.Dimension(30, 30));
        abrir.setPreferredSize(new java.awt.Dimension(30, 30));
        abrir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                abrirActionPerformed(evt);
            }
        });
        panelHerramientas.add(abrir);

        guardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/guardar.png"))); // NOI18N
        guardar.setToolTipText("Guardar");
        guardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                guardarActionPerformed(evt);
            }
        });
        panelHerramientas.add(guardar);

        jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator2.setMaximumSize(new java.awt.Dimension(20, 30));
        jSeparator2.setMinimumSize(new java.awt.Dimension(20, 30));
        jSeparator2.setPreferredSize(new java.awt.Dimension(20, 30));
        panelHerramientas.add(jSeparator2);

        volcado.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/volcado.png"))); // NOI18N
        volcado.setToolTipText("Volcado");
        volcado.setMaximumSize(new java.awt.Dimension(30, 30));
        volcado.setMinimumSize(new java.awt.Dimension(30, 30));
        volcado.setPreferredSize(new java.awt.Dimension(30, 30));
        volcado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                volcadoActionPerformed(evt);
            }
        });
        panelHerramientas.add(volcado);

        duplicar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/duplicar.png"))); // NOI18N
        duplicar.setToolTipText("Duplicar");
        duplicar.setMaximumSize(new java.awt.Dimension(30, 30));
        duplicar.setMinimumSize(new java.awt.Dimension(30, 30));
        duplicar.setPreferredSize(new java.awt.Dimension(30, 30));
        duplicar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                duplicarActionPerformed(evt);
            }
        });
        panelHerramientas.add(duplicar);

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator1.setBorder(javax.swing.BorderFactory.createCompoundBorder());
        jSeparator1.setMaximumSize(new java.awt.Dimension(20, 30));
        jSeparator1.setMinimumSize(new java.awt.Dimension(20, 30));
        jSeparator1.setPreferredSize(new java.awt.Dimension(20, 30));
        panelHerramientas.add(jSeparator1);

        trazoLibre.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/trazo.png"))); // NOI18N
        trazoLibre.setToolTipText("Trazo Libre");
        trazoLibre.setMaximumSize(new java.awt.Dimension(30, 30));
        trazoLibre.setMinimumSize(new java.awt.Dimension(30, 30));
        trazoLibre.setPreferredSize(new java.awt.Dimension(30, 30));
        trazoLibre.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                trazoLibreActionPerformed(evt);
            }
        });
        panelHerramientas.add(trazoLibre);

        linea.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/linea.png"))); // NOI18N
        linea.setToolTipText("Línea");
        linea.setMaximumSize(new java.awt.Dimension(30, 30));
        linea.setMinimumSize(new java.awt.Dimension(30, 30));
        linea.setPreferredSize(new java.awt.Dimension(30, 30));
        linea.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lineaActionPerformed(evt);
            }
        });
        panelHerramientas.add(linea);

        rectangulo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/rectangulo.png"))); // NOI18N
        rectangulo.setToolTipText("Cuadrado");
        rectangulo.setMaximumSize(new java.awt.Dimension(30, 30));
        rectangulo.setMinimumSize(new java.awt.Dimension(30, 30));
        rectangulo.setPreferredSize(new java.awt.Dimension(30, 30));
        rectangulo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rectanguloActionPerformed(evt);
            }
        });
        panelHerramientas.add(rectangulo);

        elipse.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/elipse.png"))); // NOI18N
        elipse.setToolTipText("Círculo");
        elipse.setMaximumSize(new java.awt.Dimension(30, 30));
        elipse.setMinimumSize(new java.awt.Dimension(30, 30));
        elipse.setPreferredSize(new java.awt.Dimension(30, 30));
        elipse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                elipseActionPerformed(evt);
            }
        });
        panelHerramientas.add(elipse);

        curva.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/curva.png"))); // NOI18N
        curva.setToolTipText("Elipse");
        curva.setMaximumSize(new java.awt.Dimension(30, 30));
        curva.setMinimumSize(new java.awt.Dimension(30, 30));
        curva.setPreferredSize(new java.awt.Dimension(30, 30));
        curva.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                curvaActionPerformed(evt);
            }
        });
        panelHerramientas.add(curva);

        mover.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/seleccion.png"))); // NOI18N
        mover.setToolTipText("Mover");
        mover.setMaximumSize(new java.awt.Dimension(30, 30));
        mover.setMinimumSize(new java.awt.Dimension(30, 30));
        mover.setPreferredSize(new java.awt.Dimension(30, 30));
        mover.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                moverActionPerformed(evt);
            }
        });
        panelHerramientas.add(mover);

        jSeparator3.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator3.setMaximumSize(new java.awt.Dimension(20, 30));
        jSeparator3.setMinimumSize(new java.awt.Dimension(20, 30));
        jSeparator3.setPreferredSize(new java.awt.Dimension(20, 30));
        panelHerramientas.add(jSeparator3);

        colorFondo.setToolTipText("color de fondo");
        panelHerramientas.add(colorFondo);

        selectorFondo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/colorFondo.png"))); // NOI18N
        selectorFondo.setToolTipText("Selector del color de fondo");
        selectorFondo.setMaximumSize(new java.awt.Dimension(30, 30));
        selectorFondo.setMinimumSize(new java.awt.Dimension(30, 30));
        selectorFondo.setPreferredSize(new java.awt.Dimension(30, 30));
        selectorFondo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectorFondoActionPerformed(evt);
            }
        });
        panelHerramientas.add(selectorFondo);

        colorBorde.setToolTipText("color de bordes");
        panelHerramientas.add(colorBorde);

        selectorBorde.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/colorBorde.png"))); // NOI18N
        selectorBorde.setToolTipText("Selector del color del borde");
        selectorBorde.setMaximumSize(new java.awt.Dimension(30, 30));
        selectorBorde.setMinimumSize(new java.awt.Dimension(30, 30));
        selectorBorde.setPreferredSize(new java.awt.Dimension(30, 30));
        selectorBorde.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectorBordeActionPerformed(evt);
            }
        });
        panelHerramientas.add(selectorBorde);

        jSeparator4.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator4.setMaximumSize(new java.awt.Dimension(20, 30));
        jSeparator4.setMinimumSize(new java.awt.Dimension(20, 30));
        jSeparator4.setPreferredSize(new java.awt.Dimension(20, 30));
        panelHerramientas.add(jSeparator4);

        spinnerGrosor.setModel(new javax.swing.SpinnerNumberModel(1, 1, null, 1));
        spinnerGrosor.setToolTipText("Grosor");
        spinnerGrosor.setMaximumSize(new java.awt.Dimension(50, 30));
        spinnerGrosor.setMinimumSize(new java.awt.Dimension(50, 30));
        spinnerGrosor.setPreferredSize(new java.awt.Dimension(50, 30));
        spinnerGrosor.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinnerGrosorStateChanged(evt);
            }
        });
        panelHerramientas.add(spinnerGrosor);

        relleno.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/rellenar.png"))); // NOI18N
        relleno.setToolTipText("Relleno");
        relleno.setMaximumSize(new java.awt.Dimension(30, 30));
        relleno.setMinimumSize(new java.awt.Dimension(30, 30));
        relleno.setPreferredSize(new java.awt.Dimension(30, 30));
        relleno.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rellenoActionPerformed(evt);
            }
        });
        panelHerramientas.add(relleno);

        transparencia.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/transparencia.png"))); // NOI18N
        transparencia.setToolTipText("Transparencia");
        transparencia.setMaximumSize(new java.awt.Dimension(30, 30));
        transparencia.setMinimumSize(new java.awt.Dimension(30, 30));
        transparencia.setPreferredSize(new java.awt.Dimension(30, 30));
        transparencia.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                transparenciaActionPerformed(evt);
            }
        });
        panelHerramientas.add(transparencia);

        alisar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/alisar.png"))); // NOI18N
        alisar.setToolTipText("Alisado");
        alisar.setMaximumSize(new java.awt.Dimension(30, 30));
        alisar.setMinimumSize(new java.awt.Dimension(30, 30));
        alisar.setPreferredSize(new java.awt.Dimension(30, 30));
        alisar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                alisarActionPerformed(evt);
            }
        });
        panelHerramientas.add(alisar);

        listaFiguras.setModel(new javax.swing.DefaultComboBoxModel<>(new Figura[] {}));
        listaFiguras.setToolTipText("Figuras");
        listaFiguras.setMaximumSize(new java.awt.Dimension(300, 30));
        listaFiguras.setMinimumSize(new java.awt.Dimension(300, 30));
        listaFiguras.setPreferredSize(new java.awt.Dimension(300, 30));
        listaFiguras.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                listaFigurasActionPerformed(evt);
            }
        });
        panelHerramientas.add(listaFiguras);

        jSeparator5.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator5.setMaximumSize(new java.awt.Dimension(20, 30));
        jSeparator5.setMinimumSize(new java.awt.Dimension(20, 30));
        jSeparator5.setPreferredSize(new java.awt.Dimension(20, 30));
        panelHerramientas.add(jSeparator5);

        inicioYPausa.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/play24x24.png"))); // NOI18N
        inicioYPausa.setToolTipText("Reproducir/Pausar audio");
        inicioYPausa.setMaximumSize(new java.awt.Dimension(30, 30));
        inicioYPausa.setMinimumSize(new java.awt.Dimension(30, 30));
        inicioYPausa.setPreferredSize(new java.awt.Dimension(30, 30));
        inicioYPausa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inicioYPausaActionPerformed(evt);
            }
        });
        panelHerramientas.add(inicioYPausa);

        parar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/stop24x24.png"))); // NOI18N
        parar.setToolTipText("Parar video");
        parar.setMaximumSize(new java.awt.Dimension(30, 30));
        parar.setMinimumSize(new java.awt.Dimension(30, 30));
        parar.setPreferredSize(new java.awt.Dimension(30, 30));
        parar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pararActionPerformed(evt);
            }
        });
        panelHerramientas.add(parar);

        duracionAudio.setMaximumSize(new java.awt.Dimension(40, 20));
        duracionAudio.setMinimumSize(new java.awt.Dimension(40, 20));
        duracionAudio.setPreferredSize(new java.awt.Dimension(40, 20));
        panelHerramientas.add(duracionAudio);

        listaAudios.setToolTipText("Lista Reproducción");
        listaAudios.setMaximumSize(new java.awt.Dimension(100, 30));
        listaAudios.setMinimumSize(new java.awt.Dimension(100, 30));
        listaAudios.setPreferredSize(new java.awt.Dimension(100, 30));
        listaAudios.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                listaAudiosActionPerformed(evt);
            }
        });
        panelHerramientas.add(listaAudios);

        grabar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/record24x24.png"))); // NOI18N
        grabar.setToolTipText("Volcado");
        grabar.setMaximumSize(new java.awt.Dimension(30, 30));
        grabar.setMinimumSize(new java.awt.Dimension(30, 30));
        grabar.setPreferredSize(new java.awt.Dimension(30, 30));
        grabar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                grabarActionPerformed(evt);
            }
        });
        panelHerramientas.add(grabar);

        jSeparator6.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator6.setMaximumSize(new java.awt.Dimension(20, 30));
        jSeparator6.setMinimumSize(new java.awt.Dimension(20, 30));
        jSeparator6.setPreferredSize(new java.awt.Dimension(20, 30));
        panelHerramientas.add(jSeparator6);

        inicioVideo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/play24x24.png"))); // NOI18N
        inicioVideo.setToolTipText("Reproducir video");
        inicioVideo.setMaximumSize(new java.awt.Dimension(30, 30));
        inicioVideo.setMinimumSize(new java.awt.Dimension(30, 30));
        inicioVideo.setPreferredSize(new java.awt.Dimension(30, 30));
        inicioVideo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inicioVideoActionPerformed(evt);
            }
        });
        panelHerramientas.add(inicioVideo);

        finVideo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/stop24x24.png"))); // NOI18N
        finVideo.setToolTipText("Parar video");
        finVideo.setMaximumSize(new java.awt.Dimension(30, 30));
        finVideo.setMinimumSize(new java.awt.Dimension(30, 30));
        finVideo.setPreferredSize(new java.awt.Dimension(30, 30));
        finVideo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                finVideoActionPerformed(evt);
            }
        });
        panelHerramientas.add(finVideo);

        camaraWeb.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/webcam.png"))); // NOI18N
        camaraWeb.setToolTipText("Iniciar webcam");
        camaraWeb.setMaximumSize(new java.awt.Dimension(30, 30));
        camaraWeb.setMinimumSize(new java.awt.Dimension(30, 30));
        camaraWeb.setPreferredSize(new java.awt.Dimension(30, 30));
        camaraWeb.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                camaraWebActionPerformed(evt);
            }
        });
        panelHerramientas.add(camaraWeb);

        capturar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/captura.png"))); // NOI18N
        capturar.setToolTipText("Hacer captura");
        capturar.setMaximumSize(new java.awt.Dimension(30, 30));
        capturar.setMinimumSize(new java.awt.Dimension(30, 30));
        capturar.setPreferredSize(new java.awt.Dimension(30, 30));
        capturar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                capturarActionPerformed(evt);
            }
        });
        panelHerramientas.add(capturar);

        getContentPane().add(panelHerramientas, java.awt.BorderLayout.PAGE_START);

        ventanaMedio.setLayout(new java.awt.BorderLayout());

        escritorio.setMinimumSize(new java.awt.Dimension(700, 500));

        javax.swing.GroupLayout escritorioLayout = new javax.swing.GroupLayout(escritorio);
        escritorio.setLayout(escritorioLayout);
        escritorioLayout.setHorizontalGroup(
            escritorioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 2058, Short.MAX_VALUE)
        );
        escritorioLayout.setVerticalGroup(
            escritorioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 965, Short.MAX_VALUE)
        );

        ventanaMedio.add(escritorio, java.awt.BorderLayout.CENTER);

        getContentPane().add(ventanaMedio, java.awt.BorderLayout.CENTER);

        jPanel1.setLayout(new java.awt.BorderLayout());

        panelBarraEstado.setBackground(new java.awt.Color(204, 204, 204));
        panelBarraEstado.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        panelBarraEstado.setLayout(new javax.swing.BoxLayout(panelBarraEstado, javax.swing.BoxLayout.LINE_AXIS));

        barraEstado.setMaximumSize(new java.awt.Dimension(100, 20));
        barraEstado.setMinimumSize(new java.awt.Dimension(100, 20));
        barraEstado.setPreferredSize(new java.awt.Dimension(100, 20));
        panelBarraEstado.add(barraEstado);

        coordenadasPixel.setMaximumSize(new java.awt.Dimension(130, 20));
        coordenadasPixel.setMinimumSize(new java.awt.Dimension(130, 20));
        coordenadasPixel.setPreferredSize(new java.awt.Dimension(130, 20));
        panelBarraEstado.add(coordenadasPixel);

        valorPixel.setMaximumSize(new java.awt.Dimension(100, 20));
        valorPixel.setMinimumSize(new java.awt.Dimension(100, 20));
        valorPixel.setPreferredSize(new java.awt.Dimension(100, 20));
        panelBarraEstado.add(valorPixel);

        jPanel1.add(panelBarraEstado, java.awt.BorderLayout.PAGE_END);

        panelProcesamiento.setMaximumSize(new java.awt.Dimension(2000, 100));
        panelProcesamiento.setMinimumSize(new java.awt.Dimension(987, 100));
        panelProcesamiento.setPreferredSize(new java.awt.Dimension(987, 100));
        panelProcesamiento.setLayout(new javax.swing.BoxLayout(panelProcesamiento, javax.swing.BoxLayout.LINE_AXIS));

        panelProcesamientoBrilloyContraste.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED), "Brillo y contraste", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Siemens Sans SC", 1, 14))); // NOI18N
        panelProcesamientoBrilloyContraste.setMaximumSize(new java.awt.Dimension(201, 90));
        panelProcesamientoBrilloyContraste.setMinimumSize(new java.awt.Dimension(201, 90));
        panelProcesamientoBrilloyContraste.setPreferredSize(new java.awt.Dimension(201, 90));
        panelProcesamientoBrilloyContraste.setLayout(new javax.swing.BoxLayout(panelProcesamientoBrilloyContraste, javax.swing.BoxLayout.LINE_AXIS));

        sliderBrillo.setMajorTickSpacing(255);
        sliderBrillo.setMaximum(255);
        sliderBrillo.setMinimum(-255);
        sliderBrillo.setPaintLabels(true);
        sliderBrillo.setPaintTicks(true);
        sliderBrillo.setToolTipText("Brillo");
        sliderBrillo.setValue(0);
        sliderBrillo.setMaximumSize(new java.awt.Dimension(90, 46));
        sliderBrillo.setMinimumSize(new java.awt.Dimension(90, 46));
        sliderBrillo.setPreferredSize(new java.awt.Dimension(90, 46));
        sliderBrillo.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                sliderBrilloStateChanged(evt);
            }
        });
        sliderBrillo.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                sliderBrilloFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                sliderBrilloFocusLost(evt);
            }
        });
        panelProcesamientoBrilloyContraste.add(sliderBrillo);

        sliderContraste.setMajorTickSpacing(19);
        sliderContraste.setMaximum(20);
        sliderContraste.setMinimum(1);
        sliderContraste.setMinorTickSpacing(4);
        sliderContraste.setPaintLabels(true);
        sliderContraste.setPaintTicks(true);
        sliderContraste.setToolTipText("Contraste");
        sliderContraste.setValue(1);
        sliderContraste.setMaximumSize(new java.awt.Dimension(90, 46));
        sliderContraste.setMinimumSize(new java.awt.Dimension(90, 46));
        sliderContraste.setPreferredSize(new java.awt.Dimension(90, 46));
        sliderContraste.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                sliderContrasteStateChanged(evt);
            }
        });
        sliderContraste.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                sliderContrasteFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                sliderContrasteFocusLost(evt);
            }
        });
        panelProcesamientoBrilloyContraste.add(sliderContraste);

        panelProcesamiento.add(panelProcesamientoBrilloyContraste);

        panelProcesamientoFiltros.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED), "Filtros", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Siemens Sans SC", 1, 14))); // NOI18N
        panelProcesamientoFiltros.setMaximumSize(new java.awt.Dimension(221, 90));
        panelProcesamientoFiltros.setMinimumSize(new java.awt.Dimension(221, 90));
        panelProcesamientoFiltros.setPreferredSize(new java.awt.Dimension(221, 90));
        panelProcesamientoFiltros.setLayout(new javax.swing.BoxLayout(panelProcesamientoFiltros, javax.swing.BoxLayout.LINE_AXIS));

        comboBoxFiltros.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Media", "Binomial", "Enfoque", "Relieve", "Laplaciano" }));
        comboBoxFiltros.setToolTipText("Filtros");
        comboBoxFiltros.setMaximumSize(new java.awt.Dimension(100, 26));
        comboBoxFiltros.setMinimumSize(new java.awt.Dimension(100, 26));
        comboBoxFiltros.setPreferredSize(new java.awt.Dimension(100, 26));
        comboBoxFiltros.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboBoxFiltrosActionPerformed(evt);
            }
        });
        panelProcesamientoFiltros.add(comboBoxFiltros);

        sliderFiltro.setMajorTickSpacing(15);
        sliderFiltro.setMaximum(31);
        sliderFiltro.setMinimum(1);
        sliderFiltro.setPaintLabels(true);
        sliderFiltro.setPaintTicks(true);
        sliderFiltro.setToolTipText("Emborronamiento");
        sliderFiltro.setValue(1);
        sliderFiltro.setMaximumSize(new java.awt.Dimension(110, 46));
        sliderFiltro.setMinimumSize(new java.awt.Dimension(110, 46));
        sliderFiltro.setPreferredSize(new java.awt.Dimension(110, 46));
        sliderFiltro.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                sliderFiltroStateChanged(evt);
            }
        });
        sliderFiltro.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                sliderFiltroFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                sliderFiltroFocusLost(evt);
            }
        });
        panelProcesamientoFiltros.add(sliderFiltro);

        panelProcesamiento.add(panelProcesamientoFiltros);

        panelProcesamientoTransformaciones.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED), "Transformaciones", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Siemens Sans SC", 1, 14))); // NOI18N
        panelProcesamientoTransformaciones.setMaximumSize(new java.awt.Dimension(427, 90));
        panelProcesamientoTransformaciones.setMinimumSize(new java.awt.Dimension(427, 90));
        panelProcesamientoTransformaciones.setPreferredSize(new java.awt.Dimension(427, 90));
        panelProcesamientoTransformaciones.setLayout(new javax.swing.BoxLayout(panelProcesamientoTransformaciones, javax.swing.BoxLayout.LINE_AXIS));

        contraste.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/contraste.png"))); // NOI18N
        contraste.setToolTipText("Contraste");
        contraste.setMaximumSize(new java.awt.Dimension(40, 30));
        contraste.setMinimumSize(new java.awt.Dimension(40, 30));
        contraste.setPreferredSize(new java.awt.Dimension(40, 30));
        contraste.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                contrasteActionPerformed(evt);
            }
        });
        panelProcesamientoTransformaciones.add(contraste);

        iluminar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/iluminar.png"))); // NOI18N
        iluminar.setToolTipText("Iluminar");
        iluminar.setMaximumSize(new java.awt.Dimension(40, 30));
        iluminar.setMinimumSize(new java.awt.Dimension(40, 30));
        iluminar.setPreferredSize(new java.awt.Dimension(40, 30));
        iluminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                iluminarActionPerformed(evt);
            }
        });
        panelProcesamientoTransformaciones.add(iluminar);

        oscurecer.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/oscurecer.png"))); // NOI18N
        oscurecer.setToolTipText("Ocurecer");
        oscurecer.setMaximumSize(new java.awt.Dimension(40, 30));
        oscurecer.setMinimumSize(new java.awt.Dimension(40, 30));
        oscurecer.setPreferredSize(new java.awt.Dimension(40, 30));
        oscurecer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                oscurecerActionPerformed(evt);
            }
        });
        panelProcesamientoTransformaciones.add(oscurecer);

        panelProcesamientoFiltros6.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        panelProcesamientoFiltros6.setMaximumSize(new java.awt.Dimension(143, 60));
        panelProcesamientoFiltros6.setMinimumSize(new java.awt.Dimension(143, 60));
        panelProcesamientoFiltros6.setPreferredSize(new java.awt.Dimension(143, 60));
        panelProcesamientoFiltros6.setLayout(new javax.swing.BoxLayout(panelProcesamientoFiltros6, javax.swing.BoxLayout.LINE_AXIS));

        funcionCuadratica.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/cuadratica.png"))); // NOI18N
        funcionCuadratica.setToolTipText("Activar función cuadrática");
        funcionCuadratica.setMaximumSize(new java.awt.Dimension(40, 30));
        funcionCuadratica.setMinimumSize(new java.awt.Dimension(40, 30));
        funcionCuadratica.setPreferredSize(new java.awt.Dimension(40, 30));
        funcionCuadratica.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                funcionCuadraticaActionPerformed(evt);
            }
        });
        panelProcesamientoFiltros6.add(funcionCuadratica);

        sliderFuncionCuadratica.setMajorTickSpacing(127);
        sliderFuncionCuadratica.setMaximum(255);
        sliderFuncionCuadratica.setPaintLabels(true);
        sliderFuncionCuadratica.setPaintTicks(true);
        sliderFuncionCuadratica.setToolTipText("Valor del parámetro de la función cuadrática");
        sliderFuncionCuadratica.setValue(0);
        sliderFuncionCuadratica.setEnabled(false);
        sliderFuncionCuadratica.setMaximumSize(new java.awt.Dimension(90, 46));
        sliderFuncionCuadratica.setMinimumSize(new java.awt.Dimension(90, 46));
        sliderFuncionCuadratica.setPreferredSize(new java.awt.Dimension(90, 46));
        sliderFuncionCuadratica.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                sliderFuncionCuadraticaStateChanged(evt);
            }
        });
        sliderFuncionCuadratica.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                sliderFuncionCuadraticaFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                sliderFuncionCuadraticaFocusLost(evt);
            }
        });
        panelProcesamientoFiltros6.add(sliderFuncionCuadratica);

        panelProcesamientoTransformaciones.add(panelProcesamientoFiltros6);

        funcionTrapezoidal.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/función trapezoidal.png"))); // NOI18N
        funcionTrapezoidal.setToolTipText("Función trapezoidal");
        funcionTrapezoidal.setMaximumSize(new java.awt.Dimension(40, 30));
        funcionTrapezoidal.setMinimumSize(new java.awt.Dimension(40, 30));
        funcionTrapezoidal.setPreferredSize(new java.awt.Dimension(40, 30));
        funcionTrapezoidal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                funcionTrapezoidalActionPerformed(evt);
            }
        });
        panelProcesamientoTransformaciones.add(funcionTrapezoidal);

        spinnerValorA.setModel(new javax.swing.SpinnerNumberModel(128, 0, 255, 1));
        spinnerValorA.setToolTipText("Valor de \"a\"");
        spinnerValorA.setEnabled(false);
        spinnerValorA.setMaximumSize(new java.awt.Dimension(55, 30));
        spinnerValorA.setMinimumSize(new java.awt.Dimension(55, 30));
        spinnerValorA.setPreferredSize(new java.awt.Dimension(55, 30));
        spinnerValorA.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinnerValorAStateChanged(evt);
            }
        });
        panelProcesamientoTransformaciones.add(spinnerValorA);

        spinnerValorB.setModel(new javax.swing.SpinnerNumberModel(128, 0, 255, 1));
        spinnerValorB.setToolTipText("Valor de \"b\"");
        spinnerValorB.setEnabled(false);
        spinnerValorB.setMaximumSize(new java.awt.Dimension(55, 30));
        spinnerValorB.setMinimumSize(new java.awt.Dimension(55, 30));
        spinnerValorB.setPreferredSize(new java.awt.Dimension(55, 30));
        spinnerValorB.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinnerValorBStateChanged(evt);
            }
        });
        panelProcesamientoTransformaciones.add(spinnerValorB);

        panelProcesamiento.add(panelProcesamientoTransformaciones);

        panelProcesamientoRotacionYEscalado.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED), "Rotación y escalado", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Siemens Sans SC", 1, 14))); // NOI18N
        panelProcesamientoRotacionYEscalado.setMaximumSize(new java.awt.Dimension(316, 90));
        panelProcesamientoRotacionYEscalado.setMinimumSize(new java.awt.Dimension(316, 90));
        panelProcesamientoRotacionYEscalado.setPreferredSize(new java.awt.Dimension(316, 90));
        panelProcesamientoRotacionYEscalado.setLayout(new javax.swing.BoxLayout(panelProcesamientoRotacionYEscalado, javax.swing.BoxLayout.LINE_AXIS));

        sliderRotacion.setMajorTickSpacing(180);
        sliderRotacion.setMaximum(360);
        sliderRotacion.setMinorTickSpacing(90);
        sliderRotacion.setPaintLabels(true);
        sliderRotacion.setPaintTicks(true);
        sliderRotacion.setToolTipText("Variación rotación");
        sliderRotacion.setValue(0);
        sliderRotacion.setMaximumSize(new java.awt.Dimension(100, 46));
        sliderRotacion.setMinimumSize(new java.awt.Dimension(100, 46));
        sliderRotacion.setPreferredSize(new java.awt.Dimension(100, 46));
        sliderRotacion.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                sliderRotacionStateChanged(evt);
            }
        });
        sliderRotacion.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                sliderRotacionFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                sliderRotacionFocusLost(evt);
            }
        });
        panelProcesamientoRotacionYEscalado.add(sliderRotacion);

        rotacion90.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/rotacion90.png"))); // NOI18N
        rotacion90.setToolTipText("Rotación 90º");
        rotacion90.setMaximumSize(new java.awt.Dimension(40, 30));
        rotacion90.setMinimumSize(new java.awt.Dimension(40, 30));
        rotacion90.setPreferredSize(new java.awt.Dimension(40, 30));
        rotacion90.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rotacion90ActionPerformed(evt);
            }
        });
        panelProcesamientoRotacionYEscalado.add(rotacion90);

        rotacion180.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/rotacion180.png"))); // NOI18N
        rotacion180.setToolTipText("Rotación 180º");
        rotacion180.setMaximumSize(new java.awt.Dimension(40, 30));
        rotacion180.setMinimumSize(new java.awt.Dimension(40, 30));
        rotacion180.setPreferredSize(new java.awt.Dimension(40, 30));
        rotacion180.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rotacion180ActionPerformed(evt);
            }
        });
        panelProcesamientoRotacionYEscalado.add(rotacion180);

        rotacion270.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/rotacion270.png"))); // NOI18N
        rotacion270.setToolTipText("Rotación 270º");
        rotacion270.setMaximumSize(new java.awt.Dimension(40, 30));
        rotacion270.setMinimumSize(new java.awt.Dimension(40, 30));
        rotacion270.setPreferredSize(new java.awt.Dimension(40, 30));
        rotacion270.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rotacion270ActionPerformed(evt);
            }
        });
        panelProcesamientoRotacionYEscalado.add(rotacion270);

        aumentar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/aumentar.png"))); // NOI18N
        aumentar.setToolTipText("Aumentar");
        aumentar.setMaximumSize(new java.awt.Dimension(40, 30));
        aumentar.setMinimumSize(new java.awt.Dimension(40, 30));
        aumentar.setPreferredSize(new java.awt.Dimension(40, 30));
        aumentar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aumentarActionPerformed(evt);
            }
        });
        panelProcesamientoRotacionYEscalado.add(aumentar);

        disminuir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/disminuir.png"))); // NOI18N
        disminuir.setToolTipText("Disminuir");
        disminuir.setMaximumSize(new java.awt.Dimension(40, 30));
        disminuir.setMinimumSize(new java.awt.Dimension(40, 30));
        disminuir.setPreferredSize(new java.awt.Dimension(40, 30));
        disminuir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                disminuirActionPerformed(evt);
            }
        });
        panelProcesamientoRotacionYEscalado.add(disminuir);

        panelProcesamiento.add(panelProcesamientoRotacionYEscalado);

        panelProcesamientoFiltros1.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED), "Color", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Siemens Sans SC", 1, 14))); // NOI18N
        panelProcesamientoFiltros1.setMaximumSize(new java.awt.Dimension(136, 110));
        panelProcesamientoFiltros1.setMinimumSize(new java.awt.Dimension(136, 110));
        panelProcesamientoFiltros1.setPreferredSize(new java.awt.Dimension(136, 110));
        panelProcesamientoFiltros1.setLayout(new javax.swing.BoxLayout(panelProcesamientoFiltros1, javax.swing.BoxLayout.LINE_AXIS));

        bandas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/bandas.png"))); // NOI18N
        bandas.setToolTipText("Extracción de bandas");
        bandas.setMaximumSize(new java.awt.Dimension(40, 30));
        bandas.setMinimumSize(new java.awt.Dimension(40, 30));
        bandas.setPreferredSize(new java.awt.Dimension(40, 30));
        bandas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bandasActionPerformed(evt);
            }
        });
        panelProcesamientoFiltros1.add(bandas);

        cambioEspacio.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "RGB", "YCC", "GREY", "YCbCr" }));
        cambioEspacio.setToolTipText("Cambio de espacio");
        cambioEspacio.setMaximumSize(new java.awt.Dimension(80, 26));
        cambioEspacio.setMinimumSize(new java.awt.Dimension(80, 26));
        cambioEspacio.setPreferredSize(new java.awt.Dimension(80, 26));
        cambioEspacio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cambioEspacioActionPerformed(evt);
            }
        });
        panelProcesamientoFiltros1.add(cambioEspacio);

        panelProcesamiento.add(panelProcesamientoFiltros1);

        panelProcesamientoFiltros2.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED), " Operaciones", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Siemens Sans SC", 1, 14))); // NOI18N
        panelProcesamientoFiltros2.setMaximumSize(new java.awt.Dimension(792, 110));
        panelProcesamientoFiltros2.setMinimumSize(new java.awt.Dimension(792, 110));
        panelProcesamientoFiltros2.setName(""); // NOI18N
        panelProcesamientoFiltros2.setPreferredSize(new java.awt.Dimension(792, 110));
        panelProcesamientoFiltros2.setRequestFocusEnabled(false);
        panelProcesamientoFiltros2.setLayout(new javax.swing.BoxLayout(panelProcesamientoFiltros2, javax.swing.BoxLayout.LINE_AXIS));

        panelProcesamientoFiltros3.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        panelProcesamientoFiltros3.setMaximumSize(new java.awt.Dimension(143, 60));
        panelProcesamientoFiltros3.setMinimumSize(new java.awt.Dimension(143, 60));
        panelProcesamientoFiltros3.setPreferredSize(new java.awt.Dimension(143, 60));
        panelProcesamientoFiltros3.setLayout(new javax.swing.BoxLayout(panelProcesamientoFiltros3, javax.swing.BoxLayout.LINE_AXIS));

        tintado.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/tintar.png"))); // NOI18N
        tintado.setToolTipText("Activar tintado");
        tintado.setMaximumSize(new java.awt.Dimension(40, 30));
        tintado.setMinimumSize(new java.awt.Dimension(40, 30));
        tintado.setPreferredSize(new java.awt.Dimension(40, 30));
        tintado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tintadoActionPerformed(evt);
            }
        });
        panelProcesamientoFiltros3.add(tintado);

        sliderTintado.setMajorTickSpacing(5);
        sliderTintado.setMaximum(10);
        sliderTintado.setPaintLabels(true);
        sliderTintado.setPaintTicks(true);
        sliderTintado.setToolTipText("Valor de alfa para el tintado");
        sliderTintado.setValue(0);
        sliderTintado.setEnabled(false);
        sliderTintado.setMaximumSize(new java.awt.Dimension(90, 46));
        sliderTintado.setMinimumSize(new java.awt.Dimension(90, 46));
        sliderTintado.setPreferredSize(new java.awt.Dimension(90, 46));
        sliderTintado.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                sliderTintadoStateChanged(evt);
            }
        });
        sliderTintado.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                sliderTintadoFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                sliderTintadoFocusLost(evt);
            }
        });
        panelProcesamientoFiltros3.add(sliderTintado);

        panelProcesamientoFiltros2.add(panelProcesamientoFiltros3);

        panelProcesamientoFiltros4.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        panelProcesamientoFiltros4.setMaximumSize(new java.awt.Dimension(143, 60));
        panelProcesamientoFiltros4.setMinimumSize(new java.awt.Dimension(143, 60));
        panelProcesamientoFiltros4.setPreferredSize(new java.awt.Dimension(143, 60));
        panelProcesamientoFiltros4.setLayout(new javax.swing.BoxLayout(panelProcesamientoFiltros4, javax.swing.BoxLayout.LINE_AXIS));

        filtroRojo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/rojo.png"))); // NOI18N
        filtroRojo.setToolTipText("Activar filtro rojo");
        filtroRojo.setMaximumSize(new java.awt.Dimension(40, 30));
        filtroRojo.setMinimumSize(new java.awt.Dimension(40, 30));
        filtroRojo.setPreferredSize(new java.awt.Dimension(40, 30));
        filtroRojo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filtroRojoActionPerformed(evt);
            }
        });
        panelProcesamientoFiltros4.add(filtroRojo);

        sliderFiltroRojo.setMajorTickSpacing(64);
        sliderFiltroRojo.setMaximum(128);
        sliderFiltroRojo.setMinorTickSpacing(64);
        sliderFiltroRojo.setPaintLabels(true);
        sliderFiltroRojo.setPaintTicks(true);
        sliderFiltroRojo.setToolTipText("Umbral filtro rojo");
        sliderFiltroRojo.setValue(0);
        sliderFiltroRojo.setEnabled(false);
        sliderFiltroRojo.setMaximumSize(new java.awt.Dimension(90, 46));
        sliderFiltroRojo.setMinimumSize(new java.awt.Dimension(90, 46));
        sliderFiltroRojo.setPreferredSize(new java.awt.Dimension(90, 46));
        sliderFiltroRojo.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                sliderFiltroRojoStateChanged(evt);
            }
        });
        sliderFiltroRojo.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                sliderFiltroRojoFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                sliderFiltroRojoFocusLost(evt);
            }
        });
        panelProcesamientoFiltros4.add(sliderFiltroRojo);

        panelProcesamientoFiltros2.add(panelProcesamientoFiltros4);

        panelProcesamientoFiltros5.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        panelProcesamientoFiltros5.setMaximumSize(new java.awt.Dimension(143, 60));
        panelProcesamientoFiltros5.setMinimumSize(new java.awt.Dimension(143, 60));
        panelProcesamientoFiltros5.setPreferredSize(new java.awt.Dimension(143, 60));
        panelProcesamientoFiltros5.setLayout(new javax.swing.BoxLayout(panelProcesamientoFiltros5, javax.swing.BoxLayout.LINE_AXIS));

        pintarBordes.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/pintar lineas.png"))); // NOI18N
        pintarBordes.setToolTipText("Activar pintado de bordes");
        pintarBordes.setMaximumSize(new java.awt.Dimension(40, 30));
        pintarBordes.setMinimumSize(new java.awt.Dimension(40, 30));
        pintarBordes.setPreferredSize(new java.awt.Dimension(40, 30));
        pintarBordes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pintarBordesActionPerformed(evt);
            }
        });
        panelProcesamientoFiltros5.add(pintarBordes);

        sliderBordes.setMajorTickSpacing(16);
        sliderBordes.setMaximum(64);
        sliderBordes.setMinimum(16);
        sliderBordes.setPaintLabels(true);
        sliderBordes.setPaintTicks(true);
        sliderBordes.setToolTipText("Umbral para pintar los bordes");
        sliderBordes.setValue(16);
        sliderBordes.setEnabled(false);
        sliderBordes.setMaximumSize(new java.awt.Dimension(90, 46));
        sliderBordes.setMinimumSize(new java.awt.Dimension(90, 46));
        sliderBordes.setPreferredSize(new java.awt.Dimension(90, 46));
        sliderBordes.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                sliderBordesStateChanged(evt);
            }
        });
        sliderBordes.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                sliderBordesFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                sliderBordesFocusLost(evt);
            }
        });
        panelProcesamientoFiltros5.add(sliderBordes);

        panelProcesamientoFiltros2.add(panelProcesamientoFiltros5);

        sliderPosterizado.setMajorTickSpacing(9);
        sliderPosterizado.setMaximum(20);
        sliderPosterizado.setMinimum(2);
        sliderPosterizado.setPaintLabels(true);
        sliderPosterizado.setPaintTicks(true);
        sliderPosterizado.setToolTipText("Posterizado");
        sliderPosterizado.setValue(2);
        sliderPosterizado.setMaximumSize(new java.awt.Dimension(90, 46));
        sliderPosterizado.setMinimumSize(new java.awt.Dimension(90, 46));
        sliderPosterizado.setPreferredSize(new java.awt.Dimension(90, 46));
        sliderPosterizado.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                sliderPosterizadoStateChanged(evt);
            }
        });
        sliderPosterizado.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                sliderPosterizadoFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                sliderPosterizadoFocusLost(evt);
            }
        });
        panelProcesamientoFiltros2.add(sliderPosterizado);

        combinar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/combinar.png"))); // NOI18N
        combinar.setToolTipText("Combinación de bandas");
        combinar.setMaximumSize(new java.awt.Dimension(40, 30));
        combinar.setMinimumSize(new java.awt.Dimension(40, 30));
        combinar.setPreferredSize(new java.awt.Dimension(40, 30));
        combinar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                combinarActionPerformed(evt);
            }
        });
        panelProcesamientoFiltros2.add(combinar);

        sepia.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/sepia.png"))); // NOI18N
        sepia.setToolTipText("Filtro sepia");
        sepia.setMaximumSize(new java.awt.Dimension(40, 30));
        sepia.setMinimumSize(new java.awt.Dimension(40, 30));
        sepia.setPreferredSize(new java.awt.Dimension(40, 30));
        sepia.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sepiaActionPerformed(evt);
            }
        });
        panelProcesamientoFiltros2.add(sepia);

        ecualizacion.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/ecualizar.png"))); // NOI18N
        ecualizacion.setToolTipText("Ecualización");
        ecualizacion.setMaximumSize(new java.awt.Dimension(40, 30));
        ecualizacion.setMinimumSize(new java.awt.Dimension(40, 30));
        ecualizacion.setPreferredSize(new java.awt.Dimension(40, 30));
        ecualizacion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ecualizacionActionPerformed(evt);
            }
        });
        panelProcesamientoFiltros2.add(ecualizacion);

        negativo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/negativo.png"))); // NOI18N
        negativo.setToolTipText("Negativo");
        negativo.setMaximumSize(new java.awt.Dimension(40, 30));
        negativo.setMinimumSize(new java.awt.Dimension(40, 30));
        negativo.setPreferredSize(new java.awt.Dimension(40, 30));
        negativo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                negativoActionPerformed(evt);
            }
        });
        panelProcesamientoFiltros2.add(negativo);

        panelProcesamiento.add(panelProcesamientoFiltros2);

        jPanel1.add(panelProcesamiento, java.awt.BorderLayout.PAGE_START);

        getContentPane().add(jPanel1, java.awt.BorderLayout.SOUTH);

        archivo.setText("Archivo");
        archivo.setToolTipText("");

        archivo_nuevo.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        archivo_nuevo.setText("Nuevo");
        archivo_nuevo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                archivo_nuevoActionPerformed(evt);
            }
        });
        archivo.add(archivo_nuevo);

        archivo_abrir.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        archivo_abrir.setText("Abrir");
        archivo_abrir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                archivo_abrirActionPerformed(evt);
            }
        });
        archivo.add(archivo_abrir);

        archivo_guardar.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_G, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        archivo_guardar.setText("Guardar");
        archivo_guardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                archivo_guardarActionPerformed(evt);
            }
        });
        archivo.add(archivo_guardar);

        menuOpciones.add(archivo);

        edicion.setText("Edición");

        activarBarraEstado.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        activarBarraEstado.setSelected(true);
        activarBarraEstado.setText("Barra de Estado");
        activarBarraEstado.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                activarBarraEstadoStateChanged(evt);
            }
        });
        edicion.add(activarBarraEstado);

        menuOpciones.add(edicion);

        ayuda.setText("Ayuda");

        acercaDe.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        acercaDe.setText("Acerca de");
        acercaDe.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                acercaDeActionPerformed(evt);
            }
        });
        ayuda.add(acercaDe);

        menuOpciones.add(ayuda);

        setJMenuBar(menuOpciones);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Método implementado para cuando se pulsa el botón "trazoLibre" Cambia el
     * valor de la variable herramienta de la clase Lienzo a "trazo" Cambia el
     * valor de la barra de Estado a "Trazo Libre"
     *
     * @param evt
     */
    private void trazoLibreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_trazoLibreActionPerformed
        if (ventanaActiva() && activa instanceof VentanaInternaImagen){
            VentanaInternaImagen ventana = (VentanaInternaImagen)activa;
            ventana.getLienzo().setFiguraTrazo();
        }
        barraEstado.setText("Trazo Libre");
    }//GEN-LAST:event_trazoLibreActionPerformed

    /**
     * Método implementado para cuando se pulsa el botón "linea" Cambia el valor
     * de la variable herramienta de la clase Lienzo a "Linea" Cambia el valor
     * de la barra de Estado a "Linea"
     *
     * @param evt
     */
    private void lineaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lineaActionPerformed
        if (ventanaActiva() && activa instanceof VentanaInternaImagen){
            VentanaInternaImagen ventana = (VentanaInternaImagen)activa;
            ventana.getLienzo().setFiguraLinea();
        }
        barraEstado.setText("Linea");
    }//GEN-LAST:event_lineaActionPerformed

    /**
     * Método implementado para cuando se pulsa el botón "rectangulo" Cambia el
     * valor de la variable herramienta de la clase Lienzo a "Rectangulo" Cambia
     * el valor de la barra de Estado a "Rectangulo"
     *
     * @param evt
     */
    private void rectanguloActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rectanguloActionPerformed
        if (ventanaActiva() && activa instanceof VentanaInternaImagen){
            VentanaInternaImagen ventana = (VentanaInternaImagen)activa;
            ventana.getLienzo().setFiguraRectangulo();
        }
        barraEstado.setText("Rectangulo");
    }//GEN-LAST:event_rectanguloActionPerformed

    /**
     * Método implementado para cuando se pulsa el botón "elipse" Cambia el
     * valor de la variable herramienta de la clase Lienzo a "Elipse" Cambia el
     * valor de la barra de Estado a "Elipse"
     *
     * @param evt
     */
    private void elipseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_elipseActionPerformed
        if (ventanaActiva() && activa instanceof VentanaInternaImagen){
            VentanaInternaImagen ventana = (VentanaInternaImagen)activa;
            ventana.getLienzo().setFiguraElipse();
        }
        barraEstado.setText("Elipse");
    }//GEN-LAST:event_elipseActionPerformed

    /**
     * Método implementado para cuando se pulsa el botón "curva" Cambia el valor
     * de la variable herramienta de la clase Lienzo a "Curva" Cambia el valor
     * de la barra de Estado a "Curva"
     *
     * @param evt
     */
    private void curvaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_curvaActionPerformed
        if (ventanaActiva() && activa instanceof VentanaInternaImagen){
            VentanaInternaImagen ventana = (VentanaInternaImagen)activa;
            ventana.getLienzo().setFiguraCurva();
        }
        barraEstado.setText("Curva");
    }//GEN-LAST:event_curvaActionPerformed

    /**
     * Método implementado para cuando se pulsa el Item "archivoNuevo" Crea una
     * nueva ventana interna, la añade al escritorio, la hace visible y le añade
     * un nuevo manejador
     *
     * @param evt
     */
    private void archivo_nuevoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_archivo_nuevoActionPerformed
        String alto = JOptionPane.showInputDialog(this, "Introduce el alto de nueva imagen");                     
        String ancho = JOptionPane.showInputDialog(this, "Introduce el ancho de nueva imagen");
        if (alto == null || ancho == null)
            return;
        if (Integer.parseInt(alto) <= 0 || ancho.equals(""))
            alto = "300";
        if (Integer.parseInt(ancho) <= 0 || ancho.equals(""))
            ancho = "300";
        BufferedImage imagen = new BufferedImage(Integer.parseInt(alto), Integer.parseInt(ancho), BufferedImage.TYPE_INT_RGB);
        this.añadirVentanaImagen(imagen, "Imagen nueva");
    }//GEN-LAST:event_archivo_nuevoActionPerformed

    /**
     * Método implementado para cuando se pulsa el Item "archivoAbrir" Hace una
     * llamada al método que abre un archivo de la clase Lienzo
     *
     * @param evt
     */
    private void archivo_abrirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_archivo_abrirActionPerformed
        dlg.setFileFilter(definirFiltro());
        int resp = dlg.showOpenDialog(this);
        if (resp == JFileChooser.APPROVE_OPTION) {
            try {
                File f = dlg.getSelectedFile();
                File fichero = crearFichero(f);
                String extension = this.obtenerExtension(fichero);
                if (this.comprobarExtensionAudio(extension)) // La extensión del archivo es de audio
                    this.comprobarSiExiste(fichero);
                else if (comprobarExtensionImagen(extension)){ // La extensión del archivo es de imagen
                    BufferedImage imagen = ImageIO.read(f);
                    this.añadirVentanaImagen(imagen, f.getName());
                } else // La extensión del archivo es de video
                    this.añadirVentanaVideo(f);
            } catch (IOException ex) {
                System.err.println("Error al leer la imagen");
            }
        }
    }//GEN-LAST:event_archivo_abrirActionPerformed

    private void comprobarSiExiste(File fichero) {
        Boolean existe = false;
        int i = 0;
        int elementos = listaAudios.getItemCount();
        if (elementos > 0) {
            while (i < elementos && !existe) {
                if (listaAudios.getItemAt(i).equals(fichero)) {
                    existe = true;
                }
                i++;
            }
        }
        int valor = -1;
        if (existe)
            valor = JOptionPane.showConfirmDialog(this, "El archivo seleccionado ya está en la lista de reproducción.\n ¿Desea sobreescribirlo?", "Sobreescribir archivo", JOptionPane.YES_NO_OPTION);
        if (valor == 0) {
            listaAudios.removeItemAt(i-1);
            listaAudios.addItem(fichero);
            listaAudios.setSelectedItem(fichero);
            JOptionPane.showMessageDialog(this, "Archivo sobreescrito correctamente.");
        } else if (valor == 1)
            JOptionPane.showMessageDialog(this, "El archivo no se ha añadido.");
        else if (!existe){
            listaAudios.addItem(fichero);
            listaAudios.setSelectedItem(fichero);
            JOptionPane.showMessageDialog(this, "Archivo añadido a la lista.");
        }
    }

    private boolean comprobarExtensionImagen(String extension){
        boolean existe = false;
        for (String s: ImageIO.getReaderFormatNames())
            if (s.endsWith(extension))
                existe = true;
        return existe;
    }

    private boolean comprobarExtensionAudio(String extension){
        boolean existe = false;
        for (AudioFileFormat.Type t: AudioSystem.getAudioFileTypes())
            if (t.getExtension().equals(extension))
                existe = true;
        return existe;     
    }

    private File crearFichero(File f){
        File fichero = new File(f.getAbsolutePath()) {
            @Override
            public String toString() {
                return this.getName();
            }
        };
        return fichero;
    }

    private void añadirVentanaImagen(BufferedImage imagen, String titulo) {
        VentanaInternaImagen ventana = new VentanaInternaImagen(imagen);
        ventana.setVisible(true);
        ventana.setTitle(titulo);
        ventana.addVentanaInternaListener (new miManejadorVentanaInterna());
        ventana.addLienzoListener(new miManejadorLienzo());
        ventana.addRatonListener(new miManejadorRaton());
        activa = ventana;
        escritorio.add(ventana);
    }
    
    private void añadirVentanaVideo(File ficheroVideo){
        VentanaInternaVideo ventana = VentanaInternaVideo.getInstance(ficheroVideo);
        ventana.setVisible(true);
        ventana.setTitle(ficheroVideo.getName());
        ventana.addMediaPlayerListener(new miManejadorVideo());
        activa = ventana;
        escritorio.add(ventana);
    }

    /**
     * Método implementado para cuando se pulsa el Item "archivoGuardar" Hace
     * una llamada al método que guarda un archivo de la clase Lienzo
     *
     * @param evt
     */
    private void archivo_guardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_archivo_guardarActionPerformed
        if (ventanaActiva() && !(activa instanceof VentanaInternaVideo)){
            VentanaInternaImagen ventana = (VentanaInternaImagen)activa;
            BufferedImage imagen = ventana.getLienzo().getImagenGuardar();
            if (imagen != null) {
                dlg.setFileFilter(definirFiltro());
                int resp = dlg.showSaveDialog(this);
                if (resp == JFileChooser.APPROVE_OPTION) {
                    try {
                        File f = dlg.getSelectedFile();
                        ImageIO.write(imagen, "jpg", f);
                        activa.setTitle(f.getName());
                    } catch (IOException ex) {
                        System.err.println("Error al guardar la imagen");
                    }
                }
            }
        }
    }//GEN-LAST:event_archivo_guardarActionPerformed

    /**
     * Método implementado para cuando se pulsa el botón "activarBarraEstado"
     * Cambia la visibilidad del elemento barraEstado en función de si el botón
     * activarBarraEstado está marchado (true) o no (false)
     *
     * @param evt
     */
    private void activarBarraEstadoStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_activarBarraEstadoStateChanged
        this.barraEstado.setVisible(activarBarraEstado.getState());
    }//GEN-LAST:event_activarBarraEstadoStateChanged

    private void nuevoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nuevoActionPerformed
        this.archivo_nuevoActionPerformed(evt);
    }//GEN-LAST:event_nuevoActionPerformed

    private void abrirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_abrirActionPerformed
        this.archivo_abrirActionPerformed(evt);
    }//GEN-LAST:event_abrirActionPerformed

    private void guardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_guardarActionPerformed
        this.archivo_guardarActionPerformed(evt);
    }//GEN-LAST:event_guardarActionPerformed

    private void spinnerGrosorStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinnerGrosorStateChanged
        if (ventanaActiva() && activa instanceof VentanaInternaImagen){
            VentanaInternaImagen ventana = (VentanaInternaImagen)activa;
            ventana.getLienzo().setGrosor((Integer) spinnerGrosor.getValue());
        }
    }//GEN-LAST:event_spinnerGrosorStateChanged

    private void sliderBrilloStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_sliderBrilloStateChanged
        if (ventanaActiva() && activa instanceof VentanaInternaImagen && this.copiaImagen != null){
            VentanaInternaImagen ventana = (VentanaInternaImagen)activa;
            try {
                RescaleOp rop = new RescaleOp(1.0F, (float) this.sliderBrillo.getValue(), null);
                rop.filter(this.copiaImagen, ventana.getLienzo().getImagen());
                escritorio.repaint();
            } catch (IllegalArgumentException e) {
                System.err.println(e.getLocalizedMessage());
            }
        }
    }//GEN-LAST:event_sliderBrilloStateChanged

    private void sliderBrilloFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_sliderBrilloFocusLost
        this.copiaImagen = null;
        this.sliderBrillo.setValue(0);
    }//GEN-LAST:event_sliderBrilloFocusLost

    private void sliderContrasteStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_sliderContrasteStateChanged
        if (ventanaActiva() && activa instanceof VentanaInternaImagen && this.copiaImagen != null) {
            VentanaInternaImagen ventana = (VentanaInternaImagen)activa;
            try {
                float valor = (float) (this.sliderContraste.getValue() / 10.0);
                float[] filtro = {valor, valor, valor, valor, valor, valor, valor, valor, valor, valor};
                Kernel k = new Kernel(3, 3, filtro);
                ConvolveOp cop = new ConvolveOp(k);
                cop.filter(this.copiaImagen, ventana.getLienzo().getImagen());
                escritorio.repaint();
            } catch (IllegalArgumentException e) {
                System.err.println(e.getLocalizedMessage());
            }
        }
    }//GEN-LAST:event_sliderContrasteStateChanged

    private void sliderBrilloFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_sliderBrilloFocusGained
        copiarImagen();
    }//GEN-LAST:event_sliderBrilloFocusGained

    private void sliderContrasteFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_sliderContrasteFocusLost
        copiaImagen = null;
        sliderContraste.setValue(0);
    }//GEN-LAST:event_sliderContrasteFocusLost

    private void sliderContrasteFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_sliderContrasteFocusGained
        copiarImagen();
    }//GEN-LAST:event_sliderContrasteFocusGained

    private void comboBoxFiltrosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboBoxFiltrosActionPerformed
        if (ventanaActiva() && activa instanceof VentanaInternaImagen){
            VentanaInternaImagen ventana = (VentanaInternaImagen)activa;
            int filtro = this.comboBoxFiltros.getSelectedIndex();
            Kernel k = null;
            switch (filtro) {
                case 0:
                    k = KernelProducer.createKernel(KernelProducer.TYPE_MEDIA_3x3);
                    break;
                case 1:
                    k = KernelProducer.createKernel(KernelProducer.TYPE_BINOMIAL_3x3);
                    break;
                case 2:
                    k = KernelProducer.createKernel(KernelProducer.TYPE_ENFOQUE_3x3);
                    break;
                case 3:
                    k = KernelProducer.createKernel(KernelProducer.TYPE_RELIEVE_3x3);
                    break;
                case 4:
                    k = KernelProducer.createKernel(KernelProducer.TYPE_LAPLACIANA_3x3);
                    break;
            }
            ConvolveOp cop = new ConvolveOp(k, ConvolveOp.EDGE_NO_OP, null);
            BufferedImage imgDest = null;
            imgDest = cop.filter(ventana.getLienzo().getImagen(), imgDest);
            ventana.getLienzo().setImagen(imgDest);
            escritorio.repaint();
        }
    }//GEN-LAST:event_comboBoxFiltrosActionPerformed

    private void sliderFiltroStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_sliderFiltroStateChanged

    }//GEN-LAST:event_sliderFiltroStateChanged

    private void sliderFiltroFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_sliderFiltroFocusLost
        copiaImagen = null;
        sliderFiltro.setValue(1);
    }//GEN-LAST:event_sliderFiltroFocusLost

    private void sliderFiltroFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_sliderFiltroFocusGained
        copiarImagen();
    }//GEN-LAST:event_sliderFiltroFocusGained

// ????????????
    private void comboBoxColoresMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_comboBoxColoresMouseClicked
        if (ventanaActiva() && activa instanceof VentanaInternaImagen){
            VentanaInternaImagen ventana = (VentanaInternaImagen)activa;
            ventana.getLienzo().setFondo(colorFondo.getValue());
        }
    }//GEN-LAST:event_comboBoxColoresMouseClicked

    private void contrasteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_contrasteActionPerformed
        int type = LookupTableProducer.TYPE_SFUNCION;
        LookupTable tabla = LookupTableProducer.createLookupTable(type);
        aplicarContraste(tabla);
    }//GEN-LAST:event_contrasteActionPerformed

    private void iluminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_iluminarActionPerformed
        LookupTable tabla = LookupTableProducer.rootFuction(2.0);
        aplicarContraste(tabla);
    }//GEN-LAST:event_iluminarActionPerformed

    private void oscurecerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_oscurecerActionPerformed
        LookupTable tabla = LookupTableProducer.gammaCorrection(1.0, 1.5);
        aplicarContraste(tabla);
    }//GEN-LAST:event_oscurecerActionPerformed

    private void sliderRotacionFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_sliderRotacionFocusGained
        copiarImagen();
    }//GEN-LAST:event_sliderRotacionFocusGained

    private void sliderRotacionStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_sliderRotacionStateChanged
        if (ventanaActiva() && activa instanceof VentanaInternaImagen){
            VentanaInternaImagen ventana = (VentanaInternaImagen)activa; 
            if (ventana.getLienzo().getImagen() != null) {
                try {
                    double valor = sliderRotacion.getValue();
                    int centroAncho = copiaImagen.getWidth() / 2;
                    int centroAlto = copiaImagen.getHeight() / 2;
                    AffineTransform at = AffineTransform.getRotateInstance(Math.toRadians(valor), centroAncho, centroAlto);
                    AffineTransformOp atop = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
                    BufferedImage imgDest = null;
                    imgDest = atop.filter(copiaImagen, imgDest);
                    ventana.getLienzo().setImagen(imgDest);
                    escritorio.repaint();
                } catch (IllegalArgumentException e) {
                    System.err.println(e.getLocalizedMessage());
                }
            }
        }
    }//GEN-LAST:event_sliderRotacionStateChanged

    private void sliderRotacionFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_sliderRotacionFocusLost
        copiaImagen = null;
        sliderFiltro.setValue(0);
    }//GEN-LAST:event_sliderRotacionFocusLost

    private void rotacion90ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rotacion90ActionPerformed
        aplicarRotacion(90);
    }//GEN-LAST:event_rotacion90ActionPerformed

    private void rotacion180ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rotacion180ActionPerformed
        aplicarRotacion(180);
    }//GEN-LAST:event_rotacion180ActionPerformed

    private void rotacion270ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rotacion270ActionPerformed
        aplicarRotacion(270);
    }//GEN-LAST:event_rotacion270ActionPerformed

    private void aumentarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aumentarActionPerformed
        aplicarEscalado(1.25);
    }//GEN-LAST:event_aumentarActionPerformed

    private void disminuirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_disminuirActionPerformed
        aplicarEscalado(0.75);
    }//GEN-LAST:event_disminuirActionPerformed

    private void spinnerValorAStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinnerValorAStateChanged
        a = (Integer) spinnerValorA.getValue();
        aplicarFuncion();
    }//GEN-LAST:event_spinnerValorAStateChanged

    private void spinnerValorBStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinnerValorBStateChanged
        b = (Integer) spinnerValorB.getValue();
        aplicarFuncion();
    }//GEN-LAST:event_spinnerValorBStateChanged

    private void bandasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bandasActionPerformed
        if (ventanaActiva() && activa instanceof VentanaInternaImagen){
            VentanaInternaImagen ventana = (VentanaInternaImagen)activa;
            BufferedImage imagen = ventana.getLienzo().getImagen();
            if (imagen != null) {
                BufferedImage img;
                for (int i = 0; i < imagen.getRaster().getNumBands(); i++) {
                    img = this.getImagenBand(imagen, i);
                    añadirVentanaImagen(img, ventana.getTitle() + " - Banda " + i);
                }
            }
        }
    }//GEN-LAST:event_bandasActionPerformed

    private void cambioEspacioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cambioEspacioActionPerformed
        BufferedImage img;
        img = this.cambioEspacio(this.cambioEspacio.getSelectedIndex());
        if (img != null) 
            añadirVentanaImagen(img, activa.getTitle() + " - Espacio " + this.cambioEspacio.getItemAt(this.cambioEspacio.getSelectedIndex()));
    }//GEN-LAST:event_cambioEspacioActionPerformed

    private void combinarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_combinarActionPerformed
        if (ventanaActiva() && activa instanceof VentanaInternaImagen){
            VentanaInternaImagen ventana = (VentanaInternaImagen)activa;
            BufferedImage img = ventana.getLienzo().getImagen();
            if (img != null) {
                try {
                    float[][] matriz = {{0.0F, 1.0F, 1.0F},
                    {1.0F, 0.0F, 1.0F},
                    {1.0F, 1.0F, 0.0F}};
                    BandCombineOp bcop = new BandCombineOp(matriz, null);
                    bcop.filter(img.getRaster(), img.getRaster());
                    escritorio.repaint();
                } catch (IllegalArgumentException e) {
                    System.err.println(e.getLocalizedMessage());
                }
            }
        }
    }//GEN-LAST:event_combinarActionPerformed

    private void rellenoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rellenoActionPerformed
        if (ventanaActiva() && activa instanceof VentanaInternaImagen){
            VentanaInternaImagen ventana = (VentanaInternaImagen)activa;
            ventana.getLienzo().setRelleno(relleno.isSelected());
        }
    }//GEN-LAST:event_rellenoActionPerformed

    private void transparenciaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_transparenciaActionPerformed
        if (ventanaActiva() && activa instanceof VentanaInternaImagen){
            VentanaInternaImagen ventana = (VentanaInternaImagen)activa;
            ventana.getLienzo().setTransparencia(transparencia.isSelected());
        }
    }//GEN-LAST:event_transparenciaActionPerformed

    private void alisarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_alisarActionPerformed
        if (ventanaActiva() && activa instanceof VentanaInternaImagen){
            VentanaInternaImagen ventana = (VentanaInternaImagen)activa;
            ventana.getLienzo().setAlisar(alisar.isSelected());
        }
    }//GEN-LAST:event_alisarActionPerformed

    private void moverActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_moverActionPerformed
        if (ventanaActiva() && activa instanceof VentanaInternaImagen){
            VentanaInternaImagen ventana = (VentanaInternaImagen)activa;
            ventana.getLienzo().setMover(mover.isSelected());
            ventana.getLienzo().marcarFigura((Shape)listaFiguras.getSelectedItem());
            escritorio.repaint();
        }
    }//GEN-LAST:event_moverActionPerformed

    private void funcionTrapezoidalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_funcionTrapezoidalActionPerformed
        if (ventanaActiva() && activa instanceof VentanaInternaImagen){
            boolean valor = funcionTrapezoidal.isSelected();
            spinnerValorA.setEnabled(valor);
            spinnerValorB.setEnabled(valor);
            if (valor) {
                a = (Integer) spinnerValorA.getValue();
                b = (Integer) spinnerValorB.getValue();
                this.copiarImagen();
                this.aplicarFuncion();
            } else {
                this.copiaImagen = null;
            }
        }
    }//GEN-LAST:event_funcionTrapezoidalActionPerformed

    private void volcadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_volcadoActionPerformed
        if (ventanaActiva() && activa instanceof VentanaInternaImagen){
            VentanaInternaImagen ventana = (VentanaInternaImagen)activa;
            if (!ventana.getLienzo().isVolcado()) {
                int valor = JOptionPane.showConfirmDialog(ventana.getLienzo(), "Esta opción es irreversible, ¿desea continuar?", "Confirmar volcado", JOptionPane.YES_NO_OPTION);
                if (valor == 0) {
                    ventana.getLienzo().setVolcado(volcado.isSelected());
                    JOptionPane.showMessageDialog(ventana.getLienzo(), "Se han volcado las figuras a la imagen");
                } else {
                    volcado.setSelected(!volcado.isSelected());
                    JOptionPane.showMessageDialog(ventana.getLienzo(), "No se ha realizado el volcado");
                }
            } else {
                ventana.getLienzo().setVolcado(volcado.isSelected());
            }
        }
    }//GEN-LAST:event_volcadoActionPerformed

    private void sepiaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sepiaActionPerformed
        if (ventanaActiva() && activa instanceof VentanaInternaImagen){
            VentanaInternaImagen ventana = (VentanaInternaImagen)activa;
            BufferedImage img = ventana.getLienzo().getImagen();
            if (img != null) {
                SepiaOp sepia = new SepiaOp();
                sepia.filter(img, img);
            }
            escritorio.repaint();
        }
    }//GEN-LAST:event_sepiaActionPerformed

    private void ecualizacionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ecualizacionActionPerformed
        if (ventanaActiva() && activa instanceof VentanaInternaImagen){
            VentanaInternaImagen ventana = (VentanaInternaImagen)activa;
            BufferedImage img = ventana.getLienzo().getImagen();
            if (img != null) {
                EqualizationOp ecualizacion = new EqualizationOp();
                ecualizacion.filter(img, img);
            }
            escritorio.repaint();
        }
    }//GEN-LAST:event_ecualizacionActionPerformed

    private void sliderPosterizadoStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_sliderPosterizadoStateChanged
        if (ventanaActiva() && activa instanceof VentanaInternaImagen){
            VentanaInternaImagen ventana = (VentanaInternaImagen)activa;
            if (ventana.getLienzo().getImagen() != null) {
                try {
                    int valor = sliderPosterizado.getValue();
                    PosterizarOp posterizar = new PosterizarOp(valor);
                    BufferedImage imgDest = null;
                    imgDest = posterizar.filter(copiaImagen, imgDest);
                    ventana.getLienzo().setImagen(imgDest);
                    escritorio.repaint();
                } catch (IllegalArgumentException e) {
                    System.err.println(e.getLocalizedMessage());
            }
            }
        }
    }//GEN-LAST:event_sliderPosterizadoStateChanged

    private void sliderPosterizadoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_sliderPosterizadoFocusGained
        copiarImagen();
    }//GEN-LAST:event_sliderPosterizadoFocusGained

    private void sliderPosterizadoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_sliderPosterizadoFocusLost
        copiaImagen = null;
        sliderPosterizado.setValue(2);
    }//GEN-LAST:event_sliderPosterizadoFocusLost

    private void sliderTintadoStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_sliderTintadoStateChanged
        if (ventanaActiva() && activa instanceof VentanaInternaImagen){
            VentanaInternaImagen ventana = (VentanaInternaImagen)activa;
            if (ventana.getLienzo().getImagen() != null) {
                try {
                    float valor = (float) (sliderTintado.getValue() / 10.0);
                    TintOp tintado = new TintOp(ventana.getLienzo().getFondo(), valor);
                    BufferedImage imgDest = null;
                    imgDest = tintado.filter(copiaImagen, imgDest);
                    ventana.getLienzo().setImagen(imgDest);
                    escritorio.repaint();
                } catch (IllegalArgumentException e) {
                    System.err.println(e.getLocalizedMessage());
                }
            }
        }
    }//GEN-LAST:event_sliderTintadoStateChanged

    private void sliderTintadoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_sliderTintadoFocusGained
        copiarImagen();
    }//GEN-LAST:event_sliderTintadoFocusGained

    private void sliderTintadoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_sliderTintadoFocusLost
        copiaImagen = null;
        sliderPosterizado.setValue(0);
    }//GEN-LAST:event_sliderTintadoFocusLost

    private void tintadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tintadoActionPerformed
        if (ventanaActiva() && activa instanceof VentanaInternaImagen)
            sliderTintado.setEnabled(tintado.isSelected());
    }//GEN-LAST:event_tintadoActionPerformed

    private void sliderFiltroRojoStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_sliderFiltroRojoStateChanged
        if (ventanaActiva() && activa instanceof VentanaInternaImagen){
            VentanaInternaImagen ventana = (VentanaInternaImagen)activa;
            if (ventana.getLienzo().getImagen() != null) {
                try {
                    int valor = sliderFiltroRojo.getValue();
                    RojoOp rojo = new RojoOp(valor);
                    BufferedImage imgDest = null;
                    imgDest = rojo.filter(copiaImagen, imgDest);
                    ventana.getLienzo().setImagen(imgDest);
                    escritorio.repaint();
                } catch (IllegalArgumentException e) {
                    System.err.println(e.getLocalizedMessage());
                }
            }
        }
    }//GEN-LAST:event_sliderFiltroRojoStateChanged

    private void sliderFiltroRojoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_sliderFiltroRojoFocusGained
        copiarImagen();
    }//GEN-LAST:event_sliderFiltroRojoFocusGained

    private void sliderFiltroRojoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_sliderFiltroRojoFocusLost
        copiaImagen = null;
        sliderPosterizado.setValue(0);
    }//GEN-LAST:event_sliderFiltroRojoFocusLost

    private void filtroRojoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filtroRojoActionPerformed
        if (ventanaActiva() && activa instanceof VentanaInternaImagen)
            sliderFiltroRojo.setEnabled(filtroRojo.isSelected());
    }//GEN-LAST:event_filtroRojoActionPerformed

    private void grabarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_grabarActionPerformed
        if (grabar.isSelected()) {
            int resp = dlg.showSaveDialog(this);
            if (resp == JFileChooser.APPROVE_OPTION) {
                audioGrabado = dlg.getSelectedFile();
                recorder = new SMSoundRecorder(audioGrabado);
                if (recorder != null) {
                    recorder.addLineListener(new miManejadorAudio());
                    recorder.record();
                }
            }
        } else if (recorder != null) {
            File fichero = crearFichero(audioGrabado);
            comprobarSiExiste(fichero);
            recorder.stop();
            recorder = null;
        }
    }//GEN-LAST:event_grabarActionPerformed

    private void inicioYPausaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inicioYPausaActionPerformed
        if (inicioOPausa == 1) {
            if (player == null) {
                File f = (File) listaAudios.getSelectedItem();
                if (f != null) {
                    player = new SMClipPlayer(f);
                    if (player != null) {
                        player.addLineListener(new miManejadorAudio());
                        player.play();
                    }
                    añadirDuracionAudio();
                }
            } else if (player.getClip() != null) {
                player.getClip().setMicrosecondPosition(tiempoPausa);
                player.play();
            }
        } else if (inicioOPausa == 2 && player != null) {
            tiempoPausa = player.getClip().getMicrosecondPosition();
            player.stop();
        }
    }//GEN-LAST:event_inicioYPausaActionPerformed

    private void pararActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pararActionPerformed
        if (player != null) {
            player.stop();
            player = null;
        }
    }//GEN-LAST:event_pararActionPerformed

    private void listaAudiosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_listaAudiosActionPerformed
        if (player != null) {
            player.stop();
            player = null;
        }
    }//GEN-LAST:event_listaAudiosActionPerformed

    private void acercaDeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_acercaDeActionPerformed
        JOptionPane.showMessageDialog(this, "Práctica Sistemas multimedia\n Versión 1.0 \n Gloria Méndez Sánchez", "Acerca de", JOptionPane.INFORMATION_MESSAGE,  new javax.swing.ImageIcon(getClass().getResource("/iconos/acercaDe.png")));
    }//GEN-LAST:event_acercaDeActionPerformed

    private void selectorBordeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectorBordeActionPerformed
        if (ventanaActiva() && activa instanceof VentanaInternaImagen){
            VentanaInternaImagen ventana = (VentanaInternaImagen)activa;
            Color c = obtenerColor("Seleccionar color de borde");
            if (c != null)
                ventana.getLienzo().setBorde(c);
        }
    }//GEN-LAST:event_selectorBordeActionPerformed

    private void selectorFondoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectorFondoActionPerformed
        if (ventanaActiva() && activa instanceof VentanaInternaImagen){
            VentanaInternaImagen ventana = (VentanaInternaImagen)activa;
            Color c = obtenerColor("Seleccionar color de fondo");
            if (c != null)
                ventana.getLienzo().setFondo(c);
        }
    }//GEN-LAST:event_selectorFondoActionPerformed

    private void duplicarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_duplicarActionPerformed
        if (ventanaActiva() && activa instanceof VentanaInternaImagen){     
            this.copiarImagen();
            this.añadirVentanaImagen(this.copiaImagen, this.activa.getTitle() + " - Copia");
            escritorio.repaint();
        }
    }//GEN-LAST:event_duplicarActionPerformed

    private void listaFigurasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_listaFigurasActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_listaFigurasActionPerformed

    private void inicioVideoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inicioVideoActionPerformed
        if (ventanaActiva() && activa instanceof VentanaInternaVideo){
            VentanaInternaVideo ventana = (VentanaInternaVideo)activa;
            if (ventana != null)
                ventana.play();
        }
    }//GEN-LAST:event_inicioVideoActionPerformed

    private void finVideoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_finVideoActionPerformed
        if (ventanaActiva() && activa instanceof VentanaInternaVideo){
            VentanaInternaVideo ventana = (VentanaInternaVideo)activa;
            if (ventana != null)
                ventana.stop();
        }
    }//GEN-LAST:event_finVideoActionPerformed

    private void camaraWebActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_camaraWebActionPerformed
        VentanaInternaCamara ventana = VentanaInternaCamara.getInstance();
        ventana.setVisible(true);
        ventana.setTitle("Webcam");
        escritorio.add(ventana);
        escritorio.repaint();
    }//GEN-LAST:event_camaraWebActionPerformed

    private void capturarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_capturarActionPerformed
        if (ventanaActiva() && activa instanceof VentanaInternaCamara){
            VentanaInternaCamara ventana = (VentanaInternaCamara)activa;
            BufferedImage img = ventana.capturar();
            if (img != null)
                añadirVentanaImagen(img, "Captura webcam");
        }
    }//GEN-LAST:event_capturarActionPerformed

    private void negativoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_negativoActionPerformed
        if (ventanaActiva() && activa instanceof VentanaInternaImagen){
            VentanaInternaImagen ventana = (VentanaInternaImagen)activa;
            BufferedImage img = ventana.getLienzo().getImagen();
            if (img != null) {
                NegativoOp negativo = new NegativoOp();
                negativo.filter(img, img);
            }
            escritorio.repaint();
        }
    }//GEN-LAST:event_negativoActionPerformed

    private void sliderBordesStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_sliderBordesStateChanged
        if (ventanaActiva() && activa instanceof VentanaInternaImagen){
            VentanaInternaImagen ventana = (VentanaInternaImagen)activa;
            if (ventana.getLienzo().getImagen() != null) {
                try {
                    int valor = sliderBordes.getValue();
                    BordeOp borde = new BordeOp(valor, ventana.getLienzo().getBorde());
                    BufferedImage imgDest = null;
                    imgDest = borde.filter(copiaImagen, imgDest);
                    ventana.getLienzo().setImagen(imgDest);
                    escritorio.repaint();
                    
                } catch (IllegalArgumentException e) {
                    System.err.println(e.getLocalizedMessage());
                }
            }
        }
    }//GEN-LAST:event_sliderBordesStateChanged

    private void sliderBordesFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_sliderBordesFocusGained
        copiarImagen();
    }//GEN-LAST:event_sliderBordesFocusGained

    private void sliderBordesFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_sliderBordesFocusLost
        copiaImagen = null;
        sliderPosterizado.setValue(0);
    }//GEN-LAST:event_sliderBordesFocusLost

    private void pintarBordesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pintarBordesActionPerformed
        if (ventanaActiva() && activa instanceof VentanaInternaImagen)
            sliderBordes.setEnabled(pintarBordes.isSelected());
    }//GEN-LAST:event_pintarBordesActionPerformed

    private void funcionCuadraticaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_funcionCuadraticaActionPerformed
        if (ventanaActiva() && activa instanceof VentanaInternaImagen)
            sliderFuncionCuadratica.setEnabled(funcionCuadratica.isSelected());
    }//GEN-LAST:event_funcionCuadraticaActionPerformed

    private void sliderFuncionCuadraticaStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_sliderFuncionCuadraticaStateChanged
        if (ventanaActiva() && activa instanceof VentanaInternaImagen){
            VentanaInternaImagen ventana = (VentanaInternaImagen)activa;
            if (ventana.getLienzo().getImagen() != null) {
                try {
                    int valor = sliderFuncionCuadratica.getValue();
                    byte funcionT[] = new byte[256];
                    for (int x = 0; x < 256; x++)
                        funcionT[x] = (byte) (0.01 * Math.pow(x - valor, 2));
                    LookupTable tabla = new ByteLookupTable(0, funcionT);
                    aplicarContraste(tabla);
                    escritorio.repaint();
                } catch (IllegalArgumentException e) {
                    System.err.println(e.getLocalizedMessage());
                }
            }
        }
    }//GEN-LAST:event_sliderFuncionCuadraticaStateChanged

    private void sliderFuncionCuadraticaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_sliderFuncionCuadraticaFocusGained
        copiarImagen();
    }//GEN-LAST:event_sliderFuncionCuadraticaFocusGained

    private void sliderFuncionCuadraticaFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_sliderFuncionCuadraticaFocusLost
        copiaImagen = null;
        sliderPosterizado.setValue(0);
    }//GEN-LAST:event_sliderFuncionCuadraticaFocusLost

    private Color obtenerColor(String titulo){
        Color c = null;
        if (ventanaActiva() && activa instanceof VentanaInternaImagen){
            JColorChooser colores = new JColorChooser();
            c = colores.showDialog(this, titulo, Color.BLACK);
        }
        return c;
    }

    private void añadirDuracionAudio() {
        long duracion = (player.getClip().getMicrosecondLength() / 1000000);
        long minutos = 0, segundos = 0;
        if (duracion >= 60) {
            minutos = (duracion / 60);
            segundos = (duracion % 60);
        } else
            segundos = duracion;
        String min = "", seg = "";
        if (minutos < 10)
            min = "0";
        if (segundos < 10)
            seg = "0";
        min = min.concat(Long.toString(minutos));
        seg = seg.concat(Long.toString(segundos));
        this.duracionAudio.setText(min + ":" + seg);
    }

    private void aplicarContraste(LookupTable tabla) {
        if (ventanaActiva() && activa instanceof VentanaInternaImagen){
            VentanaInternaImagen ventana = (VentanaInternaImagen)activa;
            BufferedImage img = null;
            if (copiaImagen != null) {
                img = copiaImagen;
            } else {
                img = ventana.getLienzo().getImagen();
            }
            if (img != null) {
                try {
                    LookupOp lop = new LookupOp(tabla, null);
                    lop.filter(img, ventana.getLienzo().getImagen());
                    escritorio.repaint();
                } catch (IllegalArgumentException e) {
                    System.err.println(e.getLocalizedMessage());
                }
            }
        }
    }

    private void aplicarRotacion(int valor) {
        if (ventanaActiva() && activa instanceof VentanaInternaImagen){
            VentanaInternaImagen ventana = (VentanaInternaImagen)activa;
            BufferedImage img = ventana.getLienzo().getImagen();
            if (img != null) {
                try {
                    int centroAncho = img.getWidth() / 2;
                    int centroAlto = img.getHeight() / 2;
                    AffineTransform at = AffineTransform.getRotateInstance(Math.toRadians(valor), centroAncho, centroAlto);
                    AffineTransformOp atop = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
                    BufferedImage imgDest = atop.filter(img, null);
                    ventana.getLienzo().setImagen(imgDest);
                    escritorio.repaint();
                } catch (IllegalArgumentException e) {
                    System.err.println(e.getLocalizedMessage());
                }
            }
        }
    }

    private void aplicarEscalado(double valor) {
        if (ventanaActiva() && activa instanceof VentanaInternaImagen){
            VentanaInternaImagen ventana = (VentanaInternaImagen)activa;
            BufferedImage img = ventana.getLienzo().getImagen();
            if (img != null) {
                try {
                    AffineTransform at = AffineTransform.getScaleInstance(valor, valor);
                    AffineTransformOp atop = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
                    BufferedImage imgDest = atop.filter(img, null);
                    ventana.getLienzo().setImagen(imgDest);
                    escritorio.repaint();
                } catch (IllegalArgumentException e) {
                    System.err.println(e.getLocalizedMessage());
                }
            }
        }
    }

    private void aplicarFuncion() {
        double a, b;
        a = (double) this.a;
        b = (double) this.b;
        byte funcionT[] = new byte[256];
        for (int x = 0; x < 256; x++) {
            if (x <= 0 || x >= 255) {
                funcionT[x] = (byte) 0.0;
            } else if (x > 0 && x < a) {
                funcionT[x] = (byte) ((x / a) * 255.0);
            } else if (x >= a && x <= b) {
                funcionT[x] = (byte) 255.0;
            } else {
                funcionT[x] = (byte) ((255.0 - x) / (255.0 - b) * 255.0);
            }
        }
        LookupTable tabla = new ByteLookupTable(0, funcionT);
        this.aplicarContraste(tabla);
    }

    public BufferedImage getImagenBand(BufferedImage imagen, int banda) {
        //Creamos el modelo de color de la nueva imagen basado en un espcio de color GRAY
        ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
        ComponentColorModel cm = new ComponentColorModel(cs, false, false, Transparency.OPAQUE, DataBuffer.TYPE_BYTE);
        //Creamos el nuevo raster a partir del raster de la imagen original
        int vband[] = {banda};
        WritableRaster bRaster = (WritableRaster) imagen.getRaster().createWritableChild(0, 0, imagen.getWidth(), imagen.getHeight(), 0, 0, vband);
        //Creamos una nueva imagen que contiene como raster el correspondiente a la banda
        return new BufferedImage(cm, bRaster, false, null);
    }

    private BufferedImage cambioEspacio(int i) {
        BufferedImage imgDest = null;
        if (ventanaActiva() && activa instanceof VentanaInternaImagen){
            VentanaInternaImagen ventana = (VentanaInternaImagen)activa;
            BufferedImage img = ventana.getLienzo().getImagen();
            if (img != null) {
                ColorSpace cs = null;
                ColorConvertOp cop = null;
                switch (i) {
                    case 0:
                        cs = ColorSpace.getInstance(ColorSpace.CS_sRGB);
                        cop = new ColorConvertOp(cs, null);
                        break;
                    case 1:
                        cs = ColorSpace.getInstance(ColorSpace.CS_PYCC);
                        cop = new ColorConvertOp(cs, null);
                        break;
                    case 2:
                        cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
                        cop = new ColorConvertOp(cs, null);
                        break;
                    case 3:
                        cs = new YCbCrColorSpace();
                        cop = new ColorConvertOp(cs, null);
                        break;
                }
                imgDest = cop.filter(img, null);
            }
        }
        return imgDest;
    }

    private void copiarImagen() {
        if (ventanaActiva() && activa instanceof VentanaInternaImagen){
            VentanaInternaImagen ventana = (VentanaInternaImagen)activa;
            if (ventana.getLienzo().getImagen() != null) {
                ColorModel cm = ventana.getLienzo().getImagen().getColorModel();
                WritableRaster raster = ventana.getLienzo().getImagen().copyData(null);
                boolean alfaPre = ventana.getLienzo().getImagen().isAlphaPremultiplied();
                this.copiaImagen = new BufferedImage(cm, raster, alfaPre, null);
            }
        }
    }

    private String obtenerExtension(File fichero) {
        String nombre = fichero.toString();
        String extension = "";
        int index = nombre.lastIndexOf('.');
        if (index > 0) {
            extension = nombre.substring(index + 1);
        }
        return extension;
    }

    private FileFilter definirFiltro(){
        List <String> formatos = new ArrayList();
        for (AudioFileFormat.Type t : AudioSystem.getAudioFileTypes()) 
            formatos.add(t.getExtension());
        formatos.addAll(Arrays.asList(ImageIO.getReaderFormatNames()));
        FileFilter filtro = new FileNameExtensionFilter("", formatos.toArray(new String[0]));
        return filtro;
    }

    /* Utilizar getSlectedIndex() para el tipo de filtro y hacer un switch 
    entre todos los índices, mejor que con string, que hay que compararlos
    Dentro de cada caso del switch se crea el kernel correspondiente, se hace
    un ne ConvolveOp y hacer la llamada correspondiente
    En el propio actionPerformed de la lista ya se debe cambiar la imagen
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton abrir;
    private javax.swing.JMenuItem acercaDe;
    private javax.swing.JCheckBoxMenuItem activarBarraEstado;
    private javax.swing.JToggleButton alisar;
    private javax.swing.JMenu archivo;
    private javax.swing.JMenuItem archivo_abrir;
    private javax.swing.JMenuItem archivo_guardar;
    private javax.swing.JMenuItem archivo_nuevo;
    private javax.swing.JButton aumentar;
    private javax.swing.JMenu ayuda;
    private javax.swing.JButton bandas;
    private javax.swing.JLabel barraEstado;
    private javax.swing.JButton camaraWeb;
    private javax.swing.JComboBox<String> cambioEspacio;
    private javax.swing.JButton capturar;
    private sm.gms.colores.VentanaPrincipal colorBorde;
    private sm.gms.colores.VentanaPrincipal colorFondo;
    private javax.swing.JButton combinar;
    private javax.swing.JComboBox<String> comboBoxFiltros;
    private javax.swing.JButton contraste;
    private javax.swing.JLabel coordenadasPixel;
    private javax.swing.JButton curva;
    private javax.swing.JButton disminuir;
    private javax.swing.JButton duplicar;
    private javax.swing.JLabel duracionAudio;
    private javax.swing.JButton ecualizacion;
    private javax.swing.JMenu edicion;
    private javax.swing.JButton elipse;
    private javax.swing.JDesktopPane escritorio;
    private javax.swing.JToggleButton filtroRojo;
    private javax.swing.JButton finVideo;
    private javax.swing.JToggleButton funcionCuadratica;
    private javax.swing.JToggleButton funcionTrapezoidal;
    private javax.swing.JToggleButton grabar;
    private javax.swing.JButton guardar;
    private javax.swing.JButton iluminar;
    private javax.swing.JButton inicioVideo;
    private javax.swing.JButton inicioYPausa;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JSeparator jSeparator6;
    private javax.swing.JButton linea;
    private javax.swing.JComboBox<File> listaAudios;
    private javax.swing.JComboBox<Figura> listaFiguras;
    private javax.swing.JMenuBar menuOpciones;
    private javax.swing.JToggleButton mover;
    private javax.swing.JButton negativo;
    private javax.swing.JButton nuevo;
    private javax.swing.JButton oscurecer;
    private javax.swing.JPanel panelBarraEstado;
    private javax.swing.JPanel panelHerramientas;
    private javax.swing.JPanel panelProcesamiento;
    private javax.swing.JPanel panelProcesamientoBrilloyContraste;
    private javax.swing.JPanel panelProcesamientoFiltros;
    private javax.swing.JPanel panelProcesamientoFiltros1;
    private javax.swing.JPanel panelProcesamientoFiltros2;
    private javax.swing.JPanel panelProcesamientoFiltros3;
    private javax.swing.JPanel panelProcesamientoFiltros4;
    private javax.swing.JPanel panelProcesamientoFiltros5;
    private javax.swing.JPanel panelProcesamientoFiltros6;
    private javax.swing.JPanel panelProcesamientoRotacionYEscalado;
    private javax.swing.JPanel panelProcesamientoTransformaciones;
    private javax.swing.JButton parar;
    private javax.swing.JToggleButton pintarBordes;
    private javax.swing.JButton rectangulo;
    private javax.swing.JToggleButton relleno;
    private javax.swing.JButton rotacion180;
    private javax.swing.JButton rotacion270;
    private javax.swing.JButton rotacion90;
    private javax.swing.JButton selectorBorde;
    private javax.swing.JButton selectorFondo;
    private javax.swing.JButton sepia;
    private javax.swing.JSlider sliderBordes;
    private javax.swing.JSlider sliderBrillo;
    private javax.swing.JSlider sliderContraste;
    private javax.swing.JSlider sliderFiltro;
    private javax.swing.JSlider sliderFiltroRojo;
    private javax.swing.JSlider sliderFuncionCuadratica;
    private javax.swing.JSlider sliderPosterizado;
    private javax.swing.JSlider sliderRotacion;
    private javax.swing.JSlider sliderTintado;
    private javax.swing.JSpinner spinnerGrosor;
    private javax.swing.JSpinner spinnerValorA;
    private javax.swing.JSpinner spinnerValorB;
    private javax.swing.JToggleButton tintado;
    private javax.swing.JToggleButton transparencia;
    private javax.swing.JButton trazoLibre;
    private javax.swing.JLabel valorPixel;
    private javax.swing.JPanel ventanaMedio;
    private javax.swing.JToggleButton volcado;
    // End of variables declaration//GEN-END:variables
}
