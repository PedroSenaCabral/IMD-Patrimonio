package MetropoleBot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.request.SendMessage;
import javafx.scene.control.TextArea;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

class Bot
{
    /**
     * Bot criado para controlar acervo do instituto metropole digital
     *
     * @param log referencia para a area de log na interface
     * @param con conexao com o banco
     * @param bot bot criado com o token gerado pelo bot father e desenvolvido por pengrad
     * token de teste 927616899:AAFD419_B2phsx5iFtjM3hM17cvVkNTkO9Q
     * token de producao 1068564256:AAGsb9uiUmM37Vpnkrl7cQPKjVWrgKI-2NQ
     * @param locais locais do IMD
     * @param bens bens do IMD
     * @param categorias categorias dos bens
     */
    private TextArea log;
    private Connection con = null;
    private TelegramBot bot = new TelegramBot("927616899:AAFD419_B2phsx5iFtjM3hM17cvVkNTkO9Q");
    private Locais locais = new Locais();
    private Bens bens = new Bens();
    private Categorias categorias = new Categorias();


    Bot(TextArea log)
    {
        this.log = log;
    }

    // Acao realizada apos pressionar o botao de start, inicia a conexao com o banco e logo apos o bot
    void start()
    {
        connect();

        if(con == null)
        {
            log.appendText("\n\n>>> Não foi possível connectar ao banco de dados! <<<");
            return;
        }

        listen();
    }

