package Api;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class DataBase {

	private ArrayList<String> logins = new ArrayList<>();
	private ArrayList<String> passes = new ArrayList<>();
	public DataBase(Environment env) throws FileNotFoundException {
		Scanner sc = new Scanner(new File("data"));
		while (sc.hasNext()) {
			if (sc.nextInt() == env.ordinal()) {
				logins.add(sc.next());
				passes.add(sc.next());
			} else
				sc.nextLine();
		}
		sc.close();
	}
	ArrayList<String> getLogins(){
		return logins;
	}

	ArrayList<String> getPasses(){
		return passes;
	}
}
