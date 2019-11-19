package MetropoleBot;

class Categoria
{
    private String nome;
    private String codigo;
    private String descricao;

    Categoria(String Codigo, String Nome, String Descricao)
    {
        this.codigo = Codigo;
        this.nome = Nome;
        this.descricao = Descricao;
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
}
