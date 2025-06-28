/*
 * IDE-Triangle v1.0
 * IDEInterpreter.java
 */

package Core.IDE;
import java.awt.event.ActionListener;

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
    
    public synchronized void RunLLVM(final String fileName) {
        new Thread(() -> {/**
            try {
                String fileBase = triFilePath.replaceFirst("[.][^.]+$", ""); // test.tri ? test
                String llFile = fileBase + ".ll";
                String exeFile = fileBase + ".exe";

                setInputEnabled(false); // Desactiva el input mientras corre

                // Compilar a LLVM
                runCommand("Compilando a LLVM...\n", "java", "-cp", "build/classes", "Triangle.Compiler", triFilePath, "--llvm");

                // Linkear con io.ll
                runCommand("Linkeando con io.ll...\n", "clang", llFile, "io.ll", "-o", exeFile);

                // Ejecutar el binario
                runCommand("Ejecutando el programa:\n", exeFile);

            } catch (Exception e) {
                writeToLLVMConsole("?? Error: " + e.getMessage() + "\n");
            } finally {
                setInputEnabled(true);
            }*/
        }).start();
    }
    
    
    
    // <editor-fold defaultstate="collapsed" desc=" Attributes ">
    private ActionListener delegate;    // Gets triggered when the Interpreter stops.
    // </editor-fold>
}