    // Faz o bot esperar por acoes dos usuarios
    private void listen()
    {
        // Registra atualizacoes
        bot.setUpdatesListener(updates -> {
            // Processa as atualizacoes
            updates.forEach(update -> {
                /*
                 * @param chatId id do chat atual
                 * @param msg mensagem vinda do chat
                 * @param now data que o bot recebe a acao
                 */
                long chatId = update.message().chat().id();
                String msg = update.message().text();
                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
                LocalDateTime now = LocalDateTime.now();


                if(msg.contains("cadastrar localização"))
                {
                    if(msg.contains("-local") && msg.contains("-descrição"))
                    {
                        try
                        {
                            if(cadastrarLocalizacao(msg))
                                bot.execute(new SendMessage(chatId, "Cadastro realizado com sucesso!"));
                            else
                                bot.execute(new SendMessage(chatId, "Local já existe!"));
                        }
                        catch(SQLException e)
                        {
                            e.printStackTrace();
                        }
                    }
                    else
                        bot.execute(new SendMessage(chatId, "Erro ao cadastrar localização " +
                                "(cadastrar localização -local *** -descrição ***)"));
                }
                else if(msg.contains("cadastrar categoria de bem"))
                {
                    if(msg.contains("-codigo") && msg.contains("-nome") && msg.contains("-descrição"))
                    {
                        try
                        {
                            if(cadastrarCategoriaBem(msg))
                                bot.execute(new SendMessage(chatId, "Cadastro realizado com sucesso!"));
                            else
                                bot.execute(new SendMessage(chatId, "Categoria de bem já existe!"));
                        }
                        catch(SQLException e)
                        {
                            e.printStackTrace();
                        }
                    }
                    else
                        bot.execute(new SendMessage(chatId, "Erro ao cadastrar categoria de bem " +
                                "(cadastrar categoria de bem -codigo *** -nome *** -descrição ***)"));
                }
                else if(msg.contains("cadastrar bem"))
                {
                    if(msg.contains("-codigo") && msg.contains("-nome") && msg.contains("-descrição") && msg.contains("-localização") && msg.contains("-categoria"))
                    {
                        try
                        {
                            if(cadastrarBem(msg, chatId))
                                bot.execute(new SendMessage(chatId, "Cadastro realizado com sucesso!"));
                            else
                                bot.execute(new SendMessage(chatId, "Não foi possível cadastrar o bem!"));
                        }
                        catch(SQLException e)
                        {
                            e.printStackTrace();
                        }
                    }
                    else
                        bot.execute(new SendMessage(chatId, "Erro ao cadastrar categoria de bem " +
                                "(cadastrar bem -codigo *** -nome *** -descrição *** -localização *** -categoria ***)"));
                }
                else if(msg.contains("listar localizações")) ;
                else if(msg.contains("listar categorias")) ;
                else if(msg.contains("listar bens de uma localização")) ;
                else if(msg.contains("buscar bem por código")) ;
                else if(msg.contains("buscar bem por nome")) ;
                else if(msg.contains("buscar bem por descricao")) ;
                else if(msg.contains("movimentar bem")) ;
                else if(msg.contains("gerar relatório")) ;
                else if(msg.contains("gerar relatório -arquivo-")) ;//TODO think a better way
                else if(msg.contains("Carregar dados")) ;
                else if(msg.contains("Apagar localização")) ;
                else if(msg.contains("Apagar categoria")) ;
                else if(msg.contains("Apagar bem")) ;
                else if(msg.contains("-help"))
                {
                    String comandos = "Comandos possíveis:\n" +
                            "cadastrar localização -local *** -descrição ***\n" +
                            "cadastrar categoria de bem -codigo *** -nome *** -descrição ***\n" +
                            "cadastrar bem -codigo *** -nome *** -descrição *** -localização *** -categoria ***\n";
                    bot.execute(new SendMessage(chatId, comandos));
                }
                else
                    bot.execute(new SendMessage(chatId, "Comando inválido!"));


                // Log
                // System.out.println("\n" + timeFormatter.format(now) + " - chatID: " + chatId + " - msg: " + msg);
                log.appendText("\n" + timeFormatter.format(now) + " - chatID: " + chatId + " - msg: " + msg);
            });

            // Retorna id da ultima atualizacao processada ou confirma todas
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }

    /**
     * @param msg    mensagem enviada pelo usuario
     * @param chatId id correspondente ao chat da mensagem
     * @return true se conseguir cadastrar o bem e false caso contrario
     * @throws SQLException tratamento simples de excecao
     */
    private boolean cadastrarBem(String msg, long chatId) throws SQLException
    {
        String codigoFlag = "-codigoFlag ";
        String nomeFlag = "-nomeFlag ";
        String descricaoFlag = "-descrição ";
        String localFlag = "-localização ";
        String categoriaFlag = "-categoriaFlag ";

        final int indexCodigo = msg.indexOf(codigoFlag) + codigoFlag.length();
        final int indexNome = msg.indexOf(nomeFlag) + nomeFlag.length();
        final int indexDescricao = msg.indexOf(descricaoFlag) + descricaoFlag.length();
        final int indexLocal = msg.indexOf(localFlag) + localFlag.length();
        final int indexCategoria = msg.indexOf(categoriaFlag) + categoriaFlag.length();

        String codigo = msg.substring(indexCodigo, msg.indexOf(nomeFlag) - 1);
        String nome = msg.substring(indexNome, msg.indexOf(descricaoFlag) - 1);
        String descricao = msg.substring(indexDescricao, msg.indexOf(localFlag) - 1);
        String local = msg.substring(indexLocal, msg.indexOf(categoriaFlag) - 1);
        String categoria = msg.substring(indexCategoria);

        // Procura o local no banco de dados
        String idLocal = locais.find(local, con);

        // Se local existir
        if(idLocal != null)
        {
            // Procura categoria no banco de dados
            String idCategoria = categorias.find(categoria, con);

            // Se categoria existir
            if(idCategoria != null)
                return bens.cadastrar(new Bem(codigo, nome, descricao, local, categoria), idCategoria, idLocal, con);
            else
            {
                bot.execute(new SendMessage(chatId, "Categoria não cadastrada!"));
                return false;
            }
        }
        else
        {
            bot.execute(new SendMessage(chatId, "Local não cadastrado!"));
            return false;
        }
    }

    /**
     * @param msg mensagem enviada pelo usuario
     * @return true se conseguir cadastrar a categoria do bem e false caso nao
     * @throws SQLException tratamento basico de exececao
     */
    private boolean cadastrarCategoriaBem(String msg) throws SQLException
    {
        String codigoFlag = "-codigoFlag ";
        String nomeFlag = "-nomeFlag ";
        String descricaoFlag = "-descrição ";

        final int indexCodigo = msg.indexOf(codigoFlag) + codigoFlag.length();
        final int indexNome = msg.indexOf(nomeFlag) + nomeFlag.length();
        final int indexDescricao = msg.indexOf(descricaoFlag) + descricaoFlag.length();

        String codigo = msg.substring(indexCodigo, msg.indexOf(nomeFlag) - 1);
        String nome = msg.substring(indexNome, msg.indexOf(descricaoFlag) - 1);
        String descricao = msg.substring(indexDescricao);

        // Procura categoria no banco
        String idCategoria = categorias.find(nome, con);
        // Se categoria nao esta cadastrada
        if(idCategoria == null)
            return categorias.cadastrarCategoria(new Categoria(codigo, nome, descricao), con);
        else
            return false;
    }

    /**
     * @param msg mensagem enviada pelo usuario
     * @return true se conseguir cadastrar o local, false caso nao
     * @throws SQLException tratamento basico de excecao
     */
    private boolean cadastrarLocalizacao(String msg) throws SQLException
    {
        String localFlag = "-local ";
        String descricaoFlag = "-descrição ";

        final int indexLocal = msg.indexOf(localFlag) + localFlag.length();
        final int indexDescricao = msg.indexOf(descricaoFlag) + descricaoFlag.length();

        String local = msg.substring(indexLocal, msg.indexOf(descricaoFlag) - 1);
        String descricao = msg.substring(indexDescricao);

        // Procura local no banco
        String idLocal = locais.find(local, con);

        // Se local nao existe
        if(idLocal == null)
            return locais.cadastrarLocal(new Local(local, descricao), con);
        else
            return false;
    }

    /**
     * Conecta com o banco de dados
     */
    private void connect()
    {
        try
        {
            con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/imdPatrimonio", "root", "mysql");
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
        }
    }
}