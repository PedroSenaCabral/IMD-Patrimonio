package MetropoleBot;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;

public class Bem extends Categorias
{
    private ArrayList<String> tipoBens;
    private int codigo;
    private String nome;
    private String descricao;
    private Local localizacao;
    private Categoria categoria;

    /*
     * Construtor de classe TipoBem
     */
    public Bem(ArrayList<String> bem, int codigo, String nome, String descricao, Local localizacao, Categoria categoria)
    {
        super();
        tipoBens = bem;
        this.codigo = codigo;
        this.nome = nome;
        this.descricao = descricao;
        this.localizacao = localizacao;
        this.categoria = categoria;
    }

    Bem()
    {
        super();
    }

    public Bem(String msgCodigo, String msgNome, String msgDescricao, String msgLocalizacao, String msgCategoria)
    {
    }

    public void setTipoBens(ArrayList<String> tipoBens)
    {
        this.tipoBens = tipoBens;
    }

    public ArrayList<String> getTipoBens()
    {
        return tipoBens;
    }

    /*
     * Cadastrar Tipo de bem
     */
    public void cadastrarBem(String tipobem)
    {
        tipoBens.add(tipobem);
    }

    /*
     * Listar os bens
     */
    public void listarTipobem()
    {
        for(String tipobem: tipoBens)
        {
            System.out.println(tipobem);
        }
    }

    public int getCodigo()
    {
        return codigo;
    }

    public String getNome()
    {
        return nome;
    }

    public String getDescricao()
    {
        return descricao;
    }

    /*
     * Buscar Tipo de bem por codigo
     */
    public void buscarBemCodigo(int Codigo)
    {
        for(String tipobem: tipoBens)
        {
            if(Codigo == this.codigo)
                System.out.println(tipobem);
        }
        System.out.println("Item nao existe");
    }

    /*
     * Buscar Tipo de bem por nome
     */
    public void buscarBemNome(String Nome)
    {
        for(String tipobem: tipoBens)
        {
            if(Nome.equals(this.nome))
            {
                System.out.println(tipobem);
            }
        }
        System.out.println("Item nao existe");
    }

    /*
     * Buscar Tipo de bem por descricao
     */
    public void buscarBemDescricao(String Descricao)
    {
        for(String tipobem: tipoBens)
        {
            if(Descricao.equals(this.descricao))
            {
                System.out.println(tipobem);
            }
            System.out.println("Item nao existe");
        }
    }

    /*
     * Excuir Tipo de bem por nome
     */
    public void excluirTipoBem(String Nome)
    {
        for(String tipobem: tipoBens)
        {
            if(Nome.equals(this.nome))
            {
                tipoBens.remove(tipobem);
            }
            System.out.println("Item nao existe");
        }
    }

    public boolean cadastrarBem(Bem bem, Connection con)
    {
        for(Categoria cat: categorias)
        {
            if(cat.getNome().equals(categoria.getNome()))
                return false;
        }

        this.categorias.add(categoria);


        String sql = "INSERT INTO categorias values(?, ?, ?)";

        PreparedStatement stmt = con.prepareStatement(sql);

        stmt.setString(1, categoria.getCodigo());
        stmt.setString(2, categoria.getNome());
        stmt.setString(3, categoria.getDescricao());

        stmt.executeUpdate();

        return true;
    }
}
