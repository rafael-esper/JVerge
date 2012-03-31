package ps;

import static core.Script.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import ps.oo.MenuStack;

import core.VergeEngine;

public class PS extends VergeEngine {

	static Locale locale = new Locale("en", "US");
	static ResourceBundle rb = ResourceBundle.getBundle("ps/Script", locale);		

	public static void main(String args[]) {
		
		setSystemPath(new PS().getClass());

		// Path, configfile and args
		initVergeEngine(args);
		//initVergeEngine("D:\\Rbp\\pessoal\\verge\\verge\\test\\mousedemo\\", "src/ps/verge.cfg", new String[]{"bumville.map"});
	}
	
	// SYSTEM.JAVA
	static int gotox, gotoy;
	
	public static void mapswitch(String mapname, int x, int y) {
		gotox=x;gotoy=y;
		fadeout(30); //tvout();
		map(mapname);
	}

	public static void autoexec() {
		setappname("Phantasy Star: Remake");

		//playerdiagonals = false;
		//playerstep = 16;
		
		gotox = 83;
		gotoy = 50;
		playmusic("Title.vgz");

		Stext("Testing á é í ó ú ça va?");
		if(Prompt("This is truth?", new String[]{"Yes", "No"}) == 1) {
			Stext("I believe this is true.");
		}
		else {
			Prompt("I guess this is false. Where do you want to go?", new String[]{"Palma", "Motavia", "Dezoris"});
		}
		
		Stext("Phantasy Star make. Press enter to enter Palma.");
		//Stext("Alis: This is the longest word I know. Are you sure?! I've known him for sure... Odin: This is fun!!! They're near here!");
	}

	static int Prompt(String text, String[] choices) {
		List<String> rows = splitTextIntoRows(text);
		if(rows.size() == 1) {
			MenuStack.instance.push(MenuStack.instance.createTextBox(20,185,280,42,rows.get(0), ""));
		}
		else if(rows.size() > 1) {
			MenuStack.instance.push(MenuStack.instance.createTextBox(20,185,280,42,rows.get(0), rows.get(1)));			
		}
		MenuStack.instance.push(MenuStack.instance.createPromptBox(220+choices.length*20,145, choices));
		int ret = MenuStack.instance.waitOpt(false);
		MenuStack.instance.pop();
		MenuStack.instance.pop();
		return ret+1; // Start counting options from 1
	}

	static void Stext(String text) {
		List<String> rows = splitTextIntoRows(text);
		
		// Show multiples textboxes with at most two rows for the text
		for(int j=0; j<rows.size(); j++) {
			
			String r2 = "";
			if(j+1 < rows.size())
				r2 = rows.get(j+1);
			MenuStack.instance.push(MenuStack.instance.createTextBox(20,185,280,42,rows.get(j), r2));
			MenuStack.instance.waitB1();
			MenuStack.instance.pop();
			j++;
		}
	}

	// Split list of words into rows 
	static List<String> splitTextIntoRows(String text) {
		final int MAX_PER_ROW = 38;
		
		List<String> words = splitTextIntoWords(text);
		List<String> rows = new ArrayList<String>();
		int i = 0;
		String str;
		while (i < words.size()) {
			str = words.get(i);
		    while (i < words.size()-1 && str.length()+ 1 + words.get(i+1).length() <= MAX_PER_ROW) {
		       str = str.concat(" " + words.get(i+1));
		       i += 1;
			}
		    rows.add(str); //System.out.println(str);
		    str = "";i+=1;
		}
		return rows;
	}
		// Split String in trimmed words
	static List<String> splitTextIntoWords(String text) { 
		int initial = 0;
		List<String> words = new ArrayList<String>();
		for(int i=0; i<text.length(); i++) {
			while(i<text.length() && (Character.isLetterOrDigit(text.charAt(i)) || text.charAt(i) == '\'')) {
				i++;
			}
			while(i<text.length() && !Character.isLetterOrDigit(text.charAt(i))) {
				i++;
			}
			words.add(text.substring(initial, i).trim());
			initial = i;
		}
		return words;
	}

	
	public static void startmap()   {
		
		System.out.println("startmap");
		playerstep = 16;
		cameratracking=1;

		//SetPlayer(AllocateEntity(current_map.startX*16, current_map.startY*16, "res\\chrs\\walker02.chr"));
		//SetPlayer(AllocateEntity(current_map.startX*16, current_map.startY*16, "alis.chr"));
		//SetPlayer(AllocateEntity(current_map.startX*16, current_map.startY*16, "..\\openchr\\vopenchr-0.5\\v2(4).chr"));
		//SetPlayer(AllocateEntity(current_map.startX*16, current_map.startY*16, "..\\openchr\\vopenchr-0.5\\v4temp.chr"));
		//System.out.println("RBP :" + current_map);
		//System.exit(0);
		
		setplayer(AllocateEntity(gotox*16, gotoy*16, "alis.chr"));
		render();fadein(30, false);
		//tvin();	
		int char1 = AllocateEntity(current_map.startX*16, current_map.startY*16, "alis.chr");
		int char2 = AllocateEntity(current_map.startX*16, current_map.startY*16, "alis.chr");
		entitystalk(char1, player);
		entitystalk(char2, char1);
		//current_map.horizontalWrapable = current_map.verticalWrapable = true;
		
		//Ente[1]=EntitySpawn(gotox, gotoy, Name[Party[1]]+".CHR");
		//SetPlayer(EntitySpawn(gotox, gotoy, Name[Party[0]]+".CHR"));
	}

	static int _Ent_tmpvar;
	public static void EntStart() {

		_Ent_tmpvar = entity.get(event_entity).speed;
		entity.get(event_entity).speed=0;
		pauseplayerinput(); // rbp
	}
	
	public static void EntFinish() {
		entity.get(event_entity).speed = _Ent_tmpvar;
		unpauseplayerinput(); // rbp
	}	
	
	
}
