
fun main() {
    val c = ConnectionBuilder()


    if (c.connection.isValid(10)) {
        println("Conexion valida")

        c.connection.use {
            val tiendas = tiendasDAO(c.connection)
            val inventarios = inventariosDAO(c.connection)

            // PREPARAMOS Y CREAMOS LA TABLA INVENTARIOS Y LA TABLA TIENDAS , TAMBIEN CREAMOS UN INDICE
            tiendas.prepareTable()
            inventarios.prepareTable()
            inventarios.createIndice()

            // CREO E INSERTO 5 ARTICULOS Y 5 TIENDAS PARA RELLENAR LAS TABLAS
            var articulo1 = Inventarios(1, "CD-DVD", "900 MB DE ESPACIO", 35.50, 5)
            var articulo2 = Inventarios(2, "USB-HP", "32GB, USB 3.0", 155.90, 4)
            var articulo3 = Inventarios(3, "Laptop SONY", "4GB de RAM,300 HDD , i5 2.6 GHz", 13410.07, 3)
            var articulo4 = Inventarios(4, "Mouse Optico", "700 DPI", 104.40, 2)
            var articulo5 = Inventarios(5, "Disco Duro", "200 TB, HDD, USB 3.0", 2300.00, 1)

            tiendas.insertTienda(Tiendas(5, "decathlon", "avenida a"))
            tiendas.insertTienda(Tiendas(4, "el corte ingles", "avenida b"))
            tiendas.insertTienda(Tiendas(3, "orange", "avenida cocodrilo"))
            tiendas.insertTienda(Tiendas(2, "lefties", "avenida c"))
            tiendas.insertTienda(Tiendas(1, "ZARA", "avenida dedo"))

            inventarios.insertInventario(articulo1)
            inventarios.insertInventario(articulo2)
            inventarios.insertInventario(articulo3)
            inventarios.insertInventario(articulo4)
            inventarios.insertInventario(articulo5)

            //UTILIZAMOS LA FUNCION DE MI SELECT PARA SABER QUE FILA TIENE UN PRECIO MAYOR A 2000 PESOS
            println(inventarios.miSelect())

            // UTILIZAMOS LA FUNCION DE MIUPDATE PARA ACTUALIZAR EL PRECIO DE LOS PRODUCTOS QUE LO REQUIERAN
            (inventarios.miUpdate(articulo3))
            (inventarios.miUpdate(articulo5))

            // IMPRIMO TODAS LAS TABLAS PARA COMPROBAR LA ACTUALIZACIÓN
            println(inventarios.selectAll())

            // MOSTRAR INVENTARIOS POR ID_TIENDA
            println(inventarios.selectByIdTienda(3))

            // MOSTRAR LAS TIENDAS
            println(tiendas.selectAll())
        }


    }
}

