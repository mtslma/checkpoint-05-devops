package br.com.fiap.checkpoint05devops.dto;

import java.util.List;

public record PedidoRequestDTO(
        String cliente,
        Double valorTotal,
        List<ItemRequestDTO> itens
) {}

