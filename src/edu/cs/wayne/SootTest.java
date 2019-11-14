package edu.cs.wayne;
import soot.options.Options;

import java.io.File;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import soot.Main;
import soot.PackManager;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import soot.Body;
import soot.jimple.*;

import soot.jimple.Jimple;
import soot.jimple.infoflow.AbstractInfoflow;
import soot.jimple.infoflow.android.InfoflowAndroidConfiguration;
import soot.jimple.infoflow.android.SootConfigForAndroid;
//import soot.jimple.infoflow.android.config.SootConfigForAndroid;
import soot.jimple.infoflow.config.IInfoflowConfig;

public class SootTest {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final String apkFileLocation;
	
	private final InfoflowAndroidConfiguration config = new InfoflowAndroidConfiguration();
	private final IInfoflowConfig sootConfig = new SootConfigForAndroid();

	public SootTest(String apkFileLocation) {
		this.apkFileLocation = apkFileLocation;
	}
	
	public void setOptions(String apkPath) {
		Options.v().set_src_prec(Options.src_prec_apk);
	    Options.v().set_ignore_resolution_errors(true);
	    Options.v().set_debug(false);
	    Options.v().set_verbose(true);
	    Options.v().set_unfriendly_mode(true);

	    soot.options.Options.v().set_process_dir(Collections.singletonList(apkPath));
	    soot.options.Options.v().set_allow_phantom_refs(true);
	    soot.options.Options.v().set_force_android_jar("ic3-android.jar");
	}

	public Set<String> computeAnalysisClassesInApk(String apkPath) {
	    Set<String> result = new HashSet<>();

	    this.setOptions(apkPath);
	    
	    Scene.v().loadNecessaryClasses();

	    for (SootClass className : Scene.v().getApplicationClasses()) {
	    	
	    	String name = className.getName();
	    	
	    	if (name.contains("com.karmacracy.app.ui.RegisterActivity")) {

		    	if (!name.startsWith("android.app.FragmentManager")&& !name.startsWith("android.support")) {
		    		
		    		System.out.println("Class: "+ name+"\n==================\n");
		    		List<SootMethod> methods= className.getMethods();
		    		
			    	for (SootMethod method :methods) {
			    		System.out.println(method.getSignature());
			    		if(method.hasActiveBody())
			    			System.out.println(method.getActiveBody());
	
			    		JimpleBody j_body = Jimple.v().newBody(method);
			    		method.setActiveBody(j_body);
			    		System.out.println(j_body.getLocals());
			    	}
			        
			    	System.out.println("------------------\n\n");
		    	}
	    	}
	    }

	    return result;
	  }

	public void printJimpleCode() {
		Set<String> results=computeAnalysisClassesInApk(apkFileLocation);
	}
	
	public static void main(String args[]) {
		SootTest test=new SootTest("com.karmacracy.apk");
		test.printJimpleCode();
	}
}