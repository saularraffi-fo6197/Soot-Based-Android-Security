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
		
	// analyze the APK components 
	public static void analyzeAPK() {
				
		System.out.println("Analyzing APK...\n");
		
		initializeSoot();
		
		for (SootClass s_class : JimpleParser.getClasses()) {
			
			if (s_class.getName() == "com.example.test_app.MainActivity") {
			
				System.out.println("\n\n" + s_class.getName());
				JimpleParser.generateLine(s_class.getName());
				
				for (SootMethod method : JimpleParser.getMethods(s_class)) {
					System.out.println("  *  " + method.getSignature() + "\n");
					
					try {
						JimpleBody body = (JimpleBody) method.retrieveActiveBody();
						
						ArrayList<String> functionCalls = new ArrayList<String>();
						functionCalls = JimpleParser.getFunctionCalls(body);
						
//						System.out.println(body.toString());
						
//						for (String func : functionCalls) {
//							System.out.println("\t  Function call : " + func);
//							System.out.println("\t  Params:");
//							for (String param : JimpleParser.getParams(func))
//								System.out.println("\t\t- " + param);
//							System.out.println("");
//						}												
					}
					catch (RuntimeException e) {
						System.out.println("Error");
					}
					System.out.println("\n");
				}
			}
		}
		XmlParser.xmlParser();
	}
	
	
    public static void main(String args[]) { 
    	
    	System.out.println("Soot analysis...\n");
    	sootFromScratch soot_analysis = new sootFromScratch("app-debug.apk"); 
    	soot_analysis.analyzeAPK();
    } 
} 


