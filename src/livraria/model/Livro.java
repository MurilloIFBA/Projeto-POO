package livraria.model;
 
public class Livro {
    private int id;
    private String titulo;
    private String autor;
    private double preco;
    private int estoque;
 
    public Livro(int id, String titulo, String autor, double preco, int estoque) {
        this.id      = id;
        this.titulo  = titulo;
        this.autor   = autor;
        this.preco   = preco;
        this.estoque = estoque;
    }
 
    // Construtor sem id, usado ao CADASTRAR um livro novo (o banco gera o id sozinho)
    public Livro(String titulo, String autor, double preco, int estoque) {
        this(0, titulo, autor, preco, estoque);
    }
 
    public int    getId()      { return id; }
    public String getTitulo()  { return titulo; }
    public String getAutor()   { return autor; }
    public double getPreco()   { return preco; }
    public int    getEstoque() { return estoque; }
 
    public void setId(int id)             { this.id      = id; }
    public void setTitulo(String titulo)  { this.titulo  = titulo; }
    public void setAutor(String autor)    { this.autor   = autor; }
    public void setPreco(double preco)    { this.preco   = preco; }
    public void setEstoque(int estoque)   { this.estoque = estoque; }
 
    @Override
    public String toString() {
        return String.format("[%d] %s — %s | R$ %.2f | Estoque: %d",
                id, titulo, autor, preco, estoque);
    }
}