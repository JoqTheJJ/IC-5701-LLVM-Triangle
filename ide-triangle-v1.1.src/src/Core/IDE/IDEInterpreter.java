/*
 * IDE-Triangle v1.0
 * IDEInterpreter.java
 */

package Core.IDE;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Just another small class to call the Triangle interpreter.
 *
 * @author Luis Leopoldo Perez <luiperpe@ns.isi.ulatina.ac.cr>
 */
public class IDEInterpreter {
    
    // <editor-fold defaultstate="collapsed" desc=" Methods ">    
    
    /**
     * Creates a new instance of IDEInterpreter.
     * @param _delegate Event to be fired when the thread stops running.
     */
    public IDEInterpreter(ActionListener _delegate) {
        delegate = _delegate;
    }
    
    /**
     * Runs the Interpreter static main method as a separate thread.
     * @param fileName Path to the TAM Object File.
     */       
    public synchronized void Run(final String fileName) {
        new Thread(new Runnable() {
            public void run() {
                TAM.Interpreter.main(new String[] {fileName});
                delegate.actionPerformed(null);
            }
        }).start();
    }      
    // </editor-fold>
    
    
    private void runCommand(String title, String... command) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.redirectErrorStream(true);
        Process process = pb.start();

        //writeToLLVMConsole("\n?? " + title);
        System.out.println(title);
        System.out.println("Command: " + String.join(" ", command));

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            //writeToLLVMConsole(line + "\n");
            System.out.println(line + "\n");
        }

        int exitCode = process.waitFor();
        //writeToLLVMConsole("?? C�digo de salida: " + exitCode + "\n");
        System.out.println("C�digo de salida: " + exitCode + "\n");
    }
    
    public void RunLLVM(String triFilePath) {
        new Thread(() -> {
            try {
                String fileBase = triFilePath.replaceFirst("[.][^.]+$", "");
                String llFile = fileBase + ".ll";
                String exeFile = fileBase + ".exe";
                
                String ioFile = new File("runtime/io.ll").getAbsolutePath();

                // TODO
                //setInputEnabled(false); // Desactiva el input mientras corre

                // Linkear con io.ll
                runCommand("Linkeando con io.ll...\n", "clang", llFile, ioFile, "-o", exeFile);

                // Ejecutar exe
                runCommand("Ejecutando el programa:\n", exeFile);

            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage() + "\n");
            } finally {
                
                // TODO
                //setInputEnabled(true); // Reactiva input al final
            }
            
            delegate.actionPerformed(null);
        }).start();
    }
    
    
    
    // <editor-fold defaultstate="collapsed" desc=" Attributes ">
    private ActionListener delegate;    // Gets triggered when the Interpreter stops.
    // </editor-fold>
}
