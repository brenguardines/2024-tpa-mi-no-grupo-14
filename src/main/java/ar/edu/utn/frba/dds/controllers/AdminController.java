package ar.edu.utn.frba.dds.controllers;
import ar.edu.utn.frba.dds.models.entities.cargaDeUsuarios.CargaDeUsuarios;
import ar.edu.utn.frba.dds.models.entities.colaboradores.PersonaHumana;
import ar.edu.utn.frba.dds.models.repositories.implementaciones.PersonaHumanaRepository;
import ar.edu.utn.frba.dds.models.repositories.implementaciones.UsuarioRepository;
import ar.edu.utn.frba.dds.models.usuario.Contrasenia;
import ar.edu.utn.frba.dds.models.usuario.RolDeUsuario;
import ar.edu.utn.frba.dds.models.usuario.Usuario;
import io.javalin.http.Context;
import io.javalin.http.UploadedFile;

import javax.mail.MessagingException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;

public class AdminController {
    CargaDeUsuarios cargaDeUsuarios;
    UsuarioRepository usuarioRepository;
    PersonaHumanaRepository personaHumanaRepository;

    public AdminController(CargaDeUsuarios cargaDeUsuarios, UsuarioRepository usuarioRepository, PersonaHumanaRepository personaHumanaRepository) {
        this.cargaDeUsuarios = cargaDeUsuarios;
        this.usuarioRepository = usuarioRepository;
        this.personaHumanaRepository = personaHumanaRepository;
    }

    public void cargarCSV(Context context) throws IOException, MessagingException {
        UploadedFile uploadedFile = context.uploadedFile("archivoCSV");
        List<PersonaHumana> nuevosColaboradores;

        if (uploadedFile != null) {
            // Guardar el archivo en el sistema de archivos
            Path uploadPath = Paths.get(
                    "../2024-tpa-mi-no-grupo-14/src/main/java/ar/edu/utn/frba/dds/models/entities/cargaDeUsuarios/" + uploadedFile.filename());
            //Files.createDirectories(uploadPath.getParent()); // Crear directorio si no existe
            Files.copy(uploadedFile.content(), uploadPath, StandardCopyOption.REPLACE_EXISTING);
            nuevosColaboradores = this.cargaDeUsuarios.getColaboradores(uploadPath.toAbsolutePath().toString());
            for (PersonaHumana persona : nuevosColaboradores) {
                this.personaHumanaRepository.guardar(persona);
            }
            CargaDeUsuarios.enviarCorreos(nuevosColaboradores);
            context.redirect("/menuAdmin");


        } else {
            context.result("No se ha subido ningún archivo.");
        }
    }
}
