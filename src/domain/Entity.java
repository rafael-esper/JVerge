package domain;

import core.Script;

import static core.VergeEngine.*;
import static core.Script.*;

// Merge of g_entity.cpp and g_entity.h
public class Entity {

	public final static int NORTH				= 1;
	public final static int SOUTH				= 2;
	public final static int WEST				= 3;
	public final static int EAST				= 4;
	public final static int NW					= 5;
	public final static int NE					= 6;
	public final static int SW					= 7;
	public final static int SE					= 8;
	public final static int FOLLOWDISTANCE		= 16;

	public final static int ENT_AUTOFACE		= 1;
	public final static int ENT_OBSTRUCTS		= 2;
	public final static int ENT_OBSTRUCTED		= 4;

	public final static int ENT_MOTIONLESS		= 0;
	public final static int ENT_MOVESCRIPT		= 1;
	public final static int ENT_WANDERZONE		= 2;
	public final static int ENT_WANDERBOX		= 3;
	
	// Loaded inside the map 
	
	public CHR chr; 
	
	private int x, y; // Unsigned short
	//public byte direction=0; //, isObstructable=1, isObstruction=1, autoface=1;
	public byte activationMode = 0; // Unused
	public int expandFlag = 0; // Unused
	public String chrname = ""; // filename
	
	public boolean visible = true, active = true, autoface = true;
	public boolean obstruction = true;
	public boolean obstructable = true;
	
	public int lucent;
	int waypointx, waypointy;
	public int speed;
	int speedct;
	public byte movecode;
	int delay, wdelay;
	public int face;
	int framect;
	public int specframe;
	int frame;
	int wx1, wy1, wx2, wy2;
    int movemult;

	String description; // Overkill - 2006-05-21
	public String hookrender;
	public String movescript;
	public String script;

	String movestr;
	int moveofs;	

	public int getx() {
		if(current_map!=null && current_map.horizontalWrapable && 
				(this.x/65536 > current_map.getWidth()<<4 || this.x < 0)) {
			this.x = (this.x + (current_map.getWidth()<<20)) % (current_map.getWidth()<<20);
			this.waypointx = (this.waypointx + (current_map.getWidth()<<4)) % (current_map.getWidth()<<4);
		}
		return this.x/65536;	}
	
	public int gety() { 
		if(current_map!=null && current_map.verticalWrapable &&
			(this.y/65536 > current_map.getHeight()<<4 || this.y < 0)) {
				this.y = (this.y + (current_map.getHeight()<<20)) % (current_map.getHeight()<<20);
				this.waypointy = (this.waypointy + (current_map.getHeight()<<4)) % (current_map.getHeight()<<4);
			}		
		return this.y/65536; 	}
	
	private int getOriginalX() {
		return this.x;
	}
	private int getOriginalY() {
		return this.y;
	}
	private void setoriginalx(int x) {
		this.x = x;
		getx();
	}
	private void setoriginaly(int y) {
		this.y = y;
		gety();
	}
	
	//RBP
	public void setx(int x) {
		//this.setxy(x* 65536, this.y);
		this.x = x * 65536;
		clear_waypoints();
		//set_waypoint(getx(), gety());
	}
	public void sety(int y) {
		//this.setxy(this.x, y* 65536);
		this.y = y * 65536;
		clear_waypoints();
		//set_waypoint(getx(), gety());
	}
	public void incx() {
		this.incx(1);
	}
	public void incy() {
		this.incy(1);
	}
	public void incx(int i) {
		this.x+= i * 65536;
	}
	public void incy(int i) {
		this.y+= i * 65536;
	}
	public void clear_waypoints() {
		set_waypoint(getx(), gety());
		delay = 0;
	}
	
	
	// END RBP

