package edu.cs.wayne;

import java.util.Collections;
import java.util.Iterator;
import java.util.ArrayList;

import soot.options.*;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.jimple.JimpleBody;
import soot.UnitPatchingChain;

class sootFromScratch
{
	private static String apk_file_path = "";
	
	public sootFromScratch(String apk_path) {
		this.apk_file_path = apk_path;
	}
	
	
	// initialize soot options and set up Scene 
	public static void initializeSoot() {
		Options.v().set_src_prec(Options.src_prec_apk);
		Options.v().set_verbose(false);
		soot.options.Options.v().set_process_dir(Collections.singletonList(apk_file_path));
	    soot.options.Options.v().set_allow_phantom_refs(true);
	    soot.options.Options.v().set_force_android_jar("ic3-android.jar");
	    
	    // Not sure if I'm doing this right...
	    soot.options.Options.v().set_output_dir("./");
	    soot.options.Options.v().set_output_format(Options.output_format_jimple);
	    
	    Scene.v().loadNecessaryClasses();
	}
	
	
	// generates a line with length of the string it is under 
	public static void generateLine(String s) {
		for (int i = 0; i < s.length(); i++) {
			System.out.print("=");
		}
		System.out.println("\n");
	}
	
	
	// generates a list of classes in the APK
	public static ArrayList<SootClass> getClasses() {
		
		ArrayList<SootClass> classes = new ArrayList<SootClass>();
		
		Iterator class_it = Scene.v().getApplicationClasses().iterator();
		
		while (class_it.hasNext()) 
			classes.add((SootClass) class_it.next());
		
		return classes;
	}
	
	
	// generates a list of methods in a given class 
	public static ArrayList<SootMethod> getMethods(SootClass s_class) {
		
		ArrayList<SootMethod> methods = new ArrayList<SootMethod>();
		
		Iterator method_it = s_class.getMethods().iterator();
		
		while (method_it.hasNext()) 
			methods.add((SootMethod) method_it.next());
		
		return methods;
	}
	
	
	// generates the jimple body of a given method 
	public static ArrayList<UnitPatchingChain> getMethodBody(SootMethod method) {
		
		ArrayList<UnitPatchingChain> body = new ArrayList<UnitPatchingChain>();
		
		try {
			JimpleBody s_body = (JimpleBody) method.retrieveActiveBody();
			
			Iterator chain_it = s_body.getUnits().iterator();
			
			while(chain_it.hasNext()) 
				body.add((UnitPatchingChain) chain_it.next());
		}
		catch (RuntimeException e) {
			
		}
		return body;
	}
	
	
	// generates a list of function calls in the given method 
	public static ArrayList<String> getFunctionCalls(JimpleBody body) {
		
		ArrayList<String> functionCalls = new ArrayList<String>();

		try {
		
			Iterator chain_it = body.getUnits().iterator();
			
			while(chain_it.hasNext()) {
				String instruction = (String) chain_it.next().toString();
				
				if (!instruction.contains("virtualinvoke")) 
					continue;
				
				String functionSubstring1[] = instruction.split(":");
				String functionSubstring2[] = functionSubstring1[1].split(" ");
				String functionString = "";
				
				for (int i = 0; i < functionSubstring2.length; i++) {
					if (i < 2)
						continue;
					functionString = functionString + " " + functionSubstring2[i]; 
				}
				functionCalls.add(functionString);
			}
		}
		catch(RuntimeException e) {
			System.out.println(e);
		}
		return functionCalls;
	}
	
	
	// returns true if body contains function that matches funcNameCheck.  Returns false if no function with name
	// funcNameCheck is found 
	public static boolean containsFunctionCall(JimpleBody body, String funcNameCheck) {
		
		ArrayList<String> functionCalls = new ArrayList<String>();
		functionCalls = getFunctionCalls(body);
		
		for (String func : functionCalls) {
			String funcName = func.split(" ")[2];
			if (funcName.contains(funcNameCheck));
				return true;
		}
		return false;
	}
	
	
	// returns the parameter passed to the given function 
	// format of argument --> funcName(param type)>argument
	public static ArrayList<String> getParams(String functionCall) {
		
		String functionCallSubstring[] = functionCall.split(">");
		String paramString = functionCallSubstring[1];
		paramString = paramString.substring(1,paramString.length()-1);
		
		String paramList[] = paramString.split(",");
		
		ArrayList<String> params = new ArrayList<String>();
		
		for (int i = 0; i < paramList.length; i++)
			params.add(paramList[i].trim());
		
		if (paramList[0].length() == 0) {
			params.clear();
			params.add("null");
		}
		
		return params;
	}
	
	
	// returns the name of the function in the function call
	// format of argument --> funcName(param type)>argument
	public static String getFunctionName(String functionCall) {
		String funcName = "";
		for(int i = 0; i < functionCall.length(); i++) { 
			if (functionCall.charAt(i) == '(')
				break;
			funcName = funcName + functionCall.charAt(i);
		}
		return funcName;
	}
	
	
	// analyze the APK components 
	public static void analyzeAPK() {
				
		System.out.println("Analyzing APK...\n");
		
		initializeSoot();
		
		for (SootClass s_class : getClasses()) {
			
			if (s_class.getName() == "com.example.test_app.MainActivity") {
			
				System.out.println("\n\n" + s_class.getName());
				generateLine(s_class.getName());
				
				for (SootMethod method : getMethods(s_class)) {
					System.out.println("  *  " + method.getSignature() + "\n");
					
					try {
						JimpleBody body = (JimpleBody) method.retrieveActiveBody();
						
						ArrayList<String> functionCalls = new ArrayList<String>();
						functionCalls = getFunctionCalls(body);
						
//						System.out.println(body.toString());
						
						for (String func : functionCalls) {
							System.out.println(getFunctionName(func));
//							System.out.println("\t  Function call : " + func);
//							System.out.println("\t  Params:");
//							for (String param : getParams(func))
//								System.out.println("\t\t- " + param);
//							System.out.println("");
						}		
					}
					catch (RuntimeException e) {
						System.out.println("Error");
					}
					System.out.println("\n");
				}
			}
		}
	}
	
	
    public static void main(String args[]) { 
    	
    	System.out.println("Soot analysis...\n");
    	sootFromScratch soot_analysis = new sootFromScratch("app-debug.apk"); 
    	soot_analysis.analyzeAPK();
    } 
} 


