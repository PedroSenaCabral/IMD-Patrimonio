package MetropoleBot;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

class Categorias
{
    private ArrayList<Categoria> categorias;

    Categorias()
    {
        this.categorias = new ArrayList<>();
    }

    /*
     * Cria novas categorias
     */
    boolean cadastrarCategoria(Categoria categoria, Connection con) throws SQLException
    {
        if(find(categoria, con))
        {

            this.categorias.add(categoria);


            String sql = "INSERT INTO categorias(codigo, nome, descricao) values(?, ?, ?)";

            PreparedStatement stmt = con.prepareStatement(sql);

            stmt.setString(1, categoria.getCodigo());
            stmt.setString(2, categoria.getNome());
            stmt.setString(3, categoria.getDescricao());

            stmt.executeUpdate();

            return true;
        }
        else
            return false;
    }

    /**
     * Busca por um objeto categoria, usando seu nome, no banco
     *
     * @param categoria categoria(objeto) a ser procurada no banco
     * @param con       conexao com o banco
     * @return retorna true se encontrar categoria, false se nao
     * @throws SQLException tratamento basico de excecao
     */
    private boolean find(Categoria categoria, Connection con) throws SQLException
    {
        String sql = "SELECT id FROM categorias WHERE nome = ?";

        PreparedStatement stmt = con.prepareStatement(sql);

        stmt.setString(1, categoria.getNome());

        ResultSet result = stmt.executeQuery();

        return !result.next();
    }


    /**
     * Busca por um nome de categoria no banco
     *
     * @param categoria categoria(string) a ser procurada no banco
     * @param con       conexao com o banco
     * @return retorna true se encontrar categoria, false se nao
     * @throws SQLException tratamento basico de excecao
     */
    String find(String categoria, Connection con) throws SQLException
    {
        String sql = "SELECT id FROM categorias WHERE nome = ?";

        PreparedStatement stmt = con.prepareStatement(sql);

        stmt.setString(1, categoria);

        ResultSet result = stmt.executeQuery();

        if(result.next())
            return result.getString("id");
        else
            return null;
    }

    /**
     * Gera uma lista de categorias que estao salvas no banco
     *
     * @param con conexao com o banco
     * @return retorna uma lista de categorias, ou null caso nao possua categorias cadastradas
     */
    ResultSet list(Connection con) throws SQLException
    {
        String sql = "SELECT nome FROM categorias";

        PreparedStatement stmt = con.prepareStatement(sql);

        return stmt.executeQuery();
    }
}
