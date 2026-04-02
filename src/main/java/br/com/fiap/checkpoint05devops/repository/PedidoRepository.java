package br.com.fiap.checkpoint05devops.repository;

import br.com.fiap.checkpoint05devops.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidoRepository extends JpaRepository<Pedido, Integer> {
}
