package MetropoleBot;

public class Local
{
    private String nome;
    private String descricao;

    public Local(String local, String descricao)
    {
        this.nome = local;
        this.descricao = descricao;
    }

    public String getNome()
    {
        return nome;
    }

    public String getDescricao()
    {
        return descricao;
    }
}
