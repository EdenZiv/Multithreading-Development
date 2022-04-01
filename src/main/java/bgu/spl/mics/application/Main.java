package bgu.spl.mics.application;

import bgu.spl.mics.MessageBus;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.passiveObjects.Attack;
import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.passiveObjects.Ewoks;
import bgu.spl.mics.application.passiveObjects.Input;
import bgu.spl.mics.application.services.*;
import com.google.gson.Gson;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
/** This is the Main class of the application. You should parse the input file, 
 * create the different components of the application, and run the system.
 * In the end, you should output a JSON.
 */
public class Main {
	public static void main(String[] args) {
		Gson gson = new Gson();
		Input input;
		try {
			Reader reader = new FileReader(args[0]);
			input = gson.fromJson(reader, Input.class);
			Attack[] attacks = input.getAttacks();
			Integer R2D2Duration = input.getR2D2();
			Integer LandoDuration = input.getLando();
			Integer numOfEwoks = input.getEwoks();

			Ewoks.getInstance().setEwoksCollection(numOfEwoks);
			Thread t1 = new Thread(new LeiaMicroservice(attacks)); //Leia
			Thread t2 = new Thread(new HanSoloMicroservice());//Han
			Thread t3 = new Thread(new C3POMicroservice());//C3P0
			Thread t4 = new Thread(new R2D2Microservice(R2D2Duration)); //R2D2
			Thread t5 = new Thread(new LandoMicroservice(LandoDuration));//LANDO
			MessageBus bus = MessageBusImpl.getInstance();
			Diary diary = Diary.getInstance();
			t1.start();
			t2.start();
			t3.start();
			t4.start();
			t5.start();
			try {
				t1.join();
				t2.join();
				t3.join();
				t4.join();
				t5.join();
			} catch (InterruptedException ignored) {};

			Writer writer = new FileWriter(args[1]);
			Map<String,Object> map= new HashMap<>();

			map.put("totalAttack:"+" ",diary.getTotalAttacks());
			map.put("HanSoloFinish:"+" ",diary.getHanSoloFinish());
			map.put("C3POFinish:"+" ", diary.getC3POFinish());
			map.put("R2D2Deactivate:"+" ", diary.getR2D2Deactivate());
			map.put("LeiaTerminate:"+" ", diary.getLeiaTerminate());
			map.put("HanSoloTerminate:"+" ", diary.getHanSoloTerminate());
			map.put("C3POTerminate:"+" ",diary.getC3POTerminate());
			map.put("R2D2Terminate:"+" ", diary.getR2D2Terminate());
			map.put("LandoTerminate:"+" ",diary.getLandoTerminate());
			//map.put("Diff"+" ",diary.getDiff());

			// convert map to JSON File
			new Gson().toJson(map, writer);

			// close the writer
			writer.close();
		}
		catch (FileNotFoundException e){ System.out.println("Input file not found"); }
		catch (IOException e) { System.out.println("Output file not found");}
	}
}
