
import java.util.*;	
import java.io.*;
import java.sql.Timestamp;

public class Log {
	private PrintWriter writer;

	public Log(String file){
		try{
			Date date= new java.util.Date();
			writer = new PrintWriter(file, "UTF-8");
			writer.print(new Timestamp(date.getTime()) +"| Log file has been initialized");
			writer.flush();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void LogInfo(String info){
		try{
			Date date= new java.util.Date();
			writer.print("\r\n"+ new Timestamp(date.getTime()) +"| " +info);
			writer.flush();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
	}
}