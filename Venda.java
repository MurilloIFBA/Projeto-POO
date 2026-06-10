public class Venda {
    private int id;
    private Livro livro;
    private Cliente cliente;
    private int quantidade;

    public Venda(int id, Livro livro, Cliente cliente, int quantidade) {
        this.id         = id;
        this.livro      = livro;
        this.cliente    = cliente;
        this.quantidade = quantidade;
    }

    public int     getId()         { return id; }
    public Livro   getLivro()      { return livro; }
    public Cliente getCliente()    { return cliente; }
    public int     getQuantidade() { return quantidade; }

    public double total() {
        return livro.getPreco() * quantidade;
    }

    @Override
    public String toString() {
        return String.format("[%d] %s comprou %dx \"%s\" — Total: R$ %.2f",
                id, cliente.getNome(), quantidade, livro.getTitulo(), total());
    }
}