	public int getwaypointx() { 
		//if(current_map.horizontalWrapable && this.waypointx > (current_map.getWidth()+32)<<4)
//			this.waypointx = (this.waypointx + (current_map.getWidth()<<4)) % (current_map.getWidth()<<4);
		return this.waypointx; 
	}
	public int getwaypointy() { 
		//if(current_map.verticalWrapable)
//			this.waypointy = (this.waypointy + (current_map.getHeight()<<4)) % (current_map.getHeight()<<4);
		return this.waypointy; 
	}
	void setwaypointx(int waypointx) {
		this.waypointx = waypointx;
	//	getwaypointx();
	}
	void setwaypointy(int waypointy) {
		this.waypointy = waypointy;
		//getwaypointy();
	}
	
	
	public int index;
	
	Entity follower, follow;
	int pathx[] = new int[FOLLOWDISTANCE]; 
	int pathy[] = new int[FOLLOWDISTANCE];
	int pathf[] = new int[FOLLOWDISTANCE];	
	
	
	public void setface(int face) {
		this.face = face;
	}

	public String toString() {
		return "Entity (" + getx() + ", " + gety() + ") dir:" + face + " isObsT:" + obstructable + " isObs:" + obstruction + " autoF:" + autoface + " " +
				" speed:" + speed + " movMode:" + movecode + " wanders: (" + wx1 +","+ wy1 +"," + wx2 +"," + wy2 + ") wDelay:" + wdelay +
				" movescript:" + movescript + " filename:" + chrname + " desc:" + description + " actEvent:" + script;
	}	
	
	// Used by the engine
	
	public Entity(int x, int y, String chrfn) {
		follower = null;
		follow = null;
		delay = 0;
		lucent = 0;
		wdelay = 75;
		setxy(x, y);
		setspeed(100);
		speedct = 0;
		chrname = chrfn;
		if(chrfn!=null) // rbp
			chr = new CHR(chrfn); // RequestCHR(chrfn);
		visible = true;
		active = true;
		specframe = 0;
		movecode = 0;
		moveofs = 0;
		framect = 0;
		frame = 0;
		face = SOUTH;
		hookrender = "";
		script = "";
		description = "";
		obstructable = false;
		obstruction = false;
		for (int i=0; i<FOLLOWDISTANCE; i++) {
			pathx[i] = x;
			pathy[i] = y;
			pathf[i] = SOUTH;
		}
	
	}
	
	public Entity() {
		// TODO Auto-generated constructor stub
	}	
	
	/*void setface(int d) { 
	  // bounds-check this since it's easy to set an invalid value in vc (janus)
	  if ((d > 0) && (d <= 4)) {
	    face = d; 
	  } else {
	    err("setface(%d), facing values must be within the range [1, 4]", d);
	  }
	}*/

	void setxy(int x1, int y1) {
		setoriginalx(x1 * 65536);
		setoriginaly(y1 * 65536);
		if (follower != null) follower.setxy(x1, y1);
		set_waypoint(x1, y1);
		for (int i=0; i<FOLLOWDISTANCE; i++) {
			pathx[i] = getOriginalX();
			pathy[i] = getOriginalY();
			pathf[i] = SOUTH;
		}
	}

	int getspeed() { return speed; }
	void setspeed(int s)
	{
		speed = s;
	    // We don't reset the speedct here, because
	    // 1) Is is keeping track of distance already moved but not acted on
	    //    (ie any partial movement made but not turned into a tick)
	    // 2) If we reset speedct, setting the speed frequently will slow
	    //    the character down by discarding the partial bits

		if (follower != null) follower.setspeed(s);
	}

	void set_waypoint(int x1, int y1)
	{
		setwaypointx(x1);
		setwaypointy(y1);

		switch ((int) Math.signum(y1-gety()))
		{
			case -1: face = NORTH; break;
			case 0:  break;
			case 1:  face = SOUTH; break;
		}
		switch ((int)Math.signum(x1-getx()))
		{
			case -1: face = WEST; break;
			case 0:  break;
			case 1:  face = EAST; break;
		}
	}

