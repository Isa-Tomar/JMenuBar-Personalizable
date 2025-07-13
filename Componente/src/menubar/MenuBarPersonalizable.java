package menubar;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.beans.BeanProperty;

public class MenuBarPersonalizable extends JMenuBar {

    public enum EfectoVisual {
        NINGUNO, BRIGHTER, DARKER, LINEA_SUPERIOR, LINEA_INFERIOR, BORDES_LATERALES
    }

    public enum DireccionGradiente {
        NINGUNO, ARRIBA_ABAJO, ABAJO_ARRIBA, IZQUIERDA_DERECHA, DERECHA_IZQUIERDA
    }

    private EfectoVisual efectoVisual = EfectoVisual.NINGUNO;
    private DireccionGradiente direccionGradiente = DireccionGradiente.NINGUNO;
    private Color colorFondo = Color.WHITE;
    private Color colorTexto = Color.BLACK;
    private Color colorHover = Color.LIGHT_GRAY;
    private Font fuenteTexto = new Font("Arial", Font.PLAIN, 14);
    private int separacionIconoTexto = 2;
    private Insets tamañoBotones = new Insets(3 , 6, 3, 6);
    private boolean botonesVentanaVisibles = true;
    private JButton btnMinimizar;
    private JButton btnMaximizar;
    private JButton btnCerrar;

    public MenuBarPersonalizable() {
        setOpaque(true);
        setBorderPainted(true);
        setBorder(BorderFactory.createEtchedBorder());
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        initComponent();
    }

    private void initComponent() {
        add(Box.createHorizontalGlue());
        agregarBotonesVentana();
        setFont(fuenteTexto);
        setBackground(colorFondo);
        setForeground(colorTexto);
        actualizarEstilos();
    }

    private void agregarBotonesVentana() {
        btnMinimizar = crearBotonVentana("_", e -> minimizarVentana());
        btnMaximizar = crearBotonVentana("❐", e -> maximizarRestaurarVentana());
        btnCerrar = crearBotonVentana("X", e -> cerrarVentana());

        btnMinimizar.setVisible(botonesVentanaVisibles);
        btnMaximizar.setVisible(botonesVentanaVisibles);
        btnCerrar.setVisible(botonesVentanaVisibles);

        add(btnMinimizar);
        add(btnMaximizar);
        add(btnCerrar);
 
    }

