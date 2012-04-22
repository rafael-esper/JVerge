package tak;

import static core.Script.*;
import static core.Controls.*;

import java.awt.Color;
import java.awt.Font;

import core.VergeEngine;
import domain.CHR;
import domain.VImage;

public class AK extends VergeEngine {

	protected static Font sys_font = new Font("Monospaced", Font.BOLD, 12);
	
	public static void main(String args[]) {
		
		setSystemPath(new AK().getClass());
		initVergeEngine(args);
	}
	

	/*	STATE		COND		ACTION		FACE		DIRECTION
	0	Stopped		None		Normal		Left		Left
	1	Walking		Walking		Punching	Right		Right
	2   Jumping		Swimming	Trembling				Up
	3	Falling		Moto								Down
	4				Surf								Diagonal-DownLeft
	5				Heli								Diagonal-DownRight
	6				Fly
	7				Star
	8				Shinobi Walk
	9				Shinobi Swim
	*/

	static final int SPEED = 3;
	static final int FALL = 6;		// air resistance. The higher, slower the falling
	static final int GRAV = 5;		// gravity velocity. The higher, faster the falling
	static final int GRAV_EF = 50;	// gravity threshold
	static final int MAXVEL = 15;	// maximum walking velocity
	static final int MAXJUMP = 48;	// maximum jump height
	static final int FRIC_NOR = 5;	// normal friction
	static final int FRIC_ICE = 10;	// ice friction
	static final int MAXSWIM = 8;	// normal swim velocity
	static final int MAXRSWIM = 12;	// fast swim velocity
	static final int MAXHELI = 12;
	static final int MAXFLY = 8;
	static final int MAXSTAR = 24;

	static final int MINMOTO = 12;
	static final int MAXMOTO = 26;
	static final int ALTMOTO = 56;	// altura máxima de pulo com moto

	static final int MINSURF = 10;
	static final int MAXSURF = 25;
	static final int ALTSURF = 50;	// altura máxima de pulo com lancha

	static final int COND_WALK = 1;
	static final int COND_SWIM = 2;
	static final int COND_MOTO = 3;
	static final int COND_SURF = 4;
	static final int COND_HELI = 5;
	static final int COND_FLY  = 6;
	static final int COND_STAR = 7;
	static final int COND_ROPE = 8;
	static final int COND_SHIW = 9;
	static final int COND_SHIS = 10;

	static final int S_STOPPED = 0;
	static final int S_WALKING = 1;
	static final int S_JUMPING = 2;
	static final int S_FALLING = 3;
	static final int S_DUCKING = 4;
	
	static final int EAST = 0;
	static final int WEST = 1;
	static final int NORTH = 2;
	static final int SOUTH = 3;
	
	static final int DUST = 50;
	static final int BIGDUST = 51;

	static int zx,zy, inx,iny, gotox, gotoy; 
	static int State, Cond, Action, Gold, Prog=0, Slow=1, Energy, Lives;
	static int velocity, friction, vertical, zonecalled, playerframe, alt, maxvel, pdelay,tdelay,monsterframe,wind,inv,brac;
	static int akidd_px, akidd_py, akidd_vx, akidd_vy;
	static int player=0;
	static boolean debug;
	static VImage img=null;
	static VImage mapa, rock_t, rock_g, rock_c, leaf, brac0, brac1, firing;
	static String snd[] = new String[20];
	static int spx[] = new int [25]; // for rock fragments and other sprites, (x,y),energy and type
	static int spy[] = new int [25];
	static int spe[] = new int [25];
	static int spt[] = new int [25];
	
	static String Name, currentMusic, currentLevel;
	
	private static boolean changemap = false;

	static void Wait(int dela)
	{
		for(int a=0;a<dela;a++)
		{
			//timer = 0;
			//while(timer < 1) { 
				showpage(); //DefaultTimer(); //showpage();
			//}  
		}
	}

	public static void autoexec() 
	{
		setappname("Alex Kidd: Remake");
		
		VImage title = new VImage(load("res/image/title.png"));
		
		Color background = new Color(-86);

		timer = 0;
		while(!b1) {
			blit(0, 0, title, screen);
			
			if(timer < 75) {
				rectfill(210, 4, 295, 50, background, screen);
			}
			if(timer < 150) {
				rectfill(134, 134, 233, 169, background, screen);
			}
			if(timer < 225) {
				rectfill(32, 7, 76, 60, background, screen);
			}
			if(timer < 300) {
				rectfill(265, 72, 301, 156, background, screen);
			}
			if(timer < 375) {
				rectfill(25, 78, 108, 194, background, screen);
			}
			
			if(timer < 400 || timer%50 < 25)
				rectfill(88, 207, 228, 218, background, screen);
			
			showpage();
		}
		unpress(1);
		
		//Map("Level01.map");
		StartUp();
	}

	static void Mapswitch(String mapname, int x, int y, int ix, int iy, String music, int cnd, String level)
	{ 
		gotox=x;gotoy=y; // player coordinates
		inx=ix;iny=iy;	// map coordinates
		currentMusic=music;
		currentLevel = level;
		Cond=cnd;
		if(Prog > 0)
			changemap = true;
		map(mapname);
	}


	static void DoLevel() { // Hills, Lake, Field/Grass, Cave, Forest, Castle
		//if(Prog==1)  Mapswitch("Level30.map",1,34,277,190,"res/music/field.vgz", COND_WALK, "");	// Castle
		if(Prog<=1)  Mapswitch("Level01.map",2,4,277,190,"res/music/field.vgz", COND_WALK, "Mount Nibana");	// Hills
		if(Prog==2)  Mapswitch("Level02.map",2,5,290,176,"res/music/field.vgz", COND_WALK, "Lake Bimurai");	// Lake
		if(Prog==3)  Mapswitch("Level03.map",2,10,279,166,"res/music/field.vgz", COND_WALK, "");	// Field (C)
		if(Prog==4)  Mapswitch("Level04.map",8,15,291,143,"res/music/field.vgz", COND_WALK, "");	// Cave
		if(Prog==5)  Mapswitch("Level05.map",3,14,291,143,"res/music/field.vgz", COND_WALK, "");	// Wood (starting grass)
		if(Prog==6)  Mapswitch("Level06.map",2,14,270,121,"res/music/field.vgz", COND_WALK, "");	// Grass (City)
	
		if(Prog==7)  Mapswitch("Level07.map",2,3,290,186,"res/music/field.vgz", COND_WALK, ""); 	// Hills
		if(Prog==8)  Mapswitch("Level08.map",8,0,288,151,"res/music/swim.vgz", COND_SWIM, "");	// Lake
		if(Prog==9)  Mapswitch("Level09.map",2,14,283,132,"res/music/field.vgz", COND_WALK, "");	// Grass
		if(Prog==10) Mapswitch("Level10.map",8,73,234,105,"res/music/field.vgz", COND_WALK, "");	// Cave	(Mountain)
		if(Prog==11) Mapswitch("Level11.map",2,14,258,107,"res/music/field.vgz", COND_WALK, "");	// Wood
		if(Prog==12) Mapswitch("Level12.map",9,2,228,86,"res/music/field.vgz", COND_WALK, "");	// Fall
	
		if(Prog==13) Mapswitch("Level13.map",26,7,290,186,"res/music/field.vgz", COND_WALK, ""); 	// Hills
		if(Prog==14) Mapswitch("Level14.map",2,13,288,151,"res/music/swim.vgz", COND_SURF, "River Patarai");	// Lake
		if(Prog==15) Mapswitch("Level15.map",2,15,228,86,"res/music/field.vgz", COND_WALK, "");	// Grass
		if(Prog==16) Mapswitch("Level16.map",2,10,228,86,"res/music/field.vgz", COND_WALK, "");	// Cave (starting field)
		if(Prog==17) Mapswitch("Level17.map",2,174,228,86,"res/music/field.vgz", COND_WALK, "");	// Wood (vertical)
		if(Prog==18) Prog++;
	
		if(Prog==19) Mapswitch("Level19.map",1,12,228,86,"res/music/field.vgz", COND_WALK, "");	// Secret1 (eagle)
		if(Prog==20) Mapswitch("Level20.map",1,12,228,86,"res/music/swim.vgz", COND_SWIM, "");	// Secret2 (fish)
		if(Prog==21) Mapswitch("Level21.map",1,12,228,86,"res/music/field.vgz", COND_WALK, "");	// Secret3 (bull)
		if(Prog==22) Mapswitch("Level22.map",1,12,228,86,"res/music/field.vgz", COND_WALK, "");	// Secret3 (bat/owl)
	
		if(Prog==23) Mapswitch("MFase1.map",3,5,228,86,"res/music/field.vgz", COND_WALK, ""); 	// Hills Miracle World
		if(Prog==24) Mapswitch("MFase3.map",4,5,228,86,"res/music/swim.vgz", COND_SWIM, ""); 		// Lake Miracle World
		if(Prog==25) Mapswitch("MFase2.map",4,12,228,86,"res/music/field.vgz", COND_WALK, ""); 	// Field Miracle World
	
		if(Prog==26) exit("Thanks for playing...");              // Palace (C)
		
		if(Prog==0) Prog++; // for start-up purposed
		Prog++;
	}