	public void set_waypoint_relative(int x1, int y1, boolean changeface)
	{
		setwaypointx(getwaypointx() + x1);
		setwaypointy(getwaypointy() + y1);
		
		if(changeface) {
			switch ((int) Math.signum(y1))
			{
				case -1: face = NORTH; break;
				case 0:  break;
				case 1:  face = SOUTH; break;
			}
			switch ((int) Math.signum(x1))
			{
				case -1: face = WEST; break;
				case 0:  break;
				case 1:  face = EAST; break;
			}
		}
	}

	public boolean ready() { 
		return (getx() == getwaypointx() && gety() == getwaypointy()); 
	}

	boolean leaderidle(){

		if (follow!=null) return follow.leaderidle();
		return (getx() == getwaypointx() && gety() == getwaypointy());
	}

	// called to sync up with leader's frame
	// of course, if the two people have different-
	// length walk cycles, they might have the same framect,
	// but they won't sync visuall, which is OK
	int get_leader_framect()
	{
	    if(follow!=null) 
	    	return follow.get_leader_framect();
	    return framect;
	}

	void set_framect_follow(int f)
	{
	    if(follower!=null) {
	        follower.set_framect_follow(f);
	    }
	    framect = f;
	}

	public void stalk(Entity e)
	{
		follow = e;
		e.follower = this;
		for (int i=0; i<FOLLOWDISTANCE; i++) {
			pathx[i] = follow.pathx[FOLLOWDISTANCE-1];
			pathy[i] = follow.pathy[FOLLOWDISTANCE-1];
			pathf[i] = SOUTH;
		}
		setoriginalx(follow.pathx[FOLLOWDISTANCE-1]);
		setoriginaly(follow.pathy[FOLLOWDISTANCE-1]);
		set_waypoint(getx(), gety());
	    movecode = 0;
		obstruction = false;
		obstructable = false;
	    // clear delay info from wandering
	    delay = 0;
	    // sync our (and followers') framect with the leader
	    set_framect_follow(get_leader_framect());
	}

	// This is called when we are going to change
	// to a kind of movement that isn't stalking to
	// ensure we are not trying to stalk at the same time
	public void clear_stalk()
	{
	    if(follow!=null) {
	        follow.follower = null;
	        follow = null;
	    }
	}

	void move_tick()
	{
		int dx = getwaypointx() - getx();
		int dy = getwaypointy() - gety();

		if (this != myself && follow==null && obstructable)
		{
			// do obstruction checking */

			switch (face)
			{
				case NORTH: if (ObstructDirTick(NORTH)) return; break;
				case SOUTH: if (ObstructDirTick(SOUTH)) return; break;
				case WEST: if (ObstructDirTick(WEST)) return; break;
				case EAST: if (ObstructDirTick(EAST)) return; break;
				default: System.err.println("move_tick() - bad face value!!");
			}
		}
		framect++;

		// update pathxy for following
		for (int i=FOLLOWDISTANCE-2; i>=0; i--) {
			pathx[i+1] = pathx[i];
			pathy[i+1] = pathy[i];
			pathf[i+1] = pathf[i];
		}
		pathx[0] = getOriginalX();
		pathy[0] = getOriginalY();
		pathf[0] = face;

		// if following, grab new position from leader
	    // We now keep track of our own framect, (rather
	    // than using the leader's framect)
	    // which is synced with the leader in stalk(),
	    // but then runs free after that so animations
	    // of different lengths are ok in a stalking chain.
		if (follow != null)
		{
			setoriginalx(follow.pathx[FOLLOWDISTANCE-1]);
			setoriginaly(follow.pathy[FOLLOWDISTANCE-1]);
			face = follow.pathf[FOLLOWDISTANCE-1];
			set_waypoint(getx(), gety());
			if (follower != null)
				follower.move_tick();
			return;
		}

		// else move
		if (dx != 0)
			setoriginalx((int) (getOriginalX() + (Math.signum(dx) * 65536)));

		if (dy != 0)
			setoriginaly((int) (getOriginalY() + (Math.signum(dy) * 65536)));

	/*
		if (dx &&!dy)
			x += sgn(dx) * 65536;
		if (!dx && dy)
			y += sgn(dy) * 65536;

		if (dx && dy)
		{
			x += sgn(dx) * 46340;
			y += sgn(dy) * 46340;
		}
	*/
		if (follower != null)
			follower.move_tick();
	}