    private JButton crearBotonVentana(String texto, ActionListener accion) {
        JButton boton = new JButton(texto);
        boton.setFocusPainted(false);
        boton.setBorderPainted(false);
        boton.setBorder(null);
        boton.setOpaque(false);
        boton.setForeground(colorTexto);
        boton.setFont(fuenteTexto);
        boton.setPreferredSize(new Dimension(50, 35));
        boton.setCursor(Cursor.getDefaultCursor());
        boton.addActionListener(accion);

        boton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                boton.setBackground(colorHover);
                boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                boton.setBackground(colorFondo);
                boton.setCursor(Cursor.getDefaultCursor());
            }});
        return boton;
    }

    private void minimizarVentana() {
        Window ventana = SwingUtilities.getWindowAncestor(this);
        if (ventana instanceof Frame) {
            ((Frame) ventana).setState(Frame.ICONIFIED);
        }
    }

    private void maximizarRestaurarVentana() {
        Window ventana = SwingUtilities.getWindowAncestor(this);
        if (ventana instanceof Frame) {
            Frame frame = (Frame) ventana;
            int estado = frame.getExtendedState();
            frame.setExtendedState((estado & Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH ? Frame.NORMAL : Frame.MAXIMIZED_BOTH);
        }
    }

    private void cerrarVentana() {
        Window ventana = SwingUtilities.getWindowAncestor(this);
        ventana.dispose();
    }

    @Override
    public void addNotify() {
        super.addNotify();
        SwingUtilities.invokeLater(() -> {
            java.util.List<Component> menus = new java.util.ArrayList<>();
            java.util.List<Component> otros = new java.util.ArrayList<>();
            for (Component c : getComponents()) {
                if (c instanceof JMenu) {
                    menus.add(c);
                } else {
                    otros.add(c);
                }
            }
            removeAll();
            for (Component menu : menus) {
                add(menu);
            }
            add(Box.createHorizontalGlue());
            for (Component otro : otros) {
                add(otro);
            }
            actualizarEstilos();
            revalidate();
            repaint();
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();

        if (direccionGradiente != DireccionGradiente.NINGUNO) {
            GradientPaint gradiente;
            switch (direccionGradiente) {
                case ARRIBA_ABAJO:
                    gradiente = new GradientPaint(0, 0, colorFondo.brighter(), 0, getHeight(), colorFondo.darker());
                    break;
                case ABAJO_ARRIBA:
                    gradiente = new GradientPaint(0, getHeight(), colorFondo.brighter(), 0, 0, colorFondo.darker());
                    break;
                case IZQUIERDA_DERECHA:
                    gradiente = new GradientPaint(0, 0, colorFondo.brighter(), getWidth(), 0, colorFondo.darker());
                    break;
                case DERECHA_IZQUIERDA:
                    gradiente = new GradientPaint(getWidth(), 0, colorFondo.brighter(), 0, 0, colorFondo.darker());
                    break;
                default:
                    gradiente = null;
            }
            if (gradiente != null) {
                g2d.setPaint(gradiente);
            }
        } else {
            g2d.setColor(colorFondo);
        }
        g2d.fillRect(0, 0, getWidth(), getHeight());

        switch (efectoVisual) {
            case BRIGHTER:
                g2d.setColor(colorFondo.brighter());
                g2d.fillRect(0, 0, getWidth(), getHeight());
                break;
            case DARKER:
                g2d.setColor(colorFondo.darker());
                g2d.fillRect(0, 0, getWidth(), getHeight());
                break;
            case LINEA_INFERIOR:
                g2d.setPaint(new GradientPaint(0, getHeight() - 15, new Color(0, 0, 0, 100), 0, getHeight(), new Color(0, 0, 0, 0)));
                g2d.fillRect(0, getHeight() - 15, getWidth(), 15);
                break;
            case LINEA_SUPERIOR:
                g2d.setPaint(new GradientPaint(0, 0, new Color(0, 0, 0, 0), 0, 15, new Color(0, 0, 0, 100)));
                g2d.fillRect(0, 0, getWidth(), 15);
                break;
            case BORDES_LATERALES:
                g2d.setPaint(new GradientPaint(0, 0, new Color(0, 0, 0, 150), 150, 0, new Color(0, 0, 0, 0)));
                g2d.fillRect(0, 0, 150, getHeight());
                g2d.setPaint(new GradientPaint(getWidth(), 0, new Color(0, 0, 0, 150), getWidth() - 150, 0, new Color(0, 0, 0, 0)));
                g2d.fillRect(getWidth() - 150, 0, 150, getHeight());
                break;
            case NINGUNO:
            default:
                break;
        }

        g2d.dispose();
    }

    private void actualizarEstilos() {
        for (MenuElement me : getSubElements()) {
            Component comp = me.getComponent();
            if (comp instanceof JMenu) {
                JMenu menu = (JMenu) comp;
                aplicarEstilo(menu);
                for (Component sub : menu.getMenuComponents()) {
                    if (sub instanceof JMenuItem) {
                        aplicarEstilo((JComponent) sub);
                    }
                }
            }
        }

        if (btnMinimizar != null) {
            aplicarEstilo(btnMinimizar);
        }
        if (btnMaximizar != null) {
            aplicarEstilo(btnMaximizar);
        }
        if (btnCerrar != null) {
            aplicarEstilo(btnCerrar);
        }

        btnMinimizar.setVisible(botonesVentanaVisibles);
        btnMaximizar.setVisible(botonesVentanaVisibles);
        btnCerrar.setVisible(botonesVentanaVisibles);

        revalidate();
        repaint();
    }

    private void aplicarHover(JComponent comp) {
        for (MouseListener listener : comp.getMouseListeners()) {
            if (listener.getClass().getName().contains("MenuBarPersonalizable")) {
                comp.removeMouseListener(listener);
            }
        }

        comp.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                comp.setBackground(colorHover);
                comp.setOpaque(true);
                comp.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }

            public void mouseExited(MouseEvent evt) {
                aplicarFondoConEstilo(comp);
                comp.setCursor(Cursor.getDefaultCursor());
            }
        });
    }

    private void aplicarFondoConEstilo(JComponent comp) {
        comp.setOpaque(false);
        comp.setBackground(colorFondo);
    }

    private void aplicarEstilo(JComponent comp) {
        comp.setForeground(colorTexto);
        comp.setFont(fuenteTexto);
        aplicarFondoConEstilo(comp);
        if (comp instanceof AbstractButton) {
            ((AbstractButton) comp).setIconTextGap(separacionIconoTexto);
        }
        comp.setBorder(BorderFactory.createEmptyBorder(tamañoBotones.top, tamañoBotones.left, tamañoBotones.bottom, tamañoBotones.right));
        aplicarHover(comp);
    }

    //Getters y Setters con @BeanProperty para agrupar en Propiedades 
    @BeanProperty(preferred = true)
    public Color getColorFondo() {
        return colorFondo;
    }

    public void setColorFondo(Color colorFondo) {
        this.colorFondo = colorFondo;
        setBackground(colorFondo);
        actualizarEstilos();
    }

    @BeanProperty(preferred = true)
    public Color getColorTexto() {
        return colorTexto;
    }

    public void setColorTexto(Color colorTexto) {
        this.colorTexto = colorTexto;
        setForeground(colorTexto);
        actualizarEstilos();
    }

    @BeanProperty(preferred = true)
    public Color getColorHover() {
        return colorHover;
    }

    public void setColorHover(Color colorHover) {
        this.colorHover = colorHover;
        actualizarEstilos();
    }

    @BeanProperty(preferred = true)
    public Font getFuenteTexto() {
        return fuenteTexto;
    }

    public void setFuenteTexto(Font fuenteTexto) {
        this.fuenteTexto = fuenteTexto;
        setFont(fuenteTexto);
        actualizarEstilos();
    }

    @BeanProperty(preferred = true)
    public int getSeparacionIconoTexto() {
        return separacionIconoTexto;
    }

    public void setSeparacionIconoTexto(int separacionIconoTexto) {
        this.separacionIconoTexto = separacionIconoTexto;
        actualizarEstilos();
    }

    @BeanProperty(preferred = true)
    public Insets getTamañoBotones() {
        return tamañoBotones;
    }

    public void setTamañoBotones(Insets tamañoBotones) {
        this.tamañoBotones = tamañoBotones;
        actualizarEstilos();
    }

    @BeanProperty(preferred = true)
    public boolean isBotonesVentanaVisibles() {
        return botonesVentanaVisibles;
    }

    public void setBotonesVentanaVisibles(boolean visibles) {
        this.botonesVentanaVisibles = visibles;
        actualizarEstilos();
    }

    @BeanProperty(preferred = true)
    public DireccionGradiente getDireccionGradiente() {
        return direccionGradiente;
    }

    public void setDireccionGradiente(DireccionGradiente direccionGradiente) {
        if (direccionGradiente != null) {
            this.direccionGradiente = direccionGradiente;
            repaint();
        }
    }

    @BeanProperty(preferred = true)
    public EfectoVisual getEfectoVisual() {
        return efectoVisual;
    }

    public void setEfectoVisual(EfectoVisual efectoVisual) {
        if (efectoVisual != null) {
            this.efectoVisual = efectoVisual;
            repaint();
        }
    }
}


