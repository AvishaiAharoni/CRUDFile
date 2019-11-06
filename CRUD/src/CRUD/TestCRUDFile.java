package CRUD;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;


class MonitorTest {
	MonitorLog monitor;
	CRUDFile crud;

	/* colors */
	static final String RESET = "\u001b[0m";
	static final String BLACK = "\u001b[30m";
	static final String RED = "\u001b[91m";
	static final String GREEN = "\u001b[32m";
	static final String YELLOW = "\u001b[33m";
	static final String BLUE = "\u001b[94m";
	static final String PURPLE = "\u001b[35m";
	static final String BOLD_PINK = "\u001b[95;4;1m";
	static final String PINK = "\u001b[95m";
	static final String CYAN = "\u001b[96m";
	static final String WHITE = "\u001b[97m";

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		System.out.println(BOLD_PINK + "URI: monitor tests:" + RESET);
		MonitorTest.deleteDir();
	}

	@AfterAll
	static void afterAll() {
		System.out.println(GREEN + "GREAT SUCCESS!" + RESET);
	}

	private static void deleteDir() throws InterruptedException {
		File dir = new File("/home/student/test/full stack/java/eclipse-workspace/CRUD/src/monitor");
		String[] myFiles = dir.list();

		if (dir.exists()) {
			for (int i=0; i<myFiles.length; i++) {
				File myFile = new File(dir, myFiles[i]); 
				myFile.delete();
			}

			dir.delete();
		}

		dir.mkdir();
	}

	void after() {
		monitor.unsubscribe(t -> {
			try {
				return crud.notifyOnMonitor(t);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		});
	}

/*********************************************************************************************/
	@Test
	void createTest() throws IOException, InterruptedException {
		File log = new File("/home/student/test/full stack/java/eclipse-workspace/CRUD/src/monitor/log1.txt");
		log.createNewFile();
		assertTrue(log.exists());

		crud = new CRUDFile("/home/student/test/full stack/java/eclipse-workspace/CRUD/src/monitor/copy_log1.txt");
		assertNotNull(crud);
		monitor = new MonitorLog("/home/student/test/full stack/java/eclipse-workspace/CRUD/src/monitor/log1.txt");
		monitor.subscribe(t -> {
			try {
				return crud.notifyOnMonitor(t);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		});

		FileWriter writer = new FileWriter(log);
		writer.write("The first sentence in this log!\n");
		writer.close();

		writer = new FileWriter(log, true);
		writer.write("And here the second one..\n");
		writer.close();

		writer = new FileWriter(log, true);
		writer.write("And the third\n");
		writer.close();

		// start - need to copy all the lines that currently in the log
		monitor.start();

//		assertEquals(MonitorState.RUNNING, monitor.getMonitorState());
		
		// after sleep - see if the copyLog updated
		Thread.sleep(5000);
		writer = new FileWriter(log, true);
		writer.write("Add after start running\n");
		writer.close();
		
		this.after();
		monitor.stop();
	}

/**
 * @throws Exception *******************************************************************************************/
	@Test
	void deleteTest() throws Exception {
		File log = new File("/home/student/test/full stack/java/eclipse-workspace/CRUD/src/monitor/log2.txt");
		log.createNewFile();
		assertTrue(log.exists());
		
		crud = new CRUDFile("/home/student/test/full stack/java/eclipse-workspace/CRUD/src/monitor/copy_log2.txt");
		assertNotNull(crud);
		monitor = new MonitorLog("/home/student/test/full stack/java/eclipse-workspace/CRUD/src/monitor/log2.txt");
		monitor.subscribe(t -> {
			try {
				return crud.notifyOnMonitor(t);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		});

		monitor.start();
		FileWriter writer = new FileWriter(log);
		for (int i = 0; i < 18; ++i) {
			writer.write("line " + i + "\n");
		}
		writer.close();

		Thread.sleep(1000);
		// delete an exist line
		assertEquals(true, crud.delete(1));
		assertEquals(true, crud.delete(0));
		assertEquals(true, crud.delete(17));
		assertEquals(true, crud.delete(14));

		assertFalse(crud.delete(0)); // try to delete again
		assertFalse(crud.delete(1)); // try to delete again
		assertFalse(crud.delete(31)); //
		
		assertNull(crud.read(1));
		
		
		this.after();
		monitor.stop();
	}

/**
 * @throws Exception *******************************************************************************************/
	@Test
	void readTest() throws Exception {
		File log = new File("/home/student/test/full stack/java/eclipse-workspace/CRUD/src/monitor/log3.txt");
		log.createNewFile();
		assertTrue(log.exists());

		crud = new CRUDFile("/home/student/test/full stack/java/eclipse-workspace/CRUD/src/monitor/copy_log3.txt");
		assertNotNull(crud);
		monitor = new MonitorLog("/home/student/test/full stack/java/eclipse-workspace/CRUD/src/monitor/log3.txt");
		monitor.subscribe(t -> {
			try {
				return crud.notifyOnMonitor(t);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		});

		FileWriter writer = new FileWriter(log);
		writer.write("The first sentence in this log!\n");
		writer.close();

		writer = new FileWriter(log, true);
		writer.write("And here the second one..\n");
		writer.close();

		writer = new FileWriter(log, true);
		writer.write("And the third\n");
		writer.close();

		monitor.start();
		writer = new FileWriter(log, true);
		writer.write("Add after start running\n");
		writer.close();

		Thread.sleep(2000);

		/* read exist */
		assertEquals("And here the second one..", crud.read(1));
		assertEquals("The first sentence in this log!", crud.read(0));
		assertEquals("And the third", crud.read(2));

		/* read second time */
		assertEquals("And the third", crud.read(2));

		/* read line written after monitor start  */
		assertEquals("Add after start running", crud.read(3));

		/* read not exist */
		assertNull(crud.read(13));
		

		this.after();
		monitor.stop();
	}

/**
 * @throws Exception *******************************************************************************************/
	@Test
	void updateTest() throws Exception {
		File log = new File("/home/student/test/full stack/java/eclipse-workspace/CRUD/src/monitor/log4.txt");
		log.createNewFile();
		assertTrue(log.exists());

		crud = new CRUDFile("/home/student/test/full stack/java/eclipse-workspace/CRUD/src/monitor/copy_log4.txt");
		assertNotNull(crud);
		monitor = new MonitorLog("/home/student/test/full stack/java/eclipse-workspace/CRUD/src/monitor/log4.txt");
		monitor.subscribe(t -> {
			try {
				return crud.notifyOnMonitor(t);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		});

		monitor.start();
		FileWriter writer = new FileWriter(log);
		for (int i = 0; i < 20; ++i) {
			writer.write("line " + i + "\n");
		}
		writer.close();
		Thread.sleep(1000); // give the crud enough time

		// update an exist line
		assertEquals(true, crud.update(1, "new line 1"));

		// update not exist
		assertEquals(false, crud.update(29, "non line 29"));

		// update twice
		assertEquals(true, crud.update(17, "new line 17"));
		assertEquals(true, crud.update(17, "new new line 17"));

		// update deleted
		assertEquals(true, crud.delete(6));
		assertEquals(false, crud.update(6, "non line 7"));
		
		
		this.after();
		monitor.stop();
	}

/*********************************************************************************************/
//	@Test
//	void errorFileNotFoundTest() throws IOException, InterruptedException {
//		File log = new File("/home/student/test/full stack/java/eclipse-workspace/CRUD/src/monitor/log5.txt");
//		boolean catched = false;
//		crud = new CRUDFile("/home/student/test/full stack/java/eclipse-workspace/CRUD/src/monitor/copy_log5.txt");
//		assertNotNull(crud);
//
////		try {
//			monitor = new MonitorLog("/home/student/test/full stack/java/eclipse-workspace/CRUD/src/monitor/log5.txt");			
////		}
////		catch(FileNotFoundException e) {
////			catched = true;
////		}
//
////		assertEquals(true, catched);
//		assertEquals(false, log.exists());
//
//		//		assertThrows(new FileNotFoundException(), new Executable() {
//		//			@Override
//		//			public void execute() throws Throwable {
//		//				monitor = new MonitorLog("/home/me/Desktop/monitor/log5.txt");
//		//			}
//		//			
//		//		});
//	}


/*********************************************************************************************/	
	@Test
	void errorDeleteSrcLogTest() throws IOException, InterruptedException {
		File log = new File("/home/student/test/full stack/java/eclipse-workspace/CRUD/src/monitor/log6.txt");
		log.createNewFile();
		assertTrue(log.exists());

		crud = new CRUDFile("/home/student/test/full stack/java/eclipse-workspace/CRUD/src/monitor/copy_log6.txt");
		assertNotNull(crud);
		monitor = new MonitorLog("/home/student/test/full stack/java/eclipse-workspace/CRUD/src/monitor/log6.txt");
		monitor.subscribe(t -> {
			try {
				return crud.notifyOnMonitor(t);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		});

		monitor.start();
		FileWriter writer = new FileWriter(log);
		for (int i = 1; i < 100; ++i) {
			writer.write("line " + i + "\n");
		}
		writer.close();
		
		log.delete();
		assertFalse(log.exists());
		
		Thread.sleep(2000);
		monitor.stop();
	}

/*********************************************************************************************/
	@Test
	void endTest() throws IOException, InterruptedException {
		File log = new File("/home/student/test/full stack/java/eclipse-workspace/CRUD/src/monitor/log7.txt");
		log.createNewFile();
		assertTrue(log.exists());

		crud = new CRUDFile("/home/student/test/full stack/java/eclipse-workspace/CRUD/src/monitor/copy_log7.txt");
		assertNotNull(crud);
		monitor = new MonitorLog("/home/student/test/full stack/java/eclipse-workspace/CRUD/src/monitor/log7.txt");
		monitor.subscribe(t -> {
			try {
				return crud.notifyOnMonitor(t);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		});

		FileWriter writer = new FileWriter(log);
		for (int i = 1; i < 100; ++i) {
			writer.write("line " + i + "\n");
		}
		monitor.start();
		writer.close();
		
		
		monitor.stop();
	}
	
/*********************************************************************************************/
	@Test
	void testStam() throws IOException {
		File log = new File("/home/student/test/full stack/java/eclipse-workspace/CRUD/src/monitor/main.txt");
		File copy = new File("/home/student/test/full stack/java/eclipse-workspace/CRUD/src/monitor/copy.txt");
		log.createNewFile();
		copy.createNewFile();
		assertTrue(copy.exists());
		assertTrue(log.exists());

		FileReader fileRead = new FileReader(log);
		BufferedReader Reader = new BufferedReader(fileRead);
		FileWriter fw = new FileWriter(copy.getAbsoluteFile(), true);

		FileWriter writer = new FileWriter(log);
		writer.write("The first sentence in this log!\n");
		writer.close();

		writer = new FileWriter(log, true);
		writer.write("And here the second one..\n");
		writer.close();

		String line = Reader.readLine();
		fw.write(line);
		fw.close();
		System.out.println(line);

		line = Reader.readLine();
		System.out.println(line);

		line = Reader.readLine();
		System.out.println(line + "end");

		Reader.close();
	}
}