package Final_Version.main;

import Final_Version.testing.CreateTitleForTesting;
import Final_Version.testing.Testing;
import Final_Version.training.CreateTitleForTraining;

public class PrincipalMain {

	public static void main(String[] args) throws Exception {
		CreateTitleForTraining.createTitle();
		
		CreateTitleForTesting.createTitle();
		
		Testing.testingTitle();

	}

}