	static void StartUp() 
	{
		
		rock_t=new VImage(load("res/image/rock_t.gif"));
		rock_g=new VImage(load("res/image/rock_g.gif"));
		rock_c=new VImage(load("res/image/rock_c.gif"));	
		leaf=new VImage(load("res/image/leaf.gif"));
		brac0=new VImage(load("res/image/brac0.gif"));
		brac1=new VImage(load("res/image/brac1.gif"));
		firing=new VImage(load("res/image/firing.gif"));
		mapa=new VImage(load("res/image/Mapa.gif"));
		
		
		snd[1]="res/sound/Mapa.mp3";  
		snd[2]="res/sound/Gold.wav";
		snd[3]="res/sound/Punch.wav"; 
		snd[4]="res/sound/Rock.wav";
		snd[5]="res/sound/Star.wav";
		snd[6]="res/sound/Death.wav";
		snd[7]="res/sound/Hit.wav";
		snd[8]="res/sound/Brac.wav";
		snd[9]="res/sound/Item.wav";
		snd[10]="res/sound/Water.wav";		

		Name="akidd.chr";
		
		DoLevel();
	}

	static void showmapscreen() 
	{
	  int b=0;
	  playsound(snd[1]);
	  Wait(20);
	  
	  while(!b1) 
	  {
		if(b>12) b=0;
		b++;
		rectfill(0,0,320,240,Color.BLACK, screen);
		blit(0,0,mapa,screen);
		drawText(10, 225, "Level " + (Prog-1) + ": " + currentLevel);
		
		if(b<6) circlefill(inx,iny,b,b, Color.RED,screen);
		if(b>=6) circlefill(inx,iny,10-b,10-b,Color.RED,screen);
		Wait(1);showpage();
	  }
	}

	static int SelectLevel(int sx, int sy) 
	{
		int bx = 0,by = 0;
		while(true) 
		{
			if(left) { unpress(7);bx--;} 
			if(right) { unpress(8);bx++;}
			if(up) { unpress(5);by--;} 
			if(down) { unpress(6);by++;}
			if(b1) return (by*sx)+bx+1;
			if(bx<0) bx=sx-1;
			if(bx>=sx) bx=0;
			if(by<0) by=sy-1;
			if(by>=sy) by=0;
		
			rectfill(100,100,100+(20*sx),105+(20*sy), Color.BLACK,screen);
			rect(99,99,100+(20*sx),105+(20*sy), Color.WHITE,screen);
			rect(98,98,101+(20*sx),106+(20*sy), Color.WHITE,screen);
			for(int i=0;i<sx;i++)
			{
				for(int j=0;j<sy;j++)
					drawText(105+(i*20),110+(j*20),str((j*sx)+i+1));
			}
			drawText(105+(bx*20),115+(by*20),"=");
			showpage();
		}
	}

	static void CallEvent(int num)
	{
		switch(num)
		{
			case 1: // Gold I
			settile(zx, zy, 1, 0);setzone(zx, zy, 0);
			playsound(snd[2]);Gold+=20;
			break;
		
			case 2: // Gold II
			settile(zx, zy, 1, 0);setzone(zx, zy, 0);
			playsound(snd[2]);Gold+=10;
			break;
			
	 		case 3: // Rock
	 		playsound(snd[4]);
			if(gettile(zx, zy, 1)==32 || gettile(zx, zy, 1)==52) AddSprite(zx<<4,zy<<4,3); // cave rock
			else if(gettile(zx, zy, 1)==65 || Cond==COND_SWIM) AddSprite(zx<<4,zy<<4,2); // sea rock
			else  AddSprite(zx<<4,zy<<4,1); // common rock
			settile(zx, zy, 1, 0);
			setzone(zx, zy, 0);
			setobs(zx, zy, 0);
			break;
			
			case 4: // Star
			playsound(snd[5]);
			setobs(zx, zy, 0);
			if(random(0,1)==0) { settile(zx, zy, 1, 12);setzone(zx, zy, 1);}
			else { settile(zx, zy, 1, 13);setzone(zx, zy, 2);}
			AddSprite(zx<<4,zy<<4,Cond);
			break;
		
			case 5: // Rice
			settile(zx, zy, 1, 0);
			setzone(zx, zy, 0);
			DoLevel();
			break;
			
			case 6: // Swim
			if(Cond!=COND_SWIM && Cond!=COND_STAR) 
			{ 
				Cond=COND_SWIM;
				State=S_STOPPED;
	 			stopmusic();
	 			playsound(snd[10]);
	 			vertical=0; alt=0; brac=0;
				for(int j=0;j<12;j++)
				{
					entity.get(player).incy(2);
					entity.get(player).specframe=Showplayer(); 
					ProcessEnemies();ProcessSprites();ProcessMisc();
					render();showpage();
					Wait(1);
				}
				currentMusic="res/music/swim.vgz";
				playmusic(currentMusic);
			}
			break;

	 		case 7: // Item (CHANGE!)
	 		playsound(snd[4]);
			settile(zx, zy, 1, 0);
			setzone(zx, zy, 0);
			setobs(zx, zy, 0);
			AddSprite(zx<<4,zy<<4,Cond);
			break;

			case 8: // Skull
	 		playsound(snd[4]);
			settile(zx, zy, 1, 0);
			setzone(zx, zy, 0);
			setobs(zx, zy, 0);
			AddSprite(zx<<4,zy<<4,Cond);
			if (brac==0) Action=2;
			break;
			
			case 9: // Getkilled
			GetKilled(2);
			break;

			case 10: // Air Wind In
			wind=4;
			break;

			case 11: // Air Wind Out
			wind=0;
			break;
			
			case 12: // Stair
			/*if(up || down)
			{
				entity.get(player).x = zx*16;
				//entity.get(player).x = zx*16;
				State=S_STOPPED;
				Cond = COND_ROPE;
			}*/
			
	 	}
	}

	public static void game() 
	{
	showmapscreen();
	if(Prog==0) StartUp();

	// This changes all entities to load the CHR info from an image file
	CHR c = CHR.createCHRFromImage(32, 32, 8, 56, true, new VImage(load("monster_newt.png")));
	for(int i=0; i<numentities; i++) {
		entity.get(i).chr = c;
	}
	
	
	 player = entityspawn(gotox, gotoy, Name);
	 setplayer(player);
	 //cameratracking = 1;
	 NormalCondition(Cond);
	 
	 entity.get(player).setx(gotox<<4);
	 entity.get(player).sety(gotoy<<4);
	 
	 while(true)     
	 {
		 //if(timer>(4-Fast))   
	 	//{ 
		 if(changemap) {
			 changemap = false;
			 break;
		 }
		 
		 	Wait(Slow);
			 //timer=0;
	 		render();ControlKeys();ProcessZones();
	 		if(Cond==COND_WALK || Cond==COND_MOTO || Cond==COND_SURF || Cond==COND_SHIW) 
	 			MovePlayer(); 
	 		else 
	 			Swimplayer();
	 		if (Cond!=COND_SHIW && Cond!=COND_SHIS)
	 			entity.get(player).specframe=Showplayer(); 
	 		else
	 			entity.get(player).specframe=Showplayer(); // Shinobi
	 		ProcessEnemies();ProcessSprites();ProcessMisc();
	 		showpage();
	  	
	 	//incrementtimers();
	  }
	}


	static void NormalCondition(int Cnd)
	{
		unpress(0);
		Cond=Cnd;
		State=S_STOPPED; 
		Action=0; wind=0;
	 	vertical=0; alt=0; brac=0;
	 	entity.get(player).speed=0;
	 	velocity=0;pdelay=0;tdelay=0;timer=0;
		friction = FRIC_NOR;
	 	entity.get(player).face=1;Energy=3;
	 	playmusic(currentMusic);
	}

