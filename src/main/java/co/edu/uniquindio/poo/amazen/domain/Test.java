package co.edu.uniquindio.poo.amazen.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Scanner;

import co.edu.uniquindio.poo.amazen.domain.common.AppContext;
import co.edu.uniquindio.poo.amazen.domain.envio.Envio;
import co.edu.uniquindio.poo.amazen.domain.envio.Paquete;
import co.edu.uniquindio.poo.amazen.domain.envio.EstadoEnvio;
import co.edu.uniquindio.poo.amazen.domain.pago.PagoRegistro;
import co.edu.uniquindio.poo.amazen.domain.reparto.Repartidor;
import co.edu.uniquindio.poo.amazen.domain.tarifa.TarifaBase;
import co.edu.uniquindio.poo.amazen.domain.usuario.Direccion;
import co.edu.uniquindio.poo.amazen.domain.usuario.Usuario;
import co.edu.uniquindio.poo.amazen.domain.dto.TarifaDTO;

public class Test {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        AppContext ctx = AppContext.getInstance();

        // 1) Tarifa base (puedes cambiar valores aquí)
        TarifaBase tarifa = new TarifaBase(
                "T001", "Tarifa base nacional",
                BigDecimal.valueOf(1000),   // $/km
                BigDecimal.valueOf(500),    // $/kg
                BigDecimal.valueOf(200),    // $/1000 cm3
                BigDecimal.valueOf(0.19),   // IVA 19%
                true
        );
        ctx.addTarifa(tarifa);

        // 2) Captura de datos
        System.out.println("=== CREAR ENVÍO (INTERACTIVO) ===");

        System.out.print("ID cliente: ");
        String clienteId = sc.nextLine().trim();

        System.out.print("Nombre cliente: ");
        String clienteNombre = sc.nextLine().trim();

        System.out.print("Email cliente: ");
        String clienteEmail = sc.nextLine().trim();

        System.out.print("Origen (alias): ");
        String aliasOrigen = sc.nextLine().trim();
        System.out.print("Origen (ciudad): ");
        String ciudadOrigen = sc.nextLine().trim();
        System.out.print("Origen (barrio): ");
        String barrioOrigen = sc.nextLine().trim();
        System.out.print("Origen (calle): ");
        String calleOrigen = sc.nextLine().trim();
        System.out.print("Origen (referencia): ");
        String refOrigen = sc.nextLine().trim();

        System.out.print("Destino (alias): ");
        String aliasDestino = sc.nextLine().trim();
        System.out.print("Destino (ciudad): ");
        String ciudadDestino = sc.nextLine().trim();
        System.out.print("Destino (barrio): ");
        String barrioDestino = sc.nextLine().trim();
        System.out.print("Destino (calle): ");
        String calleDestino = sc.nextLine().trim();
        System.out.print("Destino (referencia): ");
        String refDestino = sc.nextLine().trim();

        System.out.print("Distancia (km): ");
        double distanciaKm = readDouble(sc);

        System.out.print("Peso total (kg): ");
        double pesoKg = readDouble(sc);

        System.out.print("Volumen total (cm3): ");
        double volumenCm3 = readDouble(sc);

        System.out.print("Prioridad [Normal/Alta]: ");
        String prioridad = sc.nextLine().trim();

        // 3) Construcción de entidades mínimas
        Usuario usuario = new Usuario(clienteId, clienteNombre, clienteEmail);
        Direccion origen = new Direccion("ORG-" + clienteId, aliasOrigen, ciudadOrigen, barrioOrigen, calleOrigen, refOrigen);
        Direccion destino = new Direccion("DST-" + clienteId, aliasDestino, ciudadDestino, barrioDestino, calleDestino, refDestino);
        usuario.addDireccion(origen);
        usuario.addDireccion(destino);

        Envio envio = new Envio("E-" + System.currentTimeMillis(), usuario, origen, destino);
        envio.addPaquete(new Paquete("PK-1", pesoKg, volumenCm3));
        envio.setPrioridad(prioridad);
        envio.setEstado(EstadoEnvio.CREADO);

