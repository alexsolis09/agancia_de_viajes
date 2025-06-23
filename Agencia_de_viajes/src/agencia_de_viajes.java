package src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// -----------------------------
// SINGLETON: AgenciaReservas
// Esta clase garantiza que solo exista una instancia que controle las reservas.
// -----------------------------
class AgenciaReservas {
    // Instancia estática única (Singleton)
    private static AgenciaReservas instancia;

    // Constructor privado para evitar instanciación externa
    private AgenciaReservas() {}

    // Método para obtener la única instancia (creación perezosa)
    public static AgenciaReservas getInstancia() {
        if (instancia == null) {
            instancia = new AgenciaReservas();
        }
        return instancia;
    }

    // Método para realizar la reserva de cualquier servicio que implemente la interfaz Servicio
    public void reservarServicio(Servicio servicio) {
        servicio.reservar();
    }
}

// -----------------------------
// INTERFAZ Servicio
// Define el método que cualquier servicio debe implementar para reservarse.
// -----------------------------
interface Servicio {
    void reservar();
}

// -----------------------------
// CLASES CONCRETAS DE SERVICIO
// Implementan el comportamiento específico para reservar cada tipo de servicio.
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

// -----------------------------
// PROVEEDOR EXTERNO (SIN CAMBIOS)
// Esta clase simula un sistema externo para reservar vuelos que no implementa Servicio.
// -----------------------------
class ProveedorVueloExterno {
    public void hacerReservaVuelo() {
        JOptionPane.showMessageDialog(null, "✈️ Vuelo reservado con proveedor externo.");
    }
}

// -----------------------------
// ADAPTER: VueloAdapter
// Adapta la interfaz de ProveedorVueloExterno para que sea compatible con Servicio.
// -----------------------------
class VueloAdapter implements Servicio {
    private ProveedorVueloExterno proveedor;

    public VueloAdapter(ProveedorVueloExterno proveedor) {
        this.proveedor = proveedor;
    }

    @Override
    public void reservar() {
        // Traduce la llamada reservar() a hacerReservaVuelo()
        proveedor.hacerReservaVuelo();
    }
}

// -----------------------------
// STRATEGY: Métodos de pago
// Define una interfaz para diferentes formas de pago.
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

// Contexto que utiliza una estrategia de pago para procesar el pago
class ProcesadorPago {
    private MetodoPago metodo;

    // Permite cambiar la estrategia (método de pago) en tiempo de ejecución
    public void setMetodoPago(MetodoPago metodo) {
        this.metodo = metodo;
    }

    // Procesa el pago usando la estrategia seleccionada
    public void procesarPago(double monto) {
        if (metodo != null) {
            metodo.pagar(monto);
        } else {
            JOptionPane.showMessageDialog(null, "⚠️ No se ha seleccionado método de pago.");
        }
    }
}

// -----------------------------
// CLASE PRINCIPAL con GUI
// -----------------------------
public class Main {
    public static void main(String[] args) {
        // Ejecuta la interfaz gráfica en el hilo de eventos de Swing para seguridad
        SwingUtilities.invokeLater(() -> new InterfazReservas().crearVentana());
    }
}

// Clase que maneja la interfaz gráfica y la lógica de interacción
class InterfazReservas {
    private JFrame frame;
    private JComboBox<String> comboServicios;
    private JComboBox<String> comboPago;
    private JTextField txtMonto;

    // Configura y muestra la ventana principal
    public void crearVentana() {
        frame = new JFrame("🧳 Agencia de Viajes");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 250);
        frame.setLayout(new GridLayout(5, 2, 10, 10));

        // Inicialización de componentes
        comboServicios = new JComboBox<>(new String[]{"Hotel", "Auto", "Vuelo"});
        comboPago = new JComboBox<>(new String[]{"Tarjeta", "PayPal"});
        txtMonto = new JTextField();

        // Añadir componentes a la ventana
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

        // Listener para botón reservar
        btnReservar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                realizarReserva();
            }
        });

        // Listener para botón salir
        btnSalir.addActionListener(e -> frame.dispose());

        frame.setLocationRelativeTo(null); // Centrar ventana
        frame.setVisible(true);
    }

    // Método que procesa la reserva y el pago
    private void realizarReserva() {
        String servicioSeleccionado = (String) comboServicios.getSelectedItem();
        String metodoSeleccionado = (String) comboPago.getSelectedItem();
        double monto;

        // Validar monto ingresado
        try {
            monto = Double.parseDouble(txtMonto.getText());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(frame, "Ingrese un monto válido.");
            return;
        }

        // Obtener instancia única de la agencia (Singleton)
        AgenciaReservas agencia = AgenciaReservas.getInstancia();

        Servicio servicio;
        // Crear la instancia del servicio seleccionado usando Adapter si es vuelo
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

        // Selección de estrategia de pago (Strategy)
        MetodoPago metodo;
        if (metodoSeleccionado.equals("Tarjeta")) {
            metodo = new PagoTarjeta();
        } else {
            metodo = new PagoPayPal();
        }

        // Realizar la reserva usando Singleton y Adapter
        agencia.reservarServicio(servicio);

        // Procesar el pago usando Strategy
        ProcesadorPago procesador = new ProcesadorPago();
        procesador.setMetodoPago(metodo);
        procesador.procesarPago(monto);
    }
}
