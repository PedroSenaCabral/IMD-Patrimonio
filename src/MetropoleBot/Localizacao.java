package MetropoleBot;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Localizacao
{
    private ArrayList<Local> locais;

    Localizacao()
    {
        this.locais = new ArrayList<>();
    }

    public ArrayList<Local> getLocais()
    {
        return locais;
    }

    /*
     * Metodo para cadastrar uma localizacao
     */
    boolean cadastrarLocal(Local local, Connection con) throws SQLException
    {
        if(find(local, con))
        {

            this.locais.add(local);


            String sql = "INSERT INTO locais (nome, descricao) values(?, ?)";

            PreparedStatement stmt = con.prepareStatement(sql);

            stmt.setString(1, local.getNome());
            stmt.setString(2, local.getDescricao());

            stmt.executeUpdate();

            return true;
        }
        return false;
    }


    /*
     * Metodo para listar as localizacao cadastradas
     */
    public void listarLocais()
    {
        for(Local local: locais)
        {
            System.out.println(local);
        }
    }

    /*
     * Metodo para exclusao de localizacao cadastrada
     */
    public void excluirLocal(String nome)
    {
        for(Local local: locais)
        {
            if(nome.equals(local.getNome()))
            {
                locais.remove(local);
            }
            System.out.println("Item nao existe");
        }
    }

    /*
     * Metodo para trocar a localizacao cadastrada de um local
     */
    /*
    public void movimentarBem(ArrayList<String> tipoBens, int Codigo, String novaLocalizacao)
    {
        for(Local local: locais)
        {
            if(novaLocalizacao.equals(local.getNome()))
            {
                for(String tipobem: tipoBens)
                {
                    if(Codigo == this.getCodigo())
                    {
                        tipoBens.remove(tipobem);
                        tipoBens.add(novaLocalizacao);
                    }
                }
            }
        }
    }
*/
    /*
     * Metodo para criar relatorio por localizacao em seguida por categoria
     * !!!Falta ordenacao alfabetica
     */
    /*
    public void relatorioLocalizacaoCategoria()
    {
        for(Local local: locais)
        {
            System.out.println(local);
            this.relatorioCategoria();
        }
    }
*/
    private boolean find(Local local, Connection con) throws SQLException
    {
        String sql = "SELECT id FROM locais WHERE nome = ?";

        PreparedStatement stmt = con.prepareStatement(sql);

        stmt.setString(1, local.getNome());

        ResultSet result = stmt.executeQuery();

        return !result.next();

    }

    String find(String localizacao, Connection con) throws SQLException
    {
        String sql = "SELECT id FROM locais WHERE nome = ?";

        PreparedStatement stmt = con.prepareStatement(sql);

        stmt.setString(1, localizacao);

        ResultSet result = stmt.executeQuery();

        if(result.next())
            return result.getString("id");
        else
            return null;
    }
}
