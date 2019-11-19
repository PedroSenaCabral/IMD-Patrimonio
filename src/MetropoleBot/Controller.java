package MetropoleBot;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;


public class Controller
{
    @FXML
    Button start;    // Butao de iniciar
    @FXML
    TextArea log;    // Area de log

    private boolean started = false;

    @FXML
    void start()
    {
        if(!started)
        {
            log.setText(">>> Bot iniciado!");
            Bot bot = new Bot(log);

            bot.start();

            started = true;
        }
    }
}
