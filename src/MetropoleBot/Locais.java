package MetropoleBot;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

class Locais
{
    /**
     * Verifica se o local ja existe no banco, se nao existir o cadastra, caso contrario nao faz nada
     *
     * @param local localizacao a ser cadastrada
     * @param con   conexao com o banco
     * @return retorna true se a localizacao nao existe no banco e a cadastrou, false caso contrario
     * @throws SQLException tratamento basico de excecao
     */
    boolean cadastrarLocal(Local local, Connection con) throws SQLException
    {
        if(find(local, con))
        {
            String sql = "INSERT INTO locais (nome, descricao) values(?, ?)";

            PreparedStatement stmt = con.prepareStatement(sql);

            stmt.setString(1, local.getNome());
            stmt.setString(2, local.getDescricao());

            return stmt.executeUpdate() > 0;
        }
        return false;
    }


    /**
     * Lista os locais cadastrados no banco
     *
     * @param con conexao com o banco
     * @return retorna o resultado da consulta
     * @throws SQLException tratamento basico de excecao
     */
    ResultSet list(Connection con) throws SQLException
    {
        String sql = "SELECT nome FROM locais";

        PreparedStatement stmt = con.prepareStatement(sql);

        return stmt.executeQuery();
    }

    /**
     * Busca por um objeto local, usando seu nome, no banco
     *
     * @param local local(objeto) a ser procurado
     * @param con   conexao com o banco
     * @return retorna true se existe, false se nao
     * @throws SQLException tratamento basico de excecao
     */
    private boolean find(Local local, Connection con) throws SQLException
    {
        String sql = "SELECT id FROM locais WHERE nome = ?";

        PreparedStatement stmt = con.prepareStatement(sql);

        stmt.setString(1, local.getNome());

        ResultSet result = stmt.executeQuery();

        return !result.next();

    }

    /**
     * Busca por um nome de local no banco
     *
     * @param local local(string) a ser procurado
     * @param con   conexao com o banco
     * @return retorna true se existe, false se nao
     * @throws SQLException tratamento basico de excecao
     */
    String find(String local, Connection con) throws SQLException
    {
        String sql = "SELECT id FROM locais WHERE nome = ?";

        PreparedStatement stmt = con.prepareStatement(sql);

        stmt.setString(1, local);

        ResultSet result = stmt.executeQuery();

        if(result.next())
            return result.getString("id");
        else
            return null;
    }

    /**
     * Verifica se a localizacao nao esta em uso no banco de dados, se nao estiver, apaga, caso contrario nao faz nada
     *
     * @param local localizacao a ser apagada
     */
    public void delete(String local)
    {
        // TODO delete from locais where locais.nome = 'corredor'
    }
}
