package ps;

import static core.Script.*;
import static ps.PS.*;

public class Palma {


	public static void mapinit()   {
		current_map.horizontalWrapable =
			current_map.verticalWrapable = true; 
		
		cameratracking = 1;
		
		setplayer(AllocateEntity(gotox*16, gotoy*16, "alis.chr"));
		render();fadein(30, false);

		System.out.println("Palma::mapinit");
	}
	
	public static void camineet() {
		System.out.println("Entrou em Camineet");
		/*while(!b1) {
			Render();
			rectfill(0, 0, 200, 200, 40, screen);
			showpage();
		}*/
		
		mapswitch("Camineet.map",32,14);
	}
	
	
}
