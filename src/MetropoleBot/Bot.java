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
    private TextArea log;
    private Connection con = null;
    // bot created with code from botfather
    // test 927616899:AAFD419_B2phsx5iFtjM3hM17cvVkNTkO9Q
    // prod 1068564256:AAGsb9uiUmM37Vpnkrl7cQPKjVWrgKI-2NQ
    private TelegramBot bot = new TelegramBot("927616899:AAFD419_B2phsx5iFtjM3hM17cvVkNTkO9Q");
    private Localizacao localizacao = new Localizacao();
    private Bens bens = new Bens();
    private Categorias categorias = new Categorias();



    Bot(TextArea log)
    {
        this.log = log;
    }

    void start()
    {
        connect();

        if(con == null)
        {
            log.appendText("\n\n>>> Não foi possível connectar ao banco de dados! <<<");
            return;
        }

        botAction();
    }

    private void botAction()
    {
        // Register for updates
        bot.setUpdatesListener(updates -> {
            // Process updates
            updates.forEach(update -> {
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
                                "(cadastrar localização -codigo *** -nome *** -descrição ***)"));
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
                else
                    bot.execute(new SendMessage(chatId, "Comando inválido!"));


                // Log
                // System.out.println("\n" + timeFormatter.format(now) + " - chatID: " + chatId + " - msg: " + msg);
                log.appendText("\n" + timeFormatter.format(now) + " - chatID: " + chatId + " - msg: " + msg);
            });

            // Return id of last processed update or confirm them all
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }

    private boolean cadastrarBem(String msg, long chatId) throws SQLException
    {
        String codigo = "-codigo ";
        String nome = "-nome ";
        String descricao = "-descrição ";
        String local = "-localização ";
        String categoria = "-categoria ";

        final int codigoIndex = msg.indexOf(codigo) + codigo.length();
        final int nomeIndex = msg.indexOf(nome) + nome.length();
        final int descricaoIndex = msg.indexOf(descricao) + descricao.length();
        final int localizacaoIndex = msg.indexOf(local) + local.length();
        final int categoriaIndex = msg.indexOf(categoria) + categoria.length();

        String msgCodigo = msg.substring(codigoIndex, msg.indexOf(nome) - 1);
        String msgNome = msg.substring(nomeIndex, msg.indexOf(descricao) - 1);
        String msgDescricao = msg.substring(descricaoIndex, msg.indexOf(local) - 1);
        String msgLocalizacao = msg.substring(localizacaoIndex, msg.indexOf(categoria) - 1);
        String msgCategoria = msg.substring(categoriaIndex);

        String idLocal = localizacao.find(msgLocalizacao, con);

        if(idLocal != null)
        {
            String idCategoria = categorias.find(msgCategoria, con);
            System.out.println(msgCategoria);
            if(idCategoria != null)
                return bens.cadastrarBem(new Bem(msgCodigo, msgNome, msgDescricao, msgLocalizacao, msgCategoria), idCategoria, idLocal, con);
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

    private boolean cadastrarCategoriaBem(String msg) throws SQLException
    {
        String codigo = "-codigo ";
        String nome = "-nome ";
        String descricao = "-descrição ";

        final int codigoIndex = msg.indexOf(codigo) + codigo.length();
        final int nomeIndex = msg.indexOf(nome) + nome.length();
        final int descricaoIndex = msg.indexOf(descricao) + descricao.length();

        String msgCodigo = msg.substring(codigoIndex, msg.indexOf(nome) - 1);
        String msgNome = msg.substring(nomeIndex, msg.indexOf(descricao) - 1);
        String msgDescricao = msg.substring(descricaoIndex);

        String idCategoria = categorias.find(msgNome, con);
        if(idCategoria == null)
            return categorias.cadastrarCategoria(new Categoria(msgCodigo, msgNome, msgDescricao), con);
        else
            return false;
    }

    private boolean cadastrarLocalizacao(String msg) throws SQLException
    {
        String start = "-local ";
        String end = "-descrição ";

        final int beginIndex = msg.indexOf(start) + start.length();
        final int endIndex = msg.indexOf(end) + end.length();

        String local = msg.substring(beginIndex, msg.indexOf(end) - 1);
        String descricao = msg.substring(endIndex);

        String idLocal = localizacao.find(local, con);

        if(idLocal == null)
            return localizacao.cadastrarLocal(new Local(local, descricao), con);
        else
            return false;
    }

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