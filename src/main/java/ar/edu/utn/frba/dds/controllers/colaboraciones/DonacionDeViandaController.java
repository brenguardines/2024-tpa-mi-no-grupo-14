package ar.edu.utn.frba.dds.controllers.colaboraciones;

import ar.edu.utn.frba.dds.config.ServiceLocator;
import ar.edu.utn.frba.dds.controllers.ICrudViewsHandler;
import ar.edu.utn.frba.dds.models.entities.colaboradores.PersonaHumana;
import ar.edu.utn.frba.dds.models.entities.colaboradores.PersonaJuridica;
import ar.edu.utn.frba.dds.models.entities.contribuciones.DonacionDeVianda;
import ar.edu.utn.frba.dds.models.entities.heladera.Heladera;
import ar.edu.utn.frba.dds.models.entities.vianda.Comida;
import ar.edu.utn.frba.dds.models.entities.vianda.Vianda;
import ar.edu.utn.frba.dds.models.repositories.implementaciones.DistribucionDeViandaRepository;
import ar.edu.utn.frba.dds.models.repositories.implementaciones.DonacionDeViandaRepository;
import ar.edu.utn.frba.dds.models.repositories.implementaciones.HeladeraRepository;
import ar.edu.utn.frba.dds.models.repositories.implementaciones.PersonaHumanaRepository;
import ar.edu.utn.frba.dds.models.repositories.implementaciones.PersonaJuridicaRepository;
import io.javalin.http.Context;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class DonacionDeViandaController implements ICrudViewsHandler {

    private final DonacionDeViandaRepository donacionDeViandaRepository;

    public DonacionDeViandaController(DonacionDeViandaRepository donacionDeViandaRepository) {
        this.donacionDeViandaRepository = donacionDeViandaRepository;
    }

    @Override
    public void index(Context context) {
        // Lista todas las donaciones y las envía a la vista
        Map<String, Object> model = new HashMap<>();
        model.put("donaciones", donacionDeViandaRepository.buscarTodos());

        context.render("colaboraciones/listaDonaciones.hbs", model);
    }

    @Override
    public void show(Context context) {
        Long id = Long.parseLong(context.pathParam("id"));
        Optional<DonacionDeVianda> donacion = donacionDeViandaRepository.buscarPorId(id);

        if (donacion.isPresent()) {
            Map<String, Object> model = new HashMap<>();
            model.put("donacion", donacion.get());
            context.render("colaboraciones/verDonacion.hbs", model);
        } else {
            context.status(404).result("Donación no encontrada");
        }
    }

    @Override
    public void create(Context context) {
        Map<String, Object> model = new HashMap<>();
        model.put("esColaboradorHumano", true);
        String usuario = context.sessionAttribute("usuario");
        if (usuario == null) {
            System.out.println("No hay sesión activa, redirigiendo a login");
            context.redirect("/login");
        } else {
            System.out.println("Entrando a la vista de donar viandas");
            context.render("/colaboraciones/donarViandas.hbs", model);
        }
    }


    public void save(Context context) {
        PersonaHumanaRepository personaHumanaRepository = ServiceLocator.instanceOf(PersonaHumanaRepository.class);
        DonacionDeViandaRepository donacionDeViandaRepository = ServiceLocator.instanceOf(DonacionDeViandaRepository.class);

        DonacionDeVianda donacionDeVianda = new DonacionDeVianda();

        Long idColaborador = Long.parseLong(context.sessionAttribute("idColaboradorHumano").toString());
        PersonaHumana colaborador = personaHumanaRepository.buscarPorId(idColaborador)
            .orElseThrow(() -> new IllegalArgumentException("Colaborador humano no encontrado"));
        donacionDeVianda.setColaborador(colaborador);

        Vianda vianda = new Vianda();
        Comida comida = new Comida();
        comida.setNombre(context.formParam("descripcion"));
        vianda.setComida(comida);

        // Parsear la fecha de caducidad a LocalDate
        String fechaCaducidadStr = context.formParam("fechaCaducidad");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        vianda.setFechaDeCaducidad(LocalDate.parse(fechaCaducidadStr, formatter).atStartOfDay());

        vianda.setPeso(Integer.parseInt(context.formParam("peso")));
        vianda.setCalorias(Integer.parseInt(context.formParam("calorias")));

        // Obtener la heladera seleccionada
        String tipoHeladera = context.formParam("tipoHeladera");
        if (tipoHeladera != null) {
            Heladera heladera = obtenerHeladera(tipoHeladera);
            vianda.setHeladeraEnLaQueSeEncuentra(heladera);
        }

        donacionDeVianda.setVianda(vianda);
        this.donacionDeViandaRepository.guardar(donacionDeVianda);
        System.out.println("Redirigiendo a /colaboracionExitosa");
        context.redirect("/colaboracionExitosa");
    }



    private Heladera obtenerHeladera(String tipoHeladera) {
        return null;
    }




    @Override
    public void edit(Context context) {
        Long id = Long.parseLong(context.pathParam("id"));
        Optional<DonacionDeVianda> donacion = donacionDeViandaRepository.buscarPorId(id);

        if (donacion.isPresent()) {
            Map<String, Object> model = new HashMap<>();
            model.put("donacion", donacion.get());
            context.render("colaboraciones/editarDonacion.hbs", model);
        } else {
            context.status(404).result("Donación no encontrada");
        }
    }

    @Override
    public void update(Context context) {

    }

    @Override
    public void delete(Context context) {
        Long id = Long.parseLong(context.pathParam("id"));
        Optional<DonacionDeVianda> donacionOpt = donacionDeViandaRepository.buscarPorId(id);

        if (donacionOpt.isPresent()) {
            donacionDeViandaRepository.eliminar(donacionOpt.get());
            context.redirect("colaboraciones");
        } else {
            context.status(404).result("Donación no encontrada");
        }
    }
}

