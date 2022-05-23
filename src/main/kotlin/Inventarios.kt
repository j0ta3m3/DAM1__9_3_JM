import java.sql.Connection
import java.sql.SQLException

data class Inventarios(
    var id_articulo: Int,
    var nombre: String,
    var comentario: String,
    var precio: Double,
    var id_tienda: Int
)

class inventariosDAO(private val c: Connection) {
    // En el companion object creamos todas las constantes.
    // Las constante definirán las plantillas de las sentencias que necesitamos para construir
    // los selects, inserts, deletes, updates.

    // En aquellos casos en donde necesitemos insertar un parametro, pondremos un ?
    // Luego lo sistituiremos llamando a métodos setX, donde X será (Int, String, ...)
    // dependiendo del tiempo de dato que corresponda.
    companion object {
        private const val SCHEMA = "default"

        private const val INVENTARIOS = "INVENTARIOS"

        private const val TRUNCATE_TABLE_INVENTARIOS = "TRUNCATE TABLE INVENTARIOS"

        private const val CREATE_TABLE_INVENTARIOS_SQL =
            "CREATE TABLE INVENTARIOS (\n" +
                    "ID_ARTICULO NUMBER(10,0) CONSTRAINT PK_ID_ARTICULO PRIMARY KEY, NOMBRE VARCHAR2(50) UNIQUE, COMENTARIO VARCHAR2(200) NOT\n" +
                    "NULL, PRECIO NUMBER(10,2) CHECK(PRECIO>0), ID_TIENDA NUMBER(10,0) CONSTRAINT FK_ID_TIENDA REFERENCES TIENDAS(\n" +
                    "ID_TIENDA) );"
        private const val INSERT_INVENTARIOS_SQL =
            "INSERT INTO INVENTARIOS" + "  (ID_ARTICULO, NOMBRE, COMENTARIO, PRECIO, ID_TIENDA) VALUES " + " (?, ?, ?,?,?);"

        private const val SELECT_INVENTARIOS_BY_ID =
            "select ID_ARTICULO,NOMBRE,COMENTARIO,PRECIO,ID_TIENDA WHERE ID_INVENTARIO = ?"

        private const val SELECT_ALL_INVENTARIOS = "select * from INVENTARIOS"

        private const val DELETE_INVENTARIOS_SQL = "delete from INVENTARIOS where id = ?;"

        private const val UPDATE_INVENTARIOS_SQL =
            "update INVENTARIOS set ID_ARTICULO = ?, NOMBRE = ?, COMENTARIO = ?, PRECIO = ?, ID_TIENDA = ?;"

        private const val MIUPDATE_INVENTARIOS =
            "update INVENTARIOS set PRECIO = ? WHERE PRECIO > 2000.00 AND ID_ARTICULO = ? ;"

        private const val CREATE_INDICE = "CREATE INDEX IDX_ID_TIENDA ON INVENTARIOS(ID_TIENDA);"

        private const val SELECT_PRECIO_MAYOR_DE_2000 = "select * from INVENTARIOS where PRECIO > 2000.00 "

        private const val SELECT_INVENTARIO_BY_IDTIENDA = "select * from INVENTARIOS where ID_TIENDA = ? ;"
    }

    fun miSelect(): List<Inventarios> {

        // using try-with-resources to avoid closing resources (boiler plate code)
        val inventarios: MutableList<Inventarios> = ArrayList()
        // Step 1: Establishing a Connection
        try {
            c.prepareStatement(SELECT_PRECIO_MAYOR_DE_2000).use { st ->
                // Step 3: Execute the query or update query
                val rs = st.executeQuery()

                // Step 4: Process the ResultSet object.
                while (rs.next()) {

                    val id_articulo = rs.getInt("id_articulo")
                    val nombre = rs.getString("nombre")
                    val comentario = rs.getString("comentario")
                    val precio = rs.getDouble("precio")
                    val id_tienda = rs.getInt("id_tienda")



                    inventarios.add(Inventarios(id_articulo, nombre, comentario, precio, id_tienda))

                }
            }

        } catch (e: SQLException) {
            printSQLException(e)
        }
        return inventarios
    }

    // Actualizo el producto que sea necesario y le subo el 15% a su precio
    fun miUpdate(inventario: Inventarios) {

        var porcentaje = (inventario.precio * 15) / 100

        var precioNuevo = porcentaje + inventario.precio

        try {
            c.prepareStatement(MIUPDATE_INVENTARIOS).use { st ->

                st.setDouble(1, precioNuevo)
                st.setInt(2, inventario.id_articulo)

                println(st)
                st.executeUpdate()
            }
            //Commit the change to the database
            c.commit()
        } catch (e: SQLException) {
            printSQLException(e)
        }

    }

