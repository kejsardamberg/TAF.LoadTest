package se.claremont.taf.performance;

import se.claremont.taf.performance.gui.MainGui;
import se.claremont.taf.performance.loadagent.LoadAgent;
import se.claremont.taf.performance.loadagent.LoadAgentRunner;

import java.awt.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        String[] acceptedHelpSwitches = { "-h", "/h", "-help", "--help", "--h", "-?", "--?", "/?", "man" };
        String[] acceptedAgentModeSwitches = { "-agent", "--agent", "-a", "--a" };
        for(String s : acceptedHelpSwitches){
            for(String arg:args){
                if(arg != null && arg.trim().toLowerCase().equals(s.trim().toLowerCase())){
                    printHelpText();
                    return;
                }
            }
        }
        if(args.length == 0) {
            if(Desktop.isDesktopSupported()){
                MainGui gui = new MainGui(TestRunner.getInstance());
            }else{
                printHelpText();
            }
        }
        for(String s : acceptedAgentModeSwitches){
            for(int i = 0; i < args.length; i++){
                if(args[i] != null && args[i].trim().toLowerCase().equals(s.trim().toLowerCase())){
                    if(args.length < i+2){
                        printHelpText();
                        System.out.println("ERROR: Agent mode requires an IP address to controller machine given as argument.");
                        return;
                    } else {
                        LoadAgentRunner runner = new LoadAgentRunner(args[i+1]);
                    }
                }
            }
        }
        if(args.length == 1 && Files.exists(Paths.get(args[0]))){
            //Load TestRunner data from file
            //Execute load
        } else {
            printHelpText();
        }

    }

    private static void printHelpText() {
        System.out.println("TAF - Performance test tool");
        System.out.println("===============================");
        System.out.println("Designing a load is easiest using the desktop GUI, thus producing a JSON file to reference using the following syntax:");
        System.out.println();
        System.out.println("java.exe -jar tafperformance.jar loadfile.json");
        System.out.println();
        System.out.println("Optional arguments include to start this file as a Load Agent by stating the load controller IP:");
        System.out.println();
        System.out.println("java.exe -jar tafperformance.jar -agent 192.168.0.10");
        System.out.println();
        System.out.println("   -h|-help|/?|-?|/h|/help   prints this help screen.");
        System.out.println();
    }

}
