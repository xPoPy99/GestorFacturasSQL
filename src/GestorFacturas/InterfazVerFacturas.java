package GestorFacturas;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class InterfazVerFacturas extends JFrame {
    private ConexionBDD conexionBDD;
    private JTable tablaFacturas;
    private JTextField txtProveedor;
    private JTextField txtFecha;
    private JLabel lblGastoTotal;
    private JLabel lblGastoTotalTodos;

    public InterfazVerFacturas(ConexionBDD conexionBDD) {
        this.conexionBDD = conexionBDD;
        setTitle("Gestor de Facturas");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Configuración de los componentes de la interfaz
        JPanel panelFiltros = new JPanel();
        panelFiltros.setLayout(new GridLayout(1, 5, 10, 10));  // Cambié a GridLayout(1, 5) para mejor distribución

        JLabel lblProveedor = new JLabel("Proveedor:");
        txtProveedor = new JTextField();
        JLabel lblFecha = new JLabel("Fecha (YYYY-MM):");
        txtFecha = new JTextField();
        JButton btnBuscar = new JButton("Buscar");
        JButton btnLimpiar = new JButton("Limpiar / Actualizar");

        panelFiltros.add(lblProveedor);
        panelFiltros.add(txtProveedor);
        panelFiltros.add(lblFecha);
        panelFiltros.add(txtFecha);
        panelFiltros.add(btnBuscar);
        panelFiltros.add(btnLimpiar);

        lblGastoTotal = new JLabel("Gasto Total por Proveedor: ");
        lblGastoTotalTodos = new JLabel("Gasto Total por Todos los Proveedores: 0.00 €");

        // Configuración de la tabla
        tablaFacturas = new JTable();
        JScrollPane scrollPane = new JScrollPane(tablaFacturas);

        // Botón para abrir la interfaz de agregar facturas
        JButton btnAgregarFactura = new JButton("Agregar Nueva Factura");

        // Layout principal
        setLayout(new BorderLayout());
        add(panelFiltros, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Panel para mostrar el gasto total y el gasto por proveedor
        JPanel panelGastos = new JPanel();
        panelGastos.setLayout(new GridLayout(3, 1, 10, 10));  // Cambié a GridLayout(3, 1) para mostrar 3 filas
        panelGastos.add(lblGastoTotalTodos);  // Gasto total por todos los proveedores
        panelGastos.add(lblGastoTotal);      // Gasto total por proveedor

        // Crear un panel para los botones y el panelGastos
        JPanel panelSur = new JPanel();
        panelSur.setLayout(new BorderLayout());
        panelSur.add(panelGastos, BorderLayout.CENTER); // Coloca el panel de gastos en el centro
        panelSur.add(btnAgregarFactura, BorderLayout.SOUTH); // Coloca el botón de agregar factura en la parte inferior

        // Añadir el panel sur a la ventana
        add(panelSur, BorderLayout.SOUTH);

        // Acción de búsqueda
        btnBuscar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actualizarTabla();
            }
        });

        // Acción de limpiar
        btnLimpiar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actualizarTabla();
            }
        });

        // Acción para abrir la interfaz de nueva factura
        btnAgregarFactura.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                abrirInterfazNuevaFactura();
            }
        });

        // Cargar todos los registros al iniciar la interfaz
        actualizarTabla();
    }

    private void actualizarTabla() {
        String proveedor = txtProveedor.getText();
        String fecha = txtFecha.getText();

        try {
            conexionBDD.conectar();
            String query = "SELECT * FROM facturas WHERE 1=1";
            
            if (!proveedor.isEmpty()) {
                query += " AND proveedor LIKE ?";
            }
            if (!fecha.isEmpty()) {
                query += " AND fecha LIKE ?";
            }
            
            PreparedStatement ps = conexionBDD.conexion.prepareStatement(query);
            int index = 1;
            
            if (!proveedor.isEmpty()) {
                ps.setString(index++, "%" + proveedor + "%");
            }
            if (!fecha.isEmpty()) {
                ps.setString(index, fecha + "%");
            }
            
            ResultSet rs = ps.executeQuery();
            DefaultTableModel model = new DefaultTableModel(new String[]{"Fecha", "Proveedor", "Num. Factura", "Total"}, 0);
            
            while (rs.next()) {
                model.addRow(new Object[] {
                    rs.getDate("fecha").toLocalDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                    rs.getString("proveedor"),
                    rs.getInt("numero_factura"),
                    String.format("%.2f €", rs.getDouble("total")) // Formatear el total con el símbolo €
                });
            }
            
            tablaFacturas.setModel(model);

            // Actualizar el gasto total por proveedor y todos los proveedores
            calcularGastoPorProveedor();
            calcularGastoTotalTodos();

            rs.close();
            ps.close();
            conexionBDD.desconectar();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void calcularGastoPorProveedor() {
        // Obtener el mes y el año actuales
        LocalDate fechaActual = LocalDate.now();
        int mesActual = fechaActual.getMonthValue();
        int añoActual = fechaActual.getYear();

        try {
            conexionBDD.conectar();

            // 1. Obtener todos los proveedores registrados en la base de datos
            String queryProveedores = "SELECT DISTINCT proveedor FROM facturas";
            PreparedStatement psProveedores = conexionBDD.conexion.prepareStatement(queryProveedores);
            ResultSet rsProveedores = psProveedores.executeQuery();

            StringBuilder resultado = new StringBuilder("<html>Gasto total mensual por proveedor (" + mesActual + "/" + añoActual + "):<br><br>");

            // 2. Recorrer todos los proveedores
            while (rsProveedores.next()) {
                String proveedorNombre = rsProveedores.getString("proveedor");

                // 3. Sumar el total de las facturas para cada proveedor en el mes y año actuales
                String query = "SELECT SUM(total) AS total FROM facturas WHERE proveedor = ? AND YEAR(fecha) = ? AND MONTH(fecha) = ?";
                PreparedStatement ps = conexionBDD.conexion.prepareStatement(query);
                ps.setString(1, proveedorNombre);  // Establecer el proveedor
                ps.setInt(2, añoActual);           // Establecer el año actual
                ps.setInt(3, mesActual);           // Establecer el mes actual

                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    double total = rs.getDouble("total");
                    resultado.append(String.format("Proveedor: %s - %.2f €<br>", proveedorNombre, total));
                } else {
                    // Si no hay facturas para ese proveedor en el mes y año actuales
                    resultado.append(String.format("Proveedor: %s - 0.00 €<br>", proveedorNombre));
                }

                rs.close();
                ps.close();
            }

            // Mostrar el resultado en el label
            resultado.append("</html>");
            lblGastoTotal.setText(resultado.toString());

            rsProveedores.close();
            psProveedores.close();
            conexionBDD.desconectar();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void calcularGastoTotalTodos() {
        try {
            conexionBDD.conectar();
            
            // Obtener el mes y año actuales
            LocalDate fechaActual = LocalDate.now();
            int mesActual = fechaActual.getMonthValue();
            int añoActual = fechaActual.getYear();
            
            // Consulta SQL para obtener el gasto total del mes actual
            String query = "SELECT SUM(total) AS total FROM facturas WHERE YEAR(fecha) = ? AND MONTH(fecha) = ?";
            PreparedStatement ps = conexionBDD.conexion.prepareStatement(query);
            ps.setInt(1, añoActual);  // Establecer el año actual
            ps.setInt(2, mesActual);  // Establecer el mes actual
            
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                double total = rs.getDouble("total");
                lblGastoTotalTodos.setText("Gasto Total del Mes (" + mesActual + "/" + añoActual + "): " + String.format("%.2f €", total));
            }
            
            rs.close();
            ps.close();
            conexionBDD.desconectar();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void abrirInterfazNuevaFactura() {
        SwingUtilities.invokeLater(() -> {
            InterfazNuevaFactura interfazNuevaFactura = new InterfazNuevaFactura();
            interfazNuevaFactura.setVisible(true);
        });
    }

    public static void main(String[] args) {
        ConexionBDD conexionBDD = new ConexionBDD("jdbc:mysql://localhost:3306/gestionfacturas", "dani", "contraseña99");
        InterfazVerFacturas interfaz = new InterfazVerFacturas(conexionBDD);
        interfaz.setVisible(true);
    }
}