	static void ControlKeys() 
	{
	 UpdateControls();

	 if(getkey(SCAN_1)) Slow=0;
	 if(getkey(SCAN_2)) Slow=1;
	 if(getkey(SCAN_3)) Slow=2;
	 if(getkey(SCAN_4)) Slow=3;
	 if(getkey(SCAN_A)) {debug=true; while(!b1) UpdateControls();}
	 if(getkey(SCAN_B)) brac=1;
	 if(getkey(SCAN_F)) {Cond=COND_FLY;State=S_STOPPED;}
	 if(getkey(SCAN_H)) {Cond=COND_HELI;playmusic("res/music/swim.vgz");} // Heli.mp3
	 if(getkey(SCAN_I)) inv=100000;
	 if(getkey(SCAN_K)) GetKilled(2);
	 if(getkey(SCAN_L)) { Prog=SelectLevel(6, 4);DoLevel();}
	 if(getkey(SCAN_M)) {Cond=COND_MOTO;playmusic("res/music/Moto.vgz");}
	 if(getkey(SCAN_N)) { NormalCondition(COND_WALK);}
	 if(getkey(SCAN_P)) {
	  //copyimagetoclipboard(screen);
	  img = new VImage(current_map.getWidth()*16, current_map.getHeight()*16);
	  rendermap(xwin, ywin, img);
	  copyimagetoclipboard(img);
	 }
	 //if(getkey(SCAN_R]) setRandomAlex(1);
	 if(getkey(SCAN_S)) {Cond=COND_SURF;playmusic("res/music/swim.vgz");}
	 if(getkey(SCAN_X)) Cond=COND_STAR;
	 //if(getkey(SCAN_Z)) {Cond=COND_SHIW;changeCHR(player, "shinobi.chr");}
	 
	 if(right && left && State==S_WALKING) { State=S_STOPPED;velocity=0;}

	 switch(Cond)
	 {
	 	case COND_WALK:
	 	ControlWalk();
	 	break;
	 	
	 	case COND_SWIM:
	 	ControlSwim();
	 	break;
	 	
	 	case COND_MOTO:
	 	ControlVehicle(MINMOTO, MAXMOTO, ALTMOTO);
	 	vehicleAttack();
	 	break;

	 	case COND_SURF:
	 	ControlVehicle(MINSURF, MAXSURF, ALTSURF);
	 	break;
	 	
	 	case COND_HELI:
	 	ControlSwim();
	 	break;
	 	
	 	case COND_FLY:
	 	ControlSwim();
	 	break;
	 	
	 	case COND_STAR:
	 	ControlSwim();
	 	vehicleAttack();
	 	break;
	 	
	 	case COND_ROPE:
	 	ControlRope();
	 	break;
	 	
	 	case COND_SHIW:
	 	ControlWalk();
	 	break;
	 }

	  if(Cond!=COND_MOTO && Cond!=COND_STAR && Cond != COND_ROPE)
	  {
	 	if(b1 || pdelay>0)  // punch, bracelete, tiro (button b1)
	 	{
	 		if(tdelay==0) punch();
	 		if(pdelay==2)
	 		{
	 			if(brac==1 && Cond==COND_WALK) AddSprite(entity.get(player).getx()+(entity.get(player).face*30), entity.get(player).gety()+14, 12+entity.get(player).face); // bracelete
	 			if(Cond==COND_HELI || Cond==COND_SURF) AddSprite(entity.get(player).getx()+(entity.get(player).face*30), entity.get(player).gety()+14, 14+entity.get(player).face); // tiro
	 		}
	 	}
	 	if(!left && !right && State==S_WALKING && Cond!=COND_SURF) { State=S_STOPPED; velocity=friction*velocity/10; }
		if(Action==2 || tdelay>0) tremble();
	  }

	}

	static void ControlWalk()// %%%%%%%%%%%%%%%%%% WALKING %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
	{
	  if (Action!=2) // not trembling
	  {
	 	if (State!=S_DUCKING)
	 	{
	 	    if (right) // && Cond==COND_WALK) 
	 	    {   
	 	    	velocity+=10*SPEED/friction;
	 	    	if(State==S_STOPPED) 
	 	    		State=S_WALKING; 
	 	    	if(entity.get(player).face!=1) 
	 	    	{ 
	 	    		entity.get(player).face=1;
	 	    		velocity=friction*velocity/10;
	 	    	}
	 	    }
	 	    if (left) // && Cond==COND_WALK)  
	 	    {   
	 	    	velocity-=10*SPEED/friction;
	 	    	if(State==S_STOPPED) 
	 	    		State=S_WALKING; 
	 	    	if(entity.get(player).face!=0) 
	 	    	{ 
	 	    		entity.get(player).face=0;
	 	    		velocity=friction*velocity/10; 
	 	    	}
	 	    }
	 	}
	 	if(up && ProcessZones()==12)
	 		Cond = COND_ROPE;

	 	if(down && velocity==0)
	 		State=S_DUCKING; 
	 	else if(!down && State==S_DUCKING) 
	 		State=S_STOPPED;
	  }

	 CheckJumpFalling(MAXJUMP);
	 VelocityCheck(MAXVEL);
	} 

	static void ControlRope() // %%%%%%%%%%%%%%%%%% ROPE, up or down %%%%%%%%%%%%%%%%%%%%%%%
	{
		velocity = 0;
		
		 if(ProcessZones()!=12)
		 {
		 	Cond = COND_WALK;
			return;
		 }
		
		if(right || left)
			Cond = COND_WALK;

		if(up)
		{
			State = S_JUMPING;
			vertical--;
		}
		if(down)
		{
		 	if(getObsd(SOUTH)==0)
		 	{
				State = S_FALLING;
				vertical++;
			}
			else
			{
				State = S_STOPPED;
				vertical = 0;
			}
		}
		
		if(!up && !down)
		{
			State = S_STOPPED;
			vertical = 0;
		}
		
		if(vertical>3) vertical=3;
		if(vertical<-3) vertical=-3;

	}

	static void ControlSwim() // %%%%%%%%%%%%%%%%%% SWIMMING, HELI AND STAR %%%%%%%%%%%%%%%%%%%%%%%
	{
	   	if(Action==2) return;

		if (right) {   velocity+=SPEED; if(entity.get(player).face!=1) { entity.get(player).face=1;velocity=velocity>>2; }}
		if (left)  {   velocity-=SPEED; if(entity.get(player).face!=0) { entity.get(player).face=0;velocity=velocity>>2; }}
		
		if (up) vertical-=(Cond/2);
		if (down)
		{
			if(Cond==COND_SWIM || Cond==COND_FLY)  vertical+=2;
			if(Cond==COND_HELI)  vertical++;
			if(Cond==COND_STAR)  vertical+=3;
		}
		if (!right && velocity>0) velocity--; 
		if (!left && velocity<0) velocity++;
		if(!up && vertical<0) vertical=0;
	    
	 	// Destroy vehicle
	 	if(Cond==COND_HELI && getObsd(NORTH)!=0)
		{
				AddSprite(entity.get(player).getx()-24+entity.get(player).face*32, entity.get(player).gety(),4);
				NormalCondition(COND_WALK);
				State=S_JUMPING;
				entity.get(player).incy(16);
				return;
		}
		
		if(Cond==COND_SWIM)
		{
			if(vertical<=-3) vertical=-2;
			if(!down && vertical>0) vertical--;
			if(b1 && Action==0) VelocityCheck(MAXRSWIM);
			else VelocityCheck(MAXSWIM);
		}
		else if(Cond==COND_HELI) VelocityCheck(MAXHELI);
		else if(Cond==COND_FLY ) {VelocityCheck(MAXFLY);vertical=sgn(vertical);}
		else if(Cond==COND_STAR) VelocityCheck(MAXSTAR);
		if(vertical>3) vertical=3;
		if(vertical<-3) vertical=-3;
	}

	static void ControlVehicle(int minVehicle, int maxVehicle, int altVehicle) // %%%%%%%%%%% VEHICLE: MOTO/SURF %%%%%%%%%%%%%%%%
	{
		if(State==S_STOPPED) 
			State=S_WALKING;
		if(velocity==0)
		{
			if(entity.get(player).face==0)
				velocity=-minVehicle;
			if(entity.get(player).face==1)
				velocity=minVehicle;	
		}		

	 	if (right && velocity > 0) velocity+=SPEED;
	 	if (down && velocity > 0) velocity-=SPEED;
	 	
	 	if (down && velocity < 0) velocity+=SPEED;
	 	if (left && velocity < 0) velocity-=SPEED;

	 	// Invert direction
	 	if (left && velocity > 0 && State==S_WALKING) {
	 		velocity = -velocity;
	 		entity.get(player).face = 0;
	 	} else if (right && velocity < 0 && State==S_WALKING) {
	 		velocity = -velocity;
	 		entity.get(player).face = 1;
	 	}
	 
	 	// Destroy vehicle
	 	if(getObsd(entity.get(player).face)!=0)
		{
			if(Getpunch(entity.get(player).face*32)==0) {
				
				AddSprite(entity.get(player).getx()-24+entity.get(player).face*32, entity.get(player).gety(),4);

				if(Cond==COND_MOTO) {
					NormalCondition(COND_WALK);
					State=S_JUMPING;
					return;
				}
				else {
					CallEvent(6);
					entity.get(player).incy(54);
					return;
				}
				
			}
		}

		CheckJumpFalling(altVehicle +abs(velocity) - maxVehicle);

		if(abs(velocity)>(maxVehicle)) velocity=sgn(velocity)*maxVehicle;
		if(abs(velocity)<(minVehicle)) velocity=sgn(velocity)*minVehicle;
	}

