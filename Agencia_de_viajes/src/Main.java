
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;

// -----------------------------
// SINGLETON: AgenciaReservas
// -----------------------------
class AgenciaReservas {
    private static AgenciaReservas instancia;

    private AgenciaReservas() {}

    public static AgenciaReservas getInstancia() {
        if (instancia == null) {
            instancia = new AgenciaReservas();
        }
        return instancia;
    }

    public void reservarServicio(Servicio servicio) {
        servicio.reservar();
    }
}

// -----------------------------
// INTERFAZ Servicio
// -----------------------------
interface Servicio {
    void reservar();
}

// -----------------------------
// SERVICIOS CONCRETOS
// -----------------------------
class Hotel implements Servicio {
    @Override
    public void reservar() {
        JOptionPane.showMessageDialog(null, "🏨 Hotel reservado correctamente.");
    }
}

class Auto implements Servicio {
    @Override
    public void reservar() {
        JOptionPane.showMessageDialog(null, "🚗 Auto alquilado exitosamente.");
    }
}

class ProveedorVueloExterno {
    public void hacerReservaVuelo() {
        JOptionPane.showMessageDialog(null, "✈️ Vuelo reservado con proveedor externo.");
    }
}

class VueloAdapter implements Servicio {
    private ProveedorVueloExterno proveedor;

    public VueloAdapter(ProveedorVueloExterno proveedor) {
        this.proveedor = proveedor;
    }

    @Override
    public void reservar() {
        proveedor.hacerReservaVuelo();
    }
}

// -----------------------------
// STRATEGY: Métodos de pago
// -----------------------------
interface MetodoPago {
    void pagar(double monto);
}

class PagoTarjeta implements MetodoPago {
    @Override
    public void pagar(double monto) {
        JOptionPane.showMessageDialog(null, "💳 Pago de $" + monto + " realizado con tarjeta.");
    }
}

class PagoPayPal implements MetodoPago {
    @Override
    public void pagar(double monto) {
        JOptionPane.showMessageDialog(null, "💻 Pago de $" + monto + " realizado con PayPal.");
    }
}

class ProcesadorPago {
    private MetodoPago metodo;

    public void setMetodoPago(MetodoPago metodo) {
        this.metodo = metodo;
    }

    public void procesarPago(double monto) {
        if (metodo != null) {
            metodo.pagar(monto);
        } else {
            JOptionPane.showMessageDialog(null, "⚠️ No se ha seleccionado método de pago.");
        }
    }
}

// -----------------------------
// GUI Mejorada integrada con lógica
// -----------------------------
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new InterfazReservasMejorada().crearVentana());
    }
}

class InterfazReservasMejorada {
    private JFrame frame;
    private JComboBox<String> comboServicios;
    private JComboBox<String> comboPago;
    private JTextField txtMonto;
    private JButton btnReservar;
    private JButton btnSalir;

    public void crearVentana() {
        frame = new JFrame("🧳 Agencia de Viajes Mejorada");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(420, 320);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setBackground(new Color(245, 245, 245));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel lblServicio = new JLabel("Seleccione servicio:");
        lblServicio.setFont(new Font("Segoe UI", Font.BOLD, 14));
        comboServicios = new JComboBox<>(new String[]{"Hotel", "Auto", "Vuelo"});
        comboServicios.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        comboServicios.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        JLabel lblPago = new JLabel("Método de pago:");
        lblPago.setFont(new Font("Segoe UI", Font.BOLD, 14));
        comboPago = new JComboBox<>(new String[]{"Tarjeta", "PayPal"});
        comboPago.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        comboPago.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        JLabel lblMonto = new JLabel("Monto a pagar ($):");
        lblMonto.setFont(new Font("Segoe UI", Font.BOLD, 14));
        txtMonto = new JTextField();
        txtMonto.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtMonto.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panelBotones.setBackground(new Color(245, 245, 245));

        btnReservar = new JButton("✅ Reservar");
        btnReservar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnReservar.setEnabled(false);
        btnReservar.setBackground(new Color(100, 180, 100));
        btnReservar.setForeground(Color.WHITE);
        btnReservar.setFocusPainted(false);

        btnSalir = new JButton("❌ Salir");
        btnSalir.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSalir.setBackground(new Color(200, 80, 80));
        btnSalir.setForeground(Color.WHITE);
        btnSalir.setFocusPainted(false);

        panel.add(lblServicio);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(comboServicios);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        panel.add(lblPago);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(comboPago);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        panel.add(lblMonto);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(txtMonto);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        panelBotones.add(btnReservar);
        panelBotones.add(btnSalir);
        panel.add(panelBotones);

        frame.add(panel);

        btnReservar.addActionListener(e -> realizarReserva());
        btnSalir.addActionListener(e -> frame.dispose());

        txtMonto.getDocument().addDocumentListener(new DocumentListener() {
            void validar() {
                String texto = txtMonto.getText().trim();
                boolean valido = texto.matches("\\d*(\\.\\d{0,2})?") && !texto.isEmpty();
                btnReservar.setEnabled(valido);
            }
            @Override public void insertUpdate(DocumentEvent e) { validar(); }
            @Override public void removeUpdate(DocumentEvent e) { validar(); }
            @Override public void changedUpdate(DocumentEvent e) { validar(); }
        });

        frame.setVisible(true);
    }

    private void realizarReserva() {
        String servicioSeleccionado = (String) comboServicios.getSelectedItem();
        String metodoSeleccionado = (String) comboPago.getSelectedItem();
        double monto;

        try {
            monto = Double.parseDouble(txtMonto.getText());
            if (monto <= 0) {
                JOptionPane.showMessageDialog(frame, "Ingrese un monto mayor a cero.");
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(frame, "Monto inválido.");
            return;
        }

        AgenciaReservas agencia = AgenciaReservas.getInstancia();

        Servicio servicio;
        switch (servicioSeleccionado) {
            case "Hotel":
                servicio = new Hotel();
                break;
            case "Auto":
                servicio = new Auto();
                break;
            case "Vuelo":
                servicio = new VueloAdapter(new ProveedorVueloExterno());
                break;
            default:
                JOptionPane.showMessageDialog(frame, "Servicio no válido.");
                return;
        }

        MetodoPago metodo = metodoSeleccionado.equals("Tarjeta") ? new PagoTarjeta() : new PagoPayPal();

        // Ejecutar reserva y pago
        agencia.reservarServicio(servicio);

        ProcesadorPago procesador = new ProcesadorPago();
        procesador.setMetodoPago(metodo);
        procesador.procesarPago(monto);
    }
}
