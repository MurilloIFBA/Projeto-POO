package livraria.model;

import java.time.LocalDateTime;

public class Venda {
    private int id;
    private Livro livro;
    private Cliente cliente;
    private int quantidade;
    private LocalDateTime dataVenda;

    public Venda(int id, Livro livro, Cliente cliente, int quantidade, LocalDateTime dataVenda) {
        this.id         = id;
        this.livro      = livro;
        this.cliente    = cliente;
        this.quantidade = quantidade;
        this.dataVenda  = dataVenda;
    }

    public Venda(Livro livro, Cliente cliente, int quantidade) {
        this(0, livro, cliente, quantidade, null);
    }

    public int          getId()         { return id; }
    public Livro        getLivro()      { return livro; }
    public Cliente      getCliente()    { return cliente; }
    public int           getQuantidade() { return quantidade; }
    public LocalDateTime getDataVenda()  { return dataVenda; }

    public void setId(int id) { this.id = id; }

    public double total() {
        return livro.getPreco() * quantidade;
    }

    @Override
    public String toString() {
        return String.format("[%d] %s comprou %dx \"%s\" — Total: R$ %.2f",
                id, cliente.getNome(), quantidade, livro.getTitulo(), total());
    }
}