	static void VelocityCheck(int maxv)
	{
	 if(velocity>maxv) velocity=maxv;
	 if(velocity<-maxv) velocity=-maxv;
	}

	static void CheckJumpFalling(int MaxAlt)
	{
		if(Cond==COND_ROPE)
			return;
			
		if(Action!=2 && up) // not trembling and not upRope
		{
	 		if(State!=S_JUMPING && State!=S_FALLING && getObsd(SOUTH)!=0) { State=S_JUMPING; vertical=0; alt=MaxAlt;}
	 		if(State==S_JUMPING && alt>-20) 
	 		{   
	 			vertical-=alt/FALL;
	 			if(vertical<-MaxAlt) vertical=-MaxAlt;
	 		}
	 		if(alt>0) {  alt-=FALL; } else  {  State=S_FALLING; alt=0; unpress(5);}    
		}

	 	if(getObsd(SOUTH)==0 && State!=S_JUMPING) 
	 	{
	  		State=S_FALLING;  
	  		if(vertical<GRAV_EF) vertical+=(GRAV+1);
	 	} 
		 if(!up && State==S_JUMPING) {  State=S_FALLING; alt=0; }
	}

	static void MovePlayer() 
	{
	 entity.get(player).incy(-wind);
	 if(State!=S_WALKING && Cond!=COND_MOTO && Cond!=COND_SURF) {  if(velocity>0) velocity--; if(velocity<0) velocity++; }

	 for(int i=0; i<abs(velocity>>2); i++)
	 {
	  if(getObsd(entity.get(player).face)==0) entity.get(player).incx(sgn(velocity));
	 }

	 if(getObsd(NORTH)!=0 && State==S_JUMPING) { State=S_FALLING; vertical=0; }
	 if(getObsd(SOUTH)!=0 && State==S_FALLING) { State=S_STOPPED; vertical=0; if(Cond!=COND_MOTO && Cond!=COND_SURF) velocity=0;  }
	 
	 if(State==S_JUMPING) 
	 {  for(int i=0; i<abs(vertical); i+=FALL)  
	  	{    if(getObsd(NORTH)==0) entity.get(player).incy(sgn(vertical)); }
	 }

	 if(State==S_FALLING)
	 {
	  	for(int i=0; i<abs(vertical); i+=FALL)   
	  	{
	    		if(sgn(vertical)==1 && getObsd(SOUTH)==0) entity.get(player).incy();
	    		if(sgn(vertical)==-1 && getObsd(NORTH)==0) entity.get(player).incy(-1);
	    	}  
	 }
	}

	static void Swimplayer() 
	{
	 int aa = 0; 
	 if(Cond==COND_SWIM && getObsd(NORTH)==0) entity.get(player).incy(-1);
	 if(Cond==COND_HELI && getObsd(SOUTH)==0) entity.get(player).incy();

	 for(int i=0; i<abs(velocity>>2); i++) {
	 if(getObsd(entity.get(player).face)==0) entity.get(player).incx(sgn(velocity)); }

	 if(vertical>0) aa=SOUTH;
	 if(vertical<0) aa=NORTH;
	 for(int i=0; i<abs(vertical); i++) {
		 if(getObsd(aa)==0) entity.get(player).incy(sgn(vertical)); }

	}

	static int Showplayer()
	{
		playerframe++;
		if(playerframe>=6) playerframe=0;

		if(Cond==COND_ROPE)
		{
			setDimensions(entity.get(player).getx()+12, entity.get(player).gety()+6, 8, 20);
			if (State == S_STOPPED)
				return 42;
			else
				return 41+(playerframe/2);
		}

		if(Cond==COND_HELI) 
		{
			setDimensions(entity.get(player).getx()+6, entity.get(player).gety()+4, 20, 26);
			return 31+((1-entity.get(player).face)*4)+(playerframe/3);
		}

		if(Cond==COND_SURF) return 27+((1-entity.get(player).face)*2)+(playerframe/3);

		if(Cond==COND_MOTO)
		{
			if(State<=1)
			{
				setDimensions(entity.get(player).getx()+9, entity.get(player).gety()+7, 14, 20);
				return 21+((1-entity.get(player).face)*3)+(playerframe/3);
			}
			else
			{
				setDimensions(entity.get(player).getx()+9, entity.get(player).gety()+5, 14, 26);
				return 23+((1-entity.get(player).face)*3);
			}
		}
		if (Action==1) // punching
		{
			if(Cond==COND_SWIM) 
			{
				setDimensions(entity.get(player).getx()+10, entity.get(player).gety()+11,13,12);
				return 17-(entity.get(player).face*3); // swimming
			}
			else
			{
				setDimensions(entity.get(player).getx()+12, entity.get(player).gety()+6, 8, 20);
				return 5-entity.get(player).face; // walking
			}
		}

		if(State==S_STOPPED)
		if(Cond==COND_WALK || Cond==COND_FLY)  // idle
		{
			setDimensions(entity.get(player).getx()+12, entity.get(player).gety()+6, 8, 20);
			return 1-entity.get(player).face;
		}
		
		if(State==S_DUCKING || Cond==COND_STAR) // ducking
		{
			setDimensions(entity.get(player).getx()+12, entity.get(player).gety()+12, 8, 16);
			return 40-entity.get(player).face;
		}
		
		if(Cond==COND_WALK && State==S_WALKING) // running
		{
				setDimensions(entity.get(player).getx()+12, entity.get(player).gety()+6, 8, 20);
				return 9-(3*entity.get(player).face)+(playerframe>>1); 
		}
		
		if(Cond==COND_SWIM) // swimming
		{
			setDimensions(entity.get(player).getx()+10, entity.get(player).gety()+11,13,12);
			return 15-(3*entity.get(player).face)+(playerframe/3);
		}

		if(Cond==COND_WALK && State>=2 && State<=3)  //jumping or falling
		{
			setDimensions(entity.get(player).getx()+10, entity.get(player).gety()+8, 12, 20);
			return 3-entity.get(player).face;
		}
		
		return 0;
	}

	int ShowShinobiplayer()
	{
		playerframe++;
		if(playerframe>=6) playerframe=0;

		if (Action==1) // punching
		{
			if(Cond==COND_SHIS) 
			{
				setDimensions(entity.get(player).getx()+10, entity.get(player).gety()+11,13,12);
				return 15-(entity.get(player).face*3); // swimming
			}
			else
			{
				setDimensions(entity.get(player).getx()+16, entity.get(player).gety()+12, 8, 24);
				return 55-(entity.get(player).face*6); // walking
			}
		}

		if(State==S_STOPPED && Cond==COND_SHIW) // idle
		{
			setDimensions(entity.get(player).getx()+16, entity.get(player).gety()+12, 8, 24);
			return (1-entity.get(player).face)*2;
		}
		
		if(State==S_DUCKING) // ducking
		{
			setDimensions(entity.get(player).getx()+12, entity.get(player).gety()+12, 8, 16);
			return 40-entity.get(player).face;
		}
		
		if(Cond==COND_SHIW && State==S_WALKING) // running
		{
				setDimensions(entity.get(player).getx()+16, entity.get(player).gety()+12, 8, 24);
				return (1-entity.get(player).face)*2+(playerframe/3);
		}
		
		if(Cond==COND_SHIS) // swimming
		{
			setDimensions(entity.get(player).getx()+10, entity.get(player).gety()+11,13,12);
			return 15-(3*entity.get(player).face)+(playerframe/3);
		}

		if(Cond==COND_SHIW && State>=2 && State<=3)  //jumping or falling
		{
			setDimensions(entity.get(player).getx()+12, entity.get(player).gety()+12, 10, 20);
			return 7-entity.get(player).face; //+((State-2)*2);
		}
		
		return 0;
	}

	static void ProcessMisc()
	{
		drawText(10,10, "$ "+Gold);
		//drawText(0,230,"Cond:"+str(Cond)+" State:"+str(State)+" Face:" + str(entity.get(player).face) + " Velocity: " + str(velocity));
		
		for(int i=0;i<Energy;i++)
		{
			rectfill(316-(i*12),4,307-(i*12),9,new Color(0,0,0),screen);
			rectfill(315-(i*12),5,308-(i*12),8,new Color(30,250,50),screen);
			rect    (316-(i*12),4,307-(i*12),9,new Color(50,250,50),screen);
		}
		//Invencible
		if(inv>0) 
		{
			inv--;
			if(inv%2==0)  entity.get(player).specframe=44; // invencible (invisible)
		}
	}



	static void setDimensions(int px, int py, int vx, int vy)
	{
	    	akidd_px = px;
	    	akidd_py = py;
	    	akidd_vx = vx;
	    	akidd_vy = vy;	
	}

