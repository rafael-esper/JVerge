package domain;

public class Zone {

	public String name = "";
	public String script = "";
	public int percent=255, delay, method; // Unsigned byte
	

	public String toString() {
		return "Zone " + name + " Act:" + script + " Chance:" + percent + " Delay:" + delay + " Method:" + method;
	}

	
}