        // 4) Cálculo de tarifa (versión simple, sin extras)
        TarifaDTO cotizacion = calcularTarifaSimple(tarifa, distanciaKm, pesoKg, volumenCm3, prioridad);
        envio.setDetalleTarifa(desgloseTexto(cotizacion, distanciaKm, pesoKg, volumenCm3));
        // (Si quisieras simular pago)
        PagoRegistro pago = new PagoRegistro(
                "PG-" + System.currentTimeMillis(),
                envio.getId(),
                LocalDateTime.now(),
                cotizacion.total,
                "APROBADO", "REF-DEMO", "Efectivo"
        );
        envio.setPago(pago);

        // 5) Registrar en AppContext (más un repartidor demo)
        ctx.addUsuario(usuario);
        ctx.addEnvio(envio);
        ctx.addRepartidor(new Repartidor("R001", "Repartidor Demo", true, ciudadOrigen));

        // 6) Salida (resumen)
        System.out.println("\n=== RESUMEN DEL ENVÍO ===");
        System.out.println("ID Envío:      " + envio.getId());
        System.out.println("Cliente:       " + usuario.getNombre() + " (" + usuario.getEmail() + ")");
        System.out.println("Origen:        " + origen);
        System.out.println("Destino:       " + destino);
        System.out.println("Distancia:     " + distanciaKm + " km");
        System.out.println("Peso:          " + pesoKg + " kg");
        System.out.println("Volumen:       " + volumenCm3 + " cm3");
        System.out.println("Prioridad:     " + prioridad);
        System.out.println("----- CÁLCULO TARIFA -----");
        System.out.println(envio.getDetalleTarifa());
        System.out.println("Subtotal:      " + money(cotizacion.subtotal));
        System.out.println("Extras:        " + money(cotizacion.extras));
        System.out.println("IVA:           " + money(cotizacion.iva));
        System.out.println("TOTAL:         " + money(cotizacion.total));
        System.out.println("--------------------------");
        System.out.println("Pago:          " + envio.getPago().getEstado() + " (" + envio.getPago().getReferenciaExterna() + ")");
        System.out.println("\nUsuarios en contexto: " + ctx.getUsuarios().size());
        System.out.println("Envíos en contexto:   " + ctx.getEnvios().size());
        System.out.println("Tarifas en contexto:  " + ctx.getTarifas().size());

        sc.close();
    }

    // --- Cálculo simple de tarifa (sin extras/Decorators aún) ---
    private static TarifaDTO calcularTarifaSimple(TarifaBase t, double km, double kg, double cm3, String prioridad) {
        TarifaDTO dto = new TarifaDTO();
        if (!t.isActiva()) {
            dto.desglose = "Tarifa inactiva";
            return dto;
        }
        BigDecimal dist = t.getPrecioPorKm().multiply(BigDecimal.valueOf(km));
        BigDecimal peso = t.getPrecioPorKg().multiply(BigDecimal.valueOf(kg));
        BigDecimal vol  = t.getPrecioPorVolumen().multiply(BigDecimal.valueOf(cm3 / 1000.0)); // por 1000 cm3

        dto.subtotal = dist.add(peso).add(vol);

        // Recargo simple por prioridad (10% si "Alta")
        if ("ALTA".equalsIgnoreCase(prioridad)) {
            BigDecimal recargo = dto.subtotal.multiply(BigDecimal.valueOf(0.10));
            dto.extras = dto.extras.add(recargo);
        }

        BigDecimal base = dto.subtotal.add(dto.extras);
        dto.iva   = base.multiply(t.getIva()).setScale(2, RoundingMode.HALF_UP);
        dto.total = base.add(dto.iva).setScale(2, RoundingMode.HALF_UP);

        dto.desglose = String.format(
                "dist(%.2f km)*%s + peso(%.2f kg)*%s + vol(%.0f cm3→x1000)*%s + extras(%s) + IVA(%s)",
                km, t.getPrecioPorKm().toPlainString(),
                kg, t.getPrecioPorKg().toPlainString(),
                cm3, t.getPrecioPorVolumen().toPlainString(),
                dto.extras.toPlainString(),
                t.getIva().toPlainString()
        );
        return dto;
    }

    private static String desgloseTexto(TarifaDTO dto, double km, double kg, double cm3) {
        return "Desglose: " + dto.desglose;
    }

    private static String money(BigDecimal v) {
        return "$" + v.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    private static double readDouble(Scanner sc) {
        while (true) {
            String s = sc.nextLine().trim().replace(",", ".");
            try {
                return Double.parseDouble(s);
            } catch (NumberFormatException e) {
                System.out.print("Valor inválido, inténtalo de nuevo: ");
            }
        }
    }
}
