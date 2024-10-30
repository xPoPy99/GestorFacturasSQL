package GestorFacturas;

import java.time.LocalDate;

public class Facturas {

	private String proveedor;
	private String numeroFactura;
	private LocalDate fecha;
	private String rutaImagen;
	private int precio;

	public Facturas(String proveedor, String numeroFactura, LocalDate fecha, String rutaImagen, int precio) {
		this.proveedor = proveedor;
		this.numeroFactura = numeroFactura;
		this.fecha = fecha;
		this.rutaImagen = rutaImagen;
		this.precio = precio;
	}

	public String getProveedor() {
		return proveedor;
	}

	public String getNumeroFactura() {
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

	public void setNumeroFactura(String numeroFactura) {
		this.numeroFactura = numeroFactura;
	}

	public void setFecha(LocalDate fecha) {
		this.fecha = fecha;
	}

	public void setRutaImagen(String rutaImagen) {
		this.rutaImagen = rutaImagen;
	}

	public int getPrecio() {
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
