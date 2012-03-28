package sully.vc.util;

import static core.Script.*;

// error_handler.vc for Sully www.verge-rpg.com
// Zip 28/08/2004
//
//
//        ---------------------
//        Error functions
//        ---------------------
//
// Notifies by MessageBox() and/or Log() of problems when loading data from file
// No comments, as the text to display should be pretty explanatory
//
public class Error_handler {
	
	// Used to set how errors are reported, 3 is recommend, 0 should not be used
	// 3 is MessageBox() and Log(), 2 is MessageBox(), 1 is Log(), 0 is neither
	public static final int ERROR_HANDLING = 2;
	
	// Used to set shortest valid length of description text, should be 1 at least
	public static final int MICRO_STRING = 5;
	
	 // Used to prevent while loops getting stuck if the EOF marker is forgotten in file loading
	public static final int COUNT_OUT = 1000;
	
	 // Used as a count of the line number in currently open file for error reporting
	public static int global_linenum;
	
	public static void ErrorCountOut(String erco_filename)
	{
		error("FATAL ERROR: Count out on end of file: '"+erco_filename+"'. Ensure the file has the escape sequence and less than "+str(COUNT_OUT)+" lines of non-data.");
	}
	
	public static void ErrorDuplicateName(String erdn_location, String erdn_names)
	{
		error("ERROR on line: "+str(global_linenum)+" duplicate name for '"+erdn_location+"' changed: '"+erdn_names+"'");
	}
	
	public static void ErrorDuplicateType(String erdt_location, String erdt_typeString)
	{
		error("Possible error on line: "+str(global_linenum)+" for '"+erdt_location+"' type already defined: '"+erdt_typeString+"'");
	}
	
	public static void ErrorLoadDefine(String erld_location, String erld_valString)
	{
		error("Possible error on line: "+str(global_linenum)+" for: '"+erld_location+"' unrecognised define: '"+erld_valString+"'");
	}
	
	public static void ErrorLoadFile(String erlf_filename)
	{
		error("FATAL ERROR: Failed to load file: '"+erlf_filename+"'. Make sure the file exists, is named correctly and placed in the right directory.");
	}
	
	public static void ErrorLoadNumber(int erln_numconv, String erln_numString)
	{
		error("Possible error on line: "+str(global_linenum)+" converting String: '"+erln_numString+"' to number: '"+str(erln_numconv)+"'");
	}
	
	public static void ErrorLoadOver(int erlo_maxentries, String erlo_filename)
	{
		error("FATAL ERROR: Attempting to load from: '"+erlo_filename+"' with max: "+str(erlo_maxentries)+" entries. Either increase allowed number of entries, or trim file.");
	}
	
	public static void ErrorLoadString(String erls_failString)
	{
		error("Possible error on line: "+str(global_linenum)+" loading String: '"+erls_failString+"' due to length under: "+str(MICRO_STRING));
	}
	
	public static void ErrorLoadType(String erlt_location, String erlt_typeString)
	{
		error("ERROR on line: "+str(global_linenum)+" for '"+erlt_location+"' unrecognised type: '"+erlt_typeString+"'");
	}
	
	public static void ErrorMatchStrings(String erms_findString, String erms_checkString)
	{
		error("Possible error on line: "+str(global_linenum)+" comparing similar Strings: '"+erms_findString+"' and: '"+erms_checkString+"'");
	}
	
	public static void ErrorStupidNumber(int ersn_numconv, String ersn_numString)
	{
		error("ERROR on line: "+str(global_linenum)+" the number value: '"+str(ersn_numconv)+"' converted from: '"+ersn_numString+"' has no meaning");
	}
}