	public void think()
	{
		int num_ticks;
		if (!active) return;

		if (delay>systemtime)
		{
			framect = 0;
			return;
		}

		speedct += speed;
		num_ticks = speedct / 100;
		speedct %= 100;

		while (num_ticks > 0)
		{
			num_ticks--;

			if (ready())
			{
				switch (movecode)
				{
					case 0: if (this == myself && invc==0) ProcessControls(); break;
					case 1: do_wanderzone(); break;
					case 2: do_wanderbox(); break;
					case 3: do_movescript(); break;
					default: System.err.println("think(), unknown movecode value");
				}
			}
			if (!ready())
				move_tick();
		}
	}

	boolean ObstructDirTick(int d)
	{
		__grue_actor_index = this.index;

		int x, y;
		int ex = getx();
		int ey = gety();

		if (!obstructable) return false;
		switch (d)
		{
			case NORTH:
				for (x=ex; x<ex+chr.hw; x++)
					if (ObstructAt(x, ey-1)) return true;
				break;
			case SOUTH:
				for (x=ex; x<ex+chr.hw; x++)
					if (ObstructAt(x, ey+chr.hh)) return true;
				break;
			case WEST:
				for (y=ey; y<ey+chr.hh; y++)
					if (ObstructAt(ex-1, y)) return true;
				break;
			case EAST:
				for (y=ey; y<ey+chr.hh; y++)
					if (ObstructAt(ex+chr.hw, y)) return true;
				break;
		}
		return false;
	}

	boolean ObstructDir(int d)
	{
		__grue_actor_index = this.index;

		int i, x, y;
		int ex = getx();
		int ey = gety();

		if (!obstructable) return false;
		switch (d)
		{
			case NORTH:
				for (i=0; i<chr.hh; i++)
					for (x=ex; x<ex+chr.hw; x++)
						if (ObstructAt(x, ey-i-1)) return true;
				break;
			case SOUTH:
				for (i=0; i<chr.hh; i++)
					for (x=ex; x<ex+chr.hw; x++)
						if (ObstructAt(x, ey+i+chr.hh)) return true;
				break;
			case WEST:
				for (i=0; i<chr.hw; i++)
					for (y=ey; y<ey+chr.hh; y++)
						if (ObstructAt(ex-i-1, y)) return true;
				break;
			case EAST:
				for (i=0; i<chr.hw; i++)
					for (y=ey; y<ey+chr.hh; y++)
						if (ObstructAt(ex+chr.hw+i, y)) return true;
				break;
		}
		return false;
	}

	void do_wanderzone()
	{
		boolean ub=false, db=false, lb=false, rb=false;
		int ex = getx()/16;
		int ey = gety()/16;
		int myzone = current_map.zone(ex, ey);

		if (ObstructDir(EAST) || current_map.zone(ex+1, ey) != myzone) rb=true;
		if (ObstructDir(WEST) || current_map.zone(ex-1, ey) != myzone) lb=true;
		if (ObstructDir(SOUTH) || current_map.zone(ex, ey+1) != myzone) db=true;
		if (ObstructDir(NORTH) || current_map.zone(ex, ey-1) != myzone) ub=true;

		if (rb && lb && db && ub) return; // Can't move in any direction

		delay = systemtime + wdelay;
		while (true)
		{
			int i = Script.random(0, 3);
			switch (i)
			{
				case 0:
					if (rb) break;
					set_waypoint_relative(16, 0, true);
					return;
				case 1:
					if (lb) break;
					set_waypoint_relative(-16, 0, true);
					return;
				case 2:
					if (db) break;
					set_waypoint_relative(0, 16, true);
					return;
				case 3:
					if (ub) break;
					set_waypoint_relative(0, -16, true);
					return;
			}
		}
	}

