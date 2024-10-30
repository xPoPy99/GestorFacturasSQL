package GestorFacturas;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class InterfazNuevaFactura extends JFrame {

	private JTextField proveedorField, numeroField, fechaField, importeField;
	private JButton subirImagenButton, guardar;
	private JLabel imagenLabel;
	private List<Facturas> facturas = new ArrayList<>();
	private File archivoImagen;

	public InterfazNuevaFactura() {
		setTitle("Gestión de Facturas");
		setSize(400, 300);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new GridBagLayout());

		// Configuración del layout y constraints para GridBagLayout
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 5, 5, 5); // Espacio alrededor de cada componente
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.WEST;

		// Proveedor
		gbc.gridx = 0;
		gbc.gridy = 0;
		add(new JLabel("Proveedor:"), gbc);
		proveedorField = new JTextField(15);
		gbc.gridx = 1;
		add(proveedorField, gbc);

		// Número de Factura
		gbc.gridx = 0;
		gbc.gridy = 1;
		add(new JLabel("Número de Factura:"), gbc);
		numeroField = new JTextField(15);
		gbc.gridx = 1;
		add(numeroField, gbc);

		// Fecha
		gbc.gridx = 0;
		gbc.gridy = 2;
		add(new JLabel("Fecha (YYYY-MM-DD):"), gbc);
		fechaField = new JTextField(10);
		gbc.gridx = 1;
		add(fechaField, gbc);

		// Importe
		gbc.gridx = 0;
		gbc.gridy = 3;
		add(new JLabel("Importe (EJ. 123.32):"), gbc);
		importeField = new JTextField(10);
		gbc.gridx = 1;
		add(importeField, gbc);

		// Botón para subir imagen
		subirImagenButton = new JButton("Subir Imagen");
		gbc.gridx = 0;
		gbc.gridy = 4;
		gbc.gridwidth = 2;
		add(subirImagenButton, gbc);

		// Etiqueta de imagen
		imagenLabel = new JLabel();
		gbc.gridy = 5;
		add(imagenLabel, gbc);

		// Botón para guardar
		guardar = new JButton("Guardar factura");
		gbc.gridy = 6;
		add(guardar, gbc);

		// Acción del botón de subir imagen
		subirImagenButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				subirImagen();
			}
		});

		// Acción del botón para guardar factura
		guardar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				guardarFactura();
			}
		});
	}

	private void subirImagen() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Seleccionar imagen de la factura");
		int userSelection = fileChooser.showOpenDialog(this);

		if (userSelection == JFileChooser.APPROVE_OPTION) {
			archivoImagen = fileChooser.getSelectedFile();
			imagenLabel.setText("Imagen seleccionada: " + archivoImagen.getName());
		}
	}

	// Guardar la factura en la base de datos
	private void guardarFactura() {
	    final String url = "jdbc:mysql://localhost:3306/gestionfacturas";
	    final String usuario = "dani";
	    final String pass = "contraseña99";

	    // Datos de entrada usuario proveedor, factura, fecha e importe
	    String proveedor = proveedorField.getText();
	    String numeroFactura = numeroField.getText();
	    String fechaString = fechaField.getText();
	    String importe = importeField.getText();

	    // Comprobar si los datos no son vacios:
	    if (proveedor.isEmpty() || numeroFactura.isEmpty() || fechaString.isEmpty() || importe.isEmpty()) {
	        JOptionPane.showMessageDialog(this, "Por favor, complete todos los campos y suba una imagen.");
	        return;
	    }

	    // Conectar a la base de datos
	    ConexionBDD conexion = new ConexionBDD(url, usuario, pass);
	    try {
	        conexion.conectar(); // Establece la conexión aquí

	        // Comprobar conexión
	        System.out.println("La conexión a la base de datos es buena.");
	        System.out.println(proveedor + " | " + numeroFactura + " | " + fechaString + " | " + importe);

	        // Parsear la fecha y guardar imagen
	        LocalDate fecha;
	        try {
	            fecha = LocalDate.parse(fechaString);
	        } catch (Exception ex) {
	            JOptionPane.showMessageDialog(this, "Formato de fecha inválido. Use YYYY-MM-DD.");
	            return;
	        }

	        // Guardar imagen
	        Path destino = Paths.get("D:\\Facturas",
	                fecha.getYear() + "-" + String.format("%02d", fecha.getMonthValue()));
	        try {
	            Files.createDirectories(destino);
	            Path destinoImagen = destino.resolve(archivoImagen.getName());
	            Files.copy(archivoImagen.toPath(), destinoImagen, StandardCopyOption.REPLACE_EXISTING);

	            JOptionPane.showMessageDialog(this, "Factura guardada exitosamente.");
	            System.out.println("Imagen guardada en: " + destinoImagen);

	        } catch (IOException ex) {
	            JOptionPane.showMessageDialog(this, "Error al guardar la imagen: " + ex.getMessage());
	        }

	        // Crear objeto factura
	        Facturas factura = new Facturas(proveedor, Integer.parseInt(numeroFactura), fecha, destino.toString(),
	                Double.parseDouble(importe));

	        // Datos para la tabla
	        String tabla = "facturas";
	        String[] columnas = {"proveedor", "fecha", "imagen", "total", "numero_factura"};
	        Object[] valores = {proveedor, fecha, destino.toString(), Double.parseDouble(importe), Integer.parseInt(numeroFactura)};
	        conexion.crearRegistro(tabla, columnas, valores);

	    } catch (SQLException e) {
	        e.printStackTrace();
	        JOptionPane.showMessageDialog(this, "Error al guardar la factura: " + e.getMessage());
	    } finally {
	        try {
	            conexion.desconectar(); // Asegúrate de desconectar al final
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }

	    // Limpiar los campos
	    proveedorField.setText("");
	    numeroField.setText("");
	    fechaField.setText("");
	    imagenLabel.setText("");
	    importeField.setText("");
	    archivoImagen = null;
	}


	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			InterfazNuevaFactura frame = new InterfazNuevaFactura();
			frame.setVisible(true);
		});
	}

}
