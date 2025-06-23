import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
// INTERFAZ: Servicio
// -----------------------------
interface Servicio {
    void reservar();
}

// -----------------------------
// IMPLEMENTACIONES DE SERVICIO
// -----------------------------
class Hotel implements Servicio {
    public void reservar() {
        JOptionPane.showMessageDialog(null, "🏨 Hotel reservado correctamente.");
    }
}

class Auto implements Servicio {
    public void reservar() {
        JOptionPane.showMessageDialog(null, "🚗 Auto alquilado exitosamente.");
    }
}

// -----------------------------
// ADAPTER para proveedor externo
// -----------------------------
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
// STRATEGY para métodos de pago
// -----------------------------
interface MetodoPago {
    void pagar(double monto);
}

class PagoTarjeta implements MetodoPago {
    public void pagar(double monto) {
        JOptionPane.showMessageDialog(null, "💳 Pago de $" + monto + " realizado con tarjeta.");
    }
}

class PagoPayPal implements MetodoPago {
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
// GUI PRINCIPAL
// -----------------------------
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new InterfazReservas().crearVentana());
    }
}

class InterfazReservas {
    private JFrame frame;
    private JComboBox<String> comboServicios;
    private JComboBox<String> comboPago;
    private JTextField txtMonto;

    public void crearVentana() {
        frame = new JFrame("🧳 Agencia de Viajes");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 250);
        frame.setLayout(new GridLayout(5, 2, 10, 10));

        comboServicios = new JComboBox<>(new String[]{"Hotel", "Auto", "Vuelo"});
        comboPago = new JComboBox<>(new String[]{"Tarjeta", "PayPal"});
        txtMonto = new JTextField();

        frame.add(new JLabel("Seleccione servicio:"));
        frame.add(comboServicios);

        frame.add(new JLabel("Método de pago:"));
        frame.add(comboPago);

        frame.add(new JLabel("Monto a pagar ($):"));
        frame.add(txtMonto);

        JButton btnReservar = new JButton("Reservar");
        JButton btnSalir = new JButton("Salir");

        frame.add(btnReservar);
        frame.add(btnSalir);

        btnReservar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                realizarReserva();
            }
        });

        btnSalir.addActionListener(e -> frame.dispose());

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void realizarReserva() {
        String servicioSeleccionado = (String) comboServicios.getSelectedItem();
        String metodoSeleccionado = (String) comboPago.getSelectedItem();
        double monto;

        try {
            monto = Double.parseDouble(txtMonto.getText());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(frame, "Ingrese un monto válido.");
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

        MetodoPago metodo;
        metodo = metodoSeleccionado.equals("Tarjeta") ? new PagoTarjeta() : new PagoPayPal();

        agencia.reservarServicio(servicio);

        ProcesadorPago procesador = new ProcesadorPago();
        procesador.setMetodoPago(metodo);
        procesador.procesarPago(monto);
    }
}
