import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException


fun mai() {
    val str = "Programming is easy to learn"
    val result = str.replace("\\s+".toRegex(), "")
    println(result)
}


fun getCount(str : String) : Int {

    var contador = 0

    for (i in str) {
        if (i.toString() == "a") {
            contador ++
        } else {
            if (i.toString() == "e") {
                contador ++
            } else {
                if (i.toString() == "i") {
                    contador ++
                } else {
                    if (i.toString() == "o") {
                        contador ++
                    } else {
                        if (i.toString() == "u") {
                            contador ++
                        }
                    }
                }
            }
        }
    }
        println(contador.toString())
        return contador
    }


    fun noSpace(x: String): String {
        // code here

        val result = x.replace("\\s".toRegex(), "")

        return result


    }


data class Tiendas(
    
    var id_tienda: Int,
    var nombre_tienda: String,
    var direccion_tienda: String
   )

class ConnectionBuilder {
    // TODO Auto-generated catch block
    lateinit var connection: Connection
    private val jdbcURL = "jdbc:h2:mem:default"
    private val jdbcUsername = ""
    private val jdbcPassword = ""

    init {
        try {
            connection = DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword)
        } catch (e: SQLException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        } catch (e: ClassNotFoundException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }
    }


}

class tiendasDAO(private val c: Connection){
    // En el companion object creamos todas las constantes.
    // Las constante definirán las plantillas de las sentencias que necesitamos para construir
    // los selects, inserts, deletes, updates.

    // En aquellos casos en donde necesitemos insertar un parametro, pondremos un ?
    // Luego lo sistituiremos llamando a métodos setX, donde X será (Int, String, ...)
    // dependiendo del tiempo de dato que corresponda.
    companion object {
        private const val SCHEMA = "default"
        private const val TIENDAS = "TIENDAS"

        private const val TRUNCATE_TABLE_TIENDAS_SQL = "TRUNCATE TABLE TIENDAS"

        private const val CREATE_TABLE_TIENDAS_SQL =
            "CREATE TABLE TIENDAS (ID_TIENDA NUMBER(10,0) CONSTRAINT PK_ID_TIENDA PRIMARY KEY, NOMBRE_TIENDA VARCHAR2(40), DIRECCION_TIENDA VARCHAR2(200) );"

        private const val INSERT_TIENDAS_SQL = "INSERT INTO TIENDAS" + "  (ID_TIENDA, NOMBRE_TIENDA, DIRECCION_TIENDA) VALUES " + " (?, ?, ?);"

        private const val SELECT_TIENDA_BY_ID = "select ID_TIENDA,NOMBRE_TIENDA,DIRECCION_TIENDA from TIENDAS where ID_TIENDA = ?"

        private const val SELECT_ALL_TIENDAS = "select * from TIENDAS"

        private const val DELETE_TIENDAS_SQL = "delete from TIENDAS where id = ?;"
      }

    fun prepareTable() {
        val metaData = c.metaData
        val rs = metaData.getTables(null, SCHEMA, TIENDAS, null)

        if (!rs.next()) createTable() else truncateTable()
    }

    private fun truncateTable() {
        println(TRUNCATE_TABLE_TIENDAS_SQL)
        // try-with-resource statement will auto close the connection.
        try {
            c.createStatement().use { st ->
                st.execute(TRUNCATE_TABLE_TIENDAS_SQL)
            }
            //Commit the change to the database
            c.commit()
        } catch (e: SQLException) {
            printSQLException(e)
        }
    }

    private fun createTable() {
        println(CREATE_TABLE_TIENDAS_SQL)
        // try-with-resource statement will auto close the connection.
        try {
            //Get and instance of statemnt from the connection and use
            //the execute() method to execute the sql
            c.createStatement().use { st ->
                //SQL statement to create a table
                st.execute(CREATE_TABLE_TIENDAS_SQL)
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



    fun insertTienda(tienda: Tiendas) {
        println(INSERT_TIENDAS_SQL)
        // try-with-resource statement will auto close the connection.


        try {
            c.prepareStatement(INSERT_TIENDAS_SQL).use { st ->
                st.setInt(1, tienda.id_tienda)
                st.setString(2, tienda.nombre_tienda)
                st.setString(3, tienda.direccion_tienda)

                println(st)
                st.executeUpdate()
            }
            //Commit the change to the database
            c.commit()
        } catch (e: SQLException) {
            printSQLException(e)
        }
    }

    fun selectById(id: Int): Tiendas? {
        var miTienda: Tiendas? = null
        // Step 1: Establishing a Connection
        try {
            c.prepareStatement(SELECT_TIENDA_BY_ID).use { st ->
                st.setInt(1, id)
                println(st)
                // Step 3: Execute the query or update query
                val rs = st.executeQuery()



                // Step 4: Process the ResultSet object.
                while (rs.next()) {
                    
                    val nombre_tienda = rs.getString("nombre_tienda")
                    val direccion_tienda = rs.getString("direccion_tienda")
                    miTienda = Tiendas(id,nombre_tienda,direccion_tienda )
                }
            }

        } catch (e: SQLException) {
            printSQLException(e)
        }
        return miTienda
    }

    fun deleteById(id: Int): Boolean {
        var rowDeleted = false

        try {
            c.prepareStatement(DELETE_TIENDAS_SQL).use { st ->
                st.setInt(1, id)
                rowDeleted = st.executeUpdate() > 0
            }
            //Commit the change to the database
            c.commit()
        } catch (e: SQLException) {
            printSQLException(e)
        }
        return rowDeleted
    }

    fun selectAll(): List<Tiendas> {

        // using try-with-resources to avoid closing resources (boiler plate code)
        val tiendas: MutableList<Tiendas> = ArrayList()
        // Step 1: Establishing a Connection
        try {
            c.prepareStatement(SELECT_ALL_TIENDAS).use { st ->
                println(st)
                // Step 3: Execute the query or update query
                val rs = st.executeQuery()

                // Step 4: Process the ResultSet object.
                while (rs.next()) {
                    val id_tienda = rs.getInt("id_tienda")
                    val nombre_tienda = rs.getString("nombre_tienda")
                    val direccion_tienda = rs.getString("direccion_tienda")
                    tiendas.add(Tiendas(id_tienda,nombre_tienda,direccion_tienda))
                }
            }

        } catch (e: SQLException) {
            printSQLException(e)
        }
        return tiendas
    }}


fun main() {

    noSpace("AA AA")

    getCount("hola")
}
