package br.com.fiap.checkpoint05devops.service;

import br.com.fiap.checkpoint05devops.dto.PedidoRequestDTO;
import br.com.fiap.checkpoint05devops.model.ItemPedido;
import br.com.fiap.checkpoint05devops.model.Pedido;
import br.com.fiap.checkpoint05devops.repository.PedidoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository repository;

    public List<Pedido> listarTodos() {
        return repository.findAll();
    }

    @Transactional
    public Pedido salvar(PedidoRequestDTO dto) {
        Pedido pedido = new Pedido();
        return copiarDtoParaEntidade(dto, pedido);
    }

    @Transactional
    public Pedido atualizar(Integer id, PedidoRequestDTO dto) {
        // Busca o pedido existente ou lança erro se não achar (para o Controller retornar 404)
        Pedido pedidoExistente = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado com ID: " + id));

        return copiarDtoParaEntidade(dto, pedidoExistente);
    }

    @Transactional
    public void deletar(Integer id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Não é possível deletar: Pedido não encontrado");
        }
        repository.deleteById(id);
    }

    private Pedido copiarDtoParaEntidade(PedidoRequestDTO dto, Pedido pedido) {
        pedido.setCliente(dto.cliente());
        pedido.setValorTotal(dto.valorTotal());

        // O SEGREDO DO ATUALIZAR:
        // 1. Limpa a lista atual de itens (Orphan Removal deve estar ativo na Entity Pedido)
        if (pedido.getItens() != null) {
            pedido.getItens().clear();
        }

        List<ItemPedido> novosItens = dto.itens().stream().map(itemDto -> {
            ItemPedido item = new ItemPedido();
            item.setSabor(itemDto.sabor());
            item.setQuantidade(itemDto.quantidade());
            item.setPedido(pedido);
            return item;
        }).collect(Collectors.toList());

        if (pedido.getItens() == null) {
            pedido.setItens(novosItens);
        } else {
            pedido.getItens().addAll(novosItens);
        }

        return repository.save(pedido);
    }
}