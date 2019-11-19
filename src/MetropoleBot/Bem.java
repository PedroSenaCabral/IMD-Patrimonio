package MetropoleBot;

public class Bem
{
    private String codigo;
    private String nome;
    private String descricao;
    private Local localizacao;
    private Categoria categoria;

    Bem(String codigo, String nome, String descricao, String localizacao, String categoria)
    {
        this.codigo = codigo;
        this.nome = nome;
        this.descricao = descricao;
        this.localizacao = new Local(localizacao, null);
        this.categoria = new Categoria(null, categoria, null);
    }

    String getCodigo()
    {
        return codigo;
    }

    String getNome()
    {
        return nome;
    }

    String getDescricao()
    {
        return descricao;
    }

    public Local getLocalizacao()
    {
        return localizacao;
    }

    public Categoria getCategoria()
    {
        return categoria;
    }
}
