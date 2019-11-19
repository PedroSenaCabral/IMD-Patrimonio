package MetropoleBot;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;


public class Controller
{
	@FXML
	Button start;
	@FXML
	TextArea log;

	private boolean started = true;

	@FXML
	void start()
	{
		if(started)
		{
			log.setText("Bot iniciado");
			Bot bot = new Bot(log);

			bot.start();

			started = true;
		}
	}
}
