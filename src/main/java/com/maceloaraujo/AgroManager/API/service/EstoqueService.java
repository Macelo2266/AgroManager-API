package com.maceloaraujo.AgroManager.API.service;

import com.maceloaraujo.AgroManager.API.dto.request.MovimentacaoEstoqueRequest;
import com.maceloaraujo.AgroManager.API.dto.request.ProdutoRequest;
import com.maceloaraujo.AgroManager.API.dto.response.EstoqueResponse;
import com.maceloaraujo.AgroManager.API.exception.RecursoNaoEncontradoException;
import com.maceloaraujo.AgroManager.API.exception.RegraDeNegocioException;
import com.maceloaraujo.AgroManager.API.model.TipoProduto;
import com.maceloaraujo.AgroManager.API.model.entity.*;
import com.maceloaraujo.AgroManager.API.repository.EstoqueRepository;
import com.maceloaraujo.AgroManager.API.repository.MovimentacaoEstoqueRepository;
import com.maceloaraujo.AgroManager.API.repository.ProdutoRepository;
import com.maceloaraujo.AgroManager.API.repository.PropriedadeRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import com.maceloaraujo.AgroManager.API.model.TipoMovimentacao;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class EstoqueService {

    private final ProdutoRepository produtoRepository;
    private final EstoqueRepository estoqueRepository;
    private final MovimentacaoEstoqueRepository movimentacaoRepository;
    private final PropriedadeRepository propriedadeRepository;

    /**
     * Cadastra um novo produto e inicializa seu estoque zerado.
     */
    @Transactional
    public EstoqueResponse cadastrarProduto(ProdutoRequest request) {
        Propriedade propriedade = propriedadeRepository.findById(request.getPropriedadeId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Propriedade", request.getPropriedadeId()));

        if (produtoRepository.existsByNomeAndPropriedadeId(request.getNome(), request.getPropriedadeId())) {
            throw new RegraDeNegocioException(
                    "Produto '" + request.getNome() + "' já cadastrado nesta propriedade"
            );
        }

        Produto produto = Produto.builder()
                .nome(request.getNome())
                .descricao(request.getDescricao())
                .tipo(request.getTipo())
                .unidadeMedida(request.getUnidadeMedida())
                .estoqueMinimo(request.getEstoqueMinimo())
                .propriedade(propriedade)
                .build();

        produto = produtoRepository.save(produto);

        // Inicializa estoque zerado para o produto
        Estoque estoque = Estoque.builder()
                .produto(produto)
                .quantidade(BigDecimal.ZERO)
                .custoMedio(BigDecimal.ZERO)
                .valorTotalEstoque(BigDecimal.ZERO)
                .build();

        estoqueRepository.save(estoque);

        log.info("Produto cadastrado: {} ({})", produto.getNome(), produto.getTipo());

        return toEstoqueResponse(produto, estoque);
    }

    /**
     * Registra movimentação (entrada ou saída) no estoque.
     *
     * FLUXO:
     * 1. Busca o produto e o estoque
     * 2. Aplica a movimentação na entidade Estoque (regras de negócio ficam no domínio)
     * 3. Salva o histórico de movimentação
     * 4. Verifica alerta de estoque baixo
     */
    @Transactional
    public EstoqueResponse movimentar(MovimentacaoEstoqueRequest request) {
        Produto produto = produtoRepository.findById(request.getProdutoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto", request.getProdutoId()));

        Estoque estoque = estoqueRepository.findByProdutoId(request.getProdutoId())
                .orElseThrow(() -> new RegraDeNegocioException(
                        "Estoque não inicializado para o produto: " + produto.getNome()
                ));

        // Lógica de negócio fica na entidade (Domain Logic)
        switch (request.getTipo()) {
            case ENTRADA -> {
                BigDecimal custo = request.getCustoUnitario() != null ?
                        request.getCustoUnitario() : BigDecimal.ZERO;
                estoque.registrarEntrada(request.getQuantidade(), custo);
            }
            case SAIDA -> {
                // IllegalStateException é capturada pelo GlobalExceptionHandler → HTTP 409
                estoque.registrarSaida(request.getQuantidade());
            }
        }

        estoqueRepository.save(estoque);

        // Salva o histórico auditável
        Usuario usuarioLogado = getUsuarioLogado();
        MovimentacaoEstoque movimentacao = MovimentacaoEstoque.builder()
                .produto(produto)
                .tipo(request.getTipo())
                .quantidade(request.getQuantidade())
                .custoUnitario(request.getCustoUnitario())
                .dataMovimentacao(request.getDataMovimentacao())
                .motivo(request.getMotivo())
                .documentoReferencia(request.getDocumentoReferencia())
                .responsavel(usuarioLogado)
                .build();

        movimentacaoRepository.save(movimentacao);

        // Log de alerta se estoque baixo
        if (estoque.isEstoqueBaixo()) {
            log.warn("ALERTA: Estoque baixo para produto '{}'. Quantidade atual: {}. Mínimo: {}",
                    produto.getNome(), estoque.getQuantidade(), produto.getEstoqueMinimo());
        }

        return toEstoqueResponse(produto, estoque);
    }

    @Transactional(readOnly = true)
    public Page<EstoqueResponse> listarPorPropriedade(Long propriedadeId, TipoProduto tipo, Pageable pageable) {
        Page<Produto> produtos;
        if (tipo != null) {
            produtos = produtoRepository.findByPropriedadeIdAndTipoAndAtivoTrue(propriedadeId, tipo, pageable);
        } else {
            produtos = produtoRepository.findByPropriedadeIdAndAtivoTrue(propriedadeId, pageable);
        }

        return produtos.map(p -> {
            Estoque estoque = p.getEstoque();
            return toEstoqueResponse(p, estoque);
        });
    }

    @Transactional(readOnly = true)
    public EstoqueResponse buscarPorProduto(Long produtoId) {
        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto", produtoId));
        Estoque estoque = estoqueRepository.findByProdutoId(produtoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Estoque do produto", produtoId));
        return toEstoqueResponse(produto, estoque);
    }

    @Transactional(readOnly = true)
    public List<EstoqueResponse> verificarEstoqueBaixo(Long propriedadeId) {
        return produtoRepository.findProdutosComEstoqueBaixo(propriedadeId)
                .stream()
                .map(p -> toEstoqueResponse(p, p.getEstoque()))
                .collect(Collectors.toList());
    }

    private EstoqueResponse toEstoqueResponse(Produto produto, Estoque estoque) {
        return EstoqueResponse.builder()
                .produtoId(produto.getId())
                .nomeProduto(produto.getNome())
                .tipoProduto(produto.getTipo())
                .unidadeMedida(produto.getUnidadeMedida())
                .quantidade(estoque != null ? estoque.getQuantidade() : BigDecimal.ZERO)
                .custoMedio(estoque != null ? estoque.getCustoMedio() : BigDecimal.ZERO)
                .valorTotalEstoque(estoque != null ? estoque.getValorTotalEstoque() : BigDecimal.ZERO)
                .estoqueMinimo(produto.getEstoqueMinimo())
                .estoqueBaixo(estoque != null && estoque.isEstoqueBaixo())
                .build();
    }

    private Usuario getUsuarioLogado() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof Usuario usuario) {
            return usuario;
        }
        return null;
    }
}
