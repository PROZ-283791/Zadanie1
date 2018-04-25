package Api;

import java.util.Optional;
import javafx.stage.Stage;
import javafx.util.Pair;

public class Main extends javafx.application.Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage){
		LogonDialog dial = new LogonDialog();
		Optional<Pair<Environment,String>> res = dial.showAndWait();
		if (res.isPresent())
			System.out.println("Zalogowano. środowisko :" + res.get().getKey() + ", użytkownik: " + res.get().getValue());
		else
			System.out.println("nie zalogowano");
	}
}