	static int getObsd(int direction) 
	{
	 int a,ho=0,vo=0;
	 if(Cond==COND_STAR) vo=6;
	 if(Cond==COND_MOTO || Cond==COND_SURF) ho=4;
	 if(Cond==COND_WALK || Cond==COND_MOTO || Cond==COND_SURF || Cond==COND_STAR || Cond==COND_FLY || Cond==COND_ROPE) // normal
	 {
		if(direction==EAST) { for(a=7+vo; a<28; a+=2) { if(getobspixel((entity.get(player).getx()+8), entity.get(player).gety()+a)) return 1;  }} // left
		if(direction==WEST) { for(a=7+vo; a<28; a+=2) { if(getobspixel((entity.get(player).getx()+24), entity.get(player).gety()+a)) return 1;  }} // right
		
		if(direction==NORTH && Cond==COND_ROPE && ProcessZones()==12) return 0; // end of stair
		if(direction==NORTH) { for(a=11; a<20; a+=2) { if(getobspixel((entity.get(player).getx()+a), entity.get(player).gety()+(6+vo))) return 1; }} // up

		if(direction==SOUTH) { for(a=11-ho; a<20+ho; a+=2) { if(getobspixel((entity.get(player).getx()+a), entity.get(player).gety()+(28))) return 1;}} // down
		if(direction==SOUTH && Cond==COND_SURF) { for(a=11-ho; a<20; a+=2) { if(getzone((entity.get(player).getx()+a)>>4, (entity.get(player).gety()+28)>>4)==6) return 1; }}

	    	if(direction==4) { for(a=11; a<20; a+=2) { if(getobspixel((entity.get(player).getx()+a-6), (entity.get(player).gety()+28+6))) return 1;}} // face0 + lack of floor
	    	if(direction==5) { for(a=11; a<20; a+=2) { if(getobspixel((entity.get(player).getx()+a+16), (entity.get(player).gety()+28+6))) return 1;}} // face1 + lack of floor

	 }
	 else if(Cond==COND_SWIM) //swimming
	 {
		//if(direction==1 && getobspixel(entity.get(player).x, entity.get(player).y+24)) return 1;
		if(direction==EAST) { for(a=12; a<24; a+=2)  { if(getobspixel((entity.get(player).getx()+7), entity.get(player).gety()+a)) return 1;  }}
		if(direction==WEST) { for(a=12; a<24; a+=2)  { if(getobspixel((entity.get(player).getx()+25), entity.get(player).gety()+a)) return 1;  }}
		if(direction==NORTH) { for(a=8; a<22; a+=2) { if(getzone((entity.get(player).getx()+a)>>4, (entity.get(player).gety()+8)>>4)==6) return 1; }}
		if(direction==NORTH) { for(a=8; a<26; a+=2) { if(getobspixel((entity.get(player).getx()+a), entity.get(player).gety()+(8))) return 1; }}
		if(direction==SOUTH) { for(a=8; a<26; a+=2) { if(getobspixel((entity.get(player).getx()+a), entity.get(player).gety()+(25))) return 1;}}
	 }
	 else if (Cond==COND_HELI) // helicopter
	 {
	 	if(direction==EAST) { for(a=4; a<31; a+=2) { if(getobspixel((entity.get(player).getx()+4), entity.get(player).gety()+a)) return 1;  }}
		if(direction==WEST) { for(a=4; a<31; a+=2) { if(getobspixel((entity.get(player).getx()+27), entity.get(player).gety()+a)) return 1;  }}
		if(direction==NORTH) { for(a=5; a<27; a+=2) { if(getobspixel((entity.get(player).getx()+a), entity.get(player).gety()+(3))) return 1; }}
		if(direction==SOUTH) { for(a=5; a<27; a+=2) { if(getobspixel((entity.get(player).getx()+a), entity.get(player).gety()+(32))) return 1;}}
	  }
	  else if (Cond==COND_SHIW) // Shinobi Walking
	  {
	 	if(direction==EAST) { for(a=12; a<38; a+=2) { if(getobspixel((entity.get(player).getx()+12), entity.get(player).gety()+a)) return 1;  }}
		if(direction==WEST) { for(a=12; a<38; a+=2) { if(getobspixel((entity.get(player).getx()+27), entity.get(player).gety()+a)) return 1;  }}
		if(direction==NORTH) { for(a=14; a<24; a+=2) { if(getobspixel((entity.get(player).getx()+a), entity.get(player).gety()+(8))) return 1; }}
		if(direction==SOUTH) { for(a=14; a<24; a+=2) { if(getobspixel((entity.get(player).getx()+a), entity.get(player).gety()+(38))) return 1;}}  	
	  }
	 return 0;
	}

	static void punch() 
	{
		int ge,he = 0;
		if(State==S_WALKING)
			if(Cond==COND_WALK || Cond==COND_SHIW) velocity=friction*velocity/10;
		if(pdelay==0 && Cond!=COND_HELI && Cond!=COND_SURF) 
		{  
			if(brac==0) playsound(snd[3]);
			unpress(1);	Action=1;
			ge=Getpunch(entity.get(player).face*32);
			if(ge>=3 && ge<=8) // Eventos que são processados pelo soco
			{
				CallEvent(ge);
			}
		}
		pdelay++;
		if(Cond==COND_HELI || Cond==COND_SURF) he=9;
		if(pdelay>=6+he) 
		{ 
			pdelay=0;if(Action==1) Action=0;
		}
	}

	static int Getpunch(int HoOffset) // player is punching
	{
	   int a, UpOffset; 
	   UpOffset=16; 
	   if (Cond==COND_MOTO) UpOffset-=12;
	   zx=(entity.get(player).getx()+HoOffset)>>4;
	   for(a=UpOffset; a<28; a+=2)   
	   {   
	   	zy=(entity.get(player).gety()+a)>>4;
	   	if(getzone(zx,zy)>=3) return getzone(zx,zy);  // to avoid gold sacks
	   }
	 return 0;
	}

	static int ProcessZones()
	{
	  int a,b,c=0,z=0;

	  if(Cond==COND_WALK) c=8; //walking
	  if(Cond==COND_SWIM || Cond==COND_SHIW) c=12; // swimming
		for(a=13;a<17;a+=2) 
		{    
			for(b=c;b<26;b+=2)
			{
				zx=(entity.get(player).getx()+a)>>4; 
				zy=(entity.get(player).gety()+b)>>4;
				if(Action==1) zx=(entity.get(player).getx()+a+8)>>4; // Cond==COND_WALK && 
		 		z=getzone(zx,zy);
		 		if(z!=0 && zonecalled != z)   
		 		{ 
		   			zonecalled=z;  
	   				CallEvent(z);
		   			return z;
				}
		 		if(z==0 && zonecalled!=0)
		 			zonecalled=0;
		 	}
		}
		return z;
	}

	static void vehicleAttack() 
	{
		int ge;
		ge=Getpunch(entity.get(player).face*32);
		if(ge==0)
			ge=Getpunch(entity.get(player).face*40);
		
		if(ge>=3 && ge<=8) // Events processed by the vehicle
			CallEvent(ge);
	}

	static void tremble() 
	{
		velocity=0;Action=2;
		tdelay++;
		if(tdelay%2==0) 
			entity.get(player).incx();
		else
			entity.get(player).incx(-1);
		
		if(tdelay>=30) 
		{ 
			tdelay=0;if(Action==2) Action=0;
		}
	}