    fun prepareTable() {
        val metaData = c.metaData
        val rs = metaData.getTables(null, SCHEMA, INVENTARIOS, null)

        if (!rs.next()) createTable() else truncateTable()
    }

    private fun truncateTable() {
        println(TRUNCATE_TABLE_INVENTARIOS)
        // try-with-resource statement will auto close the connection.
        try {
            c.createStatement().use { st ->
                st.execute(TRUNCATE_TABLE_INVENTARIOS)
            }
            //Commit the change to the database
            c.commit()
        } catch (e: SQLException) {
            printSQLException(e)
        }
    }

    fun createIndice() {
        println(CREATE_INDICE)
        // try-with-resource statement will auto close the connection.
        try {
            c.createStatement().use { st ->
                st.execute(CREATE_INDICE)
            }
            //Commit the change to the database
            c.commit()
        } catch (e: SQLException) {
            printSQLException(e)
        }
    }


    private fun createTable() {
        println(CREATE_TABLE_INVENTARIOS_SQL)
        // try-with-resource statement will auto close the connection.
        try {
            //Get and instance of statemnt from the connection and use
            //the execute() method to execute the sql
            c.createStatement().use { st ->
                //SQL statement to create a table
                st.execute(CREATE_TABLE_INVENTARIOS_SQL)
            }
            //Commit the change to the database
            c.commit()
        } catch (e: SQLException) {
            printSQLException(e)
        }
    }

    private fun printSQLException(ex: SQLException) {
        for (e in ex) {
            if (e is SQLException) {
                e.printStackTrace(System.err)
                System.err.println("SQLState: " + e.sqlState)
                System.err.println("Error Code: " + e.errorCode)
                System.err.println("Message: " + e.message)
                var t = ex.cause
                while (t != null) {
                    println("Cause: $t")
                    t = t.cause
                }
            }
        }

    }


    fun insertInventario(inventarios: Inventarios) {
        println(INSERT_INVENTARIOS_SQL)
        // try-with-resource statement will auto close the connection.


        try {
            c.prepareStatement(INSERT_INVENTARIOS_SQL).use { st ->
                st.setInt(1, inventarios.id_articulo)
                st.setString(2, inventarios.nombre)
                st.setString(3, inventarios.comentario)
                st.setDouble(4, inventarios.precio)
                st.setInt(5, inventarios.id_tienda)

                println(st)
                st.executeUpdate()
            }
            //Commit the change to the database
            c.commit()
        } catch (e: SQLException) {
            printSQLException(e)
        }
    }

    fun selectByIdTienda(id: Int): Inventarios? {

        var miInventario: Inventarios? = null

        try {
            c.prepareStatement(SELECT_INVENTARIO_BY_IDTIENDA).use { st ->
                st.setInt(1, id)
                println(st)
                // Step 3: Execute the query or update query
                val rs = st.executeQuery()


                // Step 4: Process the ResultSet object.
                while (rs.next()) {


                    val nombre = rs.getString("nombre")
                    val comentario = rs.getString("comentario")
                    val precio = rs.getDouble("precio")
                    val id_tienda = rs.getInt("id_tienda")
                    miInventario = Inventarios(id, nombre, comentario, precio, id_tienda)
                }
            }

        } catch (e: SQLException) {
            printSQLException(e)
        }
        return miInventario
    }

    fun selectAll(): List<Inventarios> {

        // using try-with-resources to avoid closing resources (boiler plate code)
        val inventarios: MutableList<Inventarios> = ArrayList()
        // Step 1: Establishing a Connection
        try {
            c.prepareStatement(SELECT_ALL_INVENTARIOS).use { st ->
                // Step 3: Execute the query or update query
                val rs = st.executeQuery()

                // Step 4: Process the ResultSet object.
                while (rs.next()) {

                    val id_articulo = rs.getInt("id_articulo")
                    val nombre = rs.getString("nombre")
                    val comentario = rs.getString("comentario")
                    val precio = rs.getDouble("precio")
                    val id_tienda = rs.getInt("id_tienda")



                    inventarios.add(Inventarios(id_articulo, nombre, comentario, precio, id_tienda))

                }
            }

        } catch (e: SQLException) {
            printSQLException(e)
        }
        return inventarios
    }
}
