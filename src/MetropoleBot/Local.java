package MetropoleBot;

class Local
{
    private String nome;
    private String descricao;

    Local(String local, String descricao)
    {
        this.nome = local;
        this.descricao = descricao;
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