	static void ProcessEnemies() 
	{
	  int aa,cc;
	  monsterframe++;
	  if(monsterframe>=12) monsterframe=0;
	  
	  for(aa=0;aa<numentities;aa++) 
	  {
	    if(aa!=player && entity.get(aa).getx()>entity.get(player).getx()-300 && entity.get(aa).getx()<entity.get(player).getx()+300) 
	    {
		if (entity.get(aa).speed==50) // Dust
		{
			if(entity.get(aa).face==0) entity.get(aa).specframe=53; // small dust
			else if(entity.get(aa).face==1) entity.get(aa).specframe=54; // big dust
			else if(entity.get(aa).face==2) entity.get(aa).specframe=53; // small dust
			else { entity.get(aa).specframe=52; entity.get(aa).speed=0;entity.get(aa).face=0; }
			if(monsterframe==0) entity.get(aa).face++;
		}
		else if (entity.get(aa).speed==51) // Big Dust
		{
			if(entity.get(aa).face==0) entity.get(aa).specframe=53; // small dust
			else if(entity.get(aa).face==1) entity.get(aa).specframe=54; // big dust
			else if(entity.get(aa).face==2) entity.get(aa).specframe=55; // huge dust
			else if(entity.get(aa).face==3) entity.get(aa).specframe=54; // big dust
			else if(entity.get(aa).face==4) entity.get(aa).specframe=53; // small dust
			else { entity.get(aa).specframe=52; entity.get(aa).speed=0;entity.get(aa).face=0; }
			if(monsterframe==0) entity.get(aa).face++;
		}

		else if(entity.get(aa).speed==1) // Eagle
		{ 
			if(entity.get(aa).face>1) entity.get(aa).face=1;
			if(entity.get(aa).face==0) entity.get(aa).incx(-2);
			if(entity.get(aa).face==1) entity.get(aa).incx(2);
			if(Obstruct(aa,1,24,16)) entity.get(aa).face=0;
			if(Obstruct(aa,0,24,16)) entity.get(aa).face=1;
			entity.get(aa).specframe=(2-(entity.get(aa).face*2))+(monsterframe/6);
			if(Punched(aa,28,16)) KillThem(aa, DUST);
			if(akiddCollision(1,entity.get(aa).getx()+1, entity.get(aa).gety(), 22,14)) GetKilled(1);
		}
		
		else if (entity.get(aa).speed==2)  // Fish
		{
			if(entity.get(aa).face>1) entity.get(aa).face=1;
			if(entity.get(aa).face==0) entity.get(aa).incx(-2);
			if(entity.get(aa).face==1) entity.get(aa).incx(2);
			if(Obstruct(aa,1,16,14)) entity.get(aa).face=0;
			if(Obstruct(aa,0,16,14)) entity.get(aa).face=1;
			entity.get(aa).specframe=(6-(entity.get(aa).face*2))+(monsterframe/6);
			if(Punched(aa,18,18)) KillThem(aa, DUST);
			if(akiddCollision(1,entity.get(aa).getx()+1, entity.get(aa).gety(), 14,14)) GetKilled(1);
		}
		
		else if(entity.get(aa).speed==3)  // Scorpion
		{
			if(entity.get(aa).face>1) entity.get(aa).face=1;
			if(entity.get(aa).face==0) entity.get(aa).incx(-2);
			if(entity.get(aa).face==1) entity.get(aa).incx(2);
			if(!Obstruct(aa,3,14,16)) entity.get(aa).incy(2); // falling
			if(Obstruct(aa,5,14,14)) entity.get(aa).face=0;
			if(Obstruct(aa,4,14,14)) entity.get(aa).face=1;
			entity.get(aa).specframe=(10-(entity.get(aa).face*2))+(monsterframe/6);
			if(Punched(aa,14,16)) KillThem(aa, DUST);
			if(akiddCollision(1,entity.get(aa).getx(), entity.get(aa).gety(), 14,16)) GetKilled(1);
		}

		else if(entity.get(aa).speed==4)  // Frog
		{
			if(entity.get(player).getx()>entity.get(aa).getx()) cc = 0; else cc=1;
			if(entity.get(aa).face<=3)  // stopped
			{
				entity.get(aa).specframe=12+(2*cc);
				if(!Obstruct(aa,3,14,16)) entity.get(aa).face=7;
			}
			if(entity.get(aa).face==3 && monsterframe==0) entity.get(aa).incy(-10);
			else if(entity.get(aa).face>=4 && entity.get(aa).face<=5) // jumping
			{
				 entity.get(aa).incy(-(6-entity.get(aa).face));
				 entity.get(aa).specframe=13+(2*cc);
				 cc=10;
			}
			else if(entity.get(aa).face>=6 && entity.get(aa).face<=7) // falling
			{
				 entity.get(aa).incy(entity.get(aa).face-5);
				 entity.get(aa).specframe=13+(2*cc);
				 if(Obstruct(aa,3,12,26)) entity.get(aa).face=1;
				 cc=10;
			}
			if(entity.get(aa).face>=8) { entity.get(aa).incy(10);entity.get(aa).face=0;}
			if(monsterframe==0) entity.get(aa).face++;
			if(Punched(aa,14,26)) KillThem(aa, DUST);
			if(akiddCollision(1,entity.get(aa).getx(), entity.get(aa).gety()+10-cc, 14,24)) GetKilled(1);
		}

		else if(entity.get(aa).speed==5)  // Sea horse
		{
			entity.get(aa).specframe = 40 + monsterframe / 6;
			
			if(entity.get(aa).face == 0) entity.get(aa).incy();
			if(entity.get(aa).face == 1) entity.get(aa).incx(-1);
			if(entity.get(aa).face == 2) entity.get(aa).incx(-1);
			if(entity.get(aa).face == 3) entity.get(aa).incy();
			if(entity.get(aa).face == 4) entity.get(aa).incy(-1);
			if(entity.get(aa).face == 5) entity.get(aa).incx();
			if(entity.get(aa).face == 6) entity.get(aa).incx();
			if(entity.get(aa).face == 7) entity.get(aa).incy(-1);
			
			if(entity.get(aa).face >= 8) entity.get(aa).face = 0;
			
			if(monsterframe==0) entity.get(aa).face++;
			if(Punched(aa,11,15)) KillThem(aa, DUST);
			if(akiddCollision(1,entity.get(aa).getx(), entity.get(aa).gety(), 11,15)) GetKilled(1);
		}
	//6 piranha fish
		
		else if(entity.get(aa).speed==7) // Big Fish
		{
			if(entity.get(aa).face%2==0) entity.get(aa).incx(-1); 
			if(entity.get(aa).face%2==1) entity.get(aa).incx();
			if(entity.get(aa).face==0 || entity.get(aa).face==1) entity.get(aa).incy(3); 
			if(entity.get(aa).face==2 || entity.get(aa).face==3) entity.get(aa).incy(-3);
			if(Obstruct(aa,1,24,16)) entity.get(aa).face--;
			if(Obstruct(aa,0,24,16)) entity.get(aa).face++;
			if(monsterframe==0) entity.get(aa).face+=2;
			if(entity.get(aa).face>3) entity.get(aa).face=entity.get(aa).face%2;
			cc =(22-((entity.get(aa).face%2)*2))+(monsterframe/6);if(cc<0) cc=0;
			entity.get(aa).specframe=cc;
			if(Punched(aa,24,16)) KillThem(aa, BIGDUST);
			if(akiddCollision(1,entity.get(aa).getx(), entity.get(aa).gety(), 22,15)) GetKilled(1);
		}
		
		else if(entity.get(aa).speed==8 || entity.get(aa).speed==9) // Ghost and Fast Ghost
		{
			if(entity.get(aa).face>1) entity.get(aa).face=1;
			if(entity.get(player).getx()+4 <= entity.get(aa).getx())
			{
				entity.get(aa).face=0;
				entity.get(aa).incx(-(entity.get(aa).speed-7));
				entity.get(aa).specframe=34+(monsterframe/6);
			}
			if(entity.get(player).getx()-4 >= entity.get(aa).getx())
			{
				entity.get(aa).face=1;
				entity.get(aa).incx(entity.get(aa).speed-7);
				entity.get(aa).specframe=32+(monsterframe/6);
			}
			if(entity.get(player).gety()+4<=entity.get(aa).gety()) entity.get(aa).incy(-(entity.get(aa).speed-7));
			if(entity.get(player).gety()-4>=entity.get(aa).gety()) entity.get(aa).incy(entity.get(aa).speed-7);
			if(Punched(aa,14,16)) KillThem(aa, DUST);	
			if(akiddCollision(1,entity.get(aa).getx(), entity.get(aa).gety(), 14,16)) GetKilled(1);
		}
		
		else if(entity.get(aa).speed==10) // Bat
		{
			if(entity.get(aa).face>1) entity.get(aa).face=1;
			if(entity.get(aa).face==0 && !Obstruct(aa,0,15,12)) entity.get(aa).incx(-2);
			if(entity.get(aa).face==1 && !Obstruct(aa,1,15,12)) entity.get(aa).incx(2);
			if(Obstruct(aa,1,14,10)) entity.get(aa).face=0;if(Obstruct(aa,0,14,10)) entity.get(aa).face=1;
			if(monsterframe<6) entity.get(aa).incy(2);else entity.get(aa).incy(-2);
			entity.get(aa).specframe=30+(monsterframe/6);
			if(Punched(aa,15,16)) KillThem(aa, DUST); 
			if(akiddCollision(1,entity.get(aa).getx()+2, entity.get(aa).gety(), 12,8)) GetKilled(1);
		}
		
		else if(entity.get(aa).speed==11) //Owl
		{
			if(entity.get(aa).face>1) entity.get(aa).face=1;
			if(entity.get(aa).face==0 && !Obstruct(aa,0,15,16)) entity.get(aa).incx(-1);
			if(entity.get(aa).face==1 && !Obstruct(aa,1,15,16)) entity.get(aa).incx();
			if(Obstruct(aa,1,15,14)) entity.get(aa).face=0;if(Obstruct(aa,0,15,14)) entity.get(aa).face=1;
			if(!Obstruct(aa,3,15,16)) entity.get(aa).incy(2);
			entity.get(aa).specframe=28+(monsterframe/6);
			if(Punched(aa,15,15)) KillThem(aa, DUST); 
			if(akiddCollision(1,entity.get(aa).getx(), entity.get(aa).gety(), 15,15)) GetKilled(1);
		}

		else if(entity.get(aa).speed==12) //Monkey
		{
			if(entity.get(aa).face<=2) entity.get(aa).specframe=50; // idle
			if(entity.get(aa).face>2 && entity.get(aa).face<=4) entity.get(aa).specframe=51; // leaf prepare
			if(entity.get(aa).face==5) // leaf throw
			{
				entity.get(aa).face=0;
				if(entity.get(aa).getx()>entity.get(player).getx())
					AddSprite(entity.get(aa).getx(), entity.get(aa).gety(),10);
				else
					AddSprite(entity.get(aa).getx(), entity.get(aa).gety(),11);
			}		
			if (monsterframe==0) entity.get(aa).face++;
			if(Punched(aa,15,26)) KillThem(aa, BIGDUST); 	
			if(akiddCollision(1,entity.get(aa).getx(), entity.get(aa).gety(), 15,24)) GetKilled(1);		
		}

		else if(entity.get(aa).speed==13) // Strange monster
		{
			if(entity.get(aa).face==0 || entity.get(aa).face==2) entity.get(aa).incx(-1); 
			if(entity.get(aa).face==1 || entity.get(aa).face==3) entity.get(aa).incx();
			if(entity.get(aa).face==0 || entity.get(aa).face==1) if(!Obstruct(aa,3,16,16)) entity.get(aa).incy(2); //falling
			if(entity.get(aa).face==2 || entity.get(aa).face==3) if(!Obstruct(aa,2,16,16)) entity.get(aa).incy(-2); //jumping
			if(Obstruct(aa,1,14,14)) entity.get(aa).face--;
			if(Obstruct(aa,0,14,14)) entity.get(aa).face++;
			if(monsterframe==0)
			{
				if(Obstruct(aa,3,16,16) && entity.get(aa).face<2) entity.get(aa).face+=2;
				else entity.get(aa).face=entity.get(aa).face%2;
			}
			if(entity.get(aa).face<0 || entity.get(aa).face>3) entity.get(aa).face=entity.get(aa).face%2;
			cc =(18-((entity.get(aa).face%2)*2))+(monsterframe/6);if(cc<0) cc=52;
			entity.get(aa).specframe=cc;
			if(Punched(aa,16,16)) KillThem(aa, DUST);
			if(akiddCollision(1,entity.get(aa).getx(), entity.get(aa).gety(), 15,15)) GetKilled(1);
		}
		
		else if(entity.get(aa).speed==14) // Fire
		{
			if(monsterframe%2==0) entity.get(aa).incx((cos(systemtime)/24576));
			if(monsterframe%2==1) entity.get(aa).incy((sin(systemtime)/24576));
			cc=26+(monsterframe/6); if(cc<0)cc=52;
			entity.get(aa).specframe=cc;
			if(akiddCollision(1,entity.get(aa).getx(), entity.get(aa).gety(), 15,15)) GetKilled(3);
		}	
		
		else if(entity.get(aa).speed>=17 && entity.get(aa).speed<=25) // Bull
		{
			if(entity.get(aa).face>1) entity.get(aa).face=1;
			if(entity.get(aa).face==0) entity.get(aa).incx(-(entity.get(aa).speed-16)); else entity.get(aa).incx((entity.get(aa).speed-16));
			if(Obstruct(aa,5,32,24)) entity.get(aa).face=0;
			if(Obstruct(aa,4,32,24)) entity.get(aa).face=1;
			entity.get(aa).specframe=(46-(entity.get(aa).face<<1))+(monsterframe/6);
			if(Punched(aa,32,24)) 
			{
				entity.get(aa).face=entity.get(player).face;
				entity.get(aa).incx((entity.get(aa).face*24)-12);
				if(entity.get(aa).speed==25) KillThem(aa, BIGDUST);
				else {entity.get(aa).speed++;playsound(snd[7]);}
			}
			if(akiddCollision(1,entity.get(aa).getx(), entity.get(aa).gety()+10, 30,20)) GetKilled(1);		
		}
		
		else if(entity.get(aa).speed==26) // Bear
		{
			if(entity.get(aa).face>1) entity.get(aa).face=1;
			if(entity.get(player).getx()+8 <= entity.get(aa).getx())
			{
				entity.get(aa).face=0;
				entity.get(aa).incx(-1);
				entity.get(aa).specframe=0+(monsterframe/6);
			}
			if(entity.get(player).getx()-8 >= entity.get(aa).getx())
			{
				entity.get(aa).face=1;
				entity.get(aa).incx();
				entity.get(aa).specframe=3+(monsterframe/6);
			}
			//if(Punched(aa,36,64)) { changechr(aa, "monster.chr"); KillThem(aa, BIGDUST);}
			//if(akiddCollision(1,entity.get(aa).x+16, entity.get(aa).y+18, 22,46)) GetKilled(1);
		}
		
	    }
	  }
	}

