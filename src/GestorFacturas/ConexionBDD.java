package GestorFacturas;

import java.sql.*;
 
import java.sql.*;

public class ConexionBDD {
    Connection conexion;
    private String url;
    private String usuario;
    private String pass;

    public ConexionBDD(String url, String usuario, String pass) {
        this.url = url;
        this.usuario = usuario;
        this.pass = pass;
    }

    public void conectar() throws SQLException {
        setConexion(DriverManager.getConnection(url, usuario, pass));
    }

    public void desconectar() throws SQLException {
        if (getConexion() != null && !getConexion().isClosed()) {
            getConexion().close();
        }
    }
    
    public boolean probarConexion() {
        try {
            this.conectar();
            System.out.println("Conexión exitosa.");
            this.desconectar();
            return true;
        } catch (SQLException e) {
            System.out.println("Error al conectar con la base de datos: " + e.getMessage());
            return false;
        }
    }


    public void crearRegistro(String tabla, String[] columnas, Object[] valores) throws SQLException {
        StringBuilder consulta = new StringBuilder();
        consulta.append("INSERT INTO ").append(tabla).append(" (");
        
        for (int i = 0; i < columnas.length; i++) {
            consulta.append(columnas[i]);
            
            if (i < columnas.length - 1) {
                consulta.append(", ");
            }
        }
        
        consulta.append(") VALUES (");
        
        for (int i = 0; i < valores.length; i++) {
            consulta.append("?");
            
            if (i < valores.length - 1) {
                consulta.append(", ");
            }
        }
        
        consulta.append(")");

        PreparedStatement ps = getConexion().prepareStatement(consulta.toString());

        for (int i = 0; i < valores.length; i++) {
            ps.setObject(i + 1, valores[i]);
        }

        ps.executeUpdate();
        ps.close();
    }

    public ResultSet leerRegistros(String tabla) throws SQLException {
        Statement statement = getConexion().createStatement();
        String consulta = "SELECT * FROM " + tabla;
        ResultSet resultado = statement.executeQuery(consulta);
        return resultado;
    }

    public void actualizarRegistro(String tabla, String columnaCondicion, Object valorCondicion, String[] columnas, Object[] valores) throws SQLException {
        StringBuilder consulta = new StringBuilder();
        consulta.append("UPDATE ").append(tabla).append(" SET ");
        
        for (int i = 0; i < columnas.length; i++) {
            consulta.append(columnas[i]).append(" = ?");

            if (i < columnas.length - 1) {
                consulta.append(", ");
            }
        }
        
        consulta.append(" WHERE ").append(columnaCondicion).append(" = ?");
        
        PreparedStatement ps = getConexion().prepareStatement(consulta.toString());

        for (int i = 0; i < valores.length; i++) {
            ps.setObject(i + 1, valores[i]);
        }

        ps.setObject(valores.length + 1, valorCondicion);

        ps.executeUpdate();
        ps.close();
    }

    public void eliminarRegistro(String tabla, String columnaCondicion, Object valorCondicion) throws SQLException {
        String consulta = "DELETE FROM " + tabla + " WHERE " + columnaCondicion + " = ?";
        PreparedStatement ps = getConexion().prepareStatement(consulta);
        ps.setObject(1, valorCondicion);
        ps.executeUpdate();
        ps.close();
    }

	public Connection getConexion() {
		return conexion;
	}

	public void setConexion(Connection conexion) {
		this.conexion = conexion;
	}
}


