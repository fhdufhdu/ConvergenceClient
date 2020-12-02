package com.main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

public class mainGUI extends Application
{
	private static BufferedReader br;
	private static BufferedWriter bw;
	
	public static void main(String args[]) throws Exception
	{
		try
		{
//			String localHostAddress = InetAddress.getLocalHost().getHostAddress();
			String localHostAddress = "192.168.230.238";
			Socket socket = new Socket(localHostAddress, 5000);
			
			br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			launch();
			bw.close();
			br.close();
			socket.close();
		}
		catch (UnknownHostException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public void start(Stage primaryStage)
	{
		try
		{
			Parent root = FXMLLoader.load(mainGUI.class.getResource("../view/xml/login.fxml"));
			// Parent root = FXMLLoader.load(mainGUI.class.getResource("../view/xml/user_sub_page/theater_search.fxml"));
			Scene scene = new Scene(root);
			primaryStage.setTitle("로그인");
			primaryStage.setResizable(false);
			primaryStage.setScene(scene);
			primaryStage.show();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static BufferedReader getBr()
	{
		return br;
	}
	
	public static BufferedWriter getBw()
	{
		return bw;
	}
	
	public static void writePacket(String source) throws Exception
	{
		try
		{
			bw.write(source + "\n");
			bw.flush();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static String readLine()
	{
		try
		{
			return br.readLine();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	public static void alert(String head, String msg)
	{
		Alert alert = new Alert(AlertType.WARNING);
		alert.setTitle("경고");
		alert.setHeaderText(head);
		alert.setContentText(msg);
		
		alert.showAndWait(); // Alert창 보여주기
	}
	
	public static ButtonType confirm(String head, String msg)
	{
		Alert confirm = new Alert(AlertType.CONFIRMATION);
		confirm.setTitle("확인");
		confirm.setHeaderText(head);
		confirm.setContentText(msg);
		return confirm.showAndWait().get();
		
	}
}