	static void KillThem(int index, int dst)
	{
		entitystop(index);
		entity.get(index).face=0;
		entity.get(index).specframe=52;
		entity.get(index).speed=dst;
		playsound(snd[7]);
		// score+=
	}

	static boolean akiddCollision(int type, int mx, int my, int wx, int wy)
	{
	    //ho=0;if (State=S_DUCKING) ho+=8;
	    //px = entity.get(player).x+12;
	    //py = entity.get(player).y+6;
	    //vx = 8
	    //vy = 20
	    //if (type == 1) // passive
	    if(pdelay > 0) // When punching, make him invulnerable to collision: obs: leaf, etc, should hit 
	    	return false;
		return Collision(akidd_px,akidd_py,akidd_vx,akidd_vy,mx,my,wx,wy);
	    	
	}

	static boolean Collision(int px, int py, int vx, int vy, int mx, int my, int wx, int wy) // player is touched by a monster or sprite
	{
	    if(debug){
	    rect(mx-xwin,my-ywin,mx+wx-xwin,my+wy-ywin,new Color(200,100,100),screen);
	    rect(px-xwin,py-ywin,px+vx-xwin,py+vy-ywin,new Color(100,100,200),screen);}

	    // this formula assumes the rectangles do not intersect
	    if(mx > px+vx || mx+wx < px || my > py+vy || my+wy < py) return false;
	    else return true;
	}

	static boolean Punched(int ind, int wx, int wy) // player is punching a monster
	{
	  	int a;
	  	if(Action==1 || Cond==COND_MOTO || Cond==COND_STAR)
	  	{
		  	if((entity.get(player).getx()+(entity.get(player).face*24)) >= entity.get(ind).getx() && 
		  	   (entity.get(player).getx()+(entity.get(player).face*24)) <= entity.get(ind).getx()+wx &&
	  		   (entity.get(player).gety()+14) >= entity.get(ind).gety() && (entity.get(player).gety()+14) <= entity.get(ind).gety()+wy) return true;
	  	}
	  	if(brac==1 || Cond==COND_HELI || Cond==COND_SURF)
	  	{
	  		for(int i=0;i<20;i++)
			{
				if(spe[i]>0 && spt[i]>=12 && spt[i]<=15) // bracelete e tiro
	  				if(Collision(spx[i],spy[i],8,8,entity.get(ind).getx(),entity.get(ind).gety(),wx,wy)) {spe[i]=0; return true;}
			}
		}
	  	
	  	return false;   
	}

	static boolean Obstruct(int e, int face, int wx, int wy) 
	{
	    int a;
	    if(face==0 || face==4) { for(a=0; a<wy; a+=2) { if(getobspixel((entity.get(e).getx()), (entity.get(e).gety()+a))) return true;  }}
	    if(face==1 || face==5) { for(a=0; a<wy; a+=2) { if(getobspixel((entity.get(e).getx()+wx), (entity.get(e).gety()+a))) return true;  }}
	    if(face==2) { for(a=0; a<wx; a+=2) { if(getobspixel((entity.get(e).getx()+a), entity.get(e).gety())) return true; }}
	    if(face==3) { for(a=0; a<wx; a+=2) { if(getobspixel((entity.get(e).getx()+a), (entity.get(e).gety()+wy))) return true;}}
	    
	    if(face==4) { for(a=0; a<wx; a+=2) { if(!getobspixel((entity.get(e).getx()+a-4), (entity.get(e).gety()+wy+12))) return true;}} // face0 + lack of floor
	    if(face==5) { for(a=0; a<wx; a+=2) { if(!getobspixel((entity.get(e).getx()+a+6), (entity.get(e).gety()+wy+12))) return true;}} // face1 + lack of floor
	    return false;
	}

