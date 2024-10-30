package GestorFacturas;

import java.time.LocalDate;

public class Facturas {

	private String proveedor;
	private int numeroFactura;
	private LocalDate fecha;
	private String rutaImagen;
	private double precio;

	public Facturas(String proveedor, int numeroFactura, LocalDate fecha, String rutaImagen, double precio) {
		this.proveedor = proveedor;
		this.numeroFactura = numeroFactura;
		this.fecha = fecha;
		this.rutaImagen = rutaImagen;
		this.precio = precio;
	}

	public String getProveedor() {
		return proveedor;
	}

	public int getNumeroFactura() {
		return numeroFactura;
	}

	public LocalDate getFecha() {
		return fecha;
	}

	public String getRutaImagen() {
		return rutaImagen;
	}

	public void setProveedor(String proveedor) {
		this.proveedor = proveedor;
	}

	public void setNumeroFactura(int numeroFactura) {
		this.numeroFactura = numeroFactura;
	}

	public void setFecha(LocalDate fecha) {
		this.fecha = fecha;
	}

	public void setRutaImagen(String rutaImagen) {
		this.rutaImagen = rutaImagen;
	}

	public double getPrecio() {
		return precio;
	}

	public void setPrecio(int precio) {
		this.precio = precio;
	}

	/*public static void main(String[] args) {
		// TODO Auto-generated method stub
		String url = "jdbc:mysql://localhost:3306/gestionfacturas";
        String usuario = "dani";
        String pass = "contraseña99";

        ConexionBDD conexion = new ConexionBDD(url, usuario, pass);
        if (conexion.probarConexion()) {
            System.out.println("La conexión a la base de datos es buena.");
        } else {
            System.out.println("La conexión a la base de datos ha fallado.");
        }

	}*/

}
