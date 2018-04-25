package Api;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javafx.collections.FXCollections;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;
import java.util.Optional;

/**
 * Klasa zarządzająca okienkiem logowania, z polem wyboru środowiska, wpisaniem
 * lub wybory użytkownika oraz do wpisania hasła wywołanie metody showAndWait()
 * spowoduje wyświetlenie okienka
 * 
 * @author slawek
 *
 */
public class LogonDialog {
	private Dialog<ButtonType> dial = new Dialog<>();
	private ComboBox<String> login = new ComboBox<>(FXCollections.observableArrayList());;
	private PasswordField pass = new PasswordField();
	private ChoiceBox<String> env = new ChoiceBox<>();
	private DataBase data;

	/**
	 * Konstruuje okienko dialogowe z ustawionym tytułem, tekstem, ikoną oraz
	 * przyciskami
	 */
	public LogonDialog() {
		dial.getDialogPane().setContent(setGrid());
		dial.setTitle("Logowanie");
		dial.setHeaderText("Witaj w systemie!");
		dial.setGraphic(loadIcon());
		dial.getDialogPane().getButtonTypes().add(new ButtonType("Anuluj", ButtonData.CANCEL_CLOSE));
		dial.getDialogPane().getButtonTypes().add(new ButtonType("Logon", ButtonData.YES));
		dial.getDialogPane().lookupButton(dial.getDialogPane().getButtonTypes().get(1)).setDisable(true);
	}

	/**
	 * 
	 * @return wartosc logiczna, czy wpisane dane występują w bazie
	 */
	private boolean isPassCorrect() {
		return data.getLogins().contains(login.getValue())
				&& pass.getText().equals(data.getPasses().get(data.getLogins().indexOf(login.getValue())));
	}

	/**
	 * Konwertuje domyślnie zwracaną wartość przez javafx.scene.control.Dialog na
	 * pożądana parę środowisko + login
	 * 
	 * @param buttonType
	 *            - domyslnie zwracana wartosc przez javafx.scene.control.Dialog
	 * @return para reprezentujaca srodowisko i login uzytkownika lub null, jesli
	 *         dane są niepoprawne
	 */
	private Pair<Environment, String> resultConverter(Optional<ButtonType> buttonType) {
		if (buttonType.isPresent() && buttonType.get() == dial.getDialogPane().getButtonTypes().get(1)) {
			if (isPassCorrect()) {
				return new Pair<Environment, String>(Environment.valueOf(env.getValue()), login.getValue());
			}
		}
		return null;
	}

	/**
	 * Ustawia atrybuty oraz zawartosc głownego zarządcy okienka dialogowego
	 * 
	 * @return ustawiony kontener
	 */
	private GridPane setGrid() {
		GridPane mainPane = new GridPane();
		mainPane.add(setFirstLeft(), 0, 0);
		mainPane.add(setFirstRight(), 1, 0);
		mainPane.add(setSecondLeft(), 0, 1);
		mainPane.add(setSecondRight(), 1, 1);
		mainPane.add(setThirdLeft(), 0, 2);
		mainPane.add(setThirdRight(), 1, 2);
		mainPane.setHgap(20);
		mainPane.setVgap(10);
		return mainPane;
	}

	/**
	 * @return napis Środowisko
	 */
	private Label setFirstLeft() {
		return new Label("Środowisko:");
	}

	/**
	 * @return napis Użytkownik
	 */
	private Label setSecondLeft() {
		return new Label("Użytkownik:");
	}

	/**
	 * @return napis Hasło
	 */
	private Label setThirdLeft() {
		return new Label("Hasło:");

	}

	/**
	 * Ustawia atrubuty i wartości pola do wyboru środowiska
	 * 
	 * @return ustaione pole wybory
	 */
	private ChoiceBox<String> setFirstRight() {
		env.setItems(FXCollections.observableArrayList(Environment.Produkcyjne.toString(),
				Environment.Testowe.toString(), Environment.Deweloperskie.toString()));
		env.valueProperty().addListener((observable, oldVal, newVal) -> {
			fieldChanged();
			environmentChosen(newVal);
		});
		return env;
	}

	/**
	 * Metoda będąca Listenerem, sprawdza czy jest wpisana wartość we wszystkich
	 * polach, jeśli tak, to aktywuje przycisk LOGON, jesli nie, to go dezaktywuje
	 */
	private void fieldChanged() {
		if (env.getValue() != null && login.getValue() != null && login.getValue().length() != 0
				&& pass.getText().length() != 0)
			dial.getDialogPane().lookupButton(dial.getDialogPane().getButtonTypes().get(1)).setDisable(false);
		else
			dial.getDialogPane().lookupButton(dial.getDialogPane().getButtonTypes().get(1)).setDisable(true);
	}

	/**
	 * metoda ustawiająca odpowiednie wartości w polu wyboru użytkownika po wybraniu
	 * środowiska, uzupelnia tablice przechowujące loginy i hasla użytkowników tego
	 * środowiska
	 * 
	 * @param env
	 *            - String opisujace wybrane środowisko, zamieniany później na
	 *            wartość enumeryczną
	 */
	private void environmentChosen(String env) {
		try {
			login.getItems().clear();
			// logins.clear();
			// passes.clear();
			data = new DataBase(Environment.valueOf(env));
			login.getItems().addAll(data.getLogins().subList(0, 2));
		} catch (Exception e) {
			System.out.println("cannot fild file \"data\"");
		}
	}

	/**
	 * ustawia pole wpisywania lub wyboru użytkonika
	 * 
	 * @return ustawione pole
	 */
	private ComboBox<String> setSecondRight() {
		login.setEditable(true);
		login.valueProperty().addListener((observable, oldVal, newVal) -> fieldChanged());
		return login;
	}

	/**
	 * ustawia pole do wpisania hasła
	 * 
	 * @return ustawione pole
	 */
	private PasswordField setThirdRight() {
		pass.textProperty().addListener((observable, oldVal, newVal) -> fieldChanged());
		return pass;
	}

	/**
	 * ładuje ikonę człowieka i kłódki z pliku "icon.png"
	 * 
	 * @return załadowany obraz
	 */
	private ImageView loadIcon() {
		try {
			Image icon = new Image(new FileInputStream("icon.png"));
			ImageView imageView = new ImageView(icon);
			imageView.setFitWidth(80);
			imageView.setFitHeight(80);
			return imageView;
		} catch (FileNotFoundException e) {
			System.out.println(e.getLocalizedMessage());
		}
		return null;
	}

	/**
	 * ładuje do tablic wartości loginów i haseł w wybranym środowisku
	 * 
	 * @param env
	 *            wybrane środowisko
	 * @return ta sama wartość co argument funkcji
	 * @throws FileNotFoundException
	 *             w przypadku nie odnalezienia pliku z danymi do logowania "data"
	 */
	// private Environment loadLogsAndPasses(Environment env) throws
	// FileNotFoundException {
	// Scanner sc = new Scanner(new File("data"));
	// while (sc.hasNext()) {
	// if (sc.nextInt() == env.ordinal()) {
	// logins.add(sc.next());
	// passes.add(sc.next());
	// } else
	// sc.nextLine();
	// }
	// sc.close();
	// return env;
	// }

	/**
	 * Wyświetla okienko dialogowe logowania i oczekuje na zatwierdzenie (bądź
	 * anulowanie)
	 * 
	 * @return para wartości środowisko + login użytkowika lub null, gdy użytkownik
	 *         nie figuruje w bazie w pliku "data"
	 */
	public Optional<Pair<Environment, String>> showAndWait() {
		return Optional.ofNullable(resultConverter(dial.showAndWait()));
	}
}