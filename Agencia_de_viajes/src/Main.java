import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

// -----------------------------
// SINGLETON: Usuario
// -----------------------------
class Usuario {
    private static Usuario instancia;
    private String nombre;

    private Usuario() {}

    public static Usuario getInstancia() {
        if (instancia == null) {
            instancia = new Usuario();
        }
        return instancia;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }
}

// -----------------------------
// BUILDER: ReservaVuelo
// -----------------------------
class ReservaVuelo {
    String origen;
    String destino;
    Date fechaHora;
    double precio;

    public ReservaVuelo(String origen, String destino, Date fechaHora, double precio) {
        this.origen = origen;
        this.destino = destino;
        this.fechaHora = fechaHora;
        this.precio = precio;
    }

    public String getResumen() {
        return "Reserva: " + origen + " ➡ " + destino +
                "\nFecha: " + fechaHora +
                "\nPrecio: $" + precio;
    }
}

class ReservaBuilder {
    private String origen;
    private String destino;
    private Date fechaHora;
    private double precio;

    public ReservaBuilder setOrigen(String origen) {
        this.origen = origen;
        return this;
    }

    public ReservaBuilder setDestino(String destino) {
        this.destino = destino;
        return this;
    }

    public ReservaBuilder setFechaHora(Date fechaHora) {
        this.fechaHora = fechaHora;
        return this;
    }

    public ReservaBuilder setPrecio(double precio) {
        this.precio = precio;
        return this;
    }

    public ReservaVuelo build() {
        return new ReservaVuelo(origen, destino, fechaHora, precio);
    }
}

// -----------------------------
// ADAPTER: Vuelo externo
// -----------------------------
interface Servicio {
    void reservar(ReservaVuelo reserva);
}

class ProveedorExterno {
    public void reservarVuelo(String origen, String destino, Date fecha, double precio) {
        JOptionPane.showMessageDialog(null, "✈ Vuelo confirmado con proveedor externo:\n" +
                origen + " ➡ " + destino + "\nFecha: " + fecha + "\nPrecio: $" + precio);
    }
}

class VueloAdapter implements Servicio {
    private ProveedorExterno proveedor = new ProveedorExterno();

    @Override
    public void reservar(ReservaVuelo reserva) {
        proveedor.reservarVuelo(reserva.origen, reserva.destino, reserva.fechaHora, reserva.precio);
    }
}

// -----------------------------
// STRATEGY: Métodos de pago
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

    public void setMetodo(MetodoPago metodo) {
        this.metodo = metodo;
    }

    public void procesar(double monto) {
        if (metodo != null) {
            metodo.pagar(monto);
        }
    }
}

// -----------------------------
// MAIN
// -----------------------------
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RegistroVentana().mostrar());
    }
}

// -----------------------------
// VENTANA 1: Registro de usuario
// -----------------------------
class RegistroVentana {
    private JFrame frame;

    public void mostrar() {
        frame = new JFrame("Registro Usuario");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 150);
        frame.setLayout(new BorderLayout());

        JTextField campoNombre = new JTextField();
        JButton continuar = new JButton("Continuar");
        JLabel label = new JLabel("Ingrese su nombre para reservar un vuelo:");
        label.setHorizontalAlignment(SwingConstants.CENTER);

        continuar.addActionListener(e -> {
            String nombre = campoNombre.getText().trim();
            if (!nombre.isEmpty()) {
                Usuario.getInstancia().setNombre(nombre);
                frame.dispose();
                new VentanaReserva().mostrar();
            } else {
                JOptionPane.showMessageDialog(frame, "Por favor, ingrese su nombre.");
            }
        });

        frame.add(label, BorderLayout.NORTH);
        frame.add(campoNombre, BorderLayout.CENTER);
        frame.add(continuar, BorderLayout.SOUTH);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}

// -----------------------------
// VENTANA 2: Reserva de vuelo
// -----------------------------
class VentanaReserva {
    private JFrame frame;
    private JComboBox<String> origen, destino, metodoPago;
    private JSpinner spinnerFecha, spinnerHora;
    private JLabel precioLabel;

    private final Map<String, Double> precios = new HashMap<>();

    public void mostrar() {
        frame = new JFrame("Reserva de Vuelo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);
        frame.setLayout(new GridLayout(10, 1));

        origen = new JComboBox<>(new String[]{"El Salvador", "México", "EE.UU."});
        destino = new JComboBox<>(new String[]{"Colombia", "España", "Canadá"});
        metodoPago = new JComboBox<>(new String[]{"Tarjeta", "PayPal"});

        spinnerFecha = new JSpinner(new SpinnerDateModel());
        spinnerFecha.setEditor(new JSpinner.DateEditor(spinnerFecha, "yyyy-MM-dd"));

        spinnerHora = new JSpinner(new SpinnerDateModel());
        spinnerHora.setEditor(new JSpinner.DateEditor(spinnerHora, "HH:mm"));

        precioLabel = new JLabel("Precio: $0.00", SwingConstants.CENTER);
        JButton reservarBtn = new JButton("Reservar Vuelo");

        precios.put("El Salvador-Colombia", 300.0);
        precios.put("El Salvador-España", 850.0);
        precios.put("El Salvador-Canadá", 750.0);
        precios.put("México-Colombia", 280.0);
        precios.put("México-España", 820.0);
        precios.put("México-Canadá", 700.0);
        precios.put("EE.UU.-Colombia", 350.0);
        precios.put("EE.UU.-España", 900.0);
        precios.put("EE.UU.-Canadá", 400.0);

        origen.addActionListener(e -> actualizarPrecio());
        destino.addActionListener(e -> actualizarPrecio());

        reservarBtn.addActionListener(e -> reservar());

        frame.add(new JLabel("Origen:")); frame.add(origen);
        frame.add(new JLabel("Destino:")); frame.add(destino);
        frame.add(new JLabel("Fecha:")); frame.add(spinnerFecha);
        frame.add(new JLabel("Hora:")); frame.add(spinnerHora);
        frame.add(precioLabel);
        frame.add(new JLabel("Método de pago:")); frame.add(metodoPago);
        frame.add(reservarBtn);

        actualizarPrecio();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void actualizarPrecio() {
        String key = origen.getSelectedItem() + "-" + destino.getSelectedItem();
        double precio = precios.getOrDefault(key, 999.99);
        precioLabel.setText("Precio: $" + precio);
    }

    private void reservar() {
        String o = (String) origen.getSelectedItem();
        String d = (String) destino.getSelectedItem();
        if (o.equals(d)) {
            JOptionPane.showMessageDialog(frame, "El país de origen y destino no pueden ser iguales.");
            return;
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime((Date) spinnerFecha.getValue());
        Calendar hora = Calendar.getInstance();
        hora.setTime((Date) spinnerHora.getValue());
        cal.set(Calendar.HOUR_OF_DAY, hora.get(Calendar.HOUR_OF_DAY));
        cal.set(Calendar.MINUTE, hora.get(Calendar.MINUTE));

        String key = o + "-" + d;
        double precio = precios.getOrDefault(key, 999.99);

        ReservaVuelo reserva = new ReservaBuilder()
                .setOrigen(o)
                .setDestino(d)
                .setFechaHora(cal.getTime())
                .setPrecio(precio)
                .build();

        new VueloAdapter().reservar(reserva);

        MetodoPago metodo = metodoPago.getSelectedItem().equals("Tarjeta") ? new PagoTarjeta() : new PagoPayPal();
        ProcesadorPago procesador = new ProcesadorPago();
        procesador.setMetodo(metodo);
        procesador.procesar(precio);
    }
}
