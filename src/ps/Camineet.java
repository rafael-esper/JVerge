package ps;

import static core.Script.*;
import static ps.PS.*;
import ps.oo.MenuStack;

public class Camineet {

	static int timebox = 0;
	
	public static void startmap()   {
		current_map.renderstring="1,2,R,E";
		timebox = 0;
		hookretrace("cityname");
		PS.startmap();
	}
	public static void cityname() {
		if(timebox++ > 150)
			hookretrace("");
		
		MenuStack.drawTextBox(200, 290, "CAMINEET");
		//graycolorfilter(screen.getImage());
	}

	
	// Camineet.map
	public static void exit1() {
		System.out.println("Entrou em Palma");
		mapswitch("Palma.map",84,49);		
	}
	public static void exit2() {
		System.out.println("Entrou em Palma");
		mapswitch("Palma.map",82,49);		
	}	
	public static void robot1() {
		pauseplayerinput();
		Stext(rb.getString("Camineet_People_Cop1"));
		unpauseplayerinput();
	}
	public static void robot2() {
		pauseplayerinput();
		Stext(rb.getString("Camineet_People_Cop2"));
		unpauseplayerinput();
	}
	
	public static void alis() {
		MenuStack.instance.startScene("");
		Stext(rb.getString("Camineet_House_Alis"));
		MenuStack.instance.endScene();
		playermove("D1");
	}
	
	public static void yellow() { // house
		MenuStack.instance.startScene("");
		Stext(rb.getString("Camineet_House_Man"));
		MenuStack.instance.endScene();
		playermove("D1");
	}

	public static void oldman() { // house
		MenuStack.instance.startScene("");
		if(Prompt(rb.getString("Camineet_House_Oldman"), new String[]{ 
				rb.getString("Menu_Choice_Yes"), 
				rb.getString("Menu_Choice_No")}) == 1) {
			Stext(rb.getString("Camineet_House_Oldman_Yes"));
		}
		else {
			Stext(rb.getString("Camineet_House_Oldman_No"));
		}
		
		MenuStack.instance.endScene();
		playermove("D1");
	}
	
	public static void nekise() {
		MenuStack.instance.push(MenuStack.instance.createPromptBox(10, 10, new String[]{"Stats", "Magic", "Item", "Talk", "Options"}));
		MenuStack.instance.waitOpt(true);
		MenuStack.instance.pop();
	}
	
	public static void suelo() {
		MenuStack.instance.startScene("");
		Stext("Nuss!");
		//Prompt("Do you need to rest?", "Ya", "Nein");
		Stext("Alis: This is the longest word I know. Are you sure?! I've known him for sure... Odin: This is fun!!! They're near here!");
		Stext("Hi! My name is Suelo. Do you want anything from me, or a cure or to relax?");
		MenuStack.instance.endScene();
		playermove("D1");
	}
	
	public static void shop1() {
		MenuStack.instance.startScene("");
		Stext("Hi. This is a weapon shop.");
		
		Prompt("What do you want to buy?", new String[]{});
		
		MenuStack.instance.endScene();
		playermove("D1");
	}
	
	public static void ent1() {
		EntStart();
		Stext("Hi!");
		Stext("I'm here to tell you nothing!");
		EntFinish();
	}
	public static void ent2() {
		EntStart();
		Stext("Hi!");
		EntFinish();
	}
	public static void ent3() {
		EntStart();
		Stext("Hi!");
		EntFinish();
	}
	public static void ent4() {
		EntStart();
		Stext("Hi!");
		Stext("Hello!");
		EntFinish();
	}

	
	
	
}
