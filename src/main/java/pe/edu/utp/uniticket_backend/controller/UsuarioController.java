package pe.edu.utp.uniticket_backend.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.utp.uniticket_backend.dto.UsuarioCreateDTO;
import pe.edu.utp.uniticket_backend.dto.UsuarioDTO;
import pe.edu.utp.uniticket_backend.dto.UsuarioUpdateDTO;
import pe.edu.utp.uniticket_backend.service.UsuarioService;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {
    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> listarUsuarios(
            @RequestParam(name = "estado", required = false) String estado) {
        List<UsuarioDTO> usuarios = usuarioService.listarUsuarios(estado);
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<UsuarioDTO>> buscarPorNombre(
            @RequestParam String nombre) {
        List<UsuarioDTO> usuarios = usuarioService.buscarPorNombre(nombre);
        return ResponseEntity.ok(usuarios);
    }

    @PostMapping
    public ResponseEntity<UsuarioDTO> crearUsuario(@Valid @RequestBody UsuarioCreateDTO datos) {
        UsuarioDTO usuarioCreado = usuarioService.crearUsuario(datos);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioCreado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDTO> actualizarUsuario(
            @PathVariable("id") Long id,
            @Valid @RequestBody UsuarioUpdateDTO datos) {
        UsuarioDTO usuarioActualizado = usuarioService.actualizarUsuario(id, datos);
        return ResponseEntity.ok(usuarioActualizado);
    }
}
