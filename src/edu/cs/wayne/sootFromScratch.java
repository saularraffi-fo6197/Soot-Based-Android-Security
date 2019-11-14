package edu.cs.wayne;

import java.util.Collections;
import java.util.Iterator;

import soot.options.*;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.jimple.JimpleBody;

class sootFromScratch
{
	private static String apk_file_path = "";
	
	public sootFromScratch(String apk_path) {
		this.apk_file_path = apk_path;
	}
	
	public static void initializeSootOptions() {
		Options.v().set_src_prec(Options.src_prec_apk);
		Options.v().set_verbose(false);
		soot.options.Options.v().set_process_dir(Collections.singletonList(apk_file_path));
	    soot.options.Options.v().set_allow_phantom_refs(true);
	    soot.options.Options.v().set_force_android_jar("ic3-android.jar");
	}
	
	public static void generateLine(String s) {
		for (int i = 0; i < s.length(); i++) {
			System.out.print("=");
		}
		System.out.println("\n");
	}
	
	public static void analyzeAPK() {
				
		System.out.println("Analyzing APK...\n");
		
		initializeSootOptions();
		
		Scene.v().loadNecessaryClasses();
		
		Iterator class_it = Scene.v().getApplicationClasses().iterator();
		
		while (class_it.hasNext()) {
			
			SootClass s_class = (SootClass) class_it.next();
			Iterator method_it = s_class.getMethods().iterator();
			
			System.out.println("\n\n\n\n" + s_class.getName());
			generateLine(s_class.getName());
			
			while (method_it.hasNext()) {
				
				SootMethod method = (SootMethod) method_it.next();
				System.out.println("  *  " + method.getSignature());
				
				try {
					JimpleBody body = (JimpleBody) method.retrieveActiveBody();
					// System.out.println(body.getLocals());
				}
				catch (RuntimeException e) {}
			}
		}
	}
	
    public static void main(String args[]) { 
    	
    	System.out.println("Soot analysis...\n");
    	sootFromScratch soot_analysis = new sootFromScratch("com.karmacracy.apk"); 
    	soot_analysis.analyzeAPK();
    } 
} 