	void do_wanderbox()
	{
		boolean ub=false, db=false, lb=false, rb=false;
		int ex = getx()/16;
		int ey = gety()/16;

		if (ObstructDir(EAST) || ex+1 > wx2) rb=true;
		if (ObstructDir(WEST) || ex-1 < wx1) lb=true;
		if (ObstructDir(SOUTH) || ey+1 > wy2) db=true;
		if (ObstructDir(NORTH) || ey-1 < wy1) ub=true;

		if (rb && lb && db && ub) return; // Can't move in any direction

		delay = systemtime + wdelay;
		while (true)
		{
			int i = Script.random(0, 3);
			switch (i)
			{
				case 0:
					if (rb) break;
					set_waypoint_relative(16, 0, true);
					return;
				case 1:
					if (lb) break;
					set_waypoint_relative(-16, 0, true);
					return;
				case 2:
					if (db) break;
					set_waypoint_relative(0, 16, true);
					return;
				case 3:
					if (ub) break;
					set_waypoint_relative(0, -16, true);
					return;
			}
		}
	}

	public void do_movescript()
	{
		char vc2me[] = { 2, 1, 3, 4 };
		int arg;

		// movements factors
		// These are set to -1,0 or 1 to signify in
		// which directions movement should occur
		int vertfac = 0, horizfac = 0;


	    // reset to tile-based at the start of a movestring
	    if(moveofs == 0) {
	        movemult = 16;
	    } else if (moveofs >= movestr.length()) {
	    	movecode = 0; framect = 0; // rbp
	    }
	    
	    
	    
	    if(movestr==null || movestr.trim().isEmpty() || moveofs >= movestr.length()) // last if by rbp
	    	return;
	    
		while (moveofs < movestr.length() && ( // rbp
				(movestr.charAt(moveofs) >= '0' && movestr.charAt(moveofs) <= '9') || movestr.charAt(moveofs) == ' ' || movestr.charAt(moveofs) == '-'))
			moveofs++;

		boolean done = false;
		int found_move = 0; // number of LRUD letters we found
		while(!done && found_move < 2 && moveofs < movestr.length()) {
			switch(Character.toUpperCase(movestr.charAt(moveofs)))
			{
				case 'L':
					if(found_move==0 && face != WEST) setface(WEST);
					moveofs++;
					horizfac = -1;
					found_move++;
					break;
				case 'R':
					if(found_move==0 && face != EAST) setface(EAST);
					moveofs++;
					horizfac = 1;
					found_move++;
					break;
				case 'U':
					if(found_move==0 && face != NORTH) setface(NORTH);
					moveofs++;
					vertfac = -1;
					found_move++;
					break;
				case 'D':
					if(found_move==0 && face != SOUTH) setface(SOUTH);
					moveofs++;
					vertfac = 1;
					found_move++;
					break;
				default:
					done = true;
			}
		}
// RBP FIXME 
		if(!(moveofs < movestr.length()))
			return;
		
		if(found_move!=0) {
			
			arg = get_int(movestr, moveofs);
			
			// we've already set facing, don't do it again
			set_waypoint_relative(horizfac*arg*movemult, vertfac*arg*movemult, false);

		} else {
			// no directions, check other possible letters:
			switch(Character.toUpperCase(movestr.charAt(moveofs))) {
				case 'S': moveofs++;
					setspeed(get_int(movestr, moveofs));
					break;
				case 'W': moveofs++;
					delay = systemtime + get_int(movestr, moveofs);
					break;
				case 'F': moveofs++;
					setface(vc2me[get_int(movestr, moveofs)]);
					break;
				case 'B': moveofs = 0; break;
				case 'X': moveofs++;
					arg = get_int(movestr, moveofs);
					set_waypoint(arg*16, gety());
					break;
				case 'Y': moveofs++;
					arg = get_int(movestr, moveofs);
					set_waypoint(getx(), arg*16);
					break;
				case 'Z': moveofs++;
					specframe = get_int(movestr, moveofs);
					break;
				case 'P': movemult = 1;
					moveofs++;
					break;
				case 'T': movemult = 16;
					moveofs++;
					break;
				case 'H':
				case '0':  
					movemult = 0; moveofs = 0; movecode = 0; framect = 0; 
					return;
				default: System.err.println("do_movescript(), unidentify movescript command");
			}
		}

	}

