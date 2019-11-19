package MetropoleBot;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Bens
{
    private ArrayList<Bem> bens;

    Bens()
    {
        bens = new ArrayList<>();
    }

    /*
     * Listar os bens
     */
    public void listarTipobem()
    {
        for(Bem bem: bens)
        {
            System.out.println(bem);
        }
    }

    /*
     * Buscar Tipo de bem por codigo
     */
    public void buscarBemCodigo(String Codigo)
    {
        for(Bem bem: bens)
        {
            if(Codigo.equals(bem.getCodigo()))
                System.out.println(bem);
        }
        System.out.println("Item nao existe");
    }

    /*
     * Buscar Tipo de bem por nome
     */
    public void buscarBemNome(String Nome)
    {
        for(Bem bem: bens)
        {
            if(Nome.equals(bem.getNome()))
            {
                System.out.println(bem);
            }
        }
        System.out.println("Item nao existe");
    }

    /*
     * Buscar Tipo de bem por descricao
     */
    public void buscarBemDescricao(String Descricao)
    {
        for(Bem bem: bens)
        {
            if(Descricao.equals(bem.getDescricao()))
            {
                System.out.println(bem);
            }
            System.out.println("Item nao existe");
        }
    }

    /*
     * Excuir Tipo de bem por nome
     */
    public void excluirTipoBem(String Nome)
    {
        for(Bem bem: bens)
        {
            if(Nome.equals(bem.getNome()))
            {
                bens.remove(bem);
            }
            System.out.println("Item nao existe");
        }
    }

    /**
     * @param bem         bem a ser cadastrado
     * @param idCategoria categoria a qual o bem pertence
     * @param idLocal     local ao qual o bem pertence
     * @param con         conexao com o banco
     * @return true se conseguir cadastrar o bem, false se nao
     * @throws SQLException tratamento basico de excecao
     */
    boolean cadastrar(Bem bem, String idCategoria, String idLocal, Connection con) throws SQLException
    {

        if(find(bem, con))
        {
            this.bens.add(bem);

            String sql = "INSERT INTO bens (codigo, nome, descricao, idLocalizacao, idCategoria) values(?, ?, ?, ?, ?)";

            PreparedStatement stmt = con.prepareStatement(sql);

            stmt.setString(1, bem.getCodigo());
            stmt.setString(2, bem.getNome());
            stmt.setString(3, bem.getDescricao());
            stmt.setString(4, idLocal);
            stmt.setString(5, idCategoria);

            stmt.executeUpdate();

            return true;
        }

        else
            return false;
    }

    /**
     * @param bem bem a ser procurado no banco
     * @param con conexao com o banco
     * @return retorna true se existe, falso se nao
     * @throws SQLException tratamento basico de excecao
     */
    private boolean find(Bem bem, Connection con) throws SQLException
    {
        String sql = "SELECT id FROM bens WHERE codigo = ?";

        PreparedStatement stmt = con.prepareStatement(sql);

        stmt.setString(1, bem.getCodigo());

        ResultSet result = stmt.executeQuery();

        return !result.next();
    }
}
