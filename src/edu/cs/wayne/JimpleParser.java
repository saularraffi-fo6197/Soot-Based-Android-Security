package edu.cs.wayne;

import java.util.ArrayList;
import java.util.Iterator;

import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.UnitPatchingChain;
import soot.jimple.JimpleBody;

public class JimpleParser {
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
}

