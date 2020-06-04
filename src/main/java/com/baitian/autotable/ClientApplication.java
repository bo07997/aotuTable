package com.baitian.autotable;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author liudianbo
 */
@SpringBootApplication
public class ClientApplication {
	private static final ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
	public static final String SYSTEM_OUT_TXT = "system_out.txt";

	public static void main(String[] args) throws FileNotFoundException {
		cachedThreadPool.execute(new Runnable() {
			@Override
			public void run() {
				try {
					File file = new File(SYSTEM_OUT_TXT);
					file.delete();
					System.setOut(new PrintStream(new FileOutputStream(SYSTEM_OUT_TXT)));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
		});
		SpringApplication.run(ClientApplication.class, args);
	}
}
