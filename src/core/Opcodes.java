package core;

public class Opcodes {

	/// The VERGE 3 Project is originally by Ben Eirich and is made available via
	///  the BSD License.
	///
	/// Please see LICENSE in the project's root directory for the text of the
	/// licensing agreement.  The CREDITS file in the same directory enumerates the
	/// folks involved in this public build.
	///
	/// If you have altered this source file, please log your name, date, and what
	/// changes you made below this line.


	// Opcode values.
	public static final int ERROR			=	0;
	public static final int opRETURN		=	1;
	public static final int opASSIGN		=	2;
	public static final int opIF			=	3;
	public static final int opGOTO			=	4;
	public static final int opSWITCH		=	5;
	public static final int opCASE			=	6;
	public static final int opDEFAULT		=	7;
	public static final int opLIBFUNC		=	8;
	public static final int opUSERFUNC		=	9;
	public static final int opRETVALUE		=	10;
	public static final int opRETSTRING		=	11;

	// Overkill: Variadic functions
	public static final int opVARARG_START	=	12; // Start of argument list
	public static final int opVARARG_END	=	13;// End of argument list

	public static final int ifZERO			=	20;
	public static final int	ifNONZERO		=	21;
	public static final int ifEQUAL			=	22;
	public static final int ifNOTEQUAL		=	23;
	public static final int ifGREATER		=	24;
	public static final int ifGREATEROREQUAL=	25;
	public static final int ifLESS			=	26;
	public static final int ifLESSOREQUAL	=	27;
	public static final int ifAND			=	28;
	public static final int ifOR			=	29;
	public static final int ifUNGROUP		=	30;

	public static final int iopADD			=	31;
	public static final int iopSUB			=	32;
	public static final int iopDIV			=	33;
	public static final int iopMULT			=	34;
	public static final int iopMOD			=	35;
	public static final int iopSHL			=	36;
	public static final int iopSHR			=	37;
	public static final int iopAND			=	38;
	public static final int iopOR			=	39;
	public static final int iopXOR			=	40;
	public static final int iopEND			=	41;
	public static final int iopNOT			=	42;
	public static final int iopNEGATE		=	43;

	public static final int intLITERAL		=	50;
	public static final int intHVAR0		=	51;
	public static final int intHVAR1		=	52;
	public static final int intGLOBAL		=	53;
	public static final int intARRAY		=	54;
	public static final int intLOCAL		=	55;
	public static final int intLIBFUNC		=	56;
	public static final int intUSERFUNC		=	57;
	public static final int intGROUP		=	58;

	public static final int sADD			=	60;
	public static final int sEND			=	61;

	public static final int strLITERAL		=	70;
	public static final int strGLOBAL		=	71;
	public static final int strARRAY		=	72;
	public static final int strLOCAL		=	73;
	public static final int strLIBFUNC		=	74;
	public static final int strUSERFUNC		=	75;
	public static final int strHSTR0        =   76;
	public static final int strHSTR1		=	77;
	public static final int strINT			=	78;
	public static final int strLEFT			=	79;
	public static final int strRIGHT		=	80;
	public static final int strMID			=	81;

	public static final int aSET			=	90;
	public static final int aINC			=	91;
	public static final int aDEC			=	92;
	public static final int aINCSET			=	93;
	public static final int aDECSET			=	94;

	//plugin API

	public static final int	intPLUGINVAR	=	100;
	public static final int strPLUGINVAR	=	101;
	public static final int	intPLUGINFUNC	=	102;
	public static final int strPLUGINFUNC	=	103;
	public static final int opPLUGINFUNC	=	104;

	// Callbacks
	public static final int opRETCB			=	105; // Function returning a callback.
	public static final int opCBINVOKE		=	106; // Callback invocation with no return value
	public static final int opCBCOPY		=	107; // When copying a callback from a variable.
	public static final int strCBINVOKE		=	108; // Callback invocation with string return value
	public static final int intCBINVOKE		=	109; // Callback invocation with int return value
	public static final int cbLOCAL			=	110; // Local callback
	public static final int cbGLOBAL		=	111; // Global callback
	public static final int cbARRAY			=	112; // Global callback array
	public static final int cbUSERFUNC		=	113; // A function that returns a callback.
	public static final int opCBPADDING		=	114; // Needed after call attempt, so that failure won't mess the interpreter.
	public static final int opCBFUNCEXISTS	=	115;	// Test if a function exists or not.

	// types are 1 = int, 3 = string, 4 = variable number of ints, 5 = void, 6 = struct

	public static final int t_NOTFOUND		=	0; // Reserved for errors.
	public static final int t_INT			=	1;
	public static final int t_STRING		=	3;
	public static final int t_VARARG		=	4;
	public static final int t_VOID			=	5;
	public static final int t_STRUCT		=	6;
	public static final int t_BOOL			=	7; // For better lua compatibility with builtins.
	public static final int t_CALLBACK		=	8; // For function pointers.

	public static final int NUM_LIBFUNCS	=	281;
	public static final int NUM_HVARS		=	128;
	public static final int NUM_HDEFS		=	111;
	
	
}
