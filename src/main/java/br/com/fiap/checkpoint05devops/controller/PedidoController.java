package br.com.fiap.checkpoint05devops.controller;

import br.com.fiap.checkpoint05devops.dto.PedidoRequestDTO;
import br.com.fiap.checkpoint05devops.model.Pedido;
import br.com.fiap.checkpoint05devops.service.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/esfihas")
public class PedidoController {

    @Autowired
    private PedidoService service;

    @GetMapping
    public ResponseEntity<List<Pedido>> get() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Pedido post(@RequestBody PedidoRequestDTO dto) {
        return service.salvar(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Pedido> put(@PathVariable Integer id, @RequestBody PedidoRequestDTO dto) {
        try {
            Pedido atualizado = service.atualizar(id, dto);
            return ResponseEntity.ok(atualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        try {
            service.deletar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}