	public int get_int(String s, int offset) {
		int digit_size = 0; //rbp
		if(Character.isDigit(s.charAt(offset))) { 
			digit_size++; 
			if(offset+1 < s.length() && Character.isDigit(s.charAt(offset+1))) {
				digit_size++;
				if(offset+2 < s.length() && Character.isDigit(s.charAt(offset+2))) { 
					digit_size++;
					if(offset+3 < s.length() && Character.isDigit(s.charAt(offset+3))) {
						digit_size++;
					}
				}
			}
		}
		int ret = Integer.parseInt(movestr.substring(moveofs, moveofs+digit_size).trim());
		moveofs+=digit_size;
		return ret;
	}
	
	public void set_chr(String fname)
	{
	    chr = new CHR(fname); // rbp RequestCHR(fname);
		specframe = 0;
		framect = 0;
		frame = 0;
	}

	public void draw()
	{
		if (!visible) return;

	    // if we're idle, reset the framect
		if ((follow==null && ready()) || (follow!=null && leaderidle()))
			framect = 0;

		if (specframe > 0)
			frame = specframe;
		else
		{
			if (follow==null)
			{
				if (ready()) frame = chr.idle[face];
				else frame = chr.GetFrame(face, framect);
			}
			else
			{
				if (leaderidle()) frame = chr.idle[face];
				else frame = chr.GetFrame(face, framect);
			}
		}

		// Adapted by rbp
		int zx = (getx() - xwin + (current_map.getWidth()<<4)) % (current_map.getWidth()<<4),
			zy = (gety() - ywin + ((current_map.getHeight()<<4))) % (current_map.getHeight()<<4);

		//System.out.println(this.chrname + " " + zx + "," + zy + " " + getx() + "," + gety() + " " + xwin + "," + ywin);
		
		if (hookrender != null && !hookrender.isEmpty())
		{
			event_entity = index;
			callfunction(hookrender);
			return;
		}

		if (chr != null)
			chr.render(zx, zy, frame, screen);
		else
			;//RBP DrawRect(zx, zy, zx + 15, zy + 15, MakeColor(255,255,255), screen);
	}

	public void SetWanderZone()
	{
	    clear_stalk();
		set_waypoint(getx(), gety());
		movecode = 1;
	}

	public void SetWanderBox(int x1, int y1, int x2, int y2)
	{
	    clear_stalk();
		set_waypoint(getx(), gety());
		wx1 = x1;
		wy1 = y1;
		wx2 = x2;
		wy2 = y2;
		movecode = 2;
	}

	public void SetMoveScript(String s)
	{
	    clear_stalk();
		set_waypoint(getx(), gety());
		movestr = s;
		moveofs = 0;
		movecode = 3;
	}

	public void SetWanderDelay(int n)
	{
		wdelay = n;
	}

	public void SetMotionless()
	{
	    clear_stalk();
		set_waypoint(getx(), gety());
		movecode = 0;
		delay = 0;
	}

	public int getHotX() {
		return this.chr.hx;
	}
	
	public int getHotY() {
		return this.chr.hy;
	}

	public int getHotW() {
		return this.chr.hw;
	}

	public int getHotH() {
		return this.chr.hh;
	}
	
}
