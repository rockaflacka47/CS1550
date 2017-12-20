import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

public class vmsim{
    static int numFrames;
    static int refresh;
    static String alg, traceFile;
    static int memAccesses = 0;

    public static void main(String [] args){
        parseArgs(args);
        //run the correct algorithm
        if(refresh ==0 && alg.equalsIgnoreCase("nru")){
            System.err.println("if running nru provide flag -r followed by a number > 0");
            System.exit(1);
        }
        if(alg.equals("opt")){
            runOpt();
        }
        else if(alg.equals("clock"))
            runClock();
        else if(alg.equals("nru"))
            runNRU();
        else if(alg.equals("rand"))
            runRand();

    }

    private static void runClock(){
        ClockPageTable clock = new ClockPageTable(numFrames);
        String address = null;
        BufferedReader reader = null;
        try{
            File file = new File(traceFile);
            reader = new BufferedReader(new FileReader(file));

            String line;
            while ((line = reader.readLine()) != null) {
                memAccesses++;
                
                address = line.split(" ")[0];
                if (line.split(" ")[1].equals("R")){
                    clock.read(address);
                }
                else
                    clock.write(address);

                
            }
            System.out.printf("Algorithm:             %s\n", alg);
            System.out.printf("Number of frames:      %d\n", numFrames);
            System.out.printf("Total memory accesses: %d\n", memAccesses);
            System.out.printf("Total page faults:     %d\n", clock.getNumPageFaults());
            System.out.printf("Total writes to disk:  %d\n", clock.getNumWritesToDisk());
        }
        catch(Exception e){

        }
        try{
            
            reader.close();
        }
        catch(Exception e){
            System.out.println(e);
        }
    }
    private static void runRand(){
        RandomPageTable rand = new RandomPageTable(numFrames);
        String address = null;
        BufferedReader reader = null;
        try{
            File file = new File(traceFile);
            reader = new BufferedReader(new FileReader(file));

            String line;
            while ((line = reader.readLine()) != null) {
                memAccesses++;
                
                address = line.split(" ")[0];
                if (line.split(" ")[1].equals("R")){
                    rand.read(address);
                }
                else
                    rand.write(address);
                
            }
            System.out.printf("Algorithm:             %s\n", alg);
            System.out.printf("Number of frames:      %d\n", numFrames);
            System.out.printf("Total memory accesses: %d\n", memAccesses);
            System.out.printf("Total page faults:     %d\n", rand.getNumPageFaults());
            System.out.printf("Total writes to disk:  %d\n", rand.getNumWritesToDisk());
        }
        catch(Exception e){

        }
        try{
            reader.close();
        }
        catch(Exception e){
            System.out.println(e);
        }
    }
    private static void runNRU(){
        NruPageTable nru = new NruPageTable(numFrames);
        String address = null;
        BufferedReader reader = null;
        try{
            File file = new File(traceFile);
            reader = new BufferedReader(new FileReader(file));
            String line;
            Instant t0 = Instant.now();
            Instant t1;
            
            while ((line = reader.readLine()) != null) {
                t1 = Instant.now();
                
                if(Duration.between(t0, t1).toNanos()/1000000000 >= refresh){
                    nru.res();
                    t0 = Instant.now();
                }
                
                memAccesses++;
                
                address = line.split(" ")[0];
                if (line.split(" ")[1].equals("R")){
                    nru.read(address);
                }
                else{
                    nru.write(address);
                }

                
            }
            System.out.printf("Algorithm:             %s\n", alg);
            System.out.printf("Number of frames:      %d\n", numFrames);
            System.out.printf("Total memory accesses: %d\n", memAccesses);
            System.out.printf("Total page faults:     %d\n", nru.getNumPageFaults());
            System.out.printf("Total writes to disk:  %d\n", nru.getNumWritesToDisk());
        }
        catch(Exception e){

        }
        try{
            reader.close();
        }
        catch(Exception e){
            System.out.println(e);
        }
    }

    private static void runOpt(){
        OptPageTable opt = new OptPageTable(numFrames, traceFile);
        String address = null;
        BufferedReader reader = null;
        try{
            File file = new File(traceFile);
            reader = new BufferedReader(new FileReader(file));
            String line;
            
            while ((line = reader.readLine()) != null) {
                memAccesses++;
                
            }
            try{
                reader.close();
            }
            catch(Exception e){
                System.out.println(e);
            }
            opt.run(traceFile);
            System.out.printf("Algorithm:             %s\n", alg);
            System.out.printf("Number of frames:      %d\n", numFrames);
            System.out.printf("Total memory accesses: %d\n", memAccesses);
            System.out.printf("Total page faults:     %d\n", opt.getNumPageFaults());
            System.out.printf("Total writes to disk:  %d\n", opt.getNumWritesToDisk());
        }
        catch(Exception e){

        }
        
    }

    private static void parseArgs(String [] args){
        for(int i = 0; i < args.length; i++){
            if(args[i].equals("-n"))
                numFrames = Integer.parseInt(args[i+1]);
            else if(args[i].equals("-a"))
                alg = args[i+1];
            else if(args[i].equals("-r"))
                refresh = Integer.parseInt(args[i+1]);
            else
                traceFile = args[i];
        }       

    }

}