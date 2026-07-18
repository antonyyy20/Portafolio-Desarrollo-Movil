fun main() {
    var nombre = ""
    var apellido = ""
    var edad = 0
    var genero = ""
    var pasajeroRegistrado = false
    val precioBoleto = 20.0

    while (true) {
        println("\n===== MENÚ PRINCIPAL =====")
        println("1. Registrar pasajero")
        println("2. Realizar compra del boleto")
        println("3. Salir")
        print("Seleccione una opción: ")

        when (readLine()?.trim()) {
            "1" -> {
                print("Ingrese el nombre: ")
                nombre = readLine()?.trim() ?: ""
                print("Ingrese el apellido: ")
                apellido = readLine()?.trim() ?: ""
                print("Ingrese la edad: ")
                edad = readLine()?.trim()?.toIntOrNull() ?: 0
                print("Ingrese el género (M/F): ")
                genero = readLine()?.trim()?.uppercase() ?: ""
                pasajeroRegistrado = true
                println("\nPasajero registrado: ${nombre} ${apellido}")
            }

            "2" -> {
                if (!pasajeroRegistrado) {
                    println("Debe registrar un pasajero primero.")
                } else {
                    // Calcular descuento
                    val descuento = when {
                        edad < 12 -> 0.05
                        genero == "F" && edad > 57 -> 0.15
                        genero == "M" && edad > 62 -> 0.15
                        else -> 0.0
                    }

                    val costoFinal = precioBoleto - (precioBoleto * descuento)

                    print("Tipo de pago (visa/clave/cheque/efectivo/transferencia): ")
                    val tipoPago = readLine()?.trim() ?: "efectivo"

                    // Imprimir recibo
                    println("\n------- TRANSPORTE UTP S.A. -------")
                    println("       RUC: 01-2531-4507")
                    println("       TERMINAL PRINCIPAL")
                    println("-----------------------------------")
                    println("CLIENTE : ${nombre} ${apellido}")
                    println("EDAD    : ${edad} años")
                    if (descuento > 0) {
                        println("DESCUENTO: ${(descuento * 100).toInt()}%")
                    }
                    println("COSTO   : B/ ${"%.2f".format(costoFinal)}")
                    println("PAGO    : ${tipoPago.capitalize()}")
                    println("-----------------------------------")
                }
            }

            "3" -> {
                println("Gracias. ¡Hasta pronto!")
                return
            }

            else -> println("Opción inválida. Intente de nuevo.")
        }
    }
}