	static void GetKilled(int type) // 1 by monster, 2 naturally, 3 fire
	{
		int aa,bb = 0,cc,dd;
		if(inv>0 || Cond==COND_STAR) return; // Invencible state
		if(Cond==COND_MOTO && type==1) return;

		inv=120;
		if(Energy>0) Energy--;
		if(Energy==0 || type==2)
		{
			cc=entity.get(player).getx();
			dd=entity.get(player).gety();
			cameratracking=0;render();showpage();
			Wait(30);current_map.renderstring = "1,2,E";
			stopmusic();
			Wait(20);
			playsound(snd[6]);
			for(aa=0;aa<200;aa++) 
			{
				if(bb<6) entity.get(player).specframe=18;
				if(bb>5) entity.get(player).specframe=19;
				if(bb>=12) bb=0;
				Wait(1);entity.get(player).incy(-1);
				bb++;render();showpage();
			}
			Wait(50);
			cameratracking=1; current_map.renderstring = "1,E,2";
			entity.get(player).setx(cc);entity.get(player).sety(dd);
			if(Cond==COND_SWIM) NormalCondition(COND_SWIM);
			else NormalCondition(COND_WALK);
		}
	}

	static void AddSprite(int x, int y, int type)
	{
		int i=0;
		while(spe[i]>0 && i<24) // look for empty sprite
		{
			i++;
		}
		if(type<=9) // rock fragments
		{
			spx[i]=x;
			spy[i]=y;
			spe[i]=30;
			spt[i]=type;
		}
		if(type==10 || type==11) // leaf from monkey
		{
			spx[i]=x;
			spy[i]=y;
			spe[i]=60;
			spt[i]=type;
		}
		if(type==12 || type==13) // bracelete
		{
			spx[i]=x;
			spy[i]=y;
			spe[i]=30;
			spt[i]=type;
			playsound(snd[8]);
		}
		if(type==14 || type==15) // firing
		{
			spx[i]=x;
			spy[i]=y;
			spe[i]=12;
			spt[i]=type;
		}	
	}

	static void ProcessSprites()
	{
		VImage ptr; 
		int bb, rdc, ze;
		//wind
		if(wind>0) { ywin-=50;for(int i=0;i<40;i++) { bb=random(0,240);rdc=random(0,320);line(rdc,bb,rdc,bb-8,new Color(200,200,200),screen);}}
		
		for(int i=0;i<20;i++)
		{
			if(spe[i]>0)
			{
				spe[i]--;
				if(spt[i]==2) ptr=rock_g; // sea rock
				else if(spt[i]==3) ptr=rock_c; // cave rock
				else if(spt[i]==4) ptr=firing; // vehicle fragments
				else ptr=rock_t; // common rock
				if(spt[i]<=9) // rock fragment
				{
					tblit(spx[i]-xwin-(35-spe[i]),spy[i]-ywin,ptr,screen);
					tblit(spx[i]-xwin+5,spy[i]-ywin,ptr,screen);
					tblit(spx[i]-xwin-(35-spe[i]),spy[i]-ywin+12,ptr,screen);
					tblit(spx[i]-xwin+5,spy[i]-ywin+12,ptr,screen);
					spx[i]++;
					spy[i]+=6;
				}
				if(spt[i]==10 || spt[i]==11) // leaf
				{
					tblit(spx[i]-xwin-(120-(spe[i]*2)),spy[i]-ywin,leaf,screen);
					tblit(spx[i]-xwin+10,spy[i]-ywin,leaf,screen);
					if(akiddCollision(1,spx[i]-(120-(spe[i]*2)), spy[i], 8,6)) GetKilled(1);
					if(akiddCollision(1,spx[i]+10, spy[i], 8,6)) GetKilled(1);
					spx[i]+=spt[i]-9;
					spy[i]+=4;
				}
				if(spt[i]==12) // bracelete to the left
				{
					tblit(spx[i]-xwin,spy[i]-ywin,brac0,screen);
					zx=spx[i]>>4;zy=(spy[i])>>4; ze = getzone(zx,zy);
					if(ze==3 || ze==4 || ze==7 || ze==8) CallEvent(ze);
					else {	
						zx=spx[i]>>4;zy=(spy[i]+8)>>4;ze = getzone(zx,zy);
						if(ze==3 || ze==4 || ze==7 || ze==8) CallEvent(ze);
						else
							if(getobspixel(spx[i],spy[i]) || getobspixel(spx[i],spy[i]+8)) spe[i]=0;
					}
					spx[i]-=8;
				}
				if(spt[i]==13) // bracelete to the right
				{
					tblit(spx[i]-xwin,spy[i]-ywin,brac1,screen);
					zx=spx[i]>>4;zy=(spy[i])>>4; ze = getzone(zx,zy);
					if(ze==3 || ze==4 || ze==7 || ze==8) CallEvent(ze);
					else {	
						zx=spx[i]>>4;zy=(spy[i]+8)>>4;ze = getzone(zx,zy);
						if(ze==3 || ze==4 || ze==7 || ze==8) CallEvent(ze);
						else
							if(getobspixel(spx[i],spy[i]) || getobspixel(spx[i],spy[i]+8)) spe[i]=0;
					}
					spx[i]+=8;
				}
				if(spt[i]==14 || spt[i]==15) // firing
				{
					tblit(spx[i]-xwin,spy[i]-ywin,firing,screen);
					zx=spx[i]>>4;zy=spy[i]>>4; ze = getzone(zx,zy);	
					if(getobspixel(spx[i],spy[i]) || getobspixel(spx[i],spy[i]+7)) spe[i]=0;
					if(ze==3 || ze==4 || ze==7 || ze==8) CallEvent(ze);
					zx=spx[i]>>4;zy=(spy[i]+8)>>4;ze = getzone(zx,zy);
					if(ze==3 || ze==4 || ze==7 || ze==8) CallEvent(ze);
					if(spt[i]==14) spx[i]-=8; else spx[i]+=8;
				}
			}
		}
	}

	public static void drawText(int x, int y, String text) {
		screen.g.setFont(sys_font);
		screen.g.setColor(Color.WHITE);
		screen.g.drawString(text, x, y);	
	}


	/* Version 
	22/06/2000	Sounds, Basic engine, Akidd character
	05/02/2001	Stages, mapa.gif, some enemies
	01/03/2007	Come back to AK, conversion to V3, jumping configuration
	02/03/2007	Added death sound, dust from enemies, lives, trembling, punch as action
	04/03/2007	Added cave tiles from AK clone
	05/03/2007	Maps from AK Miracle World
	08/03/2007	Added motocycle movement, musics from vgm/vgz (field, swim, moto)
	09/03/2007	Motocycle graphics, added rock fragments (process sprites), strong bull, new stages
	15/03/2007	Moto (left, being destroyed, crazy), inv blinking, normal condition, lancha (sem tiro)
	16/03/2007	Ducking, Helicopter
	18/03/2007	Bat, Collision formula, Monkey (with leafs), Better selectLevel(), Wood stages, Energy Bar
	19/03/2007	Frog, Lake Secret Stage, Star Condition, Big Fish
	20/03/2007	Changed ShowPlayer() to return an int, strange monster, rock breaking stage, Stage2 (from MW)
	21/03/2007	Bracelete, debug mode (pause+collision rect), Tiro (helicóptero,lancha), Bear frames
	23/03/2007	setDimensions, akiddCollision, início do rndAlex
	24/03/2007	Surf stage, new Hit/Rock sounds, Swim reformulation, fast swim
	25/03/2007	Bat/owl secret stage, Bracelete bug corretion (punched)
	28/03/2007	Fly Condition, Bug (player hit by a monster when changing stages, due to late setDimensions)
	01/04/2007	Shinobi.chr, Shinobi Alex support (ShowShinobiPlayer)
	10/04/2007	Changed Brac to only hit a monster once.
	13/04/2007	Cave fragment, cave stage (starting from field), Fire (monster)
	14/04/2007	Brac sound, Item sound, Water sound
	17/04/2007	Completed rock stage.
	09/11/2008	Small change to new V3 compability.
	29/05/2009	Castle tiles and first map. Needs stair script.


	PENDENCIAS
	NEW: Trocar speed por description (Alterar mapas)
	(X) NEW: Trocar entityface por face.
	(X) NEW: Destroy vehicles (moto, surf, heli)
	Reformular Collision(), Punched() e getobs. Area do monstro (wx,wy) deve ser a mesma de getPunched.
	Alterar movimento do Bear
	Método CreateGhost() para criar um fantasma com entityface = 2 (onde ele fica parado, até normalizar).
	Criar menu para escolha de WALK/SWIM/MOTO/HELI/..., Shopping e Itens (Bengala, Manto da Invencibilidade, Cápsulas)
	(X)Fazer com que a moto tenha altura limitada quando a velocidade estiver reduzida
	Sons melhores (usar sons na chamada de AddSprite)
	Novos tiles (castelo, espetos, etc)
	(X)Tela de título e seleção
	Chefes e jo-ken-po
	Tiles, VSP Merge (automated)
	Internal Map Editor, random generator
	Nível de dificuldade (inimigos mais difíceis, sem barra de energia)
	Ajuste do mapa e Novas fases
	Personagens da família (from HighTech)
	Sprites do AK do Mega (or twin-brother Alex)
	Wonder boy merge
	*/
}
