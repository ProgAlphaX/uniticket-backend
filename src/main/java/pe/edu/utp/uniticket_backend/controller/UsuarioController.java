package pe.edu.utp.uniticket_backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.utp.uniticket_backend.dto.UsuarioDTO;
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
            @RequestParam(required = false) String estado) {
        List<UsuarioDTO> usuarios = usuarioService.listarUsuarios(estado);
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<UsuarioDTO>> buscarPorNombre(
            @RequestParam String nombre) {
        List<UsuarioDTO> usuarios = usuarioService.buscarPorNombre(nombre);
        return ResponseEntity.ok(usuarios);
    }
}